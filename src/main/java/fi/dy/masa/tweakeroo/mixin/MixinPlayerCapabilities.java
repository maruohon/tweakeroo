package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;

@Mixin(PlayerCapabilities.class)
public abstract class MixinPlayerCapabilities
{
    @Inject(method = "getFlySpeed", at = @At("HEAD"), cancellable = true)
    private void overrideFlySpeed(CallbackInfoReturnable<Float> cir)
    {
        EntityPlayer player = Minecraft.getInstance().player;

        if (FeatureToggle.TWEAK_FLY_SPEED.getBooleanValue() &&
            player != null && player.abilities.allowFlying)
        {
            cir.setReturnValue((float) Configs.getActiveFlySpeedConfig().getDoubleValue());
            cir.cancel();
        }
    }
}
