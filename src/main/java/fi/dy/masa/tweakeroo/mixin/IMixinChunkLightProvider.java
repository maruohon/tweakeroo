package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.ChunkLightProvider;

@Mixin(ChunkLightProvider.class)
public interface IMixinChunkLightProvider
{
    @Accessor("chunkProvider")
    ChunkProvider tweakeroo_getChunkProvider();
}
