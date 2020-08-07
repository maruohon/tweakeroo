package fi.dy.masa.tweakeroo.util;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.value.BaseConfigOptionListEntry;
import fi.dy.masa.malilib.config.value.ConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum PlacementRestrictionMode implements ConfigOptionListEntry<PlacementRestrictionMode>
{
    PLANE       ("plane",       "tweakeroo.label.placement_restriction_mode.plane"),
    FACE        ("face",        "tweakeroo.label.placement_restriction_mode.face"),
    COLUMN      ("column",      "tweakeroo.label.placement_restriction_mode.column"),
    LINE        ("line",        "tweakeroo.label.placement_restriction_mode.line"),
    LAYER       ("layer",       "tweakeroo.label.placement_restriction_mode.layer"),
    DIAGONAL    ("diagonal",    "tweakeroo.label.placement_restriction_mode.diagonal");

    public static final ImmutableList<PlacementRestrictionMode> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    PlacementRestrictionMode(String configString, String translationKey)
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
    public PlacementRestrictionMode cycle(boolean forward)
    {
        return BaseConfigOptionListEntry.cycleValue(VALUES, this.ordinal(), forward);
    }

    @Override
    public PlacementRestrictionMode fromString(String name)
    {
        return BaseConfigOptionListEntry.findValueByName(name, VALUES);
    }
}
