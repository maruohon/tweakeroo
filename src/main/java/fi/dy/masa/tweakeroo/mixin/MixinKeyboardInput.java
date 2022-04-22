package fi.dy.masa.tweakeroo.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.event.InputHandler;

@Mixin(KeyboardInput.class)
public abstract class MixinKeyboardInput extends Input
{
    @Inject(method = "tick(ZF)V", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/input/KeyboardInput;sneaking:Z",
            ordinal = 0,
            shift = Shift.AFTER,
            opcode = Opcodes.PUTFIELD))
    private void customMovement(boolean val1, float f, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_MOVEMENT_KEYS.getBooleanValue())
        {
            InputHandler.getInstance().handleMovementKeys(this);
        }

        if (FeatureToggle.TWEAK_PERMANENT_SNEAK.getBooleanValue() &&
            (Configs.Generic.PERMANENT_SNEAK_ALLOW_IN_GUIS.getBooleanValue() || GuiUtils.getCurrentScreen() == null))
        {
            this.sneaking = true;
        }
    }
}
