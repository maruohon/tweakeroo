package fi.dy.masa.tweakeroo.config.gui;

import fi.dy.masa.tweakeroo.config.FeatureToggle;

public class ConfigPanelTweakToggles extends ConfigPanelSub
{
    public ConfigPanelTweakToggles(TweakerooConfigPanel parent)
    {
        super("Tweak Toggles", parent);
    }

    @Override
    protected FeatureToggle[] getConfigs()
    {
        return FeatureToggle.values();
    }
}
