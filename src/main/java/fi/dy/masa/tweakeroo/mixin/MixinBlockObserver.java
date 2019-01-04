package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(BlockObserver.class)
public abstract class MixinBlockObserver
{
    @Inject(method = "onBlockAdded", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/BlockObserver;startSignal(" +
            "Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"), cancellable = true)
    private void preventPlacementTrigger(World worldIn, BlockPos pos, IBlockState state, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_OBSERVER_PLACE_NO_UPDATE.getBooleanValue() || FeatureToggle.TWEAK_OBSERVER_DISABLE.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "observedNeighborChanged", at = @At("HEAD"), cancellable = true)
    private void preventTriggering(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_OBSERVER_DISABLE.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
