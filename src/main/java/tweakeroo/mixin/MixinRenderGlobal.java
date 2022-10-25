package tweakeroo.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import tweakeroo.config.DisableToggle;
import tweakeroo.config.FeatureToggle;
import tweakeroo.util.CameraUtils;

@Mixin(net.minecraft.client.renderer.RenderGlobal.class)
public abstract class MixinRenderGlobal
{
    @Shadow private net.minecraft.client.renderer.ViewFrustum viewFrustum;
    @Shadow private int frustumUpdatePosChunkX;
    @Shadow private int frustumUpdatePosChunkZ;

    private int lastUpdatePosX;
    private int lastUpdatePosZ;

    @Inject(method = "notifyLightSet", at = @At("HEAD"), cancellable = true)
    public void notifyLightSet(net.minecraft.util.math.BlockPos pos, CallbackInfo ci)
    {
        if (DisableToggle.DISABLE_LIGHT_UPDATES.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "setupTerrain", at = @At(
                value = "FIELD", opcode = Opcodes.PUTFIELD,
                target = "Lnet/minecraft/client/renderer/RenderGlobal;frustumUpdatePosX:D"))
    private void rebuildChunksAroundCamera1(
            net.minecraft.entity.Entity viewEntity,
            double partialTicks,
            net.minecraft.client.renderer.culling.ICamera camera,
            int frameCount,
            boolean playerSpectator,
            CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            // Hold on to the previous update position before it gets updated
            this.lastUpdatePosX = this.frustumUpdatePosChunkX;
            this.lastUpdatePosZ = this.frustumUpdatePosChunkZ;
        }
    }

    @Inject(method = "setupTerrain", at = @At(
            value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/renderer/ViewFrustum;updateChunkPositions(DD)V"))
    private void rebuildChunksAroundCamera2(
            net.minecraft.entity.Entity viewEntity,
            double partialTicks,
            net.minecraft.client.renderer.culling.ICamera camera,
            int frameCount,
            boolean playerSpectator,
            CallbackInfo ci)
    {
        // Mark the chunks at the edge of the free camera's render range for rebuilding
        // when the camera moves around.
        // Normally these rebuilds would happen when the server sends chunks to the client when the player moves around.
        // But in Free Camera mode moving the ViewFrustum/BuiltChunkStorage would cause the terrain
        // to disappear because of no dirty marking calls from chunk loading.
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            CameraUtils.markChunksForRebuild(this.viewFrustum, viewEntity.chunkCoordX, viewEntity.chunkCoordZ, this.lastUpdatePosX, this.lastUpdatePosZ);
        }
    }

    @Redirect(method = "setupTerrain", require = 0, at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;needsImmediateUpdate()Z"))
    private boolean forceChunkRendersOnMainThread(net.minecraft.client.renderer.chunk.RenderChunk renderer)
    {
        if (FeatureToggle.TWEAK_CHUNK_RENDER_MAIN_THREAD.getBooleanValue())
        {
            return true;
        }

        return renderer.needsImmediateUpdate();
    }
}