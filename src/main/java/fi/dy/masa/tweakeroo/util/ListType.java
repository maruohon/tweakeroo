package fi.dy.masa.tweakeroo.util;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import net.minecraft.client.resources.I18n;

public enum ListType implements IConfigOptionListEntry
{
    NONE        ("none",        "tweakeroo.label.list_type.none"),
    BLACKLIST   ("blacklist",   "tweakeroo.label.list_type.blacklist"),
    WHITELIST   ("whitelist",   "tweakeroo.label.list_type.whitelist");

    private final String configString;
    private final String translationKey;

    private ListType(String configString, String translationKey)
    {
        this.configString = configString;
        this.translationKey = translationKey;
    }

    @Override
    public String getStringValue()
    {
        return this.configString;
    }

    @Override
    public String getDisplayName()
    {
        return I18n.format(this.translationKey);
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward)
    {
        int id = this.ordinal();

        if (forward)
        {
            if (++id >= values().length)
            {
                id = 0;
            }
        }
        else
        {
            if (--id < 0)
            {
                id = values().length - 1;
            }
        }

        return values()[id % values().length];
    }

    @Override
    public ListType fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static ListType fromStringStatic(String name)
    {
        for (ListType mode : ListType.values())
        {
            if (mode.configString.equalsIgnoreCase(name))
            {
                return mode;
            }
        }

        return ListType.NONE;
    }
}
