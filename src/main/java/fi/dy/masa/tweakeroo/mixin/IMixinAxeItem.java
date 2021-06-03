package fi.dy.masa.tweakeroo.mixin;

import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.block.Block;
import net.minecraft.item.AxeItem;

@Mixin(AxeItem.class)
public interface IMixinAxeItem
{
    @Accessor("STRIPPED_BLOCKS")
    static Map<Block, Block> tweakeroo_getStrippedBlocks() { return null; }
}
