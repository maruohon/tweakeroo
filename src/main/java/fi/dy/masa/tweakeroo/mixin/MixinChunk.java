package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

@Mixin(Chunk.class)
public class MixinChunk
{
    @Shadow
    @Final
    private World world;

    @Inject(method = "propagateSkylightOcclusion", at = @At("HEAD"), cancellable = true)
    private void onPropagateSkylightOcclusion(int x, int z, CallbackInfo ci)
    {
        if (this.world.isRemote && FeatureToggle.TWEAK_NO_LIGHT_UPDATES_ALL.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "relightBlock", at = @At("HEAD"), cancellable = true)
    private void onRelightBlock(int x, int y, int z, CallbackInfo ci)
    {
        if (this.world.isRemote && FeatureToggle.TWEAK_NO_LIGHT_UPDATES_ALL.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "generateSkylightMap", at = @At("HEAD"), cancellable = true)
    private void onGenerateSkylightMap(CallbackInfo ci)
    {
        if (this.world.isRemote && FeatureToggle.TWEAK_NO_LIGHT_UPDATES_ALL.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
