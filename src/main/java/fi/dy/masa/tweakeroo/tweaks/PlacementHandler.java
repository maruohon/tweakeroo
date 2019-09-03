package fi.dy.masa.tweakeroo.tweaks;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlacementHandler
{
    public static BlockState getStateForPlacement(BlockState state, UseContext context)
    {
        Vec3d hitVec = context.getHitVec();
        @Nullable DirectionProperty property = fi.dy.masa.malilib.util.BlockUtils.getFirstDirectionProperty(state);
        int x = (int) (hitVec.x - (double) context.getPos().getX());

        if (x >= 2 && property != null)
        {
            Direction facingOrig = state.get(property);
            Direction facing = facingOrig;
            int facingIndex = ((x - 2) / 2) % 16;

            if (facingIndex == 6) // the opposite of the normal facing requested
            {
                facing = facing.getOpposite();
            }
            else if (facingIndex >= 0 && facingIndex <= 5)
            {
                facing = Direction.byId(facingIndex);

                if (property.getValues().contains(facing) == false)
                {
                    facing = context.getEntity().getHorizontalFacing().getOpposite();
                }
            }

            //System.out.printf("plop facing: %d -> %s\n", facingIndex, facing);

            if (facing != facingOrig && property.getValues().contains(facing))
            {
                state = state.with(property, facing);
            }
        }

        if (x >= 16)
        {
            Block block = state.getBlock();

            if (block instanceof RepeaterBlock)
            {
                Integer delay = (x / 16) + 1;

                if (RepeaterBlock.DELAY.getValues().contains(delay))
                {
                    state = state.with(RepeaterBlock.DELAY, delay);
                }
            }
            else if (block instanceof ComparatorBlock)
            {
                state = state.with(ComparatorBlock.MODE, ComparatorMode.SUBTRACT);
            }
            else if (block instanceof TrapdoorBlock)
            {
                state = state.with(TrapdoorBlock.HALF, BlockHalf.TOP);
            }
            else if (block instanceof StairsBlock && state.get(StairsBlock.HALF) == BlockHalf.BOTTOM)
            {
                state = state.with(StairsBlock.HALF, BlockHalf.TOP);
            }
        }

        return state;
    }

    public static class UseContext
    {
        private final World world;
        private final BlockPos pos;
        private final Direction side;
        private final Vec3d hitVec;
        private final LivingEntity entity;
        private final Hand hand;

        private UseContext(World world, BlockPos pos, Direction side, Vec3d hitVec, LivingEntity entity, Hand hand)
        {
            this.world = world;
            this.pos = pos;
            this.side = side;
            this.hitVec = hitVec;
            this.entity = entity;
            this.hand = hand;
        }

        public static UseContext of(World world, BlockPos pos, Direction side, Vec3d hitVec, LivingEntity entity, Hand hand)
        {
            return new UseContext(world, pos, side, hitVec, entity, hand);
        }

        public static UseContext from(ItemPlacementContext ctx, Hand hand)
        {
            Vec3d pos = ctx.getHitPos();
            return new UseContext(ctx.getWorld(), ctx.getBlockPos(), ctx.getSide(), new Vec3d(pos.x, pos.y, pos.z), ctx.getPlayer(), hand);
        }

        public World getWorld()
        {
            return world;
        }

        public BlockPos getPos()
        {
            return pos;
        }

        public Direction getSide()
        {
            return side;
        }

        public Vec3d getHitVec()
        {
            return hitVec;
        }

        public LivingEntity getEntity()
        {
            return entity;
        }

        public Hand getHand()
        {
            return hand;
        }
    }
}
