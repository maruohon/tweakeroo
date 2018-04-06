package fi.dy.masa.tweakeroo.util;

import javax.annotation.Nullable;
import fi.dy.masa.tweakeroo.config.ConfigsGeneric;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.event.InputEventHandler;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class PlacementTweaks
{
    private static BlockPos posFirst = null;
    private static BlockPos posLast = null;
    private static HitPart hitPartFirst = null;
    private static Vec3d hitVecFirst = null;
    private static EnumFacing sideFirst = null;
    private static EnumFacing sideRotatedFirst = null;
    private static float playerYawFirst;
    private static ItemStack stackFirst = ItemStack.EMPTY;
    private static FastMode fastMode = FastMode.PLANE;

    public static void onTick(Minecraft mc)
    {
        if (mc.currentScreen == null && mc.gameSettings.keyBindUseItem.isKeyDown())
        {
            onUsingTick();
        }
        else
        {
            if (mc.currentScreen == null &&
                mc.gameSettings.keyBindAttack.isKeyDown() &&
                FeatureToggle.TWEAK_FAST_LEFT_CLICK.getBooleanValue())
            {
                final int count = ConfigsGeneric.FAST_LEFT_CLICK_COUNT.getIntegerValue();

                for (int i = 0; i < count; ++i)
                {
                    ((IMinecraftAccessor) mc).leftClickMouseAccessor();
                }
            }

            clearClickedBlockInfo();
        }
    }

    public static void setFastPlacementMode(FastMode mode)
    {
        fastMode = mode;

        Minecraft mc = Minecraft.getMinecraft();
        String str = TextFormatting.GREEN + mode.name() + TextFormatting.RESET;
        InputEventHandler.printMessage(mc, "tweakeroo.message.set_fast_placement_mode_to", str);
    }

    public static FastMode getFastPlacementMode()
    {
        return fastMode;
    }

    private static void onUsingTick()
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (posFirst != null && mc.player != null && FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getBooleanValue())
        {
            EntityPlayerSP player = mc.player;
            World world = player.getEntityWorld();
            double reach = mc.playerController.getBlockReachDistance();
            int failSafe = 10;

            while (failSafe-- > 0)
            {
                RayTraceResult trace = mc.objectMouseOver;

                if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    EnumFacing side = trace.sideHit;
                    BlockPos pos = trace.getBlockPos();
                    BlockPos posNew = pos.offset(side);
                    EnumHand hand = getHandWithItem(stackFirst, player);

                    if (hand != null && world.getBlockState(posNew).getBlock().isReplaceable(world, posNew) &&
                        (
                            (fastMode == FastMode.PLANE  && isNewPositionValidForPlaneMode(posNew)) ||
                            (fastMode == FastMode.FACE   && isNewPositionValidForFaceMode(posNew, side)) ||
                            (fastMode == FastMode.COLUMN && isNewPositionValidForColumnMode(posNew))
                        )
                    )
                    {
                        IBlockState state = world.getBlockState(pos);
                        float x = (float) (trace.hitVec.x - pos.getX());
                        float y = (float) (trace.hitVec.y - pos.getY());
                        float z = (float) (trace.hitVec.z - pos.getZ());

                        if (state.getBlock().onBlockActivated(world, pos, state, player, hand, side, x, y, z))
                        {
                            return;
                        }

                        EnumActionResult result = tryPlaceBlock(mc.playerController,
                                player, mc.world, posNew, sideFirst, sideRotatedFirst, hitVecFirst, hand, hitPartFirst);

                        if (result == EnumActionResult.SUCCESS)
                        {
                            posLast = posNew;
                            mc.objectMouseOver = player.rayTrace(reach, mc.getRenderPartialTicks());
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
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
            posFirst = posIn.offset(sideIn);
            posLast = posIn;
            hitPartFirst = hitPart;
            hitVecFirst = hitVec;
            sideFirst = sideIn;
            sideRotatedFirst = sideRotated;
            playerYawFirst = player.rotationYaw;
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
            BlockPos posNew = posIn;
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
        EnumFacing facing = EnumFacing.getHorizontal(MathHelper.floor((playerYawFirst * 4.0F / 360.0F) + 0.5D) & 3);
        float yawOrig = player.rotationYaw;

        if (hitPart == HitPart.CENTER)
        {
            facing = facing.getOpposite();
        }
        else if (hitPart == HitPart.LEFT)
        {
            facing = facing.rotateYCCW();
        }
        else if (hitPart == HitPart.RIGHT)
        {
            facing = facing.rotateY();
        }

        player.rotationYaw = facing.getHorizontalAngle();
        player.connection.sendPacket(new CPacketPlayer.Rotation(player.rotationYaw, player.rotationPitch, player.onGround));

        EnumActionResult result = controller.processRightClickBlock(player, world, pos, side, hitVec, hand);

        player.rotationYaw = yawOrig;
        player.connection.sendPacket(new CPacketPlayer.Rotation(player.rotationYaw, player.rotationPitch, player.onGround));

        return result;
    }

    private static void clearClickedBlockInfo()
    {
        posFirst = null;
        hitPartFirst = null;
        hitVecFirst = null;
        sideFirst = null;
        sideRotatedFirst = null;
        stackFirst = ItemStack.EMPTY;
    }

    private static EnumFacing getRotatedFacing(EnumFacing originalSide, EnumFacing playerFacingH, HitPart hitPart)
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

    private static boolean isNewPositionValidForPlaneMode(BlockPos posNew)
    {
        if (posNew.equals(posLast) == false)
        {
            EnumFacing.Axis axis = sideFirst.getAxis();

            // Cursor moved to adjacent position
            if ((axis == EnumFacing.Axis.X && posNew.getX() == posFirst.getX()) ||
                (axis == EnumFacing.Axis.Z && posNew.getZ() == posFirst.getZ()) ||
                (axis == EnumFacing.Axis.Y && posNew.getY() == posFirst.getY()))
            {
                return true;
            }
        }

        return false;
    }

    private static boolean isNewPositionValidForFaceMode(BlockPos posNew, EnumFacing side)
    {
        return side == sideFirst && posNew.equals(posLast) == false;
    }

    private static boolean isNewPositionValidForColumnMode(BlockPos posNew)
    {
        if (posNew.equals(posLast) == false)
        {
            EnumFacing.Axis axis = sideFirst.getAxis();

            if ((axis == EnumFacing.Axis.X && posNew.getY() == posFirst.getY() && posNew.getZ() == posFirst.getZ()) ||
                (axis == EnumFacing.Axis.Y && posNew.getX() == posFirst.getX() && posNew.getZ() == posFirst.getZ()) ||
                (axis == EnumFacing.Axis.Z && posNew.getX() == posFirst.getX() && posNew.getY() == posFirst.getY()))
            {
                return true;
            }
        }

        return false;
    }

    /*
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
    */

    @Nullable
    private static EnumHand getHandWithItem(ItemStack stack, EntityPlayerSP player)
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

    public enum FastMode
    {
        PLANE,
        FACE,
        COLUMN;
    }
}
