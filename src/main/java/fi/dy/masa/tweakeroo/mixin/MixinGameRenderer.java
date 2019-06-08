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
import fi.dy.masa.tweakeroo.config.Callbacks;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer
{
    @Shadow
    @Final
    private Minecraft mc;

    @Nullable
    private Entity renderViewEntityOriginal;
    private float realYaw;
    private float realPitch;

    @Inject(method = "renderWorld(FJ)V", at = @At("HEAD"), cancellable = true)
    private void onRenderWorld(CallbackInfo ci)
    {
        if (Callbacks.skipWorldRendering)
        {
            ci.cancel();
        }
    }

    @Inject(method = "getFOVModifier", at = @At("HEAD"), cancellable = true)
    private void zoom(float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Double> cir)
    {
        if (FeatureToggle.TWEAK_ZOOM.getBooleanValue() && Hotkeys.ZOOM_ACTIVATE.getKeybind().isKeybindHeld())
        {
            cir.setReturnValue(Configs.Generic.ZOOM_FOV.getDoubleValue());
        }
        else if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            cir.setReturnValue(this.mc.gameSettings.fovSetting);
        }
    }

    @Inject(method = "renderRainSnow", at = @At("HEAD"), cancellable = true)
    private void cancelRainRender(float partialTicks, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_RAIN_EFFECTS.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "addRainParticles", at = @At("HEAD"), cancellable = true)
    private void cancelRainRender(CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_RAIN_EFFECTS.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Redirect(method = "getMouseOver", at = @At(
                value = "INVOKE",
                target = "Ljava/util/function/Predicate;and(" +
                         "Ljava/util/function/Predicate;)" +
                         "Ljava/util/function/Predicate;", remap = false))
    private Predicate<Entity> ignoreDeadEntities(Predicate<Entity> owner, Predicate<Entity> arg)
    {
        Predicate<Entity> combined = owner.and(arg);

        if (Configs.Disable.DISABLE_DEAD_MOB_TARGETING.getBooleanValue())
        {
            combined = combined.and(new Predicate<Entity>()
            {
                @Override
                public boolean test(Entity entity)
                {
                    return (entity instanceof EntityLivingBase) == false ||
                            ((EntityLivingBase) entity).getHealth() > 0f;
                }
            });
        }

        return combined;
    }

    @Inject(method = "renderWorld(FJ)V", at = @At(
                value = "INVOKE", shift = Shift.AFTER,
                target = "Lnet/minecraft/client/renderer/GameRenderer;getMouseOver(F)V"))
    private void overrideRenderViewEntityPre(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            Entity camera = CameraEntity.getCamera();

            if (camera != null)
            {
                this.renderViewEntityOriginal = this.mc.getRenderViewEntity();
                this.mc.setRenderViewEntity(camera);
            }
        }
        else if (FeatureToggle.TWEAK_ELYTRA_CAMERA.getBooleanValue() && Hotkeys.ELYTRA_CAMERA.getKeybind().isKeybindHeld())
        {
            Entity entity = this.mc.getRenderViewEntity();

            if (entity != null)
            {
                this.realYaw = entity.rotationYaw;
                this.realPitch = entity.rotationPitch;
                MiscUtils.setEntityRotations(entity, MiscUtils.getCameraYaw(), MiscUtils.getCameraPitch());
            }
        }
    }

    @Inject(method = "renderWorld(FJ)V", at = @At("RETURN"))
    private void overrideRenderViewEntityPost(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && this.renderViewEntityOriginal != null)
        {
            this.mc.setRenderViewEntity(this.renderViewEntityOriginal);
            this.renderViewEntityOriginal = null;
        }
        else if (FeatureToggle.TWEAK_ELYTRA_CAMERA.getBooleanValue() && Hotkeys.ELYTRA_CAMERA.getKeybind().isKeybindHeld())
        {
            Entity entity = this.mc.getRenderViewEntity();

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

    @Inject(method = "updateCameraAndRender(FJ)V", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/renderer/WorldRenderer;setupTerrain(" +
                         "Lnet/minecraft/entity/Entity;" +
                         "FLnet/minecraft/client/renderer/culling/ICamera;IZ)V"))
    private void preSetupTerrain(float partialTicks, long finishTimeNano, CallbackInfo ci)
    {
        MiscUtils.setFreeCameraSpectator(true);
    }

    @Inject(method = "updateCameraAndRender(FJ)V", at = @At(
                value = "INVOKE", shift = At.Shift.AFTER,
                target = "Lnet/minecraft/client/renderer/WorldRenderer;setupTerrain(" +
                         "Lnet/minecraft/entity/Entity;" +
                         "FLnet/minecraft/client/renderer/culling/ICamera;IZ)V"))
    private void postSetupTerrain(float partialTicks, long finishTimeNano, CallbackInfo ci)
    {
        MiscUtils.setFreeCameraSpectator(false);
    }
}
