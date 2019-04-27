package fi.dy.masa.tweakeroo.tweaks;

import java.util.Collection;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

public class MiscTweaks
{
    private static long lastPotionWarning;

    public static void onTick(MinecraftClient mc)
    {
        PlayerEntity player = mc.player;

        if (player == null)
        {
            return;
        }

        if (FeatureToggle.TWEAK_POTION_WARNING.getBooleanValue() &&
            player.getEntityWorld().getTime() - lastPotionWarning >= 100)
        {
            lastPotionWarning = mc.player.getEntityWorld().getTime();

            Collection<StatusEffectInstance> effects = player.getStatusEffects();

            if (effects.isEmpty() == false)
            {
                int minDuration = -1;
                int count = 0;

                for (StatusEffectInstance effectInstance : effects)
                {
                    if (effectInstance.isAmbient() == false &&
                        effectInstance.getDuration() <= Configs.Generic.POTION_WARNING_THRESHOLD.getIntegerValue())
                    {
                        ++count;

                        if (effectInstance.getDuration() < minDuration || minDuration < 0)
                        {
                            minDuration = effectInstance.getDuration();
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
