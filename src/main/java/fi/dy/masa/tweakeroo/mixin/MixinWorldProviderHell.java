package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.tweakeroo.config.DisableToggle;

@Mixin(net.minecraft.world.WorldProviderHell.class)
public abstract class MixinWorldProviderHell extends net.minecraft.world.WorldProvider
{
    @Inject(method = "doesXZShowFog", at = @At("HEAD"), cancellable = true)
    public void disableNetherFog(int x, int z, CallbackInfoReturnable<Boolean> cir)
    {
        if (DisableToggle.DISABLE_NETHER_FOG.getBooleanValue())
        {
            cir.setReturnValue(false);
        }
    }
}
