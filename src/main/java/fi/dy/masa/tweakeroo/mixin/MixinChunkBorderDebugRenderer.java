package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.render.debug.ChunkBorderDebugRenderer;

@Mixin(ChunkBorderDebugRenderer.class)
public abstract class MixinChunkBorderDebugRenderer
{
    /*
    @Redirect(method = "render", at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/render/GameRenderer;getCamera()Lnet/minecraft/client/render/Camera;"))
    private Camera useCameraEntity(GameRenderer renderer)
    {
        // Fix the chunk border renderer using the client player instead of the camera entity
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
        }

        return renderer.getCamera();
    }
    */
}
