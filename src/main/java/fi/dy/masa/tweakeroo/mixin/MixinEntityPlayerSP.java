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
import com.mojang.authlib.GameProfile;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.MobEffects;
import net.minecraft.util.MovementInput;
import net.minecraft.world.World;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer
{
    @Shadow public MovementInput movementInput;
    @Shadow protected int sprintToggleTimer;

    @Shadow
    protected abstract boolean isCurrentViewEntity();

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile)
    {
        super(worldIn, playerProfile);
    }

    @Redirect(method = "livingTick()V",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/gui/GuiScreen;doesGuiPauseGame()Z"))
    private boolean onDoesGuiPauseGame(GuiScreen gui)
    {
        // Spoof the return value to prevent entering the if block
        if (Configs.Disable.DISABLE_PORTAL_GUI_CLOSING.getBooleanValue())
        {
            return true;
        }

        return gui.doesGuiPauseGame();
    }

    @Inject(method = "livingTick", at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.AFTER,
                target = "Lnet/minecraft/client/network/NetHandlerPlayClient;sendPacket(Lnet/minecraft/network/Packet;)V"))
    private void fixElytraDeployment(CallbackInfo ci)
    {
        if (Configs.Fixes.ELYTRA_FIX.getBooleanValue() && this.isInWater() == false)
        {
            this.setFlag(7, true);
        }
    }

    @Inject(method = "livingTick", at = @At(value = "FIELD",
                target = "Lnet/minecraft/entity/player/PlayerCapabilities;allowFlying:Z", ordinal = 1))
    private void overrideSprint(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_PERMANENT_SPRINT.getBooleanValue() &&
            ! this.isSprinting() && ! this.isHandActive() && this.movementInput.moveForward >= 0.8F &&
            (this.getFoodStats().getFoodLevel() > 6.0F || this.abilities.allowFlying) &&
            ! this.isPotionActive(MobEffects.BLINDNESS))
        {
            this.setSprinting(true);
        }
    }

    @Redirect(method = "livingTick", at = @At(value = "FIELD",
                target = "Lnet/minecraft/client/entity/EntityPlayerSP;collidedHorizontally:Z"))
    private boolean overrideCollidedHorizontally(EntityPlayerSP player)
    {
        if (Configs.Disable.DISABLE_WALL_UNSPRINT.getBooleanValue())
        {
            return false;
        }

        return player.collidedHorizontally;
    }

    @Inject(method = "livingTick",
            slice = @Slice(from = @At(value = "INVOKE",
                                      target = "Lnet/minecraft/client/entity/EntityPlayerSP;getFoodStats()Lnet/minecraft/util/FoodStats;")),
            at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 0, shift = At.Shift.AFTER,
                     target = "Lnet/minecraft/client/entity/EntityPlayerSP;sprintToggleTimer:I"))
    private void disableDoubleTapSprint(CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_DOUBLE_TAP_SPRINT.getBooleanValue())
        {
            this.sprintToggleTimer = 0;
        }
    }

    @Redirect(method = "livingTick", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/entity/EntityPlayerSP;isCurrentViewEntity()Z"))
    private boolean preventVerticalMotion(EntityPlayerSP player)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && FeatureToggle.TWEAK_FREE_CAMERA_MOTION.getBooleanValue())
        {
            return false;
        }

        return this.isCurrentViewEntity();
    }

    @Redirect(method = "livingTick", at = @At(
                value = "FIELD", ordinal = 1,
                target = "Lnet/minecraft/entity/player/PlayerCapabilities;allowFlying:Z"))
    private boolean preventFlyStateToggle(PlayerCapabilities abilities)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && FeatureToggle.TWEAK_FREE_CAMERA_MOTION.getBooleanValue())
        {
            return false;
        }

        return abilities.allowFlying;
    }

    @Inject(method = "updateEntityActionState", at = @At("RETURN"))
    private void preventJumpingInCameraMode(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && FeatureToggle.TWEAK_FREE_CAMERA_MOTION.getBooleanValue())
        {
            this.isJumping = false;
        }
    }

    @Inject(method = "isSneaking", at = @At("HEAD"), cancellable = true)
    private void preventSneakingInCameraMode(CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && FeatureToggle.TWEAK_FREE_CAMERA_MOTION.getBooleanValue())
        {
            cir.setReturnValue(false);
        }
    }

    @Override
    public boolean isSpectator()
    {
        return super.isSpectator() || MiscUtils.getFreeCameraSpectator();
    }
}
