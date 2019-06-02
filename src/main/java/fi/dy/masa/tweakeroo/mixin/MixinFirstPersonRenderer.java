package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.FirstPersonRenderer;

@Mixin(FirstPersonRenderer.class)
public abstract class MixinFirstPersonRenderer
{
    @Redirect(method = "tick()V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;getCooledAttackStrength(F)F"))
    public float redirectedGetCooledAttackStrength(EntityPlayerSP player, float adjustTicks)
    {
        return FeatureToggle.TWEAK_NO_ITEM_SWITCH_COOLDOWN.getBooleanValue() ? 1.0F : player.getCooledAttackStrength(adjustTicks);
    }

    @ModifyVariable(method = "renderItemInFirstPerson(F)V", ordinal = 1,
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/entity/AbstractClientPlayer;isHandActive()Z"))
    private boolean preventOffhandRendering(boolean original)
    {
        if (FeatureToggle.TWEAK_NO_OFFHAND_RENDERING.getBooleanValue())
        {
            return false;
        }

        return original;
    }
}
