package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(MobSpawnerBlockEntityRenderer.class)
public abstract class MixinMobSpawnerBlockEntityRenderer
{
    @Inject(method = "method_3590", at = @At("HEAD"), cancellable = true) // render
    private void cancelRender(CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_MOB_SPAWNER_MOB_RENDER.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
