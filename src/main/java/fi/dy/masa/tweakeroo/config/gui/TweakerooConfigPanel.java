package fi.dy.masa.tweakeroo.config.gui;

import fi.dy.masa.malilib.config.gui.ConfigPanelBase;
import fi.dy.masa.tweakeroo.Reference;

public class TweakerooConfigPanel extends ConfigPanelBase
{
    @Override
    protected String getPanelTitlePrefix()
    {
        return Reference.MOD_NAME + " options";
    }

    @Override
    protected void createSubPanels()
    {
        this.addSubPanel(new ConfigPanelGeneric(this));
        this.addSubPanel(new ConfigPanelGenericHotkeys(this));
        this.addSubPanel(new ConfigPanelTweakToggles(this));
        this.addSubPanel(new ConfigPanelTweakHotkeys(this));
    }
}
