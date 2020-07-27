package fi.dy.masa.tweakeroo;

import net.minecraft.client.Minecraft;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.dispatch.ClientWorldChangeEventDispatcher;
import fi.dy.masa.malilib.event.dispatch.InputEventDispatcher;
import fi.dy.masa.malilib.event.dispatch.RenderEventDispatcher;
import fi.dy.masa.malilib.event.IInitializationHandler;
import fi.dy.masa.malilib.systems.BlockPlacementPositionHandler;
import fi.dy.masa.tweakeroo.config.Callbacks;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.event.ClientWorldChangeHandler;
import fi.dy.masa.tweakeroo.event.InputHandler;
import fi.dy.masa.tweakeroo.event.RenderHandler;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;

public class InitHandler implements IInitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.INSTANCE.registerConfigHandler(Reference.MOD_ID, new Configs());

        InputEventDispatcher.getKeyBindManager().registerKeyBindProvider(InputHandler.getInstance());
        InputEventDispatcher.getInputManager().registerKeyboardInputHandler(InputHandler.getInstance());
        InputEventDispatcher.getInputManager().registerMouseInputHandler(InputHandler.getInstance());

        RenderHandler renderer = new RenderHandler();
        RenderEventDispatcher.INSTANCE.registerGameOverlayRenderer(renderer);
        RenderEventDispatcher.INSTANCE.registerTooltipPostRenderer(renderer);
        RenderEventDispatcher.INSTANCE.registerWorldPostRenderer(renderer);

        BlockPlacementPositionHandler.INSTANCE.registerPositionProvider(PlacementTweaks::getOverriddenPlacementPosition);
        ClientWorldChangeEventDispatcher.INSTANCE.registerClientWorldChangeHandler(new ClientWorldChangeHandler());

        Callbacks.init(Minecraft.getMinecraft());
    }
}
