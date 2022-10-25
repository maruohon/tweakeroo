package tweakeroo.mixin;

import java.util.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import tweakeroo.config.DisableToggle;
import tweakeroo.config.FeatureToggle;
import tweakeroo.util.MiscUtils;

@Mixin(net.minecraft.client.particle.ParticleManager.class)
public abstract class MixinParticleManager
{
    @Shadow protected net.minecraft.world.World world;
    @Shadow @Final private Random rand;

    @Shadow public void addEffect(net.minecraft.client.particle.Particle effect) {}

    @Inject(method = "addBlockDestroyEffects", at = @At("HEAD"), cancellable = true)
    private void onAddBlockDestroyEffects(
            net.minecraft.util.math.BlockPos pos,
            net.minecraft.block.state.IBlockState state,
            CallbackInfo ci)
    {
        if (DisableToggle.DISABLE_BLOCK_BREAK_PARTICLES.getBooleanValue())
        {
            ci.cancel();
            return;
        }

        if (FeatureToggle.TWEAK_BLOCK_BREAKING_PARTICLES.getBooleanValue())
        {
            MiscUtils.addCustomBlockBreakingParticles((net.minecraft.client.particle.ParticleManager) (Object) this, this.world, this.rand, pos, state);
            ci.cancel();
        }
    }

    @Inject(method = "addEffect", at = @At("HEAD"), cancellable = true)
    private void disableAllParticles(
            net.minecraft.client.particle.Particle effect,
            CallbackInfo ci)
    {
        if (DisableToggle.DISABLE_PARTICLES.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
