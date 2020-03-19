package fi.dy.masa.tweakeroo.mixin;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import fi.dy.masa.tweakeroo.config.Callbacks;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.MiscUtils;

@Mixin(value = GameRenderer.class, priority = 1001)
public abstract class MixinGameRenderer
{
    @Shadow
    @Final
    private MinecraftClient client;

    @Nullable
    private Entity cameraEntityOriginal;
    private float realYaw;
    private float realPitch;

    @Inject(method = "renderWorld", at = @At("HEAD"), cancellable = true)
    private void onRenderWorld(CallbackInfo ci)
    {
        if (Callbacks.skipWorldRendering)
        {
            ci.cancel();
        }
    }

    @Inject(method = "getFov", at = @At("HEAD"), cancellable = true)
    private void applyZoom(Camera camera, float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Double> cir)
    {
        if (FeatureToggle.TWEAK_ZOOM.getBooleanValue() && Hotkeys.ZOOM_ACTIVATE.getKeybind().isKeybindHeld())
        {
            cir.setReturnValue(Configs.Generic.ZOOM_FOV.getDoubleValue());
        }
        else if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            cir.setReturnValue(this.client.options.fov);
        }
    }

    @Redirect(method = "updateTargetedEntity", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/projectile/ProjectileUtil;rayTrace(" +
                         "Lnet/minecraft/entity/Entity;" +
                         "Lnet/minecraft/util/math/Vec3d;" +
                         "Lnet/minecraft/util/math/Vec3d;" +
                         "Lnet/minecraft/util/math/Box;" +
                         "Ljava/util/function/Predicate;D)" +
                         "Lnet/minecraft/util/hit/EntityHitResult;"))
    private EntityHitResult ignoreDeadEntities(Entity entity, Vec3d startVec, Vec3d endVec,
            Box box, Predicate<Entity> predicate, double distance)
    {
        if (Configs.Disable.DISABLE_DEAD_MOB_TARGETING.getBooleanValue())
        {
            predicate = predicate.and((entityIn) ->
            {
                return (entityIn instanceof LivingEntity) == false || ((LivingEntity) entityIn).getHealth() > 0f;
            });
        }

        return ProjectileUtil.rayTrace(entity, startVec, endVec, box, predicate, distance);
    }

    @Inject(method = "renderWorld", at = @At(
                value = "INVOKE", shift = Shift.AFTER,
                target = "Lnet/minecraft/client/render/GameRenderer;updateTargetedEntity(F)V"))
    private void overrideRenderViewEntityPre(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            Entity camera = CameraEntity.getCamera();

            if (camera != null)
            {
                this.cameraEntityOriginal = this.client.getCameraEntity();
                this.client.setCameraEntity(camera);
            }
        }
        else if (FeatureToggle.TWEAK_ELYTRA_CAMERA.getBooleanValue() && Hotkeys.ELYTRA_CAMERA.getKeybind().isKeybindHeld())
        {
            Entity entity = this.client.getCameraEntity();

            if (entity != null)
            {
                this.realYaw = entity.yaw;
                this.realPitch = entity.pitch;
                MiscUtils.setEntityRotations(entity, MiscUtils.getCameraYaw(), MiscUtils.getCameraPitch());
            }
        }
    }

    @Inject(method = "renderWorld", at = @At("RETURN"))
    private void overrideRenderViewEntityPost(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && this.cameraEntityOriginal != null)
        {
            this.client.setCameraEntity(this.cameraEntityOriginal);
            this.cameraEntityOriginal = null;
        }
        else if (FeatureToggle.TWEAK_ELYTRA_CAMERA.getBooleanValue() && Hotkeys.ELYTRA_CAMERA.getKeybind().isKeybindHeld())
        {
            Entity entity = this.client.getCameraEntity();

            if (entity != null)
            {
                MiscUtils.setEntityRotations(entity, this.realYaw, this.realPitch);
            }
        }
    }

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void removeHandRendering(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
