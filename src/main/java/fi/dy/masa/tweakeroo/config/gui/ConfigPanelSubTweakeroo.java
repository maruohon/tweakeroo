package fi.dy.masa.tweakeroo.config.gui;

import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.gui.ConfigPanelBase;
import fi.dy.masa.malilib.config.gui.ConfigPanelSub;
import fi.dy.masa.tweakeroo.config.Configs;

public class ConfigPanelSubTweakeroo extends ConfigPanelSub
{
    public ConfigPanelSubTweakeroo(String title, IConfigValue[] configs, ConfigPanelBase parent)
    {
        super(title, parent);

        this.configs = configs;
    }

    @Override
    protected void onSettingsChanged()
    {
        Configs.save();
        Configs.load();
    }
}
