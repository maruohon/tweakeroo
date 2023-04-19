package tweakeroo.mixin.render.model_override;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;

import tweakeroo.config.FeatureToggle;
import tweakeroo.util.data.BlockRenderOverrides;

@Mixin(BlockRendererDispatcher.class)
public abstract class BlockRendererDispatcherMixin
{
    @ModifyArg(method = "getModelForState",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/renderer/BlockModelShapes;getModelForState(Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/client/renderer/block/model/IBakedModel;"))
    private IBlockState tweakeroo_overrideBlockModel(IBlockState state)
    {
        if (FeatureToggle.TWEAK_BLOCK_MODEL_OVERRIDE.getBooleanValue())
        {
            IBlockState substituteState = BlockRenderOverrides.getModelOverrideState(state);

            if (substituteState != null)
            {
                return substituteState;
            }
        }

        return state;
    }
}
