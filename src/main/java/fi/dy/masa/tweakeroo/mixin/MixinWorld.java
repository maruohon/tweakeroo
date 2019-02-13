package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

@Mixin(World.class)
public abstract class MixinWorld
{
    @Inject(method = "updateEntityWithOptionalForce", at = @At("HEAD"), cancellable = true)
    private void preventEntityTicking(Entity entityIn, boolean forceUpdate, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_NO_ENTITY_TICKING.getBooleanValue() && (entityIn instanceof EntityPlayer) == false)
        {
            ci.cancel();
        }
    }
}
