package fi.dy.masa.tweakeroo.util;

import net.minecraft.item.ItemStack;

public interface IItemStackLimit
{
    int getMaxStackSize(ItemStack stack);
}
