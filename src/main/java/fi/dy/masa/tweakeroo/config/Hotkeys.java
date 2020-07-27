package fi.dy.masa.tweakeroo.config;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.ConfigHotkey;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.input.KeyBindSettings.Context;

public class Hotkeys
{
    public static final ConfigHotkey ACCURATE_BLOCK_PLACEMENT_IN        = new ConfigHotkey("accurateBlockPlacementInto", "", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to activate the accurate block placement\nmode/overlay for placing the block facing\ninto the clicked block face");
    public static final ConfigHotkey ACCURATE_BLOCK_PLACEMENT_REVERSE   = new ConfigHotkey("accurateBlockPlacementReverse", "", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to activate the accurate block placement\nmode/overlay for placing the block facing\nthe opposite way from what it would normally be");
    public static final ConfigHotkey BLINK_DRIVE                        = new ConfigHotkey("blinkDrive",                        "",     "Teleports the player (using a tp command)\nto the location currently at the mouse cursor");
    public static final ConfigHotkey BLINK_DRIVE_Y_LEVEL                = new ConfigHotkey("blinkDriveYLevel",                  "",     "Teleports the player (using a tp command)\nto the location currently at the mouse cursor,\nbut maintaining the current y-position");
    public static final ConfigHotkey BREAKING_RESTRICTION_MODE_COLUMN   = new ConfigHotkey("breakingRestrictionModeColumn",     "",     "Switch the Breaking Restriction mode to the Column mode");
    public static final ConfigHotkey BREAKING_RESTRICTION_MODE_DIAGONAL = new ConfigHotkey("breakingRestrictionModeDiagonal",   "",     "Switch the Breaking Restriction mode to the Diagonal mode");
    public static final ConfigHotkey BREAKING_RESTRICTION_MODE_FACE     = new ConfigHotkey("breakingRestrictionModeFace",       "",     "Switch the Breaking Restriction mode to the Face mode");
    public static final ConfigHotkey BREAKING_RESTRICTION_MODE_LAYER    = new ConfigHotkey("breakingRestrictionModeLayer",      "",     "Switch the Breaking Restriction mode to the Layer mode");
    public static final ConfigHotkey BREAKING_RESTRICTION_MODE_LINE     = new ConfigHotkey("breakingRestrictionModeLine",       "",     "Switch the Breaking Restriction mode to the Line mode");
    public static final ConfigHotkey BREAKING_RESTRICTION_MODE_PLANE    = new ConfigHotkey("breakingRestrictionModePlane",      "",     "Switch the Breaking Restriction mode to the Plane mode");
    public static final ConfigHotkey COPY_SIGN_TEXT                     = new ConfigHotkey("copySignText",                      "",     "Copies the text from an already-placed sign.\nThat text can be used with the tweakSignCopy tweak.");
    public static final ConfigHotkey ELYTRA_CAMERA                      = new ConfigHotkey("elytraCamera", "", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to lock the current real player rotations, only allowing the\ninputs (mouse) to affect separate \"camera rotations\" used only for the rendering\nwhile this key is active.\nMeant for freely looking down/around while elytra flying straight.");
    public static final ConfigHotkey FLEXIBLE_BLOCK_PLACEMENT_ADJACENT  = new ConfigHotkey("flexibleBlockPlacementAdjacent", "", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to activate the flexible block placement\nmode/overlay for placing the block in an adjacent position");
    public static final ConfigHotkey FLEXIBLE_BLOCK_PLACEMENT_OFFSET    = new ConfigHotkey("flexibleBlockPlacementOffset", "LCONTROL", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to activate the flexible block placement\nmode/overlay for placing the block in a\noffset or diagonal position");
    public static final ConfigHotkey FLEXIBLE_BLOCK_PLACEMENT_ROTATION  = new ConfigHotkey("flexibleBlockPlacementRotation", "LMENU", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to activate the flexible block placement\nmode/overlay for placing the block with\na rotation/facing");
    public static final ConfigHotkey FLY_PRESET_1                       = new ConfigHotkey("flyPreset1", "", KeyBindSettings.INGAME_BOTH, "Switch to fly preset 1");
    public static final ConfigHotkey FLY_PRESET_2                       = new ConfigHotkey("flyPreset2", "", KeyBindSettings.INGAME_BOTH, "Switch to fly preset 2");
    public static final ConfigHotkey FLY_PRESET_3                       = new ConfigHotkey("flyPreset3", "", KeyBindSettings.INGAME_BOTH, "Switch to fly preset 3");
    public static final ConfigHotkey FLY_PRESET_4                       = new ConfigHotkey("flyPreset4", "", KeyBindSettings.INGAME_BOTH, "Switch to fly preset 4");
    public static final ConfigHotkey FREE_CAMERA_PLAYER_INPUTS          = new ConfigHotkey("freeCameraPlayerInputs",            "",     "Toggle the Generic -> freeCameraPlayerInputs option");
    public static final ConfigHotkey FREE_CAMERA_PLAYER_MOVEMENT        = new ConfigHotkey("freeCameraPlayerMovement",          "",     "Toggle the Generic -> freeCameraPlayerMovement option");
    public static final ConfigHotkey GHOST_BLOCK_REMOVER                = new ConfigHotkey("ghostBlockRemover",                 "",     "A \"manual ghost block fix\". Basically right clicks all the air blocks\nalong the look vector within the player's reach distance.");
    public static final ConfigHotkey HOTBAR_SCROLL                      = new ConfigHotkey("hotbarScroll", "", KeyBindSettings.RELEASE_ALLOW_EXTRA, "The key to hold to allow scrolling the hotbar\nthrough the player inventory rows");
    public static final ConfigHotkey HOTBAR_SWAP_BASE                   = new ConfigHotkey("hotbarSwapBase", "", KeyBindSettings.PRESS_ALLOWEXTRA, "The base key to show the hotbar/inventory overlay");
    public static final ConfigHotkey HOTBAR_SWAP_1                      = new ConfigHotkey("hotbarSwap1",                       "",     "Swap the hotbar with the top-most inventory row");
    public static final ConfigHotkey HOTBAR_SWAP_2                      = new ConfigHotkey("hotbarSwap2",                       "",     "Swap the hotbar with the middle inventory row");
    public static final ConfigHotkey HOTBAR_SWAP_3                      = new ConfigHotkey("hotbarSwap3",                       "",     "Swap the hotbar with the bottom-most inventory row");
    public static final ConfigHotkey INVENTORY_PREVIEW                  = new ConfigHotkey("inventoryPreview", "LMENU", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to activate the inventory preview feature");
    public static final ConfigHotkey OPEN_CONFIG_GUI                    = new ConfigHotkey("openConfigGui",                     "X,C",  "The key open the in-game config GUI");
    public static final ConfigHotkey PLACEMENT_Y_MIRROR                 = new ConfigHotkey("placementYMirror", "", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to mirror the targeted y-position within the block");
    public static final ConfigHotkey PLAYER_INVENTORY_PEEK              = new ConfigHotkey("playerInventoryPeek", "", KeyBindSettings.PRESS_ALLOWEXTRA, "The key to activate the player inventory peek/preview feature");
    public static final ConfigHotkey PLACEMENT_RESTRICTION_MODE_COLUMN  = new ConfigHotkey("placementRestrictionModeColumn",    "Z,3",  "Switch the Placement Restriction mode to the Column mode");
    public static final ConfigHotkey PLACEMENT_RESTRICTION_MODE_DIAGONAL= new ConfigHotkey("placementRestrictionModeDiagonal",  "Z,5",  "Switch the Placement Restriction mode to the Diagonal mode");
    public static final ConfigHotkey PLACEMENT_RESTRICTION_MODE_FACE    = new ConfigHotkey("placementRestrictionModeFace",      "Z,2",  "Switch the Placement Restriction mode to the Face mode");
    public static final ConfigHotkey PLACEMENT_RESTRICTION_MODE_LAYER   = new ConfigHotkey("placementRestrictionModeLayer",     "Z,6",  "Switch the Placement Restriction mode to the Layer mode");
    public static final ConfigHotkey PLACEMENT_RESTRICTION_MODE_LINE    = new ConfigHotkey("placementRestrictionModeLine",      "Z,4",  "Switch the Placement Restriction mode to the Line mode");
    public static final ConfigHotkey PLACEMENT_RESTRICTION_MODE_PLANE   = new ConfigHotkey("placementRestrictionModePlane",     "Z,1",  "Switch the Placement Restriction mode to the Plane mode");
    public static final ConfigHotkey SKIP_ALL_RENDERING                 = new ConfigHotkey("skipAllRendering",                  "",     "Toggles skipping _all_ rendering");
    public static final ConfigHotkey SKIP_WORLD_RENDERING               = new ConfigHotkey("skipWorldRendering",                "",     "Toggles skipping world rendering");
    public static final ConfigHotkey TOGGLE_GRAB_CURSOR                 = new ConfigHotkey("toggleGrabCursor",                  "",     "Grabs or ungrabs the mouse cursor, depending on the current state");
    public static final ConfigHotkey TOOL_PICK                          = new ConfigHotkey("toolPick",                          "",     "Switches to the effective tool for the targeted block");
    public static final ConfigHotkey ZOOM_ACTIVATE                      = new ConfigHotkey("zoomActivate", "", KeyBindSettings.create(Context.INGAME, KeyAction.BOTH, true, false, false, false, false), "Zoom activation hotkey");

    public static final List<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
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
