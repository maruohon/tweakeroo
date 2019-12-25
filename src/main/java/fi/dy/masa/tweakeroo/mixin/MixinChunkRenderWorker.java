package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(net.minecraft.client.renderer.chunk.ChunkRenderWorker.class)
public abstract class MixinChunkRenderWorker
{
    @Inject(method = "isChunkExisting", at = @At("HEAD"), cancellable = true)
    private void allowEdgeChunksToRender(
            net.minecraft.util.math.BlockPos pos,
            net.minecraft.world.World worldIn,
            CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_RENDER_EDGE_CHUNKS.getBooleanValue())
        {
            cir.setReturnValue(true);
        }
    }
}
