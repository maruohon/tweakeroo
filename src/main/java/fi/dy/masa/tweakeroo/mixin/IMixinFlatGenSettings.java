package fi.dy.masa.tweakeroo.mixin;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.world.gen.FlatGenSettings;
import net.minecraft.world.gen.FlatLayerInfo;

@Mixin(FlatGenSettings.class)
public interface IMixinFlatGenSettings
{
    @Invoker("func_197527_b")
    public static List<FlatLayerInfo> getLayersFromStringInvoker(String str) { return null; }
}
