package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class MixinBlockEntityRenderDispatcher
{
    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FI)V", at = @At("HEAD"), cancellable = true)
    private void preventTileEntityRendering(BlockEntity tileentityIn, float partialTicks, int destroyStage, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_TILE_ENTITY_RENDERING.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "renderEntity(Lnet/minecraft/block/entity/BlockEntity;DDDF)V", at = @At("HEAD"), cancellable = true)
    private void preventTileEntityRendering(BlockEntity tileEntityIn, double x, double y, double z, float partialTicks, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_TILE_ENTITY_RENDERING.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "renderEntity(Lnet/minecraft/block/entity/BlockEntity;DDDFIZ)V", at = @At("HEAD"), cancellable = true)
    private void preventTileEntityRendering(BlockEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage, boolean hasNoBlock, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_TILE_ENTITY_RENDERING.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
