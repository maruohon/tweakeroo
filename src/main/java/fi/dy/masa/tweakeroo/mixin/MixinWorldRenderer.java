package fi.dy.masa.tweakeroo.mixin;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.entity.Entity;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Shadow
    @Final
    private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "method_22713", at = @At("HEAD"), cancellable = true) // renderRain
    private void cancelRainRender(Camera camera, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_RAIN_EFFECTS.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    private void cancelRainRender(LightmapTextureManager lightmap, float partialTicks, double x, double y, double z, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_RAIN_EFFECTS.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;setupTerrain(" +
                     "Lnet/minecraft/client/render/Camera;" +
                     "Lnet/minecraft/client/render/Frustum;ZIZ)V"))
    private void preSetupTerrain(net.minecraft.client.util.math.MatrixStack matrixStack, float partialTicks, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer renderer, LightmapTextureManager lightmap, Matrix4f matrix4f, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            MiscUtils.setFreeCameraSpectator(true);
        }
    }

    @Inject(method = "render", at = @At(
            value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/render/WorldRenderer;setupTerrain(" +
                     "Lnet/minecraft/client/render/Camera;" +
                     "Lnet/minecraft/client/render/Frustum;ZIZ)V"))
    private void postSetupTerrain(net.minecraft.client.util.math.MatrixStack matrixStack, float partialTicks, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer renderer, LightmapTextureManager lightmap, Matrix4f matrix4f, CallbackInfo ci)
    {
        MiscUtils.setFreeCameraSpectator(false);
    }

    // Allow rendering the client player entity by spoofing one of the entity rendering conditions while in Free Camera mode
    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;", ordinal = 3))
    private Entity allowRenderingClientPlayerInFreeCameraMode(Camera camera)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            return MinecraftClient.getInstance().player;
        }

        return camera.getFocusedEntity();
    }

    @Redirect(method = "setupTerrain", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/render/BuiltChunkStorage;updateCameraPosition(DD)V"))
    private void preventRenderChunkPositionUpdates(net.minecraft.client.render.BuiltChunkStorage storage, double viewEntityX, double viewEntityZ)
    {
        // Don't update the RenderChunk positions when moving around in the Free Camera mode.
        // Otherwise the chunks would become empty when they are outside the render range
        // from the camera entity, ie. on the other side of the actual player.
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() == false)
        {
            storage.updateCameraPosition(viewEntityX, viewEntityZ);
        }
    }

    @Redirect(method = "renderEntity", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;getLight(Lnet/minecraft/entity/Entity;F)I"))
    private <E extends Entity> int fixingBlackTNTOnSoulsand(EntityRenderDispatcher entityRenderDispatcher, E entity, float tickDelta) {
        if (!(Configs.Fixes.TNT_ON_SOULSAND_LIGHTING_FIX.getBooleanValue()) || entity.getType() != EntityType.TNT) {
            return entityRenderDispatcher.getLight(entity, tickDelta);
        }
        int blockLight = entity.isOnFire() ? 15 : entity.world.getLightLevel(LightType.BLOCK, new BlockPos(entity.getCameraPosVec(tickDelta).add(0, 0.15, 0)));
        return LightmapTextureManager.pack(blockLight, entity.world.getLightLevel(LightType.SKY, new BlockPos(entity.getCameraPosVec(tickDelta).add(0, 0.15, 0))));
    }
}
