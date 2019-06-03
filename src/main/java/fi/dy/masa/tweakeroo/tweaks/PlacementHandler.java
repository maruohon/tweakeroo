package fi.dy.masa.tweakeroo.tweaks;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.ComparatorMode;
import net.minecraft.state.properties.Half;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlacementHandler
{
    public static IBlockState getStateForPlacement(IBlockState stateIn, UseContext context)
    {
        IBlockState state = stateIn;
        Vec3d hitVec = context.getHitVec();
        Block block = stateIn.getBlock();
        @Nullable DirectionProperty property = fi.dy.masa.malilib.util.BlockUtils.getFirstDirectionProperty(stateIn);
        int x = (int) hitVec.x;

        if (x >= 2 && property != null)
        {
            EnumFacing facingOrig = stateIn.get(property);
            EnumFacing facing = facingOrig;
            int facingIndex = (x % 10) - 2;

            if (facingIndex == 6) // the opposite of the normal facing requested
            {
                facing = facing.getOpposite();
            }
            else if (facingIndex >= 0 && facingIndex <= 5)
            {
                facing = EnumFacing.byIndex(facingIndex);

                if (property.getAllowedValues().contains(facing) == false)
                {
                    facing = context.getEntity().getHorizontalFacing().getOpposite();
                }
            }

            //System.out.printf("plop facing: %d -> %s\n", facingIndex, facing);

            if (facing != facingOrig && property.getAllowedValues().contains(facing))
            {
                state = state.with(property, facing);
            }
        }

        if (x >= 10)
        {
            if (block instanceof BlockRedstoneRepeater)
            {
                Integer delay = (x / 10) + 1;

                if (BlockRedstoneRepeater.DELAY.getAllowedValues().contains(delay))
                {
                    state = state.with(BlockRedstoneRepeater.DELAY, delay);
                }
            }
            else if (block instanceof BlockRedstoneComparator)
            {
                state = state.with(BlockRedstoneComparator.MODE, ComparatorMode.SUBTRACT);
            }
            else if (block instanceof BlockTrapDoor)
            {
                state = state.with(BlockTrapDoor.HALF, Half.TOP);
            }
            else if (block instanceof BlockStairs && state.get(BlockStairs.HALF) == Half.BOTTOM)
            {
                state = state.with(BlockStairs.HALF, Half.TOP);
            }
        }

        return state;
    }

    public static class UseContext
    {
        private final World world;
        private final BlockPos pos;
        private final EnumFacing side;
        private final Vec3d hitVec;
        private final EntityLivingBase entity;
        private final EnumHand hand;

        private UseContext(World world, BlockPos pos, EnumFacing side, Vec3d hitVec, EntityLivingBase entity, EnumHand hand)
        {
            this.world = world;
            this.pos = pos;
            this.side = side;
            this.hitVec = hitVec;
            this.entity = entity;
            this.hand = hand;
        }

        public static UseContext of(World world, BlockPos pos, EnumFacing side, Vec3d hitVec, EntityLivingBase entity, EnumHand hand)
        {
            return new UseContext(world, pos, side, hitVec, entity, hand);
        }

        public static UseContext from(BlockItemUseContext ctx, EnumHand hand)
        {
            return new UseContext(ctx.getWorld(), ctx.getPos(), ctx.getFace(), new Vec3d(ctx.getHitX(), ctx.getHitY(), ctx.getHitZ()), ctx.getPlayer(), hand);
        }

        public World getWorld()
        {
            return world;
        }

        public BlockPos getPos()
        {
            return pos;
        }

        public EnumFacing getSide()
        {
            return side;
        }

        public Vec3d getHitVec()
        {
            return hitVec;
        }

        public EntityLivingBase getEntity()
        {
            return entity;
        }

        public EnumHand getHand()
        {
            return hand;
        }
    }
}
