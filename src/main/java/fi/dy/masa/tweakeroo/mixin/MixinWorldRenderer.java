package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockPos;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Inject(method = "notifyLightSet", at = @At("HEAD"), cancellable = true)
    public void notifyLightSet(BlockPos pos, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_LIGHT_UPDATES.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Redirect(method = "setupTerrain", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/renderer/ViewFrustum;updateChunkPositions(DD)V"))
    private void preventRenderChunkPositionUpdates(ViewFrustum frustum, double viewEntityX, double viewEntityZ)
    {
        // Don't update the RenderChunk positions when moving around in the Free Camera mode.
        // Otherwise the chunks would become empty when they are outside the render range
        // from the camera entity, ie. on the other side of the actual player.
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() == false)
        {
            frustum.updateChunkPositions(viewEntityX, viewEntityZ);
        }
    }
}