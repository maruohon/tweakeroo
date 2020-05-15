package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.entity.passive.EntityLlama;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(EntityLlama.class)
public class MixinEntityLlama
{
    @Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
    private void allowLamaSteering(CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_LLAMA_STEERING.getBooleanValue())
        {
            cir.setReturnValue(true);
        }
    }
}
