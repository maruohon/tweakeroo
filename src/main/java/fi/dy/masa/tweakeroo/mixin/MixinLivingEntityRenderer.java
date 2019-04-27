package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer
{
    @Redirect(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;canSeePlayer(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
    private boolean redirectedIsInvisibleToPlayer(LivingEntity livingBase, PlayerEntity player)
    {
        if (FeatureToggle.TWEAK_RENDER_INVISIBLE_ENTITIES.getBooleanValue())
        {
            return false;
        }
        else
        {
            return livingBase.canSeePlayer(player);
        }
    }
}
