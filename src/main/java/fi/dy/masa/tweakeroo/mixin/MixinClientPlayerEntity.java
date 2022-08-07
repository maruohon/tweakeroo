package fi.dy.masa.tweakeroo.mixin;

import javax.annotation.Nullable;
import com.mojang.authlib.GameProfile;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.Hand;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.CameraUtils;
import fi.dy.masa.tweakeroo.util.DummyMovementInput;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity
{
    @Shadow public Input input;
    @Shadow protected int ticksLeftToDoubleTapSprint;

    @Shadow public float lastNauseaStrength;
    @Shadow public float nextNauseaStrength;

    private final DummyMovementInput dummyMovementInput = new DummyMovementInput(null);
    private Input realInput;
    private float realNextNauseaStrength;

    private MixinClientPlayerEntity(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey)
    {
        super(world, profile, publicKey);
    }

    @Redirect(method = "updateNausea()V",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/gui/screen/Screen;shouldPause()Z"))
    private boolean onDoesGuiPauseGame(Screen gui)
    {
        // Spoof the return value to prevent entering the if block
        if (Configs.Disable.DISABLE_PORTAL_GUI_CLOSING.getBooleanValue())
        {
            return true;
        }

        return gui.shouldPause();
    }

    @Inject(method = "updateNausea", at = @At("HEAD"))
    private void disableNauseaEffectPre(CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_NAUSEA_EFFECT.getBooleanValue())
        {
            this.nextNauseaStrength = this.realNextNauseaStrength;
        }
    }

    @Inject(method = "updateNausea", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;tickPortalCooldown()V"))
    private void disableNauseaEffectPost(CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_NAUSEA_EFFECT.getBooleanValue())
        {
            // This is used to set the value to the correct value for the duration of the
            // updateNausea() method, so that the portal sound plays correctly only once.
            this.realNextNauseaStrength = this.nextNauseaStrength;
            this.lastNauseaStrength = 0.0f;
            this.nextNauseaStrength = 0.0f;
        }
    }

    @Inject(method = "tickMovement", at = @At(value = "FIELD",
                target = "Lnet/minecraft/entity/player/PlayerAbilities;allowFlying:Z", ordinal = 1))
    private void overrideSprint(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_PERMANENT_SPRINT.getBooleanValue() &&
            ! this.isSprinting() && ! this.isUsingItem() && this.input.movementForward >= 0.8F &&
            (this.getHungerManager().getFoodLevel() > 6.0F || this.getAbilities().allowFlying) &&
            ! this.hasStatusEffect(StatusEffects.BLINDNESS) && ! this.isTouchingWater())
        {
            this.setSprinting(true);
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "FIELD",
                target = "Lnet/minecraft/client/network/ClientPlayerEntity;horizontalCollision:Z"))
    private boolean overrideCollidedHorizontally(ClientPlayerEntity player)
    {
        if (Configs.Disable.DISABLE_WALL_UNSPRINT.getBooleanValue())
        {
            return false;
        }

        return player.horizontalCollision;
    }

    @Inject(method = "tickMovement",
            slice = @Slice(from = @At(value = "INVOKE",
                                      target = "Lnet/minecraft/client/network/ClientPlayerEntity;getHungerManager()" +
                                               "Lnet/minecraft/entity/player/HungerManager;")),
            at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 0, shift = At.Shift.AFTER,
                     target = "Lnet/minecraft/client/network/ClientPlayerEntity;ticksLeftToDoubleTapSprint:I"))
    private void disableDoubleTapSprint(CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_DOUBLE_TAP_SPRINT.getBooleanValue())
        {
            this.ticksLeftToDoubleTapSprint = 0;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void disableMovementInputsPre(CallbackInfo ci)
    {
        if (CameraUtils.shouldPreventPlayerMovement())
        {
            this.realInput = this.input;
            this.input = this.dummyMovementInput;
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void disableMovementInputsPost(CallbackInfo ci)
    {
        if (this.realInput != null)
        {
            this.input = this.realInput;
            this.realInput = null;
        }
    }

    @Inject(method = "isCamera", at = @At("HEAD"), cancellable = true)
    private void allowPlayerMovementInFreeCameraMode(CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && CameraEntity.originalCameraWasPlayer())
        {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "swingHand", at = @At("HEAD"), cancellable = true)
    private void preventHandSwing(Hand hand, CallbackInfo ci)
    {
        if (CameraUtils.shouldPreventPlayerInputs())
        {
            ci.cancel();
        }
    }
}
