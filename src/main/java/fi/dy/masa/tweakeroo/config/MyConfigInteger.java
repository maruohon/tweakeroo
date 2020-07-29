package fi.dy.masa.tweakeroo.config;

import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.util.StringUtils;

public class MyConfigInteger extends ConfigInteger {
    private final String guiDisplayName;

    public MyConfigInteger(String prefix, String name, int defaultValue) {
        this(prefix, name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public MyConfigInteger(String prefix, String name, int defaultValue, int minValue, int maxValue) {
        this(prefix, name, defaultValue, minValue, maxValue, false);
    }

    public MyConfigInteger(String prefix, String name, int defaultValue, int minValue, int maxValue, boolean useSlider) {
        super(name, defaultValue, minValue, maxValue, useSlider, String.format("%s.comment.%s", prefix, name));
        this.guiDisplayName = String.format("%s.%s", prefix, name);
    }

    @Override
    public String getConfigGuiDisplayName() {
        return StringUtils.translate(this.guiDisplayName);
    }
}
