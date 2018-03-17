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
    private boolean active;

    @Override
    public boolean isValid()
    {
        return this.keyCodes.isEmpty() == false;
    }

    /**
     * Checks if this keybind is now active but previously was not active, and then updates the cached state.
     */
    @Override
    public boolean isPressed()
    {
        int activeCount = 0;

        for (Integer keyCode : this.keyCodes)
        {
            if (Keyboard.isKeyDown(keyCode))
            {
                activeCount++;
            }
        }

        boolean wasActive = this.active;
        this.active = activeCount == this.keyCodes.size();

        return wasActive == false && this.active;
    }

    @Override
    public boolean wasActive()
    {
        return this.active;
    }

    @Override
    public void clearKeys()
    {
        this.keyCodes.clear();
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
