package fi.dy.masa.tweakeroo.config;

import java.io.File;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
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
import fi.dy.masa.malilib.util.MessageOutputType;
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
        public static final ConfigInteger       AFTER_CLICKER_CLICK_COUNT           = new ConfigInteger     ("afterClickerClickCount",  1, 1, 32, "The number of right clicks to do per placed block when\ntweakAfterClicker is enabled");
        public static final ConfigDouble        ANGEL_BLOCK_PLACEMENT_DISTANCE      = new ConfigDouble      ("angelBlockPlacementDistance",  3, 1, 5, "The distance from the player blocks can be placed in\nthe air when tweakAngelBlock is enabled.\n5 is the maximum the server allows.");
        public static final ConfigDouble        BLOCK_REACH_DISTANCE                = new ConfigDouble      ("blockReachDistance", 4.5, 1, 64, "The block reach distance to use if the override tweak is enabled.\nThe maximum the game allows is 64.\n§6Do not attempt to use this at a value beyond\n[0.5 - 1.0] higher than the Game Rules defined on a server.");
        public static final ConfigOptionList    BLOCK_TYPE_BREAK_RESTRICTION_WARN   = new ConfigOptionList  ("blockTypeBreakRestrictionWarn", MessageOutputType.MESSAGE, "Selects which type of warning message to show (if any)\nwhen the Block Type Break Restriction feature prevents breaking a block");
        public static final ConfigInteger       BREAKING_GRID_SIZE                  = new ConfigInteger     ("breakingGridSize", 3, 1, 1000, "The grid interval size for the grid breaking mode.\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind.");
        public static final ConfigOptionList    BREAKING_RESTRICTION_MODE           = new ConfigOptionList  ("breakingRestrictionMode", PlacementRestrictionMode.LINE, "The Breaking Restriction mode to use (hotkey-selectable)");
        public static final ConfigColor         CHAT_BACKGROUND_COLOR               = new ConfigColor       ("chatBackgroundColor", "#80000000", "The background color for the chat messages,\nif 'tweakChatBackgroundColor' is enabled");
        public static final ConfigString        CHAT_TIME_FORMAT                    = new ConfigString      ("chatTimeFormat", "[HH:mm:ss]", "The time format for chat messages, if tweakChatTimestamp is enabled\nUses the Java SimpleDateFormat format specifiers.");
        public static final ConfigBoolean       CARPET_ACCURATE_PLACEMENT_PROTOCOL  = new ConfigBoolean     ("carpetAccuratePlacementProtocol", true, "If enabled, then the Flexible Block Placement and the\nAccurate Block Placement use the protocol implemented in Carpet mod.\n§6Note: This is required for any block rotations to work, other than\n§6blocks that only care about the block side you click on (Hoppers, Logs etc.)");
        public static final ConfigBoolean       CLIENT_PLACEMENT_ROTATION           = new ConfigBoolean     ("clientPlacementRotation", true, "Enable single player and client side placement rotations,\nsuch as Accurate Placement working in single player without Carpet mod");
        public static final ConfigInteger       CUSTOM_INVENTORY_GUI_SCALE          = new ConfigInteger     ("customInventoryGuiScale", 2, 1, 10, "The GUI scale value to use for inventory screens, if\n§etweakCustomInventoryScreenScale§r is enabled.");
        //public static final ConfigBoolean       DEBUG_LOGGING                       = new ConfigBoolean     ("debugLogging", false, "Enables some debug log messages in the game console,\nfor debugging certain issues or crashes.");
        public static final ConfigOptionList    ELYTRA_CAMERA_INDICATOR             = new ConfigOptionList  ("elytraCameraIndicator", ActiveMode.WITH_KEY, "Whether or not to render the real pitch angle\nindicator when the elytra camera mode is active");
        public static final ConfigDouble        ENTITY_REACH_DISTANCE               = new ConfigDouble      ("entityReachDistance", 3.0, 1, 64, "The entity reach distance to use if the override tweak is enabled.\nThe maximum the game allows is 64.\n§6Do not attempt to use this at a value beyond\n[0.5 - 1.0] higher than the Game Rules defined on a server.");
        public static final ConfigOptionList    ENTITY_TYPE_ATTACK_RESTRICTION_WARN = new ConfigOptionList  ("entityTypeAttackRestrictionWarn", MessageOutputType.MESSAGE, "Selects which type of warning message to show (if any)\nwhen the Entity Type Attack Restriction feature prevents attacking an entity");
        public static final ConfigInteger       FAST_BLOCK_PLACEMENT_COUNT          = new ConfigInteger     ("fastBlockPlacementCount", 2, 1, 16, "The maximum number of blocks to place per game tick\nwith the Fast Block Placement tweak");
        public static final ConfigBoolean       FAST_LEFT_CLICK_ALLOW_TOOLS         = new ConfigBoolean     ("fastLeftClickAllowTools", false, "Allow the Fast Left Click to work in survival\nalso while holding tool items");
        public static final ConfigInteger       FAST_LEFT_CLICK_COUNT               = new ConfigInteger     ("fastLeftClickCount",  10, 1, 64, "The number of left clicks to do per game tick when\ntweakFastLeftClick is enabled and the attack button is held down");
        public static final ConfigBoolean       FAST_PLACEMENT_REMEMBER_ALWAYS      = new ConfigBoolean     ("fastPlacementRememberOrientation", true, "If enabled, then the Fast Block Placement feature will always remember\nthe orientation of the first block you place.\nWithout this, the orientation will only be remembered\nwith the Flexible Block Placement enabled and active.");
        public static final ConfigInteger       FAST_RIGHT_CLICK_COUNT              = new ConfigInteger     ("fastRightClickCount", 10, 1, 64, "The number of right clicks to do per game tick when\ntweakFastRightClick is enabled and the use button is held down");
        public static final ConfigInteger       FILL_CLONE_LIMIT                    = new ConfigInteger     ("fillCloneLimit", 10000000, 1, 1000000000, "The new /fill and /clone block limit in single player,\nif the tweak to override them is enabled");
        public static final ConfigColor         FLEXIBLE_PLACEMENT_OVERLAY_COLOR    = new ConfigColor       ("flexibleBlockPlacementOverlayColor", "#C03030F0", "The color of the currently pointed-at\nregion in block placement the overlay");
        public static final ConfigDouble        FLY_DECELERATION_FACTOR             = new ConfigDouble      ("flyDecelerationFactor", 0.4, 0.0, 1.0, "This adjusts how quickly the player will stop if the\n'customFlyDeceleration' tweak is enabled");
        public static final ConfigDouble        FLY_SPEED_PRESET_1                  = new ConfigDouble      ("flySpeedPreset1", 0.01, 0, 4, "The fly speed for preset 1");
        public static final ConfigDouble        FLY_SPEED_PRESET_2                  = new ConfigDouble      ("flySpeedPreset2", 0.064, 0, 4, "The fly speed for preset 2");
        public static final ConfigDouble        FLY_SPEED_PRESET_3                  = new ConfigDouble      ("flySpeedPreset3", 0.128, 0, 4, "The fly speed for preset 3");
        public static final ConfigDouble        FLY_SPEED_PRESET_4                  = new ConfigDouble      ("flySpeedPreset4", 0.32, 0, 4, "The fly speed for preset 4");
        public static final ConfigBoolean       FREE_CAMERA_PLAYER_INPUTS           = new ConfigBoolean     ("freeCameraPlayerInputs", false, "When enabled, the attacks and use actions\n(ie. left and right clicks) in Free Camera mode are\nlet through to the actual player.");
        public static final ConfigBoolean       FREE_CAMERA_PLAYER_MOVEMENT         = new ConfigBoolean     ("freeCameraPlayerMovement", false, "When enabled, the movement inputs in the Free Camera mode\nwill move the actual client player instead of the camera");
        public static final ConfigDouble        GAMMA_OVERRIDE_VALUE                = new ConfigDouble      ("gammaOverrideValue", 16, 0, 32, "The gamma value to use when the override option is enabled");
        public static final ConfigBoolean       HAND_RESTOCK_PRE                    = new ConfigBoolean     ("handRestockPre", true, "If enabled, then hand restocking happens\nbefore the stack runs out");
        public static final ConfigInteger       HAND_RESTOCK_PRE_THRESHOLD          = new ConfigInteger     ("handRestockPreThreshold", 6, 1, 64, "The stack size threshold at which Hand Restock will happen\nin the pre-restock mode");
        public static final ConfigBoolean       HANGABLE_ENTITY_BYPASS_INVERSE      = new ConfigBoolean     ("hangableEntityBypassInverse", false, "If the hangableEntityTargetingBypass tweak is enabled,\nthen this controls whether the player must be or must not be\nsneaking to be able to target the hangable entity (Item Frame or Painting).\n > true - Sneaking = ignore/bypass the entity\n > false - Sneaking = target the entity");
        public static final ConfigInteger       HOTBAR_SLOT_CYCLE_MAX               = new ConfigInteger     ("hotbarSlotCycleMax", 2, 1, 9, "This is the last hotbar slot to use/cycle through\nif the hotbar slot cycle tweak is enabled.\nBasically the cycle will jump back to the first slot\nwhen going over the maximum slot number set here.");
        public static final ConfigInteger       HOTBAR_SLOT_RANDOMIZER_MAX          = new ConfigInteger     ("hotbarSlotRandomizerMax", 5, 1, 9, "This is the last hotbar slot to use if the hotbar slot randomizer\ntweak is enabled. Basically the selected hotbar slot will be randomly\npicked from 1 to this maximum slot after an item use.");
        public static final ConfigOptionList    HOTBAR_SWAP_OVERLAY_ALIGNMENT       = new ConfigOptionList  ("hotbarSwapOverlayAlignment", HudAlignment.BOTTOM_RIGHT, "The positioning of the hotbar swap overlay");
        public static final ConfigInteger       HOTBAR_SWAP_OVERLAY_OFFSET_X        = new ConfigInteger     ("hotbarSwapOverlayOffsetX", 4, "The horizontal offset of the hotbar swap overlay");
        public static final ConfigInteger       HOTBAR_SWAP_OVERLAY_OFFSET_Y        = new ConfigInteger     ("hotbarSwapOverlayOffsetY", 4, "The vertical offset of the hotbar swap overlay");
        public static final ConfigInteger       ITEM_SWAP_DURABILITY_THRESHOLD      = new ConfigInteger     ("itemSwapDurabilityThreshold", 20, 5, 10000, "This is the durability threshold (in uses left)\nfor the low-durability item swap feature.\nNote that items with low total durability will go lower\nand be swapped at 5%% left.");
        public static final ConfigBoolean       ITEM_USE_PACKET_CHECK_BYPASS        = new ConfigBoolean     ("itemUsePacketCheckBypass", true, "Bypass the new distance/coordinate check that was added in 1.18.2.\n\nThat check breaks the \"accurate placement protocol\" and causes\nany blocks placed with a rotation (or other property) request to just become ghost blocks.\n\nThere is basically no need to ever disable this.\nThe check didn't even exist ever before 1.18.2.");
        public static final ConfigBoolean       MAP_PREVIEW_REQUIRE_SHIFT           = new ConfigBoolean     ("mapPreviewRequireShift", true, "Whether holding shift is required for the Map Preview");
        public static final ConfigInteger       MAP_PREVIEW_SIZE                    = new ConfigInteger     ("mapPreviewSize", 160, 16, 512, "The size of the rendered map previews");
        public static final ConfigInteger       PERIODIC_ATTACK_INTERVAL            = new ConfigInteger     ("periodicAttackInterval", 20, 0, Integer.MAX_VALUE, "The number of game ticks between automatic attacks (left clicks)");
        public static final ConfigInteger       PERIODIC_USE_INTERVAL               = new ConfigInteger     ("periodicUseInterval", 20, 0, Integer.MAX_VALUE, "The number of game ticks between automatic uses (right clicks)");
        public static final ConfigInteger       PERIODIC_HOLD_ATTACK_DURATION       = new ConfigInteger     ("periodicHoldAttackDuration", 20, 0, Integer.MAX_VALUE, "The number of game ticks to hold down attack");
        public static final ConfigInteger       PERIODIC_HOLD_ATTACK_INTERVAL       = new ConfigInteger     ("periodicHoldAttackInterval", 20, 0, Integer.MAX_VALUE, "The number of game ticks between starting to hold down attack (left click)");
        public static final ConfigInteger       PERIODIC_HOLD_USE_DURATION          = new ConfigInteger     ("periodicHoldUseDuration", 20, 0, Integer.MAX_VALUE, "The number of game ticks to hold down use");
        public static final ConfigInteger       PERIODIC_HOLD_USE_INTERVAL          = new ConfigInteger     ("periodicHoldUseInterval", 20, 0, Integer.MAX_VALUE, "The number of game ticks between starting to hold down use (right click)");
        public static final ConfigBoolean       PERMANENT_SNEAK_ALLOW_IN_GUIS       = new ConfigBoolean     ("permanentSneakAllowInGUIs", false, "If true, then the permanent sneak tweak will\nalso work while GUIs are open");
        public static final ConfigInteger       PLACEMENT_GRID_SIZE                 = new ConfigInteger     ("placementGridSize", 3, 1, 1000, "The grid interval size for the grid placement mode.\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind.");
        public static final ConfigInteger       PLACEMENT_LIMIT                     = new ConfigInteger     ("placementLimit", 3, 1, 10000, "The number of blocks you are able to place at maximum per\nright click, if tweakPlacementLimit is enabled.\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind.");
        public static final ConfigOptionList    PLACEMENT_RESTRICTION_MODE          = new ConfigOptionList  ("placementRestrictionMode", PlacementRestrictionMode.FACE, "The Placement Restriction mode to use (hotkey-selectable)");
        public static final ConfigBoolean       PLACEMENT_RESTRICTION_TIED_TO_FAST  = new ConfigBoolean     ("placementRestrictionTiedToFast", true, "When enabled, the Placement Restriction mode will toggle\nits state of/off when you toggle the Fast Placement mode.");
        public static final ConfigBoolean       POTION_WARNING_BENEFICIAL_ONLY      = new ConfigBoolean     ("potionWarningBeneficialOnly", true, "Only warn about potion effects running out that are marked as \"beneficial\"");
        public static final ConfigInteger       POTION_WARNING_THRESHOLD            = new ConfigInteger     ("potionWarningThreshold", 600, 1, 1000000, "The remaining duration of potion effects (in ticks)\nafter which the warning will start showing");
        public static final ConfigBoolean       REMEMBER_FLEXIBLE                   = new ConfigBoolean     ("rememberFlexibleFromClick", true, "If enabled, then the Flexible Block Placement status\nwill be remembered from the first placed block,\nas long as the use key (right click) is held down.\nBasically you don't have to keep holding all the flexible\nactivation keys to fast place all the blocks in the same orientation.");
        public static final ConfigInteger       RENDER_LIMIT_ITEM                   = new ConfigInteger     ("renderLimitItem", -1, -1, 10000, "Maximum number of item entities rendered per frame.\nUse -1 for normal behaviour, ie. to disable this limit.");
        public static final ConfigInteger       RENDER_LIMIT_XP_ORB                 = new ConfigInteger     ("renderLimitXPOrb", -1, -1, 10000, "Maximum number of XP orb entities rendered per frame.\nUse -1 for normal behaviour, ie. to disable this limit.");
        public static final ConfigInteger       SCULK_SENSOR_PULSE_LENGTH           = new ConfigInteger     ("sculkSensorPulseLength", 40, 0, 10000, "The pulse length for Sculk Sensors, if the 'tweakSculkPulseLength' tweak is enabled.");
        public static final ConfigBoolean       SHULKER_DISPLAY_BACKGROUND_COLOR    = new ConfigBoolean     ("shulkerDisplayBgColor", true, "Enables tinting/coloring the Shulker Box display\nbackground texture with the dye color of the box");
        public static final ConfigBoolean       SHULKER_DISPLAY_REQUIRE_SHIFT       = new ConfigBoolean     ("shulkerDisplayRequireShift", true, "Whether or not holding shift is required for the Shulker Box preview");
        public static final ConfigBoolean       SLOT_SYNC_WORKAROUND                = new ConfigBoolean     ("slotSyncWorkaround", true, "This prevents the server from overriding the durability or\nstack size on items that are being used quickly for example\nwith the fast right click tweak.");
        public static final ConfigBoolean       SLOT_SYNC_WORKAROUND_ALWAYS         = new ConfigBoolean     ("slotSyncWorkaroundAlways", false, "Enables the slot sync workaround at all times when the use key\nis held, not only when using fast right click or fast block placement.\nThis is mainly for other mods that may quickly use items when\nholding down use, such as Litematica's Easy Place mode.");
        public static final ConfigBoolean       SNAP_AIM_INDICATOR                  = new ConfigBoolean     ("snapAimIndicator", true, "Whether or not to render the snap aim angle indicator");
        public static final ConfigColor         SNAP_AIM_INDICATOR_COLOR            = new ConfigColor       ("snapAimIndicatorColor", "#603030FF", "The color for the snap aim indicator background");
        public static final ConfigOptionList    SNAP_AIM_MODE                       = new ConfigOptionList  ("snapAimMode", SnapAimMode.YAW, "Snap aim mode: yaw, or pitch, or both");
        public static final ConfigBoolean       SNAP_AIM_ONLY_CLOSE_TO_ANGLE        = new ConfigBoolean     ("snapAimOnlyCloseToAngle", true, "If enabled, then the snap aim only snaps to the angle\nwhen the internal angle is within a certain distance of it.\nThe threshold can be set in snapAimThreshold");
        public static final ConfigBoolean       SNAP_AIM_PITCH_OVERSHOOT            = new ConfigBoolean     ("snapAimPitchOvershoot", false, "Whether or not to allow overshooting the pitch angle\nfrom the normal +/- 90 degrees up to +/- 180 degrees");
        public static final ConfigDouble        SNAP_AIM_PITCH_STEP                 = new ConfigDouble      ("snapAimPitchStep", 12.5, 0, 90, "The pitch angle step of the snap aim tweak");
        public static final ConfigDouble        SNAP_AIM_THRESHOLD_PITCH            = new ConfigDouble      ("snapAimThresholdPitch", 1.5, "The angle threshold inside which the player rotation will\nbe snapped to the snap angle.");
        public static final ConfigDouble        SNAP_AIM_THRESHOLD_YAW              = new ConfigDouble      ("snapAimThresholdYaw", 5.0, "The angle threshold inside which the player rotation will\nbe snapped to the snap angle.");
        public static final ConfigDouble        SNAP_AIM_YAW_STEP                   = new ConfigDouble      ("snapAimYawStep", 45, 0, 360, "The yaw angle step of the snap aim tweak");
        public static final ConfigInteger       STRUCTURE_BLOCK_MAX_SIZE            = new ConfigInteger     ("structureBlockMaxSize", 128, 1, 256, "The maximum dimensions for a Structure Block's saved area");
        public static final ConfigString        TOOL_SWITCHABLE_SLOTS               = new ConfigString      ("toolSwitchableSlots", "1-9", "The slots that the Tool Switch tweak is allowed to put tools to.\nNote that Tool Switch can also switch to other slots in the hotbar,\nif they already have the preferred tool, but it will only\nswap new tools to these slots");
        public static final ConfigString        TOOL_SWITCH_IGNORED_SLOTS           = new ConfigString      ("toolSwitchIgnoredSlots", "", "The slots where the Tool Switch tweak does not work when they are active.");
        public static final ConfigBoolean       ZOOM_ADJUST_MOUSE_SENSITIVITY       = new ConfigBoolean     ("zoomAdjustMouseSensitivity", true, "If enabled, then the mouse sensitivity is reduced\nwhile the zoom feature is enabled and the zoom key is active");
        public static final ConfigDouble        ZOOM_FOV                            = new ConfigDouble      ("zoomFov", 30, 0.01, 359.99, "The FOV value used for the zoom feature");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                CARPET_ACCURATE_PLACEMENT_PROTOCOL,
                CLIENT_PLACEMENT_ROTATION,
                //DEBUG_LOGGING,
                FAST_LEFT_CLICK_ALLOW_TOOLS,
                FAST_PLACEMENT_REMEMBER_ALWAYS,
                FREE_CAMERA_PLAYER_INPUTS,
                FREE_CAMERA_PLAYER_MOVEMENT,
                HAND_RESTOCK_PRE,
                HANGABLE_ENTITY_BYPASS_INVERSE,
                ITEM_USE_PACKET_CHECK_BYPASS,
                MAP_PREVIEW_REQUIRE_SHIFT,
                PERMANENT_SNEAK_ALLOW_IN_GUIS,
                PLACEMENT_RESTRICTION_TIED_TO_FAST,
                POTION_WARNING_BENEFICIAL_ONLY,
                REMEMBER_FLEXIBLE,
                SHULKER_DISPLAY_BACKGROUND_COLOR,
                SHULKER_DISPLAY_REQUIRE_SHIFT,
                SLOT_SYNC_WORKAROUND,
                SLOT_SYNC_WORKAROUND_ALWAYS,
                SNAP_AIM_INDICATOR,
                SNAP_AIM_ONLY_CLOSE_TO_ANGLE,
                SNAP_AIM_PITCH_OVERSHOOT,
                ZOOM_ADJUST_MOUSE_SENSITIVITY,

                BLOCK_TYPE_BREAK_RESTRICTION_WARN,
                BREAKING_RESTRICTION_MODE,
                ELYTRA_CAMERA_INDICATOR,
                ENTITY_TYPE_ATTACK_RESTRICTION_WARN,
                PLACEMENT_RESTRICTION_MODE,
                HOTBAR_SWAP_OVERLAY_ALIGNMENT,
                SNAP_AIM_MODE,

                CHAT_TIME_FORMAT,
                CHAT_BACKGROUND_COLOR,
                FLEXIBLE_PLACEMENT_OVERLAY_COLOR,
                SNAP_AIM_INDICATOR_COLOR,

                AFTER_CLICKER_CLICK_COUNT,
                BLOCK_REACH_DISTANCE,
                ANGEL_BLOCK_PLACEMENT_DISTANCE,
                BREAKING_GRID_SIZE,
                CUSTOM_INVENTORY_GUI_SCALE,
                ENTITY_REACH_DISTANCE,
                FAST_BLOCK_PLACEMENT_COUNT,
                FAST_LEFT_CLICK_COUNT,
                FAST_RIGHT_CLICK_COUNT,
                FILL_CLONE_LIMIT,
                FLY_DECELERATION_FACTOR,
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
                PERIODIC_HOLD_ATTACK_DURATION,
                PERIODIC_HOLD_ATTACK_INTERVAL,
                PERIODIC_HOLD_USE_DURATION,
                PERIODIC_HOLD_USE_INTERVAL,
                PLACEMENT_GRID_SIZE,
                PLACEMENT_LIMIT,
                POTION_WARNING_THRESHOLD,
                RENDER_LIMIT_ITEM,
                RENDER_LIMIT_XP_ORB,
                SCULK_SENSOR_PULSE_LENGTH,
                SNAP_AIM_PITCH_STEP,
                SNAP_AIM_THRESHOLD_PITCH,
                SNAP_AIM_THRESHOLD_YAW,
                SNAP_AIM_YAW_STEP,
                STRUCTURE_BLOCK_MAX_SIZE,
                TOOL_SWITCHABLE_SLOTS,
                TOOL_SWITCH_IGNORED_SLOTS,
                ZOOM_FOV
        );
    }

    public static class Fixes
    {
        public static final ConfigBoolean ELYTRA_FIX                        = new ConfigBoolean("elytraFix", false, "Elytra landing fix by Earthcomputer and Nessie.\nThe deployment fix is now in vanilla, so this only affects landing now.");
        public static final ConfigBoolean MAC_HORIZONTAL_SCROLL             = new ConfigBoolean("macHorizontalScroll", false, "If you are on Mac/OSX, this applies the same fix/change\nas the hscroll mod, while not breaking all the scroll handling\nin malilib-based mods.");
        public static final ConfigBoolean RAVAGER_CLIENT_BLOCK_BREAK_FIX    = new ConfigBoolean("ravagerClientBlockBreakFix", false, "Fixes Ravagers breaking blocks on the client side,\nwhich causes annoying ghost blocks/block desyncs");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                ELYTRA_FIX,
                MAC_HORIZONTAL_SCROLL,
                RAVAGER_CLIENT_BLOCK_BREAK_FIX
        );
    }

    public static class Lists
    {
        public static final ConfigOptionList BLOCK_TYPE_BREAK_RESTRICTION_LIST_TYPE = new ConfigOptionList("blockTypeBreakRestrictionListType", ListType.BLACKLIST, "The restriction list type for the Block Type Break Restriction tweak");
        public static final ConfigStringList BLOCK_TYPE_BREAK_RESTRICTION_BLACKLIST = new ConfigStringList("blockTypeBreakRestrictionBlackList", ImmutableList.of("minecraft:budding_amethyst"), "The blocks that are NOT allowed to be broken while the Block Break Restriction tweak is enabled,\nif the blockBreakRestrictionListType is set to Black List");
        public static final ConfigStringList BLOCK_TYPE_BREAK_RESTRICTION_WHITELIST = new ConfigStringList("blockTypeBreakRestrictionWhiteList", ImmutableList.of(), "The only blocks that can be broken while the Block Break Restriction tweak is enabled,\nif the blockBreakRestrictionListType is set to White List");
        //public static final ConfigStringList CREATIVE_EXTRA_ITEMS               = new ConfigStringList("creativeExtraItems", ImmutableList.of("minecraft:command_block", "minecraft:chain_command_block", "minecraft:repeating_command_block", "minecraft:dragon_egg", "minecraft:structure_void", "minecraft:structure_block", "minecraft:structure_block{BlockEntityTag:{mode:\"SAVE\"}}", "minecraft:structure_block{BlockEntityTag:{mode:\"LOAD\"}}", "minecraft:structure_block{BlockEntityTag:{mode:\"CORNER\"}}"), "Extra items that should be appended to the creative inventory.\nCurrently these will appear in the Transportation category.\nIn the future the group per added item will be customizable.");
        public static final ConfigOptionList ENTITY_TYPE_ATTACK_RESTRICTION_LIST_TYPE = new ConfigOptionList("entityTypeAttackRestrictionListType", ListType.BLACKLIST, "The restriction list type for the Entity Type Attack Restriction tweak");
        public static final ConfigStringList ENTITY_TYPE_ATTACK_RESTRICTION_BLACKLIST = new ConfigStringList("entityTypeAttackRestrictionBlackList", ImmutableList.of("minecraft:villager"), "The entities that are NOT allowed to be attacked while the Entity Attack Restriction tweak is enabled,\nif the entityAttackRestrictionListType is set to Black List");
        public static final ConfigStringList ENTITY_TYPE_ATTACK_RESTRICTION_WHITELIST = new ConfigStringList("entityTypeAttackRestrictionWhiteList", ImmutableList.of(), "The only entities that can be attacked while the Entity Attack Restriction tweak is enabled,\nif the entityAttackRestrictionListType is set to White List");
        public static final ConfigStringList ENTITY_WEAPON_MAPPING              = new ConfigStringList("entityWeaponMapping", ImmutableList.of("<default> => minecraft:diamond_sword, minecraft:golden_sword, minecraft:iron_sword, minecraft:netherite_sword, minecraft:stone_sword, minecraft:wooden_sword", "minecraft:end_crystal, minecraft:item_frame, minecraft:glow_item_frame, minecraft:leash_knot => <ignore>", "minecraft:minecart, minecraft:chest_minecart, minecraft:furnace_minecart, minecraft:hopper_minecart, minecraft:hopper_minecart, minecraft:spawner_minecart, minecraft:tnt_minecart, minecraft:boat=> minecraft:diamond_axe, minecraft:golden_axe, minecraft:iron_axe, minecraft:netherite_axe, minecraft:stone_axe, minecraft:wooden_axe"), "Mapping for what weapon should be used with the\n'tweakWeaponSwitch' tweak.\n'<default>' will be used when no other mapping is defined.\n'<ignore>' will not trigger a weapon switch.");
        public static final ConfigOptionList FAST_PLACEMENT_ITEM_LIST_TYPE      = new ConfigOptionList("fastPlacementItemListType", ListType.BLACKLIST, "The item restriction type for the Fast Block Placement tweak");
        public static final ConfigStringList FAST_PLACEMENT_ITEM_BLACKLIST      = new ConfigStringList("fastPlacementItemBlackList", ImmutableList.of("minecraft:ender_chest", "minecraft:white_shulker_box"), "The items that are NOT allowed to be used for the Fast Block Placement tweak,\nif the fastPlacementItemListType is set to Black List");
        public static final ConfigStringList FAST_PLACEMENT_ITEM_WHITELIST      = new ConfigStringList("fastPlacementItemWhiteList", ImmutableList.of(), "The items that are allowed to be used for the Fast Block Placement tweak,\nif the fastPLacementItemListType is set to White List");
        public static final ConfigOptionList FAST_RIGHT_CLICK_BLOCK_LIST_TYPE   = new ConfigOptionList("fastRightClickBlockListType", ListType.BLACKLIST, "The targeted block restriction type for the Fast Right Click tweak");
        public static final ConfigStringList FAST_RIGHT_CLICK_BLOCK_BLACKLIST   = new ConfigStringList("fastRightClickBlockBlackList", ImmutableList.of("minecraft:chest", "minecraft:ender_chest", "minecraft:trapped_chest", "minecraft:white_shulker_box"), "The blocks that are NOT allowed to be right clicked on with\nthe Fast Right Click tweak, if the fastRightClickBlockListType is set to Black List");
        public static final ConfigStringList FAST_RIGHT_CLICK_BLOCK_WHITELIST   = new ConfigStringList("fastRightClickBlockWhiteList", ImmutableList.of(), "The blocks that are allowed to be right clicked on with\nthe Fast Right Click tweak, if the fastRightClickBlockListType is set to White List");
        public static final ConfigOptionList FAST_RIGHT_CLICK_ITEM_LIST_TYPE    = new ConfigOptionList("fastRightClickListType", ListType.NONE, "The item restriction type for the Fast Right Click tweak");
        public static final ConfigStringList FAST_RIGHT_CLICK_ITEM_BLACKLIST    = new ConfigStringList("fastRightClickBlackList", ImmutableList.of("minecraft:firework_rocket"), "The items that are NOT allowed to be used for the Fast Right Click tweak,\nif the fastRightClickListType is set to Black List");
        public static final ConfigStringList FAST_RIGHT_CLICK_ITEM_WHITELIST    = new ConfigStringList("fastRightClickWhiteList", ImmutableList.of("minecraft:bucket", "minecraft:water_bucket", "minecraft:lava_bucket", "minecraft:glass_bottle"), "The items that are allowed to be used for the Fast Right Click tweak,\nif the fastRightClickListType is set to White List");
        public static final ConfigStringList FLAT_WORLD_PRESETS                 = new ConfigStringList("flatWorldPresets", ImmutableList.of("White Glass;1*minecraft:white_stained_glass;minecraft:plains;;minecraft:white_stained_glass", "Glass;1*minecraft:glass;minecraft:plains;;minecraft:glass"), "Custom flat world preset strings.\nThese are in the format: name;blocks_string;biome;generation_features;icon_item\nThe blocks string format is the vanilla format, such as: 62*minecraft:dirt,minecraft:grass\nThe biome can be the registry name, or the int ID\nThe icon item name format is minecraft:iron_nugget");
        public static final ConfigOptionList HAND_RESTOCK_LIST_TYPE             = new ConfigOptionList("handRestockListType", ListType.NONE, "The restriction list type for the Hand Restock tweak");
        public static final ConfigStringList HAND_RESTOCK_BLACKLIST             = new ConfigStringList("handRestockBlackList", ImmutableList.of("minecraft:bucket", "minecraft:lava_bucket", "minecraft:water_bucket"), "The items that are NOT allowed to be restocked with the Hand Restock tweak,\nif the handRestockListType is set to Black List");
        public static final ConfigStringList HAND_RESTOCK_WHITELIST             = new ConfigStringList("handRestockWhiteList", ImmutableList.of(), "The only allowed items that can be restocked with the Hand Restock tweak,\nif the handRestockListType is set to White List");
        public static final ConfigOptionList POTION_WARNING_LIST_TYPE           = new ConfigOptionList("potionWarningListType", ListType.NONE, "The list type for potion warning effects");
        public static final ConfigStringList POTION_WARNING_BLACKLIST           = new ConfigStringList("potionWarningBlackList", ImmutableList.of("minecraft:hunger", "minecraft:mining_fatigue", "minecraft:nausea", "minecraft:poison", "minecraft:slowness", "minecraft:weakness"), "The potion effects that will not be warned about");
        public static final ConfigStringList POTION_WARNING_WHITELIST           = new ConfigStringList("potionWarningWhiteList", ImmutableList.of("minecraft:fire_resistance", "minecraft:invisibility", "minecraft:water_breathing"), "The only potion effects that will be warned about");
        public static final ConfigStringList REPAIR_MODE_SLOTS                  = new ConfigStringList("repairModeSlots", ImmutableList.of("mainhand", "offhand"), "The slots the repair mode should use\nValid values: mainhand, offhand, head, chest, legs, feet");
        public static final ConfigStringList UNSTACKING_ITEMS                   = new ConfigStringList("unstackingItems", ImmutableList.of("minecraft:bucket", "minecraft:glass_bottle"), "The items that should be considered for the\n'tweakItemUnstackingProtection' tweak");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                BLOCK_TYPE_BREAK_RESTRICTION_LIST_TYPE,
                BLOCK_TYPE_BREAK_RESTRICTION_BLACKLIST,
                BLOCK_TYPE_BREAK_RESTRICTION_WHITELIST,
                //CREATIVE_EXTRA_ITEMS,
                ENTITY_TYPE_ATTACK_RESTRICTION_LIST_TYPE,
                ENTITY_TYPE_ATTACK_RESTRICTION_BLACKLIST,
                ENTITY_TYPE_ATTACK_RESTRICTION_WHITELIST,
                ENTITY_WEAPON_MAPPING,
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
                HAND_RESTOCK_LIST_TYPE,
                HAND_RESTOCK_BLACKLIST,
                HAND_RESTOCK_WHITELIST,
                POTION_WARNING_BLACKLIST,
                POTION_WARNING_WHITELIST,
                REPAIR_MODE_SLOTS,
                UNSTACKING_ITEMS
        );
    }

    public static class Disable
    {
        public static final ConfigBooleanHotkeyed       DISABLE_ARMOR_STAND_RENDERING   = new ConfigBooleanHotkeyed("disableArmorStandRendering",           false, "", "Disables all Armor Stand entity rendering");
        public static final ConfigBooleanHotkeyed       DISABLE_AXE_STRIPPING           = new ConfigBooleanHotkeyed("disableAxeStripping",                  false, "", "Disables stripping logs with an axe");
        public static final ConfigBooleanHotkeyed       DISABLE_BAT_SPAWNING            = new ConfigBooleanClient  ("disableBatSpawning",                   false, "", "Disables Bat spawning in single player");
        public static final ConfigBooleanHotkeyed       DISABLE_BEACON_BEAM_RENDERING   = new ConfigBooleanHotkeyed("disableBeaconBeamRendering",           false, "", "Disables Beacon beam rendering");
        public static final ConfigBooleanHotkeyed       DISABLE_BLOCK_BREAK_PARTICLES   = new ConfigBooleanHotkeyed("disableBlockBreakingParticles",        false, "", "Removes the block breaking particles.\n(This is originally from usefulmod by nessie.)");
        public static final ConfigBooleanHotkeyed       DISABLE_DOUBLE_TAP_SPRINT       = new ConfigBooleanHotkeyed("disableDoubleTapSprint",               false, "", "Disables the double-tap-forward-key sprinting");
        public static final ConfigBooleanHotkeyed       DISABLE_BOSS_BAR                = new ConfigBooleanHotkeyed("disableBossBar",                       false, "", "Disables boss bar rendering");
        public static final ConfigBooleanHotkeyed       DISABLE_BOSS_FOG                = new ConfigBooleanHotkeyed("disableBossFog",                       false, "", "Removes the fog that boss mobs cause");
        public static final ConfigBooleanHotkeyed       DISABLE_CHUNK_RENDERING         = new ConfigBooleanHotkeyed("disableChunkRendering",                false, "", "Disables chunk (re-)rendering. This will make any block changes non-visible\nuntil this is disabled again and F3 + A is used to refresh the world rendering.\nThis might help with low fps in places with lots of block changes in some situations,\nwhere the block changes are not really relevant at that time.");
        public static final ConfigBooleanHotkeyed       DISABLE_CLIENT_ENTITY_UPDATES   = new ConfigBooleanHotkeyed("disableClientEntityUpdates",           false, "", "Disables ALL except player entity updates on the client.\nThis is mainly meant for situations where you need to be\nable to do stuff to fix excessive entity count related problems");
        public static final ConfigBooleanHotkeyed       DISABLE_CLIENT_LIGHT_UPDATES    = new ConfigBooleanHotkeyed("disableClientLightUpdates",            false, "", "Disables all client-side light updates");
        public static final ConfigBooleanHotkeyed       DISABLE_CONSTANT_CHUNK_SAVING   = new ConfigBooleanHotkeyed("disableConstantChunkSaving",           false, "", "Disables the game saving up to 20 chunks every tick\nall the time, in addition to the normal auto-save cycle.");
        public static final ConfigBooleanHotkeyed       DISABLE_CREATIVE_INFESTED_BLOCKS= new ConfigBooleanHotkeyed("disableCreativeMenuInfestedBlocks",    false, "", "Removes infested stone blocks from the creative search inventory");
        public static final ConfigBooleanHotkeyed       DISABLE_DEAD_MOB_RENDERING      = new ConfigBooleanHotkeyed("disableDeadMobRendering",              false, "", "Prevents rendering dead mobs (entities that are at 0 health)");
        public static final ConfigBooleanHotkeyed       DISABLE_DEAD_MOB_TARGETING      = new ConfigBooleanHotkeyed("disableDeadMobTargeting",              false, "", "Prevents targeting entities that are at 0 health.\nThis fixes for example hitting already dead mobs.");
        public static final ConfigBooleanHotkeyed       DISABLE_ENTITY_RENDERING        = new ConfigBooleanHotkeyed("disableEntityRendering",               false, "", "Disables ALL except player entity rendering.\nThis is mainly meant for situations where you need to be\nable to do stuff to fix excessive entity count related problems");
        public static final ConfigBooleanHotkeyed       DISABLE_ENTITY_TICKING          = new ConfigBooleanClient  ("disableEntityTicking",                 false, "", "Prevent everything except player entities from getting ticked");
        public static final ConfigBooleanHotkeyed       DISABLE_FALLING_BLOCK_RENDER    = new ConfigBooleanHotkeyed("disableFallingBlockEntityRendering",   false, "", "If enabled, then falling block entities won't be rendered at all");
        public static final ConfigBooleanHotkeyed       DISABLE_FP_EFFECT_PARTICLES     = new ConfigBooleanHotkeyed("disableFirstPersonEffectParticles",    false, "", "Removes the potion effect particles/swirlies in first person\n(from the player itself)");
        public static final ConfigBooleanHotkeyed       DISABLE_INVENTORY_EFFECTS       = new ConfigBooleanHotkeyed("disableInventoryEffectRendering",      false, "", "Removes the potion effect rendering from the inventory GUIs");
        public static final ConfigBooleanHotkeyed       DISABLE_ITEM_SWITCH_COOLDOWN    = new ConfigBooleanHotkeyed("disableItemSwitchRenderCooldown",      false, "", "If true, then there won't be any cooldown/equip animation\nwhen switching the held item or using the item.");
        public static final ConfigBooleanHotkeyed       DISABLE_MOB_SPAWNER_MOB_RENDER  = new ConfigBooleanHotkeyed("disableMobSpawnerMobRendering",        false, "", "Removes the entity rendering from mob spawners");
        public static final ConfigBooleanHotkeyed       DISABLE_NAUSEA_EFFECT           = new ConfigBooleanHotkeyed("disableNauseaEffect",                  false, "", "Disables the nausea visual effects");
        public static final ConfigBooleanHotkeyed       DISABLE_NETHER_FOG              = new ConfigBooleanHotkeyed("disableNetherFog",                     false, "", "Removes the fog in the Nether");
        public static final ConfigBooleanHotkeyed       DISABLE_NETHER_PORTAL_SOUND     = new ConfigBooleanHotkeyed("disableNetherPortalSound",             false, "", "Disables the Nether Portal sound");
        public static final ConfigBooleanHotkeyed       DISABLE_OBSERVER                = new ConfigBooleanClient  ("disableObserver",                      false, "", "Disable Observers from triggering at all");
        public static final ConfigBooleanHotkeyed       DISABLE_OFFHAND_RENDERING       = new ConfigBooleanHotkeyed("disableOffhandRendering",              false, "", "Disables the offhand item from getting rendered");
        public static final ConfigBooleanHotkeyed       DISABLE_PARTICLES               = new ConfigBooleanHotkeyed("disableParticles",                     false, "", "Disables all particles");
        public static final ConfigBooleanHotkeyed       DISABLE_PORTAL_GUI_CLOSING      = new ConfigBooleanHotkeyed("disablePortalGuiClosing",              false, "", "If enabled, then you can still open GUIs while in a Nether Portal");
        public static final ConfigBooleanHotkeyed       DISABLE_RAIN_EFFECTS            = new ConfigBooleanHotkeyed("disableRainEffects",                   false, "", "Disables rain rendering and sounds");
        public static final ConfigBooleanHotkeyed       DISABLE_RENDERING_SCAFFOLDING   = new ConfigBooleanHotkeyed("disableRenderingScaffolding",          false, "", "Disables rendering of Scaffolding Blocks");
        public static final ConfigBooleanHotkeyed       DISABLE_RENDER_DISTANCE_FOG     = new ConfigBooleanHotkeyed("disableRenderDistanceFog",             false, "", "Disables the fog that increases around the render distance");
        public static final ConfigBooleanHotkeyed       DISABLE_SCOREBOARD_RENDERING    = new ConfigBooleanHotkeyed("disableScoreboardRendering",           false, "", "Removes the sidebar scoreboard rendering");
        public static final ConfigBooleanHotkeyed       DISABLE_SHULKER_BOX_TOOLTIP     = new ConfigBooleanHotkeyed("disableShulkerBoxTooltip",             false, "", "Disables the vanilla text tooltip for Shulker Box contents");
        public static final ConfigBooleanHotkeyed       DISABLE_SHOVEL_PATHING          = new ConfigBooleanHotkeyed("disableShovelPathing",                 false, "", "Disables converting grass etc. to Path Blocks with a shovel");
        public static final ConfigBooleanHotkeyed       DISABLE_SIGN_GUI                = new ConfigBooleanHotkeyed("disableSignGui",                       false, "", "Prevent the Sign edit GUI from opening");
        public static final ConfigBooleanHotkeyed       DISABLE_SKY_DARKNESS            = new ConfigBooleanHotkeyed("disableSkyDarkness",                   false, "", "Disables the sky darkness below y = 63\n\n(By moving the threshold y to 2 blocks below the bottom of the world instead)");
        public static final ConfigBooleanHotkeyed       DISABLE_SLIME_BLOCK_SLOWDOWN    = new ConfigBooleanHotkeyed("disableSlimeBlockSlowdown",            false, "", "Removes the slowdown from walking on Slime Blocks.\n(This is originally from usefulmod by nessie.)");
        public static final ConfigBooleanHotkeyed       DISABLE_STATUS_EFFECT_HUD       = new ConfigBooleanHotkeyed("disableStatusEffectHud",               false, "", "Disables the status effect HUD rendering (which is usually\nin the top right corner of the screen)");
        public static final ConfigBooleanHotkeyed       DISABLE_TILE_ENTITY_RENDERING   = new ConfigBooleanHotkeyed("disableTileEntityRendering",           false, "", "Prevents all TileEntity renderers from rendering");
        public static final ConfigBooleanHotkeyed       DISABLE_TILE_ENTITY_TICKING     = new ConfigBooleanClient  ("disableTileEntityTicking",             false, "", "Prevent all TileEntities from getting ticked");
        public static final ConfigBooleanHotkeyed       DISABLE_VILLAGER_TRADE_LOCKING  = new ConfigBooleanClient  ("disableVillagerTradeLocking",          false, "", "Prevents villager trades from ever locking, by always incrementing\nthe max uses as well when the recipe uses is incremented");
        public static final ConfigBooleanHotkeyed       DISABLE_WALL_UNSPRINT           = new ConfigBooleanHotkeyed("disableWallUnsprint",                  false, "", "Touching a wall doesn't drop you out from sprint mode");
        public static final ConfigBooleanHotkeyed       DISABLE_WORLD_VIEW_BOB          = new ConfigBooleanHotkeyed("disableWorldViewBob",                  false, "", "Disables the view bob wobble effect of the world, but not the hand\nThis setting will fail if you have Iris installed.");

        public static final ImmutableList<IHotkeyTogglable> OPTIONS = ImmutableList.of(
                DISABLE_ARMOR_STAND_RENDERING,
                DISABLE_AXE_STRIPPING,
                DISABLE_BAT_SPAWNING,
                DISABLE_BEACON_BEAM_RENDERING,
                DISABLE_BLOCK_BREAK_PARTICLES,
                DISABLE_DOUBLE_TAP_SPRINT,
                DISABLE_BOSS_BAR,
                DISABLE_BOSS_FOG,
                DISABLE_CHUNK_RENDERING,
                DISABLE_CLIENT_ENTITY_UPDATES,
                DISABLE_CLIENT_LIGHT_UPDATES,
                DISABLE_CONSTANT_CHUNK_SAVING,
                DISABLE_CREATIVE_INFESTED_BLOCKS,
                DISABLE_DEAD_MOB_RENDERING,
                DISABLE_DEAD_MOB_TARGETING,
                DISABLE_ENTITY_RENDERING,
                DISABLE_ENTITY_TICKING,
                DISABLE_FALLING_BLOCK_RENDER,
                DISABLE_FP_EFFECT_PARTICLES,
                DISABLE_INVENTORY_EFFECTS,
                DISABLE_ITEM_SWITCH_COOLDOWN,
                DISABLE_MOB_SPAWNER_MOB_RENDER,
                DISABLE_NAUSEA_EFFECT,
                DISABLE_NETHER_FOG,
                DISABLE_NETHER_PORTAL_SOUND,
                DISABLE_OBSERVER,
                DISABLE_OFFHAND_RENDERING,
                DISABLE_PARTICLES,
                DISABLE_PORTAL_GUI_CLOSING,
                DISABLE_RAIN_EFFECTS,
                DISABLE_RENDERING_SCAFFOLDING,
                DISABLE_RENDER_DISTANCE_FOG,
                DISABLE_SCOREBOARD_RENDERING,
                DISABLE_SHULKER_BOX_TOOLTIP,
                DISABLE_SHOVEL_PATHING,
                DISABLE_SIGN_GUI,
                DISABLE_SKY_DARKNESS,
                DISABLE_SLIME_BLOCK_SLOWDOWN,
                DISABLE_STATUS_EFFECT_HUD,
                DISABLE_TILE_ENTITY_RENDERING,
                DISABLE_TILE_ENTITY_TICKING,
                DISABLE_VILLAGER_TRADE_LOCKING,
                DISABLE_WALL_UNSPRINT,
                DISABLE_WORLD_VIEW_BOB
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
                ConfigUtils.readHotkeyToggleOptions(root, "DisableHotkeys", "DisableToggles", Disable.OPTIONS);
                ConfigUtils.readHotkeyToggleOptions(root, "TweakHotkeys", "TweakToggles", FeatureToggle.VALUES);
            }
        }

        // TODO 1.19.3+
        //CreativeExtraItems.setCreativeExtraItems(Lists.CREATIVE_EXTRA_ITEMS.getStrings());

        InventoryUtils.setToolSwitchableSlots(Generic.TOOL_SWITCHABLE_SLOTS.getStringValue());
        InventoryUtils.setToolSwitchIgnoreSlots(Generic.TOOL_SWITCH_IGNORED_SLOTS.getStringValue());
        InventoryUtils.setRepairModeSlots(Lists.REPAIR_MODE_SLOTS.getStrings());
        InventoryUtils.setUnstackingItems(Lists.UNSTACKING_ITEMS.getStrings());
        InventoryUtils.setWeaponMapping(Lists.ENTITY_WEAPON_MAPPING.getStrings());

        PlacementTweaks.BLOCK_TYPE_BREAK_RESTRICTION.setListType((ListType) Lists.BLOCK_TYPE_BREAK_RESTRICTION_LIST_TYPE.getOptionListValue());
        PlacementTweaks.BLOCK_TYPE_BREAK_RESTRICTION.setListContents(
                Lists.BLOCK_TYPE_BREAK_RESTRICTION_BLACKLIST.getStrings(),
                Lists.BLOCK_TYPE_BREAK_RESTRICTION_WHITELIST.getStrings());

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

        PlacementTweaks.HAND_RESTOCK_RESTRICTION.setListType((ListType) Lists.HAND_RESTOCK_LIST_TYPE.getOptionListValue());
        PlacementTweaks.HAND_RESTOCK_RESTRICTION.setListContents(
                Lists.HAND_RESTOCK_BLACKLIST.getStrings(),
                Lists.HAND_RESTOCK_WHITELIST.getStrings());

        MiscTweaks.POTION_RESTRICTION.setListType((ListType) Lists.POTION_WARNING_LIST_TYPE.getOptionListValue());
        MiscTweaks.POTION_RESTRICTION.setListContents(
                Lists.POTION_WARNING_BLACKLIST.getStrings(),
                Lists.POTION_WARNING_WHITELIST.getStrings());

        MiscTweaks.ENTITY_TYPE_ATTACK_RESTRICTION.setListType((ListType) Lists.ENTITY_TYPE_ATTACK_RESTRICTION_LIST_TYPE.getOptionListValue());
        MiscTweaks.ENTITY_TYPE_ATTACK_RESTRICTION.setListContents(
                Lists.ENTITY_TYPE_ATTACK_RESTRICTION_BLACKLIST.getStrings(),
                Lists.ENTITY_TYPE_ATTACK_RESTRICTION_WHITELIST.getStrings());

        if (MinecraftClient.getInstance().world == null)
        {
            // Turn off after loading the configs, just in case it was enabled in the config somehow.
            // But only if we are currently not in a world, since changing configs also re-loads them when closing the menu.
            FeatureToggle.TWEAK_FREE_CAMERA.setBooleanValue(false);
        }
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
            ConfigUtils.writeHotkeyToggleOptions(root, "DisableHotkeys", "DisableToggles", Disable.OPTIONS);
            ConfigUtils.writeHotkeyToggleOptions(root, "TweakHotkeys", "TweakToggles", FeatureToggle.VALUES);

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
