package fi.dy.masa.tweakeroo.gui;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.tab.ScreenTab;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import fi.dy.masa.malilib.util.data.ModInfo;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.DisableToggle;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;

public class ConfigScreen
{
    public static final ModInfo MOD_INFO = Reference.MOD_INFO;

    private static final BaseConfigTab GENERIC         = new BaseConfigTab(MOD_INFO, "generic",         160, Configs.Generic.OPTIONS, ConfigScreen::create);
    private static final BaseConfigTab LISTS           = new BaseConfigTab(MOD_INFO, "lists",           200, Configs.Lists.OPTIONS,   ConfigScreen::create);
    private static final BaseConfigTab FIXES           = new BaseConfigTab(MOD_INFO, "fixes",            -1, Configs.Fixes.OPTIONS,   ConfigScreen::create);
    private static final BaseConfigTab TWEAK_TOGGLES   = new BaseConfigTab(MOD_INFO, "tweaks",          200, FeatureToggle.VALUES,    ConfigScreen::create);
    private static final BaseConfigTab DISABLE_TOGGLES = new BaseConfigTab(MOD_INFO, "disable_toggles", 200, DisableToggle.VALUES,    ConfigScreen::create);
    private static final BaseConfigTab GENERIC_HOTKEYS = new BaseConfigTab(MOD_INFO, "generic_hotkeys", 160, Hotkeys.HOTKEY_LIST,     ConfigScreen::create);
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
        BaseScreen.openScreen(create(null));
    }

    public static BaseConfigScreen create(@Nullable GuiScreen currentScreen)
    {
        // The parent screen should not be set here, to prevent infinite recursion via
        // the call to the parent's setWorldAndResolution -> initScreen -> switch tab -> etc.
        return new BaseConfigScreen(MOD_INFO, null, ALL_TABS, TWEAK_TOGGLES, "tweakeroo.gui.title.configs");
    }


    public static ImmutableList<ConfigTab> getConfigTabs()
    {
        return CONFIG_TABS;
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
