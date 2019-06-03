package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.BlockSlime;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(BlockSlime.class)
public abstract class MixinBlockSlime extends BlockBreakable
{
    public MixinBlockSlime(Block.Properties properties)
    {
        super(properties);
    }

    @Inject(method = "onEntityWalk", at = @At("HEAD"), cancellable = true)
    private void onEntityWalkOnSlime(World worldIn, BlockPos pos, Entity entityIn, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_SLIME_BLOCK_SLOWDOWN.getBooleanValue() && entityIn instanceof EntityPlayer)
        {
            super.onEntityWalk(worldIn, pos, entityIn);
            ci.cancel();
        }
    }
}
