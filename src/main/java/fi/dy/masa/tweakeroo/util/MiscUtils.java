package fi.dy.masa.tweakeroo.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.mixin.IMixinCommandBlockExecutor;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;

public class MiscUtils
{
    private static net.minecraft.text.Text[] previousSignText;
    private static String previousChatText = "";
    private static final Date DATE = new Date();
    private static double lastRealPitch;
    private static double lastRealYaw;
    private static double mouseSensitivity = -1.0F;
    private static boolean zoomActive;

    public static boolean isZoomActive()
    {
        return FeatureToggle.TWEAK_ZOOM.getBooleanValue() &&
               Hotkeys.ZOOM_ACTIVATE.getKeybind().isKeybindHeld();
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
            MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate();

            zoomActive = false;
        }
    }

    public static void setMouseSensitivityForZoom()
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        double fov = Configs.Generic.ZOOM_FOV.getDoubleValue();
        double origFov = mc.options.fov;

        if (fov < origFov)
        {
            // Only store it once
            if (mouseSensitivity <= 0.0 || mouseSensitivity > 1.0)
            {
                mouseSensitivity = mc.options.mouseSensitivity;
            }

            double min = 0.04;
            double sens = min + (0.5 - min) * (1.0 - (origFov - fov) / origFov);
            mc.options.mouseSensitivity = Math.min(mouseSensitivity, sens);
        }
    }

    public static void resetMouseSensitivityForZoom()
    {
        if (mouseSensitivity > 0.0)
        {
            MinecraftClient.getInstance().options.mouseSensitivity = mouseSensitivity;
            mouseSensitivity = -1.0;
        }
    }

    public static boolean getUpdateExec(CommandBlockBlockEntity te)
    {
        return ((IMixinCommandBlockExecutor) te.getCommandExecutor()).getUpdateLastExecution();
    }

    public static void setUpdateExec(CommandBlockBlockEntity te, boolean value)
    {
        ((IMixinCommandBlockExecutor) te.getCommandExecutor()).setUpdateLastExecution(value);
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

    public static void copyTextFromSign(SignBlockEntity te)
    {
        net.minecraft.text.Text[] text = ((ISignTextAccess) te).getText();
        final int size = text.length;
        previousSignText = new net.minecraft.text.Text[size];

        for (int i = 0; i < size; ++i)
        {
            previousSignText[i] = text[i];
        }
    }

    public static void applyPreviousTextToSign(SignBlockEntity te, @Nullable String[] guiLines)
    {
        if (previousSignText != null)
        {
            final int size = previousSignText.length;

            for (int i = 0; i < size; ++i)
            {
                net.minecraft.text.Text text = previousSignText[i];
                te.setTextOnRow(i, text);

                if (guiLines != null)
                {
                    guiLines[i] = text.asString();
                }
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
        entity.yaw = yaw;
        entity.pitch = pitch;
        entity.prevYaw = yaw;
        entity.prevPitch = pitch;

        if (entity instanceof LivingEntity)
        {
            LivingEntity living = (LivingEntity) entity;
            living.headYaw = yaw;
            living.prevHeadYaw = yaw;
        }
    }

    public static float getSnappedPitch(double realPitch)
    {
        if (Configs.Generic.SNAP_AIM_MODE.getOptionListValue() == SnapAimMode.YAW)
        {
            return (float) realPitch;
        }

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

    public static float getSnappedYaw(double realYaw)
    {
        if (Configs.Generic.SNAP_AIM_MODE.getOptionListValue() == SnapAimMode.PITCH)
        {
            return (float) realYaw;
        }

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

    public static double calculateSnappedAngle(double realRotation, double step)
    {
        double offsetRealRotation = MathHelper.floorMod(realRotation, 360.0D) + (step / 2.0);
        return MathHelper.floorMod(((int) (offsetRealRotation / step)) * step, 360.0D);
    }
}
