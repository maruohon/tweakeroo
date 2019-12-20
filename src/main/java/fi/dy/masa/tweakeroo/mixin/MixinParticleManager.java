package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.BlockPos;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(ParticleManager.class)
public abstract class MixinParticleManager
{
    @Inject(method = "addBlockBreakParticles", at = @At("HEAD"), cancellable = true)
    private void onAddBlockDestroyEffects(BlockPos pos, BlockState state, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_BLOCK_BREAK_PARTICLES.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "addParticle(Lnet/minecraft/client/particle/Particle;)V", at = @At("HEAD"), cancellable = true)
    private void disableAllParticles(Particle effect, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_PARTICLES.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
