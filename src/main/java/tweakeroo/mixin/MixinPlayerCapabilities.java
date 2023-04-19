package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;

import malilib.util.game.wrap.GameUtils;
import tweakeroo.config.Configs;
import tweakeroo.config.FeatureToggle;

@Mixin(PlayerCapabilities.class)
public abstract class MixinPlayerCapabilities
{
    @Inject(method = "getFlySpeed", at = @At("HEAD"), cancellable = true)
    private void overrideFlySpeed(CallbackInfoReturnable<Float> cir)
    {
        EntityPlayer player = GameUtils.getClientPlayer();

        if (FeatureToggle.TWEAK_FLY_SPEED.getBooleanValue() &&
            player != null && player.capabilities.allowFlying)
        {
            cir.setReturnValue(Configs.Internal.ACTIVE_FLY_SPEED_OVERRIDE_VALUE.getFloatValue());
        }
    }
}
