package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(BlockFurnace.class)
public abstract class MixinBlockFurnace
{
    @Inject(method = "setDefaultFacing", at = @At("HEAD"), cancellable = true)
    private void fixRotation(CallbackInfo ci)
    {
        if (Configs.Generic.CLIENT_PLACEMENT_ROTATION.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Redirect(method = "onBlockPlacedBy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(" +
                                                                              "Lnet/minecraft/util/math/BlockPos;" +
                                                                              "Lnet/minecraft/block/state/IBlockState;I)Z"))
    private boolean fixRotation(World world, BlockPos pos, IBlockState state, int flags,
                            World worldIn, BlockPos posIn, IBlockState stateIn, EntityLivingBase placerIn, ItemStack stackIn)
    {
        if (Configs.Generic.CLIENT_PLACEMENT_ROTATION.getBooleanValue())
        {
            // Use the original state from getStateForPlacement() instead of
            // overriding the facing with one calculated from the placer
            return world.setBlockState(pos, stateIn, flags);
        }

        return world.setBlockState(pos, state, flags);
    }
}
