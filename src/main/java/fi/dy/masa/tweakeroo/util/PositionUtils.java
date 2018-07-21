package fi.dy.masa.tweakeroo.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PositionUtils
{
    public static EnumFacing getClosestLookingDirection(Entity entity)
    {
        if (entity.rotationPitch >= 60)
        {
            return EnumFacing.DOWN;
        }
        else if (entity.rotationPitch <= -60)
        {
            return EnumFacing.UP;
        }

        return entity.getHorizontalFacing();
    }

    public static BlockPos getPositionInfrontOfEntity(Entity entity)
    {
        BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);

        if (entity.rotationPitch >= 60)
        {
            return pos.down();
        }
        else if (entity.rotationPitch <= -60)
        {
            return new BlockPos(entity.posX, Math.ceil(entity.getEntityBoundingBox().maxY), entity.posZ);
        }

        double y = Math.floor(entity.posY + entity.getEyeHeight());

        switch (entity.getHorizontalFacing())
        {
            case EAST:
                return new BlockPos((int) Math.ceil( entity.posX + entity.width / 2),     (int) y, (int) Math.floor(entity.posZ));
            case WEST:
                return new BlockPos((int) Math.floor(entity.posX - entity.width / 2) - 1, (int) y, (int) Math.floor(entity.posZ));
            case SOUTH:
                return new BlockPos((int) Math.floor(entity.posX), (int) y, (int) Math.ceil( entity.posZ + entity.width / 2)    );
            case NORTH:
                return new BlockPos((int) Math.floor(entity.posX), (int) y, (int) Math.floor(entity.posZ - entity.width / 2) - 1);
            default:
        }

        return pos;
    }

    public static Vec3d getHitVecCenter(BlockPos basePos, EnumFacing facing)
    {
        int x = basePos.getX();
        int y = basePos.getY();
        int z = basePos.getZ();

        switch (facing)
        {
            case UP:    return new Vec3d(x + 0.5, y + 1  , z + 0.5);
            case DOWN:  return new Vec3d(x + 0.5, y      , z + 0.5);
            case NORTH: return new Vec3d(x + 0.5, y + 0.5, z      );
            case SOUTH: return new Vec3d(x + 0.5, y + 0.5, z + 1  );
            case WEST:  return new Vec3d(x      , y + 0.5, z      );
            case EAST:  return new Vec3d(x + 1  , y + 0.5, z + 1);
            default:    return new Vec3d(x, y, z);
        }
    }
}
