package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.init.Particles;
import net.minecraft.particles.IParticleData;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

@Mixin(Explosion.class)
public abstract class MixinExplosion
{
    @Redirect(method = "doExplosionB",
              slice = @Slice(
                            from = @At("HEAD"),
                            to = @At(value = "FIELD", ordinal = 1,
                                     target = "Lnet/minecraft/world/Explosion;damagesTerrain:Z")),
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/world/World;addParticle(" +
                                "Lnet/minecraft/particles/IParticleData;DDDDDD)V"))
    private void redirectSpawnParticles(World world, IParticleData particle, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed)
    {
        if (FeatureToggle.TWEAK_EXPLOSION_REDUCED_PARTICLES.getBooleanValue() &&
            particle == Particles.EXPLOSION_EMITTER)
        {
            particle = Particles.EXPLOSION;
        }

        world.addParticle(particle, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
    }

    @ModifyVariable(method = "doExplosionB", at = @At("HEAD"), argsOnly = true)
    private boolean shouldSpawnparticles(boolean spawnParticles)
    {
        return spawnParticles && FeatureToggle.TWEAK_EXPLOSION_REDUCED_PARTICLES.getBooleanValue() == false;
    }
}
