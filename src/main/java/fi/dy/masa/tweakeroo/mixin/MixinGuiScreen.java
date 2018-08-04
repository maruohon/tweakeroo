package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;

@Mixin(GuiScreen.class)
public class MixinGuiScreen
{
    @Inject(method = "renderToolTip", at = @At("RETURN"))
    private void onRenderToolTip(ItemStack stack, int x, int y, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_MAP_PREVIEW.getBooleanValue() && stack.getItem() instanceof ItemMap)
        {
            RenderUtils.renderMapPreview(stack, x, y);
        }
    }
}
