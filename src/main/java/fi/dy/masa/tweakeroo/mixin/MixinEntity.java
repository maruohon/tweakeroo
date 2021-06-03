package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.util.CameraUtils;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import fi.dy.masa.tweakeroo.util.SnapAimMode;

@Mixin(net.minecraft.entity.Entity.class)
public abstract class MixinEntity
{
    @Shadow public net.minecraft.world.World world;

    @Shadow public float yaw;
    @Shadow public float pitch;
    @Shadow public float prevYaw;
    @Shadow public float prevPitch;

    private double forcedPitch;
    private double forcedYaw;

    @Shadow public abstract net.minecraft.util.math.Vec3d getVelocity();
    @Shadow public abstract void setVelocity(net.minecraft.util.math.Vec3d velocity);

    @Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
    private void overrideIsInvisibleToPlayer(net.minecraft.entity.player.PlayerEntity player, CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_RENDER_INVISIBLE_ENTITIES.getBooleanValue())
        {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateVelocity", at = @At("HEAD"), cancellable = true)
    private void moreAccurateMoveRelative(float float_1, net.minecraft.util.math.Vec3d motion, CallbackInfo ci)
    {
        if ((Object) this instanceof net.minecraft.client.network.ClientPlayerEntity)
        {
            if (FeatureToggle.TWEAK_SNAP_AIM.getBooleanValue())
            {
                double speed = motion.lengthSquared();

                if (speed >= 1.0E-7D)
                {
                   motion = (speed > 1.0D ? motion.normalize() : motion).multiply((double) float_1);
                   double xFactor = Math.sin(this.yaw * Math.PI / 180D);
                   double zFactor = Math.cos(this.yaw * Math.PI / 180D);
                   net.minecraft.util.math.Vec3d change = new net.minecraft.util.math.Vec3d(motion.x * zFactor - motion.z * xFactor, motion.y, motion.z * zFactor + motion.x * xFactor);

                   this.setVelocity(this.getVelocity().add(change));
                }

                ci.cancel();
            }
        }
    }

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void overrideYaw(double yawChange, double pitchChange, CallbackInfo ci)
    {
        if ((Object) this instanceof net.minecraft.client.network.ClientPlayerEntity)
        {
            if (CameraUtils.shouldPreventPlayerMovement())
            {
                CameraUtils.updateCameraRotations((float) yawChange, (float) pitchChange);
                ci.cancel();

                return;
            }

            if (FeatureToggle.TWEAK_AIM_LOCK.getBooleanValue())
            {
                this.yaw = (float) this.forcedYaw;
                this.pitch = (float) this.forcedPitch;
                this.prevYaw = this.yaw;
                this.prevPitch = this.pitch;
                ci.cancel();

                return;
            }

            if (FeatureToggle.TWEAK_SNAP_AIM.getBooleanValue())
            {
                int pitchLimit = Configs.Generic.SNAP_AIM_PITCH_OVERSHOOT.getBooleanValue() ? 180 : 90;
                SnapAimMode mode = (SnapAimMode) Configs.Generic.SNAP_AIM_MODE.getOptionListValue();
                boolean snapAimLock = FeatureToggle.TWEAK_SNAP_AIM_LOCK.getBooleanValue();

                // Not locked, or not snapping the yaw (ie. not in Yaw or Both modes)
                boolean updateYaw = snapAimLock == false || mode == SnapAimMode.PITCH;
                // Not locked, or not snapping the pitch (ie. not in Pitch or Both modes)
                boolean updatePitch = snapAimLock == false || mode == SnapAimMode.YAW;

                this.updateCustomPlayerRotations(yawChange, pitchChange, updateYaw, updatePitch, pitchLimit);

                this.yaw = MiscUtils.getSnappedYaw(this.forcedYaw);
                this.pitch = MiscUtils.getSnappedPitch(this.forcedPitch);
                this.prevYaw = this.yaw;
                this.prevPitch = this.pitch;
                ci.cancel();

                return;
            }

            if (FeatureToggle.TWEAK_ELYTRA_CAMERA.getBooleanValue() && Hotkeys.ELYTRA_CAMERA.getKeybind().isKeybindHeld())
            {
                int pitchLimit = Configs.Generic.SNAP_AIM_PITCH_OVERSHOOT.getBooleanValue() ? 180 : 90;

                this.updateCustomPlayerRotations(yawChange, pitchChange, true, true, pitchLimit);

                CameraUtils.setCameraYaw((float) this.forcedYaw);
                CameraUtils.setCameraPitch((float) this.forcedPitch);

                this.yaw = this.prevYaw;
                this.pitch = this.prevPitch;
                this.prevYaw = this.yaw;
                this.prevPitch = this.pitch;
                ci.cancel();

                return;
            }

            // Update the internal rotations while no locking features are enabled
            // They will then be used as the forced rotations when some of the locking features are activated.
            this.forcedYaw = this.yaw;
            this.forcedPitch = this.pitch;
        }
    }

    private void updateCustomPlayerRotations(double yawChange, double pitchChange, boolean updateYaw, boolean updatePitch, float pitchLimit)
    {
        if (updateYaw)
        {
            this.forcedYaw += yawChange * 0.15D;
        }

        if (updatePitch)
        {
            this.forcedPitch = net.minecraft.util.math.MathHelper.clamp(this.forcedPitch + pitchChange * 0.15D, -pitchLimit, pitchLimit);
        }
    }
}
