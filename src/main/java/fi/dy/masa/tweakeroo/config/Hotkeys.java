package fi.dy.masa.tweakeroo.config;

import fi.dy.masa.tweakeroo.config.gui.KeybindMulti;
import fi.dy.masa.tweakeroo.config.interfaces.IHotkey;
import fi.dy.masa.tweakeroo.config.interfaces.IKeybind;

public enum Hotkeys implements IHotkey
{
    HOTBAR_SWAP_BASE            ("hotbarSwapBase",  "C", "The base key to show the hotbar/inventory overlay"),
    HOTBAR_SWAP_1               ("hotbarSwap1",     "C,1", "Swap the hotbar with the top-most inventory row"),
    HOTBAR_SWAP_2               ("hotbarSwap2",     "C,2", "Swap the hotbar with the middle inventory row"),
    HOTBAR_SWAP_3               ("hotbarSwap3",     "C,3", "Swap the hotbar with the bottom-most inventory row");

    private final String name;
    private final String comment;
    private IKeybind keybind;

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
    public void setKeybind(IKeybind keybind)
    {
        this.keybind = keybind;
    }
}
