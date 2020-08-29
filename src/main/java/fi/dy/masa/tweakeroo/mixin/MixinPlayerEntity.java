package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity
{
    @Shadow protected abstract boolean clipAtLedge();

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType_1, World world_1)
    {
        super(entityType_1, world_1);
    }

    @Inject(method = "method_30263", at = @At("HEAD"), cancellable = true)
    private void restore_1_15_2_sneaking(CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_SNEAK_1_15_2.getBooleanValue())
        {
            cir.setReturnValue(this.onGround);
        }
    }

    @Redirect(method = "adjustMovementForSneaking", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/entity/player/PlayerEntity;clipAtLedge()Z", ordinal = 0))
    private boolean fakeSneaking(PlayerEntity entity)
    {
        if (FeatureToggle.TWEAK_FAKE_SNEAKING.getBooleanValue() && ((Object) this) instanceof ClientPlayerEntity)
        {
            return true;
        }

        return this.clipAtLedge();
    }
}
