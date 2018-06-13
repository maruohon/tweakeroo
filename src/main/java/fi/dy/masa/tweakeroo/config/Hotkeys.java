package fi.dy.masa.tweakeroo.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.tweakeroo.LiteModTweakeroo;

public enum Hotkeys implements IHotkey
{
    FAST_MODE_PLANE             ("fastPlacementModePlane",  "Z,1",  "Switch the Fast Placement mode to the Plane mode"),
    FAST_MODE_FACE              ("fastPlacementModeFace",   "Z,2",  "Switch the Fast Placement mode to the Face mode"),
    FAST_MODE_COLUMN            ("fastPlacementModeColumn", "Z,3",  "Switch the Fast Placement mode to the Column mode"),
    FLEXIBLE_BLOCK_PLACEMENT_OFFSET     ("flexibleBlockPlacementOffset", "LCONTROL", "The key to activate the flexible block placement\nmode/overlay for placing the block in a offset or diagonal position"),
    FLEXIBLE_BLOCK_PLACEMENT_ROTATION   ("flexibleBlockPlacementRotation", "LMENU",  "The key to activate the flexible block placement\nmode/overlay for placing the block with a rotation/facing"),
    HOTBAR_SWAP_BASE            ("hotbarSwapBase",  "C",    "The base key to show the hotbar/inventory overlay"),
    HOTBAR_SWAP_1               ("hotbarSwap1",     "C,1",  "Swap the hotbar with the top-most inventory row"),
    HOTBAR_SWAP_2               ("hotbarSwap2",     "C,2",  "Swap the hotbar with the middle inventory row"),
    HOTBAR_SWAP_3               ("hotbarSwap3",     "C,3",  "Swap the hotbar with the bottom-most inventory row"),
    INVENTORY_PREVIEW           ("inventoryPreview", "LMENU",  "The key to activate the inventory preview feature"),
    SKIP_ALL_RENDERING          ("skipAllRendering",   "LSHIFT,X,DELETE",  "Toggles skipping _all_ rendering"),
    SKIP_WORLD_RENDERING        ("skipWorldRendering", "X,DELETE",  "Toggles skipping world rendering");

    private final String name;
    private final String comment;
    private final IKeybind keybind;

    private Hotkeys(String name, String defaultHotkey, String comment)
    {
        this.name = name;
        this.comment = comment;
        this.keybind = KeybindMulti.fromStorageString(defaultHotkey);
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
        return new JsonPrimitive(this.keybind.getStorageString());
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.keybind.setKeysFromStorageString(element.getAsString());
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
