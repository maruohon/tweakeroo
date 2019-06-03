package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;

@Mixin(TileEntityMobSpawnerRenderer.class)
public abstract class MixinTileEntityMobSpawnerRenderer
{
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cancelRender(CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_MOB_SPAWNER_MOB_RENDER.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
