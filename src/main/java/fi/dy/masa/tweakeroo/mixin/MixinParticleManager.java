package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.BlockPos;

@Mixin(ParticleManager.class)
public abstract class MixinParticleManager
{
    @Inject(method = "addBlockDestroyEffects", at = @At("HEAD"), cancellable = true)
    private void onAddBlockDestroyEffects(BlockPos pos, IBlockState state, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_BLOCK_BREAK_PARTICLES.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "addEffect", at = @At("HEAD"), cancellable = true)
    private void disableAllParticles(Particle effect, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_PARTICLES.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
