package fi.dy.masa.tweakeroo;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;
import fi.dy.masa.malilib.config.JsonModConfig;
import fi.dy.masa.malilib.config.JsonModConfig.ConfigDataUpdater;
import fi.dy.masa.malilib.config.util.ConfigUpdateUtils.ChainedConfigDataUpdater;
import fi.dy.masa.malilib.config.util.ConfigUpdateUtils.ConfigCategoryRenamer;
import fi.dy.masa.malilib.config.util.ConfigUpdateUtils.KeyBindSettingsResetter;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.gui.config.ConfigSearchInfo;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.tweakeroo.config.Callbacks;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.DisableToggle;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.event.ClientWorldChangeHandler;
import fi.dy.masa.tweakeroo.event.RenderHandler;
import fi.dy.masa.tweakeroo.feature.Actions;
import fi.dy.masa.tweakeroo.gui.ConfigScreen;
import fi.dy.masa.tweakeroo.gui.widget.DisableToggleConfigWidget;
import fi.dy.masa.tweakeroo.gui.widget.FeatureToggleConfigWidget;
import fi.dy.masa.tweakeroo.gui.widget.info.DisableConfigStatusWidget;
import fi.dy.masa.tweakeroo.gui.widget.info.TweakConfigStatusWidget;
import fi.dy.masa.tweakeroo.input.KeyboardInputHandlerImpl;
import fi.dy.masa.tweakeroo.input.MouseClickHandlerImpl;
import fi.dy.masa.tweakeroo.input.TweakerooHotkeyProvider;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import fi.dy.masa.tweakeroo.util.data.BlockRenderOverrides;

public class InitHandler implements InitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        // Reset all KeyBindSettings when updating to the first post-malilib-refactor version
        ConfigDataUpdater keyUpdater = new KeyBindSettingsResetter(TweakerooHotkeyProvider.INSTANCE::getAllHotkeys, 0);
        ConfigDataUpdater categoryUpdater = new ConfigCategoryRenamer(ImmutableList.of(Pair.of("DisableToggles", "YeetToggles"), Pair.of("DisableHotkeys", "YeetHotkeys")), 0, 0);
        ConfigDataUpdater updater = new ChainedConfigDataUpdater(categoryUpdater, keyUpdater);
        Registry.CONFIG_MANAGER.registerConfigHandler(JsonModConfig.createJsonModConfig(Reference.MOD_INFO, Configs.CURRENT_VERSION, Configs.CATEGORIES, updater));

        Registry.CONFIG_SCREEN.registerConfigScreenFactory(Reference.MOD_INFO, ConfigScreen::create);
        Registry.CONFIG_TAB.registerConfigTabProvider(Reference.MOD_INFO, ConfigScreen::getConfigTabs);

        Registry.CONFIG_WIDGET.registerConfigWidgetFactory(FeatureToggle.class, FeatureToggleConfigWidget::new);
        Registry.CONFIG_WIDGET.registerConfigWidgetFactory(DisableToggle.class, DisableToggleConfigWidget::new);

        Registry.CONFIG_WIDGET.registerConfigSearchInfo(FeatureToggle.class, new ConfigSearchInfo<FeatureToggle>(true, true).setBooleanStorageGetter(FeatureToggle::getBooleanConfig).setKeyBindGetter(FeatureToggle::getKeyBind));
        Registry.CONFIG_WIDGET.registerConfigSearchInfo(DisableToggle.class, new ConfigSearchInfo<DisableToggle>(true, true).setBooleanStorageGetter(DisableToggle::getBooleanConfig).setKeyBindGetter(DisableToggle::getKeyBind));

        Registry.CONFIG_STATUS_WIDGET.registerConfigStatusWidgetFactory(DisableToggle.class, DisableConfigStatusWidget::new, "tweakeroo:csi_value_disable_toggle");
        Registry.CONFIG_STATUS_WIDGET.registerConfigStatusWidgetFactory(FeatureToggle.class, TweakConfigStatusWidget::new, "tweakeroo:csi_value_tweak_toggle");

        Registry.HOTKEY_MANAGER.registerHotkeyProvider(TweakerooHotkeyProvider.INSTANCE);
        Registry.INPUT_DISPATCHER.registerKeyboardInputHandler(KeyboardInputHandlerImpl.INSTANCE);
        Registry.INPUT_DISPATCHER.registerMouseClickHandler(new MouseClickHandlerImpl());

        RenderHandler renderer = new RenderHandler();
        Registry.RENDER_EVENT_DISPATCHER.registerGameOverlayRenderer(renderer);
        Registry.RENDER_EVENT_DISPATCHER.registerTooltipPostRenderer(renderer);
        Registry.RENDER_EVENT_DISPATCHER.registerWorldPostRenderer(renderer);

        Registry.BLOCK_PLACEMENT_POSITION_HANDLER.registerPositionProvider(PlacementTweaks::getOverriddenPlacementPosition);
        Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER.registerClientWorldChangeHandler(new ClientWorldChangeHandler());

        Actions.init();
        Callbacks.init();
        BlockRenderOverrides.init();
    }
}
