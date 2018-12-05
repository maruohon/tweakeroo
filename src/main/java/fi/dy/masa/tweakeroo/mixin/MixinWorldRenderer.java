package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockPos;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Inject(method = "notifyLightSet", at = @At("HEAD"), cancellable = true)
    public void notifyLightSet(BlockPos pos, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_NO_LIGHT_UPDATES.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
