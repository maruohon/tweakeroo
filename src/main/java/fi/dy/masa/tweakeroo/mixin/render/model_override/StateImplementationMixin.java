package fi.dy.masa.tweakeroo.mixin.render.model_override;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.util.EnumBlockRenderType;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.data.BlockRenderOverrides;

@Mixin(targets = "net/minecraft/block/state/BlockStateContainer$StateImplementation")
public abstract class StateImplementationMixin extends BlockStateBase
{
    @Inject(method = "getRenderType()Lnet/minecraft/util/EnumBlockRenderType;",
            at = @At("HEAD"), cancellable = true)
    private void tweakeroo_overrideRenderType(CallbackInfoReturnable<EnumBlockRenderType> cir)
    {
        if (FeatureToggle.TWEAK_BLOCK_RENDER_TYPE_OVERRIDE.getBooleanValue())
        {
            EnumBlockRenderType type = BlockRenderOverrides.getRenderType(this);

            if (type != null)
            {
                cir.setReturnValue(type);
            }
        }
    }
}
