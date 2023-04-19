package tweakeroo;

import java.io.File;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;

import malilib.registry.Registry;
import tweakeroo.gui.TweakerooConfigPanel;
import tweakeroo.tweaks.PlacementTweaks;

public class LiteModTweakeroo implements LiteMod, Configurable, Tickable
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
        Registry.INITIALIZATION_DISPATCHER.registerInitializationHandler(new InitHandler());
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath)
    {
    }

    @Override
    public void onTick(Minecraft mc, float partialTicks, boolean inGame, boolean clock)
    {
        PlacementTweaks.onTick(mc);

        // Reset the counters after rendering each frame
        renderCountItems = 0;
        renderCountXPOrbs = 0;
    }
}
