package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(value = BlockObserver.class, priority = 1001)
public abstract class MixinBlockObserver extends BlockDirectional
{
    protected MixinBlockObserver(Material materialIn)
    {
        super(materialIn);
    }

    @Inject(method = "onBlockAdded", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/BlockObserver;startSignal(" +
            "Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"), cancellable = true)
    private void preventTrigger(World worldIn, BlockPos pos, IBlockState state, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_OBSERVER_DISABLE.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "observedNeighborChanged", at = @At("HEAD"), cancellable = true)
    private void preventTrigger(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_OBSERVER_DISABLE.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "getStateForPlacement", at = @At("HEAD"), cancellable = true)
    private void preventPlacementTrigger(World worldIn, BlockPos pos, EnumFacing facing,
            float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, CallbackInfoReturnable<IBlockState> cir)
    {
        if (FeatureToggle.TWEAK_OBSERVER_PLACE_NO_UPDATE.getBooleanValue() && worldIn.isRemote == false)
        {
            // Setting powered to true here causes the updateTick() method to immediately set it to false again,
            // called via the onBlockAdded() method from Chunk#setBlockState(), before the World#setBlockState()
            // does the neighbor updates with the powered state.
            cir.setReturnValue(this.getDefaultState()
                    .withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer).getOpposite())
                    .withProperty(BlockObserver.POWERED, true));
            cir.cancel();
        }
    }
}
