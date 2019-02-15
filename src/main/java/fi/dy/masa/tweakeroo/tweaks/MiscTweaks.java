package fi.dy.masa.tweakeroo.tweaks;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.tweakeroo.LiteModTweakeroo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.IMinecraftAccessor;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.ListType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class MiscTweaks
{
    private static final Set<Potion> POTION_WARNING_BLACKLIST = new HashSet<>();
    private static final Set<Potion> POTION_WARNING_WHITELIST = new HashSet<>();

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

        doPeriodicClicks(mc);
        doPotionWarnings(player);

        if (FeatureToggle.TWEAK_REPAIR_MODE.getBooleanValue())
        {
            InventoryUtils.repairModeSwapItems(player);
        }
    }

    private static void doPeriodicClicks(Minecraft mc)
    {
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
    }

    private static void doPotionWarnings(EntityPlayer player)
    {
        if (FeatureToggle.TWEAK_POTION_WARNING.getBooleanValue() &&
            player.getEntityWorld().getTotalWorldTime() - lastPotionWarning >= 100)
        {
            lastPotionWarning = player.getEntityWorld().getTotalWorldTime();

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
        return effect.getIsAmbient() == false &&
               (effect.getPotion().isBeneficial() || Configs.Generic.POTION_WARNING_BENEFICIAL_ONLY.getBooleanValue() == false) &&
               effect.getDuration() <= Configs.Generic.POTION_WARNING_THRESHOLD.getIntegerValue() &&
               potionWarningListsAllowEffect(effect.getPotion());
    }

    private static boolean potionWarningListsAllowEffect(Potion effect)
    {
        ListType type = (ListType) Configs.Lists.POTION_WARNING_LIST_TYPE.getOptionListValue();

        if (type == ListType.NONE)
        {
            return true;
        }
        else if (type == ListType.WHITELIST)
        {
            return POTION_WARNING_WHITELIST.contains(effect);
        }
        else if (type == ListType.BLACKLIST)
        {
            return POTION_WARNING_BLACKLIST.contains(effect) == false;
        }

        return true;
    }

    public static void setPotionWarningLists(List<String> blacklist, List<String> whitelist)
    {
        POTION_WARNING_BLACKLIST.clear();
        POTION_WARNING_WHITELIST.clear();

        populatePotionList(POTION_WARNING_BLACKLIST, blacklist);
        populatePotionList(POTION_WARNING_WHITELIST, whitelist);
    }

    private static void populatePotionList(Set<Potion> set, List<String> names)
    {
        for (String name : names)
        {
            try
            {
                if (name.isEmpty() == false)
                {
                    Potion effect = Potion.REGISTRY.getObject(new ResourceLocation(name));

                    if (effect != null)
                    {
                        set.add(effect);
                    }
                }
            }
            catch (Exception e)
            {
                LiteModTweakeroo.logger.warn("Invalid potion effect name '{}'", name);
            }
        }
    }
}
