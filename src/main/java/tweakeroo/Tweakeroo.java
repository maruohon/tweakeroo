package tweakeroo;

import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import malilib.registry.Registry;

public class Tweakeroo implements ClientModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    @Override
    public void initClient()
    {
        Registry.INITIALIZATION_DISPATCHER.registerInitializationHandler(new InitHandler());
    }
}
