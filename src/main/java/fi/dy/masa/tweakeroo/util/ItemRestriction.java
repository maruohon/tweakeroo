package fi.dy.masa.tweakeroo.util;

import java.util.HashSet;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction.ListType;
import fi.dy.masa.tweakeroo.Tweakeroo;

public class ItemRestriction
{
    private ListType type = ListType.NONE;
    private final HashSet<Item> blackList = new HashSet<>();
    private final HashSet<Item> whiteList = new HashSet<>();

    public void setValues(ListType type, List<String> namesBlacklist, List<String> namesWhitelist)
    {
        this.type = type;
        this.setValuesForList(ListType.BLACKLIST, namesBlacklist);
        this.setValuesForList(ListType.WHITELIST, namesWhitelist);
    }

    protected void setValuesForList(ListType type, List<String> names)
    {
        HashSet<Item> set = type == ListType.WHITELIST ? this.whiteList : this.blackList;
        set.clear();

        for (String name : names)
        {
            try
            {
                Item item = Registry.ITEM.get(new Identifier(name));

                if (item != null && item != Items.AIR)
                {
                    set.add(item);
                }
                else
                {
                    Tweakeroo.logger.warn("Invalid item name in a black- or whitelist: '{}", name);
                }
            }
            catch (Exception e)
            {
                Tweakeroo.logger.warn("Invalid item name in a black- or whitelist: '{}", name, e);
            }
        }
    }

    public boolean isItemAllowed(ItemStack stack)
    {
        switch (this.type)
        {
            case BLACKLIST:
                return this.blackList.contains(stack.getItem()) == false;

            case WHITELIST:
                return this.whiteList.contains(stack.getItem());

            default:
                return true;
        }
    }
}
