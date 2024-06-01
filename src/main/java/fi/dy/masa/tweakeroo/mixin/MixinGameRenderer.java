package fi.dy.masa.tweakeroo.mixin;

import java.util.function.Predicate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;

import fi.dy.masa.tweakeroo.config.Callbacks;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.util.CameraUtils;
import fi.dy.masa.tweakeroo.util.MiscUtils;

@Mixin(value = GameRenderer.class, priority = 1001)
public abstract class MixinGameRenderer
{
    @Shadow @Final MinecraftClient client;

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
        if (MiscUtils.isZoomActive())
        {
            cir.setReturnValue(Configs.Generic.ZOOM_FOV.getDoubleValue());
        }
        else if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            cir.setReturnValue((double) this.client.options.getFov().getValue());
        }
    }

    @Redirect(method = "updateCrosshairTarget", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/client/MinecraftClient;getCameraEntity()Lnet/minecraft/entity/Entity;"))
    private Entity overrideCameraEntityForRayTrace(MinecraftClient mc)
    {
        // Return the real player for the hit target ray tracing if the
        // player inputs option is enabled in Free Camera mode.
        // Normally in Free Camera mode the Tweakeroo CameraEntity is set as the
        // render view/camera entity, which would then also ray trace from the camera point of view.
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() &&
            Configs.Generic.FREE_CAMERA_PLAYER_INPUTS.getBooleanValue() &&
            mc.player != null)
        {
            return mc.player;
        }

        return mc.getCameraEntity();
    }

    @ModifyArg(method = "findCrosshairTarget",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(" +
                                 "Lnet/minecraft/entity/Entity;" +
                                 "Lnet/minecraft/util/math/Vec3d;" +
                                 "Lnet/minecraft/util/math/Vec3d;" +
                                 "Lnet/minecraft/util/math/Box;" +
                                 "Ljava/util/function/Predicate;D)" +
                                 "Lnet/minecraft/util/hit/EntityHitResult;"))
    private Predicate<Entity> overrideTargetedEntityCheck(Predicate<Entity> predicate)
    {
        if (Configs.Disable.DISABLE_DEAD_MOB_TARGETING.getBooleanValue())
        {
            predicate = predicate.and((entityIn) -> (entityIn instanceof LivingEntity) == false || ((LivingEntity) entityIn).getHealth() > 0f);
        }

        if ((FeatureToggle.TWEAK_HANGABLE_ENTITY_BYPASS.getBooleanValue() && this.client.player != null
             && this.client.player.isSneaking() == Configs.Generic.HANGABLE_ENTITY_BYPASS_INVERSE.getBooleanValue()))
        {
            predicate = predicate.and((entityIn) -> (entityIn instanceof AbstractDecorationEntity) == false);
        }

        return predicate;
    }

    @Inject(method = "renderWorld", at = @At(
                value = "INVOKE", shift = Shift.AFTER,
                target = "Lnet/minecraft/client/render/GameRenderer;updateCrosshairTarget(F)V"))
    private void overrideRenderViewEntityPre(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_ELYTRA_CAMERA.getBooleanValue() && Hotkeys.ELYTRA_CAMERA.getKeybind().isKeybindHeld())
        {
            Entity entity = this.client.getCameraEntity();

            if (entity != null)
            {
                this.realYaw = entity.getYaw();
                this.realPitch = entity.getPitch();
                MiscUtils.setEntityRotations(entity, CameraUtils.getCameraYaw(), CameraUtils.getCameraPitch());
            }
        }
    }

    @Inject(method = "renderWorld", at = @At("RETURN"))
    private void overrideRenderViewEntityPost(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_ELYTRA_CAMERA.getBooleanValue() && Hotkeys.ELYTRA_CAMERA.getKeybind().isKeybindHeld())
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
