package fi.dy.masa.tweakeroo.gui;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.ScreenTab;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.DisableToggle;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;

public class ConfigScreen
{
    private static final BaseConfigTab GENERIC         = new BaseConfigTab("tweakeroo.gui.button.config_gui.generic",           Reference.MOD_NAME, 160, Configs.Generic.OPTIONS, ConfigScreen::create);
    private static final BaseConfigTab LISTS           = new BaseConfigTab("tweakeroo.gui.button.config_gui.lists",             Reference.MOD_NAME, 200, Configs.Lists.OPTIONS, ConfigScreen::create);
    private static final BaseConfigTab FIXES           = new BaseConfigTab("tweakeroo.gui.button.config_gui.fixes",             Reference.MOD_NAME,  -1, Configs.Fixes.OPTIONS, ConfigScreen::create);
    private static final BaseConfigTab TWEAK_TOGGLES   = new BaseConfigTab("tweakeroo.gui.button.config_gui.tweaks",            Reference.MOD_NAME, 200, FeatureToggle.VALUES, ConfigScreen::create);
    private static final BaseConfigTab DISABLE_TOGGLES = new BaseConfigTab("tweakeroo.gui.button.config_gui.disable_toggles",   Reference.MOD_NAME, 200, DisableToggle.VALUES, ConfigScreen::create);
    private static final BaseConfigTab GENERIC_HOTKEYS = new BaseConfigTab("tweakeroo.gui.button.config_gui.generic_hotkeys",   Reference.MOD_NAME, 160, Hotkeys.HOTKEY_LIST, ConfigScreen::create);
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

    public static BaseConfigScreen create(@Nullable GuiScreen currentScreen)
    {
        return new BaseConfigScreen(Reference.MOD_ID, null, ALL_TABS, TWEAK_TOGGLES, "tweakeroo.gui.title.configs");
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
