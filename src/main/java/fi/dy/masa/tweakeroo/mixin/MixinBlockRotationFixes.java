package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin({ BlockDispenser.class, BlockFurnace.class, BlockPistonBase.class})
public abstract class MixinBlockRotationFixes
{
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
