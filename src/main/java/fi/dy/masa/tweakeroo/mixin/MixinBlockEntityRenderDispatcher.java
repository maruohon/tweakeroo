package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class MixinBlockEntityRenderDispatcher
{
    @Inject(method = "render(" +
                                "Lnet/minecraft/block/entity/BlockEntity;F" +
                                "Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;DDD)V", at = @At("HEAD"), cancellable = true)
    private void preventTileEntityRendering(BlockEntity tileentityIn, float partialTicks, class_4587 foo1, class_4597 foo2, double x, double y, double z, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_TILE_ENTITY_RENDERING.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "renderEntity(" +
                                    "Lnet/minecraft/block/entity/BlockEntity;" +
                                    "Lnet/minecraft/class_4587;I)V", at = @At("HEAD"), cancellable = true)
    private void preventTileEntityRendering(BlockEntity tileEntityIn, class_4587 foo1, int foo, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_TILE_ENTITY_RENDERING.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "method_23077(" +
                                    "Lnet/minecraft/block/entity/BlockEntity;" +
                                    "Lnet/minecraft/class_4587;" +
                                    "Lnet/minecraft/class_4597;I)Z", at = @At("HEAD"), cancellable = true)
    private void preventTileEntityRendering(BlockEntity tileEntityIn, class_4587 foo1, class_4597 foo2, int foo, CallbackInfoReturnable<Boolean> cir)
    {
        if (Configs.Disable.DISABLE_TILE_ENTITY_RENDERING.getBooleanValue())
        {
            cir.setReturnValue(true);
        }
    }
}
