package fi.dy.masa.tweakeroo.event;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.WorldClient;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

public class ClientWorldChangeHandler implements fi.dy.masa.malilib.event.ClientWorldChangeHandler
{
    @Override
    public void onPreClientWorldChange(@Nullable WorldClient worldBefore, @Nullable WorldClient worldAfter)
    {
        // Always disable the Free Camera mode when leaving the world or switching dimensions
        FeatureToggle.TWEAK_FREE_CAMERA.getBooleanConfig().setValue(false);
    }
}
