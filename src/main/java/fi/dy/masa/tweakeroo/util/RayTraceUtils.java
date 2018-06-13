package fi.dy.masa.tweakeroo.util;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RayTraceUtils
{
    @Nonnull
    public static RayTraceResult getRayTraceFromEntity(World worldIn, Entity entityIn, boolean useLiquids)
    {
        double reach = 5.0d;
        return getRayTraceFromEntity(worldIn, entityIn, useLiquids, reach);
    }

    @Nonnull
    public static RayTraceResult getRayTraceFromEntity(World worldIn, Entity entityIn, boolean useLiquids, double range)
    {
        Vec3d eyesVec = new Vec3d(entityIn.posX, entityIn.posY + entityIn.getEyeHeight(), entityIn.posZ);
        Vec3d rangedLookRot = entityIn.getLook(1f).scale(range);
        Vec3d lookVec = eyesVec.add(rangedLookRot);

        RayTraceResult result = worldIn.rayTraceBlocks(eyesVec, lookVec, useLiquids, false, false);

        if (result == null)
        {
            result = new RayTraceResult(RayTraceResult.Type.MISS, Vec3d.ZERO, EnumFacing.UP, BlockPos.ORIGIN);
        }

        AxisAlignedBB bb = entityIn.getEntityBoundingBox().expand(rangedLookRot.x, rangedLookRot.y, rangedLookRot.z).expand(1d, 1d, 1d);
        List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity(entityIn, bb);

        double closest = result.typeOfHit == RayTraceResult.Type.BLOCK ? eyesVec.distanceTo(result.hitVec) : Double.MAX_VALUE;
        RayTraceResult entityTrace = null;
        Entity targetEntity = null;

        for (int i = 0; i < list.size(); i++)
        {
            Entity entity = list.get(i);
            bb = entity.getEntityBoundingBox();
            RayTraceResult traceTmp = bb.calculateIntercept(lookVec, eyesVec);

            if (traceTmp != null)
            {
                double distance = eyesVec.distanceTo(traceTmp.hitVec);

                if (distance <= closest)
                {
                    targetEntity = entity;
                    entityTrace = traceTmp;
                    closest = distance;
                }
            }
        }

        if (targetEntity != null)
        {
            result = new RayTraceResult(targetEntity, entityTrace.hitVec);
        }

        return result;
    }
}
