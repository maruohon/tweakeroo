package fi.dy.masa.tweakeroo.config;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;

public class Hotkeys
{
    public static final ConfigHotkey ACCURATE_BLOCK_PLACEMENT_IN        = new ConfigHotkey("accurateBlockPlacementInto",        "LMENU",    KeybindSettings.PRESS_ALLOWEXTRA, "The key to activate the accurate block placement\nmode/overlay for placing the block facing\ninto the clicked block face");
    public static final ConfigHotkey ACCURATE_BLOCK_PLACEMENT_REVERSE   = new ConfigHotkey("accurateBlockPlacementReverse",     "LCONTROL", KeybindSettings.PRESS_ALLOWEXTRA, "The key to activate the accurate block placement\nmode/overlay for placing the block facing\nthe opposite way from what it would normally be");
    public static final ConfigHotkey RESTRICTION_MODE_PLANE             = new ConfigHotkey("placementRestrictionModePlane",     "Z,1",  "Switch the Placement Restriction mode to the Plane mode");
    public static final ConfigHotkey RESTRICTION_MODE_FACE              = new ConfigHotkey("placementRestrictionModeFace",      "Z,2",  "Switch the Placement Restriction mode to the Face mode");
    public static final ConfigHotkey RESTRICTION_MODE_COLUMN            = new ConfigHotkey("placementRestrictionModeColumn",    "Z,3",  "Switch the Placement Restriction mode to the Column mode");
    public static final ConfigHotkey RESTRICTION_MODE_LINE              = new ConfigHotkey("placementRestrictionModeLine",      "Z,4",  "Switch the Placement Restriction mode to the Line mode");
    public static final ConfigHotkey RESTRICTION_MODE_DIAGONAL          = new ConfigHotkey("placementRestrictionModeDiagonal",  "Z,5",  "Switch the Placement Restriction mode to the Diagonal mode");
    public static final ConfigHotkey FLEXIBLE_BLOCK_PLACEMENT_OFFSET    = new ConfigHotkey("flexibleBlockPlacementOffset",      "LCONTROL", KeybindSettings.PRESS_ALLOWEXTRA, "The key to activate the flexible block placement\nmode/overlay for placing the block in a\noffset or diagonal position");
    public static final ConfigHotkey FLEXIBLE_BLOCK_PLACEMENT_ROTATION  = new ConfigHotkey("flexibleBlockPlacementRotation",    "LMENU",    KeybindSettings.PRESS_ALLOWEXTRA, "The key to activate the flexible block placement\nmode/overlay for placing the block with\na rotation/facing");
    public static final ConfigHotkey HOTBAR_SCROLL                      = new ConfigHotkey("hotbarScroll",                      "V",        KeybindSettings.RELEASE_ALLOW_EXTRA, "The key to hold to allow scrolling the hotbar\nthrough the player inventory rows");
    public static final ConfigHotkey HOTBAR_SWAP_BASE                   = new ConfigHotkey("hotbarSwapBase",                    "C",        KeybindSettings.PRESS_ALLOWEXTRA, "The base key to show the hotbar/inventory overlay");
    public static final ConfigHotkey HOTBAR_SWAP_1                      = new ConfigHotkey("hotbarSwap1",                       "C,1",  "Swap the hotbar with the top-most inventory row");
    public static final ConfigHotkey HOTBAR_SWAP_2                      = new ConfigHotkey("hotbarSwap2",                       "C,2",  "Swap the hotbar with the middle inventory row");
    public static final ConfigHotkey HOTBAR_SWAP_3                      = new ConfigHotkey("hotbarSwap3",                       "C,3",  "Swap the hotbar with the bottom-most inventory row");
    public static final ConfigHotkey INVENTORY_PREVIEW                  = new ConfigHotkey("inventoryPreview",                  "LMENU", KeybindSettings.PRESS_ALLOWEXTRA, "The key to activate the inventory preview feature");
    public static final ConfigHotkey OPEN_CONFIG_GUI                    = new ConfigHotkey("openConfigGui",                     "",      "The key open the in-game config GUI");
    public static final ConfigHotkey PLAYER_INVENTORY_PEEK              = new ConfigHotkey("playerInventoryPeek",               "Q",     KeybindSettings.PRESS_ALLOWEXTRA, "The key to activate the player inventory peek/preview feature");
    public static final ConfigHotkey SKIP_ALL_RENDERING                 = new ConfigHotkey("skipAllRendering",                  "LSHIFT,X,DELETE",  "Toggles skipping _all_ rendering");
    public static final ConfigHotkey SKIP_WORLD_RENDERING               = new ConfigHotkey("skipWorldRendering",                "X,DELETE",  "Toggles skipping world rendering");

    public static final List<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
            ACCURATE_BLOCK_PLACEMENT_IN,
            ACCURATE_BLOCK_PLACEMENT_REVERSE,
            RESTRICTION_MODE_PLANE,
            RESTRICTION_MODE_FACE,
            RESTRICTION_MODE_COLUMN,
            RESTRICTION_MODE_LINE,
            RESTRICTION_MODE_DIAGONAL,
            FLEXIBLE_BLOCK_PLACEMENT_OFFSET,
            FLEXIBLE_BLOCK_PLACEMENT_ROTATION,
            HOTBAR_SCROLL,
            HOTBAR_SWAP_BASE,
            HOTBAR_SWAP_1,
            HOTBAR_SWAP_2,
            HOTBAR_SWAP_3,
            INVENTORY_PREVIEW,
            OPEN_CONFIG_GUI,
            PLAYER_INVENTORY_PEEK,
            SKIP_ALL_RENDERING,
            SKIP_WORLD_RENDERING
    );
}
