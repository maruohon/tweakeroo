package fi.dy.masa.tweakeroo.tweaks;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
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
        @Nullable PropertyDirection property = fi.dy.masa.malilib.util.BlockUtils.getFirstDirectionProperty(stateIn);
        int x = (int) hitVec.x % 10;

        if (x >= 2 && property != null)
        {
            EnumFacing facingOrig = stateIn.getValue(property);
            EnumFacing facing = facingOrig;
            int facingIndex = x - 2;

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
                state = state.withProperty(property, facing);
            }
        }

        if (block instanceof BlockRedstoneRepeater)
        {
            if (x > 10)
            {
                Integer delay = (x / 10) + 1;

                if (BlockRedstoneRepeater.DELAY.getAllowedValues().contains(delay))
                {
                    state = state.withProperty(BlockRedstoneRepeater.DELAY, delay);
                }
            }
        }
        else if (block instanceof BlockRedstoneComparator)
        {
            if (x >= 10)
            {
                state = state.withProperty(BlockRedstoneComparator.MODE, BlockRedstoneComparator.Mode.SUBTRACT);
            }
        }
        else if (block instanceof BlockTrapDoor)
        {
            if (x >= 10)
            {
                state = state.withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP);
            }
        }
        else if (block instanceof BlockStairs && state.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP)
        {
            if (x >= 10)
            {
                state = state.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
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
