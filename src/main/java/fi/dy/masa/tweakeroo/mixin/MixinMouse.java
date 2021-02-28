package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(Mouse.class)
public class MixinMouse
{
    @Shadow @Final private MinecraftClient client;

    @ModifyVariable(method = "onMouseScroll", ordinal = 2, at = @At(value = "FIELD", ordinal = 0,
                    target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"))
    private double applyHorizontalScroll(double delta, long window, double horizontal, double vertical)
    {
        if (Configs.Fixes.MAC_HORIZONTAL_SCROLL.getBooleanValue() && MinecraftClient.IS_SYSTEM_MAC && vertical == 0)
        {
            vertical = horizontal;
            delta = (this.client.options.discreteMouseScroll ? Math.signum(vertical) : vertical) * this.client.options.mouseWheelSensitivity;
        }

        return delta;
    }
}
