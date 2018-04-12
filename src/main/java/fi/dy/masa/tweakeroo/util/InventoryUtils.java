package fi.dy.masa.tweakeroo.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

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

    public static void swapNewStackToHand(EntityPlayer player, EnumHand hand, ItemStack stackReference)
    {
        int slotWithItem = findSlotWithItem(player.inventoryContainer, stackReference);

        if (slotWithItem != -1)
        {
            Minecraft mc = Minecraft.getMinecraft();
            Container container = player.inventoryContainer;

            if (hand == EnumHand.MAIN_HAND)
            {
                int currentHotbarSlot = player.inventory.currentItem;
                mc.playerController.windowClick(container.windowId, slotWithItem, currentHotbarSlot, ClickType.SWAP, mc.player);
            }
            else if (hand == EnumHand.OFF_HAND)
            {
                // Pick up the new stack
                mc.playerController.windowClick(container.windowId, slotWithItem, 0, ClickType.PICKUP, mc.player);
                // Place the new stack to the off-hand slot
                mc.playerController.windowClick(container.windowId, 45, 0, ClickType.PICKUP, mc.player);

                // Items left in the cursor, put them to the slot the new stack came from
                if (player.inventory.getCurrentItem().isEmpty() == false)
                {
                    mc.playerController.windowClick(container.windowId, slotWithItem, 0, ClickType.PICKUP, mc.player);
                }
            }
        }
    }

    public static int findSlotWithItem(Container container, ItemStack stackReference)
    {
        for (Slot slot : container.inventorySlots)
        {
            if (areStacksEqual(slot.getStack(), stackReference))
            {
                return slot.slotNumber;
            }
        }

        return -1;
    }
}
