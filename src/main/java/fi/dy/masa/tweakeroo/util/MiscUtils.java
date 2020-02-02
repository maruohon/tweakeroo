package fi.dy.masa.tweakeroo.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.mixin.IMixinCommandBlockBaseLogic;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;

public class MiscUtils
{
    private static net.minecraft.util.text.ITextComponent[] previousSignText;
    private static String previousChatText = "";
    private static final Date DATE = new Date();
    private static double lastRealPitch;
    private static double lastRealYaw;

    public static void applyDebugPieChartScale()
    {
        double scale = Configs.Generic.DEBUG_PIE_CHART_SCALE.getDoubleValue();

        if (scale > 0 && scale != 1.0)
        {
            Minecraft mc = Minecraft.getMinecraft();
            int origX = mc.displayWidth - 170;
            int origY = mc.displayHeight - 320;
            double width = 320.0;
            double height = 400.0;
            double xOff = (1.0 - scale) * (origX + width / 2.0);
            double yOff = (1.0 - scale) * (origY + height / 2.0);

            GlStateManager.translate(xOff, yOff, 0.0);
            GlStateManager.scale(scale, scale, 1);
        }
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
        SimpleDateFormat sdf = new SimpleDateFormat(Configs.Generic.CHAT_TIME_FORMAT.getStringValue());
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

        for (int i = 0; i < size; ++i)
        {
            previousSignText[i] = te.signText[i];
        }
    }

    public static void applyPreviousTextToSign(TileEntitySign te)
    {
        if (previousSignText != null)
        {
            int size = Math.min(te.signText.length, previousSignText.length);

            for (int i = 0; i < size; ++i)
            {
                te.signText[i] = previousSignText[i];
            }
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
        entity.rotationYaw = yaw;
        entity.rotationPitch = pitch;
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
        if (Configs.Generic.SNAP_AIM_MODE.getOptionListValue() != SnapAimMode.YAW)
        {
            if (lastRealPitch != realPitch)
            {
                lastRealPitch = realPitch;
                RenderUtils.notifyRotationChanged();
            }

            if (FeatureToggle.TWEAK_SNAP_AIM_LOCK.getBooleanValue())
            {
                return (float) Configs.Internal.SNAP_AIM_LAST_PITCH.getDoubleValue();
            }

            double step = Configs.Generic.SNAP_AIM_PITCH_STEP.getDoubleValue();
            int limit = Configs.Generic.SNAP_AIM_PITCH_OVERSHOOT.getBooleanValue() ? 180 : 90;
            double snappedPitch;

            //realPitch = MathHelper.clamp(realPitch, -limit, limit);

            if (realPitch < 0)
            {
                snappedPitch = -calculateSnappedAngle(-realPitch, step);
            }
            else
            {
                snappedPitch = calculateSnappedAngle(realPitch, step);
            }

            double offset = Math.abs(MathHelper.wrapDegrees((float) (snappedPitch - realPitch)));
            if (GuiBase.isCtrlDown()) System.out.printf("real: %.2f, snapped: %.2f, offset: %.2f\n", realPitch, snappedPitch, offset);

            if (Configs.Generic.SNAP_AIM_ONLY_CLOSE_TO_ANGLE.getBooleanValue() == false ||
                offset <= Configs.Generic.SNAP_AIM_THRESHOLD_PITCH.getDoubleValue())
            {
                snappedPitch = MathHelper.clamp(MathHelper.wrapDegrees(snappedPitch), -limit, limit);

                if (Configs.Internal.SNAP_AIM_LAST_PITCH.getDoubleValue() != snappedPitch)
                {
                    String g = GuiBase.TXT_GREEN;
                    String r = GuiBase.TXT_RST;
                    String str = String.format("%s%s%s (step %s%s%s)", g, String.valueOf(MathHelper.wrapDegrees(snappedPitch)), r, g, String.valueOf(step), r);

                    InfoUtils.printActionbarMessage("tweakeroo.message.snapped_to_pitch", str);

                    Configs.Internal.SNAP_AIM_LAST_PITCH.setDoubleValue(snappedPitch);
                }

                return MathHelper.wrapDegrees((float) snappedPitch);
            }
        }

        // This causes the snap message to also get shown when re-snapping to the same snap angle, when using the threshold
        Configs.Internal.SNAP_AIM_LAST_PITCH.setDoubleValue(realPitch);

        return (float) realPitch;
    }

    public static float getSnappedYaw(double realYaw)
    {
        if (Configs.Generic.SNAP_AIM_MODE.getOptionListValue() != SnapAimMode.PITCH)
        {
            if (lastRealYaw != realYaw)
            {
                lastRealYaw = realYaw;
                RenderUtils.notifyRotationChanged();
            }

            if (FeatureToggle.TWEAK_SNAP_AIM_LOCK.getBooleanValue())
            {
                return (float) Configs.Internal.SNAP_AIM_LAST_YAW.getDoubleValue();
            }

            double step = Configs.Generic.SNAP_AIM_YAW_STEP.getDoubleValue();
            double snappedYaw = calculateSnappedAngle(realYaw, step);

            if (Configs.Generic.SNAP_AIM_ONLY_CLOSE_TO_ANGLE.getBooleanValue() == false ||
                Math.abs(MathHelper.wrapDegrees((float) (snappedYaw - realYaw))) <= Configs.Generic.SNAP_AIM_THRESHOLD_YAW.getDoubleValue())
            {
                if (Configs.Internal.SNAP_AIM_LAST_YAW.getDoubleValue() != snappedYaw)
                {
                    String g = GuiBase.TXT_GREEN;
                    String r = GuiBase.TXT_RST;
                    String str = String.format("%s%s%s (step %s%s%s)", g, String.valueOf(MathHelper.wrapDegrees(snappedYaw)), r, g, String.valueOf(step), r);

                    InfoUtils.printActionbarMessage("tweakeroo.message.snapped_to_yaw", str);

                    Configs.Internal.SNAP_AIM_LAST_YAW.setDoubleValue(snappedYaw);
                }

                return MathHelper.wrapDegrees((float) snappedYaw);
            }
        }

        // This causes the snap message to also get shown when re-snapping to the same snap angle, when using the threshold
        Configs.Internal.SNAP_AIM_LAST_YAW.setDoubleValue(realYaw);

        return (float) realYaw;
    }

    public static double calculateSnappedAngle(double realRotation, double step)
    {
        double offsetRealRotation = MathHelper.positiveModulo(realRotation, 360.0D) + (step / 2.0);
        return MathHelper.positiveModulo(((int) (offsetRealRotation / step)) * step, 360.0D);
    }
}
