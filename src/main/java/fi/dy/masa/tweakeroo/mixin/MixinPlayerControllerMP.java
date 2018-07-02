package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP
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
}
