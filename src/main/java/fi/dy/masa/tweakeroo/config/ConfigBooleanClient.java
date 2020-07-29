package fi.dy.masa.tweakeroo.config;

import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigBooleanClient extends ConfigBooleanHotkeyed
{
    private final String guiDisplayName;
    public ConfigBooleanClient(String prefix, String name, boolean defaultValue, String defaultHotkey)
    {
        super(name, defaultValue, defaultHotkey,
                String.format("%s.comment.%s", prefix, name),
                String.format("%s.pretty_name.%s", prefix, name));
        this.guiDisplayName = String.format("%s.%s", prefix, name);
    }

    @Override
    public String getComment()
    {
        String comment = super.getComment();

        if (comment == null)
        {
            return "";
        }

        return comment + "\n" + StringUtils.translate("tweakeroo.label.config_comment.single_player_only");
    }

    @Override
    public String getPrettyName()
    {
        String ret = super.getPrettyName();
        if (ret.contains("pretty_name")) {
            ret = StringUtils.splitCamelCase(this.getRawConfigGuiDisplayName());
        }
        return ret;
    }

    private String getRawConfigGuiDisplayName()
    {
        return StringUtils.translate(this.guiDisplayName);
    }

    @Override
    public String getConfigGuiDisplayName()
    {
        return String.format("%s%s%s",
                GuiBase.TXT_GOLD,
                this.getRawConfigGuiDisplayName(),
                GuiBase.TXT_RST);
    }
}
