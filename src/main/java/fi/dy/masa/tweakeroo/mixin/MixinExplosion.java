package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.explosion.Explosion;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(Explosion.class)
public abstract class MixinExplosion
{
    @Redirect(method = "affectWorld",
              slice = @Slice(
                            from = @At("HEAD"),
                            to = @At(value = "FIELD",
                                     target = "Lnet/minecraft/world/explosion/Explosion;affectedBlocks:Lit/unimi/dsi/fastutil/objects/ObjectArrayList;")),
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/particle/ParticleTypes;EXPLOSION_EMITTER:Lnet/minecraft/particle/DefaultParticleType;"))
    private DefaultParticleType redirectSpawnParticles()
    {
        if (FeatureToggle.TWEAK_EXPLOSION_REDUCED_PARTICLES.getBooleanValue())
        {
            return ParticleTypes.EXPLOSION;
        }

        return ParticleTypes.EXPLOSION_EMITTER;
    }
}
