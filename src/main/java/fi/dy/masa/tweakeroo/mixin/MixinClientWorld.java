package fi.dy.masa.tweakeroo.mixin;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World
{
    protected MixinClientWorld(MutableWorldProperties mutableWorldProperties, RegistryKey<World> registryKey, RegistryKey<DimensionType> registryKey2, DimensionType dimensionType, Supplier<Profiler> supplier, boolean bl, boolean bl2, long l)
    {
        super(mutableWorldProperties, registryKey, registryKey2, dimensionType, supplier, bl, bl2, l);
    }

    @Override
    public void tickEntity(Consumer<Entity> consumer, Entity entity)
    {
        if (Configs.Disable.DISABLE_CLIENT_ENTITY_UPDATES.getBooleanValue() == false || entity instanceof PlayerEntity)
        {
            super.tickEntity(consumer, entity);
        }
    }

    @Inject(method = "addEntitiesToChunk", at = @At("HEAD"), cancellable = true)
    private void fixChunkEntityLeak(WorldChunk chunk, CallbackInfo ci)
    {
        if (Configs.Fixes.CLIENT_CHUNK_ENTITY_DUPE.getBooleanValue())
        {
            for (int y = 0; y < 16; ++y)
            {
                // The chunk already has entities, which means it's a re-used existing chunk,
                // in such a case we don't want to add the from the world entities again, otherwise
                // they are basically duped within the Chunk.
                if (chunk.getEntitySectionArray()[y].size() > 0)
                {
                    ci.cancel();
                    return;
                }
            }
        }
    }
}
