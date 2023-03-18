package fi.dy.masa.tweakeroo.mixin;

import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.MiscTweaks;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import fi.dy.masa.tweakeroo.util.CameraUtils;
import fi.dy.masa.tweakeroo.util.InventoryUtils;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager
{
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "interactItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;syncSelectedSlot()V"),
            cancellable = true)
    private void onProcessRightClickFirst(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir)
    {
        if (CameraUtils.shouldPreventPlayerInputs() ||
            PlacementTweaks.onProcessRightClickPre(player, hand))
        {
            cir.setReturnValue(ActionResult.PASS);
            cir.cancel();
        }
    }

    @Inject(method = "method_41929",
            slice = @Slice(from = @At(value = "INVOKE",
                                      target = "Lnet/minecraft/item/ItemStack;use(" +
                                               "Lnet/minecraft/world/World;" +
                                               "Lnet/minecraft/entity/player/PlayerEntity;" +
                                               "Lnet/minecraft/util/Hand;" +
                                               ")Lnet/minecraft/util/TypedActionResult;")),
            at = @At("RETURN"))
    private void onProcessRightClickPost(Hand hand, PlayerEntity playerEntity,
                                         MutableObject<?> mutableObject, int sequence,
                                         CallbackInfoReturnable<Packet<?>> cir)
    {
        PlacementTweaks.onProcessRightClickPost(playerEntity, hand);
    }

    @Inject(method = "interactEntity(" +
                     "Lnet/minecraft/entity/player/PlayerEntity;" +
                     "Lnet/minecraft/entity/Entity;" +
                     "Lnet/minecraft/util/Hand;" +
                     ")Lnet/minecraft/util/ActionResult;",
            at = @At("HEAD"),
            cancellable = true)
    private void onRightClickMouseOnEntityPre1(PlayerEntity player, Entity target, Hand hand, CallbackInfoReturnable<ActionResult> cir)
    {
        if (CameraUtils.shouldPreventPlayerInputs() ||
            PlacementTweaks.onProcessRightClickPre(player, hand))
        {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    @Inject(method = "interactEntityAtLocation(" +
                     "Lnet/minecraft/entity/player/PlayerEntity;" +
                     "Lnet/minecraft/entity/Entity;" +
                     "Lnet/minecraft/util/hit/EntityHitResult;" +
                     "Lnet/minecraft/util/Hand;" +
                     ")Lnet/minecraft/util/ActionResult;",
            at = @At("HEAD"),
            cancellable = true)
    private void onRightClickMouseOnEntityPre2(PlayerEntity player, Entity target, EntityHitResult trace, Hand hand, CallbackInfoReturnable<ActionResult> cir)
    {
        if (CameraUtils.shouldPreventPlayerInputs() ||
            PlacementTweaks.onProcessRightClickPre(player, hand))
        {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void preventEntityAttacksInFreeCameraMode(PlayerEntity player, Entity target, CallbackInfo ci)
    {
        if (CameraUtils.shouldPreventPlayerInputs())
        {
            ci.cancel();
        }
        else if (FeatureToggle.TWEAK_ENTITY_TYPE_ATTACK_RESTRICTION.getBooleanValue() &&
                 MiscTweaks.isEntityAllowedByAttackingRestriction(target.getType()) == false)
        {
            ci.cancel();
        }
        else if (FeatureToggle.TWEAK_WEAPON_SWITCH.getBooleanValue())
        {
            InventoryUtils.trySwitchToWeapon(target);
        }
    }

    @Inject(method = "attackBlock",
            slice = @Slice(from = @At(value = "FIELD", ordinal = 0,
                                      target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;breakingBlock:Z")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getBlockState(" +
                                                "Lnet/minecraft/util/math/BlockPos;" +
                                                ")Lnet/minecraft/block/BlockState;", ordinal = 0))
    private void onClickBlockPre(BlockPos pos, Direction face, CallbackInfoReturnable<Boolean> cir)
    {
        if (this.client.player != null && this.client.world != null)
        {
            if (FeatureToggle.TWEAK_TOOL_SWITCH.getBooleanValue())
            {
                InventoryUtils.trySwitchToEffectiveTool(pos);
            }

            PlacementTweaks.cacheStackInHand(Hand.MAIN_HAND);
        }
    }

    @Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
    private void handleBreakingRestriction1(BlockPos pos, Direction side, CallbackInfoReturnable<Boolean> cir)
    {
        if (CameraUtils.shouldPreventPlayerInputs() ||
            PlacementTweaks.isPositionAllowedByBreakingRestriction(pos, side) == false)
        {
            cir.setReturnValue(false);
        }
        else
        {
            InventoryUtils.trySwapCurrentToolIfNearlyBroken();
        }
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"), cancellable = true) // MCP: onPlayerDamageBlock
    private void handleBreakingRestriction2(BlockPos pos, Direction side, CallbackInfoReturnable<Boolean> cir)
    {
        if (CameraUtils.shouldPreventPlayerInputs() ||
            PlacementTweaks.isPositionAllowedByBreakingRestriction(pos, side) == false)
        {
            cir.setReturnValue(true);
        }
        else
        {
            InventoryUtils.trySwapCurrentToolIfNearlyBroken();
        }
    }

    @Inject(method = "getReachDistance", at = @At("HEAD"), cancellable = true)
    private void overrideReachDistance(CallbackInfoReturnable<Float> cir)
    {
        if (FeatureToggle.TWEAK_BLOCK_REACH_OVERRIDE.getBooleanValue())
        {
            cir.setReturnValue((float) Configs.Generic.BLOCK_REACH_DISTANCE.getDoubleValue());
        }
    }

    @Inject(method = "hasExtendedReach", at = @At("HEAD"), cancellable = true)
    private void overrideExtendedReach(CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_BLOCK_REACH_OVERRIDE.getBooleanValue())
        {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "hasLimitedAttackSpeed", at = @At("HEAD"), cancellable = true)
    private void overrideLimitedAttackSpeed(CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_FAST_LEFT_CLICK.getBooleanValue())
        {
            cir.setReturnValue(false);
        }
    }
}
