package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlimeBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(SlimeBlock.class)
public abstract class MixinSlimeBlock extends TransparentBlock
{
    public MixinSlimeBlock(Block.Settings settings)
    {
        super(settings);
    }

    @Inject(method = "onSteppedOn", at = @At("HEAD"), cancellable = true)
    private void onEntityWalkOnSlime(World worldIn, BlockPos pos, BlockState state, Entity entityIn, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_SLIME_BLOCK_SLOWDOWN.getBooleanValue() && entityIn instanceof PlayerEntity)
        {
            super.onSteppedOn(worldIn, pos, state, entityIn);
            ci.cancel();
        }
    }
}
