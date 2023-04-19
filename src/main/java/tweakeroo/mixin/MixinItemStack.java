package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import tweakeroo.tweaks.MiscTweaks;
import tweakeroo.util.IItemStackLimit;

@Mixin(ItemStack.class)
public abstract class MixinItemStack
{
    @Shadow
    public Item getItem() { return null; }

    @Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
    public void getMaxStackSizeStackSensitive(CallbackInfoReturnable<Integer> ci)
    {
        ci.setReturnValue(((IItemStackLimit) this.getItem()).getItemStackLimit((ItemStack) (Object) this));
    }

    @Inject(method = "hasEffect", at = @At("HEAD"), cancellable = true)
    private void disableItemGlint(CallbackInfoReturnable<Boolean> cir)
    {
        if (MiscTweaks.shouldPreventItemGlintFor((ItemStack) (Object) this))
        {
            cir.setReturnValue(false);
        }
    }
}
