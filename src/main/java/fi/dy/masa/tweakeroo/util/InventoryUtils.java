package fi.dy.masa.tweakeroo.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
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
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.Constants;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.tweakeroo.Tweakeroo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;

public class InventoryUtils
{
    private static final List<EquipmentSlot> REPAIR_MODE_SLOTS = new ArrayList<>();
    private static final List<Integer> REPAIR_MODE_SLOT_NUMBERS = new ArrayList<>();
    private static final HashSet<Item> UNSTACKING_ITEMS = new HashSet<>();
    private static final List<Integer> TOOL_SWITCHABLE_SLOTS = new ArrayList<>();
    private static final List<Integer> TOOL_SWITCH_IGNORED_SLOTS = new ArrayList<>();
    private static final HashMap<EntityType<?>, HashSet<Item>> WEAPON_MAPPING = new HashMap<>();

    public static void setToolSwitchableSlots(String configStr)
    {
        parseSlotsFromString(configStr, TOOL_SWITCHABLE_SLOTS);
    }

    public static void setToolSwitchIgnoreSlots(String configStr)
    {
        parseSlotsFromString(configStr, TOOL_SWITCH_IGNORED_SLOTS);
    }

    public static void parseSlotsFromString(String configStr, Collection<Integer> output)
    {
        String[] parts = configStr.split(",");
        Pattern patternRange = Pattern.compile("^(?<start>[0-9])-(?<end>[0-9])$");

        output.clear();

        if (configStr.isBlank())
        {
            return;
        }

        for (String str : parts)
        {
            try
            {
                Matcher matcher = patternRange.matcher(str);

                if (matcher.matches())
                {
                    int slotStart = Integer.parseInt(matcher.group("start")) - 1;
                    int slotEnd = Integer.parseInt(matcher.group("end")) - 1;

                    if (slotStart <= slotEnd &&
                        PlayerInventory.isValidHotbarIndex(slotStart) &&
                        PlayerInventory.isValidHotbarIndex(slotEnd))
                    {
                        for (int slotNum = slotStart; slotNum <= slotEnd; ++slotNum)
                        {
                            if (output.contains(slotNum) == false)
                            {
                                output.add(slotNum);
                            }
                        }
                    }
                }
                else
                {
                    int slotNum = Integer.parseInt(str) - 1;

                    if (PlayerInventory.isValidHotbarIndex(slotNum) && output.contains(slotNum) == false)
                    {
                        output.add(slotNum);
                    }
                }
            }
            catch (NumberFormatException ignore)
            {
                InfoUtils.showGuiOrInGameMessage(Message.MessageType.ERROR, "Failed to parse slots from string %s", configStr);
            }
        }
    }

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
        REPAIR_MODE_SLOT_NUMBERS.clear();

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

                int slotNum = getSlotNumberForEquipmentType(type, null);

                if (slotNum >= 0)
                {
                    REPAIR_MODE_SLOT_NUMBERS.add(slotNum);
                }
            }
        }
    }

    public static void setWeaponMapping(List<String> mappings)
    {
        WEAPON_MAPPING.clear();

        for (String mapping : mappings)
        {
            String[] split = mapping.replaceAll(" ", "").split("=>");

            if (split.length != 2)
            {
                Tweakeroo.logger.warn("Expected weapon mapping to be `entity_ids => weapon_ids` got '{}'", mapping);
                continue;
            }

            HashSet<Item> weapons = new HashSet<>();
            String entities = split[0].trim();
            String items = split[1].trim();
            
            if (items.equals("<ignore>") == false)
            {
                for (String itemId : items.split(","))
                {
                    try
                    {
                        Optional<Item> weapon = Registry.ITEM.getOrEmpty(new Identifier(itemId));

                        if (weapon.isPresent())
                        {
                            weapons.add(weapon.get());
                            continue;
                        }
                    }
                    catch (Exception ignore) {}

                    Tweakeroo.logger.warn("Unable to find item to use as weapon: '{}'", itemId);
                }
            }

            if (entities.equalsIgnoreCase("<default>"))
            {
                WEAPON_MAPPING.computeIfAbsent(null, s -> new HashSet<>()).addAll(weapons);
            }
            else
            {
                for (String entity_id : entities.split(","))
                {
                    try
                    {
                        Optional<EntityType<?>> entity = Registry.ENTITY_TYPE.getOrEmpty(new Identifier(entity_id));

                        if (entity.isPresent())
                        {
                            WEAPON_MAPPING.computeIfAbsent(entity.get(), s -> new HashSet<>()).addAll(weapons);
                            continue;
                        }
                    }
                    catch (Exception ignore) {}

                    Tweakeroo.logger.warn("Unable to find entity: '{}'", entity_id);
                }
            }
        }
    }

    private static boolean isConfiguredRepairSlot(int slotNum, PlayerEntity player)
    {
        if (REPAIR_MODE_SLOTS.contains(EquipmentSlot.MAINHAND) &&
            (slotNum - 36) == player.getInventory().selectedSlot)
        {
            return true;
        }

        return REPAIR_MODE_SLOT_NUMBERS.contains(slotNum);
    }

    /**
     * Returns the equipment type for the given slot number,
     * assuming that the slot number is for the player's main inventory container
     */
    @Nullable
    private static EquipmentSlot getEquipmentTypeForSlot(int slotNum, PlayerEntity player)
    {
        if (REPAIR_MODE_SLOTS.contains(EquipmentSlot.MAINHAND) &&
            (slotNum - 36) == player.getInventory().selectedSlot)
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
            case MAINHAND:  return player != null ? player.getInventory().selectedSlot + 36 : -1;
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

        if (FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue() &&
            Configs.Generic.HAND_RESTOCK_PRE.getBooleanValue() &&
            stackHand.isEmpty() == false &&
            stackHand.getCount() <= threshold && stackHand.getMaxCount() > threshold &&
            PlacementTweaks.canUseItemWithRestriction(PlacementTweaks.HAND_RESTOCK_RESTRICTION, stackHand) &&
            player.currentScreenHandler == player.playerScreenHandler &&
            player.currentScreenHandler.getCursorStack().isEmpty())
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            ScreenHandler container = player.playerScreenHandler;
            int endSlot = allowHotbar ? 44 : 35;
            int currentMainHandSlot = player.getInventory().selectedSlot + 36;
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
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (FeatureToggle.TWEAK_SWAP_ALMOST_BROKEN_TOOLS.getBooleanValue() && player != null)
        {
            trySwapCurrentToolIfNearlyBroken(Hand.MAIN_HAND, player);
            trySwapCurrentToolIfNearlyBroken(Hand.OFF_HAND, player);
        }
    }

    public static void trySwapCurrentToolIfNearlyBroken(Hand hand, PlayerEntity player)
    {
        ItemStack stack = player.getStackInHand(hand);

        if (stack.isEmpty() == false)
        {
            int minDurability = getMinDurability(stack);

            if (isItemAtLowDurability(stack, minDurability))
            {
                swapItemWithHigherDurabilityToHand(player, hand, stack, minDurability + 1);
            }
        }
    }

    public static void trySwitchToWeapon(Entity entity)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;

        if (player != null && mc.world != null &&
            TOOL_SWITCH_IGNORED_SLOTS.contains(player.getInventory().selectedSlot) == false)
        {
            ScreenHandler container = player.playerScreenHandler;
            ItemPickerTest test;

            if (FeatureToggle.TWEAK_SWAP_ALMOST_BROKEN_TOOLS.getBooleanValue())
            {
                test = (currentStack, previous) -> InventoryUtils.isBetterWeaponAndHasDurability(currentStack, previous, entity);
            }
            else
            {
                test = (currentStack, previous) -> InventoryUtils.isBetterWeapon(currentStack, previous, entity);
            }

            int slotNumber = findSlotWithBestItemMatch(container, test, UniformIntProvider.create(36, 44), UniformIntProvider.create(9, 35));

            if (slotNumber != -1 && (slotNumber - 36) != player.getInventory().selectedSlot)
            {
                swapToolToHand(slotNumber, mc);
                PlacementTweaks.cacheStackInHand(Hand.MAIN_HAND);
            }
        }
    }

    private static boolean isBetterWeapon(ItemStack testedStack, ItemStack previousWeapon, Entity entity)
    {
        return testedStack.isEmpty() == false && matchesWeaponMapping(testedStack, entity) && (makesMoreDamage(testedStack, previousWeapon) || matchesWeaponMapping(previousWeapon, entity) == false);
    }

    private static boolean isBetterWeaponAndHasDurability(ItemStack testedStack, ItemStack previousTool, Entity entity)
    {
        return hasEnoughDurability(testedStack) && isBetterWeapon(testedStack, previousTool, entity);
    }

    private static boolean makesMoreDamage(ItemStack testedStack, ItemStack previousTool)
    {
        return getBaseAttackDamage(testedStack) > getBaseAttackDamage(previousTool);
    }

    private static float getBaseAttackDamage(ItemStack stack)
    {
        Item item = stack.getItem();

        if (item instanceof SwordItem)
        {
            return ((SwordItem) item).getAttackDamage();
        }
        else if (item instanceof MiningToolItem)
        { 
            return ((MiningToolItem) item).getAttackDamage();
        }

        return 0F;
    }

    protected static boolean matchesWeaponMapping(ItemStack stack, Entity entity)
    {
        HashSet<Item> weapons = WEAPON_MAPPING.getOrDefault(entity.getType(), WEAPON_MAPPING.get(null));
        return weapons != null && weapons.contains(stack.getItem());
    }

    public static void trySwitchToEffectiveTool(BlockPos pos)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;

        if (player != null && mc.world != null &&
            TOOL_SWITCH_IGNORED_SLOTS.contains(player.getInventory().selectedSlot) == false)
        {
            BlockState state = mc.world.getBlockState(pos);
            ScreenHandler container = player.playerScreenHandler;
            ItemPickerTest test;

            if (FeatureToggle.TWEAK_SWAP_ALMOST_BROKEN_TOOLS.getBooleanValue())
            {
                test = (currentStack, previous) -> InventoryUtils.isBetterToolAndHasDurability(currentStack, previous, state);
            }
            else
            {
                test = (currentStack, previous) -> InventoryUtils.isBetterTool(currentStack, previous, state);
            }

            int slotNumber = findSlotWithBestItemMatch(container, test, UniformIntProvider.create(36, 44), UniformIntProvider.create(9, 35));

            if (slotNumber != -1 && (slotNumber - 36) != player.getInventory().selectedSlot)
            {
                swapToolToHand(slotNumber, mc);
            }
        }
    }

    private static boolean isBetterTool(ItemStack testedStack, ItemStack previousTool, BlockState state)
    {
        return testedStack.isEmpty() == false && isMoreEffectiveTool(testedStack, previousTool, state);
    }

    private static boolean isBetterToolAndHasDurability(ItemStack testedStack, ItemStack previousTool, BlockState state)
    {
        return hasEnoughDurability(testedStack) && isBetterTool(testedStack, previousTool, state);
    }

    private static boolean isMoreEffectiveTool(ItemStack testedStack, ItemStack previousTool, BlockState state)
    {
        return getBaseBlockBreakingSpeed(testedStack, state) > getBaseBlockBreakingSpeed(previousTool, state);
    }

    protected static float getBaseBlockBreakingSpeed(ItemStack stack, BlockState state)
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

        if (state.isToolRequired() && stack.isSuitableFor(state) == false)
        {
            speed /= (100F / 30F);
        }

        return speed;
    }

    protected static boolean hasEnoughDurability(ItemStack stack)
    {
        return stack.getMaxDamage() - stack.getDamage() > getMinDurability(stack);
    }

    private static int findSuitableSlot(ScreenHandler container, Predicate<ItemStack> itemTest)
    {
        return findSuitableSlot(container, itemTest, UniformIntProvider.create(9, container.slots.size() - 1));
    }

    private static int findSuitableSlot(ScreenHandler container, Predicate<ItemStack> itemTest, UniformIntProvider... ranges)
    {
        final int max = container.slots.size() - 1;

        for (UniformIntProvider range : ranges)
        {
            int end = Math.min(max, range.getMax());

            for (int slotNumber = range.getMin(); slotNumber <= end; ++slotNumber)
            {
                if (itemTest.test(container.getSlot(slotNumber).getStack()))
                {
                    return slotNumber;
                }
            }
        }

        return -1;
    }

    private static int findSlotWithBestItemMatch(ScreenHandler container, ItemPickerTest itemTest, UniformIntProvider... ranges)
    {
        final int max = container.slots.size() - 1;
        ItemStack bestMatch = ItemStack.EMPTY;
        int slotNum = -1;

        for (UniformIntProvider range : ranges)
        {
            int end = Math.min(max, range.getMax());

            for (int slotNumber = range.getMin(); slotNumber <= end; ++slotNumber)
            {
                Slot slot = container.getSlot(slotNumber);

                if (itemTest.isBetterMatch(slot.getStack(), bestMatch))
                {
                    bestMatch = slot.getStack();
                    slotNum = slot.id;
                }
            }
        }

        return slotNum;
    }

    private static int findEmptySlot(ScreenHandler container, Collection<Integer> slotNumbers)
    {
        final int maxSlot = container.slots.size() - 1;

        for (int slotNumber : slotNumbers)
        {
            if (slotNumber >= 0 && slotNumber <= maxSlot &&
                container.getSlot(slotNumber).hasStack() == false)
            {
                return slotNumber;
            }
        }

        return -1;
    }

    public interface ItemPickerTest
    {
        boolean isBetterMatch(ItemStack testedStack, ItemStack previousBestMatch);
    }

    private static boolean isItemAtLowDurability(ItemStack stack, int minDurability)
    {
        return stack.isDamageable() && (stack.getMaxDamage() - stack.getDamage()) <= minDurability;
    }

    private static int getMinDurability(ItemStack stack)
    {
        if (FeatureToggle.TWEAK_SWAP_ALMOST_BROKEN_TOOLS.getBooleanValue() == false)
        {
            return 0;
        }

        int minDurability = Configs.Generic.ITEM_SWAP_DURABILITY_THRESHOLD.getIntegerValue();

        // For items with low maximum durability, use 8% as the threshold,
        // if the configured durability threshold is over that.
        if (stack.getMaxDamage() <= 100 && minDurability <= 20 &&
            (double) minDurability / (double) stack.getMaxDamage() > 0.08)
        {
            minDurability = (int) Math.ceil(stack.getMaxDamage() * 0.08);
        }

        return minDurability;
    }

    private static void swapItemWithHigherDurabilityToHand(PlayerEntity player, Hand hand, ItemStack stackReference, int minDurabilityLeft)
    {
        ScreenHandler container = player.playerScreenHandler;
        int slotWithItem = findSlotWithSuitableReplacementToolWithDurabilityLeft(container, stackReference, minDurabilityLeft);

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
            InfoUtils.printActionbarMessage("tweakeroo.message.swapped_low_durability_item_for_better_durability");
            return;
        }

        slotWithItem = fi.dy.masa.malilib.util.InventoryUtils.findEmptySlotInPlayerInventory(container, false, false);

        if (slotWithItem != -1)
        {
            swapItemToHand(player, hand, slotWithItem);
            InfoUtils.printActionbarMessage("tweakeroo.message.swapped_low_durability_item_off_players_hand");
            return;
        }

        slotWithItem = findSuitableSlot(container, (s) -> s.isDamageable() == false);

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
                if ((slot.id - 36) != player.getInventory().selectedSlot &&
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

        ScreenHandler container = player.currentScreenHandler;
        ItemStack currentStack = player.getEquippedStack(EquipmentSlot.CHEST);

        Predicate<ItemStack> stackFilterChestPlate = (s) -> s.getItem() instanceof ArmorItem && ((ArmorItem) s.getItem()).getSlotType() == EquipmentSlot.CHEST;
        Predicate<ItemStack> stackFilterElytra = (s) -> s.getItem() instanceof ElytraItem && ElytraItem.isUsable(s);
        Predicate<ItemStack> stackFilter = (currentStack.isEmpty() || stackFilterChestPlate.test(currentStack)) ? stackFilterElytra : stackFilterChestPlate;
        Predicate<ItemStack> finalFilter = (s) -> s.isEmpty() == false && stackFilter.test(s) && s.getDamage() < s.getMaxDamage() - 10;
        int targetSlot = findSuitableSlot(container, finalFilter);

        if (targetSlot >= 0)
        {
            //targetSlots.sort();
            swapItemToEquipmentSlot(player, EquipmentSlot.CHEST, targetSlot);
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
        return isHotbarSlot(slot.id);
    }

    private static boolean isHotbarSlot(int slot)
    {
        return slot >= 36 && slot < (36 + PlayerInventory.getHotbarSize());
    }

    private static void swapItemToHand(PlayerEntity player, Hand hand, int slotNumber)
    {
        ScreenHandler container = player.currentScreenHandler;

        if (slotNumber != -1 && container == player.playerScreenHandler)
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            PlayerInventory inventory = player.getInventory();

            if (hand == Hand.MAIN_HAND)
            {
                int currentHotbarSlot = inventory.selectedSlot;

                if (isHotbarSlot(slotNumber))
                {
                    inventory.selectedSlot = slotNumber - 36;
                    mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(inventory.selectedSlot));
                }
                else
                {
                    mc.interactionManager.clickSlot(container.syncId, slotNumber, currentHotbarSlot, SlotActionType.SWAP, mc.player);
                }
            }
            else if (hand == Hand.OFF_HAND)
            {
                mc.interactionManager.clickSlot(container.syncId, slotNumber, 40, SlotActionType.SWAP, mc.player);
            }
        }
    }


    private static void swapItemToEquipmentSlot(PlayerEntity player, EquipmentSlot type, int sourceSlotNumber)
    {
        if (sourceSlotNumber != -1 && player.currentScreenHandler == player.playerScreenHandler)
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            ScreenHandler container = player.playerScreenHandler;
            int equipmentSlotNumber = getSlotNumberForEquipmentType(type, player);
            int equipmentSlotInvIndex = container.getSlot(equipmentSlotNumber).getIndex();

            mc.interactionManager.clickSlot(container.syncId, sourceSlotNumber, equipmentSlotInvIndex, SlotActionType.SWAP, mc.player);
        }
    }

    private static void swapToolToHand(int slotNumber, MinecraftClient mc)
    {
        PlayerEntity player = mc.player;

        if (slotNumber >= 0 && player.currentScreenHandler == player.playerScreenHandler)
        {
            PlayerInventory inventory = player.getInventory();
            ScreenHandler container = player.playerScreenHandler;

            if (isHotbarSlot(slotNumber))
            {
                inventory.selectedSlot = slotNumber - 36;
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(inventory.selectedSlot));
            }
            else
            {
                int selectedSlot = inventory.selectedSlot;
                int hotbarSlot = getUsableHotbarSlotForTool(selectedSlot, TOOL_SWITCHABLE_SLOTS, container);

                if (PlayerInventory.isValidHotbarIndex(hotbarSlot))
                {
                    if (hotbarSlot != selectedSlot)
                    {
                        inventory.selectedSlot = hotbarSlot;
                        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(inventory.selectedSlot));
                    }

                    mc.interactionManager.clickSlot(container.syncId, slotNumber, hotbarSlot, SlotActionType.SWAP, mc.player);
                }
            }
        }
    }

    private static int getUsableHotbarSlotForTool(int currentHotbarSlot, Collection<Integer> validSlots, ScreenHandler container)
    {
        int first = -1;
        int nonTool = -1;

        if (validSlots.contains(currentHotbarSlot))
        {
            ItemStack stack = container.getSlot(currentHotbarSlot + 36).getStack();

            if (stack.isEmpty())
            {
                return currentHotbarSlot;
            }

            if ((stack.getItem() instanceof ToolItem) == false)
            {
                nonTool = currentHotbarSlot;
            }
        }

        for (int hotbarSlot : validSlots)
        {
            ItemStack stack = container.getSlot(hotbarSlot + 36).getStack();

            if (stack.isEmpty())
            {
                return hotbarSlot;
            }

            if (nonTool == -1 && (stack.getItem() instanceof ToolItem) == false)
            {
                nonTool = hotbarSlot;
            }

            if (first == -1)
            {
                first = hotbarSlot;
            }
        }

        return nonTool >= 0 ? nonTool : first;
    }

    private static int findSlotWithSuitableReplacementToolWithDurabilityLeft(ScreenHandler container, ItemStack stackReference, int minDurabilityLeft)
    {
        for (Slot slot : container.slots)
        {
            ItemStack stackSlot = slot.getStack();

            // Only accept regular inventory slots (no crafting, armor slots, or offhand)
            if (fi.dy.masa.malilib.util.InventoryUtils.isRegularInventorySlot(slot.id, false) &&
                stackSlot.isItemEqualIgnoreDamage(stackReference) &&
                stackSlot.getMaxDamage() - stackSlot.getDamage() >= minDurabilityLeft &&
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
                    if (player.getInventory().getMainHandStack().isEmpty() == false)
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

                return fi.dy.masa.malilib.util.InventoryUtils.findEmptySlotInPlayerInventory(player.playerScreenHandler, false, false) == -1;
            }
        }

        return false;
    }

    public static void switchToPickedBlock()
    {
        MinecraftClient mc  = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        World world = mc.world;

        if (player == null || world == null || player.currentScreenHandler != player.playerScreenHandler)
        {
            return;
        }

        double reach = mc.interactionManager.getReachDistance();
        boolean isCreative = player.isCreative();
        HitResult trace = player.raycast(reach, mc.getTickDelta(), false);

        if (trace != null && trace.getType() == HitResult.Type.BLOCK)
        {
            BlockPos pos = ((BlockHitResult) trace).getBlockPos();
            BlockState stateTargeted = world.getBlockState(pos);
            ItemStack stack = stateTargeted.getBlock().getPickStack(world, pos, stateTargeted);

            if (stack.isEmpty() == false &&
                fi.dy.masa.malilib.util.InventoryUtils.areStacksEqual(stack, player.getMainHandStack()) == false)
            {
                ScreenHandler container = player.currentScreenHandler;
                PlayerInventory inventory = player.getInventory();
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
                    inventory.addPickBlock(stack);
                    mc.interactionManager.clickCreativeStack(player.getStackInHand(Hand.MAIN_HAND), 36 + inventory.selectedSlot);
                }
                else
                {
                    //player.getInventory().getSlotFor(stack);
                    int slotNumber = fi.dy.masa.malilib.util.InventoryUtils.findSlotWithItem(container, stack, true);

                    if (slotNumber != -1)
                    {
                        swapItemToHand(player, Hand.MAIN_HAND, slotNumber);
                    }
                }
            }
        }
    }

    public static boolean cleanUpShulkerBoxNBT(ItemStack stack)
    {
        boolean changed = false;
        NbtCompound nbt = stack.getNbt();

        if (nbt != null)
        {
            if (nbt.contains("BlockEntityTag", Constants.NBT.TAG_COMPOUND))
            {
                NbtCompound tag = nbt.getCompound("BlockEntityTag");

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
                stack.setNbt(null);
                changed = true;
            }
        }

        return changed;
    }
}
