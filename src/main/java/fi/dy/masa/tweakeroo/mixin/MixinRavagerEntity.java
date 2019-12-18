package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.entity.mob.RavagerEntity;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(RavagerEntity.class)
public abstract class MixinRavagerEntity
{
    @Redirect(method = "tickMovement", at = @At(
                value = "FIELD",
                target = "Lnet/minecraft/entity/mob/RavagerEntity;horizontalCollision:Z"))
    private boolean fixDontBreakBlocksOnClient(RavagerEntity entity)
    {
        if (Configs.Fixes.RAVAGER_CLIENT_BLOCK_BREAK_FIX.getBooleanValue())
        {
            return entity.horizontalCollision && entity.getEntityWorld().isClient == false;
        }

        return entity.horizontalCollision;
    }
}
