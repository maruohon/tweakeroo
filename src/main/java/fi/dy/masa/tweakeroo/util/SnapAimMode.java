package fi.dy.masa.tweakeroo.util;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum SnapAimMode implements IConfigOptionListEntry
{
    YAW     ("yaw",     "tweakeroo.label.snap_aim_mode.yaw"),
    PITCH   ("pitch",   "tweakeroo.label.snap_aim_mode.pitch"),
    BOTH    ("both",    "tweakeroo.label.snap_aim_mode.both");

    private final String configString;
    private final String translationKey;

    private SnapAimMode(String configString, String translationKey)
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
    public SnapAimMode fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static SnapAimMode fromStringStatic(String name)
    {
        for (SnapAimMode mode : SnapAimMode.values())
        {
            if (mode.configString.equalsIgnoreCase(name))
            {
                return mode;
            }
        }

        return SnapAimMode.YAW;
    }
}
