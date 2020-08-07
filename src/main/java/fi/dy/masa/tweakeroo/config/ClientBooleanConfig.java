package fi.dy.masa.tweakeroo.config;

import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.util.StringUtils;

public class ClientBooleanConfig extends HotkeyedBooleanConfig
{
    public ClientBooleanConfig(String name, boolean defaultValue, String defaultHotkey, String comment)
    {
        this(name, defaultValue, defaultHotkey, comment, name);
    }

    public ClientBooleanConfig(String name, boolean defaultValue, String defaultHotkey, String comment, String prettyName)
    {
        super(name, defaultValue, defaultHotkey, comment, prettyName);
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
    public String getDisplayName()
    {
        return BaseScreen.TXT_GOLD + this.getName() + BaseScreen.TXT_RST;
    }
}
