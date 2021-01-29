package fi.dy.masa.tweakeroo.mixin;

import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.block.*;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(ScaffoldingBlock.class)
public abstract class MixinScaffoldingBlock extends Block implements Waterloggable {
    public MixinScaffoldingBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        if (Configs.Disable.DISABLE_RENDERING_SCAFFOLDING.getBooleanValue() && state.getBlock() == Blocks.SCAFFOLDING) {
            return BlockRenderType.INVISIBLE;
        }

        return super.getRenderType(state);
    }
}
