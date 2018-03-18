package fi.dy.masa.tweakeroo.config.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.lwjgl.input.Keyboard;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.tweakeroo.config.interfaces.IKeybind;

public class KeybindMulti implements IKeybind
{
    private List<Integer> keyCodes = new ArrayList<>(4);
    private boolean pressed;

    @Override
    public boolean isValid()
    {
        return this.keyCodes.isEmpty() == false;
    }

    /**
     * Checks if this keybind is now active but previously was not active,
     * and then updates the cached state.
     * @return true if this keybind just became pressed
     */
    @Override
    public boolean isPressed()
    {
        if (this.isValid())
        {
            boolean wasActive = this.pressed;
            this.updateIsPressed();

            return this.pressed && wasActive == false;
        }
        else
        {
            this.pressed = false;
            return false;
        }
    }

    /**
     * Returns whether the keybind is being held down.
     * @return
     */
    @Override
    public boolean isKeybindHeld()
    {
        return this.pressed;
    }

    private void updateIsPressed()
    {
        int activeCount = 0;

        for (Integer keyCode : this.keyCodes)
        {
            if (Keyboard.isKeyDown(keyCode))
            {
                activeCount++;
            }
        }

        this.pressed = activeCount == this.keyCodes.size();
    }

    @Override
    public void clearKeys()
    {
        this.keyCodes.clear();
        this.pressed = false;
    }

    @Override
    public void addKey(int keyCode)
    {
        if (this.keyCodes.contains(keyCode) == false)
        {
            this.keyCodes.add(keyCode);
        }
    }

    @Override
    public void removeKey(int keyCode)
    {
        this.keyCodes.remove(keyCode);
    }

    @Override
    public Collection<Integer> getKeys()
    {
        return ImmutableList.copyOf(this.keyCodes);
    }

    @Override
    public String getKeysDisplayString()
    {
        return this.getStorageString().replaceAll(",", " + ");
    }

    @Override
    public String getStorageString()
    {
        StringBuilder sb = new StringBuilder(16);
        int i = 0;

        for (Integer keyCode : this.keyCodes)
        {
            if (i > 0)
            {
                sb.append(",");
            }

            sb.append(Keyboard.getKeyName(keyCode));
            i++;
        }

        return sb.toString();
    }

    @Override
    public void setKeysFromStorageString(String str)
    {
        this.clearKeys();
        String[] keys = str.split(",");

        for (String key : keys)
        {
            key = key.trim();

            if (key.isEmpty() == false)
            {
                int keyCode = Keyboard.getKeyIndex(key);

                if (keyCode != Keyboard.KEY_NONE)
                {
                    this.keyCodes.add(keyCode);
                }
            }
        }
    }

    public static KeybindMulti fromStorageString(String str)
    {
        KeybindMulti keybind = new KeybindMulti();
        keybind.setKeysFromStorageString(str);
        return keybind;
    }
}
