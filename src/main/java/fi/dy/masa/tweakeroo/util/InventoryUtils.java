package fi.dy.masa.tweakeroo.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.tweakeroo.Tweakeroo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.mixin.IMixinSlot;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Container;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class InventoryUtils
{
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

    public static void swapHotbarWithInventoryRow(PlayerEntity player, int row)
    {
        Container container = player.playerContainer;
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
        int slotWithItem = findSlotWithItem(player.playerContainer, stackReference, allowHotbar, true);

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
        }
    }

    public static void trySwapCurrentToolIfNearlyBroken()
    {
        if (FeatureToggle.TWEAK_SWAP_ALMOST_BROKEN_TOOLS.getBooleanValue())
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            ClientPlayerEntity player = mc.player;

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

    private static boolean isItemAtLowDurability(ItemStack stack, int minDurability)
    {
        return stack.hasDurability() && stack.getDamage() >= stack.getDurability() - minDurability;
    }

    private static int getMinDurability(ItemStack stack)
    {
        int minDurability = Configs.Generic.ITEM_SWAP_DURABILITY_THRESHOLD.getIntegerValue();

        // For items with low maximum durability, use 5% as the threshold,
        // if the configured durability threshold is over that.
        if ((double) minDurability / (double) stack.getDurability() >= 0.05D)
        {
            minDurability = (int) (stack.getDurability() * 0.05);
        }

        return minDurability;
    }

    private static void swapItemWithHigherDurabilityToHand(PlayerEntity player, Hand hand, ItemStack stackReference, int minDurabilityLeft)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        int slotWithItem = findSlotWithIdenticalItemWithDurabilityLeft(player.playerContainer, stackReference, minDurabilityLeft);

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
            mc.inGameHud.addChatMessage(ChatMessageType.GAME_INFO, new TranslatableComponent("tweakeroo.message.swapped_low_durability_item_for_better_durability"));
            return;
        }

        slotWithItem = fi.dy.masa.malilib.util.InventoryUtils.findEmptySlotInPlayerInventory(player.playerContainer, false, false);

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
            mc.inGameHud.addChatMessage(ChatMessageType.GAME_INFO, new TranslatableComponent("tweakeroo.message.swapped_low_durability_item_off_players_hand"));
            return;
        }

        Container container = player.playerContainer;

        for (Slot slot : container.slotList)
        {
            ItemStack stack = slot.getStack();
            {
                if (slot.id > 8 && stack.isEmpty() == false && stack.hasDurability() == false)
                {
                    slotWithItem = slot.id;
                    break;
                }
            }
        }

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
            mc.inGameHud.addChatMessage(ChatMessageType.GAME_INFO, new TranslatableComponent("tweakeroo.message.swapped_low_durability_item_for_dummy_item"));
        }
    }

    public static void repairModeSwapItems(PlayerEntity player)
    {
        if (player.container instanceof PlayerContainer)
        {
            repairModeHandleHand(player, Hand.MAIN_HAND);
            repairModeHandleHand(player, Hand.OFF_HAND);
        }
    }

    private static void repairModeHandleHand(PlayerEntity player, Hand hand)
    {
        ItemStack stackHand = player.getStackInHand(hand);

        if (stackHand.isEmpty() == false &&
            (stackHand.hasDurability() == false ||
             stackHand.isDamaged() == false ||
             EnchantmentHelper.getLevel(Enchantments.MENDING, stackHand) <= 0))
        {
            int slotNumber = findRepairableItemNotInHand(player);

            if (slotNumber != -1)
            {
                swapItemToHand(player, hand, slotNumber);
                InfoUtils.printActionbarMessage("tweakeroo.message.repair_mode.swapped_repairable_item_to_hand");
            }
        }
    }

    private static int findRepairableItemNotInHand(PlayerEntity player)
    {
        Container containerPlayer = player.container;
        int slotHand = player.inventory.selectedSlot;

        for (Slot slot : containerPlayer.slotList)
        {
            if (slot.id != slotHand && slot.hasStack())
            {
                ItemStack stack = slot.getStack();

                if (stack.hasDurability() && stack.isDamaged() &&
                    EnchantmentHelper.getLevel(Enchantments.MENDING, stack) > 0)
                {
                    return slot.id;
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
        final int startSlot = reverse ? container.slotList.size() - 1 : 0;
        final int endSlot = reverse ? -1 : container.slotList.size();
        final int increment = reverse ? -1 : 1;
        final boolean isPlayerInv = container instanceof PlayerContainer;

        for (int slotNum = startSlot; slotNum != endSlot; slotNum += increment)
        {
            Slot slot = container.slotList.get(slotNum);

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
        // This isn't correct for modded Forge IItemHandler-based inventories
        return (slot.inventory instanceof PlayerInventory) && ((IMixinSlot) slot).getSlotIndex() <= 8;
    }

    private static void swapItemToHand(PlayerEntity player, Hand hand, int slotNumber)
    {
        if (slotNumber != -1)
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            Container container = player.playerContainer;

            if (hand == Hand.MAIN_HAND)
            {
                int currentHotbarSlot = player.inventory.selectedSlot;
                mc.interactionManager.method_2906(container.syncId, slotNumber, currentHotbarSlot, SlotActionType.SWAP, mc.player);
            }
            else if (hand == Hand.OFF_HAND)
            {
                int currentHotbarSlot = player.inventory.selectedSlot;
                // Swap the requested slot to the current hotbar slot
                mc.interactionManager.method_2906(container.syncId, slotNumber, currentHotbarSlot, SlotActionType.SWAP, mc.player);

                // Swap the requested item from the hotbar slot to the offhand
                mc.interactionManager.method_2906(container.syncId, 45, currentHotbarSlot, SlotActionType.SWAP, mc.player);

                // Swap the original item back to the hotbar slot
                mc.interactionManager.method_2906(container.syncId, slotNumber, currentHotbarSlot, SlotActionType.SWAP, mc.player);
            }
        }
    }

    private static int findSlotWithIdenticalItemWithDurabilityLeft(Container container, ItemStack stackReference, int minDurabilityLeft)
    {
        for (Slot slot : container.slotList)
        {
            ItemStack stackSlot = slot.getStack();

            if (stackSlot.isEqualIgnoreDurability(stackReference) &&
                stackReference.getDurability() - stackSlot.getDamage() > minDurabilityLeft &&
                ItemStack.areTagsEqual(stackSlot, stackReference))
            {
                return slot.id;
            }
        }

        return -1;
    }

    private static void tryCombineStacksInInventory(PlayerEntity player, ItemStack stackReference)
    {
        List<Slot> slots = new ArrayList<>();
        Container container = player.playerContainer;
        MinecraftClient mc = MinecraftClient.getInstance();

        for (Slot slot : container.slotList)
        {
            // Inventory crafting and armor slots are not valid
            if (slot.id < 8)
            {
                continue;
            }

            ItemStack stack = slot.getStack();

            if (stack.getAmount() < stack.getMaxAmount() && fi.dy.masa.malilib.util.InventoryUtils.areStacksEqual(stackReference, stack))
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

                if (stack.getAmount() < stack.getMaxAmount())
                {
                    // Pick up the item from slot1 and try to put it in slot2
                    mc.interactionManager.method_2906(container.syncId, slot1.id, 0, SlotActionType.PICKUP, player);
                    mc.interactionManager.method_2906(container.syncId, slot2.id, 0, SlotActionType.PICKUP, player);

                    // If the items didn't all fit, return the rest
                    if (player.inventory.getMainHandStack().isEmpty() == false)
                    {
                        mc.interactionManager.method_2906(container.syncId, slot1.id, 0, SlotActionType.PICKUP, player);
                    }

                    if (slot2.getStack().getAmount() >= slot2.getStack().getMaxAmount())
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
            stack.getAmount() > 1 &&
            UNSTACKING_ITEMS.contains(stack.getItem()))
        {
            if (fi.dy.masa.malilib.util.InventoryUtils.findEmptySlotInPlayerInventory(player.playerContainer, false, false) == -1)
            {
                tryCombineStacksInInventory(player, stack);

                if (fi.dy.masa.malilib.util.InventoryUtils.findEmptySlotInPlayerInventory(player.playerContainer, false, false) == -1)
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
        double reach = mc.interactionManager.getReachDistance();
        boolean isCreative = player.abilities.creativeMode;
        HitResult trace = player.rayTrace(reach, mc.getTickDelta(), false);

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
                    int slot = fi.dy.masa.malilib.util.InventoryUtils.findSlotWithItem(player.playerContainer, stack, true); //player.inventory.getSlotFor(stack);

                    if (slot != -1)
                    {
                        int currentHotbarSlot = player.inventory.selectedSlot;
                        mc.interactionManager.method_2906(player.playerContainer.syncId, slot, currentHotbarSlot, SlotActionType.SWAP, mc.player);

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
}
