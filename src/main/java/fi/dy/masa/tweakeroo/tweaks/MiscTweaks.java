package fi.dy.masa.tweakeroo.tweaks;

import java.util.Collection;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;

public class MiscTweaks
{
    private static long lastPotionWarning;

    public static void onTick(Minecraft mc)
    {
        EntityPlayer player = mc.player;

        if (FeatureToggle.TWEAK_POTION_WARNING.getBooleanValue() && player != null &&
            player.getEntityWorld().getTotalWorldTime() - lastPotionWarning >= 100)
        {
            lastPotionWarning = mc.player.getEntityWorld().getTotalWorldTime();

            Collection<PotionEffect> effects = player.getActivePotionEffects();

            if (effects.isEmpty() == false)
            {
                int minDuration = -1;
                int count = 0;

                for (PotionEffect effect : effects)
                {
                    if (effect.getIsAmbient() == false && effect.getDuration() <= Configs.Generic.POTION_WARNING_THRESHOLD.getIntegerValue())
                    {
                        ++count;

                        if (effect.getDuration() < minDuration || minDuration < 0)
                        {
                            minDuration = effect.getDuration();
                        }
                    }
                }

                if (count > 0)
                {
                    StringUtils.printActionbarMessage("tweakeroo.message.potion_effects_running_out",
                            Integer.valueOf(count), Integer.valueOf(minDuration / 20));
                }
            }
        }
    }
}
