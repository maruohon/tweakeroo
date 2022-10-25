package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import tweakeroo.util.IItemStackLimit;

@Mixin(Item.class)
public abstract class MixinItem implements IItemStackLimit
{
    @Shadow
    public int getItemStackLimit() { return 0; }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return this.getItemStackLimit();
    }
}
