package fi.dy.masa.tweakeroo.mixin;

import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.map.MapState;

@Mixin(ClientWorld.class)
public interface IMixinClientWorld
{
    @Accessor("mapStates")
    Map<String, MapState> tweakeroo_getMapStates();
}
