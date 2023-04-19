package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.entity.AbstractClientPlayer;

import tweakeroo.config.FeatureToggle;
import tweakeroo.util.CameraUtils;

@Mixin(AbstractClientPlayer.class)
public class MixinAbstractClientPlayer
{
    @Inject(method = "isSpectator", at = @At("HEAD"), cancellable = true)
    private void overrideIsSpectator(CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && CameraUtils.getFreeCameraSpectator())
        {
            cir.setReturnValue(true);
        }
    }
}
