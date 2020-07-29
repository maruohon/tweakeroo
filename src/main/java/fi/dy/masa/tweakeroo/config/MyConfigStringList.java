package fi.dy.masa.tweakeroo.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.ConfigStringList;
import fi.dy.masa.malilib.util.StringUtils;

public class MyConfigStringList extends ConfigStringList {
    private final String guiDisplayName;

    public MyConfigStringList(String prefix, String name, ImmutableList<String> defaultValue) {
        super(name, defaultValue, String.format("%s.comment.%s", prefix, name));
        this.guiDisplayName = String.format("%s.%s", prefix, name);
    }

    @Override
    public String getConfigGuiDisplayName() {
        return StringUtils.translate(this.guiDisplayName);
    }
}
