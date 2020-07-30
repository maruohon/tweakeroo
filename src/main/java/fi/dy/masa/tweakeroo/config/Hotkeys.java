package fi.dy.masa.tweakeroo.config;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.input.KeyBindSettings.Context;

public class Hotkeys
{
    public static final HotkeyConfig ACCURATE_BLOCK_PLACEMENT_IN        = new HotkeyConfig("accurateBlockPlacementInto", "", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to activate the accurate block placement\nmode/overlay for placing the block facing\ninto the clicked block face");
    public static final HotkeyConfig ACCURATE_BLOCK_PLACEMENT_REVERSE   = new HotkeyConfig("accurateBlockPlacementReverse", "", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to activate the accurate block placement\nmode/overlay for placing the block facing\nthe opposite way from what it would normally be");
    public static final HotkeyConfig BLINK_DRIVE                        = new HotkeyConfig("blinkDrive", "", "Teleports the player (using a tp command)\nto the location currently at the mouse cursor");
    public static final HotkeyConfig BLINK_DRIVE_Y_LEVEL                = new HotkeyConfig("blinkDriveYLevel", "", "Teleports the player (using a tp command)\nto the location currently at the mouse cursor,\nbut maintaining the current y-position");
    public static final HotkeyConfig BREAKING_RESTRICTION_MODE_COLUMN   = new HotkeyConfig("breakingRestrictionModeColumn", "", "Switch the Breaking Restriction mode to the Column mode");
    public static final HotkeyConfig BREAKING_RESTRICTION_MODE_DIAGONAL = new HotkeyConfig("breakingRestrictionModeDiagonal", "", "Switch the Breaking Restriction mode to the Diagonal mode");
    public static final HotkeyConfig BREAKING_RESTRICTION_MODE_FACE     = new HotkeyConfig("breakingRestrictionModeFace", "", "Switch the Breaking Restriction mode to the Face mode");
    public static final HotkeyConfig BREAKING_RESTRICTION_MODE_LAYER    = new HotkeyConfig("breakingRestrictionModeLayer", "", "Switch the Breaking Restriction mode to the Layer mode");
    public static final HotkeyConfig BREAKING_RESTRICTION_MODE_LINE     = new HotkeyConfig("breakingRestrictionModeLine", "", "Switch the Breaking Restriction mode to the Line mode");
    public static final HotkeyConfig BREAKING_RESTRICTION_MODE_PLANE    = new HotkeyConfig("breakingRestrictionModePlane", "", "Switch the Breaking Restriction mode to the Plane mode");
    public static final HotkeyConfig COPY_SIGN_TEXT                     = new HotkeyConfig("copySignText", "", "Copies the text from an already-placed sign.\nThat text can be used with the tweakSignCopy tweak.");
    public static final HotkeyConfig ELYTRA_CAMERA                      = new HotkeyConfig("elytraCamera", "", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to lock the current real player rotations, only allowing the\ninputs (mouse) to affect separate \"camera rotations\" used only for the rendering\nwhile this key is active.\nMeant for freely looking down/around while elytra flying straight.");
    public static final HotkeyConfig FLEXIBLE_BLOCK_PLACEMENT_ADJACENT  = new HotkeyConfig("flexibleBlockPlacementAdjacent", "", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to activate the flexible block placement\nmode/overlay for placing the block in an adjacent position");
    public static final HotkeyConfig FLEXIBLE_BLOCK_PLACEMENT_OFFSET    = new HotkeyConfig("flexibleBlockPlacementOffset", "LCONTROL", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to activate the flexible block placement\nmode/overlay for placing the block in a\noffset or diagonal position");
    public static final HotkeyConfig FLEXIBLE_BLOCK_PLACEMENT_ROTATION  = new HotkeyConfig("flexibleBlockPlacementRotation", "LMENU", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to activate the flexible block placement\nmode/overlay for placing the block with\na rotation/facing");
    public static final HotkeyConfig FLY_PRESET_1                       = new HotkeyConfig("flyPreset1", "", KeyBindSettings.INGAME_BOTH, "Switch to fly preset 1");
    public static final HotkeyConfig FLY_PRESET_2                       = new HotkeyConfig("flyPreset2", "", KeyBindSettings.INGAME_BOTH, "Switch to fly preset 2");
    public static final HotkeyConfig FLY_PRESET_3                       = new HotkeyConfig("flyPreset3", "", KeyBindSettings.INGAME_BOTH, "Switch to fly preset 3");
    public static final HotkeyConfig FLY_PRESET_4                       = new HotkeyConfig("flyPreset4", "", KeyBindSettings.INGAME_BOTH, "Switch to fly preset 4");
    public static final HotkeyConfig FREE_CAMERA_PLAYER_INPUTS          = new HotkeyConfig("freeCameraPlayerInputs", "", "Toggle the Generic -> freeCameraPlayerInputs option");
    public static final HotkeyConfig FREE_CAMERA_PLAYER_MOVEMENT        = new HotkeyConfig("freeCameraPlayerMovement", "", "Toggle the Generic -> freeCameraPlayerMovement option");
    public static final HotkeyConfig GHOST_BLOCK_REMOVER                = new HotkeyConfig("ghostBlockRemover", "", "A \"manual ghost block fix\". Basically right clicks all the air blocks\nalong the look vector within the player's reach distance.");
    public static final HotkeyConfig HOTBAR_SCROLL                      = new HotkeyConfig("hotbarScroll", "", KeyBindSettings.RELEASE_ALLOW_EXTRA, "The key to hold to allow scrolling the hotbar\nthrough the player inventory rows");
    public static final HotkeyConfig HOTBAR_SWAP_BASE                   = new HotkeyConfig("hotbarSwapBase", "", KeyBindSettings.PRESS_ALLOWEXTRA, "The base key to show the hotbar/inventory overlay");
    public static final HotkeyConfig HOTBAR_SWAP_1                      = new HotkeyConfig("hotbarSwap1", "", "Swap the hotbar with the top-most inventory row");
    public static final HotkeyConfig HOTBAR_SWAP_2                      = new HotkeyConfig("hotbarSwap2", "", "Swap the hotbar with the middle inventory row");
    public static final HotkeyConfig HOTBAR_SWAP_3                      = new HotkeyConfig("hotbarSwap3", "", "Swap the hotbar with the bottom-most inventory row");
    public static final HotkeyConfig INVENTORY_PREVIEW                  = new HotkeyConfig("inventoryPreview", "LMENU", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to activate the inventory preview feature");
    public static final HotkeyConfig OPEN_CONFIG_GUI                    = new HotkeyConfig("openConfigGui", "X,C", "The key open the in-game config GUI");
    public static final HotkeyConfig PLACEMENT_Y_MIRROR                 = new HotkeyConfig("placementYMirror", "", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to mirror the targeted y-position within the block");
    public static final HotkeyConfig PLAYER_INVENTORY_PEEK              = new HotkeyConfig("playerInventoryPeek", "", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to activate the player inventory peek/preview feature");
    public static final HotkeyConfig PLACEMENT_RESTRICTION_MODE_COLUMN  = new HotkeyConfig("placementRestrictionModeColumn", "Z,3", "Switch the Placement Restriction mode to the Column mode");
    public static final HotkeyConfig PLACEMENT_RESTRICTION_MODE_DIAGONAL= new HotkeyConfig("placementRestrictionModeDiagonal", "Z,5", "Switch the Placement Restriction mode to the Diagonal mode");
    public static final HotkeyConfig PLACEMENT_RESTRICTION_MODE_FACE    = new HotkeyConfig("placementRestrictionModeFace", "Z,2", "Switch the Placement Restriction mode to the Face mode");
    public static final HotkeyConfig PLACEMENT_RESTRICTION_MODE_LAYER   = new HotkeyConfig("placementRestrictionModeLayer", "Z,6", "Switch the Placement Restriction mode to the Layer mode");
    public static final HotkeyConfig PLACEMENT_RESTRICTION_MODE_LINE    = new HotkeyConfig("placementRestrictionModeLine", "Z,4", "Switch the Placement Restriction mode to the Line mode");
    public static final HotkeyConfig PLACEMENT_RESTRICTION_MODE_PLANE   = new HotkeyConfig("placementRestrictionModePlane", "Z,1", "Switch the Placement Restriction mode to the Plane mode");
    public static final HotkeyConfig SKIP_ALL_RENDERING                 = new HotkeyConfig("skipAllRendering", "", "Toggles skipping _all_ rendering");
    public static final HotkeyConfig SKIP_WORLD_RENDERING               = new HotkeyConfig("skipWorldRendering", "", "Toggles skipping world rendering");
    public static final HotkeyConfig TOGGLE_GRAB_CURSOR                 = new HotkeyConfig("toggleGrabCursor", "", "Grabs or ungrabs the mouse cursor, depending on the current state");
    public static final HotkeyConfig TOOL_PICK                          = new HotkeyConfig("toolPick", "", "Switches to the effective tool for the targeted block");
    public static final HotkeyConfig ZOOM_ACTIVATE                      = new HotkeyConfig("zoomActivate", "", KeyBindSettings.create(Context.INGAME, KeyAction.BOTH, true, false, false, false, false), "Zoom activation hotkey");

    public static final List<HotkeyConfig> HOTKEY_LIST = ImmutableList.of(
            ACCURATE_BLOCK_PLACEMENT_IN,
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
            FREE_CAMERA_PLAYER_INPUTS,
            FREE_CAMERA_PLAYER_MOVEMENT,
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
            SKIP_ALL_RENDERING,
            SKIP_WORLD_RENDERING,
            TOGGLE_GRAB_CURSOR,
            TOOL_PICK,
            ZOOM_ACTIVATE
    );
}
