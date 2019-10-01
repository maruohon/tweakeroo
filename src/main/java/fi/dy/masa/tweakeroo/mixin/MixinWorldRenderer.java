package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.ChunkRenderDispatcher;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Inject(method = "method_22713", at = @At("HEAD")) // renderRain
    private void cancelRainRender(Camera camera, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_RAIN_EFFECTS.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "method_22714", at = @At("HEAD")) // renderWeather
    private void cancelRainRender(LightmapTextureManager lightmap, Camera camera, float partialTicks, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_RAIN_EFFECTS.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "method_22710", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;setUpTerrain(" +
                     "Lnet/minecraft/client/render/Camera;" +
                     "Lnet/minecraft/client/render/VisibleRegion;IZ)V"))
    private void preSetupTerrain(float partialTicks, long finishTimeNano, boolean boolean_1, Camera camera, GameRenderer renderer, LightmapTextureManager lightmap, CallbackInfo ci)
    {
        MiscUtils.setFreeCameraSpectator(true);
    }

    @Inject(method = "method_22710", at = @At(
            value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/render/WorldRenderer;setUpTerrain(" +
                     "Lnet/minecraft/client/render/Camera;" +
                     "Lnet/minecraft/client/render/VisibleRegion;IZ)V"))
    private void postSetupTerrain(float partialTicks, long finishTimeNano, boolean boolean_1, Camera camera, GameRenderer renderer, LightmapTextureManager lightmap, CallbackInfo ci)
    {
        MiscUtils.setFreeCameraSpectator(false);
    }

    @Redirect(method = "setUpTerrain", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/render/ChunkRenderDispatcher;updateCameraPosition(DD)V"))
    private void preventRenderChunkPositionUpdates(ChunkRenderDispatcher dispatcher, double viewEntityX, double viewEntityZ)
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