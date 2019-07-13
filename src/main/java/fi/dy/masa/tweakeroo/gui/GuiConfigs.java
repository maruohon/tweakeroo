package fi.dy.masa.tweakeroo.gui;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.gui.ConfigGuiTabBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.interfaces.IConfigGuiTab;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;

public class GuiConfigs extends GuiConfigsBase
{
    private static final ConfigGuiTabBase GENERIC         = new ConfigGuiTabBase("tweakeroo.gui.button.config_gui.generic",         120, false, Configs.Generic.OPTIONS);
    private static final ConfigGuiTabBase FIXES           = new ConfigGuiTabBase("tweakeroo.gui.button.config_gui.fixes",            80, false, Configs.Fixes.OPTIONS);
    private static final ConfigGuiTabBase LISTS           = new ConfigGuiTabBase("tweakeroo.gui.button.config_gui.lists",           204, false, Configs.Lists.OPTIONS);
    private static final ConfigGuiTabBase TWEAK_TOGGLES   = new ConfigGuiTabBase("tweakeroo.gui.button.config_gui.tweak_toggles",    80, false, ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(FeatureToggle.values())));
    private static final ConfigGuiTabBase TWEAK_HOTKEYS   = new ConfigGuiTabBase("tweakeroo.gui.button.config_gui.tweak_hotkeys",   204, true,  ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, ImmutableList.copyOf(FeatureToggle.values())));
    private static final ConfigGuiTabBase GENERIC_HOTKEYS = new ConfigGuiTabBase("tweakeroo.gui.button.config_gui.generic_hotkeys", 204, true,  Hotkeys.HOTKEY_LIST);
    private static final ConfigGuiTabBase DISABLE_TOGGLES = new ConfigGuiTabBase("tweakeroo.gui.button.config_gui.disable_toggle",   80, false, ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(Configs.Disable.OPTIONS)));
    private static final ConfigGuiTabBase DISABLE_HOTKEYS = new ConfigGuiTabBase("tweakeroo.gui.button.config_gui.disable_hotkeys", 204, true,  ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, ImmutableList.copyOf(Configs.Disable.OPTIONS)));
    private static final ConfigGuiTabBase PLACEMENT       = new ConfigGuiTabBase("tweakeroo.gui.button.config_gui.placement",       204, false, ImmutableList.of(),
            (tab, gui) -> (button, mouseButton) -> { });

    private static final ImmutableList<IConfigGuiTab> TABS = ImmutableList.of(
            GENERIC,
            FIXES,
            LISTS,
            TWEAK_TOGGLES,
            TWEAK_HOTKEYS,
            GENERIC_HOTKEYS,
            DISABLE_TOGGLES,
            DISABLE_HOTKEYS,
            PLACEMENT
    );

    private static IConfigGuiTab tab = TWEAK_TOGGLES;

    public GuiConfigs()
    {
        super(10, 50, Reference.MOD_ID, null, TABS, "tweakeroo.gui.title.configs");
    }

    @Override
    public IConfigGuiTab getCurrentTab()
    {
        return tab;
    }

    @Override
    public void setCurrentTab(IConfigGuiTab tab)
    {
        GuiConfigs.tab = tab;
    }
}
