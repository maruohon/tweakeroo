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
import net.minecraft.util.math.Vec3d;

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
        Entity entity = mc.getCameraEntity();

        if (FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue() &&
            entity != null &&
            mc.hitResult != null &&
            mc.hitResult.getType() == HitResult.Type.BLOCK &&
            (Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ROTATION.getKeybind().isKeybindHeld() ||
             Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_OFFSET.getKeybind().isKeybindHeld()))
        {
            BlockHitResult hitResult = (BlockHitResult) mc.hitResult;
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            //GlStateManager.pushMatrix();
            GlStateManager.disableTexture();
            Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();

            RenderUtils.renderBlockPlacementOverlay(
                    entity,
                    hitResult.getBlockPos(),
                    hitResult.getSide(),
                    hitResult.getPos(),
                    cameraPos.x, cameraPos.y, cameraPos.z);

            GlStateManager.enableTexture();
            //GlStateManager.popMatrix();
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
        }
    }
}
