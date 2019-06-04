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
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import fi.dy.masa.tweakeroo.util.SnapAimMode;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Mixin(Entity.class)
public abstract class MixinEntity
{
    @Shadow
    public World world;

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
    private boolean fakeSneaking(Entity entity)
    {
        if (FeatureToggle.TWEAK_FAKE_SNEAKING.getBooleanValue() && ((Object) this) instanceof EntityPlayerSP)
        {
            return true;
        }

        return ((Entity) (Object) this).isSneaking();
    }

    @Inject(method = "moveRelative",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/util/math/MathHelper;sin(F)F"), cancellable = true)
    private void moreAccurateMoveRelative(float strafe, float up, float forward, float friction, CallbackInfo ci)
    {
        if ((Object) this instanceof EntityPlayerSP)
        {
            if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && FeatureToggle.TWEAK_FREE_CAMERA_MOTION.getBooleanValue())
            {
                CameraEntity camera = CameraEntity.getCamera();

                if (camera != null)
                {
                    this.motionY = 0;
                    ci.cancel();
                }
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

    @Inject(method = "rotateTowards", at = @At("HEAD"), cancellable = true)
    private void overrideYaw(double yawChange, double pitchChange, CallbackInfo ci)
    {
        if ((Object) this instanceof EntityPlayerSP)
        {
            if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && FeatureToggle.TWEAK_FREE_CAMERA_MOTION.getBooleanValue())
            {
                this.rotationYaw = this.prevRotationYaw;
                this.rotationPitch = this.prevRotationPitch;

                this.updateCustomRotations(yawChange, pitchChange, true, true, 90);

                CameraEntity camera = CameraEntity.getCamera();

                if (camera != null)
                {
                    camera.setRotations((float) this.forcedYaw, (float) this.forcedPitch);
                }

                ci.cancel();

                return;
            }

            if (FeatureToggle.TWEAK_AIM_LOCK.getBooleanValue() ||
                (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && FeatureToggle.TWEAK_FREE_CAMERA_MOTION.getBooleanValue()))
            {
                this.rotationYaw = (float) this.forcedYaw;
                this.rotationPitch = (float) this.forcedPitch;
                this.prevRotationYaw = this.rotationYaw;
                this.prevRotationPitch = this.rotationPitch;
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

                this.updateCustomRotations(yawChange, pitchChange, updateYaw, updatePitch, pitchLimit);

                this.rotationYaw = MiscUtils.getSnappedYaw(this.forcedYaw);
                this.rotationPitch = MiscUtils.getSnappedPitch(this.forcedPitch);
                this.prevRotationYaw = this.rotationYaw;
                this.prevRotationPitch = this.rotationPitch;
                ci.cancel();

                return;
            }

            if (FeatureToggle.TWEAK_ELYTRA_CAMERA.getBooleanValue() && Hotkeys.ELYTRA_CAMERA.getKeybind().isKeybindHeld())
            {
                int pitchLimit = Configs.Generic.SNAP_AIM_PITCH_OVERSHOOT.getBooleanValue() ? 180 : 90;

                this.updateCustomRotations(yawChange, pitchChange, true, true, pitchLimit);

                MiscUtils.setCameraYaw((float) this.forcedYaw);
                MiscUtils.setCameraPitch((float) this.forcedPitch);

                this.rotationYaw = this.prevRotationYaw;
                this.rotationPitch = this.prevRotationPitch;
                this.prevRotationYaw = this.rotationYaw;
                this.prevRotationPitch = this.rotationPitch;
                ci.cancel();

                return;
            }

            // Update the internal rotations while no locking features are enabled
            // They will then be used as the forced rotations when some of the locking features are activated.
            this.forcedYaw = this.rotationYaw;
            this.forcedPitch = this.rotationPitch;
        }
    }

    private void updateCustomRotations(double yawChange, double pitchChange, boolean updateYaw, boolean updatePitch, float pitchLimit)
    {
        if (updateYaw)
        {
            this.forcedYaw += yawChange * 0.15D;
        }

        if (updatePitch)
        {
            this.forcedPitch = MathHelper.clamp(this.forcedPitch + pitchChange * 0.15D, -pitchLimit, pitchLimit);
        }
    }
}
