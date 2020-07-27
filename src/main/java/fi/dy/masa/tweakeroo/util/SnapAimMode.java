package fi.dy.masa.tweakeroo.util;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.value.ConfigOptionListEntry;
import fi.dy.masa.malilib.config.value.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum SnapAimMode implements IConfigOptionListEntry<SnapAimMode>
{
    YAW     ("yaw",     "tweakeroo.label.snap_aim_mode.yaw"),
    PITCH   ("pitch",   "tweakeroo.label.snap_aim_mode.pitch"),
    BOTH    ("both",    "tweakeroo.label.snap_aim_mode.both");

    public static final ImmutableList<SnapAimMode> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    SnapAimMode(String configString, String translationKey)
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
    public SnapAimMode cycle(boolean forward)
    {
        return ConfigOptionListEntry.cycleValue(VALUES, this.ordinal(), forward);
    }

    @Override
    public SnapAimMode fromString(String name)
    {
        return ConfigOptionListEntry.findValueByName(name, VALUES);
    }
}
