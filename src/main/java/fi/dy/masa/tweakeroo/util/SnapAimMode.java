package fi.dy.masa.tweakeroo.util;

import com.google.common.collect.ImmutableList;

import malilib.config.value.BaseOptionListConfigValue;

public class SnapAimMode extends BaseOptionListConfigValue
{
    public static final SnapAimMode YAW   = new SnapAimMode("yaw",     "tweakeroo.label.snap_aim_mode.yaw");
    public static final SnapAimMode PITCH = new SnapAimMode("pitch",   "tweakeroo.label.snap_aim_mode.pitch");
    public static final SnapAimMode BOTH  = new SnapAimMode("both",    "tweakeroo.label.snap_aim_mode.both");

    public static final ImmutableList<SnapAimMode> VALUES = ImmutableList.of(YAW, PITCH, BOTH);

    private SnapAimMode(String name, String translationKey)
    {
        super(name, translationKey);
    }
}
