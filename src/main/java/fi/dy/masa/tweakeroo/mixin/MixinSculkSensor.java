package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.minecraft.block.SculkSensorBlock;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(SculkSensorBlock.class)
public abstract class MixinSculkSensor
{
    @ModifyConstant(method = "setActive", constant = @Constant(intValue = 40), require = 0)
    private static int modifyPulseLength(int original)
    {
        if (FeatureToggle.TWEAK_SCULK_PULSE_LENGTH.getBooleanValue())
        {
            return Configs.Generic.SCULK_SENSOR_PULSE_LENGTH.getIntegerValue();
        }

        return original;
    }
}
