package fi.dy.masa.tweakeroo.util;

import javax.annotation.Nullable;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
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

public class Tweaks
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
        if (posFirst != null && FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getBooleanValue())
        {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayerSP player = mc.player;
            RayTraceResult trace = mc.objectMouseOver;

            if (player != null && trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos pos = trace.getBlockPos();

                if (isNewPosValidForFastPlacement(pos, trace.sideHit, player))
                {
                    EnumHand hand = getHandWithItem(stackFirst, player);

                    if (hand != null)
                    {
                        while (true)
                        {
                            EnumActionResult result = tryPlaceBlock(mc.playerController, player, mc.world, pos, sideFirst, hitVecFirst, hand);

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
            }

            // Reset the timer to prevent the regular process method from re-firing
            ((IMinecraftAccessor) mc).setRightClickDelayTimer(4);
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
        BlockPos posNew = posIn;
        HitPart hitPart = getHitPart(sideIn, posIn, hitVec);
        EnumFacing side = sideIn;
        EnumFacing sideRotated = getRotatedFacing(sideIn, hitPart);
        EnumActionResult result;

        // Alt: Place the block facing/against the adjacent block (= just rotated from normal)
        if (FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue() && GuiScreen.isAltKeyDown())
        {
            posNew = posIn.offset(sideIn);

            // Alt + Ctrl: Place the block into the adjacent space
            if (GuiScreen.isCtrlKeyDown())
            {
                posNew = posNew.offset(sideRotated.getOpposite());
            }
            else
            {
                side = sideRotated;
            }

            result = handleFlexibleBlockPlacement(controller, player, world, posNew, side, hitVec, hand, hitPart);
        }
        else
        {
            result = controller.processRightClickBlock(player, world, posIn, sideIn, hitVec, hand);
        }

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
            Vec3d hitVec,
            EnumHand hand)
    {
        BlockPos posNew = posIn;
        EnumFacing side = sideIn;

        // Alt: Place the block facing/against the adjacent block (= just rotated from normal)
        if (FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue() && GuiScreen.isAltKeyDown())
        {
            posNew = posIn.offset(sideIn);

            // Alt + Ctrl: Place the block into the adjacent space
            if (GuiScreen.isCtrlKeyDown())
            {
                posNew = posNew.offset(sideRotatedFirst.getOpposite());
            }
            else
            {
                side = sideRotatedFirst;
            }

            return handleFlexibleBlockPlacement(controller, player, world, posNew, side, hitVec, hand, hitPartFirst);
        }
        else
        {
            return controller.processRightClickBlock(player, world, posIn, sideIn, hitVec, hand);
        }
    }

    private static EnumActionResult handleFlexibleBlockPlacement(
            PlayerControllerMP controller,
            EntityPlayerSP player,
            WorldClient world,
            BlockPos pos,
            EnumFacing side,
            Vec3d hitVec,
            EnumHand hand,
            HitPart hitPart)
    {
        boolean rotated = false;

        if (hitPart == HitPart.CENTER)
        {
            player.connection.sendPacket(new CPacketPlayer.Rotation(player.rotationYaw - 180f, player.rotationPitch, player.onGround));
            rotated = true;
        }
        else if (hitPart == HitPart.LEFT)
        {
            player.connection.sendPacket(new CPacketPlayer.Rotation(player.rotationYaw - 90f, player.rotationPitch, player.onGround));
            rotated = true;
        }
        else if (hitPart == HitPart.RIGHT)
        {
            player.connection.sendPacket(new CPacketPlayer.Rotation(player.rotationYaw + 90f, player.rotationPitch, player.onGround));
            rotated = true;
        }

        EnumActionResult result = controller.processRightClickBlock(player, world, pos, side, hitVec, hand);

        if (rotated)
        {
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

    public static EnumFacing getRotatedFacing(EnumFacing originalSide, HitPart hitPart)
    {
        if (originalSide.getAxis().isVertical())
        {
            switch (hitPart)
            {
                case LEFT:      return EnumFacing.EAST;
                case RIGHT:     return EnumFacing.WEST;
                case BOTTOM:    return EnumFacing.NORTH;
                case TOP:       return EnumFacing.SOUTH;
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

    public static HitPart getHitPart(EnumFacing originalSide, BlockPos pos, Vec3d hitVec)
    {
        double x = hitVec.x - pos.getX();
        double y = hitVec.y - pos.getY();
        double z = hitVec.z - pos.getZ();
        double posH = 0;
        double posV = 0;

        switch (originalSide)
        {
            case DOWN:
                posH = x;
                posV = z;
                break;
            case UP:
                posH = x;
                posV = 1.0d - z;
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
        if (areStacksEqual(player.getHeldItemMainhand(), stackFirst))
        {
            return EnumHand.MAIN_HAND;
        }

        if (areStacksEqual(player.getHeldItemOffhand(), stackFirst))
        {
            return EnumHand.OFF_HAND;
        }

        return null;
    }

    public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2)
    {
        return ItemStack.areItemsEqual(stack1, stack2) && ItemStack.areItemStackTagsEqual(stack1, stack2);
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
