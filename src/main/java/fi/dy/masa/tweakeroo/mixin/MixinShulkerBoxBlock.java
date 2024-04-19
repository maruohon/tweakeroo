package fi.dy.masa.tweakeroo.mixin;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(ShulkerBoxBlock.class)
public abstract class MixinShulkerBoxBlock
{
    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    private void removeVanillaTooltip(ItemStack stack,
                                      Item.TooltipContext context,
                                      List<Text> tooltip,
                                      TooltipType options,
                                      CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_SHULKER_BOX_TOOLTIP.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
