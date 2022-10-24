package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLivingBase;

import malilib.util.game.wrap.GameUtils;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.DisableToggle;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(targets = "net/minecraft/client/renderer/EntityRenderer$1")
public abstract class MixinEntityRenderer_Predicate
{
    @Inject(method = "apply(Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void ignoreDeadEntities(Entity entity, CallbackInfoReturnable<Boolean> cir)
    {
        if ((DisableToggle.DISABLE_DEAD_MOB_TARGETING.getBooleanValue()
                && entity instanceof EntityLivingBase
                && ((EntityLivingBase) entity).getHealth() <= 0f)
            ||
            (FeatureToggle.TWEAK_HANGABLE_ENTITY_BYPASS.getBooleanValue()
                && (entity instanceof EntityHanging)
                && GameUtils.getClientPlayer().isSneaking() == Configs.Generic.HANGABLE_ENTITY_BYPASS_INVERSE.getBooleanValue()))
        {
            cir.setReturnValue(false);
        }
    }
}
