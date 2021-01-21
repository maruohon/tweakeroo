package fi.dy.masa.tweakeroo.mixin;

import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlockState.class)
public class MixinAbstractBlockState {
    @Inject(method = "getModelOffset", at = @At("HEAD"), cancellable = true)
    private void overrideGetModelOffset(CallbackInfoReturnable<Vec3d> cir)
    {
        if (Configs.Disable.DISABLE_RANDOM_MODEL_OFFSET.getBooleanValue())
        {
            cir.setReturnValue(Vec3d.ZERO);
        }
    }

}
