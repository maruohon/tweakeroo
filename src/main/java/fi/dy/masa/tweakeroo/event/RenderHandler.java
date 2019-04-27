package fi.dy.masa.tweakeroo.event;

import com.mojang.blaze3d.platform.GlStateManager;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class RenderHandler implements IRenderer
{
    @Override
    public void onRenderGameOverlayPost(float partialTicks)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (FeatureToggle.TWEAK_HOTBAR_SWAP.getBooleanValue() &&
            Hotkeys.HOTBAR_SWAP_BASE.getKeybind().isKeybindHeld())
        {
            RenderUtils.renderHotbarSwapOverlay(mc);
        }
        else if (FeatureToggle.TWEAK_HOTBAR_SCROLL.getBooleanValue() &&
                 Hotkeys.HOTBAR_SCROLL.getKeybind().isKeybindHeld())
        {
            RenderUtils.renderHotbarScrollOverlay(mc);
        }

        if (FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() &&
            Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld())
        {
            RenderUtils.renderInventoryOverlay(mc);
        }

        if (FeatureToggle.TWEAK_PLAYER_INVENTORY_PEEK.getBooleanValue() &&
            Hotkeys.PLAYER_INVENTORY_PEEK.getKeybind().isKeybindHeld())
        {
            RenderUtils.renderPlayerInventoryOverlay(mc);
        }
    }

    @Override
    public void onRenderWorldLast(float partialTicks)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player != null)
        {
            this.renderOverlays(mc, partialTicks);
        }
    }

    private void renderOverlays(MinecraftClient mc, float partialTicks)
    {
        if (FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue() &&
            mc.hitResult != null &&
            mc.hitResult.getType() == HitResult.Type.BLOCK &&
            (Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ROTATION.getKeybind().isKeybindHeld() ||
             Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_OFFSET.getKeybind().isKeybindHeld()))
        {
            Entity entity = mc.player;
            BlockHitResult hitResult = (BlockHitResult) mc.hitResult;
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            //GlStateManager.pushMatrix();
            GlStateManager.disableTexture();
            double dx = entity.prevRenderX + (entity.x - entity.prevRenderX) * partialTicks;
            double dy = entity.prevRenderY + (entity.y - entity.prevRenderY) * partialTicks;
            double dz = entity.prevRenderZ + (entity.z - entity.prevRenderZ) * partialTicks;

            RenderUtils.renderBlockPlacementOverlay(
                    entity,
                    hitResult.getBlockPos(),
                    hitResult.getSide(),
                    hitResult.getPos(),
                    dx, dy, dz);

            GlStateManager.enableTexture();
            //GlStateManager.popMatrix();
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
        }
    }
}
