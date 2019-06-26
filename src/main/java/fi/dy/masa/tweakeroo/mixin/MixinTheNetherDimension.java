package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.TheNetherDimension;

@Mixin(TheNetherDimension.class)
public abstract class MixinTheNetherDimension extends Dimension
{
    public MixinTheNetherDimension(World world, DimensionType dimensionType)
    {
        super(world, dimensionType);
    }

    @Override
    public boolean shouldRenderFog(int x, int z)
    {
        return ! Configs.Disable.DISABLE_NETHER_FOG.getBooleanValue();
    }
}
