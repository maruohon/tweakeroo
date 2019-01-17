package fi.dy.masa.tweakeroo.tweaks;

import java.util.Collection;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.IMinecraftAccessor;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;

public class MiscTweaks
{
    private static long lastPotionWarning;
    private static int periodicAttackCounter;
    private static int periodicUseCounter;

    public static void onTick(Minecraft mc)
    {
        EntityPlayer player = mc.player;

        if (player == null)
        {
            return;
        }

        if (mc.currentScreen == null)
        {
            if (FeatureToggle.TWEAK_PERIODIC_ATTACK.getBooleanValue() &&
                ++periodicAttackCounter >= Configs.Generic.PERIODIC_ATTACK_INTERVAL.getIntegerValue())
            {
                ((IMinecraftAccessor) mc).leftClickMouseAccessor();
                periodicAttackCounter = 0;
            }

            if (FeatureToggle.TWEAK_PERIODIC_USE.getBooleanValue() &&
                ++periodicUseCounter >= Configs.Generic.PERIODIC_USE_INTERVAL.getIntegerValue())
            {
                ((IMinecraftAccessor) mc).rightClickMouseAccessor();
                periodicUseCounter = 0;
            }
        }

        if (FeatureToggle.TWEAK_POTION_WARNING.getBooleanValue() &&
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

        if (FeatureToggle.TWEAK_REPAIR_MODE.getBooleanValue())
        {
            InventoryUtils.repairModeSwapItems(player);
        }
    }
}
