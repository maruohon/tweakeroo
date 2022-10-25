package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import tweakeroo.config.FeatureToggle;

@Mixin(net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.class)
public abstract class MixinChunkRenderDispatcher
{
    @Shadow @Final private net.minecraft.client.renderer.chunk.ChunkRenderWorker renderWorker;

    @Inject(method = "updateTransparencyLater", at = @At("HEAD"), cancellable = true)
    private void forceTransparencyUpdatesOnMainThread(net.minecraft.client.renderer.chunk.RenderChunk renderer, CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_CHUNK_RENDER_MAIN_THREAD.getBooleanValue())
        {
            renderer.getLockCompileTask().lock();

            try
            {
                final net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator task = renderer.makeCompileTaskTransparency();

                if (task != null)
                {
                    ((IMixinChunkRenderWorker) this.renderWorker).invokeProcessTask(task);
                }
            }
            finally
            {
                renderer.getLockCompileTask().unlock();
                cir.setReturnValue(true);
            }
        }
    }
}
