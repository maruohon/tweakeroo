package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;

import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.util.CameraUtils;
import fi.dy.masa.tweakeroo.util.SnapAimMode;
import fi.dy.masa.tweakeroo.util.SnapAimUtils;

@Mixin(net.minecraft.entity.Entity.class)
public abstract class MixinEntity
{
    @Shadow private float yaw;
    @Shadow private float pitch;
    @Shadow public float prevYaw;
    @Shadow public float prevPitch;

    @Unique private double lastFreePitch;
    @Unique private double lastFreeYaw;
    @Unique private double cameraPitch;
    @Unique private double cameraYaw;

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
    private void moreAccurateMoveRelative(float speedIn, net.minecraft.util.math.Vec3d motion, CallbackInfo ci)
    {
        if ((Object) this instanceof ClientPlayerEntity &&
            (FeatureToggle.TWEAK_SNAP_AIM.getBooleanValue() ||
             FeatureToggle.TWEAK_AIM_LOCK.getBooleanValue()))
        {
            SnapAimUtils.onUpdateVelocity((Entity) (Object) this, this.yaw, speedIn, motion, ci);
        }
    }

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void overrideYaw(double yawChange, double pitchChange, CallbackInfo ci)
    {
        if ((Object) this instanceof ClientPlayerEntity)
        {
            if (CameraUtils.shouldPreventPlayerMovement())
            {
                CameraUtils.updateCameraRotations((float) yawChange, (float) pitchChange);
            }

            if (FeatureToggle.TWEAK_ELYTRA_CAMERA.getBooleanValue() && Hotkeys.ELYTRA_CAMERA.getKeybind().isKeybindHeld())
            {
                int pitchLimit = Configs.Generic.SNAP_AIM_PITCH_OVERSHOOT.getBooleanValue() ? 180 : 90;

                this.cameraYaw += yawChange * 0.15D;
                this.cameraPitch = net.minecraft.util.math.MathHelper.clamp(this.cameraPitch + pitchChange * 0.15D, -pitchLimit, pitchLimit);

                CameraUtils.setCameraYaw((float) this.cameraYaw);
                CameraUtils.setCameraPitch((float) this.cameraPitch);

                this.yaw = this.prevYaw;
                this.pitch = this.prevPitch;
                ci.cancel();

                return;
            }

            if (FeatureToggle.TWEAK_AIM_LOCK.getBooleanValue())
            {
                if (FeatureToggle.TWEAK_SNAP_AIM.getBooleanValue())
                {
                    this.yaw = SnapAimUtils.getSnappedYaw(this.lastFreeYaw);
                    this.pitch = SnapAimUtils.getSnappedPitch(this.lastFreePitch);
                }
                else
                {
                    this.yaw = (float) this.lastFreeYaw;
                    this.pitch = (float) this.lastFreePitch;
                }

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

                this.yaw = SnapAimUtils.getSnappedYaw(this.lastFreeYaw);
                this.pitch = SnapAimUtils.getSnappedPitch(this.lastFreePitch);
                this.prevYaw = this.yaw;
                this.prevPitch = this.pitch;
                ci.cancel();

                return;
            }

            if (CameraUtils.shouldPreventPlayerMovement())
            {
                ci.cancel();
                return;
            }

            // Update the internal rotations while no locking features are enabled
            // They will then be used as the forced rotations when some of the locking features are activated.
            this.lastFreeYaw = this.yaw;
            this.lastFreePitch = this.pitch;
            this.cameraYaw = this.yaw;
            this.cameraPitch = this.pitch;
        }
    }

    @Unique
    private void updateCustomPlayerRotations(double yawChange, double pitchChange, boolean updateYaw, boolean updatePitch, float pitchLimit)
    {
        if (updateYaw)
        {
            this.lastFreeYaw += yawChange * 0.15D;
        }

        if (updatePitch)
        {
            this.lastFreePitch = net.minecraft.util.math.MathHelper.clamp(this.lastFreePitch + pitchChange * 0.15D, -pitchLimit, pitchLimit);
        }
    }
}
