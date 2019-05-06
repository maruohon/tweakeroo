package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager
{
    @Inject(method = "interactItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;syncSelectedSlot()V"),
            cancellable = true)
    private void onProcessRightClickFirst(PlayerEntity player, World worldIn, Hand hand, CallbackInfoReturnable<ActionResult> cir)
    {
        if (PlacementTweaks.onProcessRightClickPre(player, hand))
        {
            cir.setReturnValue(ActionResult.PASS);
            cir.cancel();
        }
    }

    @Inject(method = "interactItem",
            slice = @Slice(from = @At(value = "INVOKE", 
                                      target = "Lnet/minecraft/item/ItemStack;use(" +
                                               "Lnet/minecraft/world/World;" +
                                               "Lnet/minecraft/entity/player/PlayerEntity;" +
                                               "Lnet/minecraft/util/Hand;" +
                                               ")Lnet/minecraft/util/TypedActionResult;")),
            at = @At("RETURN"))
    private void onProcessRightClickPost(PlayerEntity player, World worldIn, Hand hand, CallbackInfoReturnable<ActionResult> cir)
    {
        PlacementTweaks.onProcessRightClickPost(player, hand);
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
        if (PlacementTweaks.onProcessRightClickPre(player, hand))
        {
            cir.setReturnValue(ActionResult.PASS);
            cir.cancel();
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
        if (PlacementTweaks.onProcessRightClickPre(player, hand))
        {
            cir.setReturnValue(ActionResult.PASS);
            cir.cancel();
        }
    }
}
