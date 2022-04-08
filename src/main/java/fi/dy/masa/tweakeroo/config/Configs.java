package fi.dy.masa.tweakeroo.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import fi.dy.masa.malilib.config.category.BaseConfigOptionCategory;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ColorConfig;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.config.option.list.BlackWhiteListConfig;
import fi.dy.masa.malilib.config.option.list.EquipmentSlotListConfig;
import fi.dy.masa.malilib.config.option.list.IdentifierListConfig;
import fi.dy.masa.malilib.config.option.list.ItemListConfig;
import fi.dy.masa.malilib.config.option.list.StringListConfig;
import fi.dy.masa.malilib.config.value.ActiveMode;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.config.value.HudAlignment;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.util.restriction.UsageRestriction.ListType;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.util.PlacementRestrictionMode;
import fi.dy.masa.tweakeroo.util.SnapAimMode;

public class Configs
{
    public static final int CURRENT_VERSION = 1;

    public static class Generic
    {
        public static final HotkeyedBooleanConfig CARPET_ACCURATE_PLACEMENT_PROTOCOL    = new HotkeyedBooleanConfig("carpetAccuratePlacementProtocol", true, "");
        public static final HotkeyedBooleanConfig FREE_CAMERA_PLAYER_INPUTS             = new HotkeyedBooleanConfig("freeCameraPlayerInputs", false, "");
        public static final HotkeyedBooleanConfig FREE_CAMERA_PLAYER_MOVEMENT           = new HotkeyedBooleanConfig("freeCameraPlayerMovement", false, "");

        public static final IntegerConfig AFTER_CLICKER_CLICK_COUNT                     = new IntegerConfig("afterClickerClickCount", 1, 1, 64);
        public static final DoubleConfig BLOCK_REACH_DISTANCE                           = new DoubleConfig("blockReachDistance", 4.5, 0, 8);
        public static final IntegerConfig BLOCK_BREAKING_PARTICLE_LIMIT                 = new IntegerConfig("blockBreakingParticleLimit", 8, 1, 1024);
        public static final DoubleConfig BLOCK_BREAKING_PARTICLE_SCALE                  = new DoubleConfig("blockBreakingParticleScale", 1.0, 0, 10D);
        public static final DoubleConfig BLOCK_BREAKING_PARTICLE_SPEED                  = new DoubleConfig("blockBreakingParticleSpeedMultiplier", 1.0, 0, 20D);
        public static final IntegerConfig BREAKING_GRID_SIZE                            = new IntegerConfig("breakingGridSize", 3, 1, 1000);
        public static final ColorConfig CHAT_BACKGROUND_COLOR                           = new ColorConfig("chatBackgroundColor", "#80000000");
        public static final StringConfig CHAT_TIME_FORMAT                               = new StringConfig("chatTimeFormat", "[HH:mm:ss]");
        public static final IntegerConfig CHUNK_RENDER_TIMEOUT                          = new IntegerConfig("chunkRenderTimeout", 50000000, 1, Integer.MAX_VALUE);
        public static final BooleanConfig CLIENT_PLACEMENT_ROTATION                     = new BooleanConfig("clientPlacementRotation", true);
        public static final DoubleConfig CLOUD_HEIGHT_OVERRIDE                          = new DoubleConfig("cloudHeightOverride", 128, -1024, 1024);
        public static final DoubleConfig DEBUG_PIE_CHART_SCALE                          = new DoubleConfig("debugPieChartScale", 1, 0, 10);
        public static final IntegerConfig FAST_BLOCK_PLACEMENT_COUNT                    = new IntegerConfig("fastBlockPlacementCount", 2, 1, 16);
        public static final IntegerConfig FAST_LEFT_CLICK_COUNT                         = new IntegerConfig("fastLeftClickCount", 2, 1, 64);
        public static final BooleanConfig FAST_PLACEMENT_REMEMBER                       = new BooleanConfig("fastPlacementRememberOrientation", true);
        public static final IntegerConfig FAST_RIGHT_CLICK_COUNT                        = new IntegerConfig("fastRightClickCount", 2, 1, 64);
        public static final IntegerConfig FILL_CLONE_LIMIT                              = new IntegerConfig("fillCloneLimit", 10000000, 1, 1000000000);
        public static final ColorConfig FLEXIBLE_PLACEMENT_OVERLAY_COLOR                = new ColorConfig("flexibleBlockPlacementOverlayColor", "#C03030F0");
        public static final DoubleConfig FLY_SPEED_PRESET_1                             = new DoubleConfig("flySpeedPreset1", 0.01, 0.0, 4.0);
        public static final DoubleConfig FLY_SPEED_PRESET_2                             = new DoubleConfig("flySpeedPreset2", 0.064, 0.0, 4.0);
        public static final DoubleConfig FLY_SPEED_PRESET_3                             = new DoubleConfig("flySpeedPreset3", 0.128, 0.0, 4.0);
        public static final DoubleConfig FLY_SPEED_PRESET_4                             = new DoubleConfig("flySpeedPreset4", 0.32, 0.0, 4.0);
        public static final DoubleConfig GAMMA_OVERRIDE_VALUE                           = new DoubleConfig("gammaOverrideValue", 16, 0, 32);
        public static final BooleanConfig HAND_RESTOCK_CONTINUOUS                       = new BooleanConfig("handRestockContinuous", false);
        public static final BooleanConfig HAND_RESTOCK_PRE                              = new BooleanConfig("handRestockPre", true);
        public static final IntegerConfig HAND_RESTOCK_PRE_THRESHOLD                    = new IntegerConfig("handRestockPreThreshold", 6, 1, 64);
        public static final BooleanConfig HANGABLE_ENTITY_BYPASS_INVERSE                = new BooleanConfig("hangableEntityBypassInverse", false);
        public static final IntegerConfig HOTBAR_SLOT_CYCLE_MAX                         = new IntegerConfig("hotbarSlotCycleMax", 2, 1, 9);
        public static final IntegerConfig HOTBAR_SLOT_RANDOMIZER_MAX                    = new IntegerConfig("hotbarSlotRandomizerMax", 5, 1, 9);
        public static final IntegerConfig HOTBAR_SWAP_OVERLAY_OFFSET_X                  = new IntegerConfig("hotbarSwapOverlayOffsetX", 4);
        public static final IntegerConfig HOTBAR_SWAP_OVERLAY_OFFSET_Y                  = new IntegerConfig("hotbarSwapOverlayOffsetY", 4);
        public static final IntegerConfig ITEM_PREVIEW_Z                                = new IntegerConfig("itemPreviewZ", 400, 0, 4096);
        public static final IntegerConfig ITEM_SWAP_DURABILITY_THRESHOLD                = new IntegerConfig("itemSwapDurabilityThreshold", 20, 0, 10000);
        public static final BooleanConfig LAVA_VISIBILITY_OPTIFINE                      = new BooleanConfig("lavaVisibilityOptifineCompat", true);
        public static final BooleanConfig MAP_PREVIEW_REQUIRE_SHIFT                     = new BooleanConfig("mapPreviewRequireShift", true);
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
        public static final BooleanConfig REPAIR_MODE_MENDING_ONLY                      = new BooleanConfig("repairModeMendingOnly", true);
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
        public static final DoubleConfig SNAP_AIM_THRESHOLD_PITCH                       = new DoubleConfig("snapAimThresholdPitch", 1.5, 0, 360);
        public static final DoubleConfig SNAP_AIM_THRESHOLD_YAW                         = new DoubleConfig("snapAimThresholdYaw", 5.0, 0, 360);
        public static final DoubleConfig SNAP_AIM_YAW_STEP                              = new DoubleConfig("snapAimYawStep", 45, 0, 360);
        public static final BooleanConfig SOUND_NAME_OUTPUT                             = new BooleanConfig("soundNameOutput", false);
        public static final DoubleConfig STATIC_FOV                                     = new DoubleConfig("staticFov", 70, 0.01, 359.99);
        public static final IntegerConfig STRUCTURE_BLOCK_MAX_SIZE                      = new IntegerConfig("structureBlockMaxSize", 128, 1, 256);
        public static final StringConfig WORLD_LIST_DATE_FORMAT                         = new StringConfig("worldListDateFormat", "yyyy-MM-dd HH:mm:ss");
        public static final BooleanConfig ZOOM_ADJUST_MOUSE_SENSITIVITY                 = new BooleanConfig("zoomAdjustMouseSensitivity", true);
        public static final DoubleConfig ZOOM_FOV                                       = new DoubleConfig("zoomFov", 30, 0.01, 359.99);

        public static final OptionListConfig<PlacementRestrictionMode> BREAKING_RESTRICTION_MODE    = new OptionListConfig<>("breakingRestrictionMode", PlacementRestrictionMode.LINE, PlacementRestrictionMode.VALUES);
        public static final OptionListConfig<ActiveMode> ELYTRA_CAMERA_INDICATOR                    = new OptionListConfig<>("elytraCameraIndicator", ActiveMode.WITH_KEY, ActiveMode.VALUES);
        public static final OptionListConfig<HudAlignment> HOTBAR_SWAP_OVERLAY_ALIGNMENT            = new OptionListConfig<>("hotbarSwapOverlayAlignment", HudAlignment.BOTTOM_RIGHT, HudAlignment.VALUES);
        public static final OptionListConfig<PlacementRestrictionMode> PLACEMENT_RESTRICTION_MODE   = new OptionListConfig<>("placementRestrictionMode", PlacementRestrictionMode.FACE, PlacementRestrictionMode.VALUES);
        public static final OptionListConfig<SnapAimMode> SNAP_AIM_MODE                             = new OptionListConfig<>("snapAimMode", SnapAimMode.YAW, SnapAimMode.VALUES);

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
                ITEM_PREVIEW_Z,
                ITEM_SWAP_DURABILITY_THRESHOLD,
                LAVA_VISIBILITY_OPTIFINE,
                MAP_PREVIEW_REQUIRE_SHIFT,
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
                REPAIR_MODE_MENDING_ONLY,
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
                STATIC_FOV,
                STRUCTURE_BLOCK_MAX_SIZE,
                WORLD_LIST_DATE_FORMAT,
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
        public static final StringListConfig FLAT_WORLD_PRESETS                 = new StringListConfig("flatWorldPresets", ImmutableList.of("White Glass;1*minecraft:stained_glass;minecraft:plains;;minecraft:stained_glass", "Glass;1*minecraft:glass;minecraft:plains;;minecraft:glass"));
        public static final EquipmentSlotListConfig REPAIR_MODE_SLOTS           = EquipmentSlotListConfig.create("repairModeSlots", ImmutableList.of(EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND));
        public static final EquipmentSlotListConfig SWAP_BROKEN_TOOLS_SLOTS     = EquipmentSlotListConfig.create("swapBrokenToolsSlots", ImmutableList.of(EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND), ImmutableList.of(EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND));
        public static final ItemListConfig UNSTACKING_ITEMS                     = new ItemListConfig("unstackingItems", ImmutableList.of(Items.BUCKET, Items.GLASS_BOTTLE));

        public static final BlackWhiteListConfig<Item>              FAST_PLACEMENT_ITEM_LIST        = new BlackWhiteListConfig<>("fastPlacementItemList", BlackWhiteList.itemNames(ListType.BLACKLIST, ImmutableList.of("minecraft:ender_chest", "minecraft:white_shulker_box"), ImmutableList.of()));
        public static final BlackWhiteListConfig<Block>             FAST_RIGHT_CLICK_BLOCK_LIST     = new BlackWhiteListConfig<>("fastRightClickBlockList", BlackWhiteList.blocks(ListType.BLACKLIST, ImmutableList.of(Blocks.CHEST, Blocks.ENDER_CHEST, Blocks.TRAPPED_CHEST, Blocks.WHITE_SHULKER_BOX), ImmutableList.of()));
        public static final BlackWhiteListConfig<Item>              FAST_RIGHT_CLICK_ITEM_LIST      = new BlackWhiteListConfig<>("fastRightClickItemList", BlackWhiteList.items(ListType.BLACKLIST, ImmutableList.of(Items.FIREWORKS), ImmutableList.of(Items.BUCKET, Items.WATER_BUCKET, Items.LAVA_BUCKET, Items.GLASS_BOTTLE)));
        public static final BlackWhiteListConfig<Item>              ITEM_GLINT_ITEM_LIST            = new BlackWhiteListConfig<>("itemGlintItemList", BlackWhiteList.items(ListType.BLACKLIST, ImmutableList.of(Items.POTIONITEM, Items.SPLASH_POTION, Items.LINGERING_POTION), ImmutableList.of()));
        public static final BlackWhiteListConfig<Potion>            POTION_WARNING_LIST             = new BlackWhiteListConfig<>("potionWarningList", BlackWhiteList.effects(ListType.NONE, ImmutableList.of("minecraft:hunger", "minecraft:mining_fatigue", "minecraft:nausea", "minecraft:poison", "minecraft:slowness", "minecraft:weakness"), ImmutableList.of("minecraft:fire_resistance", "minecraft:invisibility", "minecraft:water_breathing")));
        public static final BlackWhiteListConfig<ResourceLocation>  SOUND_DISABLE_LIST              = new BlackWhiteListConfig<>("soundDisableList", new BlackWhiteList<>(ListType.NONE, IdentifierListConfig.create("malilib.label.list_type.blacklist", getSortedSoundNamesList()), IdentifierListConfig.create("malilib.label.list_type.whitelist", getSortedSoundNamesList())));

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                FAST_PLACEMENT_ITEM_LIST,
                FAST_RIGHT_CLICK_BLOCK_LIST,
                FAST_RIGHT_CLICK_ITEM_LIST,
                FLAT_WORLD_PRESETS,
                ITEM_GLINT_ITEM_LIST,
                POTION_WARNING_LIST,
                REPAIR_MODE_SLOTS,
                SOUND_DISABLE_LIST,
                SWAP_BROKEN_TOOLS_SLOTS,
                UNSTACKING_ITEMS
        );

        public static List<ResourceLocation> getSortedSoundNamesList()
        {
            List<ResourceLocation> names = new ArrayList<>(SoundEvent.REGISTRY.getKeys());
            names.sort(Comparator.comparing(ResourceLocation::toString));
            return names;
        }
    }

    public static class Internal
    {
        public static final DoubleConfig  ACTIVE_FLY_SPEED_OVERRIDE_VALUE   = new DoubleConfig("activeFlySpeedOverrideValue", 0.064, 0.0, 4.0);
        public static final IntegerConfig FLY_SPEED_PRESET                  = new IntegerConfig("flySpeedPreset", 0, 0, 3);
        public static final DoubleConfig  GAMMA_VALUE_ORIGINAL              = new DoubleConfig("gammaValueOriginal", -1, -1, 1000);
        public static final IntegerConfig HOTBAR_SCROLL_CURRENT_ROW         = new IntegerConfig("hotbarScrollCurrentRow", 2, 0, 2);
        public static final DoubleConfig  SLIME_BLOCK_SLIPPERINESS_ORIGINAL = new DoubleConfig("slimeBlockSlipperinessOriginal", 0.8, 0, 1);
        public static final DoubleConfig  SNAP_AIM_LAST_PITCH               = new DoubleConfig("snapAimLastPitch", 0, -135, 135);
        public static final DoubleConfig  SNAP_AIM_LAST_YAW                 = new DoubleConfig("snapAimLastYaw", 0, 0, 360);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                ACTIVE_FLY_SPEED_OVERRIDE_VALUE,
                FLY_SPEED_PRESET,
                GAMMA_VALUE_ORIGINAL,
                HOTBAR_SCROLL_CURRENT_ROW,
                SLIME_BLOCK_SLIPPERINESS_ORIGINAL,
                SNAP_AIM_LAST_YAW
        );
    }

    public static final List<ConfigOptionCategory> CATEGORIES = ImmutableList.of(
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "Generic",          Generic.OPTIONS),
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "Fixes",            Fixes.OPTIONS),
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "Lists",            Lists.OPTIONS),
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "TweakToggles",     FeatureToggle.TOGGLE_CONFIGS),
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "TweakHotkeys",     FeatureToggle.TOGGLE_HOTKEYS),
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "GenericHotkeys",   Hotkeys.HOTKEY_LIST),
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "YeetToggles",      DisableToggle.TOGGLE_CONFIGS),
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "YeetHotkeys",      DisableToggle.TOGGLE_HOTKEYS),
            BaseConfigOptionCategory.normal(Reference.MOD_INFO, "Internal",         Internal.OPTIONS)
    );

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
}
