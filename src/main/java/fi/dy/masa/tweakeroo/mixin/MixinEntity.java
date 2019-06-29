package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(Entity.class)
public abstract class MixinEntity
{
    // This method should be called isInvisibleToPlayer
    @Inject(method = "canSeePlayer", at = @At("HEAD"), cancellable = true)
    private void overrideIsInvisibleToPlayer(PlayerEntity player, CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_RENDER_INVISIBLE_ENTITIES.getBooleanValue())
        {
            cir.setReturnValue(false);
        }
    }
}
