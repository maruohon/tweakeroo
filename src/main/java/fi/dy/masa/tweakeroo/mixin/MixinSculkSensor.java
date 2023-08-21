package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.block.SculkSensorBlock;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(SculkSensorBlock.class)
public abstract class MixinSculkSensor
{
    @Inject(method = "getCooldownTime", at = @At(value = "HEAD"), require = 0, cancellable = true)
    private void modifyPulseLength(CallbackInfoReturnable<Integer> cir)
    {
        if (FeatureToggle.TWEAK_SCULK_PULSE_LENGTH.getBooleanValue())
        {
            cir.setReturnValue(Configs.Generic.SCULK_SENSOR_PULSE_LENGTH.getIntegerValue());
        }
    }
}
