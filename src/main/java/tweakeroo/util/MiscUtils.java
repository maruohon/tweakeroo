package tweakeroo.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import malilib.action.ActionContext;
import malilib.gui.util.GuiUtils;
import malilib.input.ActionResult;
import malilib.overlay.message.MessageUtils;
import malilib.util.MathUtils;
import malilib.util.game.RayTraceUtils;
import malilib.util.game.RayTraceUtils.IRayPositionHandler;
import malilib.util.game.wrap.EntityWrap;
import malilib.util.game.wrap.GameUtils;
import malilib.util.game.wrap.ItemWrap;
import malilib.util.inventory.InventoryUtils;
import tweakeroo.config.Configs;
import tweakeroo.config.FeatureToggle;
import tweakeroo.config.Hotkeys;
import tweakeroo.mixin.IMixinCommandBlockBaseLogic;
import tweakeroo.renderer.RenderUtils;
import tweakeroo.tweaks.PlacementTweaks;

public class MiscUtils
{
    private static final HashMap<String, SimpleDateFormat> DATE_FORMATS = new HashMap<>();

    public static boolean skipWorldRendering;
    public static int renderCountItems;
    public static int renderCountXPOrbs;

    private static net.minecraft.util.text.ITextComponent[] previousSignText;
    private static String previousChatText = "";
    private static final Date DATE = new Date();
    private static double lastRealPitch;
    private static double lastRealYaw;
    private static float mouseSensitivity = -1.0F;
    private static boolean zoomActive;

    public static void onClientTick()
    {
        PlacementTweaks.onTick();
        // Reset the counters after rendering each frame
        renderCountItems = 0;
        renderCountXPOrbs = 0;
    }

    public static boolean isZoomActive()
    {
        return FeatureToggle.TWEAK_ZOOM.getBooleanValue() &&
               Hotkeys.ZOOM_ACTIVATE.getKeyBind().isKeyBindHeld();
    }

    public static void checkZoomStatus()
    {
        if (zoomActive && isZoomActive() == false)
        {
            onZoomDeactivated();
        }
    }

    public static void onZoomActivated()
    {
        if (Configs.Generic.ZOOM_ADJUST_MOUSE_SENSITIVITY.getBooleanValue())
        {
            setMouseSensitivityForZoom();
        }

        zoomActive = true;
    }

    public static void onZoomDeactivated()
    {
        if (zoomActive)
        {
            resetMouseSensitivityForZoom();

            // Refresh the rendered chunks when exiting zoom mode
            GameUtils.getClient().renderGlobal.setDisplayListEntitiesDirty();

            zoomActive = false;
        }
    }

    public static void setMouseSensitivityForZoom()
    {
        Minecraft mc = GameUtils.getClient();

        float fov = Configs.Generic.ZOOM_FOV.getFloatValue();
        float origFov = mc.gameSettings.fovSetting;

        if (fov < origFov)
        {
            // Only store it once
            if (mouseSensitivity <= 0.0F || mouseSensitivity > 1.0F)
            {
                mouseSensitivity = mc.gameSettings.mouseSensitivity;
            }

            float min = 0.04F;
            float sens = min + (0.5F - min) * (1 - (origFov - fov) / origFov);
            mc.gameSettings.mouseSensitivity = Math.min(mouseSensitivity, sens);
        }
    }

    public static void resetMouseSensitivityForZoom()
    {
        if (mouseSensitivity > 0.0F)
        {
            GameUtils.getClient().gameSettings.mouseSensitivity = mouseSensitivity;
            mouseSensitivity = -1.0F;
        }
    }

    public static void applyDebugPieChartScale()
    {
        double scale = Configs.Generic.DEBUG_PIE_CHART_SCALE.getDoubleValue();

        if (scale > 0 && scale != 1.0)
        {
            int origX = GuiUtils.getDisplayWidth() - 170;
            int origY = GuiUtils.getDisplayHeight() - 320;
            double width = 320.0;
            double height = 400.0;
            double xOff = (1.0 - scale) * (origX + width / 2.0);
            double yOff = (1.0 - scale) * (origY + height / 2.0);

            GlStateManager.translate(xOff, yOff, 0.0);
            GlStateManager.scale(scale, scale, 1);
        }
    }

    public static ActionResult antiGhostBlock(ActionContext ctx)
    {
        Minecraft mc = GameUtils.getClient();
        EntityPlayer player = ctx.getPlayer();
        World worldIn = ctx.getWorld();

        if (player == null || worldIn == null)
        {
            return ActionResult.PASS;
        }

        double range = mc.playerController.getBlockReachDistance();
        malilib.util.position.Vec3d eyesPos = EntityWrap.getEntityEyePos(player);
        malilib.util.position.Vec3d rangedLook = EntityWrap.getScaledLookVector(player, range);
        malilib.util.position.Vec3d lookEndPos = eyesPos.add(rangedLook);
        int swappedSlotMain = -1;
        int swappedSlotOff = -1;
        int hotbarSlot = player.inventory.currentItem;
        Container container = player.openContainer;

        // Move away the items in the player's hands
        if (ItemWrap.notEmpty(player.getHeldItemMainhand()))
        {
            swappedSlotMain = InventoryUtils.findEmptySlotInPlayerInventory(container, false, false);

            if (swappedSlotMain != -1)
            {
                InventoryUtils.swapSlots(container, swappedSlotMain, hotbarSlot);
            }
        }

        if (ItemWrap.notEmpty(player.getHeldItemOffhand()))
        {
            swappedSlotOff = InventoryUtils.findEmptySlotInPlayerInventory(container, false, false);

            if (swappedSlotOff != -1)
            {
                InventoryUtils.swapSlots(container, 45, hotbarSlot);
                InventoryUtils.swapSlots(container, swappedSlotOff, hotbarSlot);
            }
        }

        // Use a custom "collision checker" that just right clicks on all air blocks, and aborts when hitting an existing non-air block
        IRayPositionHandler handler = (data, world, ignore) -> {
            IBlockState state = world.getBlockState(data.mutablePos);

            if (state.getMaterial() == Material.AIR)
            {
                Vec3d vec = new Vec3d(data.blockX + 0.5, data.blockY + 1.0, data.blockZ + 0.5);
                mc.playerController.processRightClickBlock(mc.player, mc.world, data.mutablePos.toImmutable(), EnumFacing.UP, vec, EnumHand.MAIN_HAND);
            }
            else
            {
                return true;
            }

            return false;
        };

        RayTraceUtils.rayTraceBlocks(worldIn, eyesPos, lookEndPos,
                handler, RayTraceUtils.RayTraceFluidHandling.NONE,
                RayTraceUtils.BLOCK_FILTER_NON_AIR, false, false, null, 16);

        // Restore the items the player was holding initially
        if (swappedSlotOff != -1)
        {
            InventoryUtils.swapSlots(container, swappedSlotOff, hotbarSlot);
            InventoryUtils.swapSlots(container, 45, hotbarSlot);
        }

        if (swappedSlotMain != -1)
        {
            InventoryUtils.swapSlots(container, swappedSlotMain, hotbarSlot);
        }

        return ActionResult.SUCCESS;
    }

    public static void addCustomBlockBreakingParticles(ParticleManager manager, World world, Random rand, BlockPos pos, IBlockState state)
    {
        if (state.getMaterial() != Material.AIR)
        {
            state = state.getActualState(world, pos);
            int limit = Configs.Generic.BLOCK_BREAKING_PARTICLE_LIMIT.getIntegerValue();

            for (int i = 0; i < limit; ++i)
            {
                double x = ((double) pos.getX() + rand.nextDouble());
                double y = ((double) pos.getY() + rand.nextDouble());
                double z = ((double) pos.getZ() + rand.nextDouble());
                double speedX = (0.5 - rand.nextDouble());
                double speedY = (0.5 - rand.nextDouble());
                double speedZ = (0.5 - rand.nextDouble());

                manager.addEffect((new ParticleDiggingExt(world, x, y, z, speedX, speedY, speedZ, state))
                        .setBlockPos(pos)
                        .multiplyVelocity(Configs.Generic.BLOCK_BREAKING_PARTICLE_SPEED.getFloatValue())
                        .multipleParticleScaleBy(Configs.Generic.BLOCK_BREAKING_PARTICLE_SCALE.getFloatValue()));
            }
        }
    }

    public static boolean getUpdateExec(TileEntityCommandBlock te)
    {
        return ((IMixinCommandBlockBaseLogic) te.getCommandBlockLogic()).getUpdateLastExecution();
    }

    public static String getChatTimestamp()
    {
        SimpleDateFormat sdf = new SimpleDateFormat(Configs.Generic.CHAT_TIME_FORMAT.getValue());
        DATE.setTime(System.currentTimeMillis());
        return sdf.format(DATE);
    }

    public static void setLastChatText(String text)
    {
        previousChatText = text;
    }

    public static String getLastChatText()
    {
        return previousChatText;
    }

    public static int getChatBackgroundColor(int colorOrig)
    {
        int newColor = Configs.Generic.CHAT_BACKGROUND_COLOR.getIntegerValue();
        return (newColor & 0x00FFFFFF) | ((int) (((newColor >>> 24) / 255.0) * ((colorOrig >>> 24) / 255.0) / 0.5 * 255) << 24);
    }

    public static void doPlayerOnFireRenderModifications()
    {
        float scale = Configs.Generic.PLAYER_ON_FIRE_SCALE.getFloatValue();

        if (scale > 1)
        {
            GlStateManager.translate(0, scale / 8, 0);
        }

        GlStateManager.scale(scale, scale, 1);
    }

    public static void copyTextFromSign(TileEntitySign te)
    {
        int size = te.signText.length;
        previousSignText = new net.minecraft.util.text.ITextComponent[size];
        System.arraycopy(te.signText, 0, previousSignText, 0, size);
    }

    public static void applyPreviousTextToSign(TileEntitySign te)
    {
        if (previousSignText != null)
        {
            int size = Math.min(te.signText.length, previousSignText.length);
            System.arraycopy(previousSignText, 0, te.signText, 0, size);
        }
    }

    public static double getLastRealPitch()
    {
        return lastRealPitch;
    }

    public static double getLastRealYaw()
    {
        return lastRealYaw;
    }

    public static void setEntityRotations(Entity entity, float yaw, float pitch)
    {
        EntityWrap.setYaw(entity, yaw);
        EntityWrap.setPitch(entity, pitch);
        entity.prevRotationYaw = yaw;
        entity.prevRotationPitch = pitch;

        if (entity instanceof EntityLivingBase)
        {
            EntityLivingBase living = (EntityLivingBase) entity;
            living.rotationYawHead = yaw;
            living.prevRotationYawHead = yaw;
        }
    }

    public static float getSnappedPitch(double realPitch)
    {
        if (Configs.Generic.SNAP_AIM_MODE.getValue() != SnapAimMode.YAW)
        {
            if (lastRealPitch != realPitch)
            {
                lastRealPitch = realPitch;
                RenderUtils.notifyRotationChanged();
            }

            if (FeatureToggle.TWEAK_SNAP_AIM_LOCK.getBooleanValue())
            {
                return Configs.Internal.SNAP_AIM_LAST_PITCH.getFloatValue();
            }

            double step = Configs.Generic.SNAP_AIM_PITCH_STEP.getDoubleValue();
            int limit = Configs.Generic.SNAP_AIM_PITCH_OVERSHOOT.getBooleanValue() ? 180 : 90;
            double snappedPitch;

            //realPitch = MathUtils.clamp(realPitch, -limit, limit);

            if (realPitch < 0)
            {
                snappedPitch = -calculateSnappedAngle(-realPitch, step);
            }
            else
            {
                snappedPitch = calculateSnappedAngle(realPitch, step);
            }

            double offset = Math.abs(MathUtils.wrapDegrees((float) (snappedPitch - realPitch)));

            if (Configs.Generic.SNAP_AIM_ONLY_CLOSE_TO_ANGLE.getBooleanValue() == false ||
                offset <= Configs.Generic.SNAP_AIM_THRESHOLD_PITCH.getDoubleValue())
            {
                snappedPitch = MathUtils.clamp(MathUtils.wrapDegrees(snappedPitch), -limit, limit);
                float wrappedPitch = MathUtils.wrapDegrees((float) snappedPitch);

                if (Configs.Internal.SNAP_AIM_LAST_PITCH.getDoubleValue() != snappedPitch)
                {
                    Configs.Internal.SNAP_AIM_LAST_PITCH.setValue(snappedPitch);
                    MessageUtils.printCustomActionbarMessage("tweakeroo.message.snapped_to_pitch", wrappedPitch, step);
                }

                return wrappedPitch;
            }
        }

        // This causes the snap message to also get shown when re-snapping to the same snap angle, when using the threshold
        Configs.Internal.SNAP_AIM_LAST_PITCH.setValue(realPitch);

        return (float) realPitch;
    }

    public static float getSnappedYaw(double realYaw)
    {
        if (Configs.Generic.SNAP_AIM_MODE.getValue() != SnapAimMode.PITCH)
        {
            if (lastRealYaw != realYaw)
            {
                lastRealYaw = realYaw;
                RenderUtils.notifyRotationChanged();
            }

            if (FeatureToggle.TWEAK_SNAP_AIM_LOCK.getBooleanValue())
            {
                return Configs.Internal.SNAP_AIM_LAST_YAW.getFloatValue();
            }

            double step = Configs.Generic.SNAP_AIM_YAW_STEP.getDoubleValue();
            double snappedYaw = calculateSnappedAngle(realYaw, step);

            if (Configs.Generic.SNAP_AIM_ONLY_CLOSE_TO_ANGLE.getBooleanValue() == false ||
                Math.abs(MathUtils.wrapDegrees((float) (snappedYaw - realYaw))) <= Configs.Generic.SNAP_AIM_THRESHOLD_YAW.getDoubleValue())
            {
                float wrappedYaw = MathUtils.wrapDegrees((float) snappedYaw);

                if (Configs.Internal.SNAP_AIM_LAST_YAW.getDoubleValue() != snappedYaw)
                {
                    Configs.Internal.SNAP_AIM_LAST_YAW.setValue(snappedYaw);
                    MessageUtils.printCustomActionbarMessage("tweakeroo.message.snapped_to_yaw", wrappedYaw, step);
                }

                return wrappedYaw;
            }
        }

        // This causes the snap message to also get shown when re-snapping to the same snap angle, when using the threshold
        Configs.Internal.SNAP_AIM_LAST_YAW.setValue(realYaw);

        return (float) realYaw;
    }

    public static double calculateSnappedAngle(double realRotation, double step)
    {
        double offsetRealRotation = MathUtils.positiveModulo(realRotation, 360.0D) + (step / 2.0);
        return MathUtils.positiveModulo(((int) (offsetRealRotation / step)) * step, 360.0D);
    }

    public static SimpleDateFormat getDateFormatFor(String format)
    {
        return DATE_FORMATS.computeIfAbsent(format, SimpleDateFormat::new);
    }
}
