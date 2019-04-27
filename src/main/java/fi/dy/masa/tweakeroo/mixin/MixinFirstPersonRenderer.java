package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.FirstPersonRenderer;

@Mixin(FirstPersonRenderer.class)
public abstract class MixinFirstPersonRenderer
{
    @Redirect(method = "updateHeldItems()V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;method_7261(F)F"))
    public float redirectedGetCooledAttackStrength(ClientPlayerEntity player, float adjustTicks)
    {
        return FeatureToggle.TWEAK_NO_ITEM_SWITCH_COOLDOWN.getBooleanValue() ? 1.0F : player.method_7261(adjustTicks);
    }
}
