package tweakeroo.mixin;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;

import malilib.gui.util.GuiUtils;
import tweakeroo.config.Configs;
import tweakeroo.config.FeatureToggle;
import tweakeroo.input.KeyboardInputHandlerImpl;

@Mixin(MovementInputFromOptions.class)
public abstract class MixinMovementInputFromOptions extends MovementInput
{
    @Inject(method = "updatePlayerMoveState()V", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/util/MovementInputFromOptions;sneak:Z",
            ordinal = 0,
            shift = Shift.AFTER,
            opcode = Opcodes.PUTFIELD))
    private void customMovement(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_MOVEMENT_KEYS.getBooleanValue())
        {
            KeyboardInputHandlerImpl.INSTANCE.handleMovementKeys(this);
        }

        if (FeatureToggle.TWEAK_PERMANENT_SNEAK.getBooleanValue() &&
            (Configs.Generic.PERMANENT_SNEAK_ALLOW_IN_GUIS.getBooleanValue() || GuiUtils.getCurrentScreen() == null))
        {
            this.sneak = true;
        }
    }
}
