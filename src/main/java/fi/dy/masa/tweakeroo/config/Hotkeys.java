package fi.dy.masa.tweakeroo.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.tweakeroo.LiteModTweakeroo;

public enum Hotkeys implements IHotkey
{
    ACCURATE_BLOCK_PLACEMENT_IN         ("accurateBlockPlacementInto",      "LMENU",    KeybindSettings.PRESS_ALLOWEXTRA, "The key to activate the accurate block placement mode/overlay\nfor placing the block facing into the clicked block face"),
    ACCURATE_BLOCK_PLACEMENT_REVERSE    ("accurateBlockPlacementReverse",   "LCONTROL", KeybindSettings.PRESS_ALLOWEXTRA, "The key to activate the accurate block placement mode/overlay\nfor placing the block facing the opposite way from what it would normally be"),
    RESTRICTION_MODE_PLANE              ("placementRestrictionModePlane",   "Z,1",  "Switch the Placement Restriction mode to the Plane mode"),
    RESTRICTION_MODE_FACE               ("placementRestrictionModeFace",    "Z,2",  "Switch the Placement Restriction mode to the Face mode"),
    RESTRICTION_MODE_COLUMN             ("placementRestrictionModeColumn",  "Z,3",  "Switch the Placement Restriction mode to the Column mode"),
    RESTRICTION_MODE_LINE               ("placementRestrictionModeLine",    "Z,4",  "Switch the Placement Restriction mode to the Line mode"),
    RESTRICTION_MODE_DIAGONAL           ("placementRestrictionModeDiagonal","Z,5",  "Switch the Placement Restriction mode to the Diagonal mode"),
    FLEXIBLE_BLOCK_PLACEMENT_OFFSET     ("flexibleBlockPlacementOffset",    "LCONTROL", KeybindSettings.PRESS_ALLOWEXTRA, "The key to activate the flexible block placement\nmode/overlay for placing the block in a offset or diagonal position"),
    FLEXIBLE_BLOCK_PLACEMENT_ROTATION   ("flexibleBlockPlacementRotation",  "LMENU",    KeybindSettings.PRESS_ALLOWEXTRA, "The key to activate the flexible block placement\nmode/overlay for placing the block with a rotation/facing"),
    HOTBAR_SCROLL                       ("hotbarScroll",                    "V",        KeybindSettings.PRESS_ALLOWEXTRA, "The key to hold to allow scrolling the hotbar through the player inventory rows"),
    HOTBAR_SWAP_BASE                    ("hotbarSwapBase",                  "C",        KeybindSettings.PRESS_ALLOWEXTRA, "The base key to show the hotbar/inventory overlay"),
    HOTBAR_SWAP_1                       ("hotbarSwap1",                     "C,1",  "Swap the hotbar with the top-most inventory row"),
    HOTBAR_SWAP_2                       ("hotbarSwap2",                     "C,2",  "Swap the hotbar with the middle inventory row"),
    HOTBAR_SWAP_3                       ("hotbarSwap3",                     "C,3",  "Swap the hotbar with the bottom-most inventory row"),
    INVENTORY_PREVIEW                   ("inventoryPreview",                "LMENU", KeybindSettings.PRESS_ALLOWEXTRA, "The key to activate the inventory preview feature"),
    PLAYER_INVENTORY_PEEK               ("playerInventoryPeek",             "",      KeybindSettings.PRESS_ALLOWEXTRA, "The key to activate the player inventory peek/preview feature"),
    SKIP_ALL_RENDERING                  ("skipAllRendering",                "LSHIFT,X,DELETE",  "Toggles skipping _all_ rendering"),
    SKIP_WORLD_RENDERING                ("skipWorldRendering",              "X,DELETE",  "Toggles skipping world rendering");

    private final String name;
    private final String comment;
    private final IKeybind keybind;

    private Hotkeys(String name, String defaultHotkey, String comment)
    {
        this(name, defaultHotkey, KeybindSettings.DEFAULT, comment);
    }

    private Hotkeys(String name, String defaultHotkey, KeybindSettings settings, String comment)
    {
        this.name = name;
        this.comment = comment;
        this.keybind = KeybindMulti.fromStorageString(defaultHotkey, settings);
    }

    @Override
    public ConfigType getType()
    {
        return ConfigType.HOTKEY;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getComment()
    {
        return comment != null ? this.comment : "";
    }

    @Override
    public IKeybind getKeybind()
    {
        return this.keybind;
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.keybind.getStringValue());
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.keybind.setValueFromString(element.getAsString());
            }
            else
            {
                LiteModTweakeroo.logger.warn("Failed to set the keybinds for '{}' from the JSON element '{}'", this.getName(), element);
            }
        }
        catch (Exception e)
        {
            LiteModTweakeroo.logger.warn("Failed to set the keybinds for '{}' from the JSON element '{}'", this.getName(), element, e);
        }
    }
}
