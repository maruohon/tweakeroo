package fi.dy.masa.tweakeroo.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IConfigOptionList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.tweakeroo.LiteModTweakeroo;
import fi.dy.masa.tweakeroo.util.PlacementTweaks.FastMode;

public enum ConfigsGeneric implements IConfigValue, IConfigBoolean, IConfigOptionList
{
    AFTER_CLICKER_CLICK_COUNT           ("afterClickerClickCount",  1, "The number of right clicks to do per placed block when\ntweakAfterClicker is enabled"),
    FAST_LEFT_CLICK_COUNT               ("fastLeftClickCount",  10, "The number of left clicks to do per game tick when\ntweakFastLeftClick is enabled and the attack button is held down"),
    FAST_RIGHT_CLICK_COUNT              ("fastRightClickCount", 10, "The number of right clicks to do per game tick when\ntweakFastRightClick is enabled and the use button is held down"),
    FAST_PLACEMENT_MODE                 ("fastPlacementMode", FastMode.FACE, "The Fast Block Placement mode to use (hotkey-selectable)"),
    FLEXIBLE_PLACEMENT_OVERLAY_COLOR    ("flexibleBlockPlacementOverlayColor", "#C03030F0", true, "The color of the currently pointed-at\nregion in block placement the overlay"),
    GAMMA_OVERRIDE_VALUE                ("gammaOverrideValue", 1000, "The gamma value to use when the override option is enabled"),
    HOTBAR_SWAP_OVERLAY_ALIGNMENT       ("hotbarSwapOverlayAlignment", HudAlignment.BOTTOM_RIGHT, "The positioning of the hotbar swap overlay"),
    HOTBAR_SWAP_OVERLAY_OFFSET_X        ("hotbarSwapOverlayOffsetX", 4, "The horizontal offset of the hotbar swap overlay"),
    HOTBAR_SWAP_OVERLAY_OFFSET_Y        ("hotbarSwapOverlayOffsetY", 4, "The vertical offset of the hotbar swap overlay"),
    ITEM_SWAP_DURABILITY_THRESHOLD      ("itemSwapDurabilityThreshold", 20, "This is the durability threshold (in uses left) for the low-durability item swap feature.\nNote that items with low total durability will go lower and be swapped at 5%% left."),
    LAVA_VISIBILITY_OPTIFINE            ("lavaVisibilityOptifineCompat", true, "Use an alternative version of the Lava Visibility,\nwhich is Optifine compatible (but more hacky).\nImplementation credit to Nessie."),
    PERMANENT_SNEAK_ALLOW_IN_GUIS       ("permanentSneakAllowInGUIs", false, "If true, then the permanent sneak tweak will also work while GUIs are open"),
    SLOT_SYNC_WORKAROUND                ("slotSyncWorkaround", true, "This prevents the server from overriding the durability or stack size on items\nthat are being used quickly for example with the fast right click tweak");

    private final String name;
    private final String prettyName;
    private final ConfigType type;
    private boolean defaultValueBoolean;
    private int defaultValueInteger;
    private String defaultValueString;
    private IConfigOptionListEntry defaultValueOptionList;
    private String comment;
    private boolean valueBoolean;
    private int valueInteger;
    private String valueString;
    private IConfigOptionListEntry valueOptionList;

    private ConfigsGeneric(String name, boolean defaultValue, String comment)
    {
        this.type = ConfigType.BOOLEAN;
        this.name = name;
        this.prettyName = name;
        this.valueBoolean = defaultValue;
        this.defaultValueBoolean = defaultValue;
        this.comment = comment;
    }

    private ConfigsGeneric(String name, int defaultValue, String comment)
    {
        this.type = ConfigType.INTEGER;
        this.name = name;
        this.prettyName = name;
        this.valueInteger = defaultValue;
        this.defaultValueInteger = defaultValue;
        this.comment = comment;
    }

    private ConfigsGeneric(String name, String defaultValue, boolean isColor, String comment)
    {
        this.type = ConfigType.HEX_STRING;
        this.name = name;
        this.prettyName = name;
        this.valueString = defaultValue;
        this.defaultValueString = defaultValue;
        this.valueInteger = getColor(defaultValue, 0);
        this.defaultValueInteger = this.valueInteger;
        this.comment = comment;
    }

    private ConfigsGeneric(String name, IConfigOptionListEntry defaultValue, String comment)
    {
        this.type = ConfigType.OPTION_LIST;
        this.name = name;
        this.prettyName = name;
        this.valueOptionList = defaultValue;
        this.defaultValueOptionList = defaultValue;
        this.comment = comment;
    }

    @Override
    public ConfigType getType()
    {
        return this.type;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getPrettyName()
    {
        return this.prettyName;
    }

    @Override
    @Nullable
    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
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

    public int getIntegerValue()
    {
        return this.valueInteger;
    }

    public void setIntegerValue(int value)
    {
        this.valueInteger = value;
    }

    @Override
    public IConfigOptionListEntry getOptionListValue()
    {
        return this.valueOptionList;
    }

    @Override
    public void setOptionListValue(IConfigOptionListEntry value)
    {
        this.valueOptionList = value;
    }

    @Override
    public boolean isModified()
    {
        switch (this.type)
        {
            case BOOLEAN:       return this.valueBoolean != this.defaultValueBoolean;
            case INTEGER:       return this.valueInteger != this.defaultValueInteger;
            case HEX_STRING:    return this.valueInteger != this.defaultValueInteger;
            case OPTION_LIST:   return this.valueOptionList != this.defaultValueOptionList;
            case STRING:        return this.valueString.equals(this.defaultValueString) == false;
            default:            return false;
        }
    }

    @Override
    public boolean isModified(String newValue)
    {
        switch (this.type)
        {
            case BOOLEAN:       return String.valueOf(this.defaultValueBoolean).equals(newValue) == false;
            case INTEGER:       return String.valueOf(this.defaultValueInteger).equals(newValue) == false;
            case HEX_STRING:    return String.format("0x%08X", this.defaultValueInteger).equals(newValue) == false;
            case OPTION_LIST:   return this.defaultValueOptionList.getStringValue().equals(newValue) == false;
            case STRING:
            default:            return this.defaultValueString.equals(newValue) == false;
        }
    }

    @Override
    public void resetToDefault()
    {
        switch (this.type)
        {
            case BOOLEAN:       this.valueBoolean = this.defaultValueBoolean;   break;
            case INTEGER:       this.valueInteger = this.defaultValueInteger;   break;
            case HEX_STRING:
                this.valueString = this.defaultValueString;
                this.valueInteger = this.defaultValueInteger;
                break;
            case OPTION_LIST:   this.valueOptionList = this.defaultValueOptionList; break;
            case STRING:        this.valueString = this.defaultValueString;     break;
            default:
        }
    }

    public String getStringValue()
    {
        switch (this.type)
        {
            case BOOLEAN:       return String.valueOf(this.valueBoolean);
            case INTEGER:       return String.valueOf(this.valueInteger);
            case HEX_STRING:    return String.format("0x%08X", this.valueInteger);
            case OPTION_LIST:   return this.valueOptionList.getStringValue();
            case STRING:
            default:            return this.valueString;
        }
    }

    public void setStringValue(String value)
    {
        this.valueString = value;
    }

    public void setColorValue(String str)
    {
        this.valueInteger = getColor(str, 0);
    }

    @Override
    public void setValueFromString(String value)
    {
        try
        {
            switch (this.type)
            {
                case BOOLEAN:
                    this.valueBoolean = Boolean.getBoolean(value);
                    break;
                case INTEGER:
                    this.valueInteger = Integer.parseInt(value);
                    break;
                case STRING:
                    this.valueString = value;
                    break;
                case HEX_STRING:
                    this.valueInteger = getColor(value, 0);
                    break;
                case OPTION_LIST:
                    this.valueOptionList = this.valueOptionList.fromString(value);
                    break;
                default:
            }
        }
        catch (Exception e)
        {
            LiteModTweakeroo.logger.warn("Failed to read config value for {} from the JSON config", this.getName(), e);
        }
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                switch (this.type)
                {
                    case BOOLEAN:
                        this.valueBoolean = element.getAsBoolean();
                        break;
                    case INTEGER:
                        this.valueInteger = element.getAsInt();
                        break;
                    case STRING:
                        this.valueString = element.getAsString();
                        break;
                    case HEX_STRING:
                        this.valueInteger = getColor(element.getAsString(), 0);
                        break;
                    case OPTION_LIST:
                        this.valueOptionList = this.valueOptionList.fromString(element.getAsString());
                        break;
                    default:
                }
            }
            else
            {
                LiteModTweakeroo.logger.warn("Failed to read config value for {} from the JSON config", this.getName());
            }
        }
        catch (Exception e)
        {
            LiteModTweakeroo.logger.warn("Failed to read config value for {} from the JSON config", this.getName(), e);
        }
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        switch (this.type)
        {
            case BOOLEAN:       return new JsonPrimitive(this.getBooleanValue());
            case INTEGER:       return new JsonPrimitive(this.getIntegerValue());
            case STRING:        return new JsonPrimitive(this.getStringValue());
            case HEX_STRING:    return new JsonPrimitive(String.format("0x%08X", this.getIntegerValue()));
            case OPTION_LIST:   return new JsonPrimitive(this.getStringValue());
            default:
        }

        return new JsonPrimitive(this.getStringValue());
    }

    public static int getColor(String colorStr, int defaultColor)
    {
        Pattern pattern = Pattern.compile("(?:0x|#)([a-fA-F0-9]{1,8})");
        Matcher matcher = pattern.matcher(colorStr);

        if (matcher.matches())
        {
            try { return (int) Long.parseLong(matcher.group(1), 16); }
            catch (NumberFormatException e) { return defaultColor; }
        }

        try { return Integer.parseInt(colorStr, 10); }
        catch (NumberFormatException e) { return defaultColor; }
    }
}
