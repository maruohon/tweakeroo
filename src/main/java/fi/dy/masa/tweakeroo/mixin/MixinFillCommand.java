package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.command.FillCommand;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.IntRule;
import net.minecraft.world.GameRules.Key;

import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(value = FillCommand.class, priority = 1001)
public abstract class MixinFillCommand
{
    @Redirect(method = "execute", require = 0,
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/world/GameRules;getInt(Lnet/minecraft/world/GameRules$Key;)I"))
    private static int tweakeroo_overrideBlockLimit(GameRules instance, Key<IntRule> rule)
    {
        if (FeatureToggle.TWEAK_FILL_CLONE_LIMIT.getBooleanValue())
        {
            return Configs.Generic.FILL_CLONE_LIMIT.getIntegerValue();
        }

        return instance.getInt(rule);
    }
}
