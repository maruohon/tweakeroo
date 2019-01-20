package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.config.Callbacks;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer
{
    @Inject(method = "renderWorld(FJ)V", at = @At("HEAD"), cancellable = true)
    private void onRenderWorld(CallbackInfo ci)
    {
        if (Callbacks.skipWorldRendering)
        {
            ci.cancel();
        }
    }

    @Inject(method = "setupFog(IF)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GlStateManager;setFogDensity(F)V",
            shift = Shift.AFTER,
            ordinal = 4))
    private void onSetupLavaFog(int startCoords, float partialTicks, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_LAVA_VISIBILITY.getBooleanValue() && Configs.Generic.LAVA_VISIBILITY_OPTIFINE.getBooleanValue() == false)
        {
            RenderUtils.overrideLavaFog(Minecraft.getMinecraft().getRenderViewEntity());
        }
    }

    @Inject(method = "getFOVModifier", at = @At("HEAD"), cancellable = true)
    private void zoom(float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Float> cir)
    {
        if (FeatureToggle.TWEAK_ZOOM.getBooleanValue() && Hotkeys.ZOOM_ACTIVATE.getKeybind().isKeybindHeld())
        {
            cir.setReturnValue((float) Configs.Generic.ZOOM_FOV.getDoubleValue());
            cir.cancel();
        }
    }

    @Inject(method = "renderRainSnow", at = @At("HEAD"), cancellable = true)
    private void cancelRainRender(float partialTicks, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_NO_RAIN_EFFECTS.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "addRainParticles", at = @At("HEAD"), cancellable = true)
    private void cancelRainRender(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_NO_RAIN_EFFECTS.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
