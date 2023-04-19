package tweakeroo.config;

import java.util.List;
import com.google.common.collect.ImmutableList;

import malilib.config.option.HotkeyConfig;
import malilib.input.Context;
import malilib.input.KeyBindSettings;
import malilib.overlay.message.MessageOutput;

public class Hotkeys
{
    public static final HotkeyConfig ACCURATE_BLOCK_PLACEMENT_INTO      = new HotkeyConfig("accurateBlockPlacementInto",        "",         KeyBindSettings.INGAME_MODIFIER);
    public static final HotkeyConfig ACCURATE_BLOCK_PLACEMENT_REVERSE   = new HotkeyConfig("accurateBlockPlacementReverse",     "",         KeyBindSettings.INGAME_MODIFIER);
    public static final HotkeyConfig BLINK_DRIVE                        = new HotkeyConfig("blinkDrive",                        "");
    public static final HotkeyConfig BLINK_DRIVE_Y_LEVEL                = new HotkeyConfig("blinkDriveYLevel",                  "");
    public static final HotkeyConfig BREAKING_RESTRICTION_MODE_COLUMN   = new HotkeyConfig("breakingRestrictionModeColumn",     "");
    public static final HotkeyConfig BREAKING_RESTRICTION_MODE_DIAGONAL = new HotkeyConfig("breakingRestrictionModeDiagonal",   "");
    public static final HotkeyConfig BREAKING_RESTRICTION_MODE_FACE     = new HotkeyConfig("breakingRestrictionModeFace",       "");
    public static final HotkeyConfig BREAKING_RESTRICTION_MODE_LAYER    = new HotkeyConfig("breakingRestrictionModeLayer",      "");
    public static final HotkeyConfig BREAKING_RESTRICTION_MODE_LINE     = new HotkeyConfig("breakingRestrictionModeLine",       "");
    public static final HotkeyConfig BREAKING_RESTRICTION_MODE_PLANE    = new HotkeyConfig("breakingRestrictionModePlane",      "");
    public static final HotkeyConfig COPY_SIGN_TEXT                     = new HotkeyConfig("copySignText",                      "");
    public static final HotkeyConfig ELYTRA_CAMERA                      = new HotkeyConfig("elytraCamera",                      "",         KeyBindSettings.INGAME_MODIFIER);
    public static final HotkeyConfig FLEXIBLE_BLOCK_PLACEMENT_ADJACENT  = new HotkeyConfig("flexibleBlockPlacementAdjacent",    "",         KeyBindSettings.INGAME_MODIFIER);
    public static final HotkeyConfig FLEXIBLE_BLOCK_PLACEMENT_OFFSET    = new HotkeyConfig("flexibleBlockPlacementOffset",      "L_CTRL",   KeyBindSettings.INGAME_MODIFIER);
    public static final HotkeyConfig FLEXIBLE_BLOCK_PLACEMENT_ROTATION  = new HotkeyConfig("flexibleBlockPlacementRotation",    "L_ALT",    KeyBindSettings.INGAME_MODIFIER);
    public static final HotkeyConfig FLY_PRESET_1                       = new HotkeyConfig("flySpeedPreset1",                   "",         KeyBindSettings.INGAME_BOTH);
    public static final HotkeyConfig FLY_PRESET_2                       = new HotkeyConfig("flySpeedPreset2",                   "",         KeyBindSettings.INGAME_BOTH);
    public static final HotkeyConfig FLY_PRESET_3                       = new HotkeyConfig("flySpeedPreset3",                   "",         KeyBindSettings.INGAME_BOTH);
    public static final HotkeyConfig FLY_PRESET_4                       = new HotkeyConfig("flySpeedPreset4",                   "",         KeyBindSettings.INGAME_BOTH);
    public static final HotkeyConfig FLY_PRESET_5                       = new HotkeyConfig("flySpeedPreset5",                   "",         KeyBindSettings.INGAME_BOTH);
    public static final HotkeyConfig FLY_PRESET_6                       = new HotkeyConfig("flySpeedPreset6",                   "",         KeyBindSettings.INGAME_BOTH);
    public static final HotkeyConfig GHOST_BLOCK_REMOVER                = new HotkeyConfig("ghostBlockRemover",                 "");
    public static final HotkeyConfig HOTBAR_SCROLL                      = new HotkeyConfig("hotbarScroll",                      "",         KeyBindSettings.INGAME_RELEASE);
    public static final HotkeyConfig HOTBAR_SWAP_BASE                   = new HotkeyConfig("hotbarSwapBase",                    "",         KeyBindSettings.INGAME_MODIFIER);
    public static final HotkeyConfig HOTBAR_SWAP_1                      = new HotkeyConfig("hotbarSwap1",                       "");
    public static final HotkeyConfig HOTBAR_SWAP_2                      = new HotkeyConfig("hotbarSwap2",                       "");
    public static final HotkeyConfig HOTBAR_SWAP_3                      = new HotkeyConfig("hotbarSwap3",                       "");
    public static final HotkeyConfig INVENTORY_PREVIEW                  = new HotkeyConfig("inventoryPreview",                  "L_ALT",    KeyBindSettings.INGAME_MODIFIER);
    public static final HotkeyConfig OPEN_CONFIG_GUI                    = new HotkeyConfig("openConfigGui",                     "X,C");
    public static final HotkeyConfig PLACEMENT_Y_MIRROR                 = new HotkeyConfig("placementYMirror",                  "",         KeyBindSettings.INGAME_MODIFIER);
    public static final HotkeyConfig PLAYER_INVENTORY_PEEK              = new HotkeyConfig("playerInventoryPeek",               "",         KeyBindSettings.INGAME_MODIFIER);
    public static final HotkeyConfig PLACEMENT_RESTRICTION_MODE_COLUMN  = new HotkeyConfig("placementRestrictionModeColumn",    "Z,3");
    public static final HotkeyConfig PLACEMENT_RESTRICTION_MODE_DIAGONAL= new HotkeyConfig("placementRestrictionModeDiagonal",  "Z,5");
    public static final HotkeyConfig PLACEMENT_RESTRICTION_MODE_FACE    = new HotkeyConfig("placementRestrictionModeFace",      "Z,2");
    public static final HotkeyConfig PLACEMENT_RESTRICTION_MODE_LAYER   = new HotkeyConfig("placementRestrictionModeLayer",     "Z,6");
    public static final HotkeyConfig PLACEMENT_RESTRICTION_MODE_LINE    = new HotkeyConfig("placementRestrictionModeLine",      "Z,4");
    public static final HotkeyConfig PLACEMENT_RESTRICTION_MODE_PLANE   = new HotkeyConfig("placementRestrictionModePlane",     "Z,1");
    public static final HotkeyConfig RELOAD_LANGUAGE_PACKS              = new HotkeyConfig("reloadLanguagePacks",               "",         KeyBindSettings.builder().context(Context.ANY).build());
    public static final HotkeyConfig SKIP_ALL_RENDERING                 = new HotkeyConfig("skipAllRendering",                  "");
    public static final HotkeyConfig SKIP_WORLD_RENDERING               = new HotkeyConfig("skipWorldRendering",                "");
    public static final HotkeyConfig TOGGLE_GRAB_CURSOR                 = new HotkeyConfig("toggleGrabCursor",                  "");
    public static final HotkeyConfig TOOL_PICK                          = new HotkeyConfig("toolPick",                          "");
    public static final HotkeyConfig ZOOM_ACTIVATE                      = new HotkeyConfig("zoomActivate",                      "",         KeyBindSettings.INGAME_MODIFIER_BOTH.asBuilder().messageOutput(MessageOutput.CUSTOM_HOTBAR).build());

    public static final List<HotkeyConfig> HOTKEY_LIST = ImmutableList.of(
            ACCURATE_BLOCK_PLACEMENT_INTO,
            ACCURATE_BLOCK_PLACEMENT_REVERSE,
            BLINK_DRIVE,
            BLINK_DRIVE_Y_LEVEL,
            BREAKING_RESTRICTION_MODE_COLUMN,
            BREAKING_RESTRICTION_MODE_DIAGONAL,
            BREAKING_RESTRICTION_MODE_FACE,
            BREAKING_RESTRICTION_MODE_LAYER,
            BREAKING_RESTRICTION_MODE_LINE,
            BREAKING_RESTRICTION_MODE_PLANE,
            COPY_SIGN_TEXT,
            ELYTRA_CAMERA,
            FLEXIBLE_BLOCK_PLACEMENT_ADJACENT,
            FLEXIBLE_BLOCK_PLACEMENT_OFFSET,
            FLEXIBLE_BLOCK_PLACEMENT_ROTATION,
            FLY_PRESET_1,
            FLY_PRESET_2,
            FLY_PRESET_3,
            FLY_PRESET_4,
            FLY_PRESET_5,
            FLY_PRESET_6,
            GHOST_BLOCK_REMOVER,
            HOTBAR_SCROLL,
            HOTBAR_SWAP_BASE,
            HOTBAR_SWAP_1,
            HOTBAR_SWAP_2,
            HOTBAR_SWAP_3,
            INVENTORY_PREVIEW,
            OPEN_CONFIG_GUI,
            PLACEMENT_Y_MIRROR,
            PLAYER_INVENTORY_PEEK,
            PLACEMENT_RESTRICTION_MODE_COLUMN,
            PLACEMENT_RESTRICTION_MODE_DIAGONAL,
            PLACEMENT_RESTRICTION_MODE_FACE,
            PLACEMENT_RESTRICTION_MODE_LAYER,
            PLACEMENT_RESTRICTION_MODE_LINE,
            PLACEMENT_RESTRICTION_MODE_PLANE,
            RELOAD_LANGUAGE_PACKS,
            SKIP_ALL_RENDERING,
            SKIP_WORLD_RENDERING,
            TOGGLE_GRAB_CURSOR,
            TOOL_PICK,
            ZOOM_ACTIVATE
    );
}
