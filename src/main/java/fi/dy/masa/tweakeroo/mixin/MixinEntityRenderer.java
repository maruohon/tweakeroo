package fi.dy.masa.tweakeroo.mixin;

import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.config.Callbacks;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer
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

    @Inject(method = "setupFog(IF)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GlStateManager;setFogDensity(F)V",
            shift = Shift.AFTER,
            ordinal = 4))
    private void onSetupLavaFog(int startCoords, float partialTicks, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_LAVA_VISIBILITY.getBooleanValue() && Configs.Generic.LAVA_VISIBILITY_OPTIFINE.getBooleanValue() == false)
        {
            RenderUtils.overrideLavaFog(Minecraft.getMinecraft().getRenderViewEntity());
        }
    }

    @Inject(method = "getFOVModifier", at = @At("HEAD"), cancellable = true)
    private void zoom(float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Float> cir)
    {
        if (FeatureToggle.TWEAK_ZOOM.getBooleanValue() && Hotkeys.ZOOM_ACTIVATE.getKeybind().isKeybindHeld())
        {
            cir.setReturnValue((float) Configs.Generic.ZOOM_FOV.getDoubleValue());
        }
        else if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() || FeatureToggle.TWEAK_STATIC_FOV.getBooleanValue())
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

    @Inject(method = "renderWorld(FJ)V", at = @At(
                value = "INVOKE", shift = Shift.AFTER,
                target = "Lnet/minecraft/client/renderer/EntityRenderer;getMouseOver(F)V"))
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

    @Inject(method = "renderWorldPass", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/renderer/RenderGlobal;setupTerrain(" +
                         "Lnet/minecraft/entity/Entity;" +
                         "DLnet/minecraft/client/renderer/culling/ICamera;IZ)V"))
    private void preSetupTerrain(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci)
    {
        MiscUtils.setFreeCameraSpectator(true);
    }

    @Inject(method = "renderWorldPass", at = @At(
                value = "INVOKE", shift = At.Shift.AFTER,
                target = "Lnet/minecraft/client/renderer/RenderGlobal;setupTerrain(" +
                         "Lnet/minecraft/entity/Entity;" +
                         "DLnet/minecraft/client/renderer/culling/ICamera;IZ)V"))
    private void postSetupTerrain(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci)
    {
        MiscUtils.setFreeCameraSpectator(false);
    }
}
