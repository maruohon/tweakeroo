package fi.dy.masa.tweakeroo.config;

import java.text.DecimalFormat;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import fi.dy.masa.malilib.action.Action;
import fi.dy.masa.malilib.config.ValueChangeCallback;
import fi.dy.masa.malilib.config.option.BooleanContainingConfig;
import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.callback.AdjustableValueHotkeyCallback;
import fi.dy.masa.malilib.overlay.message.MessageHelpers;
import fi.dy.masa.malilib.overlay.message.MessageHelpers.SimpleBooleanConfigMessageFactory;
import fi.dy.masa.malilib.overlay.message.MessageUtils;
import fi.dy.masa.malilib.util.GameUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.tweakeroo.feature.Actions;
import fi.dy.masa.tweakeroo.tweaks.MiscTweaks;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import fi.dy.masa.tweakeroo.util.SnapAimMode;

public class Callbacks
{
    private static final DecimalFormat TWO_DIGITS = new DecimalFormat("#.##");

    public static void init()
    {
        FeatureToggle.TWEAK_GAMMA_OVERRIDE.getBooleanConfig().setValueLoadCallback((newValue) -> {
            // If the feature is enabled on game launch, apply it here. Note: This does require the Generic configs to be read first.
            if (newValue) { GameUtils.getClient().gameSettings.gammaSetting = Configs.Generic.GAMMA_OVERRIDE_VALUE.getFloatValue(); }
        });

        FeatureToggle.TWEAK_GAMMA_OVERRIDE.getBooleanConfig().setValueChangeCallback((newValue, oldValue) -> {
            Minecraft mc = GameUtils.getClient();
            if (newValue) {
                Configs.Internal.GAMMA_VALUE_ORIGINAL.setValue((double) mc.gameSettings.gammaSetting);
                mc.gameSettings.gammaSetting = Configs.Generic.GAMMA_OVERRIDE_VALUE.getFloatValue();
            }
            else { mc.gameSettings.gammaSetting = Configs.Internal.GAMMA_VALUE_ORIGINAL.getFloatValue(); }
        });

        DisableToggle.DISABLE_SLIME_BLOCK_SLOWDOWN.getBooleanConfig().setValueLoadCallback((newValue) -> {
            if (newValue) { Blocks.SLIME_BLOCK.slipperiness = Blocks.STONE.slipperiness; }
        });
        DisableToggle.DISABLE_SLIME_BLOCK_SLOWDOWN.getBooleanConfig().setValueChangeCallback((newValue, oldValue) -> {
            if (newValue) { Blocks.SLIME_BLOCK.slipperiness = Blocks.STONE.slipperiness; }
            else { Blocks.SLIME_BLOCK.slipperiness = Configs.Internal.SLIME_BLOCK_SLIPPERINESS_ORIGINAL.getFloatValue(); }
        });

        Minecraft mc = GameUtils.getClient();
        Configs.Internal.GAMMA_VALUE_ORIGINAL.setValue((double) mc.gameSettings.gammaSetting);
        Configs.Internal.SLIME_BLOCK_SLIPPERINESS_ORIGINAL.setValue((double) Blocks.SLIME_BLOCK.slipperiness);

        Configs.Lists.REPAIR_MODE_SLOTS.setValueLoadCallback(InventoryUtils::setRepairModeSlots);
        Configs.Lists.REPAIR_MODE_SLOTS.setValueChangeCallback((newValue, oldValue) -> InventoryUtils.setRepairModeSlots(newValue));

        Configs.Lists.SWAP_BROKEN_TOOLS_SLOTS.setValueLoadCallback(InventoryUtils::setSwapBrokenToolsSlots);
        Configs.Lists.SWAP_BROKEN_TOOLS_SLOTS.setValueChangeCallback((newValue, oldValue) -> InventoryUtils.setSwapBrokenToolsSlots(newValue));

        Configs.Lists.UNSTACKING_ITEMS.setValueLoadCallback(InventoryUtils::setUnstackingItems);
        Configs.Lists.UNSTACKING_ITEMS.setValueChangeCallback((newValue, oldValue) -> InventoryUtils.setUnstackingItems(newValue));

        Configs.Lists.FAST_RIGHT_CLICK_BLOCK_LIST.setValueLoadCallback(PlacementTweaks::updateFastRightClickBlockRestriction);
        Configs.Lists.FAST_RIGHT_CLICK_BLOCK_LIST.setValueChangeCallback((newValue, oldValue) -> PlacementTweaks.updateFastRightClickBlockRestriction(newValue));

        Configs.Lists.FAST_RIGHT_CLICK_ITEM_LIST.setValueLoadCallback(PlacementTweaks::updateFastRightClickItemRestriction);
        Configs.Lists.FAST_RIGHT_CLICK_ITEM_LIST.setValueChangeCallback((newValue, oldValue) -> PlacementTweaks.updateFastRightClickItemRestriction(newValue));

        Configs.Lists.FAST_PLACEMENT_ITEM_LIST.setValueLoadCallback(PlacementTweaks::updateFastPlacementItemRestriction);
        Configs.Lists.FAST_PLACEMENT_ITEM_LIST.setValueChangeCallback((newValue, oldValue) -> PlacementTweaks.updateFastPlacementItemRestriction(newValue));

        Configs.Lists.ITEM_GLINT_ITEM_LIST.setValueLoadCallback(MiscTweaks::updateItemGlintRestriction);
        Configs.Lists.ITEM_GLINT_ITEM_LIST.setValueChangeCallback((newValue, oldValue) -> MiscTweaks.updateItemGlintRestriction(newValue));

        Configs.Lists.POTION_WARNING_LIST.setValueLoadCallback(MiscTweaks::updatePotionRestrictionLists);
        Configs.Lists.POTION_WARNING_LIST.setValueChangeCallback((newValue, oldValue) -> MiscTweaks.updatePotionRestrictionLists(newValue));

        Configs.Lists.SOUND_DISABLE_LIST.setValueLoadCallback(MiscTweaks::updateSoundRestrictionLists);
        Configs.Lists.SOUND_DISABLE_LIST.setValueChangeCallback((newValue, oldValue) -> MiscTweaks.updateSoundRestrictionLists(newValue));

        FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getBooleanConfig().setValueChangeCallback((newValue, oldValue) -> {
            if (Configs.Generic.PLACEMENT_RESTRICTION_TIED_TO_FAST.getBooleanValue())
            {
                FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.getBooleanConfig().setValue(newValue);
            }
        });
        FeatureToggle.TWEAK_FREE_CAMERA.getBooleanConfig().setValueChangeCallback((newValue, oldValue) -> CameraEntity.setCameraState(newValue));
        FeatureToggle.TWEAK_HOLD_ATTACK.getBooleanConfig().setValueChangeCallback(new FeatureCallbackHold(mc.gameSettings.keyBindAttack::getKeyCode));
        FeatureToggle.TWEAK_HOLD_USE.getBooleanConfig().setValueChangeCallback(new FeatureCallbackHold(mc.gameSettings.keyBindUseItem::getKeyCode));

        Hotkeys.BLINK_DRIVE.createCallbackForAction(Actions.BLINK_DRIVE_TELEPORT_GROUND);
        Hotkeys.BLINK_DRIVE_Y_LEVEL.createCallbackForAction(Actions.BLINK_DRIVE_TELEPORT_SAME_Y);
        Hotkeys.BREAKING_RESTRICTION_MODE_COLUMN.createCallbackForAction(Actions.SET_BREAKING_RESTRICTION_MODE_COLUMN);
        Hotkeys.BREAKING_RESTRICTION_MODE_DIAGONAL.createCallbackForAction(Actions.SET_BREAKING_RESTRICTION_MODE_DIAGONAL);
        Hotkeys.BREAKING_RESTRICTION_MODE_FACE.createCallbackForAction(Actions.SET_BREAKING_RESTRICTION_MODE_FACE);
        Hotkeys.BREAKING_RESTRICTION_MODE_LAYER.createCallbackForAction(Actions.SET_BREAKING_RESTRICTION_MODE_LAYER);
        Hotkeys.BREAKING_RESTRICTION_MODE_LINE.createCallbackForAction(Actions.SET_BREAKING_RESTRICTION_MODE_LINE);
        Hotkeys.BREAKING_RESTRICTION_MODE_PLANE.createCallbackForAction(Actions.SET_BREAKING_RESTRICTION_MODE_PLANE);
        Hotkeys.COPY_SIGN_TEXT.createCallbackForAction(Actions.COPY_SIGN_TEXT);
        Hotkeys.FLY_PRESET_1.setHotkeyCallback(createFlySpeedAdjustCallback(1, Configs.Generic.FLY_SPEED_PRESET_1, (ctx) -> Actions.setFlySpeedPreset(1)));
        Hotkeys.FLY_PRESET_2.setHotkeyCallback(createFlySpeedAdjustCallback(2, Configs.Generic.FLY_SPEED_PRESET_2, (ctx) -> Actions.setFlySpeedPreset(2)));
        Hotkeys.FLY_PRESET_3.setHotkeyCallback(createFlySpeedAdjustCallback(3, Configs.Generic.FLY_SPEED_PRESET_3, (ctx) -> Actions.setFlySpeedPreset(3)));
        Hotkeys.FLY_PRESET_4.setHotkeyCallback(createFlySpeedAdjustCallback(4, Configs.Generic.FLY_SPEED_PRESET_4, (ctx) -> Actions.setFlySpeedPreset(4)));
        Hotkeys.FLY_PRESET_5.setHotkeyCallback(createFlySpeedAdjustCallback(5, Configs.Generic.FLY_SPEED_PRESET_5, (ctx) -> Actions.setFlySpeedPreset(5)));
        Hotkeys.FLY_PRESET_6.setHotkeyCallback(createFlySpeedAdjustCallback(6, Configs.Generic.FLY_SPEED_PRESET_6, (ctx) -> Actions.setFlySpeedPreset(6)));
        Hotkeys.GHOST_BLOCK_REMOVER.createCallbackForAction(Actions.GHOST_BLOCK_REMOVER_MANUAL);
        Hotkeys.HOTBAR_SCROLL.setHotkeyCallback(AdjustableValueHotkeyCallback.createWrapping(null, Configs.Internal.HOTBAR_SCROLL_CURRENT_ROW, 0, 2)
                                                    .setAdjustmentEnabledCondition(FeatureToggle.TWEAK_HOTBAR_SCROLL::getBooleanValue)
                                                    .setKeyAction(Actions::hotbarScroll).setReverseDirection(true).setTriggerAlwaysOnRelease(true));
        Hotkeys.HOTBAR_SWAP_1.createCallbackForAction(Actions.HOTBAR_SWAP_ROW_1);
        Hotkeys.HOTBAR_SWAP_2.createCallbackForAction(Actions.HOTBAR_SWAP_ROW_2);
        Hotkeys.HOTBAR_SWAP_3.createCallbackForAction(Actions.HOTBAR_SWAP_ROW_3);
        Hotkeys.OPEN_CONFIG_GUI.createCallbackForAction(Actions.OPEN_CONFIG_SCREEN);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_COLUMN.createCallbackForAction(Actions.SET_PLACEMENT_RESTRICTION_MODE_COLUMN);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_DIAGONAL.createCallbackForAction(Actions.SET_PLACEMENT_RESTRICTION_MODE_DIAGONAL);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_FACE.createCallbackForAction(Actions.SET_PLACEMENT_RESTRICTION_MODE_FACE);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_LAYER.createCallbackForAction(Actions.SET_PLACEMENT_RESTRICTION_MODE_LAYER);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_LINE.createCallbackForAction(Actions.SET_PLACEMENT_RESTRICTION_MODE_LINE);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_PLANE.createCallbackForAction(Actions.SET_PLACEMENT_RESTRICTION_MODE_PLANE);
        Hotkeys.RELOAD_LANGUAGE_PACKS.createCallbackForAction(Actions.RELOAD_LANGUAGE_PACKS);
        Hotkeys.TOGGLE_GRAB_CURSOR.createCallbackForAction(Actions.TOGGLE_GRAB_CURSOR);
        Hotkeys.TOOL_PICK.createCallbackForAction(Actions.TOOL_PICK);
        Hotkeys.ZOOM_ACTIVATE.setHotkeyCallback(AdjustableValueHotkeyCallback.createClamped(null, Configs.Generic.ZOOM_FOV, () -> BaseScreen.isCtrlDown() ? 5.0 : 1.0)
                                                    .setToggleMessageFactory(Callbacks::getZoomToggleMessage)
                                                    .setHotkeyCallback((a, k) -> Actions.zoomActivate(a == KeyAction.PRESS))
                                                    .addAdjustListener(MiscUtils::onZoomActivated)
                                                    .addAdjustListener(() -> MessageUtils.printCustomActionbarMessage("tweakeroo.message.set_zoom_fov_to",
                                                                                                                      String.format("%.1f", Configs.Generic.ZOOM_FOV.getDoubleValue()))));
        Hotkeys.SKIP_ALL_RENDERING.createCallbackForAction(Actions.TOGGLE_SKIP_ALL_RENDERING);
        Hotkeys.SKIP_WORLD_RENDERING.createCallbackForAction(Actions.TOGGLE_SKIP_WORLD_RENDERING);

        addAdjustableCallback(FeatureToggle.TWEAK_AFTER_CLICKER,            Configs.Generic.AFTER_CLICKER_CLICK_COUNT,  "tweakeroo.message.toggled_after_clicker_on",   "tweakeroo.message.set_after_clicker_count_to");
        addAdjustableCallback(FeatureToggle.TWEAK_BREAKING_GRID,            Configs.Generic.BREAKING_GRID_SIZE,         "tweakeroo.message.toggled_breaking_grid_on",   "tweakeroo.message.set_breaking_grid_size_to");
        addAdjustableCallback(FeatureToggle.TWEAK_FAST_LEFT_CLICK,          Configs.Generic.FAST_LEFT_CLICK_COUNT,      "tweakeroo.message.toggled_fast_left_click_on", "tweakeroo.message.set_fast_left_click_count_to");
        addAdjustableCallback(FeatureToggle.TWEAK_FAST_RIGHT_CLICK,         Configs.Generic.FAST_RIGHT_CLICK_COUNT,     "tweakeroo.message.toggled_fast_right_click_on","tweakeroo.message.set_fast_right_click_count_to");
        addAdjustableCallback(FeatureToggle.TWEAK_GAMMA_OVERRIDE,           Configs.Generic.GAMMA_OVERRIDE_VALUE,       "tweakeroo.message.toggled_gamma_override_on",  "tweakeroo.message.set_gamma_override_value_to", () -> 0.1);
        addAdjustableCallback(FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE,        Configs.Generic.HOTBAR_SLOT_CYCLE_MAX,      "tweakeroo.message.toggled_slot_cycle_on",      "tweakeroo.message.set_hotbar_slot_cycle_max_to");
        addAdjustableCallback(FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER,   Configs.Generic.HOTBAR_SLOT_RANDOMIZER_MAX, "tweakeroo.message.toggled_slot_randomizer_on", "tweakeroo.message.set_hotbar_slot_randomizer_max_to");
        addAdjustableCallback(FeatureToggle.TWEAK_PERIODIC_ATTACK,          Configs.Generic.PERIODIC_ATTACK_INTERVAL,   "tweakeroo.message.toggled_periodic_attack_on", "tweakeroo.message.set_periodic_attack_interval_to");
        addAdjustableCallback(FeatureToggle.TWEAK_PERIODIC_USE,             Configs.Generic.PERIODIC_USE_INTERVAL,      "tweakeroo.message.toggled_periodic_use_on",    "tweakeroo.message.set_periodic_use_interval_to");
        addAdjustableCallback(FeatureToggle.TWEAK_PLACEMENT_GRID,           Configs.Generic.PLACEMENT_GRID_SIZE,        "tweakeroo.message.toggled_placement_grid_on",  "tweakeroo.message.set_placement_grid_size_to");
        addAdjustableCallback(FeatureToggle.TWEAK_PLACEMENT_LIMIT,          Configs.Generic.PLACEMENT_LIMIT,            "tweakeroo.message.toggled_placement_limit_on", "tweakeroo.message.set_placement_limit_to");
        addAdjustableCallback(FeatureToggle.TWEAK_STATIC_FOV,               Configs.Generic.STATIC_FOV,                 "tweakeroo.message.toggled_static_fov_on",      "tweakeroo.message.set_static_fov_value_to", () -> 1);

        addAdjustableCallback(FeatureToggle.TWEAK_BREAKING_RESTRICTION,     Configs.Generic.BREAKING_RESTRICTION_MODE,  "tweakeroo.message.toggled_breaking_restriction_on", "tweakeroo.message.set_breaking_restriction_mode_to");
        addAdjustableCallback(FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT,     Configs.Generic.PLACEMENT_RESTRICTION_MODE, "tweakeroo.message.toggled_fast_block_placement_on", "tweakeroo.message.set_placement_restriction_mode_to");

        FeatureToggle.TWEAK_FLY_SPEED.setHotkeyCallback(AdjustableValueHotkeyCallback.createClampedDoubleDelegate(
                FeatureToggle.TWEAK_FLY_SPEED.getBooleanConfig(), () -> Configs.Internal.ACTIVE_FLY_SPEED_OVERRIDE_VALUE,
                0, 4.0, () -> BaseScreen.isCtrlDown() ? 0.02 : 0.005)
                    .setToggleMessageFactory(Callbacks::getFlySpeedToggleMessage)
                    .addAdjustListener(() -> MessageUtils.printCustomActionbarMessage("tweakeroo.message.set_fly_speed_to", String.format("%.4f", Configs.Internal.ACTIVE_FLY_SPEED_OVERRIDE_VALUE.getDoubleValue()))));

        FeatureToggle.TWEAK_SNAP_AIM.setHotkeyCallback(AdjustableValueHotkeyCallback.createClampedDoubleDelegate(
                FeatureToggle.TWEAK_SNAP_AIM.getBooleanConfig(),
                () -> Configs.Generic.SNAP_AIM_MODE.getValue() == SnapAimMode.PITCH ? Configs.Generic.SNAP_AIM_PITCH_STEP : Configs.Generic.SNAP_AIM_YAW_STEP, 0, 360.0,
                (wheel) -> wheel > 0 ? 2.0 : 0.5)
                .setToggleMessageFactory(Callbacks::getSnapAimToggleMessage)
                .addAdjustListener(() -> {
                    SnapAimMode mode = Configs.Generic.SNAP_AIM_MODE.getValue();
                    DoubleConfig config = mode == SnapAimMode.PITCH ? Configs.Generic.SNAP_AIM_PITCH_STEP : Configs.Generic.SNAP_AIM_YAW_STEP;
                    String key = mode == SnapAimMode.PITCH ? "tweakeroo.message.set_snap_aim_pitch_step_to" : "tweakeroo.message.set_snap_aim_yaw_step_to";
                    MessageUtils.printCustomActionbarMessage(key, config.getStringValue());
                }));

        FeatureToggle.TWEAK_ZOOM.setHotkeyCallback(AdjustableValueHotkeyCallback.createClamped(
                FeatureToggle.TWEAK_ZOOM.getBooleanConfig(), Configs.Generic.ZOOM_FOV, () -> BaseScreen.isCtrlDown() ? 5.0 : 1.0)
                    .setToggleMessageFactory(Callbacks::getZoomToggleMessage)
                    .addAdjustListener(MiscUtils::onZoomActivated)
                    .addAdjustListener(() -> MessageUtils.printCustomActionbarMessage("tweakeroo.message.set_zoom_fov_to", String.format("%.1f", Configs.Generic.ZOOM_FOV.getDoubleValue()))));
    }

    private static void addAdjustableCallback(FeatureToggle feature,
                                              IntegerConfig intConfig,
                                              String toggleMessageKey,
                                              String adjustMessageKey)
    {
        AdjustableValueHotkeyCallback callback = AdjustableValueHotkeyCallback.createClamped(feature.getBooleanConfig(), intConfig)
                .setToggleMessageFactory(new SimpleBooleanConfigMessageFactory(toggleMessageKey, intConfig::getStringValue))
                .addAdjustListener(() -> MessageUtils.printCustomActionbarMessage(adjustMessageKey, intConfig.getStringValue()));
        feature.setHotkeyCallback(callback);
    }

    private static void addAdjustableCallback(FeatureToggle feature,
                                              DoubleConfig doubleConfig,
                                              String toggleMessageKey,
                                              String adjustMessageKey,
                                              DoubleSupplier multiplier)
    {
        AdjustableValueHotkeyCallback callback = AdjustableValueHotkeyCallback.createClamped(feature.getBooleanConfig(), doubleConfig, multiplier)
                .setToggleMessageFactory(new SimpleBooleanConfigMessageFactory(toggleMessageKey, () -> TWO_DIGITS.format(doubleConfig.getDoubleValue())))
                .addAdjustListener(() -> MessageUtils.printCustomActionbarMessage(adjustMessageKey, TWO_DIGITS.format(doubleConfig.getDoubleValue())));
        feature.setHotkeyCallback(callback);
    }

    private static void addAdjustableCallback(FeatureToggle feature,
                                              OptionListConfig<?> config,
                                              String toggleMessageKey,
                                              String adjustMessageKey)
    {
        AdjustableValueHotkeyCallback callback = AdjustableValueHotkeyCallback.create(feature.getBooleanConfig(), config)
                .setToggleMessageFactory(new SimpleBooleanConfigMessageFactory(toggleMessageKey, () -> config.getValue().getDisplayName()))
                .addAdjustListener(() -> MessageUtils.printCustomActionbarMessage(adjustMessageKey, config.getValue().getDisplayName()));
        feature.setHotkeyCallback(callback);
    }

    private static AdjustableValueHotkeyCallback createFlySpeedAdjustCallback(int preset, DoubleConfig config, Action action)
    {
        return AdjustableValueHotkeyCallback.createClamped(null, config, 0, 4.0, () -> BaseScreen.isCtrlDown() ? 0.1 : 0.005)
                .setKeyAction(action)
                .addAdjustListener(() -> MessageUtils.printCustomActionbarMessage("tweakeroo.message.set_fly_speed_to", preset, String.format("%.4f", config.getDoubleValue())));
    }

    private static String getFlySpeedToggleMessage(BooleanContainingConfig<?> config)
    {
        if (config.getBooleanValue())
        {
            int preset = Configs.Internal.FLY_SPEED_PRESET.getIntegerValue();
            float presetSpeed = Configs.getFlySpeedConfig(preset).getFloatValue();
            float activeSpeed = Configs.Internal.ACTIVE_FLY_SPEED_OVERRIDE_VALUE.getFloatValue();
            String strSpeed = String.format("%.3f", activeSpeed);

            if (activeSpeed == presetSpeed)
            {
                return StringUtils.translate("tweakeroo.message.toggled_fly_speed_on.preset", preset + 1, strSpeed);
            }
            else
            {
                return StringUtils.translate("tweakeroo.message.toggled_fly_speed_on.custom", strSpeed);
            }
        }
        else
        {
            return MessageHelpers.getBasicBooleanConfigToggleMessage(config);
        }
    }

    private static String getSnapAimToggleMessage(BooleanContainingConfig<?> config)
    {
        if (config.getBooleanValue())
        {
            SnapAimMode mode = Configs.Generic.SNAP_AIM_MODE.getValue();

            if (mode == SnapAimMode.YAW)
            {
                String yaw = Configs.Generic.SNAP_AIM_YAW_STEP.getStringValue();
                return StringUtils.translate("tweakeroo.message.toggled_snap_aim_on_yaw", yaw);
            }
            else if (mode == SnapAimMode.PITCH)
            {
                String pitch = Configs.Generic.SNAP_AIM_PITCH_STEP.getStringValue();
                return StringUtils.translate("tweakeroo.message.toggled_snap_aim_on_pitch", pitch);
            }
            else
            {
                String yaw = Configs.Generic.SNAP_AIM_YAW_STEP.getStringValue();
                String pitch = Configs.Generic.SNAP_AIM_PITCH_STEP.getStringValue();
                return StringUtils.translate("tweakeroo.message.toggled_snap_aim_on_both", yaw, pitch);
            }
        }
        else
        {
            return MessageHelpers.getBasicBooleanConfigToggleMessage(config);
        }
    }

    private static String getZoomToggleMessage(BooleanContainingConfig<?> config)
    {
        if (config.getBooleanValue())
        {
            String strValue = String.format("%.1f", Configs.Generic.ZOOM_FOV.getDoubleValue());
            return StringUtils.translate("tweakeroo.message.toggled_zoom_on", strValue);
        }
        else
        {
            return MessageHelpers.getBasicBooleanConfigToggleMessage(config);
        }
    }

    public static class FeatureCallbackHold implements ValueChangeCallback<Boolean>
    {
        private final IntSupplier keyCode;

        public FeatureCallbackHold(IntSupplier keyCode)
        {
            this.keyCode = keyCode;
        }

        @Override
        public void onValueChanged(Boolean newValue, Boolean oldValue)
        {
            int keyCode = this.keyCode.getAsInt();

            if (newValue)
            {
                KeyBinding.setKeyBindState(keyCode, true);
                KeyBinding.onTick(keyCode);
            }
            else
            {
                KeyBinding.setKeyBindState(keyCode, false);
            }
        }
    }
}
