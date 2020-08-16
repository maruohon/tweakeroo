package fi.dy.masa.tweakeroo.config;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.BaseConfigOptionCategory;
import fi.dy.masa.malilib.config.ConfigOptionCategory;
import fi.dy.masa.malilib.config.ModConfig;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ColorConfig;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.config.option.StringListConfig;
import fi.dy.masa.malilib.config.value.ActiveMode;
import fi.dy.masa.malilib.config.value.HudAlignment;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.util.restriction.UsageRestriction.ListType;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.tweaks.MiscTweaks;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.PlacementRestrictionMode;
import fi.dy.masa.tweakeroo.util.SnapAimMode;

public class Configs implements ModConfig
{
    public static class Generic
    {
        public static final IntegerConfig AFTER_CLICKER_CLICK_COUNT                     = new IntegerConfig("afterClickerClickCount", 1, 1, 32);
        public static final DoubleConfig BLOCK_REACH_DISTANCE                           = new DoubleConfig("blockReachDistance", 4.5, 0, 8);
        public static final IntegerConfig BLOCK_BREAKING_PARTICLE_LIMIT                 = new IntegerConfig("blockBreakingParticleLimit", 8, 1, 1024);
        public static final DoubleConfig BLOCK_BREAKING_PARTICLE_SCALE                  = new DoubleConfig("blockBreakingParticleScale", 1.0, 0, 10D);
        public static final DoubleConfig BLOCK_BREAKING_PARTICLE_SPEED                  = new DoubleConfig("blockBreakingParticleSpeedMultiplier", 1.0, 0, 20D);
        public static final IntegerConfig BREAKING_GRID_SIZE                            = new IntegerConfig("breakingGridSize", 3, 1, 1000);
        public static final HotkeyedBooleanConfig CARPET_ACCURATE_PLACEMENT_PROTOCOL    = new HotkeyedBooleanConfig("carpetAccuratePlacementProtocol", true, "");
        public static final ColorConfig CHAT_BACKGROUND_COLOR                           = new ColorConfig("chatBackgroundColor", "#80000000");
        public static final StringConfig CHAT_TIME_FORMAT                               = new StringConfig("chatTimeFormat", "[HH:mm:ss]");
        public static final IntegerConfig CHUNK_RENDER_TIMEOUT                          = new IntegerConfig("chunkRenderTimeout", 50000000, 1, Integer.MAX_VALUE);
        public static final BooleanConfig CLIENT_PLACEMENT_ROTATION                     = new BooleanConfig("clientPlacementRotation", true);
        public static final DoubleConfig CLOUD_HEIGHT_OVERRIDE                          = new DoubleConfig("cloudHeightOverride", 128, -1024, 1024);
        public static final DoubleConfig DEBUG_PIE_CHART_SCALE                          = new DoubleConfig("debugPieChartScale", 1, 0, 10);
        public static final IntegerConfig FAST_BLOCK_PLACEMENT_COUNT                    = new IntegerConfig("fastBlockPlacementCount", 2, 1, 16);
        public static final IntegerConfig FAST_LEFT_CLICK_COUNT                         = new IntegerConfig("fastLeftClickCount", 10, 1, 64);
        public static final BooleanConfig FAST_PLACEMENT_REMEMBER                       = new BooleanConfig("fastPlacementRememberOrientation", true);
        public static final IntegerConfig FAST_RIGHT_CLICK_COUNT                        = new IntegerConfig("fastRightClickCount", 10, 1, 64);
        public static final IntegerConfig FILL_CLONE_LIMIT                              = new IntegerConfig("fillCloneLimit", 10000000, 1, 1000000000);
        public static final ColorConfig FLEXIBLE_PLACEMENT_OVERLAY_COLOR                = new ColorConfig("flexibleBlockPlacementOverlayColor", "#C03030F0");
        public static final DoubleConfig FLY_SPEED_PRESET_1                             = new DoubleConfig("flySpeedPreset1", 0.01, 0, 4);
        public static final DoubleConfig FLY_SPEED_PRESET_2                             = new DoubleConfig("flySpeedPreset2", 0.064, 0, 4);
        public static final DoubleConfig FLY_SPEED_PRESET_3                             = new DoubleConfig("flySpeedPreset3", 0.128, 0, 4);
        public static final DoubleConfig FLY_SPEED_PRESET_4                             = new DoubleConfig("flySpeedPreset4", 0.32, 0, 4);
        public static final HotkeyedBooleanConfig FREE_CAMERA_PLAYER_INPUTS             = new HotkeyedBooleanConfig("freeCameraPlayerInputs", false, "");
        public static final HotkeyedBooleanConfig FREE_CAMERA_PLAYER_MOVEMENT           = new HotkeyedBooleanConfig("freeCameraPlayerMovement", false, "");
        public static final IntegerConfig GAMMA_OVERRIDE_VALUE                          = new IntegerConfig("gammaOverrideValue", 16, 0, 1000);
        public static final BooleanConfig HAND_RESTOCK_CONTINUOUS                       = new BooleanConfig("handRestockContinuous", false);
        public static final BooleanConfig HAND_RESTOCK_PRE                              = new BooleanConfig("handRestockPre", true);
        public static final IntegerConfig HAND_RESTOCK_PRE_THRESHOLD                    = new IntegerConfig("handRestockPreThreshold", 6, 1, 64);
        public static final BooleanConfig HANGABLE_ENTITY_BYPASS_INVERSE                = new BooleanConfig("hangableEntityBypassInverse", false);
        public static final IntegerConfig HOTBAR_SLOT_CYCLE_MAX                         = new IntegerConfig("hotbarSlotCycleMax", 2, 1, 9);
        public static final IntegerConfig HOTBAR_SLOT_RANDOMIZER_MAX                    = new IntegerConfig("hotbarSlotRandomizerMax", 5, 1, 9);
        public static final IntegerConfig HOTBAR_SWAP_OVERLAY_OFFSET_X                  = new IntegerConfig("hotbarSwapOverlayOffsetX", 4);
        public static final IntegerConfig HOTBAR_SWAP_OVERLAY_OFFSET_Y                  = new IntegerConfig("hotbarSwapOverlayOffsetY", 4);
        public static final IntegerConfig ITEM_SWAP_DURABILITY_THRESHOLD                = new IntegerConfig("itemSwapDurabilityThreshold", 20, 0, 10000);
        public static final BooleanConfig LAVA_VISIBILITY_OPTIFINE                      = new BooleanConfig("lavaVisibilityOptifineCompat", true);
        public static final IntegerConfig MAP_PREVIEW_SIZE                              = new IntegerConfig("mapPreviewSize", 160, 16, 512);
        public static final IntegerConfig PERIODIC_ATTACK_INTERVAL                      = new IntegerConfig("periodicAttackInterval", 20, 0, Integer.MAX_VALUE);
        public static final IntegerConfig PERIODIC_USE_INTERVAL                         = new IntegerConfig("periodicUseInterval", 20, 0, Integer.MAX_VALUE);
        public static final BooleanConfig PERMANENT_SNEAK_ALLOW_IN_GUIS                 = new BooleanConfig("permanentSneakAllowInGUIs", false);
        public static final IntegerConfig PLACEMENT_GRID_SIZE                           = new IntegerConfig("placementGridSize", 3, 1, 1000);
        public static final IntegerConfig PLACEMENT_LIMIT                               = new IntegerConfig("placementLimit", 3, 1, 10000);
        public static final BooleanConfig PLACEMENT_RESTRICTION_TIED_TO_FAST            = new BooleanConfig("placementRestrictionTiedToFast", true);
        public static final DoubleConfig PLAYER_ON_FIRE_SCALE                           = new DoubleConfig("playerOnFireScale", 1, 0, 10);
        public static final BooleanConfig POTION_WARNING_BENEFICIAL_ONLY                = new BooleanConfig("potionWarningBeneficialOnly", true);
        public static final IntegerConfig POTION_WARNING_THRESHOLD                      = new IntegerConfig("potionWarningThreshold", 600, 1, 1000000);
        public static final BooleanConfig REMEMBER_FLEXIBLE                             = new BooleanConfig("rememberFlexibleFromClick", true);
        public static final IntegerConfig RENDER_LIMIT_ITEM                             = new IntegerConfig("renderLimitItem", -1, -1, 10000);
        public static final IntegerConfig RENDER_LIMIT_XP_ORB                           = new IntegerConfig("renderLimitXPOrb", -1, -1, 10000);
        public static final BooleanConfig SHULKER_DISPLAY_BACKGROUND_COLOR              = new BooleanConfig("shulkerDisplayBgColor", true);
        public static final BooleanConfig SHULKER_DISPLAY_REQUIRE_SHIFT                 = new BooleanConfig("shulkerDisplayRequireShift", true);
        public static final BooleanConfig SLOT_SYNC_WORKAROUND                          = new BooleanConfig("slotSyncWorkaround", true);
        public static final BooleanConfig SLOT_SYNC_WORKAROUND_ALWAYS                   = new BooleanConfig("slotSyncWorkaroundAlways", false);
        public static final BooleanConfig SNAP_AIM_INDICATOR                            = new BooleanConfig("snapAimIndicator", true);
        public static final ColorConfig SNAP_AIM_INDICATOR_COLOR                        = new ColorConfig("snapAimIndicatorColor", "#603030FF");
        public static final BooleanConfig SNAP_AIM_ONLY_CLOSE_TO_ANGLE                  = new BooleanConfig("snapAimOnlyCloseToAngle", true);
        public static final BooleanConfig SNAP_AIM_PITCH_OVERSHOOT                      = new BooleanConfig("snapAimPitchOvershoot", false);
        public static final DoubleConfig SNAP_AIM_PITCH_STEP                            = new DoubleConfig("snapAimPitchStep", 12.5, 0, 90);
        public static final DoubleConfig SNAP_AIM_THRESHOLD_PITCH                       = new DoubleConfig("snapAimThresholdPitch", 1.5);
        public static final DoubleConfig SNAP_AIM_THRESHOLD_YAW                         = new DoubleConfig("snapAimThresholdYaw", 5.0);
        public static final DoubleConfig SNAP_AIM_YAW_STEP                              = new DoubleConfig("snapAimYawStep", 45, 0, 360);
        public static final BooleanConfig SOUND_NAME_OUTPUT                             = new BooleanConfig("soundNameOutput", false);
        public static final IntegerConfig STRUCTURE_BLOCK_MAX_SIZE                      = new IntegerConfig("structureBlockMaxSize", 128, 1, 256);
        public static final BooleanConfig ZOOM_ADJUST_MOUSE_SENSITIVITY                 = new BooleanConfig("zoomAdjustMouseSensitivity", true);
        public static final DoubleConfig ZOOM_FOV                                       = new DoubleConfig("zoomFov", 30, 0.01, 359.99);

        public static final OptionListConfig<PlacementRestrictionMode> BREAKING_RESTRICTION_MODE    = new OptionListConfig<>("breakingRestrictionMode", PlacementRestrictionMode.LINE);
        public static final OptionListConfig<ActiveMode> ELYTRA_CAMERA_INDICATOR                    = new OptionListConfig<>("elytraCameraIndicator", ActiveMode.WITH_KEY);
        public static final OptionListConfig<HudAlignment> HOTBAR_SWAP_OVERLAY_ALIGNMENT            = new OptionListConfig<>("hotbarSwapOverlayAlignment", HudAlignment.BOTTOM_RIGHT);
        public static final OptionListConfig<PlacementRestrictionMode> PLACEMENT_RESTRICTION_MODE   = new OptionListConfig<>("placementRestrictionMode", PlacementRestrictionMode.FACE);
        public static final OptionListConfig<SnapAimMode> SNAP_AIM_MODE                             = new OptionListConfig<>("snapAimMode", SnapAimMode.YAW);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                AFTER_CLICKER_CLICK_COUNT,
                BLOCK_BREAKING_PARTICLE_LIMIT,
                BLOCK_BREAKING_PARTICLE_SCALE,
                BLOCK_BREAKING_PARTICLE_SPEED,
                BLOCK_REACH_DISTANCE,
                BREAKING_GRID_SIZE,
                BREAKING_RESTRICTION_MODE,
                CARPET_ACCURATE_PLACEMENT_PROTOCOL,
                CHAT_BACKGROUND_COLOR,
                CHAT_TIME_FORMAT,
                CHUNK_RENDER_TIMEOUT,
                CLIENT_PLACEMENT_ROTATION,
                CLOUD_HEIGHT_OVERRIDE,
                DEBUG_PIE_CHART_SCALE,
                ELYTRA_CAMERA_INDICATOR,
                FAST_BLOCK_PLACEMENT_COUNT,
                FAST_LEFT_CLICK_COUNT,
                FAST_PLACEMENT_REMEMBER,
                FAST_RIGHT_CLICK_COUNT,
                FILL_CLONE_LIMIT,
                FLEXIBLE_PLACEMENT_OVERLAY_COLOR,
                FLY_SPEED_PRESET_1,
                FLY_SPEED_PRESET_2,
                FLY_SPEED_PRESET_3,
                FLY_SPEED_PRESET_4,
                FREE_CAMERA_PLAYER_INPUTS,
                FREE_CAMERA_PLAYER_MOVEMENT,
                GAMMA_OVERRIDE_VALUE,
                HAND_RESTOCK_CONTINUOUS,
                HAND_RESTOCK_PRE,
                HAND_RESTOCK_PRE_THRESHOLD,
                HANGABLE_ENTITY_BYPASS_INVERSE,
                HOTBAR_SLOT_CYCLE_MAX,
                HOTBAR_SLOT_RANDOMIZER_MAX,
                HOTBAR_SWAP_OVERLAY_ALIGNMENT,
                HOTBAR_SWAP_OVERLAY_OFFSET_X,
                HOTBAR_SWAP_OVERLAY_OFFSET_Y,
                ITEM_SWAP_DURABILITY_THRESHOLD,
                LAVA_VISIBILITY_OPTIFINE,
                MAP_PREVIEW_SIZE,
                PERIODIC_ATTACK_INTERVAL,
                PERIODIC_USE_INTERVAL,
                PERMANENT_SNEAK_ALLOW_IN_GUIS,
                PLACEMENT_GRID_SIZE,
                PLACEMENT_LIMIT,
                PLACEMENT_RESTRICTION_MODE,
                PLACEMENT_RESTRICTION_TIED_TO_FAST,
                PLAYER_ON_FIRE_SCALE,
                POTION_WARNING_BENEFICIAL_ONLY,
                POTION_WARNING_THRESHOLD,
                REMEMBER_FLEXIBLE,
                RENDER_LIMIT_ITEM,
                RENDER_LIMIT_XP_ORB,
                SHULKER_DISPLAY_BACKGROUND_COLOR,
                SHULKER_DISPLAY_REQUIRE_SHIFT,
                SLOT_SYNC_WORKAROUND,
                SLOT_SYNC_WORKAROUND_ALWAYS,
                SNAP_AIM_INDICATOR,
                SNAP_AIM_INDICATOR_COLOR,
                SNAP_AIM_MODE,
                SNAP_AIM_ONLY_CLOSE_TO_ANGLE,
                SNAP_AIM_PITCH_OVERSHOOT,
                SNAP_AIM_PITCH_STEP,
                SNAP_AIM_THRESHOLD_PITCH,
                SNAP_AIM_THRESHOLD_YAW,
                SNAP_AIM_YAW_STEP,
                SOUND_NAME_OUTPUT,
                STRUCTURE_BLOCK_MAX_SIZE,
                ZOOM_ADJUST_MOUSE_SENSITIVITY,
                ZOOM_FOV
        );

        public static final List<Hotkey> HOTKEY_LIST = ImmutableList.of(
                CARPET_ACCURATE_PLACEMENT_PROTOCOL,
                FREE_CAMERA_PLAYER_INPUTS,
                FREE_CAMERA_PLAYER_MOVEMENT
        );
    }

    public static class Fixes
    {
        public static final BooleanConfig ELYTRA_FIX                = new BooleanConfig("elytraFix", false);
        public static final BooleanConfig TILE_UNLOAD_OPTIMIZATION  = new BooleanConfig("tileEntityUnloadOptimization", false);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                ELYTRA_FIX,
                TILE_UNLOAD_OPTIMIZATION
        );
    }

    public static class Lists
    {
        public static final StringListConfig FAST_PLACEMENT_ITEM_BLACKLIST      = new StringListConfig("fastPlacementItemBlackList", ImmutableList.of("minecraft:ender_chest", "minecraft:white_shulker_box"));
        public static final StringListConfig FAST_PLACEMENT_ITEM_WHITELIST      = new StringListConfig("fastPlacementItemWhiteList", ImmutableList.of());
        public static final StringListConfig FAST_RIGHT_CLICK_BLOCK_BLACKLIST   = new StringListConfig("fastRightClickBlockBlackList", ImmutableList.of("minecraft:chest", "minecraft:ender_chest", "minecraft:trapped_chest", "minecraft:white_shulker_box"));
        public static final StringListConfig FAST_RIGHT_CLICK_BLOCK_WHITELIST   = new StringListConfig("fastRightClickBlockWhiteList", ImmutableList.of());
        public static final StringListConfig FAST_RIGHT_CLICK_ITEM_BLACKLIST    = new StringListConfig("fastRightClickItemBlackList", ImmutableList.of("minecraft:fireworks"));
        public static final StringListConfig FAST_RIGHT_CLICK_ITEM_WHITELIST    = new StringListConfig("fastRightClickItemWhiteList", ImmutableList.of("minecraft:bucket", "minecraft:water_bucket", "minecraft:lava_bucket", "minecraft:glass_bottle"));
        public static final StringListConfig FLAT_WORLD_PRESETS                 = new StringListConfig("flatWorldPresets", ImmutableList.of("White Glass;1*minecraft:stained_glass;minecraft:plains;;minecraft:stained_glass", "Glass;1*minecraft:glass;minecraft:plains;;minecraft:glass"));
        public static final StringListConfig ITEM_GLINT_BLACKLIST               = new StringListConfig("itemGlintBlackList", ImmutableList.of("minecraft:potion"));
        public static final StringListConfig ITEM_GLINT_WHITELIST               = new StringListConfig("itemGlintWhiteList", ImmutableList.of());
        public static final StringListConfig POTION_WARNING_BLACKLIST           = new StringListConfig("potionWarningBlackList", ImmutableList.of("minecraft:hunger", "minecraft:mining_fatigue", "minecraft:nausea", "minecraft:poison", "minecraft:slowness", "minecraft:weakness"));
        public static final StringListConfig POTION_WARNING_WHITELIST           = new StringListConfig("potionWarningWhiteList", ImmutableList.of("minecraft:fire_resistance", "minecraft:invisibility", "minecraft:water_breathing"));
        public static final StringListConfig REPAIR_MODE_SLOTS                  = new StringListConfig("repairModeSlots", ImmutableList.of("mainhand", "offhand"));
        public static final StringListConfig SOUND_DISABLE_BLACKLIST            = new StringListConfig("soundDisableBlackList", ImmutableList.of());
        public static final StringListConfig SOUND_DISABLE_WHITELIST            = new StringListConfig("soundDisableWhiteList", ImmutableList.of());
        public static final StringListConfig UNSTACKING_ITEMS                   = new StringListConfig("unstackingItems", ImmutableList.of("minecraft:bucket", "minecraft:glass_bottle"));

        public static final OptionListConfig<ListType> FAST_PLACEMENT_ITEM_LIST_TYPE        = new OptionListConfig<>("fastPlacementItemListType", ListType.BLACKLIST);
        public static final OptionListConfig<ListType> FAST_RIGHT_CLICK_BLOCK_LIST_TYPE     = new OptionListConfig<>("fastRightClickBlockListType", ListType.BLACKLIST);
        public static final OptionListConfig<ListType> FAST_RIGHT_CLICK_ITEM_LIST_TYPE      = new OptionListConfig<>("fastRightClickItemListType", ListType.BLACKLIST);
        public static final OptionListConfig<ListType> ITEM_GLINT_LIST_TYPE                 = new OptionListConfig<>("itemGlintListType", ListType.BLACKLIST);
        public static final OptionListConfig<ListType> POTION_WARNING_LIST_TYPE             = new OptionListConfig<>("potionWarningListType", ListType.NONE);
        public static final OptionListConfig<ListType> SOUND_DISABLE_LIST_TYPE              = new OptionListConfig<>("soundDisableListType", ListType.NONE);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                FAST_PLACEMENT_ITEM_LIST_TYPE,
                FAST_PLACEMENT_ITEM_BLACKLIST,
                FAST_PLACEMENT_ITEM_WHITELIST,
                FAST_RIGHT_CLICK_BLOCK_LIST_TYPE,
                FAST_RIGHT_CLICK_BLOCK_BLACKLIST,
                FAST_RIGHT_CLICK_BLOCK_WHITELIST,
                FAST_RIGHT_CLICK_ITEM_LIST_TYPE,
                FAST_RIGHT_CLICK_ITEM_BLACKLIST,
                FAST_RIGHT_CLICK_ITEM_WHITELIST,
                FLAT_WORLD_PRESETS,
                ITEM_GLINT_LIST_TYPE,
                ITEM_GLINT_BLACKLIST,
                ITEM_GLINT_WHITELIST,
                POTION_WARNING_LIST_TYPE,
                POTION_WARNING_BLACKLIST,
                POTION_WARNING_WHITELIST,
                SOUND_DISABLE_LIST_TYPE,
                SOUND_DISABLE_BLACKLIST,
                SOUND_DISABLE_WHITELIST,
                REPAIR_MODE_SLOTS,
                UNSTACKING_ITEMS
        );
    }

    public static class Internal
    {
        public static final IntegerConfig FLY_SPEED_PRESET                  = new IntegerConfig("flySpeedPreset", 0, 0, 3);
        public static final DoubleConfig GAMMA_VALUE_ORIGINAL               = new DoubleConfig("gammaValueOriginal", 0, 0, 1);
        public static final IntegerConfig HOTBAR_SCROLL_CURRENT_ROW         = new IntegerConfig("hotbarScrollCurrentRow", 3, 0, 3);
        public static final DoubleConfig SLIME_BLOCK_SLIPPERINESS_ORIGINAL  = new DoubleConfig("slimeBlockSlipperinessOriginal", 0.8, 0, 1);
        public static final DoubleConfig SNAP_AIM_LAST_PITCH                = new DoubleConfig("snapAimLastPitch", 0, -135, 135);
        public static final DoubleConfig SNAP_AIM_LAST_YAW                  = new DoubleConfig("snapAimLastYaw", 0, 0, 360);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                FLY_SPEED_PRESET,
                GAMMA_VALUE_ORIGINAL,
                HOTBAR_SCROLL_CURRENT_ROW,
                SLIME_BLOCK_SLIPPERINESS_ORIGINAL,
                SNAP_AIM_LAST_YAW
        );
    }

    private static final List<ConfigOptionCategory> CATEGORIES = ImmutableList.of(
            BaseConfigOptionCategory.normal("Generic",          Generic.OPTIONS),
            BaseConfigOptionCategory.normal("Fixes",            Fixes.OPTIONS),
            BaseConfigOptionCategory.normal("Lists",            Lists.OPTIONS),
            BaseConfigOptionCategory.normal("TweakToggles",     FeatureToggle.TOGGLE_CONFIGS),
            BaseConfigOptionCategory.normal("TweakHotkeys",     FeatureToggle.TOGGLE_HOTKEYS),
            BaseConfigOptionCategory.normal("GenericHotkeys",   Hotkeys.HOTKEY_LIST),
            BaseConfigOptionCategory.normal("DisableToggles",   DisableToggle.TOGGLE_CONFIGS),
            BaseConfigOptionCategory.normal("DisableHotkeys",   DisableToggle.TOGGLE_HOTKEYS),
            BaseConfigOptionCategory.normal("Internal",         Internal.OPTIONS)
    );

    @Override
    public String getModId()
    {
        return Reference.MOD_ID;
    }

    @Override
    public String getModName()
    {
        return Reference.MOD_NAME;
    }

    @Override
    public String getConfigFileName()
    {
        return Reference.MOD_ID + ".json";
    }

    @Override
    public List<ConfigOptionCategory> getConfigOptionCategories()
    {
        return CATEGORIES;
    }

    @Override
    public void onPostLoad()
    {
        InventoryUtils.setRepairModeSlots(Lists.REPAIR_MODE_SLOTS.getStrings());
        InventoryUtils.setUnstackingItems(Lists.UNSTACKING_ITEMS.getStrings());

        PlacementTweaks.updateFastRightClickBlockRestriction();
        PlacementTweaks.updateFastRightClickItemRestriction();
        PlacementTweaks.updateFastPlacementItemRestriction();

        MiscTweaks.updateItemGlintRestriction();
        MiscTweaks.updatePotionRestrictionLists();
    }

    public static DoubleConfig getActiveFlySpeedConfig()
    {
        return getFlySpeedConfig(Configs.Internal.FLY_SPEED_PRESET.getIntegerValue());
    }

    public static DoubleConfig getFlySpeedConfig(int preset)
    {
        switch (preset)
        {
            case 1:  return Configs.Generic.FLY_SPEED_PRESET_2;
            case 2:  return Configs.Generic.FLY_SPEED_PRESET_3;
            case 3:  return Configs.Generic.FLY_SPEED_PRESET_4;
            case 0:
            default: return Configs.Generic.FLY_SPEED_PRESET_1;
        }
    }

    public static HotkeyConfig getFlySpeedHotkey(int preset)
    {
        switch (preset)
        {
            case 1:  return Hotkeys.FLY_PRESET_2;
            case 2:  return Hotkeys.FLY_PRESET_3;
            case 3:  return Hotkeys.FLY_PRESET_4;
            case 0:
            default: return Hotkeys.FLY_PRESET_1;
        }
    }
}
