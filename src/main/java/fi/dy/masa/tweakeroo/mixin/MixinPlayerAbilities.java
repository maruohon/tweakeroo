package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerAbilities.class)
public abstract class MixinPlayerAbilities
{
    @Inject(method = "getFlySpeed", at = @At("HEAD"), cancellable = true)
    private void overrideFlySpeed(CallbackInfoReturnable<Float> cir)
    {
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (FeatureToggle.TWEAK_FLY_SPEED.getBooleanValue() &&
            player != null && player.abilities.allowFlying)
        {
            cir.setReturnValue((float) Configs.getActiveFlySpeedConfig().getDoubleValue());
            cir.cancel();
        }
    }
}
