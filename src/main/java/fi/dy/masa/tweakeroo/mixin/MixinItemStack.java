package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import fi.dy.masa.tweakeroo.util.IItemStackLimit;

@Mixin(ItemStack.class)
public abstract class MixinItemStack
{
    @Shadow
    public abstract Item getItem();

    @Shadow public abstract ComponentMap getComponents();

    @Inject(method = "getMaxCount", at = @At("RETURN"), cancellable = true)
    public void getMaxStackSizeStackSensitive(CallbackInfoReturnable<Integer> cir)
    {
        cir.setReturnValue(Math.max(this.getComponents().getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1), ((IItemStackLimit) this.getItem()).getMaxStackSize((ItemStack) (Object) this)));
    }
}
