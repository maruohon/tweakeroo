package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(MobSpawnerLogic.class)
public abstract class MixinMobSpawnerLogic
{
    @Inject(method = "clientTick", at = @At("HEAD"), cancellable = true)
    private void cancelParticleRendering(World world, BlockPos pos, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_MOB_SPAWNER_MOB_RENDER.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
