package fi.dy.masa.tweakeroo;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.ShutdownListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.tweakeroo.config.Callbacks;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.gui.TweakerooConfigPanel;
import fi.dy.masa.tweakeroo.event.InputHandler;
import fi.dy.masa.tweakeroo.event.RenderHandler;
import fi.dy.masa.tweakeroo.tweaks.MiscTweaks;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import net.minecraft.client.Minecraft;

public class LiteModTweakeroo implements LiteMod, Configurable, InitCompleteListener, ShutdownListener, Tickable
{
    public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

    public static int renderCountItems;
    public static int renderCountXPOrbs;

    public LiteModTweakeroo()
    {
    }

    @Override
    public String getName()
    {
        return Reference.MOD_NAME;
    }

    @Override
    public String getVersion()
    {
        return Reference.MOD_VERSION;
    }

    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass()
    {
        return TweakerooConfigPanel.class;
    }

    @Override
    public void init(File configPath)
    {
        Configs.loadFromFile();
        ConfigManager.getInstance().registerConfigHandler(Reference.MOD_ID, new Configs());

        InputEventHandler.getInstance().registerKeybindProvider(InputHandler.getInstance());
        InputEventHandler.getInstance().registerKeyboardInputHandler(InputHandler.getInstance());
        InputEventHandler.getInstance().registerMouseInputHandler(InputHandler.getInstance());

        IRenderer renderer = new RenderHandler();
        RenderEventHandler.getInstance().registerGameOverlayRenderer(renderer);
        RenderEventHandler.getInstance().registerWorldLastRenderer(renderer);
    }

    @Override
    public void onInitCompleted(Minecraft mc, LiteLoader loader)
    {
        Callbacks.init(mc);
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath)
    {
    }

    @Override
    public void onTick(Minecraft mc, float partialTicks, boolean inGame, boolean clock)
    {
        PlacementTweaks.onTick(mc);
        MiscTweaks.onTick(mc);

        // Reset the counters after rendering each frame
        renderCountItems = 0;
        renderCountXPOrbs = 0;
    }

    @Override
    public void onShutDown()
    {
        // Does this even do anything in here anymore? Ie. when are the settings saved?
        Callbacks.FEATURE_CALLBACK_GAMMA.restoreOriginalGamma();
    }
}
