package fi.dy.masa.tweakeroo.config.interfaces;

public interface IConfig extends INamed
{
    ConfigType getType();

    String getStringValue();
}
