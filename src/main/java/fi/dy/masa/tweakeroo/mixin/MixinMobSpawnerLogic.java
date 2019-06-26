package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

@Mixin(MobSpawnerLogic.class)
public abstract class MixinMobSpawnerLogic
{
    @Shadow
    public abstract World getWorld();

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void cancelParticleRendering(CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_MOB_SPAWNER_MOB_RENDER.getBooleanValue() && this.getWorld().isClient)
        {
            ci.cancel();
        }
    }
}
