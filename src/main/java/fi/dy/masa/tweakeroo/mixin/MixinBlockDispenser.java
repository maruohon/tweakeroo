package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.block.BlockDispenser;

@Mixin(BlockDispenser.class)
public abstract class MixinBlockDispenser
{
    @Inject(method = "setDefaultDirection", at = @At("HEAD"), cancellable = true)
    private void rotationFix(CallbackInfo ci)
    {
        if (Configs.Generic.CLIENT_PLACEMENT_ROTATION.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
