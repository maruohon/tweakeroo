package fi.dy.masa.tweakeroo.config.interfaces;

public interface IConfigOptionListEntry
{
    String getStringValue();

    String getDisplayName();

    int getOrdinalValue();

    IConfigOptionListEntry cycle(boolean forward);

    IConfigOptionListEntry fromString(String value);
}
