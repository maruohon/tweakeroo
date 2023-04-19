package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import tweakeroo.config.Configs;
import tweakeroo.config.FeatureToggle;
import tweakeroo.tweaks.PlacementTweaks;
import tweakeroo.util.CameraUtils;
import tweakeroo.util.InventoryUtils;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP
{
    @Shadow @Final private Minecraft mc;

    @Inject(method = "processRightClick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;syncCurrentPlayItem()V"),
            cancellable = true)
    private void onProcessRightClickFirst(EntityPlayer player, World worldIn, EnumHand hand, CallbackInfoReturnable<EnumActionResult> cir)
    {
        if (CameraUtils.shouldPreventPlayerInputs() ||
            PlacementTweaks.onProcessRightClickPre(player, hand))
        {
            cir.setReturnValue(EnumActionResult.PASS);
        }
    }

    /*
    @Inject(method = "processRightClick", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/item/ItemStack;useItemRightClick(" +
                         "Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;" +
                         ")Lnet/minecraft/util/ActionResult;"),
            cancellable = true)
    private void onProcessRightClickPre(EntityPlayer player, World worldIn, EnumHand hand, CallbackInfoReturnable<EnumActionResult> cir)
    {
        if (PlacementTweaks.onProcessRightClickPre(player, hand))
        {
            cir.setReturnValue(EnumActionResult.PASS);
            cir.cancel();
        }
    }
    */

    @Inject(method = "processRightClick",
            slice = @Slice(from = @At(value = "INVOKE", 
                                      target = "Lnet/minecraft/item/ItemStack;useItemRightClick(" +
                                               "Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;" +
                                               ")Lnet/minecraft/util/ActionResult;")),
            at = @At("RETURN"))
    private void onProcessRightClickPost(EntityPlayer player, World worldIn, EnumHand hand, CallbackInfoReturnable<EnumActionResult> cir)
    {
        PlacementTweaks.onProcessRightClickPost(player, hand);
    }

    @Inject(method = "clickBlock",
            slice = @Slice(from = @At(value = "FIELD", ordinal = 0,
                                      target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;isHittingBlock:Z")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getBlockState(" +
                                                "Lnet/minecraft/util/math/BlockPos;" +
                                                ")Lnet/minecraft/block/state/IBlockState;", ordinal = 0))
    private void onClickBlockPre(BlockPos pos, EnumFacing face, CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_TOOL_SWITCH.getBooleanValue())
        {
            InventoryUtils.trySwitchToEffectiveTool(pos);
        }

        PlacementTweaks.cacheStackInHand(EnumHand.MAIN_HAND);
    }

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void preventEntityAttacksInFreeCameraMode(CallbackInfo ci)
    {
        if (CameraUtils.shouldPreventPlayerInputs())
        {
            ci.cancel();
        }
    }

    @Inject(method = "interactWithEntity(" +
                     "Lnet/minecraft/entity/player/EntityPlayer;" +
                     "Lnet/minecraft/entity/Entity;" +
                     "Lnet/minecraft/util/EnumHand;" +
                     ")Lnet/minecraft/util/EnumActionResult;",
            at = @At("HEAD"),
            cancellable = true)
    private void onRightClickMouseOnEntityPre1(EntityPlayer player, Entity target, EnumHand hand, CallbackInfoReturnable<EnumActionResult> cir)
    {
        if (CameraUtils.shouldPreventPlayerInputs() ||
            PlacementTweaks.onProcessRightClickPre(player, hand))
        {
            cir.setReturnValue(EnumActionResult.PASS);
        }
    }

    @Inject(method = "interactWithEntity(" +
                     "Lnet/minecraft/entity/player/EntityPlayer;" +
                     "Lnet/minecraft/entity/Entity;" +
                     "Lnet/minecraft/util/math/RayTraceResult;" +
                     "Lnet/minecraft/util/EnumHand;" +
                     ")Lnet/minecraft/util/EnumActionResult;",
            at = @At("HEAD"),
            cancellable = true)
    private void onRightClickMouseOnEntityPre2(EntityPlayer player, Entity target, RayTraceResult ray, EnumHand hand, CallbackInfoReturnable<EnumActionResult> cir)
    {
        if (CameraUtils.shouldPreventPlayerInputs() ||
            PlacementTweaks.onProcessRightClickPre(player, hand))
        {
            cir.setReturnValue(EnumActionResult.PASS);
        }
    }

    @Inject(method = "clickBlock", at = @At("HEAD"), cancellable = true)
    private void handleBreakingRestriction1(BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> cir)
    {
        if (CameraUtils.shouldPreventPlayerInputs() ||
            PlacementTweaks.isPositionAllowedByBreakingRestriction(pos, side) == false)
        {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "onPlayerDamageBlock", at = @At("HEAD"), cancellable = true)
    private void handleBreakingRestriction2(BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> cir)
    {
        if (CameraUtils.shouldPreventPlayerInputs() ||
            PlacementTweaks.isPositionAllowedByBreakingRestriction(pos, side) == false)
        {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getBlockReachDistance", at = @At("HEAD"), cancellable = true)
    private void overrideReachDistance(CallbackInfoReturnable<Float> cir)
    {
        if (FeatureToggle.TWEAK_BLOCK_REACH_OVERRIDE.getBooleanValue())
        {
            cir.setReturnValue((float) Configs.Generic.BLOCK_REACH_DISTANCE.getDoubleValue());
        }
    }

    @Inject(method = "extendedReach", at = @At("HEAD"), cancellable = true)
    private void overrideExtendedReach(CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_BLOCK_REACH_OVERRIDE.getBooleanValue())
        {
            cir.setReturnValue(false);
        }
    }
}
