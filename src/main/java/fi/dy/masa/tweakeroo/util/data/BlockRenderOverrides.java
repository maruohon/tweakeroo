package fi.dy.masa.tweakeroo.util.data;

import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumBlockRenderType;

public class BlockRenderOverrides
{
    private static final Object2ObjectOpenHashMap<IBlockState, EnumBlockRenderType> RENDER_TYPE_OVERRIDES = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<IBlockState, IBlockState> MODEL_OVERRIDES = new Object2ObjectOpenHashMap<>();

    // TODO/FIXME temporary hard-coded things until proper configs are added for these
    public static void init()
    {
        RENDER_TYPE_OVERRIDES.put(Blocks.BARRIER.getDefaultState(), EnumBlockRenderType.MODEL);
        MODEL_OVERRIDES.put(Blocks.BARRIER.getDefaultState(), Blocks.GLASS.getDefaultState());
    }

    @Nullable
    public static EnumBlockRenderType getRenderType(IBlockState state)
    {
        return RENDER_TYPE_OVERRIDES.get(state);
    }

    @Nullable
    public static IBlockState getModelOverrideState(IBlockState state)
    {
        return MODEL_OVERRIDES.get(state);
    }
}
