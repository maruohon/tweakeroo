package fi.dy.masa.tweakeroo.event;

import javax.annotation.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import fi.dy.masa.malilib.interfaces.IWorldLoadListener;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

public class WorldLoadListener implements IWorldLoadListener
{
    // Hacky toggle for "fixing" gamma state automatically if left on from previous play session
    private boolean gammaEnabled = false;
    @Override
    public void onWorldLoadPre(@Nullable ClientWorld worldBefore, @Nullable ClientWorld worldAfter, MinecraftClient mc)
    {
        // Always disable the Free Camera mode when leaving the world or switching dimensions
        FeatureToggle.TWEAK_FREE_CAMERA.setBooleanValue(false);
        if (FeatureToggle.TWEAK_GAMMA_OVERRIDE.getBooleanValue()) {
            gammaEnabled = false;
            //Tweakeroo.debugLog("WorldLoadListener#onWorldLoadPre(): tweak of TWEAK_GAMMA_OVERRIDE = false.");
        }
    }
    @Override
    public void onWorldLoadPost(@Nullable ClientWorld worldBefore, @Nullable ClientWorld worldAfter, MinecraftClient mc)
    {
        if (FeatureToggle.TWEAK_GAMMA_OVERRIDE.getBooleanValue())
        {
            if (!gammaEnabled)
            {
                //Tweakeroo.debugLog("WorldLoadListener#onWorldLoadPost(): tweaking TWEAK_GAMMA_OVERRIDE from false -> true.");
                FeatureToggle.TWEAK_GAMMA_OVERRIDE.setBooleanValue(false);
                FeatureToggle.TWEAK_GAMMA_OVERRIDE.setBooleanValue(true);
                gammaEnabled = true;
            }
        }
    }
}
