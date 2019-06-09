package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.event.InputHandler;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;

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
            InputHandler.getInstance().handleMovementKeys(this);
        }

        if (FeatureToggle.TWEAK_PERMANENT_SNEAK.getBooleanValue() &&
            (Configs.Generic.PERMANENT_SNEAK_ALLOW_IN_GUIS.getBooleanValue() || GuiUtils.getCurrentScreen() == null))
        {
            this.sneak = true;
        }
    }
}
