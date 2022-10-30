package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.Window;

import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(Window.class)
public abstract class MixinWindow
{
    @Shadow public abstract int getWidth();
    @Shadow public abstract int getHeight();

    @Inject(method = "getScaleFactor", at = @At("HEAD"), cancellable = true)
    private void tweakeroo_customGuiScaleGetScale(CallbackInfoReturnable<Double> cir)
    {
        if (FeatureToggle.TWEAK_CUSTOM_INVENTORY_GUI_SCALE.getBooleanValue() &&
            MinecraftClient.getInstance().currentScreen instanceof HandledScreen<?>)
        {
            int scale = Configs.Generic.CUSTOM_INVENTORY_GUI_SCALE.getIntegerValue();

            if (scale > 0)
            {
                cir.setReturnValue((double) scale);
            }
        }
    }

    @Inject(method = "getScaledWidth", at = @At("HEAD"), cancellable = true)
    private void tweakeroo_customGuiScaleGetWidth(CallbackInfoReturnable<Integer> cir)
    {
        if (FeatureToggle.TWEAK_CUSTOM_INVENTORY_GUI_SCALE.getBooleanValue() &&
            MinecraftClient.getInstance().currentScreen instanceof HandledScreen<?>)
        {
            int scale = Configs.Generic.CUSTOM_INVENTORY_GUI_SCALE.getIntegerValue();

            if (scale > 0)
            {
                cir.setReturnValue((int) Math.ceil((double) this.getWidth() / scale));
            }
        }
    }

    @Inject(method = "getScaledHeight", at = @At("HEAD"), cancellable = true)
    private void tweakeroo_customGuiScaleGetHeight(CallbackInfoReturnable<Integer> cir)
    {
        if (FeatureToggle.TWEAK_CUSTOM_INVENTORY_GUI_SCALE.getBooleanValue() &&
            MinecraftClient.getInstance().currentScreen instanceof HandledScreen<?>)
        {
            int scale = Configs.Generic.CUSTOM_INVENTORY_GUI_SCALE.getIntegerValue();

            if (scale > 0)
            {
                cir.setReturnValue((int) Math.ceil((double) this.getHeight() / scale));
            }
        }
    }
}
