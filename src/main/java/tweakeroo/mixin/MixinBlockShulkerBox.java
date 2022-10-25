package tweakeroo.mixin;

import java.util.List;
import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import tweakeroo.config.DisableToggle;

@Mixin(net.minecraft.block.BlockShulkerBox.class)
public abstract class MixinBlockShulkerBox extends net.minecraft.block.Block
{
    protected MixinBlockShulkerBox(net.minecraft.block.material.Material materialIn)
    {
        super(materialIn);
    }

    @Inject(method = "addInformation", at = @At("HEAD"), cancellable = true)
    private void removeContentTooltip(
            net.minecraft.item.ItemStack stack,
            @Nullable net.minecraft.world.World worldIn,
            List<String> tooltip,
            net.minecraft.client.util.ITooltipFlag flagIn, CallbackInfo ci)
    {
        if (DisableToggle.DISABLE_SHULKER_BOX_TOOLTIP.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
