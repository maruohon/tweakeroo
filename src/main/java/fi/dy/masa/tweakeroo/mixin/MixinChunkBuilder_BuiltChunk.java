package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(ChunkBuilder.BuiltChunk.class)
public abstract class MixinChunkBuilder_BuiltChunk
{
    @Inject(method = "isChunkNonEmpty", at = @At("HEAD"), cancellable = true)
    private void allowEdgeChunksToRender(BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_RENDER_EDGE_CHUNKS.getBooleanValue())
        {
            cir.setReturnValue(true);
        }
    }
}
