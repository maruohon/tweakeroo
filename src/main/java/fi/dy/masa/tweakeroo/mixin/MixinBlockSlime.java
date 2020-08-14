package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import fi.dy.masa.tweakeroo.config.DisableToggle;

@Mixin(BlockSlime.class)
public abstract class MixinBlockSlime extends BlockBreakable
{
    protected MixinBlockSlime(Material materialIn, boolean ignoreSimilarityIn)
    {
        super(materialIn, ignoreSimilarityIn);
    }

    @Inject(method = "onEntityWalk", at = @At("HEAD"), cancellable = true)
    private void onEntityWalkOnSlime(World worldIn, BlockPos pos, Entity entityIn, CallbackInfo ci)
    {
        if (DisableToggle.DISABLE_SLIME_BLOCK_SLOWDOWN.getBooleanValue() && entityIn instanceof EntityPlayer)
        {
            super.onEntityWalk(worldIn, pos, entityIn);
            ci.cancel();
        }
    }
}
