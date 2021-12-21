package fi.dy.masa.tweakeroo.util;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum MessageOutputType implements IConfigOptionListEntry
{
    NONE      ("none",      "tweakeroo.label.message_output_type.none"),
    ACTIONBAR ("actionbar", "tweakeroo.label.message_output_type.actionbar"),
    MESSAGE   ("message",   "tweakeroo.label.message_output_type.message");

    public static final ImmutableList<MessageOutputType> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    MessageOutputType(String configString, String translationKey)
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
        return StringUtils.translate(this.translationKey);
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
    public MessageOutputType fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static MessageOutputType fromStringStatic(String name)
    {
        for (MessageOutputType val : VALUES)
        {
            if (val.configString.equalsIgnoreCase(name))
            {
                return val;
            }
        }

        return MessageOutputType.NONE;
    }
}
