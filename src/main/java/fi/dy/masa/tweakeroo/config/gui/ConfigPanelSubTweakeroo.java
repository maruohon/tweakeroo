package fi.dy.masa.tweakeroo.config.gui;

import fi.dy.masa.malilib.config.gui.ConfigPanelBase;
import fi.dy.masa.malilib.config.gui.ConfigPanelSub;
import fi.dy.masa.tweakeroo.config.Configs;

public abstract class ConfigPanelSubTweakeroo extends ConfigPanelSub
{
    public ConfigPanelSubTweakeroo(String title, ConfigPanelBase parent)
    {
        super(title, parent);
    }

    @Override
    protected void onSettingsChanged()
    {
        Configs.save();
        Configs.load();
    }
}
