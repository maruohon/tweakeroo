package fi.dy.masa.tweakeroo;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.JsonModConfig;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.dispatch.ClientWorldChangeEventDispatcher;
import fi.dy.masa.malilib.event.dispatch.InputDispatcher;
import fi.dy.masa.malilib.event.dispatch.KeyBindManager;
import fi.dy.masa.malilib.event.dispatch.RenderEventDispatcher;
import fi.dy.masa.malilib.gui.config.ConfigSearchInfo;
import fi.dy.masa.malilib.gui.config.ConfigTabRegistry;
import fi.dy.masa.malilib.gui.config.ConfigWidgetRegistry;
import fi.dy.masa.malilib.systems.BlockPlacementPositionHandler;
import fi.dy.masa.tweakeroo.config.Callbacks;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.DisableToggle;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.event.ClientWorldChangeHandler;
import fi.dy.masa.tweakeroo.event.InputHandler;
import fi.dy.masa.tweakeroo.event.RenderHandler;
import fi.dy.masa.tweakeroo.gui.ConfigScreen;
import fi.dy.masa.tweakeroo.gui.widget.DisableToggleConfigWidget;
import fi.dy.masa.tweakeroo.gui.widget.FeatureToggleConfigWidget;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;

public class InitHandler implements InitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.INSTANCE.registerConfigHandler(new JsonModConfig(Reference.MOD_ID, Reference.MOD_NAME, Configs.CATEGORIES, 1));
        ConfigTabRegistry.INSTANCE.registerConfigTabProvider(Reference.MOD_ID, ConfigScreen::getConfigTabs);

        ConfigWidgetRegistry.INSTANCE.registerWidgetFactory(FeatureToggle.class, FeatureToggleConfigWidget::new);
        ConfigWidgetRegistry.INSTANCE.registerWidgetFactory(DisableToggle.class, DisableToggleConfigWidget::new);

        ConfigWidgetRegistry.INSTANCE.registerConfigSearchInfo(FeatureToggle.class, new ConfigSearchInfo<FeatureToggle>(true, true).setBooleanConfigGetter(FeatureToggle::getBooleanConfig).setKeyBindGetter(FeatureToggle::getKeyBind));
        ConfigWidgetRegistry.INSTANCE.registerConfigSearchInfo(DisableToggle.class, new ConfigSearchInfo<DisableToggle>(true, true).setBooleanConfigGetter(DisableToggle::getBooleanConfig).setKeyBindGetter(DisableToggle::getKeyBind));

        KeyBindManager.INSTANCE.registerKeyBindProvider(InputHandler.getInstance());
        InputDispatcher.INSTANCE.registerKeyboardInputHandler(InputHandler.getInstance());
        InputDispatcher.INSTANCE.registerMouseInputHandler(InputHandler.getInstance());

        RenderHandler renderer = new RenderHandler();
        RenderEventDispatcher.INSTANCE.registerGameOverlayRenderer(renderer);
        RenderEventDispatcher.INSTANCE.registerTooltipPostRenderer(renderer);
        RenderEventDispatcher.INSTANCE.registerWorldPostRenderer(renderer);

        BlockPlacementPositionHandler.INSTANCE.registerPositionProvider(PlacementTweaks::getOverriddenPlacementPosition);
        ClientWorldChangeEventDispatcher.INSTANCE.registerClientWorldChangeHandler(new ClientWorldChangeHandler());

        Callbacks.init();
    }
}
