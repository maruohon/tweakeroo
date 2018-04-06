package fi.dy.masa.tweakeroo.util;

import javax.annotation.Nullable;
import fi.dy.masa.tweakeroo.config.ConfigsGeneric;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class PlacementTweaks
{
    private static BlockPos posFirst = null;
    private static BlockPos posLast = null;
    private static HitPart hitPartFirst = null;
    private static Vec3d hitVecFirst = null;
    private static Vec3d playerPosLast = null;
    private static EnumFacing sideFirst = null;
    private static EnumFacing sideRotatedFirst = null;
    private static ItemStack stackFirst = ItemStack.EMPTY;

    public static void onUsingTick()
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (posFirst != null && FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getBooleanValue())
        {
            EntityPlayerSP player = mc.player;
            RayTraceResult trace = mc.objectMouseOver;

            if (player != null && trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos pos = trace.getBlockPos();
                EnumHand hand = getHandWithItem(stackFirst, player);

                if (hand != null && isNewPosValidForFastPlacement(pos, trace.sideHit, player))
                {
                    int failSafe = 10;

                    while (failSafe-- > 0)
                    {
                        EnumActionResult result = tryPlaceBlock(mc.playerController,
                                player, mc.world, pos, sideFirst, sideRotatedFirst, hitVecFirst, hand, hitPartFirst);

                        if (result == EnumActionResult.SUCCESS)
                        {
                            posLast = pos;
                        }
                        else
                        {
                            break;
                        }
                    }
                }

                playerPosLast = player.getPositionVector();
            }

            // Reset the timer to prevent the regular process method from re-firing
            ((IMinecraftAccessor) mc).setRightClickDelayTimer(4);
        }
        else if (FeatureToggle.TWEAK_FAST_RIGHT_CLICK.getBooleanValue() && mc.gameSettings.keyBindUseItem.isKeyDown())
        {
            final int count = ConfigsGeneric.FAST_RIGHT_CLICK_COUNT.getIntegerValue();

            for (int i = 0; i < count; ++i)
            {
                ((IMinecraftAccessor) mc).rightClickMouseAccessor();
            }
        }
    }

    public static EnumActionResult onProcessRightClickBlock(
            PlayerControllerMP controller,
            EntityPlayerSP player,
            WorldClient world,
            BlockPos posIn,
            EnumFacing sideIn,
            Vec3d hitVec,
            EnumHand hand)
    {
        EnumFacing playerFacingH = player.getHorizontalFacing();
        HitPart hitPart = getHitPart(sideIn, playerFacingH, posIn, hitVec);
        EnumFacing sideRotated = getRotatedFacing(sideIn, playerFacingH, hitPart);
        EnumActionResult result = tryPlaceBlock(controller, player, world, posIn, sideIn, sideRotated, hitVec, hand, hitPart);

        // Store the initial click data for the fast placement mode
        if (posFirst == null && FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getBooleanValue())
        {
            posFirst = posIn;
            posLast = posIn;
            hitPartFirst = hitPart;
            hitVecFirst = hitVec;
            sideFirst = sideIn;
            sideRotatedFirst = sideRotated;
            playerPosLast = player.getPositionVector();
            stackFirst = player.getHeldItem(hand).copy();
        }

        return result;
    }

    private static EnumActionResult tryPlaceBlock(
            PlayerControllerMP controller,
            EntityPlayerSP player,
            WorldClient world,
            BlockPos posIn,
            EnumFacing sideIn,
            EnumFacing sideRotatedIn,
            Vec3d hitVec,
            EnumHand hand,
            HitPart hitPart)
    {
        if (FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue())
        {
            BlockPos posNew = posIn.offset(sideIn);
            EnumFacing side = sideIn;
            boolean handle = false;

            // Place the block facing/against the adjacent block (= just rotated from normal)
            if (Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ROTATION.getKeybind().isKeybindHeld(false))
            {
                side = sideRotatedIn;
                handle = true;
            }
            else
            {
                // Don't rotate the player facing in handleFlexibleBlockPlacement()
                hitPart = null;
            }

            // Place the block into the diagonal position
            if (Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_OFFSET.getKeybind().isKeybindHeld(false))
            {
                posNew = posNew.offset(sideRotatedIn.getOpposite());
                handle = true;
            }

            if (handle)
            {
                if (world.getBlockState(posNew).getBlock().isReplaceable(world, posNew))
                {
                    return handleFlexibleBlockPlacement(controller, player, world, posNew, side, hitVec, hand, hitPart);
                }
                else
                {
                    return EnumActionResult.FAIL;
                }
            }
        }

        return controller.processRightClickBlock(player, world, posIn, sideIn, hitVec, hand);
    }

    private static EnumActionResult handleFlexibleBlockPlacement(
            PlayerControllerMP controller,
            EntityPlayerSP player,
            WorldClient world,
            BlockPos pos,
            EnumFacing side,
            Vec3d hitVec,
            EnumHand hand,
            @Nullable HitPart hitPart)
    {
        boolean rotated = false;
        EnumFacing facing = player.getHorizontalFacing();
        float yawOrig = player.rotationYaw;

        if (hitPart == HitPart.CENTER)
        {
            facing = facing.getOpposite();
            rotated = true;
        }
        else if (hitPart == HitPart.LEFT)
        {
            facing = facing.rotateYCCW();
            rotated = true;
        }
        else if (hitPart == HitPart.RIGHT)
        {
            facing = facing.rotateY();
            rotated = true;
        }

        if (rotated)
        {
            player.rotationYaw = facing.getHorizontalAngle();
            player.connection.sendPacket(new CPacketPlayer.Rotation(player.rotationYaw, player.rotationPitch, player.onGround));
        }

        EnumActionResult result = controller.processRightClickBlock(player, world, pos, side, hitVec, hand);

        if (rotated)
        {
            player.rotationYaw = yawOrig;
            player.connection.sendPacket(new CPacketPlayer.Rotation(player.rotationYaw, player.rotationPitch, player.onGround));
        }

        return result;
    }

    public static void clearClickedBlockInfo()
    {
        posFirst = null;
        hitPartFirst = null;
        hitVecFirst = null;
        sideFirst = null;
        sideRotatedFirst = null;
        stackFirst = ItemStack.EMPTY;
    }

    public static EnumFacing getRotatedFacing(EnumFacing originalSide, EnumFacing playerFacingH, HitPart hitPart)
    {
        if (originalSide.getAxis().isVertical())
        {
            switch (hitPart)
            {
                case LEFT:      return playerFacingH.rotateY();
                case RIGHT:     return playerFacingH.rotateYCCW();
                case BOTTOM:    return originalSide == EnumFacing.UP   ? playerFacingH : playerFacingH.getOpposite();
                case TOP:       return originalSide == EnumFacing.DOWN ? playerFacingH : playerFacingH.getOpposite();
                case CENTER:    return originalSide.getOpposite();
                default:        return originalSide;
            }
        }
        else
        {
            switch (hitPart)
            {
                case LEFT:      return originalSide.rotateYCCW();
                case RIGHT:     return originalSide.rotateY();
                case BOTTOM:    return EnumFacing.UP;
                case TOP:       return EnumFacing.DOWN;
                case CENTER:    return originalSide.getOpposite();
                default:        return originalSide;
            }
        }
    }

    public static HitPart getHitPart(EnumFacing originalSide, EnumFacing playerFacingH, BlockPos pos, Vec3d hitVec)
    {
        double x = hitVec.x - pos.getX();
        double y = hitVec.y - pos.getY();
        double z = hitVec.z - pos.getZ();
        double posH = 0;
        double posV = 0;

        switch (originalSide)
        {
            case DOWN:
            case UP:
                switch (playerFacingH)
                {
                    case NORTH:
                        posH = x;
                        posV = 1.0d - z;
                        break;
                    case SOUTH:
                        posH = 1.0d - x;
                        posV = z;
                        break;
                    case WEST:
                        posH = 1.0d - z;
                        posV = 1.0d - x;
                        break;
                    case EAST:
                        posH = z;
                        posV = x;
                        break;
                    default:
                }

                if (originalSide == EnumFacing.DOWN)
                {
                    posV = 1.0d - posV;
                }

                break;
            case NORTH:
            case SOUTH:
                posH = originalSide.getAxisDirection() == AxisDirection.POSITIVE ? x : 1.0d - x;
                posV = y;
                break;
            case WEST:
            case EAST:
                posH = originalSide.getAxisDirection() == AxisDirection.NEGATIVE ? z : 1.0d - z;
                posV = y;
                break;
        }

        double offH = Math.abs(posH - 0.5d);
        double offV = Math.abs(posV - 0.5d);

        if (offH > 0.25d || offV > 0.25d)
        {
            if (offH > offV)
            {
                return posH < 0.5d ? HitPart.LEFT : HitPart.RIGHT;
            }
            else
            {
                return posV < 0.5d ? HitPart.BOTTOM : HitPart.TOP;
            }
        }
        else
        {
            return HitPart.CENTER;
        }
    }

    private static boolean isNewPosValidForFastPlacement(BlockPos posNew, EnumFacing side, EntityPlayerSP player)
    {
        if (side == sideFirst && posNew.equals(posLast) == false)
        {
            EnumFacing.Axis axis = side.getAxis();

            // Cursor moved to adjacent position
            if ((axis == EnumFacing.Axis.X && posNew.getX() == posLast.getX()) ||
                (axis == EnumFacing.Axis.Z && posNew.getZ() == posLast.getZ()) ||
                (axis == EnumFacing.Axis.Y && posNew.getY() == posLast.getY()))
            {
                return true;
            }

            if (getPlayerMovementDirection(player) == side && posLast.offset(side).equals(posNew))
            {
                return true;
            }
        }

        return false;
    }

    @Nullable
    private static EnumFacing getPlayerMovementDirection(EntityPlayerSP player)
    {
        double dx = player.posX - playerPosLast.x;
        double dy = player.posY - playerPosLast.y;
        double dz = player.posZ - playerPosLast.z;
        double ax = Math.abs(dx);
        double ay = Math.abs(dy);
        double az = Math.abs(dz);

        if (Math.max(Math.max(ax, az), ay) < 0.001)
        {
            return null;
        }

        if (ax > az)
        {
            if (ax > ay)
            {
                return dx > 0 ? EnumFacing.EAST : EnumFacing.WEST;
            }
            else
            {
                return dy > 0 ? EnumFacing.UP : EnumFacing.DOWN;
            }
        }
        else
        {
            if (az > ay)
            {
                return dz > 0 ? EnumFacing.SOUTH : EnumFacing.NORTH;
            }
            else
            {
                return dy > 0 ? EnumFacing.UP : EnumFacing.DOWN;
            }
        }
    }

    @Nullable
    public static EnumHand getHandWithItem(ItemStack stack, EntityPlayerSP player)
    {
        if (InventoryUtils.areStacksEqual(player.getHeldItemMainhand(), stackFirst))
        {
            return EnumHand.MAIN_HAND;
        }

        if (InventoryUtils.areStacksEqual(player.getHeldItemOffhand(), stackFirst))
        {
            return EnumHand.OFF_HAND;
        }

        return null;
    }

    public enum HitPart
    {
        CENTER,
        LEFT,
        RIGHT,
        BOTTOM,
        TOP;
    }
}
