package fi.dy.masa.tweakeroo.mixin;

import java.util.Map;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

@Mixin(Chunk.class)
public abstract class MixinChunk
{
    @Shadow
    @Final
    private World world;

    @Shadow
    @Final
    private Map<BlockPos, TileEntity> tileEntities;

    @Inject(method = "propagateSkylightOcclusion", at = @At("HEAD"), cancellable = true)
    private void onPropagateSkylightOcclusion(int x, int z, CallbackInfo ci)
    {
        if (this.world.isRemote && Configs.Disable.DISABLE_LIGHT_UPDATES_ALL.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "relightBlock", at = @At("HEAD"), cancellable = true)
    private void onRelightBlock(int x, int y, int z, IBlockState state, CallbackInfo ci)
    {
        if (this.world.isRemote && Configs.Disable.DISABLE_LIGHT_UPDATES_ALL.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "generateSkylightMap", at = @At("HEAD"), cancellable = true)
    private void onGenerateSkylightMap(CallbackInfo ci)
    {
        if (this.world.isRemote && Configs.Disable.DISABLE_LIGHT_UPDATES_ALL.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "read", at = @At("HEAD"))
    private void chunkPacketTileEntityCleanup(PacketBuffer buf, int availableSections, boolean fullChunk, CallbackInfo ci)
    {
        if (Configs.Fixes.TILE_LEAK_FIX.getBooleanValue())
        {
            if (fullChunk)
            {
                for (TileEntity te : this.tileEntities.values())
                {
                    this.world.markTileEntityForRemoval(te);
                }
            }
            else
            {
                for (BlockPos pos : this.tileEntities.keySet())
                {
                    int cy = pos.getY() >> 4;

                    if ((availableSections & (1 << cy)) != 0)
                    {
                        this.world.markTileEntityForRemoval(this.tileEntities.get(pos));
                    }
                }
            }
        }
    }
}
