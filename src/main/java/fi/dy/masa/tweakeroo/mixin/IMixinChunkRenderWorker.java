package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(net.minecraft.client.renderer.chunk.ChunkRenderWorker.class)
public interface IMixinChunkRenderWorker
{
    @Invoker("processTask")
    void invokeProcessTask(final net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator task);
}
