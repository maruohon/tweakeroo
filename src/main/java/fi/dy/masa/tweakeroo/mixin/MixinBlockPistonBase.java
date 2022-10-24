package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(value = BlockPistonBase.class, priority = 1001)
public abstract class MixinBlockPistonBase extends BlockDirectional
{
    protected MixinBlockPistonBase(Material materialIn)
    {
        super(materialIn);
    }

    @Inject(method = "onBlockPlacedBy", at = @At("HEAD"), cancellable = true)
    private void fixRotationState(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack, CallbackInfo ci)
    {
        if (Configs.Generic.CLIENT_PLACEMENT_ROTATION.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
