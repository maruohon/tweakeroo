package fi.dy.masa.tweakeroo.config.gui;

import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.tweakeroo.config.ConfigsGeneric;

public class ConfigPanelGeneric extends ConfigPanelSubTweakeroo
{
    public ConfigPanelGeneric(TweakerooConfigPanel parent)
    {
        super("Generic", parent);
    }

    @Override
    protected IConfigValue[] getConfigs()
    {
        return ConfigsGeneric.values();
    }
}
