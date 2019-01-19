package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP
{
    @Inject(method = "processRightClick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;syncCurrentPlayItem()V"),
            cancellable = true)
    private void onProcessRightClickFirst(EntityPlayer player, World worldIn, EnumHand hand, CallbackInfoReturnable<EnumActionResult> cir)
    {
        if (PlacementTweaks.onProcessRightClickPre(player, hand))
        {
            cir.setReturnValue(EnumActionResult.PASS);
            cir.cancel();
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
        InventoryUtils.trySwitchToEffectiveTool(pos);
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
        if (PlacementTweaks.onProcessRightClickPre(player, hand))
        {
            cir.setReturnValue(EnumActionResult.PASS);
            cir.cancel();
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
        if (PlacementTweaks.onProcessRightClickPre(player, hand))
        {
            cir.setReturnValue(EnumActionResult.PASS);
            cir.cancel();
        }
    }

    @Inject(method = "getBlockReachDistance", at = @At("HEAD"), cancellable = true)
    private void overrideReachDistance(CallbackInfoReturnable<Float> cir)
    {
        if (FeatureToggle.TWEAK_BLOCK_REACH_OVERRIDE.getBooleanValue())
        {
            cir.setReturnValue((float) Configs.Generic.BLOCK_REACH_DISTANCE.getDoubleValue());
            cir.cancel();
        }
    }

    @Inject(method = "extendedReach", at = @At("HEAD"), cancellable = true)
    private void overrideExtendedReach(CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_BLOCK_REACH_OVERRIDE.getBooleanValue())
        {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
