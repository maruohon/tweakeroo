package fi.dy.masa.tweakeroo.config.gui;

import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.gui.ConfigPanelHotkeysBase;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

public class ConfigPanelTweakHotkeys extends ConfigPanelHotkeysBase
{
    public ConfigPanelTweakHotkeys(String modId, TweakerooConfigPanel parent)
    {
        super(modId, "Tweak Hotkeys", FeatureToggle.values(), parent);
    }

    @Override
    protected String getHotkeyComment(IHotkey hotkey)
    {
        return "Hotkey to toggle the '" + ((IConfigBoolean) hotkey).getPrettyName() + "' tweak";
    }
}
