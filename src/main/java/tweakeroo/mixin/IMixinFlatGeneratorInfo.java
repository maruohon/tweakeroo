package tweakeroo.mixin;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;

@Mixin(FlatGeneratorInfo.class)
public interface IMixinFlatGeneratorInfo
{
    @Invoker("getLayersFromString")
    public static List<FlatLayerInfo> getLayersFromStringInvoker(int parts, String str) { return null; }
}
