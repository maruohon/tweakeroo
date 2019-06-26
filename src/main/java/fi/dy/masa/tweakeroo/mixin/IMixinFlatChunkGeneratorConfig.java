package fi.dy.masa.tweakeroo.mixin;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;

@Mixin(FlatChunkGeneratorConfig.class)
public interface IMixinFlatChunkGeneratorConfig
{
    @Invoker("parseLayersString")
    public static List<FlatChunkGeneratorLayer> getLayersFromStringInvoker(String str) { return null; }
}
