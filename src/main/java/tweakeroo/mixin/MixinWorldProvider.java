package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.WorldProvider;

import tweakeroo.config.Configs;
import tweakeroo.config.FeatureToggle;

@Mixin(WorldProvider.class)
public abstract class MixinWorldProvider
{
    @Inject(method = "getCloudHeight", at = @At("HEAD"), cancellable = true)
    private void overrideCloudHeight(CallbackInfoReturnable<Float> cir)
    {
        if (FeatureToggle.TWEAK_CLOUD_HEIGHT_OVERRIDE.getBooleanValue())
        {
            cir.setReturnValue(Configs.Generic.CLOUD_HEIGHT_OVERRIDE.getFloatValue());
        }
    }
}
