package fi.dy.masa.tweakeroo.tweaks;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class StackingTweaks
{
    public static boolean shulkerBoxHasItems(ItemStack stack)
    {
        NBTTagCompound nbt = stack.getTagCompound();

        if (nbt != null && nbt.hasKey("BlockEntityTag", 10))
        {
            NBTTagCompound tag = nbt.getCompoundTag("BlockEntityTag");

            if (tag.hasKey("Items", 9))
            {
                NBTTagList tagList = tag.getTagList("Items", 10);
                return tagList.tagCount() > 0;
            }
        }

        return false;
    }
}
