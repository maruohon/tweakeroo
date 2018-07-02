package fi.dy.masa.tweakeroo.util;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;

public enum PlacementRestrictionMode implements IConfigOptionListEntry
{
    PLANE   ("Plane"),
    FACE    ("Face"),
    COLUMN  ("Column");

    private final String displayName;

    private PlacementRestrictionMode(String displayName)
    {
        this.displayName = displayName;
    }

    @Override
    public String getStringValue()
    {
        return this.name().toLowerCase();
    }

    @Override
    public String getDisplayName()
    {
        return this.displayName;
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
            if (mode.name().equalsIgnoreCase(name))
            {
                return mode;
            }
        }

        return PlacementRestrictionMode.FACE;
    }
}
