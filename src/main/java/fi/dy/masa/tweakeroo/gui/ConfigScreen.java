package fi.dy.masa.tweakeroo.gui;

import com.google.common.collect.ImmutableList;
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
    private static final BaseConfigTab GENERIC         = new BaseConfigTab("tweakeroo.gui.button.config_gui.generic",           Reference.MOD_NAME, 160, Configs.Generic.OPTIONS);
    private static final BaseConfigTab FIXES           = new BaseConfigTab("tweakeroo.gui.button.config_gui.fixes",             Reference.MOD_NAME,  -1, Configs.Fixes.OPTIONS);
    private static final BaseConfigTab LISTS           = new BaseConfigTab("tweakeroo.gui.button.config_gui.lists",             Reference.MOD_NAME, 200, Configs.Lists.OPTIONS);
    private static final BaseConfigTab TWEAK_TOGGLES   = new BaseConfigTab("tweakeroo.gui.button.config_gui.tweaks",            Reference.MOD_NAME, 200, FeatureToggle.VALUES);
    private static final BaseConfigTab DISABLE_TOGGLES = new BaseConfigTab("tweakeroo.gui.button.config_gui.disable_toggles",   Reference.MOD_NAME, 200, DisableToggle.VALUES);
    private static final BaseConfigTab GENERIC_HOTKEYS = new BaseConfigTab("tweakeroo.gui.button.config_gui.generic_hotkeys",   Reference.MOD_NAME, 160, Hotkeys.HOTKEY_LIST);
    private static final BaseConfigTab PLACEMENT       = new BaseConfigTab("tweakeroo.gui.button.config_gui.placement",         Reference.MOD_NAME, 204, ImmutableList.of(),
                                                                           (tab, gui) -> (button, mouseButton) -> openPlacementStuffScreen(gui));

    private static final ImmutableList<ConfigTab> TABS = ImmutableList.of(
            GENERIC,
            FIXES,
            LISTS,
            TWEAK_TOGGLES,
            DISABLE_TOGGLES,
            GENERIC_HOTKEYS,
            PLACEMENT
    );

    public static BaseConfigScreen create()
    {
        return new BaseConfigScreen(10, 50, Reference.MOD_ID, null, TABS, TWEAK_TOGGLES, "tweakeroo.gui.title.configs");
    }


    public static ImmutableList<ConfigTab> getConfigTabs()
    {
        return TABS;
    }

    private static boolean openPlacementStuffScreen(BaseConfigScreen screen)
    {
        screen.setCurrentTab(PLACEMENT);
        //BaseScreen.openGui(new PlacementStuffScreen());
        return true;
    }
}
