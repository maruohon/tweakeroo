package fi.dy.masa.tweakeroo.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.world.World;

// Have to extend it just to get a public constructor >_>
public class ParticleDiggingExt extends ParticleDigging
{
    public ParticleDiggingExt(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
            double xSpeedIn, double ySpeedIn, double zSpeedIn, IBlockState state)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, state);
    }
}
