package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.WorldRenderer;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Redirect(method = "setUpTerrain", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/render/BuiltChunkStorage;updateCameraPosition(DD)V"))
    private void preventRenderChunkPositionUpdates(BuiltChunkStorage dispatcher, double viewEntityX, double viewEntityZ)
    {
        // Don't update the RenderChunk positions when moving around in the Free Camera mode.
        // Otherwise the chunks would become empty when they are outside the render range
        // from the camera entity, ie. on the other side of the actual player.
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() == false)
        {
            dispatcher.updateCameraPosition(viewEntityX, viewEntityZ);
        }
    }
}