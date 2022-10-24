package fi.dy.masa.tweakeroo.gui;

import java.util.ArrayList;
import com.google.common.collect.ImmutableList;

import malilib.config.option.ConfigInfo;
import malilib.config.util.ConfigUtils;
import malilib.gui.BaseScreen;
import malilib.gui.config.BaseConfigScreen;
import malilib.gui.config.BaseConfigTab;
import malilib.gui.config.ConfigTab;
import malilib.gui.tab.ScreenTab;
import malilib.util.data.ModInfo;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.DisableToggle;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;

public class ConfigScreen
{
    public static final ModInfo MOD_INFO = Reference.MOD_INFO;

    private static final BaseConfigTab GENERIC         = new BaseConfigTab(MOD_INFO, "generic", 160, getGenericConfigs(),   ConfigScreen::create);
    private static final BaseConfigTab LISTS           = new BaseConfigTab(MOD_INFO, "lists",   200, Configs.Lists.OPTIONS, ConfigScreen::create);
    private static final BaseConfigTab FIXES           = new BaseConfigTab(MOD_INFO, "fixes",    -1, Configs.Fixes.OPTIONS, ConfigScreen::create);
    private static final BaseConfigTab TWEAK_TOGGLES   = new BaseConfigTab(MOD_INFO, "tweaks",  200, FeatureToggle.VALUES,  ConfigScreen::create);
    private static final BaseConfigTab DISABLE_TOGGLES = new BaseConfigTab(MOD_INFO, "yeets",   200, DisableToggle.VALUES,  ConfigScreen::create);
    private static final BaseConfigTab GENERIC_HOTKEYS = new BaseConfigTab(MOD_INFO, "hotkeys", 160, getHotkeys(),          ConfigScreen::create);
    //private static final BaseScreenTab PLACEMENT       = new BaseScreenTab("tweakeroo.gui.button.config_gui.placement", null, ConfigScreen::openPlacementStuffScreen);

    private static final ImmutableList<ConfigTab> CONFIG_TABS = ImmutableList.of(
            GENERIC,
            LISTS,
            FIXES,
            TWEAK_TOGGLES,
            DISABLE_TOGGLES,
            GENERIC_HOTKEYS
    );

    private static final ImmutableList<ScreenTab> ALL_TABS = ImmutableList.of(
            GENERIC,
            LISTS,
            FIXES,
            TWEAK_TOGGLES,
            DISABLE_TOGGLES,
            GENERIC_HOTKEYS
    );

    public static void open()
    {
        BaseScreen.openScreen(create());
    }

    public static BaseConfigScreen create()
    {
        // The parent screen should not be set here, to prevent infinite recursion via
        // the call to the parent's setWorldAndResolution -> initScreen -> switch tab -> etc.
        return new BaseConfigScreen(MOD_INFO, ALL_TABS, TWEAK_TOGGLES, "tweakeroo.title.screen.configs", Reference.MOD_VERSION);
    }

    public static ImmutableList<ConfigTab> getConfigTabs()
    {
        return CONFIG_TABS;
    }

    private static ImmutableList<ConfigInfo> getGenericConfigs()
    {
        ArrayList<ConfigInfo> list = new ArrayList<>(Configs.Generic.OPTIONS);

        list.add(ConfigUtils.extractOptionsToExpandableGroup(list, MOD_INFO, "fly_speed", c -> c.getName().startsWith("flySpeed")));
        list.add(ConfigUtils.extractOptionsToExpandableGroup(list, MOD_INFO, "snap_aim", c -> c.getName().startsWith("snapAim")));

        ConfigUtils.sortConfigsByDisplayName(list);

        return ImmutableList.copyOf(list);
    }

    private static ImmutableList<ConfigInfo> getHotkeys()
    {
        ArrayList<ConfigInfo> list = new ArrayList<>(Hotkeys.HOTKEY_LIST);

        list.add(ConfigUtils.extractOptionsToExpandableGroup(list, MOD_INFO, "hotkey.breaking_restriction",  c -> c.getName().startsWith("breakingRestriction")));
        list.add(ConfigUtils.extractOptionsToExpandableGroup(list, MOD_INFO, "hotkey.fly_speed",             c -> c.getName().startsWith("flySpeed")));
        list.add(ConfigUtils.extractOptionsToExpandableGroup(list, MOD_INFO, "hotkey.placement_restriction", c -> c.getName().startsWith("placementRestriction")));

        ConfigUtils.sortConfigsByDisplayName(list);

        return ImmutableList.copyOf(list);
    }

    /*
    private static PlacementStuffScreen openPlacementStuffScreen()
    {
        PlacementStuffScreen screen = new PlacementStuffScreen();
        screen.setCurrentTab(PLACEMENT);
        return PlacementStuffScreen;
    }
    */
}
