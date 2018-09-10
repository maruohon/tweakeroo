package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Callbacks;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer
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
}
