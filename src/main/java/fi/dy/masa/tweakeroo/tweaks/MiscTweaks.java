package fi.dy.masa.tweakeroo.tweaks;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.gen.FlatLayerInfo;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.render.message.MessageUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.restriction.UsageRestriction;
import fi.dy.masa.tweakeroo.LiteModTweakeroo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.DisableToggle;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.mixin.IMixinFlatGeneratorInfo;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.IMinecraftAccessor;
import fi.dy.masa.tweakeroo.util.InventoryUtils;

public class MiscTweaks
{
    private static final UsageRestriction<Item> ITEM_GLINT_RESTRICTION = new UsageRestriction<>();
    private static final UsageRestriction<Potion> POTION_RESTRICTION = new UsageRestriction<>();
    private static final UsageRestriction<ResourceLocation> SOUND_RESTRICTION = new UsageRestriction<>((rl) -> {
        SoundEvent soundEvent = SoundEvent.REGISTRY.getObject(rl);
         if (soundEvent != null && soundEvent.getSoundName() != null)
         {
             return true;
         }
         LiteModTweakeroo.logger.warn(StringUtils.translate("tweakeroo.error.invalid_sound_blacklist_entry", rl.toString()));
         return false;
    });

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
                    MessageUtils.printActionbarMessage("tweakeroo.message.potion_effects_running_out",
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
        return DisableToggle.DISABLE_ITEM_GLINT.getBooleanValue() && ITEM_GLINT_RESTRICTION.isAllowed(stack.getItem()) == false;
    }

    public static boolean shouldDisableSound(ISound sound)
    {
        return DisableToggle.DISABLE_SOUNDS_LIST.getBooleanValue() && SOUND_RESTRICTION.isAllowed(sound.getSoundLocation()) == false;
    }

    @Nullable
    public static FlatLayerInfo[] parseBlockString(String blockString)
    {
        return IMixinFlatGeneratorInfo.getLayersFromStringInvoker(3, blockString).toArray(new FlatLayerInfo[0]);
    }

    public static void updateItemGlintRestriction(BlackWhiteList<Item> list)
    {
        ITEM_GLINT_RESTRICTION.setListContents(list);
    }

    public static void updatePotionRestrictionLists(BlackWhiteList<Potion> list)
    {
        POTION_RESTRICTION.setListContents(list);
    }

    public static void updateSoundRestrictionLists(BlackWhiteList<ResourceLocation> list)
    {
        SOUND_RESTRICTION.setListContents(list);
    }
}
