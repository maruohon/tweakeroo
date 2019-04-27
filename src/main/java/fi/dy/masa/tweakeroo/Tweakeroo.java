package fi.dy.masa.tweakeroo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.tweakeroo.config.Callbacks;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.event.InputHandler;
import fi.dy.masa.tweakeroo.event.RenderHandler;
import fi.dy.masa.tweakeroo.tweaks.MiscTweaks;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;

public class Tweakeroo implements ModInitializer, ClientTickCallback
{
    public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

    public static int renderCountItems;
    public static int renderCountXPOrbs;

    @Override
    public void onInitialize()
    {
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
        ClientTickCallback.EVENT.register(this);
    }

    @Override
    public void tick(MinecraftClient mc)
    {
        PlacementTweaks.onTick(mc);
        MiscTweaks.onTick(mc);

        // Reset the counters after rendering each frame
        renderCountItems = 0;
        renderCountXPOrbs = 0;
    }

    private static class InitHandler implements IInitializationHandler
    {
        @Override
        public void registerModHandlers()
        {
            ConfigManager.getInstance().registerConfigHandler(Reference.MOD_ID, new Configs());

            InputEventHandler.getInstance().registerKeybindProvider(InputHandler.getInstance());
            InputEventHandler.getInstance().registerKeyboardInputHandler(InputHandler.getInstance());
            InputEventHandler.getInstance().registerMouseInputHandler(InputHandler.getInstance());

            IRenderer renderer = new RenderHandler();
            RenderEventHandler.getInstance().registerGameOverlayRenderer(renderer);
            RenderEventHandler.getInstance().registerWorldLastRenderer(renderer);

            Callbacks.init(MinecraftClient.getInstance());
        }
    }
}
