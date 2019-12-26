package fi.dy.masa.tweakeroo.event;

import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import fi.dy.masa.malilib.interfaces.IWorldLoadListener;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

public class WorldLoadListener implements IWorldLoadListener
{
    @Override
    public void onWorldLoadPre(@Nullable ClientWorld worldBefore, @Nullable ClientWorld worldAfter, MinecraftClient mc)
    {
        // Always disable the Free Camera mode when leaving the world or switching dimensions
        FeatureToggle.TWEAK_FREE_CAMERA.setBooleanValue(false);
    }
}
