package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.item.ItemStack;
import fi.dy.masa.tweakeroo.tweaks.MiscTweaks;

@Mixin(ItemStack.class)
public abstract class MixinItemStack
{
    @Inject(method = "hasEffect", at = @At("HEAD"), cancellable = true)
    private void disableItemGlint(CallbackInfoReturnable<Boolean> cir)
    {
        if (MiscTweaks.shouldPreventItemGlintFor((ItemStack) (Object) this))
        {
            cir.setReturnValue(false);
        }
    }
}
