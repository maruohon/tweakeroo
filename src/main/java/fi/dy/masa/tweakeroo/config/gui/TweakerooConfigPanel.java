package fi.dy.masa.tweakeroo.config.gui;

import fi.dy.masa.malilib.gui.config.ModConfigScreen;
import fi.dy.masa.malilib.gui.config.liteloader.BaseConfigPanel;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.DisableToggle;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;

public class TweakerooConfigPanel extends BaseConfigPanel
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

        this.addSubPanel((new ModConfigScreen(modId, Configs.Generic.OPTIONS, "tweakeroo.gui.button.config_gui.generic")).setConfigElementsWidth(120));
        this.addSubPanel((new ModConfigScreen(modId, Configs.Fixes.OPTIONS, "tweakeroo.gui.button.config_gui.fixes")).setConfigElementsWidth(100));
        this.addSubPanel((new ModConfigScreen(modId, Configs.Lists.OPTIONS, "tweakeroo.gui.button.config_gui.lists")).setConfigElementsWidth(200));
        this.addSubPanel((new ModConfigScreen(modId, Hotkeys.HOTKEY_LIST, "tweakeroo.gui.button.config_gui.generic_hotkeys")).setConfigElementsWidth(160));
        this.addSubPanel((new ModConfigScreen(modId, FeatureToggle.VALUES, "tweakeroo.gui.button.config_gui.tweak_toggles")).setConfigElementsWidth(200));
        this.addSubPanel((new ModConfigScreen(modId, DisableToggle.VALUES, "tweakeroo.gui.button.config_gui.disable_toggle")).setConfigElementsWidth(200));
    }
}
