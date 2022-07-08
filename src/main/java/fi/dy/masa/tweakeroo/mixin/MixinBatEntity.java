package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(BatEntity.class)
public abstract class MixinBatEntity
{
    @Inject(method = "canSpawn", at = @At("HEAD"), cancellable = true)
    private static void tweakeroo_disableBatSpawning(EntityType<BatEntity> type,
                                                     WorldAccess world,
                                                     SpawnReason spawnReason,
                                                     BlockPos pos,
                                                     Random random,
                                                     CallbackInfoReturnable<Boolean> cir)
    {
        if (Configs.Disable.DISABLE_BAT_SPAWNING.getBooleanValue())
        {
            cir.setReturnValue(false);
        }
    }
}
