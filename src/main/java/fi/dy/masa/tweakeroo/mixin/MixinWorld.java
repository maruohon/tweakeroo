package fi.dy.masa.tweakeroo.mixin;

import java.util.HashSet;
import java.util.List;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;

@Mixin(World.class)
public abstract class MixinWorld
{
    @Shadow
    @Final
    public List<BlockEntity> blockEntities;

    @Shadow
    @Final
    public List<BlockEntity> tickingBlockEntities;

    @Shadow
    @Final
    private List<BlockEntity> unloadedBlockEntities;

    @Inject(method = "tickBlockEntities",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;unloadedBlockEntities:Ljava/util/List;", ordinal = 0))
    private void optimizedTileEntityRemoval(CallbackInfo ci)
    {
        if (Configs.Fixes.TILE_UNLOAD_OPTIMIZATION.getBooleanValue())
        {
            if (this.unloadedBlockEntities.isEmpty() == false)
            {
                HashSet<BlockEntity> remove = new HashSet<>();
                remove.addAll(this.unloadedBlockEntities);

                this.tickingBlockEntities.removeAll(remove);
                this.blockEntities.removeAll(remove);
                this.unloadedBlockEntities.clear();
            }
        }
    }
}
