package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.thrown.ThrownEnderpearlEntity;
import net.minecraft.entity.thrown.ThrownEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;

@Mixin(ThrownEntity.class)
public abstract class MixinThrownEntity extends Entity
{
    private MixinThrownEntity(EntityType<?> type, World world)
    {
        super(type, world);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/thrown/ThrownEntity;updatePosition(DDD)V"))
    private void chunkLoadNextChunk(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_ENDER_PEARL_CHUNK_LOADING.getBooleanValue() &&
            (((Object) this) instanceof ThrownEnderpearlEntity) &&
            this.getEntityWorld() instanceof ServerWorld)
        {
            MiscUtils.addEnderPearlChunkTicket(this);
        }
    }
}
