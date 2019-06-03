package fi.dy.masa.tweakeroo.mixin;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

@Mixin(World.class)
public abstract class MixinWorld
{
    @Shadow
    @Final
    public List<TileEntity> loadedTileEntityList;

    @Shadow
    @Final
    public List<TileEntity> tickableTileEntities;

    @Shadow
    @Final
    private List<TileEntity> tileEntitiesToBeRemoved;

    @Inject(method = "tickEntity(Lnet/minecraft/entity/Entity;Z)V", at = @At("HEAD"), cancellable = true)
    private void preventEntityTicking(Entity entityIn, boolean forceUpdate, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_ENTITY_TICKING.getBooleanValue() && (entityIn instanceof EntityPlayer) == false)
        {
            ci.cancel();
        }
    }

    @Redirect(method = "tickEntities",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/world/World;processingLoadedTiles:Z", ordinal = 0)),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;isRemoved()Z", ordinal = 0))
    private boolean preventTileEntityTicking(TileEntity te)
    {
        if (Configs.Disable.DISABLE_TILE_ENTITY_TICKING.getBooleanValue())
        {
            return true;
        }

        return te.isRemoved();
    }

    @Inject(method = "tickEntities",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;tileEntitiesToBeRemoved:Ljava/util/List;", ordinal = 0))
    private void optimizedTileEntityRemoval(CallbackInfo ci)
    {
        if (Configs.Fixes.TILE_UNLOAD_OPTIMIZATION.getBooleanValue())
        {
            if (this.tileEntitiesToBeRemoved.isEmpty() == false)
            {
                Set<TileEntity> remove = Collections.newSetFromMap(new IdentityHashMap<>());
                remove.addAll(this.tileEntitiesToBeRemoved);
                this.tickableTileEntities.removeAll(remove);
                this.loadedTileEntityList.removeAll(remove);
                this.tileEntitiesToBeRemoved.clear();
            }
        }
    }
}
