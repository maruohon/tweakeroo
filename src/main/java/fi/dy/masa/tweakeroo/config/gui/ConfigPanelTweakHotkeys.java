package fi.dy.masa.tweakeroo.config.gui;

import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import fi.dy.masa.malilib.config.gui.ConfigPanelHotkeysBase;
import fi.dy.masa.malilib.config.gui.button.ConfigButtonHotkey;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

public class ConfigPanelTweakHotkeys extends ConfigPanelHotkeysBase
{
    public ConfigPanelTweakHotkeys(TweakerooConfigPanel parent)
    {
        super("Tweak Hotkeys", parent);
    }

    @Override
    protected FeatureToggle[] getConfigs()
    {
        return FeatureToggle.values();
    }

    @Override
    public void addOptions(ConfigPanelHost host)
    {
        this.clearOptions();

        int x = 10;
        int y = 10;
        int i = 0;
        int labelWidth = this.getMaxLabelWidth(this.getConfigs());

        for (FeatureToggle hotkey : this.getConfigs())
        {
            this.addLabel(i, x, y + 7, labelWidth, 8, 0xFFFFFFFF, hotkey.getName());
            this.addConfigComment(x, y + 7, labelWidth, 8, "Hotkey to toggle the '" + hotkey.getPrettyName() + "' tweak");

            this.addButton(new ConfigButtonHotkey(i + 1, x + labelWidth + 10, y, 200, 20, hotkey, this), this.getConfigListener());

            i += 2;
            y += 21;
        }
    }
}
