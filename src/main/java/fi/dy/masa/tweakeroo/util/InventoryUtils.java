package fi.dy.masa.tweakeroo.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import fi.dy.masa.malilib.util.Constants;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.tweakeroo.Tweakeroo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

public class InventoryUtils
{
    private static final List<EquipmentSlot> REPAIR_MODE_SLOTS = new ArrayList<>();
    private static final List<Integer> REPAIR_MODE_SLOT_NUMBES = new ArrayList<>();
    private static final HashSet<Item> UNSTACKING_ITEMS = new HashSet<>();

    public static void setUnstackingItems(List<String> names)
    {
        UNSTACKING_ITEMS.clear();

        for (String name : names)
        {
            try
            {
                Item item = Registry.ITEM.get(new Identifier(name));

                if (item != null && item != Items.AIR)
                {
                    UNSTACKING_ITEMS.add(item);
                }
            }
            catch (Exception e)
            {
                Tweakeroo.logger.warn("Failed to set an unstacking protected item from name '{}'", name, e);
            }
        }
    }

    public static void setRepairModeSlots(List<String> names)
    {
        REPAIR_MODE_SLOTS.clear();
        REPAIR_MODE_SLOT_NUMBES.clear();

        for (String name : names)
        {
            EquipmentSlot type = null;

            switch (name)
            {
                case "mainhand":    type = EquipmentSlot.MAINHAND; break;
                case "offhand":     type = EquipmentSlot.OFFHAND; break;
                case "head":        type = EquipmentSlot.HEAD; break;
                case "chest":       type = EquipmentSlot.CHEST; break;
                case "legs":        type = EquipmentSlot.LEGS; break;
                case "feet":        type = EquipmentSlot.FEET; break;
            }

            if (type != null)
            {
                REPAIR_MODE_SLOTS.add(type);
                REPAIR_MODE_SLOT_NUMBES.add(getSlotNumberForEquipmentType(type, null));
            }
        }
    }

    private static boolean isConfiguredRepairSlot(int slotNum, PlayerEntity player)
    {
        if (REPAIR_MODE_SLOTS.contains(EquipmentSlot.MAINHAND) &&
            (slotNum - 36) == player.inventory.selectedSlot)
        {
            return true;
        }

        return REPAIR_MODE_SLOT_NUMBES.contains(slotNum);
    }

    /**
     * Returns the equipment type for the given slot number,
     * assuming that the slot number is for the player's main inventory container
     */
    @Nullable
    private static EquipmentSlot getEquipmentTypeForSlot(int slotNum, PlayerEntity player)
    {
        if (REPAIR_MODE_SLOTS.contains(EquipmentSlot.MAINHAND) &&
            (slotNum - 36) == player.inventory.selectedSlot)
        {
            return EquipmentSlot.MAINHAND;
        }

        switch (slotNum)
        {
            case 45: return EquipmentSlot.OFFHAND;
            case  5: return EquipmentSlot.HEAD;
            case  6: return EquipmentSlot.CHEST;
            case  7: return EquipmentSlot.LEGS;
            case  8: return EquipmentSlot.FEET;
        }

        return null;
    }

    /**
     * Returns the slot number for the given equipment type
     * in the player's inventory container
     */
    private static int getSlotNumberForEquipmentType(EquipmentSlot type, @Nullable PlayerEntity player)
    {
        switch (type)
        {
            case MAINHAND:  return player != null ? player.inventory.selectedSlot + 36 : -1;
            case OFFHAND:   return 45;
            case HEAD:      return 5;
            case CHEST:     return 6;
            case LEGS:      return 7;
            case FEET:      return 8;
        }

        return -1;
    }

    public static void swapHotbarWithInventoryRow(PlayerEntity player, int row)
    {
        ScreenHandler container = player.playerScreenHandler;
        row = MathHelper.clamp(row, 0, 2);
        int slot = row * 9 + 9;

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++)
        {
            fi.dy.masa.malilib.util.InventoryUtils.swapSlots(container, slot, hotbarSlot);
            slot++;
        }
    }

    public static void restockNewStackToHand(PlayerEntity player, Hand hand, ItemStack stackReference, boolean allowHotbar)
    {
        int slotWithItem;

        if (stackReference.getItem().isDamageable())
        {
            int minDurability = getMinDurability(stackReference);
            slotWithItem = findSlotWithSuitableReplacementToolWithDurabilityLeft(player.playerScreenHandler, stackReference, minDurability);
        }
        else
        {
            slotWithItem = findSlotWithItem(player.playerScreenHandler, stackReference, allowHotbar, true);
        }

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
        }
    }

    public static void preRestockHand(PlayerEntity player, Hand hand, boolean allowHotbar)
    {
        ItemStack stackHand = player.getStackInHand(hand);
        int threshold = Configs.Generic.HAND_RESTOCK_PRE_THRESHOLD.getIntegerValue();

        if (FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue() && Configs.Generic.HAND_RESTOCK_PRE.getBooleanValue() &&
            stackHand.isEmpty() == false && stackHand.getCount() <= threshold && stackHand.getMaxCount() > threshold &&
            player.currentScreenHandler == player.playerScreenHandler && player.inventory.getCursorStack().isEmpty())
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            ScreenHandler container = player.playerScreenHandler;
            int endSlot = allowHotbar ? 44 : 35;
            int currentMainHandSlot = player.inventory.selectedSlot + 36;
            int currentSlot = hand == Hand.MAIN_HAND ? currentMainHandSlot : 45;

            for (int slotNum = 9; slotNum <= endSlot; ++slotNum)
            {
                if (slotNum == currentMainHandSlot)
                {
                    continue;
                }

                Slot slot = container.slots.get(slotNum);
                ItemStack stackSlot = slot.getStack();

                if (fi.dy.masa.malilib.util.InventoryUtils.areStacksEqualIgnoreDurability(stackSlot, stackHand))
                {
                    // If all the items from the found slot can fit into the current
                    // stack in hand, then left click, otherwise right click to split the stack
                    int button = stackSlot.getCount() + stackHand.getCount() <= stackHand.getMaxCount() ? 0 : 1;

                    mc.interactionManager.clickSlot(container.syncId, slot.id, button, SlotActionType.PICKUP, player);
                    mc.interactionManager.clickSlot(container.syncId, currentSlot, 0, SlotActionType.PICKUP, player);

                    break;
                }
            }
        }
    }

    public static void trySwapCurrentToolIfNearlyBroken()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;

        if (FeatureToggle.TWEAK_SWAP_ALMOST_BROKEN_TOOLS.getBooleanValue() && player != null)
        {

            for (Hand hand : Hand.values())
            {
                ItemStack stack = player.getStackInHand(hand);

                if (stack.isEmpty() == false)
                {
                    int minDurability = getMinDurability(stack);

                    if (isItemAtLowDurability(stack, minDurability))
                    {
                        swapItemWithHigherDurabilityToHand(player, hand, stack, minDurability);
                    }
                }
            }
        }
    }

    public static void trySwitchToEffectiveTool(BlockPos pos)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;

        if (player != null && mc.world != null)
        {
            BlockState state = mc.world.getBlockState(pos);
            ItemStack stack = player.getMainHandStack();

            if (stack.isEmpty() || stack.getMiningSpeedMultiplier(state) <= 1f)
            {
                ScreenHandler container = player.playerScreenHandler;
                int slotNumber = findSlotWithEffectiveItemWithDurabilityLeft(container, state);

                if (slotNumber != -1 && (slotNumber - 36) != player.inventory.selectedSlot)
                {
                    swapItemToHand(player, Hand.MAIN_HAND, slotNumber);
                }
            }
        }
    }

    private static boolean isItemAtLowDurability(ItemStack stack, int minDurability)
    {
        return stack.isDamageable() && stack.getDamage() >= stack.getMaxDamage() - minDurability;
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

    private static void swapItemWithHigherDurabilityToHand(PlayerEntity player, Hand hand, ItemStack stackReference, int minDurabilityLeft)
    {
        int slotWithItem = findSlotWithSuitableReplacementToolWithDurabilityLeft(player.playerScreenHandler, stackReference, minDurabilityLeft);

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
            InfoUtils.printActionbarMessage("tweakeroo.message.swapped_low_durability_item_for_better_durability");
            return;
        }

        slotWithItem = fi.dy.masa.malilib.util.InventoryUtils.findEmptySlotInPlayerInventory(player.playerScreenHandler, false, false);

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
            InfoUtils.printActionbarMessage("tweakeroo.message.swapped_low_durability_item_off_players_hand");
            return;
        }

        ScreenHandler container = player.playerScreenHandler;

        for (Slot slot : container.slots)
        {
            if (slot.id <= 8)
            {
                continue;
            }

            ItemStack stack = slot.getStack();

            if (stack.isEmpty() == false && stack.getItem().isDamageable() == false)
            {
                slotWithItem = slot.id;
                break;
            }
        }

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
            InfoUtils.printActionbarMessage("tweakeroo.message.swapped_low_durability_item_for_dummy_item");
        }
    }

    public static void repairModeSwapItems(PlayerEntity player)
    {
        if (player.currentScreenHandler == player.playerScreenHandler)
        {
            for (EquipmentSlot type : REPAIR_MODE_SLOTS)
            {
                repairModeHandleSlot(player, type);
            }
        }
    }

    private static void repairModeHandleSlot(PlayerEntity player, EquipmentSlot type)
    {
        int slotNum = getSlotNumberForEquipmentType(type, player);

        if (slotNum == -1)
        {
            return;
        }

        ItemStack stack = player.getEquippedStack(type);

        if (stack.isEmpty() == false &&
            (stack.isDamageable() == false ||
             stack.isDamaged() == false ||
             EnchantmentHelper.getLevel(Enchantments.MENDING, stack) <= 0))
        {
            Slot slot = player.currentScreenHandler.getSlot(slotNum);
            int slotRepairableItem = findRepairableItemNotInRepairableSlot(slot, player);

            if (slotRepairableItem != -1)
            {
                swapItemToEquipmentSlot(player, type, slotRepairableItem);
                InfoUtils.printActionbarMessage("tweakeroo.message.repair_mode.swapped_repairable_item_to_slot", type.getName());
            }
        }
    }

    private static int findRepairableItemNotInRepairableSlot(Slot targetSlot, PlayerEntity player)
    {
        ScreenHandler containerPlayer = player.currentScreenHandler;

        for (Slot slot : containerPlayer.slots)
        {
            if (slot.hasStack() && isConfiguredRepairSlot(slot.id, player) == false)
            {
                ItemStack stack = slot.getStack();

                // Don't take items from the current hotbar slot
                if ((slot.id - 36) != player.inventory.selectedSlot &&
                    stack.isDamageable() && stack.isDamaged() && targetSlot.canInsert(stack) &&
                    EnchantmentHelper.getLevel(Enchantments.MENDING, stack) > 0)
                {
                    return slot.id;
                }
            }
        }

        return -1;
    }

    public static void swapElytraWithChestPlate(@Nullable PlayerEntity player)
    {
        if (player == null || GuiUtils.getCurrentScreen() != null)
        {
            return;
        }

        ScreenHandler containerPlayer = player.currentScreenHandler;
        ItemStack currentStack = player.getEquippedStack(EquipmentSlot.CHEST);

        Predicate<ItemStack> stackFilterChestPlate = (s) -> s.getItem() instanceof ArmorItem && ((ArmorItem) s.getItem()).getSlotType() == EquipmentSlot.CHEST;
        Predicate<ItemStack> stackFilterElytra = (s) -> s.getItem() instanceof ElytraItem && ElytraItem.isUsable(s);
        Predicate<ItemStack> stackFilter = currentStack.isEmpty() || stackFilterChestPlate.test(currentStack) ? stackFilterElytra : stackFilterChestPlate;

        List<Slot> targetSlots = new ArrayList<>();

        for (Slot slot : containerPlayer.slots)
        {
            ItemStack stack = slot.getStack();

            if (stack.isEmpty() == false &&
                stackFilter.test(stack) &&
                stack.getDamage() < stack.getMaxDamage() - 10)
            {
                targetSlots.add(slot);
            }
        }

        if (targetSlots.isEmpty() == false)
        {
            //targetSlots.sort();
            swapItemToEquipmentSlot(player, EquipmentSlot.CHEST, targetSlots.get(0).id);
        }
    }

    /**
     * 
     * Finds a slot with an identical item than <b>stackReference</b>, ignoring the durability
     * of damageable items. Does not allow crafting or armor slots or the offhand slot
     * in the ContainerPlayer container.
     * @return the slot number, or -1 if none were found
     */
    public static int findSlotWithItem(ScreenHandler container, ItemStack stackReference, boolean allowHotbar, boolean reverse)
    {
        final int startSlot = reverse ? container.slots.size() - 1 : 0;
        final int endSlot = reverse ? -1 : container.slots.size();
        final int increment = reverse ? -1 : 1;
        final boolean isPlayerInv = container instanceof PlayerScreenHandler;

        for (int slotNum = startSlot; slotNum != endSlot; slotNum += increment)
        {
            Slot slot = container.slots.get(slotNum);

            if ((isPlayerInv == false || fi.dy.masa.malilib.util.InventoryUtils.isRegularInventorySlot(slot.id, false)) &&
                (allowHotbar || isHotbarSlot(slot) == false) &&
                fi.dy.masa.malilib.util.InventoryUtils.areStacksEqualIgnoreDurability(slot.getStack(), stackReference))
            {
                return slot.id;
            }
        }

        return -1;
    }

    private static boolean isHotbarSlot(Slot slot)
    {
        return slot.id >= 36 && slot.id <= 44;
    }

    private static void swapItemToHand(PlayerEntity player, Hand hand, int slotNumber)
    {
        if (slotNumber != -1 && player.currentScreenHandler == player.playerScreenHandler)
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            ScreenHandler container = player.playerScreenHandler;

            if (hand == Hand.MAIN_HAND)
            {
                int currentHotbarSlot = player.inventory.selectedSlot;
                Slot slot = container.getSlot(slotNumber);

                if (slot != null && isHotbarSlot(slot))
                {
                    player.inventory.selectedSlot = slotNumber - 36;
                    mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(player.inventory.selectedSlot));
                }
                else
                {
                    mc.interactionManager.clickSlot(container.syncId, slotNumber, currentHotbarSlot, SlotActionType.SWAP, mc.player);
                }
            }
            else if (hand == Hand.OFF_HAND)
            {
                int currentHotbarSlot = player.inventory.selectedSlot;
                // Swap the requested slot to the current hotbar slot
                mc.interactionManager.clickSlot(container.syncId, slotNumber, currentHotbarSlot, SlotActionType.SWAP, mc.player);

                // Swap the requested item from the hotbar slot to the offhand
                mc.interactionManager.clickSlot(container.syncId, 45, currentHotbarSlot, SlotActionType.SWAP, mc.player);

                // Swap the original item back to the hotbar slot
                mc.interactionManager.clickSlot(container.syncId, slotNumber, currentHotbarSlot, SlotActionType.SWAP, mc.player);
            }
        }
    }

    private static void swapItemToEquipmentSlot(PlayerEntity player, EquipmentSlot type, int sourceSlotNumber)
    {
        if (sourceSlotNumber != -1 && player.currentScreenHandler == player.playerScreenHandler)
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            ScreenHandler container = player.playerScreenHandler;

            if (type == EquipmentSlot.MAINHAND)
            {
                int currentHotbarSlot = player.inventory.selectedSlot;
                mc.interactionManager.clickSlot(container.syncId, sourceSlotNumber, currentHotbarSlot, SlotActionType.SWAP, mc.player);
            }
            else if (type == EquipmentSlot.OFFHAND)
            {
                // Use a hotbar slot that isn't the current slot
                int tempSlot = (player.inventory.selectedSlot + 1) % 9;
                // Swap the requested slot to the temp slot
                mc.interactionManager.clickSlot(container.syncId, sourceSlotNumber, tempSlot, SlotActionType.SWAP, mc.player);

                // Swap the requested item from the hotbar slot to the offhand
                mc.interactionManager.clickSlot(container.syncId, 45, tempSlot, SlotActionType.SWAP, mc.player);

                // Swap the original item back to the hotbar slot
                mc.interactionManager.clickSlot(container.syncId, sourceSlotNumber, tempSlot, SlotActionType.SWAP, mc.player);
            }
            // Armor slots
            else
            {
                int armorSlot = getSlotNumberForEquipmentType(type, player);
                int tempSlot = (player.inventory.selectedSlot + 1) % 9;
                // Swap the requested slot's item with the temp hotbar slot
                mc.interactionManager.clickSlot(container.syncId, sourceSlotNumber, tempSlot, SlotActionType.SWAP, mc.player);
                // Swap the temp hotbar slot with the armor slot
                mc.interactionManager.clickSlot(container.syncId, armorSlot, tempSlot, SlotActionType.SWAP, mc.player);
                // Swap the original item back to the temp hotbar slot
                mc.interactionManager.clickSlot(container.syncId, sourceSlotNumber, tempSlot, SlotActionType.SWAP, mc.player);
            }
        }
    }

    private static int findSlotWithSuitableReplacementToolWithDurabilityLeft(ScreenHandler container, ItemStack stackReference, int minDurabilityLeft)
    {
        for (Slot slot : container.slots)
        {
            ItemStack stackSlot = slot.getStack();

            // Only accept regular inventory slots (no crafting, armor slots, or offhand)
            if (fi.dy.masa.malilib.util.InventoryUtils.isRegularInventorySlot(slot.id, false) &&
                stackSlot.isItemEqualIgnoreDamage(stackReference) &&
                stackSlot.getMaxDamage() - stackSlot.getDamage() > minDurabilityLeft &&
                hasSameIshEnchantments(stackReference, stackSlot))
            {
                return slot.id;
            }
        }

        return -1;
    }

    private static boolean hasSameIshEnchantments(ItemStack stackReference, ItemStack stack)
    {
        int level = EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stackReference);

        if (level > 0)
        {
            return EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) >= level;
        }

        level = EnchantmentHelper.getLevel(Enchantments.FORTUNE, stackReference);

        if (level > 0)
        {
            return EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack) >= level;
        }

        return true;
    }

    private static int findSlotWithEffectiveItemWithDurabilityLeft(ScreenHandler container, BlockState state)
    {
        int slotNum = -1;
        float bestSpeed = -1f;

        for (Slot slot : container.slots)
        {
            // Don't consider armor and crafting slots
            if (slot.id <= 8 || slot.hasStack() == false)
            {
                continue;
            }

            ItemStack stack = slot.getStack();

            if (stack.getMaxDamage() - stack.getDamage() > getMinDurability(stack))
            {
                float speed = stack.getMiningSpeedMultiplier(state);

                if (speed > 1.0f)
                {
                    int effLevel = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);

                    if (effLevel > 0)
                    {
                        speed += (effLevel * effLevel) + 1;
                    }
                }

                if (speed > 1f && (slotNum == -1 || speed > bestSpeed))
                {
                    slotNum = slot.id;
                    bestSpeed = speed;
                }
            }
        }

        return slotNum;
    }

    private static void tryCombineStacksInInventory(PlayerEntity player, ItemStack stackReference)
    {
        List<Slot> slots = new ArrayList<>();
        ScreenHandler container = player.playerScreenHandler;
        MinecraftClient mc = MinecraftClient.getInstance();

        for (Slot slot : container.slots)
        {
            // Inventory crafting and armor slots are not valid
            if (slot.id < 8)
            {
                continue;
            }

            ItemStack stack = slot.getStack();

            if (stack.getCount() < stack.getMaxCount() && fi.dy.masa.malilib.util.InventoryUtils.areStacksEqual(stackReference, stack))
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

                if (stack.getCount() < stack.getMaxCount())
                {
                    // Pick up the item from slot1 and try to put it in slot2
                    mc.interactionManager.clickSlot(container.syncId, slot1.id, 0, SlotActionType.PICKUP, player);
                    mc.interactionManager.clickSlot(container.syncId, slot2.id, 0, SlotActionType.PICKUP, player);

                    // If the items didn't all fit, return the rest
                    if (player.inventory.getMainHandStack().isEmpty() == false)
                    {
                        mc.interactionManager.clickSlot(container.syncId, slot1.id, 0, SlotActionType.PICKUP, player);
                    }

                    if (slot2.getStack().getCount() >= slot2.getStack().getMaxCount())
                    {
                        slots.remove(j);
                        --j;
                    }
                }

                if (slot1.hasStack() == false)
                {
                    break;
                }
            }
        }
    }

    public static boolean canUnstackingItemNotFitInInventory(ItemStack stack, PlayerEntity player)
    {
        if (FeatureToggle.TWEAK_ITEM_UNSTACKING_PROTECTION.getBooleanValue() &&
            stack.getCount() > 1 &&
            UNSTACKING_ITEMS.contains(stack.getItem()))
        {
            if (fi.dy.masa.malilib.util.InventoryUtils.findEmptySlotInPlayerInventory(player.playerScreenHandler, false, false) == -1)
            {
                tryCombineStacksInInventory(player, stack);

                if (fi.dy.masa.malilib.util.InventoryUtils.findEmptySlotInPlayerInventory(player.playerScreenHandler, false, false) == -1)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static void switchToPickedBlock()
    {
        MinecraftClient mc  = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        World world = mc.world;

        if (player == null || world == null)
        {
            return;
        }

        double reach = mc.interactionManager.getReachDistance();
        boolean isCreative = player.abilities.creativeMode;
        HitResult trace = player.raycast(reach, mc.getTickDelta(), false);

        if (trace != null && trace.getType() == HitResult.Type.BLOCK)
        {
            BlockPos pos = ((BlockHitResult) trace).getBlockPos();
            BlockState stateTargeted = world.getBlockState(pos);
            ItemStack stack = stateTargeted.getBlock().getPickStack(world, pos, stateTargeted);

            if (stack.isEmpty() == false)
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

                if (isCreative)
                {
                    player.inventory.addPickBlock(stack);
                    mc.interactionManager.clickCreativeStack(player.getStackInHand(Hand.MAIN_HAND), 36 + player.inventory.selectedSlot);
                }
                else
                {
                    int slot = fi.dy.masa.malilib.util.InventoryUtils.findSlotWithItem(player.playerScreenHandler, stack, true); //player.inventory.getSlotFor(stack);

                    if (slot != -1)
                    {
                        int currentHotbarSlot = player.inventory.selectedSlot;
                        mc.interactionManager.clickSlot(player.playerScreenHandler.syncId, slot, currentHotbarSlot, SlotActionType.SWAP, mc.player);

                        /*
                        if (InventoryPlayer.isHotbar(slot))
                        {
                            player.inventory.selectedSlot = slot;
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
        CompoundTag nbt = stack.getTag();

        if (nbt != null)
        {
            if (nbt.contains("BlockEntityTag", Constants.NBT.TAG_COMPOUND))
            {
                CompoundTag tag = nbt.getCompound("BlockEntityTag");

                if (tag.contains("Items", Constants.NBT.TAG_LIST) &&
                    tag.getList("Items", Constants.NBT.TAG_COMPOUND).size() == 0)
                {
                    tag.remove("Items");
                    changed = true;
                }

                if (tag.isEmpty())
                {
                    nbt.remove("BlockEntityTag");
                }
            }

            if (nbt.isEmpty())
            {
                stack.setTag(null);
                changed = true;
            }
        }

        return changed;
    }
}
