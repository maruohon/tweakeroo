package tweakeroo.mixin;

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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import malilib.util.game.wrap.GameUtils;
import tweakeroo.config.Configs;
import tweakeroo.config.DisableToggle;
import tweakeroo.config.FeatureToggle;

@Mixin(net.minecraft.world.World.class)
public abstract class MixinWorld
{
    @Shadow @Final public List<TileEntity> loadedTileEntityList;
    @Shadow @Final public List<TileEntity> tickableTileEntities;
    @Shadow @Final private List<TileEntity> tileEntitiesToBeRemoved;

    @Inject(method = "getFogColor", at = @At("HEAD"), cancellable = true)
    private void adjustFogColor(float partialTicks, CallbackInfoReturnable<Vec3d> cir)
    {
        if (FeatureToggle.TWEAK_MATCHING_SKY_FOG.getBooleanValue())
        {
            World world = (World) (Object) this;

            if (world.provider.hasSkyLight() && world.isRaining() == false)
            {
                Minecraft mc = GameUtils.getClient();
                cir.setReturnValue(world.getSkyColor(mc.getRenderViewEntity(), mc.getRenderPartialTicks()));
            }
        }
    }

    @Inject(method = "updateEntityWithOptionalForce", at = @At("HEAD"), cancellable = true)
    private void preventEntityTicking(Entity entityIn, boolean forceUpdate, CallbackInfo ci)
    {
        if (DisableToggle.DISABLE_ENTITY_TICKING.getBooleanValue() &&
            (entityIn instanceof EntityPlayer) == false)
        {
            ci.cancel();
        }
    }

    @Redirect(method = "updateEntities",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/world/World;processingLoadedTiles:Z", ordinal = 0)),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;isInvalid()Z", ordinal = 0))
    private boolean preventTileEntityTicking(TileEntity te)
    {
        if (DisableToggle.DISABLE_TILE_ENTITY_TICKING.getBooleanValue())
        {
            return true;
        }

        return te.isInvalid();
    }


    @Inject(method = "updateEntities", at = @At(value = "FIELD", ordinal = 0,
                target = "Lnet/minecraft/world/World;tileEntitiesToBeRemoved:Ljava/util/List;"))
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
