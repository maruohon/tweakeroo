package fi.dy.masa.tweakeroo.config;

import java.util.function.IntSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import fi.dy.masa.malilib.action.Action;
import fi.dy.masa.malilib.config.ValueChangeCallback;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.callback.AdjustableValueHotkeyCallback;
import fi.dy.masa.malilib.input.callback.HotkeyCallback;
import fi.dy.masa.malilib.overlay.message.MessageUtils;
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
    public static void init()
    {
        FeatureToggle.TWEAK_GAMMA_OVERRIDE.getBooleanConfig().setValueLoadCallback((newValue) -> {
            // If the feature is enabled on game launch, apply it here. Note: This does require the Generic configs to be read first.
            if (newValue) { Minecraft.getMinecraft().gameSettings.gammaSetting = Configs.Generic.GAMMA_OVERRIDE_VALUE.getFloatValue(); }
        });

        FeatureToggle.TWEAK_GAMMA_OVERRIDE.getBooleanConfig().setValueChangeCallback((newValue, oldValue) -> {
            Minecraft mc = Minecraft.getMinecraft();
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

        Minecraft mc = Minecraft.getMinecraft();
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

        Hotkeys.BLINK_DRIVE.setHotkeyCallback(HotkeyCallback.of(Actions.BLINK_DRIVE_TELEPORT_GROUND));
        Hotkeys.BLINK_DRIVE_Y_LEVEL.setHotkeyCallback(HotkeyCallback.of(Actions.BLINK_DRIVE_TELEPORT_SAME_Y));
        Hotkeys.BREAKING_RESTRICTION_MODE_COLUMN.setHotkeyCallback(HotkeyCallback.of(Actions.SET_BREAKING_RESTRICTION_MODE_COLUMN));
        Hotkeys.BREAKING_RESTRICTION_MODE_DIAGONAL.setHotkeyCallback(HotkeyCallback.of(Actions.SET_BREAKING_RESTRICTION_MODE_DIAGONAL));
        Hotkeys.BREAKING_RESTRICTION_MODE_FACE.setHotkeyCallback(HotkeyCallback.of(Actions.SET_BREAKING_RESTRICTION_MODE_FACE));
        Hotkeys.BREAKING_RESTRICTION_MODE_LAYER.setHotkeyCallback(HotkeyCallback.of(Actions.SET_BREAKING_RESTRICTION_MODE_LAYER));
        Hotkeys.BREAKING_RESTRICTION_MODE_LINE.setHotkeyCallback(HotkeyCallback.of(Actions.SET_BREAKING_RESTRICTION_MODE_LINE));
        Hotkeys.BREAKING_RESTRICTION_MODE_PLANE.setHotkeyCallback(HotkeyCallback.of(Actions.SET_BREAKING_RESTRICTION_MODE_PLANE));
        Hotkeys.COPY_SIGN_TEXT.setHotkeyCallback(HotkeyCallback.of(Actions.COPY_SIGN_TEXT));
        Hotkeys.FLY_PRESET_1.setHotkeyCallback(createFlySpeedAdjustCallback(1, Configs.Generic.FLY_SPEED_PRESET_1, Actions.SET_FLY_SPEED_PRESET_1.getAction()));
        Hotkeys.FLY_PRESET_2.setHotkeyCallback(createFlySpeedAdjustCallback(2, Configs.Generic.FLY_SPEED_PRESET_2, Actions.SET_FLY_SPEED_PRESET_2.getAction()));
        Hotkeys.FLY_PRESET_3.setHotkeyCallback(createFlySpeedAdjustCallback(3, Configs.Generic.FLY_SPEED_PRESET_3, Actions.SET_FLY_SPEED_PRESET_3.getAction()));
        Hotkeys.FLY_PRESET_4.setHotkeyCallback(createFlySpeedAdjustCallback(4, Configs.Generic.FLY_SPEED_PRESET_4, Actions.SET_FLY_SPEED_PRESET_4.getAction()));
        Hotkeys.GHOST_BLOCK_REMOVER.setHotkeyCallback(HotkeyCallback.of(Actions.GHOST_BLOCK_REMOVER_MANUAL));
        Hotkeys.HOTBAR_SCROLL.setHotkeyCallback(AdjustableValueHotkeyCallback.createWrapping(null, Configs.Internal.HOTBAR_SCROLL_CURRENT_ROW, 0, 2)
                                                    .setAdjustmentEnabledCondition(FeatureToggle.TWEAK_HOTBAR_SCROLL::getBooleanValue)
                                                    .setToggleAction(Actions.HOTBAR_SCROLL.getAction()).setReverseDirection(true).setTriggerAlwaysOnRelease(true));
        Hotkeys.HOTBAR_SWAP_1.setHotkeyCallback(HotkeyCallback.of(Actions.HOTBAR_SWAP_ROW_1));
        Hotkeys.HOTBAR_SWAP_2.setHotkeyCallback(HotkeyCallback.of(Actions.HOTBAR_SWAP_ROW_2));
        Hotkeys.HOTBAR_SWAP_3.setHotkeyCallback(HotkeyCallback.of(Actions.HOTBAR_SWAP_ROW_3));
        Hotkeys.OPEN_CONFIG_GUI.setHotkeyCallback(HotkeyCallback.of(Actions.OPEN_CONFIG_SCREEN));
        Hotkeys.PLACEMENT_RESTRICTION_MODE_COLUMN.setHotkeyCallback(HotkeyCallback.of(Actions.SET_PLACEMENT_RESTRICTION_MODE_COLUMN));
        Hotkeys.PLACEMENT_RESTRICTION_MODE_DIAGONAL.setHotkeyCallback(HotkeyCallback.of(Actions.SET_PLACEMENT_RESTRICTION_MODE_DIAGONAL));
        Hotkeys.PLACEMENT_RESTRICTION_MODE_FACE.setHotkeyCallback(HotkeyCallback.of(Actions.SET_PLACEMENT_RESTRICTION_MODE_FACE));
        Hotkeys.PLACEMENT_RESTRICTION_MODE_LAYER.setHotkeyCallback(HotkeyCallback.of(Actions.SET_PLACEMENT_RESTRICTION_MODE_LAYER));
        Hotkeys.PLACEMENT_RESTRICTION_MODE_LINE.setHotkeyCallback(HotkeyCallback.of(Actions.SET_PLACEMENT_RESTRICTION_MODE_LINE));
        Hotkeys.PLACEMENT_RESTRICTION_MODE_PLANE.setHotkeyCallback(HotkeyCallback.of(Actions.SET_PLACEMENT_RESTRICTION_MODE_PLANE));
        Hotkeys.RELOAD_LANGUAGE_PACKS.setHotkeyCallback(HotkeyCallback.of(Actions.RELOAD_LANGUAGE_PACKS));
        Hotkeys.TOGGLE_GRAB_CURSOR.setHotkeyCallback(HotkeyCallback.of(Actions.TOGGLE_GRAB_CURSOR));
        Hotkeys.TOOL_PICK.setHotkeyCallback(HotkeyCallback.of(Actions.TOOL_PICK));
        Hotkeys.ZOOM_ACTIVATE.setHotkeyCallback(AdjustableValueHotkeyCallback.createClamped(null, Configs.Generic.ZOOM_FOV, () -> BaseScreen.isCtrlDown() ? 5.0 : 1.0)
                                                    .setToggleMessageFactory(Callbacks::getZoomToggleMessage)
                                                    .setHotkeyCallback((a, k) -> Actions.zoomActivate(a == KeyAction.PRESS))
                                                    .addAdjustListener(MiscUtils::onZoomActivated)
                                                    .addAdjustListener(() -> MessageUtils.printCustomActionbarMessage("tweakeroo.message.set_zoom_fov_to",
                                                                                                                      String.format("%.1f", Configs.Generic.ZOOM_FOV.getDoubleValue()))));
        Hotkeys.SKIP_ALL_RENDERING.setHotkeyCallback(HotkeyCallback.of(Actions.TOGGLE_SKIP_ALL_RENDERING));
        Hotkeys.SKIP_WORLD_RENDERING.setHotkeyCallback(HotkeyCallback.of(Actions.TOGGLE_SKIP_WORLD_RENDERING));

        addAdjustableCallback(FeatureToggle.TWEAK_AFTER_CLICKER,            Configs.Generic.AFTER_CLICKER_CLICK_COUNT,  "tweakeroo.message.toggled_after_clicker_on",   "tweakeroo.message.set_after_clicker_count_to");
        addAdjustableCallback(FeatureToggle.TWEAK_BREAKING_GRID,            Configs.Generic.BREAKING_GRID_SIZE,         "tweakeroo.message.toggled_breaking_grid_on",   "tweakeroo.message.set_breaking_grid_size_to");
        addAdjustableCallback(FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE,        Configs.Generic.HOTBAR_SLOT_CYCLE_MAX,      "tweakeroo.message.toggled_slot_cycle_on",      "tweakeroo.message.set_hotbar_slot_cycle_max_to");
        addAdjustableCallback(FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER,   Configs.Generic.HOTBAR_SLOT_RANDOMIZER_MAX, "tweakeroo.message.toggled_slot_randomizer_on", "tweakeroo.message.set_hotbar_slot_randomizer_max_to");
        addAdjustableCallback(FeatureToggle.TWEAK_PLACEMENT_GRID,           Configs.Generic.PLACEMENT_GRID_SIZE,        "tweakeroo.message.toggled_placement_grid_on",  "tweakeroo.message.set_placement_grid_size_to");
        addAdjustableCallback(FeatureToggle.TWEAK_PLACEMENT_LIMIT,          Configs.Generic.PLACEMENT_LIMIT,            "tweakeroo.message.toggled_placement_limit_on", "tweakeroo.message.set_placement_limit_to");

        addAdjustableCallback(FeatureToggle.TWEAK_BREAKING_RESTRICTION,     Configs.Generic.BREAKING_RESTRICTION_MODE,  "tweakeroo.message.toggled_breaking_restriction_on", "tweakeroo.message.set_breaking_restriction_mode_to");
        addAdjustableCallback(FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT,     Configs.Generic.PLACEMENT_RESTRICTION_MODE, "tweakeroo.message.toggled_fast_block_placement_on", "tweakeroo.message.set_placement_restriction_mode_to");

        FeatureToggle.TWEAK_FLY_SPEED.setHotkeyCallback(AdjustableValueHotkeyCallback.createClampedDoubleDelegate(
                FeatureToggle.TWEAK_FLY_SPEED.getBooleanConfig(), Configs::getActiveFlySpeedConfig, 0, 4.0, () -> BaseScreen.isCtrlDown() ? 0.02 : 0.005)
                    .setToggleMessageFactory(Callbacks::getFlySpeedToggleMessage)
                    .addAdjustListener(() -> MessageUtils.printCustomActionbarMessage("tweakeroo.message.set_fly_speed_to", String.format("%.4f", Configs.getActiveFlySpeedConfig().getDoubleValue()))));

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

    private static AdjustableValueHotkeyCallback addAdjustableCallback(FeatureToggle feature,
                                                                       IntegerConfig intConfig,
                                                                       String toggleMessageKey,
                                                                       String adjustMessageKey)
    {
        AdjustableValueHotkeyCallback callback = AdjustableValueHotkeyCallback.createClamped(feature.getBooleanConfig(), intConfig)
                 .setToggleMessageFactory((cfg) -> {
                     if (cfg.getBooleanValue()) { return StringUtils.translate(toggleMessageKey, intConfig.getStringValue()); }
                     else { return MessageUtils.getBasicBooleanConfigToggleMessage(cfg); }
                 })
                 .addAdjustListener(() -> MessageUtils.printCustomActionbarMessage(adjustMessageKey, intConfig.getStringValue()));
        feature.setHotkeyCallback(callback);
        return callback;
    }

    private static AdjustableValueHotkeyCallback addAdjustableCallback(FeatureToggle feature,
                                                                       OptionListConfig<?> config,
                                                                       String toggleMessageKey,
                                                                       String adjustMessageKey)
    {
        AdjustableValueHotkeyCallback callback = AdjustableValueHotkeyCallback.create(feature.getBooleanConfig(), config)
                 .setToggleMessageFactory((cfg) -> {
                     if (cfg.getBooleanValue()) { return StringUtils.translate(toggleMessageKey, config.getValue().getDisplayName()); }
                     else { return MessageUtils.getBasicBooleanConfigToggleMessage(cfg); }
                 })
                 .addAdjustListener(() -> MessageUtils.printCustomActionbarMessage(adjustMessageKey, config.getValue().getDisplayName()));
        feature.setHotkeyCallback(callback);
        return callback;
    }

    private static AdjustableValueHotkeyCallback createFlySpeedAdjustCallback(int preset, DoubleConfig config, Action action)
    {
        return AdjustableValueHotkeyCallback.createClamped(null, config, 0, 4.0, () -> BaseScreen.isCtrlDown() ? 0.1 : 0.005)
                .setToggleAction(action)
                .addAdjustListener(() -> MessageUtils.printCustomActionbarMessage("tweakeroo.message.set_fly_speed_to", preset, String.format("%.4f", config.getDoubleValue())));
    }

    private static String getFlySpeedToggleMessage(BooleanConfig config)
    {
        if (config.getBooleanValue())
        {
            int preset = Configs.Internal.FLY_SPEED_PRESET.getIntegerValue() + 1;
            String strSpeed = String.format("%.3f", Configs.getActiveFlySpeedConfig().getDoubleValue());
            return StringUtils.translate("tweakeroo.message.toggled_fly_speed_on", preset, strSpeed);
        }
        else
        {
            return MessageUtils.getBasicBooleanConfigToggleMessage(config);
        }
    }

    private static String getSnapAimToggleMessage(BooleanConfig config)
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
            return MessageUtils.getBasicBooleanConfigToggleMessage(config);
        }
    }

    private static String getZoomToggleMessage(BooleanConfig config)
    {
        if (config.getBooleanValue())
        {
            String strValue = String.format("%.1f", Configs.Generic.ZOOM_FOV.getDoubleValue());
            return StringUtils.translate("tweakeroo.message.toggled_zoom_on", strValue);
        }
        else
        {
            return MessageUtils.getBasicBooleanConfigToggleMessage(config);
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
