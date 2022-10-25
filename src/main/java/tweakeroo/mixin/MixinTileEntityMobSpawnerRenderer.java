package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;

import tweakeroo.config.DisableToggle;

@Mixin(TileEntityMobSpawnerRenderer.class)
public abstract class MixinTileEntityMobSpawnerRenderer
{
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cancelRender(CallbackInfo ci)
    {
        if (DisableToggle.DISABLE_MOB_SPAWNER_MOB_RENDER.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
