package fi.dy.masa.tweakeroo.config;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.util.StringUtils;

public class MyConfigOptionList extends ConfigOptionList {
    private final String guiDisplayName;

    public MyConfigOptionList(String prefix, String name, IConfigOptionListEntry defaultValue) {
        super(name, defaultValue, String.format("%s.comment.%s", prefix, name));
        this.guiDisplayName = String.format("%s.%s", prefix, name);
    }

    @Override
    public String getConfigGuiDisplayName() {
        return StringUtils.translate(this.guiDisplayName);
    }
}
