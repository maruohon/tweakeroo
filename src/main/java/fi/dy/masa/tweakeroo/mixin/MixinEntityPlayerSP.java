package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.DisableToggle;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.CameraUtils;
import fi.dy.masa.tweakeroo.util.DummyMovementInput;

@Mixin(net.minecraft.client.entity.EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends net.minecraft.client.entity.AbstractClientPlayer
{
    public MixinEntityPlayerSP(net.minecraft.world.World worldIn, com.mojang.authlib.GameProfile playerProfile)
    {
        super(worldIn, playerProfile);
    }

    @Shadow public net.minecraft.util.MovementInput movementInput;
    @Shadow protected int sprintToggleTimer;

    private final DummyMovementInput dummyMovementInput = new DummyMovementInput(null);
    private net.minecraft.util.MovementInput realInput;

    @Redirect(method = "onLivingUpdate()V", require = 0,
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/gui/GuiScreen;doesGuiPauseGame()Z"))
    private boolean onDoesGuiPauseGame(net.minecraft.client.gui.GuiScreen gui)
    {
        // Spoof the return value to prevent entering the if block
        if (DisableToggle.DISABLE_PORTAL_GUI_CLOSING.getBooleanValue())
        {
            return true;
        }

        return gui.doesGuiPauseGame();
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/network/NetHandlerPlayClient;sendPacket(Lnet/minecraft/network/Packet;)V"))
    private void fixElytraDeployment(CallbackInfo ci)
    {
        if (Configs.Fixes.ELYTRA_FIX.getBooleanValue() && this.isInWater() == false)
        {
            this.setFlag(7, true);
        }
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "FIELD",
            target = "Lnet/minecraft/entity/player/PlayerCapabilities;allowFlying:Z", ordinal = 1))
    private void overrideSprint(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_PERMANENT_SPRINT.getBooleanValue() &&
            ! this.isSprinting() && ! this.isHandActive() && this.movementInput.moveForward >= 0.8F &&
            (this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying) &&
            ! this.isPotionActive(net.minecraft.init.MobEffects.BLINDNESS))
        {
            this.setSprinting(true);
        }
    }

    @Redirect(method = "onLivingUpdate", require = 0,
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/client/entity/EntityPlayerSP;collidedHorizontally:Z"))
    private boolean overrideCollidedHorizontally(net.minecraft.client.entity.EntityPlayerSP player)
    {
        if (DisableToggle.DISABLE_WALL_UNSPRINT.getBooleanValue())
        {
            return false;
        }

        return player.collidedHorizontally;
    }

    @Inject(method = "onLivingUpdate",
            slice = @Slice(from = @At(value = "INVOKE",
                                      target = "Lnet/minecraft/client/entity/EntityPlayerSP;getFoodStats()Lnet/minecraft/util/FoodStats;")),
            at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 0, shift = At.Shift.AFTER,
                     target = "Lnet/minecraft/client/entity/EntityPlayerSP;sprintToggleTimer:I"))
    private void disableDoubleTapSprint(CallbackInfo ci)
    {
        if (DisableToggle.DISABLE_DOUBLE_TAP_SPRINT.getBooleanValue())
        {
            this.sprintToggleTimer = 0;
        }
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void disableMovementInputsPre(CallbackInfo ci)
    {
        if (CameraUtils.shouldPreventPlayerMovement())
        {
            this.realInput = this.movementInput;
            this.movementInput = this.dummyMovementInput;
        }
    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    private void disableMovementInputsPost(CallbackInfo ci)
    {
        if (this.realInput != null)
        {
            this.movementInput = this.realInput;
            this.realInput = null;
        }
    }

    @Inject(method = "isCurrentViewEntity", at = @At("HEAD"), cancellable = true)
    private void allowPlayerMovementInFreeCameraMode(CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && Configs.Generic.FREE_CAMERA_PLAYER_MOVEMENT.getBooleanValue())
        {
            cir.setReturnValue(true);
        }
    }
}
