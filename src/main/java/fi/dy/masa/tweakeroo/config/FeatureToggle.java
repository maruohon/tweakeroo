package fi.dy.masa.tweakeroo.config;

import fi.dy.masa.tweakeroo.config.Callbacks.KeyCallbackToggleFeatureWithMessage;
import fi.dy.masa.tweakeroo.config.interfaces.ConfigType;
import fi.dy.masa.tweakeroo.config.interfaces.IConfigBoolean;
import fi.dy.masa.tweakeroo.config.interfaces.IHotkey;
import fi.dy.masa.tweakeroo.config.interfaces.IKeybind;

public enum FeatureToggle implements IConfigBoolean, IHotkey
{
    TWEAK_AFTER_CLICKER             ("tweakAfterClicker",                   false, "X,C", "Enables a \"after clicker\" tweak, which does automatic right clicks on the just-placed block.\nUseful for example for Repeaters (setting the delay)."),
    TWEAK_FAST_BLOCK_PLACEMENT      ("tweakFastBlockPlacement",             false, "R",   "Enables fast/convenient block placement when moving the cursor over new blocks"),
    TWEAK_FAST_LEFT_CLICK           ("tweakFastLeftClick",                  false, "X,Y", "Enables automatic fast left clicking while holding down the attack button (left click).\nThe number of clicks per game tick is set in the Generic configs."),
    TWEAK_FAST_RIGHT_CLICK          ("tweakFastRightClick",                 false, "X,U", "Enables automatic fast right clicking while holding down the use button (right click).\nThe number of clicks per game tick is set in the Generic configs."),
    TWEAK_FLEXIBLE_BLOCK_PLACEMENT  ("tweakFlexibleBlockPlacement",         false, "X,L", "Enables placing blocks in different orientations while holding down the keybind"),
    TWEAK_GAMMA_OVERRIDE            ("tweakGammaOverride",                  false, "X,G", "Overrides the video settings gamma value with the one set in the Generic configs"),
    TWEAK_HAND_RESTOCK              ("tweakHandRestock",                    false, "X,E", "Enables swapping a new stack to the main or the offhand when the previous stack runs out"),
    TWEAK_HOTBAR_SWAP               ("tweakHotbarSwap",                     false, "X,H", "Enables the hotbar swapping feature"),
    TWEAK_INVENTORY_PREVIEW         ("tweakInventoryPreview",               false, "X,I", "Enables an inventory preview while having the cursor over a block with an inventory and holding the configured modifier key for it"),
    TWEAK_LAVA_VISIBILITY           ("tweakLavaVisibility",                 false, "X,A", "If enabled and the player has a Respiration helmet and/or Wather Breathing active, the lava fog is greatly reduced"),
    TWEAK_NO_FALLING_BLOCK_RENDER   ("tweakNoFallingBlockEntityRendering",  false, "X,R", "If enabled, then falling block entities won't be rendered at all"),
    TWEAK_NO_ITEM_SWITCH_COOLDOWN   ("tweakNoItemSwitchRenderCooldown",     false, "X,I", "If true, then there won't be any cooldown/equip\nanimation when switching the held item or using the item."),
    TWEAK_NO_LIGHT_UPDATES          ("tweakNoLightUpdates",                 false, "X,N", "If enabled, disables client-side light updates"),
    TWEAK_SHULKERBOX_STACKING       ("tweakEmptyShulkerBoxesStack",         false, "X,S", "Enables empty Shulker Boxes stacking up to 64");

    private final String name;
    private final String comment;
    private final String toggleMessage;
    private boolean valueBoolean;
    private IKeybind keybind;
    private IFeatureCallback callback;

    private FeatureToggle(String name, boolean defaultValue, String defaultHotkey, String comment)
    {
        this.name = name;
        this.valueBoolean = defaultValue;
        this.comment = comment;
        this.toggleMessage = splitCamelCase(this.name.substring(5));
        this.keybind = KeybindMulti.fromStorageString(defaultHotkey);
        this.keybind.setCallback(new KeyCallbackToggleFeatureWithMessage(this));
    }

    public void setCallback(IFeatureCallback callback)
    {
        this.callback = callback;
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
    public IKeybind getKeybind()
    {
        return this.keybind;
    }

    @Override
    public void setKeybind(IKeybind keybind)
    {
        this.keybind = keybind;
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.valueBoolean;
    }

    @Override
    public void setBooleanValue(boolean value)
    {
        boolean oldValue = this.valueBoolean;
        this.valueBoolean = value;

        if (this.callback != null && oldValue != this.valueBoolean)
        {
            this.callback.onValueChange(this);
        }
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
