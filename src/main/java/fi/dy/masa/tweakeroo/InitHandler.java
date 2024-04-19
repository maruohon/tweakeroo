package fi.dy.masa.tweakeroo;

import net.minecraft.client.MinecraftClient;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.interfaces.IWorldLoadListener;
import fi.dy.masa.tweakeroo.config.Callbacks;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.event.ClientTickHandler;
import fi.dy.masa.tweakeroo.event.InputHandler;
import fi.dy.masa.tweakeroo.event.RenderHandler;
import fi.dy.masa.tweakeroo.event.WorldLoadListener;

public class InitHandler implements IInitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.getInstance().registerConfigHandler(Reference.MOD_ID, new Configs());

        InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());
        InputEventHandler.getInputManager().registerKeyboardInputHandler(InputHandler.getInstance());
        InputEventHandler.getInputManager().registerMouseInputHandler(InputHandler.getInstance());

        IRenderer renderer = new RenderHandler();
        RenderEventHandler.getInstance().registerGameOverlayRenderer(renderer);
        RenderEventHandler.getInstance().registerTooltipLastRenderer(renderer);
        RenderEventHandler.getInstance().registerWorldLastRenderer(renderer);

        TickHandler.getInstance().registerClientTickHandler(new ClientTickHandler());

        IWorldLoadListener worldListener = new WorldLoadListener();
        WorldLoadHandler.getInstance().registerWorldLoadPreHandler(worldListener);
        WorldLoadHandler.getInstance().registerWorldLoadPostHandler(worldListener);

        Callbacks.init(MinecraftClient.getInstance());
    }
}
