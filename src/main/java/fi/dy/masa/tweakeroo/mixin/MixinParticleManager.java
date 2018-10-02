package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.BlockPos;

@Mixin(ParticleManager.class)
public abstract class MixinParticleManager
{
    @Inject(method = "addBlockDestroyEffects", at = @At("HEAD"), cancellable = true)
    private void onAddBlockDestroyEffects(BlockPos pos, IBlockState state, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_NO_BLOCK_BREAK_PARTICLES.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
