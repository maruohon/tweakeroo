package fi.dy.masa.tweakeroo.tweaks;

import java.util.Collection;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.mixin.IMixinFlatGenSettings;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.IMinecraftAccessor;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.PotionRestriction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.gen.FlatLayerInfo;

public class MiscTweaks
{
    public static final PotionRestriction POTION_RESTRICTION = new PotionRestriction();

    private static long lastPotionWarning;
    private static int periodicAttackCounter;
    private static int periodicUseCounter;

    public static void onTick(Minecraft mc)
    {
        EntityPlayerSP player = mc.player;

        if (player == null)
        {
            return;
        }

        doPeriodicClicks(mc);
        doPotionWarnings(player);

        if (FeatureToggle.TWEAK_REPAIR_MODE.getBooleanValue())
        {
            InventoryUtils.repairModeSwapItems(player);
        }

        CameraEntity.movementTick(player.movementInput.sneak, player.movementInput.jump);
    }

    private static void doPeriodicClicks(Minecraft mc)
    {
        if (GuiUtils.getCurrentScreen() == null)
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
    }

    private static void doPotionWarnings(EntityPlayer player)
    {
        if (FeatureToggle.TWEAK_POTION_WARNING.getBooleanValue() &&
            player.getEntityWorld().getGameTime() - lastPotionWarning >= 100)
        {
            lastPotionWarning = player.getEntityWorld().getGameTime();

            Collection<PotionEffect> effects = player.getActivePotionEffects();

            if (effects.isEmpty() == false)
            {
                int minDuration = -1;
                int count = 0;

                for (PotionEffect effect : effects)
                {
                    if (potionWarningShouldInclude(effect))
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
                    InfoUtils.printActionbarMessage("tweakeroo.message.potion_effects_running_out",
                            Integer.valueOf(count), Integer.valueOf(minDuration / 20));
                }
            }
        }
    }

    private static boolean potionWarningShouldInclude(PotionEffect effect)
    {
        return effect.isAmbient() == false &&
               (effect.getPotion().isBeneficial() || Configs.Generic.POTION_WARNING_BENEFICIAL_ONLY.getBooleanValue() == false) &&
               effect.getDuration() <= Configs.Generic.POTION_WARNING_THRESHOLD.getIntegerValue() &&
               POTION_RESTRICTION.isAllowed(effect.getPotion());
    }

    @Nullable
    public static FlatLayerInfo[] parseBlockString(String blockString)
    {
        return IMixinFlatGenSettings.getLayersFromStringInvoker(blockString).toArray(new FlatLayerInfo[0]);
    }
}
