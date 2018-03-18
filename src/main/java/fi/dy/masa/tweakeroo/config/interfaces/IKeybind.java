package fi.dy.masa.tweakeroo.config.interfaces;

import java.util.Collection;

public interface IKeybind
{
    boolean isValid();

    boolean isPressed();

    boolean isKeybindHeld(boolean checkNow);

    void clearKeys();

    void addKey(int keyCode);

    void removeKey(int keyCode);

    void tick();

    String getKeysDisplayString();

    String getStorageString();

    void setKeysFromStorageString(String key);

    Collection<Integer> getKeys();
}
