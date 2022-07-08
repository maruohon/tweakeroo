package fi.dy.masa.tweakeroo.util;

import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ArrayListMultimap;
import com.mojang.brigadier.StringReader;
import net.minecraft.block.InfestedBlock;
import net.minecraft.command.CommandRegistryWrapper;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import fi.dy.masa.tweakeroo.Tweakeroo;

public class CreativeExtraItems
{
    private static final ArrayListMultimap<ItemGroup, ItemStack> ADDED_ITEMS = ArrayListMultimap.create();
    private static final HashMap<Item, ItemGroup> OVERRIDDEN_GROUPS = new HashMap<>();

    @Nullable
    public static ItemGroup getGroupFor(Item item)
    {
        return OVERRIDDEN_GROUPS.get(item);
    }

    public static List<ItemStack> getExtraStacksForGroup(ItemGroup group)
    {
        return ADDED_ITEMS.get(group);
    }

    public static void setCreativeExtraItems(List<String> items)
    {
        setCreativeExtraItems(ItemGroup.TRANSPORTATION, items);
    }

    private static void setCreativeExtraItems(ItemGroup group, List<String> items)
    {
        ADDED_ITEMS.clear();
        OVERRIDDEN_GROUPS.clear();

        for (String str : items)
        {
            ItemStack stack = parseItemFromString(str);

            if (stack.isEmpty() == false)
            {
                if (stack.hasNbt())
                {
                    ADDED_ITEMS.put(group, stack);
                }
                else
                {
                    OVERRIDDEN_GROUPS.put(stack.getItem(), group);
                }
            }
        }
    }

    public static ItemStack parseItemFromString(String str)
    {
        try
        {
            ItemStringReader.ItemResult itemResult = ItemStringReader.item(CommandRegistryWrapper.of(Registry.ITEM), new StringReader(str));
            Item item = itemResult.item().value();

            if (item != null)
            {
                ItemStack stack = new ItemStack(item);
                stack.setNbt(itemResult.nbt());
                return stack;
            }
        }
        catch (Exception e)
        {
            Tweakeroo.logger.warn("Invalid item '{}'", str);
        }

        return ItemStack.EMPTY;
    }

    public static void removeInfestedBlocks(DefaultedList<ItemStack> stacks)
    {
        stacks.removeIf((stack) -> stack.getItem() instanceof BlockItem &&
                                   ((BlockItem) stack.getItem()).getBlock() instanceof InfestedBlock);
    }
}
