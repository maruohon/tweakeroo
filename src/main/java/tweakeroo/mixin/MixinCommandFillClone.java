package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandClone;
import net.minecraft.command.CommandFill;

import tweakeroo.config.Configs;
import tweakeroo.config.FeatureToggle;

@Mixin({CommandFill.class, CommandClone.class})
public abstract class MixinCommandFillClone extends CommandBase
{
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 32768))
    private int getBlockCountLimit(int original)
    {
        if (FeatureToggle.TWEAK_FILL_CLONE_LIMIT.getBooleanValue())
        {
            return Configs.Generic.FILL_CLONE_LIMIT.getIntegerValue();
        }

        return original;
    }
}
