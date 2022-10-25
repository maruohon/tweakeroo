package tweakeroo.event;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;

import malilib.config.value.ActiveMode;
import malilib.event.PostGameOverlayRenderer;
import malilib.event.PostItemTooltipRenderer;
import malilib.event.PostWorldRenderer;
import malilib.gui.BaseScreen;
import malilib.render.inventory.InventoryRenderUtils;
import malilib.util.data.Color4f;
import malilib.util.game.wrap.GameUtils;
import malilib.util.game.wrap.ItemWrap;
import tweakeroo.config.Configs;
import tweakeroo.config.FeatureToggle;
import tweakeroo.config.Hotkeys;
import tweakeroo.renderer.RenderUtils;

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
                RenderUtils.renderPitchLockIndicator();
            }
        }
    }

    @Override
    public void onPostRenderItemTooltip(ItemStack stack, int x, int y)
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
                    malilib.render.RenderUtils.renderMapPreview(stack, x, y, z, dimensions);
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
    public void onPostWorldRender(float tickDelta)
    {
        this.renderOverlays(tickDelta);
    }

    private void renderOverlays(float tickDelta)
    {
        EntityPlayer player = GameUtils.getClientPlayer();
        RayTraceResult hitResult = GameUtils.getHitResult();

        if (FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue() &&
            hitResult != null &&
            hitResult.typeOfHit == RayTraceResult.Type.BLOCK &&
            player.isSpectator() == false &&
            (Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ROTATION.getKeyBind().isKeyBindHeld() ||
             Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_OFFSET.getKeyBind().isKeyBindHeld() ||
             Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ADJACENT.getKeyBind().isKeyBindHeld()) &&
            (ItemWrap.notEmpty(player.getHeldItem(EnumHand.MAIN_HAND)) ||
             ItemWrap.notEmpty(player.getHeldItem(EnumHand.OFF_HAND))))
        {
            Entity entity = GameUtils.getCameraEntity();
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();

            malilib.render.RenderUtils.setupBlend();

            Color4f color = Configs.Generic.FLEXIBLE_PLACEMENT_OVERLAY_COLOR.getColor();

            malilib.render.RenderUtils.renderBlockTargetingOverlay(
                    entity,
                    hitResult.getBlockPos(),
                    hitResult.sideHit,
                    hitResult.hitVec,
                    color, tickDelta);

            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
        }
    }
}
