package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandClone;
import net.minecraft.command.CommandFill;

@Mixin(value = {CommandFill.class, CommandClone.class}, priority = 999)
public abstract class MixinCommandFillClone extends CommandBase
{
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 32768), require = 0)
    private int getBlockCountLimit(int original)
    {
        if (FeatureToggle.TWEAK_FILL_CLONE_LIMIT.getBooleanValue())
        {
            return Configs.Generic.FILL_CLONE_LIMIT.getIntegerValue();
        }

        return original;
    }
}
