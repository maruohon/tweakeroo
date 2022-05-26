package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.malilib.util.GameUtils;
import fi.dy.masa.malilib.util.wrap.EntityWrap;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.DisableToggle;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import fi.dy.masa.tweakeroo.util.CameraUtils;
import fi.dy.masa.tweakeroo.util.MiscUtils;

@Mixin(net.minecraft.client.renderer.EntityRenderer.class)
public abstract class MixinEntityRenderer
{
    @Shadow @Final private net.minecraft.client.Minecraft mc;
    @Shadow private float farPlaneDistance;

    private float realYaw;
    private float realPitch;

    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/client/Minecraft;getRenderViewEntity()Lnet/minecraft/entity/Entity;"))
    private net.minecraft.entity.Entity returnRealPlayer(net.minecraft.client.Minecraft mc)
    {
        // Return the real player for the hit target ray tracing if the
        // player motion option is enabled in Free Camera mode.
        // Normally in Free Camera mode the Tweakeroo CameraEntity is set as the
        // render view/camera entity, which would then also ray trace from the camera point of view.
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() &&
            Configs.Generic.FREE_CAMERA_PLAYER_INPUTS.getBooleanValue() &&
            this.mc.player != null)
        {
            return this.mc.player;
        }

        return mc.getRenderViewEntity();
    }

    @Inject(method = "renderWorld(FJ)V", at = @At("HEAD"), cancellable = true)
    private void onRenderWorld(CallbackInfo ci)
    {
        if (MiscUtils.skipWorldRendering)
        {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "renderWorld(FJ)V", at = @At("HEAD"), argsOnly = true)
    private long overrideRenderTimeout(long finishTime)
    {
        if (FeatureToggle.TWEAK_CHUNK_RENDER_TIMEOUT.getBooleanValue())
        {
            return System.nanoTime() + Configs.Generic.CHUNK_RENDER_TIMEOUT.getIntegerValue();
        }

        return finishTime;
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
            RenderUtils.overrideLavaFog(this.mc.player);
        }
    }

    @Inject(method = "setupFog",
            slice = @Slice(
                    from = @At(value = "FIELD", ordinal = 1,
                               target = "Lnet/minecraft/client/renderer/EntityRenderer;farPlaneDistance:F"),
                    to = @At(value = "FIELD", ordinal = 1,
                             target = "Lorg/lwjgl/opengl/ContextCapabilities;GL_NV_fog_distance:Z")),
            at = @At(value = "INVOKE", shift = At.Shift.AFTER,
                     target = "Lnet/minecraft/client/renderer/GlStateManager;setFogEnd(F)V"))
    private void disableRenderDistanceFog(int startCoords, float partialTicks, CallbackInfo ci)
    {
        if (DisableToggle.DISABLE_RENDER_DISTANCE_FOG.getBooleanValue())
        {
            float renderDistance = this.farPlaneDistance;
            net.minecraft.client.renderer.GlStateManager.setFogStart(renderDistance * 1.6F);
            net.minecraft.client.renderer.GlStateManager.setFogEnd(renderDistance * 2.0F);
        }
    }

    @Inject(method = "getFOVModifier", at = @At("HEAD"), cancellable = true)
    private void zoom(float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Float> cir)
    {
        if (useFOVSetting == false)
        {
            return;
        }

        if (MiscUtils.isZoomActive())
        {
            cir.setReturnValue(Configs.Generic.ZOOM_FOV.getFloatValue());
        }
        else if (FeatureToggle.TWEAK_STATIC_FOV.getBooleanValue())
        {
            cir.setReturnValue(Configs.Generic.STATIC_FOV.getFloatValue());
        }
        // Don't change the FoV when "sprinting" in Free Camera mode
        // FIXME remove this, this breaks other zoom mods while in Free Camera mode.
        // FIXME Instead try to prevent the zoom effect from happening some other way while in free cam sprint mode 
        else if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            cir.setReturnValue(this.mc.gameSettings.fovSetting);
        }
    }

    @Inject(method = "renderRainSnow", at = @At("HEAD"), cancellable = true)
    private void cancelRainRender(float partialTicks, CallbackInfo ci)
    {
        if (DisableToggle.DISABLE_RAIN_EFFECTS.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "addRainParticles", at = @At("HEAD"), cancellable = true)
    private void cancelRainRender(CallbackInfo ci)
    {
        if (DisableToggle.DISABLE_RAIN_EFFECTS.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "renderWorld(FJ)V", at = @At(
                value = "INVOKE", shift = Shift.AFTER,
                target = "Lnet/minecraft/client/renderer/EntityRenderer;getMouseOver(F)V"))
    private void overrideRenderViewEntityPre(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_ELYTRA_CAMERA.getBooleanValue() && Hotkeys.ELYTRA_CAMERA.getKeyBind().isKeyBindHeld())
        {
            net.minecraft.entity.Entity entity = this.mc.getRenderViewEntity();

            if (entity != null)
            {
                this.realYaw = EntityWrap.getYaw(entity);
                this.realPitch = EntityWrap.getPitch(entity);
                MiscUtils.setEntityRotations(entity, CameraUtils.getCameraYaw(), CameraUtils.getCameraPitch());
            }
        }
    }

    @Inject(method = "renderWorld(FJ)V", at = @At("RETURN"))
    private void overrideRenderViewEntityPost(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_ELYTRA_CAMERA.getBooleanValue() && Hotkeys.ELYTRA_CAMERA.getKeyBind().isKeyBindHeld())
        {
            net.minecraft.entity.Entity entity = GameUtils.getCameraEntity();

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
                target = "Lnet/minecraft/client/entity/EntityPlayerSP;isSpectator()Z"))
    private void preSetupTerrain(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci)
    {
        CameraUtils.setFreeCameraSpectator(true);
    }

    @Inject(method = "renderWorldPass", at = @At(
                value = "INVOKE", shift = At.Shift.AFTER,
                target = "Lnet/minecraft/client/renderer/RenderGlobal;setupTerrain(" +
                         "Lnet/minecraft/entity/Entity;" +
                         "DLnet/minecraft/client/renderer/culling/ICamera;IZ)V"))
    private void postSetupTerrain(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci)
    {
        CameraUtils.setFreeCameraSpectator(false);
    }
}
