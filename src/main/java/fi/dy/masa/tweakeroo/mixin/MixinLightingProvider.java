package fi.dy.masa.tweakeroo.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.LightingProvider;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(LightingProvider.class)
public abstract class MixinLightingProvider
{
    @Shadow @Final @Nullable private ChunkLightProvider<?, ?> blockLightProvider;

    @Inject(method = "checkBlock", at = @At("HEAD"), cancellable = true)
    private void disableLightUpdates(BlockPos pos, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_CLIENT_LIGHT_UPDATES.getBooleanValue() &&
            this.blockLightProvider != null &&
            ((IMixinChunkLightProvider) this.blockLightProvider).tweakeroo_getChunkProvider().getWorld() == MinecraftClient.getInstance().world)
        {
            ci.cancel();
        }
    }
}
