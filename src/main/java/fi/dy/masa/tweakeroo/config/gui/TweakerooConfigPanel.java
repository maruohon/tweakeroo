package fi.dy.masa.tweakeroo.config.gui;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.gui.config.ModConfigScreen;
import fi.dy.masa.malilib.gui.config.liteloader.BaseConfigPanel;
import fi.dy.masa.malilib.config.option.IConfigValue;
import fi.dy.masa.malilib.gui.config.SimpleConfigInfoProvider;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.config.Configs;
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
        List<? extends IConfigValue> configs;
        SimpleConfigInfoProvider provider;

        this.addSubPanel((new ModConfigScreen(modId, Configs.Generic.OPTIONS, "tweakeroo.gui.button.config_gui.generic")).setConfigElementsWidth(120));
        this.addSubPanel((new ModConfigScreen(modId, Configs.Fixes.OPTIONS, "tweakeroo.gui.button.config_gui.fixes")).setConfigElementsWidth(100));
        this.addSubPanel((new ModConfigScreen(modId, Configs.Lists.OPTIONS, "tweakeroo.gui.button.config_gui.lists")).setConfigElementsWidth(200));

        this.addSubPanel(new ModConfigScreen(modId, Hotkeys.HOTKEY_LIST, "tweakeroo.gui.button.config_gui.generic_hotkeys"));

        configs = ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(FeatureToggle.values()));
        this.addSubPanel((new ModConfigScreen(modId, configs, "tweakeroo.gui.button.config_gui.tweak_toggles")).setConfigElementsWidth(100));

        configs = ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, ImmutableList.copyOf(FeatureToggle.values()));
        provider = new SimpleConfigInfoProvider("Hotkey to toggle the '", "' tweak");
        this.addSubPanel(new ModConfigScreen(modId, configs, "tweakeroo.gui.button.config_gui.tweak_hotkeys").setHoverInfoProvider(provider));

        configs = ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(Configs.Disable.OPTIONS));
        this.addSubPanel((new ModConfigScreen(modId, configs, "tweakeroo.gui.button.config_gui.disable_toggle")).setConfigElementsWidth(100));

        configs = ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, ImmutableList.copyOf(Configs.Disable.OPTIONS));
        provider = new SimpleConfigInfoProvider("Hotkey to toggle the '", "' disable option");
        this.addSubPanel(new ModConfigScreen(modId, configs, "tweakeroo.gui.button.config_gui.disable_hotkeys").setHoverInfoProvider(provider));
    }
}
