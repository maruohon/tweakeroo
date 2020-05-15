package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.class_5294;
import net.minecraft.class_5294.class_5296;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(class_5296.class)
public abstract class Mixinclass_5296 extends class_5294
{
    public Mixinclass_5296(float f, boolean bl, boolean bl2)
    {
        super(f, bl, bl2);
    }

    @Inject(method = "method_28110", at = @At("HEAD"), cancellable = true)
    private void disableNetherFog(int x, int z, CallbackInfoReturnable<Boolean> cir)
    {
        if (Configs.Disable.DISABLE_NETHER_FOG.getBooleanValue())
        {
            cir.setReturnValue(false);
        }
    }
}
