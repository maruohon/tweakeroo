package fi.dy.masa.tweakeroo.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.tweakeroo.LiteModTweakeroo;
import fi.dy.masa.tweakeroo.config.interfaces.ConfigType;
import fi.dy.masa.tweakeroo.config.interfaces.IConfigBoolean;
import fi.dy.masa.tweakeroo.config.interfaces.IConfigGeneric;
import fi.dy.masa.tweakeroo.config.interfaces.IConfigOptionList;
import fi.dy.masa.tweakeroo.config.interfaces.IConfigOptionListEntry;

public enum ConfigsGeneric implements IConfigGeneric, IConfigBoolean, IConfigOptionList
{
    FLEXIBLE_PLACEMENT_OVERLAY_COLOR    ("flexibleBlockPlacementOverlayColor", "#FF3030FF", true, "The color of the currently pointed-at\nregion in block placement the overlay"),
    HOTBAR_SWAP_OVERLAY_ALIGNMENT       ("hotbarSwapOverlayAlignment", HudAlignment.BOTTOM_RIGHT, "The positioning of the hotbar swap overlay"),
    HOTBAR_SWAP_OVERLAY_OFFSET_X        ("hotbarSwapOverlayOffsetX", 4, "The horizontal offset of the hotbar swap overlay"),
    HOTBAR_SWAP_OVERLAY_OFFSET_Y        ("hotbarSwapOverlayOffsetY", 4, "The vertical offset of the hotbar swap overlay");

    private final String name;
    private final ConfigType type;
    private String comment;
    private boolean valueBoolean;
    private int valueInteger;
    private String valueString;
    private IConfigOptionListEntry valueOptionList;

    private ConfigsGeneric(String name, boolean defaultValue, String comment)
    {
        this.type = ConfigType.BOOLEAN;
        this.name = name;
        this.valueBoolean = defaultValue;
        this.comment = comment;
    }

    private ConfigsGeneric(String name, int defaultValue, String comment)
    {
        this.type = ConfigType.INTEGER;
        this.name = name;
        this.valueInteger = defaultValue;
        this.comment = comment;
    }

    private ConfigsGeneric(String name, String defaultValue, boolean isColor, String comment)
    {
        this.type = ConfigType.HEX_STRING;
        this.name = name;
        this.valueString = defaultValue;
        this.valueInteger = getColor(defaultValue, 0);
        this.comment = comment;
    }

    private ConfigsGeneric(String name, IConfigOptionListEntry defaultValue, String comment)
    {
        this.type = ConfigType.OPTION_LIST;
        this.name = name;
        this.valueOptionList = defaultValue;
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

    public void setValueFromJsonPrimitive(JsonPrimitive value)
    {
        try
        {
            switch (this.type)
            {
                case BOOLEAN:
                    this.valueBoolean = value.getAsBoolean();
                    break;
                case INTEGER:
                    this.valueInteger = value.getAsInt();
                    break;
                case STRING:
                    this.valueString = value.getAsString();
                    break;
                case HEX_STRING:
                    this.valueInteger = getColor(value.getAsString(), 0);
                    break;
                case OPTION_LIST:
                    this.valueOptionList = this.valueOptionList.fromString(value.getAsString());
                    break;
                default:
            }
        }
        catch (Exception e)
        {
            LiteModTweakeroo.logger.warn("Failed to read config value for {} from the JSON config", this.getName(), e);
        }
    }

    public JsonPrimitive getAsJsonPrimitive()
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
