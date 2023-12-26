package tweakeroo;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;

import malilib.config.JsonModConfig;
import malilib.config.JsonModConfig.ConfigDataUpdater;
import malilib.config.util.ConfigUpdateUtils.ChainedConfigDataUpdater;
import malilib.config.util.ConfigUpdateUtils.ConfigCategoryRenamer;
import malilib.config.util.ConfigUpdateUtils.KeyBindSettingsResetter;
import malilib.event.InitializationHandler;
import malilib.gui.config.ConfigSearchInfo;
import malilib.registry.Registry;
import tweakeroo.config.Callbacks;
import tweakeroo.config.Configs;
import tweakeroo.config.DisableToggle;
import tweakeroo.config.FeatureToggle;
import tweakeroo.event.ClientWorldChangeHandler;
import tweakeroo.event.RenderHandler;
import tweakeroo.feature.Actions;
import tweakeroo.gui.ConfigScreen;
import tweakeroo.gui.widget.DisableToggleConfigWidget;
import tweakeroo.gui.widget.FeatureToggleConfigWidget;
import tweakeroo.gui.widget.info.DisableConfigStatusWidget;
import tweakeroo.gui.widget.info.TweakConfigStatusWidget;
import tweakeroo.input.KeyboardInputHandlerImpl;
import tweakeroo.input.MouseClickHandlerImpl;
import tweakeroo.input.TweakerooHotkeyProvider;
import tweakeroo.tweaks.PlacementTweaks;
import tweakeroo.util.MiscUtils;
import tweakeroo.util.data.BlockRenderOverrides;

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
        Registry.CONFIG_TAB.registerConfigTabSupplier(Reference.MOD_INFO, ConfigScreen::getConfigTabs);

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
        Registry.TICK_EVENT_DISPATCHER.registerClientTickHandler(MiscUtils::onClientTick);

        Registry.BLOCK_PLACEMENT_POSITION_HANDLER.registerPositionProvider(PlacementTweaks::getOverriddenPlacementPosition);
        Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER.registerClientWorldChangeHandler(new ClientWorldChangeHandler());

        Actions.init();
        Callbacks.init();
        BlockRenderOverrides.init();
    }
}
