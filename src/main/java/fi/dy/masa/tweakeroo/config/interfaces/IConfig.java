package fi.dy.masa.tweakeroo.config.interfaces;

import javax.annotation.Nullable;

public interface IConfig
{
    ConfigType getType();

    String getName();

    @Nullable
    String getComment();

    String getStringValue();
}
