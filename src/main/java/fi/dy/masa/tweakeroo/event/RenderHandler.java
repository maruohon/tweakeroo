package fi.dy.masa.tweakeroo.event;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import fi.dy.masa.malilib.config.value.ActiveMode;
import fi.dy.masa.malilib.event.PostGameOverlayRenderer;
import fi.dy.masa.malilib.event.PostItemTooltipRenderer;
import fi.dy.masa.malilib.event.PostWorldRenderer;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.render.inventory.InventoryRenderUtils;
import fi.dy.masa.malilib.util.GameUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;

public class RenderHandler implements PostGameOverlayRenderer, PostItemTooltipRenderer, PostWorldRenderer
{
    private final Supplier<String> profilerSectionSupplier = () -> "Tweakeroo_RenderHandler";

    @Override
    public Supplier<String> getProfilerSectionSupplier()
    {
        return this.profilerSectionSupplier;
    }

    @Override
    public void onPostGameOverlayRender()
    {
        Minecraft mc = GameUtils.getClient();

        if (FeatureToggle.TWEAK_HOTBAR_SWAP.getBooleanValue() &&
            Hotkeys.HOTBAR_SWAP_BASE.getKeyBind().isKeyBindHeld())
        {
            RenderUtils.renderHotbarSwapOverlay(mc);
        }
        else if (FeatureToggle.TWEAK_HOTBAR_SCROLL.getBooleanValue() &&
                 Hotkeys.HOTBAR_SCROLL.getKeyBind().isKeyBindHeld())
        {
            RenderUtils.renderHotbarScrollOverlay(mc);
        }

        if (FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() &&
            Hotkeys.INVENTORY_PREVIEW.getKeyBind().isKeyBindHeld())
        {
            RenderUtils.renderPointedInventoryOverlay(mc);
        }

        if (FeatureToggle.TWEAK_PLAYER_INVENTORY_PEEK.getBooleanValue() &&
            Hotkeys.PLAYER_INVENTORY_PEEK.getKeyBind().isKeyBindHeld())
        {
            RenderUtils.renderPlayerInventoryPeekOverlay(mc);
        }

        if (FeatureToggle.TWEAK_SNAP_AIM.getBooleanValue() &&
            Configs.Generic.SNAP_AIM_INDICATOR.getBooleanValue())
        {
            RenderUtils.renderSnapAimAngleIndicator();
        }

        if (FeatureToggle.TWEAK_ELYTRA_CAMERA.getBooleanValue())
        {
            ActiveMode mode = Configs.Generic.ELYTRA_CAMERA_INDICATOR.getValue();

            if (mode == ActiveMode.ALWAYS || (mode == ActiveMode.WITH_KEY && Hotkeys.ELYTRA_CAMERA.getKeyBind().isKeyBindHeld()))
            {
                RenderUtils.renderPitchLockIndicator(mc);
            }
        }
    }

    @Override
    public void onPostRenderItemTooltip(ItemStack stack, int x, int y, Minecraft mc)
    {
        float z = Configs.Generic.ITEM_PREVIEW_Z.getIntegerValue();

        if (stack.getItem() instanceof ItemMap)
        {
            if (FeatureToggle.TWEAK_MAP_PREVIEW.getBooleanValue())
            {
                boolean render = Configs.Generic.MAP_PREVIEW_REQUIRE_SHIFT.getBooleanValue() == false || BaseScreen.isShiftDown();

                if (render)
                {
                    int dimensions = Configs.Generic.MAP_PREVIEW_SIZE.getIntegerValue();
                    fi.dy.masa.malilib.render.RenderUtils.renderMapPreview(stack, x, y, z, dimensions);
                }
            }
        }
        else if (FeatureToggle.TWEAK_SHULKERBOX_DISPLAY.getBooleanValue())
        {
            boolean render = Configs.Generic.SHULKER_DISPLAY_REQUIRE_SHIFT.getBooleanValue() == false || BaseScreen.isShiftDown();

            if (render)
            {
                boolean background = Configs.Generic.SHULKER_DISPLAY_BACKGROUND_COLOR.getBooleanValue();
                x += 8;
                y -= 10;
                InventoryRenderUtils.renderItemInventoryPreview(stack, x, y, z, background);
            }
        }
    }

    @Override
    public void onPostWorldRender(Minecraft mc, float partialTicks)
    {
        this.renderOverlays(mc, partialTicks);
    }

    private void renderOverlays(Minecraft mc, float partialTicks)
    {
        if (FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue() &&
            mc.objectMouseOver != null &&
            mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK &&
            mc.player.isSpectator() == false &&
            (Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ROTATION.getKeyBind().isKeyBindHeld() ||
             Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_OFFSET.getKeyBind().isKeyBindHeld() ||
             Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ADJACENT.getKeyBind().isKeyBindHeld()) &&
            (mc.player.getHeldItem(EnumHand.MAIN_HAND).isEmpty() == false ||
             mc.player.getHeldItem(EnumHand.OFF_HAND).isEmpty() == false))
        {
            Entity entity = mc.getRenderViewEntity() != null ? mc.getRenderViewEntity() : mc.player;
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();

            fi.dy.masa.malilib.render.RenderUtils.setupBlend();

            Color4f color = Configs.Generic.FLEXIBLE_PLACEMENT_OVERLAY_COLOR.getColor();

            fi.dy.masa.malilib.render.RenderUtils.renderBlockTargetingOverlay(
                    entity,
                    mc.objectMouseOver.getBlockPos(),
                    mc.objectMouseOver.sideHit,
                    mc.objectMouseOver.hitVec,
                    color, partialTicks);

            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
        }
    }
}
