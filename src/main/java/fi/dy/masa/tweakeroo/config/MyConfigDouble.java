package fi.dy.masa.tweakeroo.config;

import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.util.StringUtils;

public class MyConfigDouble extends ConfigDouble {

    private final String guiDisplayName;

    public MyConfigDouble(String prefix, String name, double defaultValue, double minValue, double maxValue) {
        super(name, defaultValue, minValue, maxValue, false, String.format("%s.comment.%s", prefix, name));
        this.guiDisplayName = String.format("%s.%s", prefix, name);
    }

    @Override
    public String getConfigGuiDisplayName() {
        return StringUtils.translate(this.guiDisplayName);
    }
}
