package fi.dy.masa.tweakeroo.config;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.tweakeroo.Reference;

public class Hotkeys
{
    public static final String PREFIX = String.format("%s.label.config_gui.hotkeys", Reference.MOD_ID);
    public static final ConfigHotkey ACCURATE_BLOCK_PLACEMENT_IN        = new MyConfigHotkey(PREFIX, "accurateBlockPlacementInto",        "",     KeybindSettings.PRESS_ALLOWEXTRA);
    public static final ConfigHotkey ACCURATE_BLOCK_PLACEMENT_REVERSE   = new MyConfigHotkey(PREFIX, "accurateBlockPlacementReverse",     "",     KeybindSettings.PRESS_ALLOWEXTRA);
    public static final ConfigHotkey BREAKING_RESTRICTION_MODE_COLUMN   = new MyConfigHotkey(PREFIX, "breakingRestrictionModeColumn",     "");
    public static final ConfigHotkey BREAKING_RESTRICTION_MODE_DIAGONAL = new MyConfigHotkey(PREFIX, "breakingRestrictionModeDiagonal",   "");
    public static final ConfigHotkey BREAKING_RESTRICTION_MODE_FACE     = new MyConfigHotkey(PREFIX, "breakingRestrictionModeFace",       "");
    public static final ConfigHotkey BREAKING_RESTRICTION_MODE_LAYER    = new MyConfigHotkey(PREFIX, "breakingRestrictionModeLayer",      "");
    public static final ConfigHotkey BREAKING_RESTRICTION_MODE_LINE     = new MyConfigHotkey(PREFIX, "breakingRestrictionModeLine",       "");
    public static final ConfigHotkey BREAKING_RESTRICTION_MODE_PLANE    = new MyConfigHotkey(PREFIX, "breakingRestrictionModePlane",      "");
    public static final ConfigHotkey COPY_SIGN_TEXT                     = new MyConfigHotkey(PREFIX, "copySignText",                      "");
    public static final ConfigHotkey ELYTRA_CAMERA                      = new MyConfigHotkey(PREFIX, "elytraCamera",                      "");
    public static final ConfigHotkey FLEXIBLE_BLOCK_PLACEMENT_ADJACENT  = new MyConfigHotkey(PREFIX, "flexibleBlockPlacementAdjacent",    "",     KeybindSettings.PRESS_ALLOWEXTRA);
    public static final ConfigHotkey FLEXIBLE_BLOCK_PLACEMENT_OFFSET    = new MyConfigHotkey(PREFIX, "flexibleBlockPlacementOffset",      "LEFT_CONTROL", KeybindSettings.PRESS_ALLOWEXTRA);
    public static final ConfigHotkey FLEXIBLE_BLOCK_PLACEMENT_ROTATION  = new MyConfigHotkey(PREFIX, "flexibleBlockPlacementRotation",    "LEFT_ALT", KeybindSettings.PRESS_ALLOWEXTRA);
    public static final ConfigHotkey FLY_PRESET_1                       = new MyConfigHotkey(PREFIX, "flyPreset1",                        "");
    public static final ConfigHotkey FLY_PRESET_2                       = new MyConfigHotkey(PREFIX, "flyPreset2",                        "");
    public static final ConfigHotkey FLY_PRESET_3                       = new MyConfigHotkey(PREFIX, "flyPreset3",                        "");
    public static final ConfigHotkey FLY_PRESET_4                       = new MyConfigHotkey(PREFIX, "flyPreset4",                        "");
    public static final ConfigHotkey HOTBAR_SCROLL                      = new MyConfigHotkey(PREFIX, "hotbarScroll",                      "",     KeybindSettings.RELEASE_ALLOW_EXTRA);
    public static final ConfigHotkey HOTBAR_SWAP_BASE                   = new MyConfigHotkey(PREFIX, "hotbarSwapBase",                    "",     KeybindSettings.PRESS_ALLOWEXTRA);
    public static final ConfigHotkey HOTBAR_SWAP_1                      = new MyConfigHotkey(PREFIX, "hotbarSwap1",                       "");
    public static final ConfigHotkey HOTBAR_SWAP_2                      = new MyConfigHotkey(PREFIX, "hotbarSwap2",                       "");
    public static final ConfigHotkey HOTBAR_SWAP_3                      = new MyConfigHotkey(PREFIX, "hotbarSwap3",                       "");
    public static final ConfigHotkey INVENTORY_PREVIEW                  = new MyConfigHotkey(PREFIX, "inventoryPreview",                  "LEFT_ALT", KeybindSettings.PRESS_ALLOWEXTRA);
    public static final ConfigHotkey OPEN_CONFIG_GUI                    = new MyConfigHotkey(PREFIX, "openConfigGui",                     "X,C");
    public static final ConfigHotkey PLACEMENT_Y_MIRROR                 = new MyConfigHotkey(PREFIX, "placementYMirror",                  "",     KeybindSettings.PRESS_ALLOWEXTRA);
    public static final ConfigHotkey PLAYER_INVENTORY_PEEK              = new MyConfigHotkey(PREFIX, "playerInventoryPeek",               "",     KeybindSettings.PRESS_ALLOWEXTRA);
    public static final ConfigHotkey PLACEMENT_RESTRICTION_MODE_COLUMN  = new MyConfigHotkey(PREFIX, "placementRestrictionModeColumn",    "Z,3");
    public static final ConfigHotkey PLACEMENT_RESTRICTION_MODE_DIAGONAL= new MyConfigHotkey(PREFIX, "placementRestrictionModeDiagonal",  "Z,5");
    public static final ConfigHotkey PLACEMENT_RESTRICTION_MODE_FACE    = new MyConfigHotkey(PREFIX, "placementRestrictionModeFace",      "Z,2");
    public static final ConfigHotkey PLACEMENT_RESTRICTION_MODE_LAYER   = new MyConfigHotkey(PREFIX, "placementRestrictionModeLayer",     "Z,6");
    public static final ConfigHotkey PLACEMENT_RESTRICTION_MODE_LINE    = new MyConfigHotkey(PREFIX, "placementRestrictionModeLine",      "Z,4");
    public static final ConfigHotkey PLACEMENT_RESTRICTION_MODE_PLANE   = new MyConfigHotkey(PREFIX, "placementRestrictionModePlane",     "Z,1");
    public static final ConfigHotkey SKIP_ALL_RENDERING                 = new MyConfigHotkey(PREFIX, "skipAllRendering",                  "");
    public static final ConfigHotkey SKIP_WORLD_RENDERING               = new MyConfigHotkey(PREFIX, "skipWorldRendering",                "");
    public static final ConfigHotkey TOOL_PICK                          = new MyConfigHotkey(PREFIX, "toolPick",                          "");
    public static final ConfigHotkey ZOOM_ACTIVATE                      = new MyConfigHotkey(PREFIX, "zoomActivate",                      "");
    public static final List<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
            ACCURATE_BLOCK_PLACEMENT_IN,
            ACCURATE_BLOCK_PLACEMENT_REVERSE,
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
            TOOL_PICK,
            ZOOM_ACTIVATE
    );
}
