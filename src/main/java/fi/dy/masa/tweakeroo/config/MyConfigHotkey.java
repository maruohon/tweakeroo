package fi.dy.masa.tweakeroo.config;

import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.StringUtils;

public class MyConfigHotkey extends ConfigHotkey {
    private final String guiDisplayName;

    public MyConfigHotkey(String prefix, String name, String defaultStorageString) {
        this(prefix, name, defaultStorageString, KeybindSettings.DEFAULT);
    }

    public MyConfigHotkey(String prefix, String name, String defaultStorageString, KeybindSettings settings) {
        super(name, defaultStorageString, settings,
                String.format("%s.comment.%s", prefix, name),
                String.format("%s.pretty_name.%s", prefix, name));
        this.guiDisplayName = String.format("%s.%s", prefix, name);
        ;
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
