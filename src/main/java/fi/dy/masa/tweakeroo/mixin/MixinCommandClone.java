package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.command.impl.CloneCommand;

@Mixin(value = CloneCommand.class, priority = 999)
public abstract class MixinCommandClone
{
    @ModifyConstant(method = "doClone", constant = @Constant(intValue = 32768), require = 0)
    private static int getBlockCountLimit(int original)
    {
        if (FeatureToggle.TWEAK_FILL_CLONE_LIMIT.getBooleanValue())
        {
            return Configs.Generic.FILL_CLONE_LIMIT.getIntegerValue();
        }

        return original;
    }
}
