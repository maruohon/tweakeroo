package fi.dy.masa.tweakeroo.config;

import fi.dy.masa.tweakeroo.config.KeybindMulti.KeyAction;
import fi.dy.masa.tweakeroo.config.interfaces.IKeybind;

public interface IHotkeyCallback
{
    /**
     * Called when a hotkey action happens.
     * @param action
     * @param key
     */
    void onKeyAction(KeyAction action, IKeybind key);
}
