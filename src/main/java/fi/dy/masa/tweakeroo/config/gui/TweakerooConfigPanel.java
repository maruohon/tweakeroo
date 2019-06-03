package fi.dy.masa.tweakeroo.config.gui;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.gui.ConfigPanelBase;
import fi.dy.masa.malilib.config.gui.GuiModConfigs;
import fi.dy.masa.malilib.gui.ConfigInfoProviderSimple;
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

        this.addSubPanel((new GuiModConfigs(modId, "Generic", Configs.Generic.OPTIONS)).setConfigWidth(120));
        this.addSubPanel((new GuiModConfigs(modId, "Fixes", Configs.Fixes.OPTIONS)).setConfigWidth(100));
        this.addSubPanel((new GuiModConfigs(modId, "Lists", Configs.Lists.OPTIONS)).setConfigWidth(200));

        this.addSubPanel(new GuiModConfigs(modId, "Generic Hotkeys", Hotkeys.HOTKEY_LIST));

        configs = ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(FeatureToggle.values()));
        this.addSubPanel((new GuiModConfigs(modId, "Tweak Toggles", configs)).setConfigWidth(100));

        configs = ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, ImmutableList.copyOf(FeatureToggle.values()));
        provider = new ConfigInfoProviderSimple("Hotkey to toggle the '", "' tweak");
        this.addSubPanel(new GuiModConfigs(modId, "Tweak Hotkeys", configs).setHoverInfoProvider(provider));

        configs = ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(Configs.Disable.OPTIONS));
        this.addSubPanel((new GuiModConfigs(modId, "Disable Options", configs)).setConfigWidth(100));

        configs = ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(Configs.Disable.OPTIONS));
        provider = new ConfigInfoProviderSimple("Hotkey to toggle the '", "' disable option");
        this.addSubPanel(new GuiModConfigs(modId, "Disable Option Hotkeys", configs).setHoverInfoProvider(provider));
    }
}
