package fi.dy.masa.tweakeroo.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import fi.dy.masa.malilib.overlay.message.MessageUtils;
import fi.dy.masa.malilib.util.GameUtils;
import fi.dy.masa.malilib.util.ItemUtils;
import fi.dy.masa.malilib.util.wrap.NbtWrap;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

public class InventoryUtils
{
    private static final List<EntityEquipmentSlot> REPAIR_MODE_SLOTS = new ArrayList<>();
    private static final List<EntityEquipmentSlot> SWAP_BROKEN_TOOLS_SLOTS = new ArrayList<>();
    private static final List<Integer> REPAIR_MODE_SLOT_NUMBERS = new ArrayList<>();
    private static final HashSet<Item> UNSTACKING_ITEMS = new HashSet<>();

    public static void setUnstackingItems(List<Item> items)
    {
        UNSTACKING_ITEMS.clear();
        UNSTACKING_ITEMS.addAll(items);
    }

    public static void setRepairModeSlots(List<EntityEquipmentSlot> slots)
    {
        REPAIR_MODE_SLOTS.clear();
        REPAIR_MODE_SLOT_NUMBERS.clear();

        for (EntityEquipmentSlot slot : slots)
        {
            if (slot != null)
            {
                REPAIR_MODE_SLOTS.add(slot);
                REPAIR_MODE_SLOT_NUMBERS.add(getSlotNumberForEquipmentType(slot, null));
            }
        }
    }

    public static void setSwapBrokenToolsSlots(List<EntityEquipmentSlot> slots)
    {
        SWAP_BROKEN_TOOLS_SLOTS.clear();

        for (EntityEquipmentSlot slot : slots)
        {
            if (slot != null)
            {
                SWAP_BROKEN_TOOLS_SLOTS.add(slot);
            }
        }
    }

    @Nullable
    private static EntityEquipmentSlot getEquipmentSlotByName(String name)
    {
        EntityEquipmentSlot slot = null;

        switch (name)
        {
            case "mainhand":    slot = EntityEquipmentSlot.MAINHAND; break;
            case "offhand":     slot = EntityEquipmentSlot.OFFHAND; break;
            case "head":        slot = EntityEquipmentSlot.HEAD; break;
            case "chest":       slot = EntityEquipmentSlot.CHEST; break;
            case "legs":        slot = EntityEquipmentSlot.LEGS; break;
            case "feet":        slot = EntityEquipmentSlot.FEET; break;
        }

        return slot;
    }

    private static boolean isConfiguredRepairSlot(int slotNum, EntityPlayer player)
    {
        if (REPAIR_MODE_SLOTS.contains(EntityEquipmentSlot.MAINHAND) &&
            (slotNum - 36) == player.inventory.currentItem)
        {
            return true;
        }

        return REPAIR_MODE_SLOT_NUMBERS.contains(slotNum);
    }

    /**
     * Returns the equipment type for the given slot number,
     * assuming that the slot number is for the player's main inventory container
     * @param slotNum
     * @return
     */
    @Nullable
    private static EntityEquipmentSlot getEquipmentTypeForSlot(int slotNum, EntityPlayer player)
    {
        if (REPAIR_MODE_SLOTS.contains(EntityEquipmentSlot.MAINHAND) &&
            (slotNum - 36) == player.inventory.currentItem)
        {
            return EntityEquipmentSlot.MAINHAND;
        }

        switch (slotNum)
        {
            case 45: return EntityEquipmentSlot.OFFHAND;
            case  5: return EntityEquipmentSlot.HEAD;
            case  6: return EntityEquipmentSlot.CHEST;
            case  7: return EntityEquipmentSlot.LEGS;
            case  8: return EntityEquipmentSlot.FEET;
        }

        return null;
    }

    /**
     * Returns the slot number for the given equipment type
     * in the player's inventory container
     * @param type
     * @return
     */
    private static int getSlotNumberForEquipmentType(EntityEquipmentSlot type, @Nullable EntityPlayer player)
    {
        switch (type)
        {
            case MAINHAND:  return player != null ? player.inventory.currentItem + 36 : -1;
            case OFFHAND:   return 45;
            case HEAD:      return 5;
            case CHEST:     return 6;
            case LEGS:      return 7;
            case FEET:      return 8;
        }

        return -1;
    }

    public static void swapHotbarWithInventoryRow(EntityPlayer player, int row)
    {
        Container container = player.inventoryContainer;
        row = MathHelper.clamp(row, 0, 2);
        int slot = row * 9 + 9;

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++)
        {
            fi.dy.masa.malilib.util.inventory.InventoryUtils.swapSlots(container, slot, hotbarSlot);
            slot++;
        }
    }

    public static void restockNewStackToHand(EntityPlayer player, EnumHand hand, ItemStack stackReference, boolean allowHotbar)
    {
        int slotWithItem;

        if (stackReference.getItem().isDamageable())
        {
            int minDurability = getMinDurability(stackReference);
            slotWithItem = findSlotWithSuitableReplacementToolWithDurabilityLeft(player.inventoryContainer, stackReference, minDurability);
        }
        else
        {
            slotWithItem = findSlotWithItem(player.inventoryContainer, stackReference, allowHotbar, true);
        }

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
        }
    }

    public static void preRestockHand(EntityPlayer player, EnumHand hand, boolean allowHotbar)
    {
        if (FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue() && Configs.Generic.HAND_RESTOCK_PRE.getBooleanValue())
        {
            int threshold = Configs.Generic.HAND_RESTOCK_PRE_THRESHOLD.getIntegerValue();
            fi.dy.masa.malilib.util.inventory.InventoryUtils.preRestockHand(player, hand, threshold, allowHotbar);
        }
    }

    public static void trySwapCurrentToolIfNearlyBroken(EnumHand hand)
    {
        if (FeatureToggle.TWEAK_SWAP_ALMOST_BROKEN_TOOLS.getBooleanValue())
        {
            EntityPlayer player = GameUtils.getClientPlayer();
            ItemStack stack = player.getHeldItem(hand);
            EntityEquipmentSlot slot = hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND;

            if (ItemUtils.notEmpty(stack) && SWAP_BROKEN_TOOLS_SLOTS.contains(slot))
            {
                int minDurability = getMinDurability(stack);

                if (isItemAtLowDurability(stack, minDurability))
                {
                    swapItemWithHigherDurabilityToHand(player, hand, stack, minDurability);
                }
            }
        }
    }

    public static void trySwitchToEffectiveTool(BlockPos pos)
    {
        EntityPlayer player = GameUtils.getClientPlayer();
        IBlockState state = GameUtils.getClientWorld().getBlockState(pos);
        ItemStack stack = player.getHeldItemMainhand();

        if (ItemUtils.isEmpty(stack) || stack.getDestroySpeed(state) <= 1f)
        {
            Container container = player.inventoryContainer;
            int slotNumber = findSlotWithEffectiveItemWithDurabilityLeft(container, state);

            if (slotNumber != -1 && (slotNumber - 36) != player.inventory.currentItem)
            {
                swapItemToHand(player, EnumHand.MAIN_HAND, slotNumber);
            }
        }
    }

    private static boolean isItemAtLowDurability(ItemStack stack, int minDurability)
    {
        return stack.getItem().isDamageable() && stack.getItemDamage() >= stack.getMaxDamage() - minDurability;
    }

    private static int getMinDurability(ItemStack stack)
    {
        int minDurability = Configs.Generic.ITEM_SWAP_DURABILITY_THRESHOLD.getIntegerValue();

        // For items with low maximum durability, use 5% as the threshold,
        // if the configured durability threshold is over that.
        if ((double) minDurability / (double) stack.getMaxDamage() >= 0.05D)
        {
            minDurability = (int) (stack.getMaxDamage() * 0.05);
        }

        return minDurability;
    }

    private static void swapItemWithHigherDurabilityToHand(EntityPlayer player, EnumHand hand, ItemStack stackReference, int minDurabilityLeft)
    {
        int slotWithItem = findSlotWithSuitableReplacementToolWithDurabilityLeft(player.inventoryContainer, stackReference, minDurabilityLeft);

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
            MessageUtils.printCustomActionbarMessage("tweakeroo.message.swapped_low_durability_item_for_better_durability");
            return;
        }

        slotWithItem = fi.dy.masa.malilib.util.inventory.InventoryUtils.findEmptySlotInPlayerInventory(player.inventoryContainer, false, false);

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
            MessageUtils.printCustomActionbarMessage("tweakeroo.message.swapped_low_durability_item_off_players_hand");
            return;
        }

        Container container = player.inventoryContainer;

        for (Slot slot : container.inventorySlots)
        {
            if (slot.slotNumber <= 8)
            {
                continue;
            }

            ItemStack stack = slot.getStack();

            if (ItemUtils.notEmpty(stack) && stack.getItem().isDamageable() == false)
            {
                slotWithItem = slot.slotNumber;
                break;
            }
        }

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
            MessageUtils.printCustomActionbarMessage("tweakeroo.message.swapped_low_durability_item_for_dummy_item");
        }
    }

    public static void repairModeSwapItems(EntityPlayer player)
    {
        if (player.openContainer == player.inventoryContainer)
        {
            for (EntityEquipmentSlot type : REPAIR_MODE_SLOTS)
            {
                repairModeHandleSlot(player, type);
            }
        }
    }

    private static void repairModeHandleSlot(EntityPlayer player, EntityEquipmentSlot type)
    {
        int slotNum = getSlotNumberForEquipmentType(type, player);
        ItemStack stack = player.getItemStackFromSlot(type);

        if (slotNum == -1 || ItemUtils.isEmpty(stack))
        {
            return;
        }

        boolean shouldSwap;

        if (Configs.Generic.REPAIR_MODE_MENDING_ONLY.getBooleanValue())
        {
            shouldSwap = stack.isItemDamaged() == false &&
                         EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) > 0;
        }
        else
        {
            shouldSwap = stack.isItemStackDamageable() == false ||
                         stack.isItemDamaged() == false ||
                         EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) <= 0;
        }

        if (shouldSwap)
        {
            Slot slot = player.openContainer.getSlot(slotNum);
            int slotRepairableItem = findRepairableItemNotInRepairableSlot(slot, player);

            if (slotRepairableItem != -1)
            {
                swapItemToEqupmentSlot(player, type, slotRepairableItem);
                MessageUtils.printCustomActionbarMessage("tweakeroo.message.repair_mode.swapped_repairable_item_to_slot", type.getName());
            }
        }
    }

    private static int findRepairableItemNotInRepairableSlot(Slot targetSlot, EntityPlayer player)
    {
        Container containerPlayer = player.openContainer;

        for (Slot slot : containerPlayer.inventorySlots)
        {
            if (slot.getHasStack() && isConfiguredRepairSlot(slot.slotNumber, player) == false)
            {
                ItemStack stack = slot.getStack();

                if (stack.isItemStackDamageable() && stack.isItemDamaged() && targetSlot.isItemValid(stack) &&
                    EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) > 0)
                {
                    return slot.slotNumber;
                }
            }
        }

        return -1;
    }

    /**
     * Finds a slot with an identical item than <b>stackReference</b>, ignoring the durability
     * of damageable items. Does not allow crafting or armor slots or the offhand slot
     * in the ContainerPlayer container.
     * @param container
     * @param stackReference
     * @param reverse
     * @return the slot number, or -1 if none were found
     */
    public static int findSlotWithItem(Container container, ItemStack stackReference, boolean allowHotbar, boolean reverse)
    {
        final int startSlot = reverse ? container.inventorySlots.size() - 1 : 0;
        final int endSlot = reverse ? -1 : container.inventorySlots.size();
        final int increment = reverse ? -1 : 1;
        final boolean isPlayerInv = container instanceof ContainerPlayer;

        for (int slotNum = startSlot; slotNum != endSlot; slotNum += increment)
        {
            Slot slot = container.inventorySlots.get(slotNum);

            if ((isPlayerInv == false || fi.dy.masa.malilib.util.inventory.InventoryUtils.isRegularInventorySlot(slot.slotNumber, false)) &&
                (allowHotbar || isHotbarSlot(slot) == false) &&
                fi.dy.masa.malilib.util.inventory.InventoryUtils.areStacksEqualIgnoreDurability(slot.getStack(), stackReference))
            {
                return slot.slotNumber;
            }
        }

        return -1;
    }

    private static boolean isHotbarSlot(Slot slot)
    {
        return slot.slotNumber >= 36 && slot.slotNumber <= 44;
    }

    private static void swapItemToHand(EntityPlayer player, EnumHand hand, int slotNumber)
    {
        if (slotNumber != -1 && player.openContainer == player.inventoryContainer)
        {
            Minecraft mc = GameUtils.getClient();
            Container container = player.inventoryContainer;

            if (hand == EnumHand.MAIN_HAND)
            {
                int currentHotbarSlot = player.inventory.currentItem;
                Slot slot = container.getSlot(slotNumber);

                if (slot != null && isHotbarSlot(slot))
                {
                    player.inventory.currentItem = slotNumber - 36;
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(player.inventory.currentItem));
                }
                else
                {
                    mc.playerController.windowClick(container.windowId, slotNumber, currentHotbarSlot, ClickType.SWAP, mc.player);
                }
            }
            else if (hand == EnumHand.OFF_HAND)
            {
                int currentHotbarSlot = player.inventory.currentItem;
                // Swap the requested slot to the current hotbar slot
                mc.playerController.windowClick(container.windowId, slotNumber, currentHotbarSlot, ClickType.SWAP, mc.player);

                // Swap the requested item from the hotbar slot to the offhand
                mc.playerController.windowClick(container.windowId, 45, currentHotbarSlot, ClickType.SWAP, mc.player);

                // Swap the original item back to the hotbar slot
                mc.playerController.windowClick(container.windowId, slotNumber, currentHotbarSlot, ClickType.SWAP, mc.player);
            }
        }
    }

    private static void swapItemToEqupmentSlot(EntityPlayer player, EntityEquipmentSlot type, int sourceSlotNumber)
    {
        if (sourceSlotNumber != -1 && player.openContainer == player.inventoryContainer)
        {
            Minecraft mc = GameUtils.getClient();
            Container container = player.inventoryContainer;

            if (type == EntityEquipmentSlot.MAINHAND)
            {
                int currentHotbarSlot = player.inventory.currentItem;
                mc.playerController.windowClick(container.windowId, sourceSlotNumber, currentHotbarSlot, ClickType.SWAP, mc.player);
            }
            else if (type == EntityEquipmentSlot.OFFHAND)
            {
                // Use a hotbar slot that isn't the current slot
                int tempSlot = (player.inventory.currentItem + 1) % 9;
                // Swap the requested slot to the current hotbar slot
                mc.playerController.windowClick(container.windowId, sourceSlotNumber, tempSlot, ClickType.SWAP, mc.player);

                // Swap the requested item from the hotbar slot to the offhand
                mc.playerController.windowClick(container.windowId, 45, tempSlot, ClickType.SWAP, mc.player);

                // Swap the original item back to the hotbar slot
                mc.playerController.windowClick(container.windowId, sourceSlotNumber, tempSlot, ClickType.SWAP, mc.player);
            }
            // Armor slots
            else
            {
                int armorSlot = getSlotNumberForEquipmentType(type, player);
                // Pick up the new item
                mc.playerController.windowClick(container.windowId, sourceSlotNumber, 0, ClickType.PICKUP, mc.player);
                // Swap it to the armor slot
                mc.playerController.windowClick(container.windowId, armorSlot, 0, ClickType.PICKUP, mc.player);
                // Place down the old armor item
                mc.playerController.windowClick(container.windowId, sourceSlotNumber, 0, ClickType.PICKUP, mc.player);
            }
        }
    }

    private static int findSlotWithSuitableReplacementToolWithDurabilityLeft(Container container, ItemStack stackReference, int minDurabilityLeft)
    {
        for (Slot slot : container.inventorySlots)
        {
            ItemStack stackSlot = slot.getStack();

            // Only accept regular inventory slots (no crafting, armor slots, or offhand)
            if (fi.dy.masa.malilib.util.inventory.InventoryUtils.isRegularInventorySlot(slot.slotNumber, false) &&
                stackSlot.isItemEqualIgnoreDurability(stackReference) &&
                stackSlot.getMaxDamage() - stackSlot.getItemDamage() > minDurabilityLeft &&
                hasSameIshEnchantments(stackReference, stackSlot))
            {
                return slot.slotNumber;
            }
        }

        return -1;
    }

    private static boolean hasSameIshEnchantments(ItemStack stackReference, ItemStack stack)
    {
        int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stackReference);

        if (level > 0)
        {
            return EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) >= level;
        }

        level = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stackReference);

        if (level > 0)
        {
            return EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack) >= level;
        }

        return true;
    }

    private static int findSlotWithEffectiveItemWithDurabilityLeft(Container container, IBlockState state)
    {
        int slotNum = -1;
        float bestSpeed = -1f;

        for (Slot slot : container.inventorySlots)
        {
            // Don't consider armor and crafting slots
            if (slot.slotNumber <= 8 || slot.getHasStack() == false)
            {
                continue;
            }

            ItemStack stack = slot.getStack();

            if (stack.getMaxDamage() - stack.getItemDamage() > getMinDurability(stack))
            {
                float speed = stack.getDestroySpeed(state);

                if (speed > 1.0f)
                {
                    int effLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);

                    if (effLevel > 0)
                    {
                        speed += (effLevel * effLevel) + 1;
                    }
                }

                if (speed > 1f && (slotNum == -1 || speed > bestSpeed))
                {
                    slotNum = slot.slotNumber;
                    bestSpeed = speed;
                }
            }
        }

        return slotNum;
    }

    private static void tryCombineStacksInInventory(EntityPlayer player, ItemStack stackReference)
    {
        List<Slot> slots = new ArrayList<>();
        Container container = player.inventoryContainer;
        Minecraft mc = GameUtils.getClient();

        for (Slot slot : container.inventorySlots)
        {
            // Inventory crafting and armor slots are not valid
            if (slot.slotNumber < 8)
            {
                continue;
            }

            ItemStack stack = slot.getStack();

            if (stack.getCount() < stack.getMaxStackSize() && fi.dy.masa.malilib.util.inventory.InventoryUtils.areStacksEqual(stackReference, stack))
            {
                slots.add(slot);
            }
        }

        for (int i = 0; i < slots.size(); ++i)
        {
            Slot slot1 = slots.get(i);

            for (int j = i + 1; j < slots.size(); ++j)
            {
                Slot slot2 = slots.get(j);
                ItemStack stack = slot1.getStack();

                if (stack.getCount() < stack.getMaxStackSize())
                {
                    // Pick up the item from slot1 and try to put it in slot2
                    mc.playerController.windowClick(container.windowId, slot1.slotNumber, 0, ClickType.PICKUP, player);
                    mc.playerController.windowClick(container.windowId, slot2.slotNumber, 0, ClickType.PICKUP, player);

                    // If the items didn't all fit, return the rest
                    if (ItemUtils.notEmpty(player.inventory.getCurrentItem()))
                    {
                        mc.playerController.windowClick(container.windowId, slot1.slotNumber, 0, ClickType.PICKUP, player);
                    }

                    if (slot2.getStack().getCount() >= slot2.getStack().getMaxStackSize())
                    {
                        slots.remove(j);
                        --j;
                    }
                }

                if (slot1.getHasStack() == false)
                {
                    break;
                }
            }
        }
    }

    public static boolean canUnstackingItemNotFitInInventory(ItemStack stack, EntityPlayer player)
    {
        if (FeatureToggle.TWEAK_ITEM_UNSTACKING_PROTECTION.getBooleanValue() &&
            stack.getCount() > 1 &&
            UNSTACKING_ITEMS.contains(stack.getItem()))
        {
            if (fi.dy.masa.malilib.util.inventory.InventoryUtils.findEmptySlotInPlayerInventory(player.inventoryContainer, false, false) == -1)
            {
                tryCombineStacksInInventory(player, stack);

                return fi.dy.masa.malilib.util.inventory.InventoryUtils.findEmptySlotInPlayerInventory(player.inventoryContainer, false, false) == -1;
            }
        }

        return false;
    }

    public static void switchToPickedBlock()
    {
        Minecraft mc  = GameUtils.getClient();
        EntityPlayer player = mc.player;
        World world = mc.world;
        double reach = mc.playerController.getBlockReachDistance();
        RayTraceResult trace = player.rayTrace(reach, mc.getRenderPartialTicks());

        if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            BlockPos pos = trace.getBlockPos();
            IBlockState stateTargeted = world.getBlockState(pos);
            ItemStack stack = stateTargeted.getBlock().getItem(world, pos, stateTargeted);

            if (ItemUtils.notEmpty(stack))
            {
                /*
                if (isCreative)
                {
                    TileEntity te = world.getTileEntity(pos);

                    if (te != null)
                    {
                        mc.storeTEInStack(stack, te);
                    }
                }
                */

                if (GameUtils.isCreativeMode())
                {
                    player.inventory.setPickedItemStack(stack);
                    mc.playerController.sendSlotPacket(player.getHeldItem(EnumHand.MAIN_HAND), 36 + player.inventory.currentItem);
                }
                else
                {
                    int slot = fi.dy.masa.malilib.util.inventory.InventoryUtils.findPlayerInventorySlotWithItem(player.inventoryContainer, stack, true); //player.inventory.getSlotFor(stack);

                    if (slot != -1)
                    {
                        int currentHotbarSlot = player.inventory.currentItem;
                        mc.playerController.windowClick(player.inventoryContainer.windowId, slot, currentHotbarSlot, ClickType.SWAP, mc.player);

                        /*
                        if (InventoryPlayer.isHotbar(slot))
                        {
                            player.inventory.currentItem = slot;
                        }
                        else
                        {
                            mc.playerController.pickItem(slot);
                        }
                        */
                    }
                }
            }
        }
    }

    public static boolean cleanUpShulkerBoxNBT(ItemStack stack)
    {
        boolean changed = false;
        NBTTagCompound tag = ItemUtils.getTag(stack);

        if (tag != null)
        {
            if (NbtWrap.containsCompound(tag, "BlockEntityTag"))
            {
                NBTTagCompound beTag = NbtWrap.getCompound(tag, "BlockEntityTag");

                if (NbtWrap.containsList(beTag, "Items") &&
                    NbtWrap.getListSize(NbtWrap.getListOfCompounds(beTag, "Items")) == 0)
                {
                    NbtWrap.remove(beTag, "Items");
                    changed = true;
                }

                if (beTag.isEmpty())
                {
                    NbtWrap.remove(tag, "BlockEntityTag");
                }
            }

            if (tag.isEmpty())
            {
                ItemUtils.setTag(stack, null);
                changed = true;
            }
        }

        return changed;
    }
}
