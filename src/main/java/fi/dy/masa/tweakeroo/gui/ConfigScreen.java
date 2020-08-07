package fi.dy.masa.tweakeroo.gui;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;

public class ConfigScreen extends BaseConfigScreen
{
    private static final BaseConfigTab GENERIC         = new BaseConfigTab("tweakeroo.gui.button.config_gui.generic", 120, false, Configs.Generic.OPTIONS);
    private static final BaseConfigTab FIXES           = new BaseConfigTab("tweakeroo.gui.button.config_gui.fixes", 80, false, Configs.Fixes.OPTIONS);
    private static final BaseConfigTab LISTS           = new BaseConfigTab("tweakeroo.gui.button.config_gui.lists", 204, false, Configs.Lists.OPTIONS);
    private static final BaseConfigTab TWEAK_TOGGLES   = new BaseConfigTab("tweakeroo.gui.button.config_gui.tweak_toggles", 80, false, ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(FeatureToggle.values())));
    private static final BaseConfigTab TWEAK_HOTKEYS   = new BaseConfigTab("tweakeroo.gui.button.config_gui.tweak_hotkeys", 204, true, ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, ImmutableList.copyOf(FeatureToggle.values())));
    private static final BaseConfigTab GENERIC_HOTKEYS = new BaseConfigTab("tweakeroo.gui.button.config_gui.generic_hotkeys", 204, true, Hotkeys.HOTKEY_LIST);
    private static final BaseConfigTab DISABLE_TOGGLES = new BaseConfigTab("tweakeroo.gui.button.config_gui.disable_toggle", 80, false, ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(Configs.Disable.OPTIONS)));
    private static final BaseConfigTab DISABLE_HOTKEYS = new BaseConfigTab("tweakeroo.gui.button.config_gui.disable_hotkeys", 204, true, ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, ImmutableList.copyOf(Configs.Disable.OPTIONS)));
    private static final BaseConfigTab PLACEMENT       = new BaseConfigTab("tweakeroo.gui.button.config_gui.placement", 204, false, ImmutableList.of(),
                                                                           (tab, gui) -> (button, mouseButton) -> { });

    private static final ImmutableList<ConfigTab> TABS = ImmutableList.of(
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

    private static ConfigTab tab = TWEAK_TOGGLES;

    public ConfigScreen()
    {
        super(10, 50, Reference.MOD_ID, null, TABS, "tweakeroo.gui.title.configs");
    }

    @Override
    public ConfigTab getCurrentTab()
    {
        return tab;
    }

    @Override
    public void setCurrentTab(ConfigTab tab)
    {
        ConfigScreen.tab = tab;
    }
}
