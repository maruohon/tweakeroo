package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.FirstPersonRenderer;

@Mixin(FirstPersonRenderer.class)
public abstract class MixinFirstPersonRenderer
{
    @Redirect(method = "updateHeldItems()V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"))
    public float redirectedGetCooledAttackStrength(ClientPlayerEntity player, float adjustTicks)
    {
        return Configs.Disable.DISABLE_ITEM_SWITCH_COOLDOWN.getBooleanValue() ? 1.0F : player.getAttackCooldownProgress(adjustTicks);
    }

    @ModifyVariable(method = "renderFirstPersonItem(F)V", ordinal = 1,
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isUsingItem()Z"))
    private boolean preventOffhandRendering(boolean original)
    {
        if (Configs.Disable.DISABLE_OFFHAND_RENDERING.getBooleanValue())
        {
            return false;
        }

        return original;
    }
}
