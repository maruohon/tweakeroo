package fi.dy.masa.tweakeroo.util;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;

public class SnapAimUtils
{
    private static double lastRealPitch;
    private static double lastRealYaw;
    private static double lastSnapYaw;
    private static double lastSnapPitch;

    public static double getLastRealPitch()
    {
        return lastRealPitch;
    }

    public static double getLastRealYaw()
    {
        return lastRealYaw;
    }

    /**
     * This does the exact same thing as the vanilla code, but using doubles instead of floats for the sin and cos calls.
     */
    public static void onUpdateVelocity(Entity entity, float yawIn, float speedIn, Vec3d motion, CallbackInfo ci)
    {
        double speed = motion.lengthSquared();

        if (speed >= 1.0E-7D)
        {
            motion = (speed > 1.0D ? motion.normalize() : motion).multiply(speedIn);
            double xFactor = Math.sin(yawIn * Math.PI / 180D);
            double zFactor = Math.cos(yawIn * Math.PI / 180D);
            Vec3d change = new Vec3d(motion.x * zFactor - motion.z * xFactor, motion.y, motion.z * zFactor + motion.x * xFactor);

            entity.setVelocity(entity.getVelocity().add(change));
        }

        ci.cancel();
    }

    public static float getSnappedPitch(double realPitch)
    {
        if (Configs.Generic.SNAP_AIM_MODE.getOptionListValue() != SnapAimMode.YAW)
        {
            if (FeatureToggle.TWEAK_SNAP_AIM_LOCK.getBooleanValue())
            {
                return (float) lastSnapPitch;
            }

            if (lastRealPitch != realPitch)
            {
                lastRealPitch = realPitch;
                RenderUtils.notifyRotationChanged();
            }

            double step = Configs.Generic.SNAP_AIM_PITCH_STEP.getDoubleValue();
            int limit = Configs.Generic.SNAP_AIM_PITCH_OVERSHOOT.getBooleanValue() ? 180 : 90;
            double newSnapPitch;

            if (realPitch < 0)
            {
                newSnapPitch = -calculateSnappedAngle(-realPitch, step);
            }
            else
            {
                newSnapPitch = calculateSnappedAngle(realPitch, step);
            }

            double offset = Math.abs(MathHelper.wrapDegrees((float) (newSnapPitch - realPitch)));
            //if (GuiBase.isCtrlDown()) System.out.printf("real: %.2f, snapped: %.2f, offset: %.2f\n", realPitch, newSnapPitch, offset);

            if (Configs.Generic.SNAP_AIM_ONLY_CLOSE_TO_ANGLE.getBooleanValue() == false ||
                offset <= Configs.Generic.SNAP_AIM_THRESHOLD_PITCH.getDoubleValue())
            {
                newSnapPitch = MathHelper.clamp(MathHelper.wrapDegrees(newSnapPitch), -limit, limit);

                if (lastSnapPitch != newSnapPitch)
                {
                    String g = GuiBase.TXT_GREEN;
                    String r = GuiBase.TXT_RST;
                    String str = String.format("%s%s%s (step %s%s%s)", g, MathHelper.wrapDegrees(newSnapPitch), r, g, step, r);

                    InfoUtils.printActionbarMessage("tweakeroo.message.snapped_to_pitch", str);

                    lastSnapPitch = newSnapPitch;
                }

                return MathHelper.wrapDegrees((float) newSnapPitch);
            }
        }

        // This causes the snap message to also get shown when re-snapping to the same snap angle, when using the threshold
        lastSnapPitch = realPitch;

        return (float) realPitch;
    }

    public static float getSnappedYaw(double realYaw)
    {
        if (Configs.Generic.SNAP_AIM_MODE.getOptionListValue() != SnapAimMode.PITCH)
        {
            if (FeatureToggle.TWEAK_SNAP_AIM_LOCK.getBooleanValue())
            {
                return (float) lastSnapYaw;
            }

            if (lastRealYaw != realYaw)
            {
                lastRealYaw = realYaw;
                RenderUtils.notifyRotationChanged();
            }

            double step = Configs.Generic.SNAP_AIM_YAW_STEP.getDoubleValue();
            double newSnapYaw = calculateSnappedAngle(realYaw, step);

            if (Configs.Generic.SNAP_AIM_ONLY_CLOSE_TO_ANGLE.getBooleanValue() == false ||
                Math.abs(MathHelper.wrapDegrees((float) (newSnapYaw - realYaw))) <= Configs.Generic.SNAP_AIM_THRESHOLD_YAW.getDoubleValue())
            {
                if (lastSnapYaw != newSnapYaw)
                {
                    String g = GuiBase.TXT_GREEN;
                    String r = GuiBase.TXT_RST;
                    String str = String.format("%s%s%s (step %s%s%s)", g, MathHelper.wrapDegrees(newSnapYaw), r, g, step, r);

                    InfoUtils.printActionbarMessage("tweakeroo.message.snapped_to_yaw", str);

                    lastSnapYaw = newSnapYaw;
                }

                return MathHelper.wrapDegrees((float) newSnapYaw);
            }
        }

        // This causes the snap message to also get shown when re-snapping to the same snap angle, when using the threshold
        lastSnapYaw = realYaw;

        return (float) realYaw;
    }

    public static double calculateSnappedAngle(double realRotation, double step)
    {
        double offsetRealRotation = MathHelper.floorMod(realRotation, 360.0D) + (step / 2.0);
        return MathHelper.floorMod(((int) (offsetRealRotation / step)) * step, 360.0D);
    }
}
