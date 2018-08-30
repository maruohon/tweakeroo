package fi.dy.masa.tweakeroo.config.gui;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.gui.ConfigPanelBase;
import fi.dy.masa.malilib.config.gui.ConfigPanelHotkeysBase;
import fi.dy.masa.malilib.config.gui.ConfigPanelSub;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;

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
        String modId = Reference.MOD_ID;
        this.addSubPanel(new ConfigPanelSub(modId, "Generic", Configs.Generic.OPTIONS.toArray(new IConfigValue[Configs.Generic.OPTIONS.size()]), this));
        this.addSubPanel(new ConfigPanelHotkeysBase(modId, "Generic Hotkeys", ImmutableList.copyOf(Hotkeys.values()), this));
        this.addSubPanel((new ConfigPanelSub(modId, "Tweak Toggles", FeatureToggle.values(), this)).setElementWidth(120));
        this.addSubPanel(new ConfigPanelTweakHotkeys(modId, this));
    }
}
