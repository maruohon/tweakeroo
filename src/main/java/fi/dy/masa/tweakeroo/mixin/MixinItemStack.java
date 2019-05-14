package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.util.IItemStackLimit;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
}
