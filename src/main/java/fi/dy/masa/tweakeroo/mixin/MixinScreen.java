package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.gui.Screen;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;

@Mixin(Screen.class)
public abstract class MixinScreen
{
    @Inject(method = "renderTooltip(Lnet/minecraft/item/ItemStack;II)V", at = @At("RETURN"))
    private void onRenderToolTip(ItemStack stack, int x, int y, CallbackInfo ci)
    {
        if (stack.getItem() instanceof FilledMapItem)
        {
            if (FeatureToggle.TWEAK_MAP_PREVIEW.getBooleanValue())
            {
                fi.dy.masa.malilib.render.RenderUtils.renderMapPreview(stack, x, y, Configs.Generic.MAP_PREVIEW_SIZE.getIntegerValue());
            }
        }
        else if (FeatureToggle.TWEAK_SHULKERBOX_DISPLAY.getBooleanValue())
        {
            fi.dy.masa.malilib.render.RenderUtils.renderShulkerBoxPreview(stack, x, y, Configs.Generic.SHULKER_DISPLAY_BACKGROUND_COLOR.getBooleanValue());
        }
    }
}
