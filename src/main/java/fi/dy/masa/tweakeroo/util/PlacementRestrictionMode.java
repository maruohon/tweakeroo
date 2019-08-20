package fi.dy.masa.tweakeroo.util;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum PlacementRestrictionMode implements IConfigOptionListEntry
{
    PLANE       ("plane",       "tweakeroo.label.placement_restriction_mode.plane"),
    FACE        ("face",        "tweakeroo.label.placement_restriction_mode.face"),
    COLUMN      ("column",      "tweakeroo.label.placement_restriction_mode.column"),
    LINE        ("line",        "tweakeroo.label.placement_restriction_mode.line"),
    LAYER       ("layer",       "tweakeroo.label.placement_restriction_mode.layer"),
    DIAGONAL    ("diagonal",    "tweakeroo.label.placement_restriction_mode.diagonal");

    private final String configString;
    private final String unlocName;

    private PlacementRestrictionMode(String configString, String unlocName)
    {
        this.configString = configString;
        this.unlocName = unlocName;
    }

    @Override
    public String getStringValue()
    {
        return this.configString;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.unlocName);
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
    public PlacementRestrictionMode fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static PlacementRestrictionMode fromStringStatic(String name)
    {
        for (PlacementRestrictionMode mode : PlacementRestrictionMode.values())
        {
            if (mode.configString.equalsIgnoreCase(name))
            {
                return mode;
            }
        }

        return PlacementRestrictionMode.FACE;
    }
}
