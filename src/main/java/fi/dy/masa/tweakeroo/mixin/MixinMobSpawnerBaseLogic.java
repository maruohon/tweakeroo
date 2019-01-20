package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.world.World;

@Mixin(MobSpawnerBaseLogic.class)
public abstract class MixinMobSpawnerBaseLogic
{
    @Shadow
    public abstract World getSpawnerWorld();

    @Inject(method = "updateSpawner", at = @At("HEAD"), cancellable = true)
    private void cancelParticleRendering(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_NO_MOB_SPAWNER_MOB_RENDER.getBooleanValue() && this.getSpawnerWorld().isRemote)
        {
            ci.cancel();
        }
    }
}
