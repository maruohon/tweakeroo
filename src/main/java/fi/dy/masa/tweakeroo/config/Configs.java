package fi.dy.masa.tweakeroo.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.config.options.ConfigStringList;
import fi.dy.masa.malilib.config.options.IConfigBase;
import fi.dy.masa.malilib.config.values.ActiveMode;
import fi.dy.masa.malilib.config.values.HudAlignment;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction.ListType;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.tweaks.MiscTweaks;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.PlacementRestrictionMode;
import fi.dy.masa.tweakeroo.util.SnapAimMode;

public class Configs implements IConfigHandler
{
    public static class Generic
    {
        public static final ConfigInteger       AFTER_CLICKER_CLICK_COUNT           = new ConfigInteger     ("afterClickerClickCount",  1, 1, 32, "The number of right clicks to do per placed block when\ntweakAfterClicker is enabled");
        public static final ConfigDouble        BLOCK_REACH_DISTANCE                = new ConfigDouble      ("blockReachDistance", 4.5, 0, 8, "The block reach distance to use if the\noverride tweak is enabled.\nThe maximum the server allows is 8 for placing, 6 for breaking.");
        public static final ConfigInteger       BLOCK_BREAKING_PARTICLE_LIMIT       = new ConfigInteger     ("blockBreakingParticleLimit", 8, 1, 1024, "This controls the maximum number of block breaking\nparticles produced per block broken, if 'tweakBlockBreakingParticleTweaks'\n is enabled.\nThe default in vanilla is 64 particles per block.");
        public static final ConfigDouble        BLOCK_BREAKING_PARTICLE_SCALE       = new ConfigDouble      ("blockBreakingParticleScale", 1.0, 0, 10D, "This is just an extra option for some fun looking block breaking particles.\nWorks if you have the 'tweakBlockBreakingParticleTweaks' feature enabled.");
        public static final ConfigDouble        BLOCK_BREAKING_PARTICLE_SPEED       = new ConfigDouble      ("blockBreakingParticleSpeedMultiplier", 1.0, 0, 20D, "This is just an extra option for some fun looking block breaking particles.\nWorks if you have the 'tweakBlockBreakingParticleTweaks' feature enabled.");
        public static final ConfigInteger       BREAKING_GRID_SIZE                  = new ConfigInteger     ("breakingGridSize", 3, 1, 1000, "The grid interval size for the grid breaking mode.\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind.");
        public static final ConfigColor         CHAT_BACKGROUND_COLOR               = new ConfigColor       ("chatBackgroundColor", "#80000000", "The background color for the chat messages,\nif 'tweakChatBackgroundColor' is enabled");
        public static final ConfigString        CHAT_TIME_FORMAT                    = new ConfigString      ("chatTimeFormat", "[HH:mm:ss]", "The time format for chat messages, if tweakChatTimestamp is enabled\nUses the Java SimpleDateFormat format specifiers.");
        public static final ConfigInteger       CHUNK_RENDER_TIMEOUT                = new ConfigInteger     ("chunkRenderTimeout", 50000000, 1, Integer.MAX_VALUE, "The timeout value per rendered frame for the chunk render tasks.\nThe value is in nanoseconds.");
        public static final ConfigBoolean       CLIENT_PLACEMENT_ROTATION           = new ConfigBoolean     ("clientPlacementRotation", true, "Enable single player and client side placement rotations,\nsuch as Accurate Placement working in single player without Carpet mod");
        public static final ConfigDouble        CLOUD_HEIGHT_OVERRIDE               = new ConfigDouble      ("cloudHeightOverride", 128, -1024, 1024, "The new cloud height, when tweakCloudHeightOverride is enabled");
        public static final ConfigDouble        DEBUG_PIE_CHART_SCALE               = new ConfigDouble      ("debugPieChartScale", 1, 0, 10, "The scaling factor for the debug profiler pie chart,\nif 'tweakDebugPieChartScale' is enabled.");
        public static final ConfigInteger       FAST_BLOCK_PLACEMENT_COUNT          = new ConfigInteger     ("fastBlockPlacementCount", 2, 1, 16, "The maximum number of blocks to place per game tick\nwith the Fast Block Placement tweak");
        public static final ConfigInteger       FAST_LEFT_CLICK_COUNT               = new ConfigInteger     ("fastLeftClickCount",  10, 1, 64, "The number of left clicks to do per game tick when\ntweakFastLeftClick is enabled and the attack button is held down");
        public static final ConfigInteger       FAST_RIGHT_CLICK_COUNT              = new ConfigInteger     ("fastRightClickCount", 10, 1, 64, "The number of right clicks to do per game tick when\ntweakFastRightClick is enabled and the use button is held down");
        public static final ConfigInteger       FILL_CLONE_LIMIT                    = new ConfigInteger     ("fillCloneLimit", 10000000, 1, 1000000000, "The new /fill and /clone block limit in single player,\nif the tweak to override them is enabled");
        public static final ConfigColor         FLEXIBLE_PLACEMENT_OVERLAY_COLOR    = new ConfigColor       ("flexibleBlockPlacementOverlayColor", "#C03030F0", "The color of the currently pointed-at\nregion in block placement the overlay");
        public static final ConfigDouble        FLY_SPEED_PRESET_1                  = new ConfigDouble      ("flySpeedPreset1", 0.01, 0, 4, "The fly speed for preset 1");
        public static final ConfigDouble        FLY_SPEED_PRESET_2                  = new ConfigDouble      ("flySpeedPreset2", 0.064, 0, 4, "The fly speed for preset 2");
        public static final ConfigDouble        FLY_SPEED_PRESET_3                  = new ConfigDouble      ("flySpeedPreset3", 0.128, 0, 4, "The fly speed for preset 3");
        public static final ConfigDouble        FLY_SPEED_PRESET_4                  = new ConfigDouble      ("flySpeedPreset4", 0.32, 0, 4, "The fly speed for preset 4");
        public static final ConfigBooleanHotkeyed FREE_CAMERA_PLAYER_MOVEMENT       = new ConfigBooleanHotkeyed("freeCameraPlayerMovement", false, "", "When enabled, the movement inputs in the Free Camera mode\nwill move the actual client player instead of the camera");
        public static final ConfigInteger       GAMMA_OVERRIDE_VALUE                = new ConfigInteger     ("gammaOverrideValue", 16, 0, 1000, "The gamma value to use when the override option is enabled");
        public static final ConfigBoolean       HAND_RESTOCK_CONTINUOUS             = new ConfigBoolean     ("handRestockContinuous", false, "If enabled, then hand restocking is attempted every tick,\nwhereas normally it only happens before or after left or\nright clicks or in the fast block placement mode.\nNormally I would recommend leaving this disabled, unless you\nknow you will need it for some specific things.");
        public static final ConfigBoolean       HAND_RESTOCK_PRE                    = new ConfigBoolean     ("handRestockPre", true, "If enabled, then hand restocking happens\nbefore the stack runs out");
        public static final ConfigInteger       HAND_RESTOCK_PRE_THRESHOLD          = new ConfigInteger     ("handRestockPreThreshold", 6, 1, 64, "In the pre-restock mode, when an item stack falls\nbelow this amount, the hand restock will happen");
        public static final ConfigBoolean       HANGABLE_ENTITY_BYPASS_INVERSE      = new ConfigBoolean     ("hangableEntityBypassInverse", false, "If the hangableEntityTargetingBypass tweak is enabled,\nthen this controls whether the player must be or must not be\nsneaking to be able to target the hangable entity (Item Frame or Painting).\n > true - Sneaking = ignore/bypass the entity\n > false - Sneaking = target the entity");
        public static final ConfigInteger       HOTBAR_SLOT_CYCLE_MAX               = new ConfigInteger     ("hotbarSlotCycleMax", 2, 1, 9, "This is the last hotbar slot to use/cycle through\nif the hotbar slot cycle tweak is enabled.\nBasically the cycle will jump back to the first slot\nwhen going over the maximum slot number set here.");
        public static final ConfigInteger       HOTBAR_SLOT_RANDOMIZER_MAX          = new ConfigInteger     ("hotbarSlotRandomizerMax", 5, 1, 9, "This is the last hotbar slot to use if the hotbar slot randomizer\ntweak is enabled. Basically the selected hotbar slot will be randomly\npicked from 1 to this maximum slot after an item use.");
        public static final ConfigInteger       HOTBAR_SWAP_OVERLAY_OFFSET_X        = new ConfigInteger     ("hotbarSwapOverlayOffsetX", 4, "The horizontal offset of the hotbar swap overlay");
        public static final ConfigInteger       HOTBAR_SWAP_OVERLAY_OFFSET_Y        = new ConfigInteger     ("hotbarSwapOverlayOffsetY", 4, "The vertical offset of the hotbar swap overlay");
        public static final ConfigInteger       ITEM_SWAP_DURABILITY_THRESHOLD      = new ConfigInteger     ("itemSwapDurabilityThreshold", 20, 0, 10000, "This is the durability threshold (in uses left)\nfor the low-durability item swap feature.\nNote that items with low total durability will go lower\nand be swapped at 5%% left.");
        public static final ConfigBoolean       LAVA_VISIBILITY_OPTIFINE            = new ConfigBoolean     ("lavaVisibilityOptifineCompat", true, "Use an alternative version of the Lava Visibility,\nwhich is Optifine compatible (but more hacky).\nImplementation credit to Nessie.");
        public static final ConfigInteger       MAP_PREVIEW_SIZE                    = new ConfigInteger     ("mapPreviewSize", 160, 16, 512, "The size of the rendered map previews");
        public static final ConfigInteger       PERIODIC_ATTACK_INTERVAL            = new ConfigInteger     ("periodicAttackInterval", 20, 0, Integer.MAX_VALUE, "The number of game ticks between automatic attacks (left clicks)");
        public static final ConfigInteger       PERIODIC_USE_INTERVAL               = new ConfigInteger     ("periodicUseInterval", 20, 0, Integer.MAX_VALUE, "The number of game ticks between automatic uses (right clicks)");
        public static final ConfigBoolean       PERMANENT_SNEAK_ALLOW_IN_GUIS       = new ConfigBoolean     ("permanentSneakAllowInGUIs", false, "If true, then the permanent sneak tweak will\nalso work while GUIs are open");
        public static final ConfigInteger       PLACEMENT_GRID_SIZE                 = new ConfigInteger     ("placementGridSize", 3, 1, 1000, "The grid interval size for the grid placement mode.\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind.");
        public static final ConfigInteger       PLACEMENT_LIMIT                     = new ConfigInteger     ("placementLimit", 3, 1, 10000, "The number of blocks you are able to place at maximum per\nright click, if tweakPlacementLimit is enabled.\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind.");
        public static final ConfigBoolean       PLACEMENT_RESTRICTION_TIED_TO_FAST  = new ConfigBoolean     ("placementRestrictionTiedToFast", true, "When enabled, the Placement Restriction mode will toggle\nits state of/off when you toggle the Fast Placement mode.");
        public static final ConfigDouble        PLAYER_ON_FIRE_SCALE                = new ConfigDouble      ("playerOnFireScale", 1, 0, 10, "A scaling factor for the player's on-fire effect.\nAccepted range: 0.0 - 10.0");
        public static final ConfigBoolean       POTION_WARNING_BENEFICIAL_ONLY      = new ConfigBoolean     ("potionWarningBeneficialOnly", true, "Only warn about potion effects running out that are marked as \"beneficial\"");
        public static final ConfigInteger       POTION_WARNING_THRESHOLD            = new ConfigInteger     ("potionWarningThreshold", 600, 1, 1000000, "The remaining duration of potion effects (in ticks)\nafter which the warning will start showing");
        public static final ConfigInteger       RENDER_LIMIT_ITEM                   = new ConfigInteger     ("renderLimitItem", -1, -1, 10000, "Maximum number of item entities rendered per frame.\nUse -1 for normal behaviour, ie. to disable this limit.");
        public static final ConfigInteger       RENDER_LIMIT_XP_ORB                 = new ConfigInteger     ("renderLimitXPOrb", -1, -1, 10000, "Maximum number of XP orb entities rendered per frame.\nUse -1 for normal behaviour, ie. to disable this limit.");
        public static final ConfigBoolean       SHULKER_DISPLAY_BACKGROUND_COLOR    = new ConfigBoolean     ("shulkerDisplayBgColor", true, "Enables tinting/coloring the Shulker Box display\nbackground texture with the dye color of the box");
        public static final ConfigBoolean       SHULKER_DISPLAY_REQUIRE_SHIFT       = new ConfigBoolean     ("shulkerDisplayRequireShift", true, "Whether or not holding shift is required for the Shulker Box preview");
        public static final ConfigBoolean       SLOT_SYNC_WORKAROUND                = new ConfigBoolean     ("slotSyncWorkaround", true, "This prevents the server from overriding the durability or\nstack size on items that are being used quickly for example\nwith the fast right click tweak.");
        public static final ConfigBoolean       SNAP_AIM_INDICATOR                  = new ConfigBoolean     ("snapAimIndicator", true, "Whether or not to render the snap aim angle indicator");
        public static final ConfigColor         SNAP_AIM_INDICATOR_COLOR            = new ConfigColor       ("snapAimIndicatorColor", "#603030FF", "The color for the snap aim indicator background");
        public static final ConfigBoolean       SNAP_AIM_ONLY_CLOSE_TO_ANGLE        = new ConfigBoolean     ("snapAimOnlyCloseToAngle", true, "If enabled, then the snap aim only snaps to the angle\nwhen the internal angle is within a certain distance of it.\nThe threshold can be set in snapAimThreshold");
        public static final ConfigBoolean       SNAP_AIM_PITCH_OVERSHOOT            = new ConfigBoolean     ("snapAimPitchOvershoot", false, "Whether or not to allow overshooting the pitch angle\nfrom the normal +/- 90 degrees up to +/- 180 degrees");
        public static final ConfigDouble        SNAP_AIM_PITCH_STEP                 = new ConfigDouble      ("snapAimPitchStep", 12.5, 0, 90, "The pitch angle step of the snap aim tweak");
        public static final ConfigDouble        SNAP_AIM_THRESHOLD_PITCH            = new ConfigDouble      ("snapAimThresholdPitch", 1.5, "The angle threshold inside which the player rotation will\nbe snapped to the snap angle.");
        public static final ConfigDouble        SNAP_AIM_THRESHOLD_YAW              = new ConfigDouble      ("snapAimThresholdYaw", 5.0, "The angle threshold inside which the player rotation will\nbe snapped to the snap angle.");
        public static final ConfigDouble        SNAP_AIM_YAW_STEP                   = new ConfigDouble      ("snapAimYawStep", 45, 0, 360, "The yaw angle step of the snap aim tweak");
        public static final ConfigBoolean       SOUND_NAME_OUTPUT                   = new ConfigBoolean     ("soundNameOutput", false, "This is meant for finding out the sound names you want to\ndisable with the 'disableSoundsList' tweak. It just prints any sound\nnames to the game console that start playing, while this is enabled.");
        public static final ConfigInteger       STRUCTURE_BLOCK_MAX_SIZE            = new ConfigInteger     ("structureBlockMaxSize", 128, 1, 256, "The maximum dimensions for a Structure Block's saved area");
        public static final ConfigDouble        ZOOM_FOV                            = new ConfigDouble      ("zoomFov", 30, 0, 600, "The FOV value used for the zoom feature");

        public static final ConfigOptionList<PlacementRestrictionMode>      BREAKING_RESTRICTION_MODE           = new ConfigOptionList<PlacementRestrictionMode>("breakingRestrictionMode", PlacementRestrictionMode.LINE, "The Breaking Restriction mode to use (hotkey-selectable)");
        public static final ConfigOptionList<ActiveMode>                    ELYTRA_CAMERA_INDICATOR             = new ConfigOptionList<ActiveMode>("elytraCameraIndicator", ActiveMode.WITH_KEY, "Whether or not to render the real pitch angle\nindicator when the elytra camera mode is active");
        public static final ConfigOptionList<HudAlignment>                  HOTBAR_SWAP_OVERLAY_ALIGNMENT       = new ConfigOptionList<HudAlignment>("hotbarSwapOverlayAlignment", HudAlignment.BOTTOM_RIGHT, "The positioning of the hotbar swap overlay");
        public static final ConfigOptionList<PlacementRestrictionMode>      PLACEMENT_RESTRICTION_MODE          = new ConfigOptionList<PlacementRestrictionMode>("placementRestrictionMode", PlacementRestrictionMode.FACE, "The Placement Restriction mode to use (hotkey-selectable)");
        public static final ConfigOptionList<SnapAimMode>                   SNAP_AIM_MODE                       = new ConfigOptionList<SnapAimMode>("snapAimMode", SnapAimMode.YAW, "Snap aim mode: yaw, or pitch, or both");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                CLIENT_PLACEMENT_ROTATION,
                FREE_CAMERA_PLAYER_MOVEMENT,
                LAVA_VISIBILITY_OPTIFINE,
                HAND_RESTOCK_CONTINUOUS,
                HAND_RESTOCK_PRE,
                HANGABLE_ENTITY_BYPASS_INVERSE,
                PERMANENT_SNEAK_ALLOW_IN_GUIS,
                PLACEMENT_RESTRICTION_TIED_TO_FAST,
                POTION_WARNING_BENEFICIAL_ONLY,
                SHULKER_DISPLAY_BACKGROUND_COLOR,
                SHULKER_DISPLAY_REQUIRE_SHIFT,
                SLOT_SYNC_WORKAROUND,
                SNAP_AIM_INDICATOR,
                SNAP_AIM_ONLY_CLOSE_TO_ANGLE,
                SNAP_AIM_PITCH_OVERSHOOT,
                SOUND_NAME_OUTPUT,

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
                BLOCK_BREAKING_PARTICLE_LIMIT,
                BLOCK_BREAKING_PARTICLE_SCALE,
                BLOCK_BREAKING_PARTICLE_SPEED,
                BLOCK_REACH_DISTANCE,
                BREAKING_GRID_SIZE,
                CHUNK_RENDER_TIMEOUT,
                CLOUD_HEIGHT_OVERRIDE,
                DEBUG_PIE_CHART_SCALE,
                FAST_BLOCK_PLACEMENT_COUNT,
                FAST_LEFT_CLICK_COUNT,
                FAST_RIGHT_CLICK_COUNT,
                FILL_CLONE_LIMIT,
                FLY_SPEED_PRESET_1,
                FLY_SPEED_PRESET_2,
                FLY_SPEED_PRESET_3,
                FLY_SPEED_PRESET_4,
                GAMMA_OVERRIDE_VALUE,
                HAND_RESTOCK_PRE_THRESHOLD,
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
                PLAYER_ON_FIRE_SCALE,
                POTION_WARNING_THRESHOLD,
                RENDER_LIMIT_ITEM,
                RENDER_LIMIT_XP_ORB,
                SNAP_AIM_PITCH_STEP,
                SNAP_AIM_THRESHOLD_PITCH,
                SNAP_AIM_THRESHOLD_YAW,
                SNAP_AIM_YAW_STEP,
                STRUCTURE_BLOCK_MAX_SIZE,
                ZOOM_FOV
        );
    }

    public static class Fixes
    {
        public static final ConfigBoolean ELYTRA_FIX                = new ConfigBoolean("elytraFix", false, "Elytra deployment/landing fix by Earthcomputer and Nessie");
        public static final ConfigBoolean TILE_UNLOAD_OPTIMIZATION  = new ConfigBoolean("tileEntityUnloadOptimization", false, "Optimizes the removal of unloading TileEntities from the World lists.\nThis can greatly improve performance if there are lots of\nTileEntities loaded and/or unloading at once.");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                ELYTRA_FIX,
                TILE_UNLOAD_OPTIMIZATION
        );
    }

    public static class Lists
    {
        public static final ConfigStringList FAST_PLACEMENT_ITEM_BLACKLIST      = new ConfigStringList("fastPlacementItemBlackList", ImmutableList.of("minecraft:ender_chest", "minecraft:white_shulker_box"), "The items that are NOT allowed to be used for the Fast Block Placement tweak,\nif the fastPlacementItemListType is set to Black List");
        public static final ConfigStringList FAST_PLACEMENT_ITEM_WHITELIST      = new ConfigStringList("fastPlacementItemWhiteList", ImmutableList.of(), "The items that are allowed to be used for the Fast Block Placement tweak,\nif the fastPLacementItemListType is set to White List");
        public static final ConfigStringList FAST_RIGHT_CLICK_BLOCK_BLACKLIST   = new ConfigStringList("fastRightClickBlockBlackList", ImmutableList.of("minecraft:chest", "minecraft:ender_chest", "minecraft:trapped_chest", "minecraft:white_shulker_box"), "The blocks that are NOT allowed to be right clicked on with\nthe Fast Right Click tweak, if the fastRightClickBlockListType is set to Black List");
        public static final ConfigStringList FAST_RIGHT_CLICK_BLOCK_WHITELIST   = new ConfigStringList("fastRightClickBlockWhiteList", ImmutableList.of(), "The blocks that are allowed to be right clicked on with\nthe Fast Right Click tweak, if the fastRightClickBlockListType is set to White List");
        public static final ConfigStringList FAST_RIGHT_CLICK_ITEM_BLACKLIST    = new ConfigStringList("fastRightClickItemBlackList", ImmutableList.of("minecraft:fireworks"), "The items that are NOT allowed to be used for the Fast Right Click tweak,\nif the fastRightClickListType is set to Black List");
        public static final ConfigStringList FAST_RIGHT_CLICK_ITEM_WHITELIST    = new ConfigStringList("fastRightClickItemWhiteList", ImmutableList.of("minecraft:bucket", "minecraft:water_bucket", "minecraft:lava_bucket", "minecraft:glass_bottle"), "The items that are allowed to be used for the Fast Right Click tweak,\nif the fastRightClickListType is set to White List");
        public static final ConfigStringList FLAT_WORLD_PRESETS                 = new ConfigStringList("flatWorldPresets", ImmutableList.of("White Glass;1*minecraft:stained_glass;minecraft:plains;;minecraft:stained_glass", "Glass;1*minecraft:glass;minecraft:plains;;minecraft:glass"), "Custom flat world preset strings.\nThese are in the format: name;blocks_string;biome;generation_features;icon_item\nThe blocks string format is the vanilla format, such as: 62*minecraft:dirt,minecraft:grass\nThe biome can be the registry name, or the int ID\nThe icon item name format can be either minecraft:iron_nugget or minecraft:stained_glass@6");
        public static final ConfigStringList ITEM_GLINT_BLACKLIST               = new ConfigStringList("itemGlintBlackList", ImmutableList.of("minecraft:potion"), "The items that will not have the glint effect,\nif itemGlintListType = blacklist");
        public static final ConfigStringList ITEM_GLINT_WHITELIST               = new ConfigStringList("itemGlintWhiteList", ImmutableList.of(), "The only items that will have the glint effect,\nif itemGlintListType = whitelist");
        public static final ConfigStringList POTION_WARNING_BLACKLIST           = new ConfigStringList("potionWarningBlackList", ImmutableList.of("minecraft:hunger", "minecraft:mining_fatigue", "minecraft:nausea", "minecraft:poison", "minecraft:slowness", "minecraft:weakness"), "The potion effects that will not be warned about");
        public static final ConfigStringList POTION_WARNING_WHITELIST           = new ConfigStringList("potionWarningWhiteList", ImmutableList.of("minecraft:fire_resistance", "minecraft:invisibility", "minecraft:water_breathing"), "The only potion effects that will be warned about");
        public static final ConfigStringList REPAIR_MODE_SLOTS                  = new ConfigStringList("repairModeSlots", ImmutableList.of("mainhand", "offhand"), "The slots the repair mode should use\nValid values: mainhand, offhand, head, chest, legs, feet");
        public static final ConfigStringList SOUND_DISABLE_BLACKLIST            = new ConfigStringList("soundDisableBlackList", ImmutableList.of(), "The sounds that will not play, if 'disableSoundsList' is enabled\nand 'soundDisableListType' is blacklist.\nSee https://pastebin.com/mLSYyLLM for a list of vanilla 1.12 sounds.");
        public static final ConfigStringList SOUND_DISABLE_WHITELIST            = new ConfigStringList("soundDisableWhiteList", ImmutableList.of(), "The only sounds that will play, if 'disableSoundsList' is enabled\nand 'soundDisableListType' is whitelist.\nSee https://pastebin.com/mLSYyLLM for a list of vanilla 1.12 sounds.");
        public static final ConfigStringList UNSTACKING_ITEMS                   = new ConfigStringList("unstackingItems", ImmutableList.of("minecraft:bucket", "minecraft:glass_bottle"), "The items that should be considered for the\n'tweakItemUnstackingProtection' tweak");

        public static final ConfigOptionList<ListType> FAST_PLACEMENT_ITEM_LIST_TYPE        = new ConfigOptionList<ListType>("fastPlacementItemListType", ListType.BLACKLIST, "The item restriction type for the Fast Block Placement tweak");
        public static final ConfigOptionList<ListType> FAST_RIGHT_CLICK_BLOCK_LIST_TYPE     = new ConfigOptionList<ListType>("fastRightClickBlockListType", ListType.BLACKLIST, "The targeted block restriction type for the Fast Right Click tweak");
        public static final ConfigOptionList<ListType> FAST_RIGHT_CLICK_ITEM_LIST_TYPE      = new ConfigOptionList<ListType>("fastRightClickItemListType", ListType.BLACKLIST, "The item restriction type for the Fast Right Click tweak");
        public static final ConfigOptionList<ListType> ITEM_GLINT_LIST_TYPE                 = new ConfigOptionList<ListType>("itemGlintListType", ListType.BLACKLIST, "The item restriction type for the Disable Item Glint feature");
        public static final ConfigOptionList<ListType> POTION_WARNING_LIST_TYPE             = new ConfigOptionList<ListType>("potionWarningListType", ListType.NONE, "The list type for potion warning effects");
        public static final ConfigOptionList<ListType> SOUND_DISABLE_LIST_TYPE              = new ConfigOptionList<ListType>("soundDisableListType", ListType.NONE, "The list type for the 'disableSoundsList' tweak");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
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

    public static class Disable
    {
        public static final ConfigBooleanHotkeyed       DISABLE_BLOCK_BREAK_PARTICLES   = new ConfigBooleanHotkeyed("disableBlockBreakingParticles",        false, "", "Removes the block breaking particles.\n(This is originally from usefulmod by nessie.)\n§6Note: There is a separate tweak 'tweakReducedBlockBreakingParticles' in Tweak Toggles,\n§6if you just want to reduce the amount of particles produced,\n§6but not disable them completely.");
        public static final ConfigBooleanHotkeyed       DISABLE_DOUBLE_TAP_SPRINT       = new ConfigBooleanHotkeyed("disableDoubleTapSprint",               false, "", "Disables the double-tap-forward-key sprinting");
        public static final ConfigBooleanHotkeyed       DISABLE_BOSS_FOG                = new ConfigBooleanHotkeyed("disableBossFog",                       false, "", "Removes the fog that boss mobs cause");
        public static final ConfigBooleanHotkeyed       DISABLE_CHRISTMAS_CHESTS        = new ConfigBooleanHotkeyed("disableChristmasChests",               false, "", "Disables the Christmas chest textures");
        public static final ConfigBooleanHotkeyed       DISABLE_CLIENT_ENTITY_UPDATES   = new ConfigBooleanHotkeyed("disableClientEntityUpdates",           false, "", "Disables ALL except player entity updates on the client.\nThis is mainly meant for situations where you need to be\nable to do stuff to fix excessive entity count related problems");
        public static final ConfigBooleanHotkeyed       DISABLE_DEAD_MOB_RENDERING      = new ConfigBooleanHotkeyed("disableDeadMobRendering",              false, "", "Prevents rendering dead mobs (entities that are at 0 health)");
        public static final ConfigBooleanHotkeyed       DISABLE_DEAD_MOB_TARGETING      = new ConfigBooleanHotkeyed("disableDeadMobTargeting",              false, "", "Prevents targeting entities that are at 0 health.\nThis fixes for example hitting already dead mobs.");
        public static final ConfigBooleanHotkeyed       DISABLE_ENTITY_RENDERING        = new ConfigBooleanHotkeyed("disableEntityRendering",               false, "", "Disables ALL except player entity rendering.\nThis is mainly meant for situations where you need to be\nable to do stuff to fix excessive entity count related problems");
        public static final ConfigBooleanHotkeyed       DISABLE_ENTITY_TICKING          = new ConfigBooleanClient  ("disableEntityTicking",                 false, "", "Prevent everything except player entities from getting ticked");
        public static final ConfigBooleanHotkeyed       DISABLE_FALLING_BLOCK_RENDER    = new ConfigBooleanHotkeyed("disableFallingBlockEntityRendering",   false, "", "If enabled, then falling block entities won't be rendered at all");
        public static final ConfigBooleanHotkeyed       DISABLE_INVENTORY_EFFECTS       = new ConfigBooleanHotkeyed("disableInventoryEffectRendering",      false, "", "Removes the potion effect rendering from the inventory GUIs");
        public static final ConfigBooleanHotkeyed       DISABLE_ITEM_GLINT              = new ConfigBooleanHotkeyed("disableItemGlint",                     false, "", "Disables the glint effect from the items.\nThe items to remove it from, or allow it on, can be configured\nin Lists -> itemGlint*");
        public static final ConfigBooleanHotkeyed       DISABLE_ITEM_SWITCH_COOLDOWN    = new ConfigBooleanHotkeyed("disableItemSwitchRenderCooldown",      false, "", "If true, then there won't be any cooldown/equip animation\nwhen switching the held item or using the item.");
        public static final ConfigBooleanHotkeyed       DISABLE_LIGHT_UPDATES           = new ConfigBooleanHotkeyed("disableLightUpdates",                  false, "", "If enabled, disables some client-side (rendering related) light updates");
        public static final ConfigBooleanHotkeyed       DISABLE_LIGHT_UPDATES_ALL       = new ConfigBooleanHotkeyed("disableLightUpdatesAll",               false, "", "If enabled, disables ALL client-side light updates.\nThis might look very bad unless you use the Gamma tweak.");
        public static final ConfigBooleanHotkeyed       DISABLE_MOB_SPAWNER_MOB_RENDER  = new ConfigBooleanHotkeyed("disableMobSpawnerMobRendering",        false, "", "Removes the entity rendering from mob spawners");
        public static final ConfigBooleanHotkeyed       DISABLE_NETHER_FOG              = new ConfigBooleanHotkeyed("disableNetherFog",                     false, "", "Removes the fog in the Nether");
        public static final ConfigBooleanHotkeyed       DISABLE_OBSERVER                = new ConfigBooleanClient  ("disableObserver",                      false, "", "Disable Observers from triggering at all");
        public static final ConfigBooleanHotkeyed       DISABLE_OBSERVER_PLACE_UPDATE   = new ConfigBooleanClient  ("disableObserverPlaceUpdate",           false, "", "Prevent Observers from triggering when placed");
        public static final ConfigBooleanHotkeyed       DISABLE_OFFHAND_RENDERING       = new ConfigBooleanHotkeyed("disableOffhandRendering",              false, "", "Disables the offhand item from getting rendered");
        public static final ConfigBooleanHotkeyed       DISABLE_PARTICLES               = new ConfigBooleanHotkeyed("disableParticles",                     false, "", "Disables all particles");
        public static final ConfigBooleanHotkeyed       DISABLE_PORTAL_GUI_CLOSING      = new ConfigBooleanHotkeyed("disablePortalGuiClosing",              false, "", "If enabled, then you can still open GUIs while in a Nether Portal");
        public static final ConfigBooleanHotkeyed       DISABLE_RAIN_EFFECTS            = new ConfigBooleanHotkeyed("disableRainEffects",                   false, "", "Disables rain rendering and sounds");
        public static final ConfigBooleanHotkeyed       DISABLE_RENDER_DISTANCE_FOG     = new ConfigBooleanHotkeyed("disableRenderDistanceFog",             false, "", "Disables the fog that increases around the render distance");
        public static final ConfigBooleanHotkeyed       DISABLE_SCOREBOARD_RENDERING    = new ConfigBooleanHotkeyed("disableScoreboardRendering",           false, "", "Removes the sidebar scoreboard rendering");
        public static final ConfigBooleanHotkeyed       DISABLE_SIGN_GUI                = new ConfigBooleanHotkeyed("disableSignGui",                       false, "", "Prevent the Sign edit GUI from opening");
        public static final ConfigBooleanHotkeyed       DISABLE_SHULKER_BOX_TOOLTIP     = new ConfigBooleanHotkeyed("disableShulkerBoxTooltip",             false, "", "Removes the vanilla Shulker Box content tooltip lines");
        public static final ConfigBooleanHotkeyed       DISABLE_SLIME_BLOCK_SLOWDOWN    = new ConfigBooleanHotkeyed("disableSlimeBlockSlowdown",            false, "", "Removes the slowdown from walking on Slime Blocks.\n(This is originally from usefulmod by nessie.)");
        public static final ConfigBooleanHotkeyed       DISABLE_SOUNDS_ALL              = new ConfigBooleanHotkeyed("disableSoundsAll",                     false, "", "Prevent ALL sounds from playing");
        public static final ConfigBooleanHotkeyed       DISABLE_SOUNDS_LIST             = new ConfigBooleanHotkeyed("disableSoundsList",                    false, "", "Prevent playing the sounds controlled by the black- or whitelists in the Lists category");
        public static final ConfigBooleanHotkeyed       DISABLE_TILE_ENTITY_RENDERING   = new ConfigBooleanHotkeyed("disableTileEntityRendering",           false, "", "Prevents all TileEntity renderers from rendering");
        public static final ConfigBooleanHotkeyed       DISABLE_TILE_ENTITY_TICKING     = new ConfigBooleanClient  ("disableTileEntityTicking",             false, "", "Prevent all TileEntities from getting ticked");
        public static final ConfigBooleanHotkeyed       DISABLE_VILLAGER_TRADE_LOCKING  = new ConfigBooleanClient  ("disableVillagerTradeLocking",          false, "", "Prevents villager trades from ever locking, by always incrementing\nthe max uses as well when the recipe uses is incremented");
        public static final ConfigBooleanHotkeyed       DISABLE_WALL_UNSPRINT           = new ConfigBooleanHotkeyed("disableWallUnsprint",                  false, "", "Touching a wall doesn't drop you out from sprint mode");

        public static final ImmutableList<ConfigBooleanHotkeyed> OPTIONS = ImmutableList.of(
                DISABLE_BLOCK_BREAK_PARTICLES,
                DISABLE_DOUBLE_TAP_SPRINT,
                DISABLE_BOSS_FOG,
                DISABLE_CHRISTMAS_CHESTS,
                DISABLE_CLIENT_ENTITY_UPDATES,
                DISABLE_DEAD_MOB_RENDERING,
                DISABLE_DEAD_MOB_TARGETING,
                DISABLE_ENTITY_RENDERING,
                DISABLE_ENTITY_TICKING,
                DISABLE_FALLING_BLOCK_RENDER,
                DISABLE_INVENTORY_EFFECTS,
                DISABLE_ITEM_GLINT,
                DISABLE_ITEM_SWITCH_COOLDOWN,
                DISABLE_LIGHT_UPDATES,
                DISABLE_LIGHT_UPDATES_ALL,
                DISABLE_MOB_SPAWNER_MOB_RENDER,
                DISABLE_NETHER_FOG,
                DISABLE_OBSERVER,
                DISABLE_OBSERVER_PLACE_UPDATE,
                DISABLE_OFFHAND_RENDERING,
                DISABLE_PARTICLES,
                DISABLE_PORTAL_GUI_CLOSING,
                DISABLE_RAIN_EFFECTS,
                DISABLE_RENDER_DISTANCE_FOG,
                DISABLE_SCOREBOARD_RENDERING,
                DISABLE_SIGN_GUI,
                DISABLE_SHULKER_BOX_TOOLTIP,
                DISABLE_SLIME_BLOCK_SLOWDOWN,
                DISABLE_SOUNDS_ALL,
                DISABLE_SOUNDS_LIST,
                DISABLE_TILE_ENTITY_RENDERING,
                DISABLE_TILE_ENTITY_TICKING,
                DISABLE_VILLAGER_TRADE_LOCKING,
                DISABLE_WALL_UNSPRINT
        );
    }

    public static class Internal
    {
        public static final ConfigInteger       FLY_SPEED_PRESET                    = new ConfigInteger     ("flySpeedPreset", 0, 0, 3, "This is just for the mod internally to track the\ncurrently selected fly speed preset");
        public static final ConfigDouble        GAMMA_VALUE_ORIGINAL                = new ConfigDouble      ("gammaValueOriginal", 0, 0, 1, "The original gamma value, before the gamma override was enabled");
        public static final ConfigInteger       HOTBAR_SCROLL_CURRENT_ROW           = new ConfigInteger     ("hotbarScrollCurrentRow", 3, 0, 3, "This is just for the mod internally to track the\n\"current hotbar row\" for the hotbar scrolling feature");
        public static final ConfigDouble        SLIME_BLOCK_SLIPPERINESS_ORIGINAL   = new ConfigDouble      ("slimeBlockSlipperinessOriginal", 0.8, 0, 1, "The original slipperiness value of Slime Blocks");
        public static final ConfigDouble        SNAP_AIM_LAST_PITCH                 = new ConfigDouble      ("snapAimLastPitch", 0, -135, 135, "The last snapped-to pitch value");
        public static final ConfigDouble        SNAP_AIM_LAST_YAW                   = new ConfigDouble      ("snapAimLastYaw", 0, 0, 360, "The last snapped-to yaw value");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                FLY_SPEED_PRESET,
                GAMMA_VALUE_ORIGINAL,
                HOTBAR_SCROLL_CURRENT_ROW,
                SLIME_BLOCK_SLIPPERINESS_ORIGINAL,
                SNAP_AIM_LAST_YAW
        );
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
    public Map<String, List<? extends IConfigBase>> getConfigsPerCategories()
    {
        Map<String, List<? extends IConfigBase>> map = new LinkedHashMap<>();

        map.put("Generic",          Generic.OPTIONS);
        map.put("Fixes",            Fixes.OPTIONS);
        map.put("Lists",            Lists.OPTIONS);
        map.put("TweakToggles",     ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(FeatureToggle.values())));
        map.put("TweakHotkeys",     ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, ImmutableList.copyOf(FeatureToggle.values())));
        map.put("GenericHotkeys",   Hotkeys.HOTKEY_LIST);
        map.put("DisableToggles",   ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, Disable.OPTIONS));
        map.put("DisableHotkeys",   ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, Disable.OPTIONS));
        map.put("Internal",         Internal.OPTIONS);

        return map;
    }

    @Override
    public boolean shouldShowCategoryOnConfigGuis(String category)
    {
        return category.equals("Internal") == false;
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

    public static ConfigDouble getActiveFlySpeedConfig()
    {
        return getFlySpeedConfig(Configs.Internal.FLY_SPEED_PRESET.getIntegerValue());
    }

    public static ConfigDouble getFlySpeedConfig(int preset)
    {
        switch (preset)
        {
            case 0:  return Configs.Generic.FLY_SPEED_PRESET_1;
            case 1:  return Configs.Generic.FLY_SPEED_PRESET_2;
            case 2:  return Configs.Generic.FLY_SPEED_PRESET_3;
            case 3:  return Configs.Generic.FLY_SPEED_PRESET_4;
            default: return Configs.Generic.FLY_SPEED_PRESET_1;
        }
    }

    public static ConfigHotkey getFlySpeedHotkey(int preset)
    {
        switch (preset)
        {
            case 0:  return Hotkeys.FLY_PRESET_1;
            case 1:  return Hotkeys.FLY_PRESET_2;
            case 2:  return Hotkeys.FLY_PRESET_3;
            case 3:  return Hotkeys.FLY_PRESET_4;
            default: return Hotkeys.FLY_PRESET_1;
        }
    }
}
