package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class MixinBlockEntityRenderDispatcher
{
    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FILnet/minecraft/block/BlockRenderLayer;Lnet/minecraft/client/render/BufferBuilder;)V", at = @At("HEAD"), cancellable = true)
    private void preventTileEntityRendering(BlockEntity tileentityIn, float partialTicks, int destroyStage, BlockRenderLayer layer, BufferBuilder buffer, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_TILE_ENTITY_RENDERING.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "renderEntity(Lnet/minecraft/block/entity/BlockEntity;)V", at = @At("HEAD"), cancellable = true)
    private void preventTileEntityRendering(BlockEntity tileEntityIn, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_TILE_ENTITY_RENDERING.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
