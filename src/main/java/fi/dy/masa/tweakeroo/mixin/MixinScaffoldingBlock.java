package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ScaffoldingBlock;
import fi.dy.masa.tweakeroo.config.Configs;


@Mixin(ScaffoldingBlock.class)
public abstract class MixinScaffoldingBlock extends Block
{
    private MixinScaffoldingBlock(Settings settings)
    {
        super(settings);
    }

    @Deprecated
    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        if (Configs.Disable.DISABLE_RENDERING_SCAFFOLDING.getBooleanValue() &&
            state.getBlock() == Blocks.SCAFFOLDING)
        {
            return BlockRenderType.INVISIBLE;
        }

        return super.getRenderType(state);
    }
}
