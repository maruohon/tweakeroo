package fi.dy.masa.tweakeroo.config.gui;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.gui.GuiModConfigs;
import fi.dy.masa.malilib.config.gui.liteloader.ConfigPanelBase;
import fi.dy.masa.malilib.config.options.IConfigValue;
import fi.dy.masa.malilib.gui.util.ConfigInfoProviderSimple;
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
        List<? extends IConfigValue> configs;
        ConfigInfoProviderSimple provider;

        this.addSubPanel((new GuiModConfigs(modId, Configs.Generic.OPTIONS, "tweakeroo.gui.button.config_gui.generic")).setConfigWidth(120));
        this.addSubPanel((new GuiModConfigs(modId, Configs.Fixes.OPTIONS, "tweakeroo.gui.button.config_gui.fixes")).setConfigWidth(100));
        this.addSubPanel((new GuiModConfigs(modId, Configs.Lists.OPTIONS, "tweakeroo.gui.button.config_gui.lists")).setConfigWidth(200));

        this.addSubPanel(new GuiModConfigs(modId, Hotkeys.HOTKEY_LIST, "tweakeroo.gui.button.config_gui.generic_hotkeys"));

        configs = ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(FeatureToggle.values()));
        this.addSubPanel((new GuiModConfigs(modId, configs, "tweakeroo.gui.button.config_gui.tweak_toggles")).setConfigWidth(100));

        configs = ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, ImmutableList.copyOf(FeatureToggle.values()));
        provider = new ConfigInfoProviderSimple("Hotkey to toggle the '", "' tweak");
        this.addSubPanel(new GuiModConfigs(modId, configs, "tweakeroo.gui.button.config_gui.tweak_hotkeys").setHoverInfoProvider(provider));

        configs = ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(Configs.Disable.OPTIONS));
        this.addSubPanel((new GuiModConfigs(modId, configs, "tweakeroo.gui.button.config_gui.disable_toggle")).setConfigWidth(100));

        configs = ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, ImmutableList.copyOf(Configs.Disable.OPTIONS));
        provider = new ConfigInfoProviderSimple("Hotkey to toggle the '", "' disable option");
        this.addSubPanel(new GuiModConfigs(modId, configs, "tweakeroo.gui.button.config_gui.disable_hotkeys").setHoverInfoProvider(provider));
    }
}
