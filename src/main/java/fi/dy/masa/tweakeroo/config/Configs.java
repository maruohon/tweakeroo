package fi.dy.masa.tweakeroo.config;

import java.io.File;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.config.options.ConfigStringList;
import fi.dy.masa.malilib.util.ActiveMode;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction.ListType;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.tweaks.MiscTweaks;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.PlacementRestrictionMode;
import fi.dy.masa.tweakeroo.util.SnapAimMode;

public class Configs implements IConfigHandler
{
    private static final String CONFIG_FILE_NAME = Reference.MOD_ID + ".json";

    public static class Generic
    {
        public static final String PREFIX = String.format("%s.label.config_gui.generic", Reference.MOD_ID);
        public static final ConfigInteger       AFTER_CLICKER_CLICK_COUNT           = new MyConfigInteger     (PREFIX, "afterClickerClickCount",  1, 1, 32);
        public static final ConfigDouble        BLOCK_REACH_DISTANCE                = new MyConfigDouble      (PREFIX, "blockReachDistance", 4.5, 0, 8);
        public static final ConfigInteger       BREAKING_GRID_SIZE                  = new MyConfigInteger     (PREFIX, "breakingGridSize", 3, 1, 1000);
        public static final ConfigOptionList    BREAKING_RESTRICTION_MODE           = new MyConfigOptionList  (PREFIX, "breakingRestrictionMode", PlacementRestrictionMode.LINE);
        public static final ConfigColor         CHAT_BACKGROUND_COLOR               = new MyConfigColor       (PREFIX, "chatBackgroundColor", "#80000000");
        public static final ConfigString        CHAT_TIME_FORMAT                    = new MyConfigString      (PREFIX, "chatTimeFormat", "[HH:mm:ss]");
        public static final ConfigBoolean       CLIENT_PLACEMENT_ROTATION           = new MyConfigBoolean     (PREFIX, "clientPlacementRotation", true);
        public static final ConfigOptionList    ELYTRA_CAMERA_INDICATOR             = new MyConfigOptionList  (PREFIX, "elytraCameraIndicator", ActiveMode.WITH_KEY);
        public static final ConfigInteger       FAST_BLOCK_PLACEMENT_COUNT          = new MyConfigInteger     (PREFIX, "fastBlockPlacementCount", 2, 1, 16);
        public static final ConfigInteger       FAST_LEFT_CLICK_COUNT               = new MyConfigInteger     (PREFIX, "fastLeftClickCount",  10, 1, 64);
        public static final ConfigInteger       FAST_RIGHT_CLICK_COUNT              = new MyConfigInteger     (PREFIX, "fastRightClickCount", 10, 1, 64);
        public static final ConfigInteger       FILL_CLONE_LIMIT                    = new MyConfigInteger     (PREFIX, "fillCloneLimit", 10000000, 1, 1000000000);
        public static final ConfigColor         FLEXIBLE_PLACEMENT_OVERLAY_COLOR    = new MyConfigColor       (PREFIX, "flexibleBlockPlacementOverlayColor", "#C03030F0");
        public static final ConfigDouble        FLY_SPEED_PRESET_1                  = new MyConfigDouble      (PREFIX, "flySpeedPreset1", 0.01, 0, 4);
        public static final ConfigDouble        FLY_SPEED_PRESET_2                  = new MyConfigDouble      (PREFIX, "flySpeedPreset2", 0.064, 0, 4);
        public static final ConfigDouble        FLY_SPEED_PRESET_3                  = new MyConfigDouble      (PREFIX, "flySpeedPreset3", 0.128, 0, 4);
        public static final ConfigDouble        FLY_SPEED_PRESET_4                  = new MyConfigDouble      (PREFIX, "flySpeedPreset4", 0.32, 0, 4);
        public static final ConfigBoolean       FREE_CAMERA_MOTION_TOGGLE           = new MyConfigBoolean     (PREFIX, "freeCameraMotionToggle", true);
        public static final ConfigInteger       GAMMA_OVERRIDE_VALUE                = new MyConfigInteger     (PREFIX, "gammaOverrideValue", 16, 0, 1000);
        public static final ConfigBoolean       HAND_RESTOCK_PRE                    = new MyConfigBoolean     (PREFIX, "handRestockPre", true);
        public static final ConfigInteger       HOTBAR_SLOT_CYCLE_MAX               = new MyConfigInteger     (PREFIX, "hotbarSlotCycleMax", 2, 1, 9);
        public static final ConfigInteger       HOTBAR_SLOT_RANDOMIZER_MAX          = new MyConfigInteger     (PREFIX, "hotbarSlotRandomizerMax", 5, 1, 9);
        public static final ConfigOptionList    HOTBAR_SWAP_OVERLAY_ALIGNMENT       = new MyConfigOptionList  (PREFIX, "hotbarSwapOverlayAlignment", HudAlignment.BOTTOM_RIGHT);
        public static final ConfigInteger       HOTBAR_SWAP_OVERLAY_OFFSET_X        = new MyConfigInteger     (PREFIX, "hotbarSwapOverlayOffsetX", 4);
        public static final ConfigInteger       HOTBAR_SWAP_OVERLAY_OFFSET_Y        = new MyConfigInteger     (PREFIX, "hotbarSwapOverlayOffsetY", 4);
        public static final ConfigInteger       ITEM_SWAP_DURABILITY_THRESHOLD      = new MyConfigInteger     (PREFIX, "itemSwapDurabilityThreshold", 20, 0, 10000);
        public static final ConfigBoolean       LAVA_VISIBILITY_OPTIFINE            = new MyConfigBoolean     (PREFIX, "lavaVisibilityOptifineCompat", true);
        public static final ConfigInteger       MAP_PREVIEW_SIZE                    = new MyConfigInteger     (PREFIX, "mapPreviewSize", 160, 16, 512);
        public static final ConfigInteger       PERIODIC_ATTACK_INTERVAL            = new MyConfigInteger     (PREFIX, "periodicAttackInterval", 20, 0, Integer.MAX_VALUE);
        public static final ConfigInteger       PERIODIC_USE_INTERVAL               = new MyConfigInteger     (PREFIX, "periodicUseInterval", 20, 0, Integer.MAX_VALUE);
        public static final ConfigBoolean       PERMANENT_SNEAK_ALLOW_IN_GUIS       = new MyConfigBoolean     (PREFIX, "permanentSneakAllowInGUIs", false);
        public static final ConfigInteger       PLACEMENT_GRID_SIZE                 = new MyConfigInteger     (PREFIX, "placementGridSize", 3, 1, 1000);
        public static final ConfigInteger       PLACEMENT_LIMIT                     = new MyConfigInteger     (PREFIX, "placementLimit", 3, 1, 10000);
        public static final ConfigOptionList    PLACEMENT_RESTRICTION_MODE          = new MyConfigOptionList  (PREFIX, "placementRestrictionMode", PlacementRestrictionMode.FACE);
        public static final ConfigBoolean       PLACEMENT_RESTRICTION_TIED_TO_FAST  = new MyConfigBoolean     (PREFIX, "placementRestrictionTiedToFast", true);
        public static final ConfigBoolean       POTION_WARNING_BENEFICIAL_ONLY      = new MyConfigBoolean     (PREFIX, "potionWarningBeneficialOnly", true);
        public static final ConfigInteger       POTION_WARNING_THRESHOLD            = new MyConfigInteger     (PREFIX, "potionWarningThreshold", 600, 1, 1000000);
        public static final ConfigInteger       RENDER_LIMIT_ITEM                   = new MyConfigInteger     (PREFIX, "renderLimitItem", -1, -1, 10000);
        public static final ConfigInteger       RENDER_LIMIT_XP_ORB                 = new MyConfigInteger     (PREFIX, "renderLimitXPOrb", -1, -1, 10000);
        public static final ConfigBoolean       SHULKER_DISPLAY_BACKGROUND_COLOR    = new MyConfigBoolean     (PREFIX, "shulkerDisplayBgColor", true);
        public static final ConfigBoolean       SHULKER_DISPLAY_REQUIRE_SHIFT       = new MyConfigBoolean     (PREFIX, "shulkerDisplayRequireShift", true);
        public static final ConfigBoolean       SLOT_SYNC_WORKAROUND                = new MyConfigBoolean     (PREFIX, "slotSyncWorkaround", true);
        public static final ConfigBoolean       SNAP_AIM_INDICATOR                  = new MyConfigBoolean     (PREFIX, "snapAimIndicator", true);
        public static final ConfigColor         SNAP_AIM_INDICATOR_COLOR            = new MyConfigColor       (PREFIX, "snapAimIndicatorColor", "#603030FF");
        public static final ConfigOptionList    SNAP_AIM_MODE                       = new MyConfigOptionList  (PREFIX, "snapAimMode", SnapAimMode.YAW);
        public static final ConfigBoolean       SNAP_AIM_PITCH_OVERSHOOT            = new MyConfigBoolean     (PREFIX, "snapAimPitchOvershoot", false);
        public static final ConfigDouble        SNAP_AIM_PITCH_STEP                 = new MyConfigDouble      (PREFIX, "snapAimPitchStep", 12.5, 0, 90);
        public static final ConfigDouble        SNAP_AIM_YAW_STEP                   = new MyConfigDouble      (PREFIX, "snapAimYawStep", 45, 0, 360);
        public static final ConfigInteger       STRUCTURE_BLOCK_MAX_SIZE            = new MyConfigInteger     (PREFIX, "structureBlockMaxSize", 128, 1, 256);
        public static final ConfigDouble        ZOOM_FOV                            = new MyConfigDouble      (PREFIX, "zoomFov", 30, 0, 600);

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                CLIENT_PLACEMENT_ROTATION,
                FREE_CAMERA_MOTION_TOGGLE,
                LAVA_VISIBILITY_OPTIFINE,
                HAND_RESTOCK_PRE,
                PERMANENT_SNEAK_ALLOW_IN_GUIS,
                PLACEMENT_RESTRICTION_TIED_TO_FAST,
                POTION_WARNING_BENEFICIAL_ONLY,
                SHULKER_DISPLAY_BACKGROUND_COLOR,
                SHULKER_DISPLAY_REQUIRE_SHIFT,
                SLOT_SYNC_WORKAROUND,
                SNAP_AIM_INDICATOR,
                SNAP_AIM_PITCH_OVERSHOOT,

                BREAKING_RESTRICTION_MODE,
                ELYTRA_CAMERA_INDICATOR,
                PLACEMENT_RESTRICTION_MODE,
                HOTBAR_SWAP_OVERLAY_ALIGNMENT,
                SNAP_AIM_MODE,

                CHAT_TIME_FORMAT,
                CHAT_BACKGROUND_COLOR,
                FLEXIBLE_PLACEMENT_OVERLAY_COLOR,
                SNAP_AIM_INDICATOR_COLOR,

                AFTER_CLICKER_CLICK_COUNT,
                BLOCK_REACH_DISTANCE,
                BREAKING_GRID_SIZE,
                FAST_BLOCK_PLACEMENT_COUNT,
                FAST_LEFT_CLICK_COUNT,
                FAST_RIGHT_CLICK_COUNT,
                FILL_CLONE_LIMIT,
                FLY_SPEED_PRESET_1,
                FLY_SPEED_PRESET_2,
                FLY_SPEED_PRESET_3,
                FLY_SPEED_PRESET_4,
                GAMMA_OVERRIDE_VALUE,
                HOTBAR_SLOT_CYCLE_MAX,
                HOTBAR_SLOT_RANDOMIZER_MAX,
                HOTBAR_SWAP_OVERLAY_OFFSET_X,
                HOTBAR_SWAP_OVERLAY_OFFSET_Y,
                ITEM_SWAP_DURABILITY_THRESHOLD,
                MAP_PREVIEW_SIZE,
                PERIODIC_ATTACK_INTERVAL,
                PERIODIC_USE_INTERVAL,
                PLACEMENT_GRID_SIZE,
                PLACEMENT_LIMIT,
                POTION_WARNING_THRESHOLD,
                RENDER_LIMIT_ITEM,
                RENDER_LIMIT_XP_ORB,
                SNAP_AIM_PITCH_STEP,
                SNAP_AIM_YAW_STEP,
                STRUCTURE_BLOCK_MAX_SIZE,
                ZOOM_FOV
        );
        private static String toNameKey(String key) {
            return StringUtils.translate(String.format("tweakeroo.label.config_gui.generic.%s", key));
        }
        private static String toCommentKey(String key) {
            return StringUtils.translate(String.format("tweakeroo.label.config_gui.generic.comment.%s", key));
        }
    }

    public static class Fixes
    {
        public static final String PREFIX = String.format("%s.label.config_gui.fixes", Reference.MOD_ID);
        public static final ConfigBoolean CLIENT_CHUNK_ENTITY_DUPE          = new MyConfigBoolean(PREFIX, "clientChunkEntityDupeFix", false);
        public static final ConfigBoolean ELYTRA_FIX                        = new MyConfigBoolean(PREFIX, "elytraFix", false);
        public static final ConfigBoolean PROFILER_CHART_FIX                = new MyConfigBoolean(PREFIX, "profilerChartFix", false);
        public static final ConfigBoolean RAVAGER_CLIENT_BLOCK_BREAK_FIX    = new MyConfigBoolean(PREFIX, "ravagerClientBlockBreakFix", false);
        public static final ConfigBoolean TILE_UNLOAD_OPTIMIZATION          = new MyConfigBoolean(PREFIX, "tileEntityUnloadOptimization", false);
        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                CLIENT_CHUNK_ENTITY_DUPE,
                ELYTRA_FIX,
                PROFILER_CHART_FIX,
                RAVAGER_CLIENT_BLOCK_BREAK_FIX,
                TILE_UNLOAD_OPTIMIZATION
        );
    }

    public static class Lists
    {
        public static final String PREFIX = String.format("%s.label.config_gui.lists", Reference.MOD_ID);
        public static final ConfigOptionList FAST_PLACEMENT_ITEM_LIST_TYPE      = new MyConfigOptionList(PREFIX, "fastPlacementItemListType", ListType.BLACKLIST);
        public static final ConfigStringList FAST_PLACEMENT_ITEM_BLACKLIST      = new MyConfigStringList(PREFIX, "fastPlacementItemBlackList", ImmutableList.of(PREFIX, "minecraft:ender_chest", "minecraft:white_shulker_box"));
        public static final ConfigStringList FAST_PLACEMENT_ITEM_WHITELIST      = new MyConfigStringList(PREFIX, "fastPlacementItemWhiteList", ImmutableList.of());
        public static final ConfigOptionList FAST_RIGHT_CLICK_BLOCK_LIST_TYPE   = new MyConfigOptionList(PREFIX, "fastRightClickBlockListType", ListType.BLACKLIST);
        public static final ConfigStringList FAST_RIGHT_CLICK_BLOCK_BLACKLIST   = new MyConfigStringList(PREFIX, "fastRightClickBlockBlackList", ImmutableList.of(PREFIX, "minecraft:chest", "minecraft:ender_chest", "minecraft:trapped_chest", "minecraft:white_shulker_box"));
        public static final ConfigStringList FAST_RIGHT_CLICK_BLOCK_WHITELIST   = new MyConfigStringList(PREFIX, "fastRightClickBlockWhiteList", ImmutableList.of());
        public static final ConfigOptionList FAST_RIGHT_CLICK_ITEM_LIST_TYPE    = new MyConfigOptionList(PREFIX, "fastRightClickListType", ListType.NONE);
        public static final ConfigStringList FAST_RIGHT_CLICK_ITEM_BLACKLIST    = new MyConfigStringList(PREFIX, "fastRightClickBlackList", ImmutableList.of(PREFIX, "minecraft:fireworks"));
        public static final ConfigStringList FAST_RIGHT_CLICK_ITEM_WHITELIST    = new MyConfigStringList(PREFIX, "fastRightClickWhiteList", ImmutableList.of(PREFIX, "minecraft:bucket", "minecraft:water_bucket", "minecraft:lava_bucket", "minecraft:glass_bottle"));
        public static final ConfigStringList FLAT_WORLD_PRESETS                 = new MyConfigStringList(PREFIX, "flatWorldPresets", ImmutableList.of(PREFIX, "White Glass;1*minecraft:white_stained_glass;minecraft:plains;;minecraft:white_stained_glass", "Glass;1*minecraft:glass;minecraft:plains;;minecraft:glass"));
        public static final ConfigOptionList POTION_WARNING_LIST_TYPE           = new MyConfigOptionList(PREFIX, "potionWarningListType", ListType.NONE);
        public static final ConfigStringList POTION_WARNING_BLACKLIST           = new MyConfigStringList(PREFIX, "potionWarningBlackList", ImmutableList.of(PREFIX, "minecraft:hunger", "minecraft:mining_fatigue", "minecraft:nausea", "minecraft:poison", "minecraft:slowness", "minecraft:weakness"));
        public static final ConfigStringList POTION_WARNING_WHITELIST           = new MyConfigStringList(PREFIX, "potionWarningWhiteList", ImmutableList.of(PREFIX, "minecraft:fire_resistance", "minecraft:invisibility", "minecraft:water_breathing"));
        public static final ConfigStringList REPAIR_MODE_SLOTS                  = new MyConfigStringList(PREFIX, "repairModeSlots", ImmutableList.of(PREFIX, "mainhand", "offhand"));
        public static final ConfigStringList UNSTACKING_ITEMS                   = new MyConfigStringList(PREFIX, "unstackingItems", ImmutableList.of(PREFIX, "minecraft:bucket", "minecraft:glass_bottle"));
        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                FAST_PLACEMENT_ITEM_LIST_TYPE,
                FAST_RIGHT_CLICK_BLOCK_LIST_TYPE,
                FAST_RIGHT_CLICK_ITEM_LIST_TYPE,
                POTION_WARNING_LIST_TYPE,
                FAST_PLACEMENT_ITEM_BLACKLIST,
                FAST_PLACEMENT_ITEM_WHITELIST,
                FAST_RIGHT_CLICK_BLOCK_BLACKLIST,
                FAST_RIGHT_CLICK_BLOCK_WHITELIST,
                FAST_RIGHT_CLICK_ITEM_BLACKLIST,
                FAST_RIGHT_CLICK_ITEM_WHITELIST,
                FLAT_WORLD_PRESETS,
                POTION_WARNING_BLACKLIST,
                POTION_WARNING_WHITELIST,
                REPAIR_MODE_SLOTS,
                UNSTACKING_ITEMS
        );
    }

    public static class Disable
    {
        public static final String PREFIX = String.format("%s.label.config_gui.disable", Reference.MOD_ID);
        public static final ConfigBooleanHotkeyed       DISABLE_BLOCK_BREAK_PARTICLES   = new MyConfigBooleanHotkeyed(PREFIX, "disableBlockBreakingParticles",        false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_DOUBLE_TAP_SPRINT       = new MyConfigBooleanHotkeyed(PREFIX, "disableDoubleTapSprint",               false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_BOSS_FOG                = new MyConfigBooleanHotkeyed(PREFIX, "disableBossFog",                       false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_CLIENT_ENTITY_UPDATES   = new MyConfigBooleanHotkeyed(PREFIX, "disableClientEntityUpdates",           false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_DEAD_MOB_RENDERING      = new MyConfigBooleanHotkeyed(PREFIX, "disableDeadMobRendering",              false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_DEAD_MOB_TARGETING      = new MyConfigBooleanHotkeyed(PREFIX, "disableDeadMobTargeting",              false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_ENTITY_RENDERING        = new MyConfigBooleanHotkeyed(PREFIX, "disableEntityRendering",               false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_ENTITY_TICKING          = new ConfigBooleanClient  (PREFIX, "disableEntityTicking",                 false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_FALLING_BLOCK_RENDER    = new MyConfigBooleanHotkeyed(PREFIX, "disableFallingBlockEntityRendering",   false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_INVENTORY_EFFECTS       = new MyConfigBooleanHotkeyed(PREFIX, "disableInventoryEffectRendering",      false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_ITEM_SWITCH_COOLDOWN    = new MyConfigBooleanHotkeyed(PREFIX, "disableItemSwitchRenderCooldown",      false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_MOB_SPAWNER_MOB_RENDER  = new MyConfigBooleanHotkeyed(PREFIX, "disableMobSpawnerMobRendering",        false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_NETHER_FOG              = new MyConfigBooleanHotkeyed(PREFIX, "disableNetherFog",                     false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_OBSERVER                = new ConfigBooleanClient  (PREFIX, "disableObserver",                      false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_OFFHAND_RENDERING       = new MyConfigBooleanHotkeyed(PREFIX, "disableOffhandRendering",              false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_PARTICLES               = new MyConfigBooleanHotkeyed(PREFIX, "disableParticles",                     false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_PORTAL_GUI_CLOSING      = new MyConfigBooleanHotkeyed(PREFIX, "disablePortalGuiClosing",              false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_RAIN_EFFECTS            = new MyConfigBooleanHotkeyed(PREFIX, "disableRainEffects",                   false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_SIGN_GUI                = new MyConfigBooleanHotkeyed(PREFIX, "disableSignGui",                       false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_SLIME_BLOCK_SLOWDOWN    = new MyConfigBooleanHotkeyed(PREFIX, "disableSlimeBlockSlowdown",            false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_TILE_ENTITY_RENDERING   = new MyConfigBooleanHotkeyed(PREFIX, "disableTileEntityRendering",           false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_TILE_ENTITY_TICKING     = new ConfigBooleanClient  (PREFIX, "disableTileEntityTicking",             false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_VILLAGER_TRADE_LOCKING  = new ConfigBooleanClient  (PREFIX, "disableVillagerTradeLocking",          false, "");
        public static final ConfigBooleanHotkeyed       DISABLE_WALL_UNSPRINT           = new MyConfigBooleanHotkeyed(PREFIX, "disableWallUnsprint",                  false, "");
        public static final ImmutableList<IHotkeyTogglable> OPTIONS = ImmutableList.of(
                DISABLE_BLOCK_BREAK_PARTICLES,
                DISABLE_DOUBLE_TAP_SPRINT,
                DISABLE_BOSS_FOG,
                DISABLE_CLIENT_ENTITY_UPDATES,
                DISABLE_DEAD_MOB_RENDERING,
                DISABLE_DEAD_MOB_TARGETING,
                DISABLE_ENTITY_RENDERING,
                DISABLE_ENTITY_TICKING,
                DISABLE_FALLING_BLOCK_RENDER,
                DISABLE_INVENTORY_EFFECTS,
                DISABLE_ITEM_SWITCH_COOLDOWN,
                DISABLE_MOB_SPAWNER_MOB_RENDER,
                DISABLE_NETHER_FOG,
                DISABLE_OBSERVER,
                DISABLE_OFFHAND_RENDERING,
                DISABLE_PARTICLES,
                DISABLE_PORTAL_GUI_CLOSING,
                DISABLE_RAIN_EFFECTS,
                DISABLE_SIGN_GUI,
                DISABLE_SLIME_BLOCK_SLOWDOWN,
                DISABLE_TILE_ENTITY_RENDERING,
                DISABLE_TILE_ENTITY_TICKING,
                DISABLE_VILLAGER_TRADE_LOCKING,
                DISABLE_WALL_UNSPRINT
        );
    }

    public static class Internal
    {
        public static final String PREFIX = String.format("%s.label.config_gui.internal", Reference.MOD_ID);
        public static final ConfigInteger       FLY_SPEED_PRESET                    = new MyConfigInteger     (PREFIX, "flySpeedPreset", 0, 0, 3);
        public static final ConfigDouble        GAMMA_VALUE_ORIGINAL                = new MyConfigDouble      (PREFIX, "gammaValueOriginal", 0, 0, 1);
        public static final ConfigInteger       HOTBAR_SCROLL_CURRENT_ROW           = new MyConfigInteger     (PREFIX, "hotbarScrollCurrentRow", 3, 0, 3);
        public static final ConfigDouble        SLIME_BLOCK_SLIPPERINESS_ORIGINAL   = new MyConfigDouble      (PREFIX, "slimeBlockSlipperinessOriginal", 0.8, 0, 1);
        public static final ConfigDouble        SNAP_AIM_LAST_PITCH                 = new MyConfigDouble      (PREFIX, "snapAimLastPitch", 0, -135, 135);
        public static final ConfigDouble        SNAP_AIM_LAST_YAW                   = new MyConfigDouble      (PREFIX, "snapAimLastYaw", 0, 0, 360);

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                FLY_SPEED_PRESET,
                GAMMA_VALUE_ORIGINAL,
                HOTBAR_SCROLL_CURRENT_ROW,
                SLIME_BLOCK_SLIPPERINESS_ORIGINAL,
                SNAP_AIM_LAST_YAW
        );
    }

    public static ConfigDouble getActiveFlySpeedConfig()
    {
        switch (Configs.Internal.FLY_SPEED_PRESET.getIntegerValue())
        {
            case 0:  return Configs.Generic.FLY_SPEED_PRESET_1;
            case 1:  return Configs.Generic.FLY_SPEED_PRESET_2;
            case 2:  return Configs.Generic.FLY_SPEED_PRESET_3;
            case 3:  return Configs.Generic.FLY_SPEED_PRESET_4;
            default: return Configs.Generic.FLY_SPEED_PRESET_1;
        }
    }

    public static void loadFromFile()
    {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead())
        {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject())
            {
                JsonObject root = element.getAsJsonObject();

                ConfigUtils.readConfigBase(root, "Fixes", Configs.Fixes.OPTIONS);
                ConfigUtils.readConfigBase(root, "Generic", Configs.Generic.OPTIONS);
                ConfigUtils.readConfigBase(root, "GenericHotkeys", Hotkeys.HOTKEY_LIST);
                ConfigUtils.readConfigBase(root, "Internal", Configs.Internal.OPTIONS);
                ConfigUtils.readConfigBase(root, "Lists", Configs.Lists.OPTIONS);
                ConfigUtils.readHotkeyToggleOptions(root, "DisableHotkeys", "DisableToggles", ImmutableList.copyOf(Disable.OPTIONS));
                ConfigUtils.readHotkeyToggleOptions(root, "TweakHotkeys", "TweakToggles", ImmutableList.copyOf(FeatureToggle.values()));
            }
        }

        InventoryUtils.setRepairModeSlots(Lists.REPAIR_MODE_SLOTS.getStrings());
        InventoryUtils.setUnstackingItems(Lists.UNSTACKING_ITEMS.getStrings());

        PlacementTweaks.FAST_RIGHT_CLICK_BLOCK_RESTRICTION.setListType((ListType) Lists.FAST_RIGHT_CLICK_BLOCK_LIST_TYPE.getOptionListValue());
        PlacementTweaks.FAST_RIGHT_CLICK_BLOCK_RESTRICTION.setListContents(
                Lists.FAST_RIGHT_CLICK_BLOCK_BLACKLIST.getStrings(),
                Lists.FAST_RIGHT_CLICK_BLOCK_WHITELIST.getStrings());

        PlacementTweaks.FAST_RIGHT_CLICK_ITEM_RESTRICTION.setListType((ListType) Lists.FAST_RIGHT_CLICK_ITEM_LIST_TYPE.getOptionListValue());
        PlacementTweaks.FAST_RIGHT_CLICK_ITEM_RESTRICTION.setListContents(
                Lists.FAST_RIGHT_CLICK_ITEM_BLACKLIST.getStrings(),
                Lists.FAST_RIGHT_CLICK_ITEM_WHITELIST.getStrings());

        PlacementTweaks.FAST_PLACEMENT_ITEM_RESTRICTION.setListType((ListType) Lists.FAST_PLACEMENT_ITEM_LIST_TYPE.getOptionListValue());
        PlacementTweaks.FAST_PLACEMENT_ITEM_RESTRICTION.setListContents(
                Lists.FAST_PLACEMENT_ITEM_BLACKLIST.getStrings(),
                Lists.FAST_PLACEMENT_ITEM_WHITELIST.getStrings());

        MiscTweaks.POTION_RESTRICTION.setListType((ListType) Lists.POTION_WARNING_LIST_TYPE.getOptionListValue());
        MiscTweaks.POTION_RESTRICTION.setListContents(
                Lists.POTION_WARNING_BLACKLIST.getStrings(),
                Lists.POTION_WARNING_WHITELIST.getStrings());
    }

    public static void saveToFile()
    {
        File dir = FileUtils.getConfigDirectory();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs())
        {
            JsonObject root = new JsonObject();

            ConfigUtils.writeConfigBase(root, "Fixes", Configs.Fixes.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Generic", Configs.Generic.OPTIONS);
            ConfigUtils.writeConfigBase(root, "GenericHotkeys", Hotkeys.HOTKEY_LIST);
            ConfigUtils.writeConfigBase(root, "Internal", Configs.Internal.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Lists", Configs.Lists.OPTIONS);
            ConfigUtils.writeHotkeyToggleOptions(root, "DisableHotkeys", "DisableToggles", ImmutableList.copyOf(Disable.OPTIONS));
            ConfigUtils.writeHotkeyToggleOptions(root, "TweakHotkeys", "TweakToggles", ImmutableList.copyOf(FeatureToggle.values()));

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    @Override
    public void load()
    {
        loadFromFile();
    }

    @Override
    public void save()
    {
        saveToFile();
    }
}
