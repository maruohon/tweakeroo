package fi.dy.masa.tweakeroo.event;

import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;

public class RenderHandler implements IRenderer
{
    @Override
    public void onRenderGameOverlayPost(float partialTicks)
    {
        Minecraft mc = Minecraft.getMinecraft();

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
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.player != null)
        {
            this.renderOverlays(mc, partialTicks);
        }
    }

    private void renderOverlays(Minecraft mc, float partialTicks)
    {
        if (FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue() &&
            mc.objectMouseOver != null &&
            mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK &&
            (Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ROTATION.getKeybind().isKeybindHeld() ||
             Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_OFFSET.getKeybind().isKeybindHeld()))
        {
            Entity entity = mc.player;
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            //GlStateManager.pushMatrix();
            GlStateManager.disableTexture2D();
            double dx = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
            double dy = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
            double dz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

            RenderUtils.renderBlockPlacementOverlay(
                    entity,
                    mc.objectMouseOver.getBlockPos(),
                    mc.objectMouseOver.sideHit,
                    mc.objectMouseOver.hitVec,
                    dx, dy, dz);

            GlStateManager.enableTexture2D();
            //GlStateManager.popMatrix();
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
        }
    }
}
