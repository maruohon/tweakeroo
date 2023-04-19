package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import tweakeroo.config.Configs;
import tweakeroo.tweaks.PlacementHandler;
import tweakeroo.tweaks.PlacementHandler.UseContext;

@Mixin({ ItemBlock.class, ItemBlockSpecial.class })
public abstract class MixinItemBlock
{
    @Redirect(method = "onItemUse", at = @At(value = "INVOKE",
                                             target = "Lnet/minecraft/block/Block;getStateForPlacement(" +
                                                    "Lnet/minecraft/world/World;" +
                                                    "Lnet/minecraft/util/math/BlockPos;" +
                                                    "Lnet/minecraft/util/EnumFacing;" +
                                                    "FFFI" +
                                                    "Lnet/minecraft/entity/EntityLivingBase;)" +
                                                    "Lnet/minecraft/block/state/IBlockState;"), require = 0)
    private IBlockState modifyPlacementState(Block block, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer,
            EntityPlayer playerIn, World worldIn, BlockPos posIn, EnumHand handIn, EnumFacing facingIn, float hitXIn, float hitYIn, float hitZIn)
    {
        IBlockState stateOriginal = block.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);

        if (Configs.Generic.CLIENT_PLACEMENT_ROTATION.getBooleanValue())
        {
            UseContext context = UseContext.of(world, pos, facing, new Vec3d(hitX, hitY, hitZ), placer, handIn);
            return PlacementHandler.getStateForPlacement(stateOriginal, context);
        }

        return stateOriginal;
    }
}
