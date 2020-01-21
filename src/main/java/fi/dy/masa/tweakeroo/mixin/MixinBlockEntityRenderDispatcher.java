package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher.class)
public abstract class MixinBlockEntityRenderDispatcher
{
    @Inject(method = "render(" +
                                "Lnet/minecraft/block/entity/BlockEntity;F" +
                                "Lnet/minecraft/client/util/math/MatrixStack;" +
                                "Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("HEAD"), cancellable = true)
    private void preventTileEntityRendering(
            net.minecraft.block.entity.BlockEntity tileentityIn,
            float partialTicks,
            net.minecraft.client.util.math.MatrixStack matrixStack,
            net.minecraft.client.render.VertexConsumerProvider vertexConsumerProvider, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_TILE_ENTITY_RENDERING.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "renderEntity(" +
                                    "Lnet/minecraft/block/entity/BlockEntity;" +
                                    "Lnet/minecraft/client/util/math/MatrixStack;" +
                                    "Lnet/minecraft/client/render/VertexConsumerProvider;II)Z", at = @At("HEAD"), cancellable = true)
    private void preventTileEntityRendering(
            net.minecraft.block.entity.BlockEntity tileEntityIn,
            net.minecraft.client.util.math.MatrixStack matrixStack,
            net.minecraft.client.render.VertexConsumerProvider vertexConsumerProvider,
            int light, int overlay, CallbackInfoReturnable<Boolean> cir)
    {
        if (Configs.Disable.DISABLE_TILE_ENTITY_RENDERING.getBooleanValue())
        {
            cir.setReturnValue(true);
        }
    }
}
