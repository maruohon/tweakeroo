package fi.dy.masa.tweakeroo.tweaks;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.gen.FlatLayerInfo;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.restrictions.ItemRestriction;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction.ListType;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.mixin.IMixinFlatGeneratorInfo;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.IMinecraftAccessor;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.PotionRestriction;
import fi.dy.masa.tweakeroo.util.SoundRestriction;

public class MiscTweaks
{
    private static final ItemRestriction ITEM_GLINT_RESTRICTION = new ItemRestriction();
    private static final PotionRestriction POTION_RESTRICTION = new PotionRestriction();
    private static final SoundRestriction SOUND_RESTRICTION = new SoundRestriction();

    private static int potionWarningTimer;
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
            ++potionWarningTimer >= 100)
        {
            potionWarningTimer = 0;

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
               POTION_RESTRICTION.isAllowed(effect.getPotion());
    }

    public static boolean shouldPreventItemGlintFor(ItemStack stack)
    {
        return Configs.Disable.DISABLE_ITEM_GLINT.getBooleanValue() && ITEM_GLINT_RESTRICTION.isAllowed(stack.getItem()) == false;
    }

    public static boolean shouldDisableSound(ISound sound)
    {
        return Configs.Disable.DISABLE_SOUNDS_LIST.getBooleanValue() && SOUND_RESTRICTION.isAllowed(sound.getSoundLocation()) == false;
    }

    @Nullable
    public static FlatLayerInfo[] parseBlockString(String blockString)
    {
        return IMixinFlatGeneratorInfo.getLayersFromStringInvoker(3, blockString).toArray(new FlatLayerInfo[0]);
    }

    public static void updateItemGlintRestriction()
    {
        ITEM_GLINT_RESTRICTION.setListType((ListType) Configs.Lists.ITEM_GLINT_LIST_TYPE.getOptionListValue());
        ITEM_GLINT_RESTRICTION.setListContents(Configs.Lists.ITEM_GLINT_BLACKLIST.getStrings(),
                Configs.Lists.ITEM_GLINT_WHITELIST.getStrings());
    }

    public static void updatePotionRestrictionLists()
    {
        POTION_RESTRICTION.setListType((ListType) Configs.Lists.POTION_WARNING_LIST_TYPE.getOptionListValue());
        POTION_RESTRICTION.setListContents(Configs.Lists.POTION_WARNING_BLACKLIST.getStrings(),
                Configs.Lists.POTION_WARNING_WHITELIST.getStrings());
    }

    public static void updateSoundRestrictionLists()
    {
        SOUND_RESTRICTION.setListType((ListType) Configs.Lists.SOUND_DISABLE_LIST_TYPE.getOptionListValue());
        SOUND_RESTRICTION.setListContents(Configs.Lists.SOUND_DISABLE_BLACKLIST.getStrings(),
                Configs.Lists.SOUND_DISABLE_WHITELIST.getStrings());
    }
}
