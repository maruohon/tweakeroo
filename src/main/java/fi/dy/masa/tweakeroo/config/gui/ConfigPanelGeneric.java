package fi.dy.masa.tweakeroo.config.gui;

import fi.dy.masa.tweakeroo.config.ConfigsGeneric;
import fi.dy.masa.tweakeroo.config.interfaces.IConfig;

public class ConfigPanelGeneric extends ConfigPanelSub
{
    public ConfigPanelGeneric(TweakerooConfigPanel parent)
    {
        super("Generic", parent);
    }

    @Override
    protected IConfig[] getConfigs()
    {
        return ConfigsGeneric.values();
    }
}
