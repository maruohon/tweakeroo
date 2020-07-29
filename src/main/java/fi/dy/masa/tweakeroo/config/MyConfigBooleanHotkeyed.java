package fi.dy.masa.tweakeroo.config;

import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import fi.dy.masa.malilib.util.StringUtils;

public class MyConfigBooleanHotkeyed extends ConfigBooleanHotkeyed {
    private final String guiDisplayName;

    public MyConfigBooleanHotkeyed(String prefix, String name, boolean defaultValue, String defaultHotkey) {
        super(name, defaultValue, defaultHotkey,
                String.format("%s.comment.%s", prefix, name),
                String.format("%s.pretty_name.%s", prefix, name));
        this.guiDisplayName = String.format("%s.%s", prefix, name);
    }

    @Override
    public String getPrettyName() {
        String ret = super.getPrettyName();
        if (ret.contains("pretty_name")) {
            ret = StringUtils.splitCamelCase(this.getRawConfigGuiDisplayName());
        }
        return ret;
    }

    public String getRawConfigGuiDisplayName() {
        return StringUtils.translate(this.guiDisplayName);
    }

    @Override
    public String getConfigGuiDisplayName() {
        return this.getRawConfigGuiDisplayName();
    }
}
