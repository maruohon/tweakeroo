package fi.dy.masa.tweakeroo.config;

import fi.dy.masa.tweakeroo.config.gui.KeybindMulti;
import fi.dy.masa.tweakeroo.config.interfaces.ConfigType;
import fi.dy.masa.tweakeroo.config.interfaces.IConfigBoolean;
import fi.dy.masa.tweakeroo.config.interfaces.IConfigHotkey;
import fi.dy.masa.tweakeroo.config.interfaces.IKeybind;

public enum FeatureToggle implements IConfigBoolean, IConfigHotkey
{
    TWEAK_SHULKERBOX_STACKING       ("tweakEmptyShulkerBoxesStack",         false, 0x0001, "X,H", "Enables empty Shulker Boxes stacking up to 64"),
    TWEAK_FAST_BLOCK_PLACEMENT      ("tweakFastBlockPlacement",             false, 0x0002, "X,F", "Enables fast/convenient block placement when moving the cursor over new blocks"),
    TWEAK_FLEXIBLE_BLOCK_PLACEMENT  ("tweakFlexibleBlockPlacement",         false, 0x0004, "X,L", "Enables placing blocks in different orientations while holding down the keybind"),
    TWEAK_NO_FALLING_BLOCK_RENDER   ("tweakNoFallingBlockEntityRendering",  false, 0x0008, "X,R", "If enabled, then falling block entities won't be rendered at all"),
    TWEAK_NO_ITEM_SWITCH_COOLDOWN   ("tweakNoItemSwitchRenderCooldown",     false, 0x0010, "X,I", "If true, then there won't be any cooldown/equip\nanimation when switching the held item or using the item.");

    private final String name;
    private final String comment;
    private final String toggleMessage;
    private final int bitMask;
    private boolean valueBoolean;
    private IKeybind keybind;

    private FeatureToggle(String name, boolean defaultValue, int bitMask, String defaultHotkey, String comment)
    {
        this.name = name;
        this.valueBoolean = defaultValue;
        this.bitMask = bitMask;
        this.comment = comment;
        this.toggleMessage = "Toggled " + splitCamelCase(this.name.substring(5)) + " %s";
        this.keybind = KeybindMulti.fromStorageString(defaultHotkey);
    }

    @Override
    public ConfigType getType()
    {
        return ConfigType.BOOLEAN;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getStringValue()
    {
        return String.valueOf(this.valueBoolean);
    }

    @Override
    public String getComment()
    {
        return comment != null ? comment : "";
    }

    public String getToggleMessage()
    {
        return this.toggleMessage;
    }

    @Override
    public int getBitMask()
    {
        return this.bitMask;
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

    /*
    @Override
    public String getHotkey()
    {
        return this.hotkey;
    }

    @Override
    public void setHotkey(String hotkey)
    {
        this.hotkey = hotkey;
    }
    */

    public int applyBitMask(int mask)
    {
        if (this.valueBoolean)
        {
            mask |= this.bitMask;
        }
        else
        {
            mask &= ~this.bitMask;
        }

        return mask;
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.valueBoolean;
    }

    @Override
    public void setBooleanValue(boolean value)
    {
        this.valueBoolean = value;
    }

    // https://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java
    public static String splitCamelCase(String str)
    {
        return str.replaceAll(
           String.format("%s|%s|%s",
              "(?<=[A-Z])(?=[A-Z][a-z])",
              "(?<=[^A-Z])(?=[A-Z])",
              "(?<=[A-Za-z])(?=[^A-Za-z])"
           ),
           " "
        );
     }
}
