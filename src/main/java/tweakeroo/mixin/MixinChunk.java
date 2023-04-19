package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import tweakeroo.config.DisableToggle;

@Mixin(Chunk.class)
public abstract class MixinChunk
{
    @Shadow
    @Final
    private World world;

    @Inject(method = "propagateSkylightOcclusion", at = @At("HEAD"), cancellable = true)
    private void onPropagateSkylightOcclusion(int x, int z, CallbackInfo ci)
    {
        if (this.world.isRemote && DisableToggle.DISABLE_LIGHT_UPDATES_ALL.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "relightBlock", at = @At("HEAD"), cancellable = true)
    private void onRelightBlock(int x, int y, int z, CallbackInfo ci)
    {
        if (this.world.isRemote && DisableToggle.DISABLE_LIGHT_UPDATES_ALL.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "generateSkylightMap", at = @At("HEAD"), cancellable = true)
    private void onGenerateSkylightMap(CallbackInfo ci)
    {
        if (this.world.isRemote && DisableToggle.DISABLE_LIGHT_UPDATES_ALL.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
