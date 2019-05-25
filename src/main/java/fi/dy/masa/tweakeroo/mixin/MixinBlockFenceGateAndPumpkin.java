package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin({ BlockFenceGate.class, BlockPumpkin.class })
public abstract class MixinBlockFenceGateAndPumpkin extends BlockHorizontal
{
    protected MixinBlockFenceGateAndPumpkin(Material materialIn)
    {
        super(materialIn);
    }

    @Inject(method = "canPlaceBlockAt", at = @At("HEAD"), cancellable = true)
    private void allowRelaxedBlockPlacement(World world, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_RELAXED_BLOCK_PLACEMENT.getBooleanValue())
        {
            cir.setReturnValue(super.canPlaceBlockAt(world, pos));
        }
    }
}
