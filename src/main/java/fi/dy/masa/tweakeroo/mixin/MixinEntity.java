package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

    @Shadow public float rotationPitch;
    @Shadow public float rotationYaw;
    @Shadow public float prevRotationYaw;
    @Shadow public float prevRotationPitch;
    @Shadow public double motionX;
    @Shadow public double motionY;
    @Shadow public double motionZ;

    private double forcedPitch;
    private double forcedYaw;

    @Redirect(method = "move",
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;onGround:Z", ordinal = 0)),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z", ordinal = 0))
    private boolean fakeSneaking(net.minecraft.entity.Entity entity)
    {
        if (FeatureToggle.TWEAK_FAKE_SNEAKING.getBooleanValue() && ((Object) this) instanceof net.minecraft.client.entity.EntityPlayerSP)
        {
            return true;
        }

        return ((net.minecraft.entity.Entity) (Object) this).isSneaking();
    }

    @Inject(method = "moveRelative",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/util/math/MathHelper;sin(F)F"), cancellable = true)
    private void moreAccurateMoveRelative(float strafe, float up, float forward, float friction, CallbackInfo ci)
    {
        if ((Object) this instanceof net.minecraft.client.entity.EntityPlayerSP)
        {
            if (CameraUtils.shouldPreventPlayerMovement())
            {
                ci.cancel();
            }
            else if (FeatureToggle.TWEAK_SNAP_AIM.getBooleanValue())
            {
                double xFactor = Math.sin(this.rotationYaw * Math.PI / 180D);
                double zFactor = Math.cos(this.rotationYaw * Math.PI / 180D);

                this.motionX += (double) (strafe * zFactor - forward * xFactor);
                this.motionY += (double) up;
                this.motionZ += (double) (forward * zFactor + strafe * xFactor);

                ci.cancel();
            }
        }
    }

    @Inject(method = "turn",
            at = @At(value = "FIELD",
                     target = "Lnet/minecraft/entity/Entity;prevRotationPitch:F", ordinal = 0))
    private void overrideYaw(float yawChange, float pitchChange, CallbackInfo ci)
    {
        if ((Object) this instanceof net.minecraft.client.entity.EntityPlayerSP)
        {
            if (CameraUtils.shouldPreventPlayerMovement())
            {
                this.rotationYaw = this.prevRotationYaw;
                this.rotationPitch = this.prevRotationPitch;

                CameraUtils.updateCameraRotations(yawChange, pitchChange);

                return;
            }

            if (FeatureToggle.TWEAK_AIM_LOCK.getBooleanValue())
            {
                this.rotationYaw = (float) this.forcedYaw;
                this.rotationPitch = (float) this.forcedPitch;
                return;
            }

            if (FeatureToggle.TWEAK_SNAP_AIM.getBooleanValue())
            {
                int pitchLimit = Configs.Generic.SNAP_AIM_PITCH_OVERSHOOT.getBooleanValue() ? 180 : 90;
                SnapAimMode mode = Configs.Generic.SNAP_AIM_MODE.getOptionListValue();
                boolean snapAimLock = FeatureToggle.TWEAK_SNAP_AIM_LOCK.getBooleanValue();

                // Not locked, or not snapping the yaw (ie. not in Yaw or Both modes)
                boolean updateYaw = snapAimLock == false || mode == SnapAimMode.PITCH;
                // Not locked, or not snapping the pitch (ie. not in Pitch or Both modes)
                boolean updatePitch = snapAimLock == false || mode == SnapAimMode.YAW;

                this.updateCustomPlayerRotations(yawChange, pitchChange, updateYaw, updatePitch, pitchLimit);

                this.rotationYaw = MiscUtils.getSnappedYaw(this.forcedYaw);
                this.rotationPitch = MiscUtils.getSnappedPitch(this.forcedPitch);
                return;
            }

            if (FeatureToggle.TWEAK_ELYTRA_CAMERA.getBooleanValue() && Hotkeys.ELYTRA_CAMERA.getKeybind().isKeybindHeld())
            {
                int pitchLimit = Configs.Generic.SNAP_AIM_PITCH_OVERSHOOT.getBooleanValue() ? 180 : 90;

                this.updateCustomPlayerRotations(yawChange, pitchChange, true, true, pitchLimit);

                CameraUtils.setCameraYaw((float) this.forcedYaw);
                CameraUtils.setCameraPitch((float) this.forcedPitch);

                this.rotationYaw = this.prevRotationYaw;
                this.rotationPitch = this.prevRotationPitch;

                return;
            }

            // Update the internal rotations while no locking features are enabled
            // They will then be used as the forced rotations when some of the locking features are activated.
            this.forcedYaw = this.rotationYaw;
            this.forcedPitch = this.rotationPitch;
        }
    }

    private void updateCustomPlayerRotations(float yawChange, float pitchChange, boolean updateYaw, boolean updatePitch, float pitchLimit)
    {
        if (updateYaw)
        {
            this.forcedYaw += (double) yawChange * 0.15D;
        }

        if (updatePitch)
        {
            this.forcedPitch = net.minecraft.util.math.MathHelper.clamp(this.forcedPitch - (double) pitchChange * 0.15D, -pitchLimit, pitchLimit);
        }
    }
}
