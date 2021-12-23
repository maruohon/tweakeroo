package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.ActionResult;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(ShovelItem.class)
public class MixinShovelItem
{
    @Inject(method = "useOnBlock", cancellable = true, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    private void tweakeroo_disableShovelPathing(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir)
    {
        if (Configs.Disable.DISABLE_SHOVEL_PATHING.getBooleanValue())
        {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
