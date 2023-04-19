package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import tweakeroo.config.FeatureToggle;

@Mixin(Explosion.class)
public abstract class MixinExplosion
{
    @Redirect(method = "doExplosionB",
              slice = @Slice(
                            from = @At("HEAD"),
                            to = @At(value = "FIELD", ordinal = 1,
                                     target = "Lnet/minecraft/world/Explosion;damagesTerrain:Z")),
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/world/World;spawnParticle(" +
                                "Lnet/minecraft/util/EnumParticleTypes;DDDDDD[I)V"))
    private void redirectSpawnParticles(World world, EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        if (FeatureToggle.TWEAK_EXPLOSION_REDUCED_PARTICLES.getBooleanValue() &&
            particleType == EnumParticleTypes.EXPLOSION_HUGE)
        {
            particleType = EnumParticleTypes.EXPLOSION_LARGE;
        }

        world.spawnParticle(particleType, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    @ModifyVariable(method = "doExplosionB", at = @At("HEAD"), argsOnly = true)
    private boolean shouldSpawnparticles(boolean spawnParticles)
    {
        return spawnParticles && FeatureToggle.TWEAK_EXPLOSION_REDUCED_PARTICLES.getBooleanValue() == false;
    }
}
