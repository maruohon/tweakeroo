package fi.dy.masa.tweakeroo.config.interfaces;

import java.util.Collection;
import javax.annotation.Nullable;
import fi.dy.masa.tweakeroo.config.IHotkeyCallback;

public interface IKeybind
{
    void setCallback(@Nullable IHotkeyCallback callback);

    boolean isValid();

    boolean isPressed();

    boolean isKeybindHeld();

    void clearKeys();

    void addKey(int keyCode);

    void removeKey(int keyCode);

    void tick();

    String getKeysDisplayString();

    String getStorageString();

    void setKeysFromStorageString(String key);

    Collection<Integer> getKeys();
}
