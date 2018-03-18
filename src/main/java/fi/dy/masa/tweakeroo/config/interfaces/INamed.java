package fi.dy.masa.tweakeroo.config.interfaces;

import javax.annotation.Nullable;

public interface INamed
{
    String getName();

    @Nullable
    String getComment();
}
