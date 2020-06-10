package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.render.SkyProperties;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(SkyProperties.Nether.class)
public abstract class MixinSkyProperties_Nether extends SkyProperties
{
    private MixinSkyProperties_Nether(float cloudsHeight, boolean alternateSkyColor, class_5401 arg, boolean bl, boolean bl2)
    {
        super(cloudsHeight, alternateSkyColor, arg, bl, bl2);
    }

    @Inject(method = "useThickFog", at = @At("HEAD"), cancellable = true)
    private void disableNetherFog(int x, int z, CallbackInfoReturnable<Boolean> cir)
    {
        if (Configs.Disable.DISABLE_NETHER_FOG.getBooleanValue())
        {
            cir.setReturnValue(false);
        }
    }
}
