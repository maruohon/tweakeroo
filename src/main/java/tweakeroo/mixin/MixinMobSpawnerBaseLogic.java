package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.world.World;

import tweakeroo.config.DisableToggle;

@Mixin(MobSpawnerBaseLogic.class)
public abstract class MixinMobSpawnerBaseLogic
{
    @Shadow
    public abstract World getSpawnerWorld();

    @Inject(method = "updateSpawner", at = @At("HEAD"), cancellable = true)
    private void cancelParticleRendering(CallbackInfo ci)
    {
        if (DisableToggle.DISABLE_MOB_SPAWNER_MOB_RENDER.getBooleanValue() && this.getSpawnerWorld().isRemote)
        {
            ci.cancel();
        }
    }
}
