package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;

@Mixin(FogRenderer.class)
public abstract class MixinFogRenderer
{
    @Inject(method = "setupFog(IF)V",
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/tags/FluidTags;LAVA")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;fogDensity(F)V",
                     ordinal = 0, shift = Shift.AFTER))
    private void onSetupLavaFog(int startCoords, float partialTicks, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_LAVA_VISIBILITY.getBooleanValue() &&
            Configs.Generic.LAVA_VISIBILITY_OPTIFINE.getBooleanValue() == false)
        {
            RenderUtils.overrideLavaFog(Minecraft.getInstance().getRenderViewEntity());
        }
    }
}
