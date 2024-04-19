package fi.dy.masa.tweakeroo.tweaks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IConfigInteger;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.MessageOutputType;
import fi.dy.masa.tweakeroo.Tweakeroo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.EntityRestriction;
import fi.dy.masa.tweakeroo.util.IMinecraftClientInvoker;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.PotionRestriction;

public class MiscTweaks
{
    public static final EntityRestriction ENTITY_TYPE_ATTACK_RESTRICTION = new EntityRestriction();
    public static final PotionRestriction POTION_RESTRICTION = new PotionRestriction();

    private static final KeybindState KEY_STATE_ATTACK = new KeybindState(MinecraftClient.getInstance().options.attackKey, (mc) -> ((IMinecraftClientInvoker) mc).tweakeroo_invokeDoAttack());
    private static final KeybindState KEY_STATE_USE = new KeybindState(MinecraftClient.getInstance().options.useKey, (mc) -> ((IMinecraftClientInvoker) mc).tweakeroo_invokeDoItemUse());

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

            InputUtil.Key key = InputUtil.fromTranslationKey(this.keybind.getBoundKeyTranslationKey());
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

        CameraEntity.movementTick();
    }

    public static void onGameLoop(MinecraftClient mc)
    {
        PlacementTweaks.onTick(mc);

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

    public static boolean isEntityAllowedByAttackingRestriction(EntityType<?> type)
    {
        if (MiscTweaks.ENTITY_TYPE_ATTACK_RESTRICTION.isAllowed(type) == false)
        {
            MessageOutputType messageOutputType = (MessageOutputType) Configs.Generic.ENTITY_TYPE_ATTACK_RESTRICTION_WARN.getOptionListValue();

            if (messageOutputType == MessageOutputType.MESSAGE)
            {
                InfoUtils.showGuiOrInGameMessage(Message.MessageType.WARNING, "tweakeroo.message.warning.entity_type_attack_restriction");
            }
            else if (messageOutputType == MessageOutputType.ACTIONBAR)
            {
                InfoUtils.printActionbarMessage("tweakeroo.message.warning.entity_type_attack_restriction");
            }

            return false;
        }

        return true;
    }


    private static boolean potionWarningShouldInclude(StatusEffectInstance effect)
    {
        return effect.isAmbient() == false &&
               (effect.getEffectType().value().isBeneficial() ||
               Configs.Generic.POTION_WARNING_BENEFICIAL_ONLY.getBooleanValue() == false) &&
               effect.getDuration() <= Configs.Generic.POTION_WARNING_THRESHOLD.getIntegerValue() &&
               POTION_RESTRICTION.isAllowed(effect.getEffectType().value());
    }

    @Nullable
    public static FlatChunkGeneratorLayer[] parseBlockString(String blockString)
    {
        List<FlatChunkGeneratorLayer> list = new ArrayList<>();
        String[] strings = blockString.split(",");
        final int count = strings.length;
        int thicknessSum = 0;

        for (int i = 0; i < count; ++i)
        {
            String str = strings[i];
            FlatChunkGeneratorLayer layer = parseLayerString(str, thicknessSum);

            if (layer == null)
            {
                list = Collections.emptyList();
                break;
            }

            list.add(layer);
            thicknessSum += layer.getThickness();
        }

        return list.toArray(new FlatChunkGeneratorLayer[list.size()]);
    }

    @Nullable
    private static FlatChunkGeneratorLayer parseLayerString(String string, int startY)
    {
        String[] strings = string.split("\\*", 2);
        int thickness;

        if (strings.length == 2)
        {
            try
            {
                thickness = Math.max(Integer.parseInt(strings[0]), 0);
            }
            catch (NumberFormatException e)
            {
                Tweakeroo.logger.error("Error while parsing flat world string => {}", e.getMessage());
                return null;
            }
        }
        else
        {
            thickness = 1;
        }

        int endY = Math.min(startY + thickness, 256);
        int finalThickness = endY - startY;
        Block block;

        try
        {
            block = getBlockFromName(strings[strings.length - 1]);
        }
        catch (Exception e)
        {
            Tweakeroo.logger.error("Error while parsing flat world string => {}", e.getMessage());
            return null;
        }

        if (block == null)
        {
            Tweakeroo.logger.error("Error while parsing flat world string => Unknown block, {}", strings[strings.length - 1]);
            return null;
        }
        else
        {
            FlatChunkGeneratorLayer layer = new FlatChunkGeneratorLayer(finalThickness, block);
            // FIXME 1.17 is this just not needed anymore?
            //layer.setStartY(startY);
            return layer;
        }
    }

    @Nullable
    private static Block getBlockFromName(String name)
    {
        try
        {
            Identifier identifier = new Identifier(name);
            return Registries.BLOCK.getOrEmpty(identifier).orElse(null);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
