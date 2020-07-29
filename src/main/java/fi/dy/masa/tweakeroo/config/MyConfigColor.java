package fi.dy.masa.tweakeroo.config;

import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.util.StringUtils;

public class MyConfigColor extends ConfigColor {

    private final String guiDisplayName;

    public MyConfigColor(String prefix, String name, String defaultValue) {
        super(name, defaultValue, String.format("%s.comment.%s", prefix, name));
        this.guiDisplayName = String.format("%s.%s", prefix, name);
    }

    @Override
    public String getConfigGuiDisplayName() {
        return StringUtils.translate(this.guiDisplayName);
    }
}
