package fi.dy.masa.tweakeroo.mixin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(value = TileEntityStructure.class, priority = 999)
public abstract class MixinTileEntityStructure extends TileEntity
{
    @ModifyConstant(method = "readFromNBT",
                    slice = @Slice(from = @At(value = "FIELD",
                                              target = "Lnet/minecraft/tileentity/TileEntityStructure;position:Lnet/minecraft/util/math/BlockPos;"),
                                   to = @At(value = "FIELD",
                                            target = "Lnet/minecraft/tileentity/TileEntityStructure;size:Lnet/minecraft/util/math/BlockPos;")),
                    constant = @Constant(intValue = 32), require = 0)
    private int overrideMaxSize(int original)
    {
        if (FeatureToggle.TWEAK_STRUCTURE_BLOCK_LIMIT.getBooleanValue())
        {
            return Configs.Generic.STRUCTURE_BLOCK_MAX_SIZE.getIntegerValue();
        }

        return original;
    }

    @Inject(method = "getNearbyCornerBlocks", at = @At("HEAD"), cancellable = true)
    private void overrideCornerBlockScan(BlockPos start, BlockPos end, CallbackInfoReturnable<List<TileEntityStructure>> cir)
    {
        if (FeatureToggle.TWEAK_STRUCTURE_BLOCK_LIMIT.getBooleanValue())
        {
            List<TileEntityStructure> structureBlocks = new ArrayList<>();
            BlockPos pos = this.getPos();
            World world = this.getWorld();
            String name = ((TileEntityStructure) (Object) this).getName();
            int maxSize = Configs.Generic.STRUCTURE_BLOCK_MAX_SIZE.getIntegerValue();

            // Expand by the maximum position/offset and a bit of margin
            final int minX = pos.getX() - maxSize - 32 - 2;
            final int minZ = pos.getZ() - maxSize - 32 - 2;
            final int maxX = pos.getX() + maxSize + 32 + 2;
            final int maxZ = pos.getZ() + maxSize + 32 + 2;

            final int minY = Math.max(  0, pos.getY() - maxSize - 32 - 2);
            final int maxY = Math.min(255, pos.getY() + maxSize + 32 + 2);

            for (int cz = minZ >> 4; cz <= (maxZ >> 4); ++cz)
            {
                for (int cx = minX >> 4; cx <= (maxX >> 4); ++cx)
                {
                    Collection<TileEntity> list = world.getChunk(cx, cz).getTileEntityMap().values();

                    for (TileEntity te : list)
                    {
                        if (te instanceof TileEntityStructure)
                        {
                            TileEntityStructure tes = (TileEntityStructure) te;
                            BlockPos p = te.getPos();

                            if (tes.getMode() == TileEntityStructure.Mode.CORNER &&
                                tes.getName().equals(name) &&
                                p.getX() >= minX && p.getX() <= maxX &&
                                p.getY() >= minY && p.getY() <= maxY &&
                                p.getZ() >= minZ && p.getZ() <= maxZ)
                            {
                                structureBlocks.add((TileEntityStructure) te);
                            }
                        }
                    }
                }
            }

            cir.setReturnValue(structureBlocks);
        }
    }

    @Override
    public double getMaxRenderDistanceSquared()
    {
        if (FeatureToggle.TWEAK_STRUCTURE_BLOCK_LIMIT.getBooleanValue())
        {
            return 65536.0D;
        }

        return super.getMaxRenderDistanceSquared();
    }
}
