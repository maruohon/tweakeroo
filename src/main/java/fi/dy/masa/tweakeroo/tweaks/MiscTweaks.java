package fi.dy.masa.tweakeroo.tweaks;

import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IConfigInteger;
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

public class MiscTweaks
{
    public static final PotionRestriction POTION_RESTRICTION = new PotionRestriction();

    private static final KeybindState KEY_STATE_ATTACK = new KeybindState(MinecraftClient.getInstance().options.keyAttack, (mc) -> ((IMinecraftClientInvoker) mc).leftClickMouseAccessor());
    private static final KeybindState KEY_STATE_USE = new KeybindState(MinecraftClient.getInstance().options.keyUse, (mc) -> ((IMinecraftClientInvoker) mc).rightClickMouseAccessor());

    private static int potionWarningTimer;

    private static class KeybindState
    {
        private final KeyBinding keybind;
        private final Consumer<MinecraftClient> clickFunc;
        private boolean state;
        private int durationCounter;
        private int intervalCounter;

        public KeybindState(KeyBinding keybind, Consumer<MinecraftClient> clickFunc)
        {
            this.keybind = keybind;
            this.clickFunc = clickFunc;
        }

        public void reset()
        {
            this.state = false;
            this.intervalCounter = 0;
            this.durationCounter = 0;
        }

        public void handlePeriodicHold(int interval, int holdDuration, MinecraftClient mc)
        {
            if (this.state)
            {
                if (++this.durationCounter >= holdDuration)
                {
                    this.setKeyState(false, mc);
                    this.durationCounter = 0;
                }
            }
            else if (++this.intervalCounter >= interval)
            {
                this.setKeyState(true, mc);
                this.intervalCounter = 0;
                this.durationCounter = 0;
            }
        }

        public void handlePeriodicClick(int interval, MinecraftClient mc)
        {
            if (++this.intervalCounter >= interval)
            {
                this.clickFunc.accept(mc);
                this.intervalCounter = 0;
                this.durationCounter = 0;
            }
        }

        private void setKeyState(boolean state, MinecraftClient mc)
        {
            this.state = state;

            InputUtil.KeyCode key = InputUtil.fromName(this.keybind.getName());
            KeyBinding.setKeyPressed(key, state);

            if (state)
            {
                this.clickFunc.accept(mc);
                KeyBinding.onKeyPressed(key);
            }
        }
    }

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
            handlePeriodicClicks(
                    KEY_STATE_ATTACK,
                    FeatureToggle.TWEAK_PERIODIC_HOLD_ATTACK,
                    FeatureToggle.TWEAK_PERIODIC_ATTACK,
                    Configs.Generic.PERIODIC_HOLD_ATTACK_INTERVAL,
                    Configs.Generic.PERIODIC_HOLD_ATTACK_DURATION,
                    Configs.Generic.PERIODIC_ATTACK_INTERVAL, mc);

            handlePeriodicClicks(
                    KEY_STATE_USE,
                    FeatureToggle.TWEAK_PERIODIC_HOLD_USE,
                    FeatureToggle.TWEAK_PERIODIC_USE,
                    Configs.Generic.PERIODIC_HOLD_USE_INTERVAL,
                    Configs.Generic.PERIODIC_HOLD_USE_DURATION,
                    Configs.Generic.PERIODIC_USE_INTERVAL, mc);
        }
        else
        {
            KEY_STATE_ATTACK.reset();
            KEY_STATE_USE.reset();
        }
    }

    private static void handlePeriodicClicks(
            KeybindState keyState,
            IConfigBoolean cfgPeriodicHold,
            IConfigBoolean cfgPeriodicClick,
            IConfigInteger cfgHoldClickInterval,
            IConfigInteger cfgHoldDuration,
            IConfigInteger cfgClickInterval,
            MinecraftClient mc)
    {
        if (cfgPeriodicHold.getBooleanValue())
        {
            int interval = cfgHoldClickInterval.getIntegerValue();
            int holdDuration = cfgHoldDuration.getIntegerValue();
            keyState.handlePeriodicHold(interval, holdDuration, mc);
        }
        else if (cfgPeriodicClick.getBooleanValue())
        {
            int interval = cfgClickInterval.getIntegerValue();
            keyState.handlePeriodicClick(interval, mc);
        }
        else
        {
            keyState.reset();
        }
    }

    private static void doPotionWarnings(PlayerEntity player)
    {
        if (FeatureToggle.TWEAK_POTION_WARNING.getBooleanValue() &&
            ++potionWarningTimer >= 100)
        {
            potionWarningTimer = 0;

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
               (effect.getEffectType().isBeneficial() ||
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
