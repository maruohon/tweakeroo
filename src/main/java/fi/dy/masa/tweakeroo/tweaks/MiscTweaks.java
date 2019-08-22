package fi.dy.masa.tweakeroo.tweaks;

import java.util.Collection;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.tweakeroo.Tweakeroo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.mixin.IMixinFlatChunkGeneratorConfig;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.IMinecraftClientInvoker;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.PotionRestriction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;

public class MiscTweaks
{
    public static final PotionRestriction POTION_RESTRICTION = new PotionRestriction();

    private static long lastPotionWarning;
    private static int periodicAttackCounter;
    private static int periodicUseCounter;

    public static void onTick(MinecraftClient mc)
    {
        ClientPlayerEntity player = mc.player;

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

        CameraEntity.movementTick(player.input.sneaking, player.input.jumping);
    }

    public static void onGameLoop()
    {
        PlacementTweaks.onTick();

        // Reset the counters after rendering each frame
        Tweakeroo.renderCountItems = 0;
        Tweakeroo.renderCountXPOrbs = 0;
    }

    private static void doPeriodicClicks(MinecraftClient mc)
    {
        if (GuiUtils.getCurrentScreen() == null)
        {
            if (FeatureToggle.TWEAK_PERIODIC_ATTACK.getBooleanValue() &&
                ++periodicAttackCounter >= Configs.Generic.PERIODIC_ATTACK_INTERVAL.getIntegerValue())
            {
                ((IMinecraftClientInvoker) mc).leftClickMouseAccessor();
                periodicAttackCounter = 0;
            }

            if (FeatureToggle.TWEAK_PERIODIC_USE.getBooleanValue() &&
                ++periodicUseCounter >= Configs.Generic.PERIODIC_USE_INTERVAL.getIntegerValue())
            {
                ((IMinecraftClientInvoker) mc).rightClickMouseAccessor();
                periodicUseCounter = 0;
            }
        }
    }

    private static void doPotionWarnings(PlayerEntity player)
    {
        if (FeatureToggle.TWEAK_POTION_WARNING.getBooleanValue() &&
            player.getEntityWorld().getTime() - lastPotionWarning >= 100)
        {
            lastPotionWarning = player.getEntityWorld().getTime();

            Collection<StatusEffectInstance> effects = player.getStatusEffects();

            if (effects.isEmpty() == false)
            {
                int minDuration = -1;
                int count = 0;

                for (StatusEffectInstance effectInstance : effects)
                {
                    if (potionWarningShouldInclude(effectInstance))
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
                    InfoUtils.printActionbarMessage("tweakeroo.message.potion_effects_running_out",
                            Integer.valueOf(count), Integer.valueOf(minDuration / 20));
                }
            }
        }
    }

    private static boolean potionWarningShouldInclude(StatusEffectInstance effect)
    {
        return effect.isAmbient() == false &&
               (effect.getEffectType().method_5573() || // isBeneficial()
               Configs.Generic.POTION_WARNING_BENEFICIAL_ONLY.getBooleanValue() == false) &&
               effect.getDuration() <= Configs.Generic.POTION_WARNING_THRESHOLD.getIntegerValue() &&
               POTION_RESTRICTION.isAllowed(effect.getEffectType());
    }

    @Nullable
    public static FlatChunkGeneratorLayer[] parseBlockString(String blockString)
    {
        return IMixinFlatChunkGeneratorConfig.getLayersFromStringInvoker(blockString).toArray(new FlatChunkGeneratorLayer[0]);
    }
}
