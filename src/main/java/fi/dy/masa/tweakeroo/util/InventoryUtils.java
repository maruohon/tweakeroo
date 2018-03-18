package fi.dy.masa.tweakeroo.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class InventoryUtils
{
    public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2)
    {
        return ItemStack.areItemsEqual(stack1, stack2) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    public static void swapHotbarWithInventoryRow(EntityPlayer player, int row)
    {
        Container container = player.inventoryContainer;
        int slot = row * 9 + 9;

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++)
        {
            swapSlots(container, slot, hotbarSlot);
            slot++;
        }
    }

    public static void swapSlots(Container container, int slot, int hotbarSlot)
    {
        Minecraft mc = Minecraft.getMinecraft();
        mc.playerController.windowClick(container.windowId, slot, hotbarSlot, ClickType.SWAP, mc.player);
    }
}
