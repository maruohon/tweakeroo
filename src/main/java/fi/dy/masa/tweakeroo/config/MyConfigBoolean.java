package fi.dy.masa.tweakeroo.config;

import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.util.StringUtils;

public class MyConfigBoolean extends ConfigBoolean {
    private final String guiDisplayName;

    public MyConfigBoolean(String prefix, String name, boolean defaultValue) {
        super(name, defaultValue, String.format("%s.comment.%s", prefix, name));
        this.guiDisplayName = String.format("%s.%s", prefix, name);
    }

    @Override
    public String getConfigGuiDisplayName() {
        return StringUtils.translate(this.guiDisplayName);
    }
}
