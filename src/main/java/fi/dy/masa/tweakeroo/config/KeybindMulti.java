package fi.dy.masa.tweakeroo.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.tweakeroo.config.interfaces.IKeybind;

public class KeybindMulti implements IKeybind
{
    private static Set<Integer> pressedKeys = new HashSet<>();

    private List<Integer> keyCodes = new ArrayList<>(4);
    private boolean pressed;
    private int heldTime;
    @Nullable
    private IHotkeyCallback callback;

    @Override
    public void setCallback(@Nullable IHotkeyCallback callback)
    {
        this.callback = callback;
    }

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
            this.updateIsPressed();
            return this.pressed && this.heldTime == 0;
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

        for (int i = 0; i < this.keyCodes.size(); ++i)
        {
            int keyCode = this.keyCodes.get(i).intValue();

            if (keyCode > 0)
            {
                if (Keyboard.isKeyDown(keyCode))
                {
                    activeCount++;
                }
            }
            else
            {
                keyCode += 100;

                if (keyCode >= 0 && keyCode < Mouse.getButtonCount() && Mouse.isButtonDown(keyCode))
                {
                    activeCount++;
                }
            }
        }

        boolean pressedLast = this.pressed;
        this.pressed = pressedKeys.size() == activeCount && this.keyCodes.size() == activeCount;

        if (this.pressed == false)
        {
            this.heldTime = 0;

            if (pressedLast && this.callback != null)
            {
                this.callback.onKeyAction(KeyAction.RELEASE, this);
            }
        }
        else if (this.heldTime == 0 && this.callback != null)
        {
            this.callback.onKeyAction(KeyAction.PRESS, this);
        }
    }

    @Override
    public void clearKeys()
    {
        this.keyCodes.clear();
        this.pressed = false;
        this.heldTime = 0;
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
    public void tick()
    {
        if (this.pressed)
        {
            this.heldTime++;
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
        StringBuilder sb = new StringBuilder(32);

        for (int i = 0; i < this.keyCodes.size(); ++i)
        {
            if (i > 0)
            {
                sb.append(",");
            }

            int keyCode = this.keyCodes.get(i).intValue();

            if (keyCode > 0)
            {
                sb.append(Keyboard.getKeyName(keyCode));
            }
            else
            {
                keyCode += 100;

                if (keyCode >= 0 && keyCode < Mouse.getButtonCount())
                {
                    sb.append(Mouse.getButtonName(keyCode));
                }
            }
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
                    this.addKey(keyCode);
                    continue;
                }

                keyCode = Mouse.getButtonIndex(key);

                if (keyCode >= 0 && keyCode < Mouse.getButtonCount())
                {
                    this.addKey(keyCode - 100);
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

    public static boolean isKeyDown(int keyCode)
    {
        if (keyCode > 0)
        {
            return Keyboard.isKeyDown(keyCode);
        }

        keyCode += 100;

        return keyCode >= 0 && keyCode < Mouse.getButtonCount() && Mouse.isButtonDown(keyCode);
    }

    public static void onKeyInput(int keyCode, boolean state)
    {
        reCheckPressedKeys();

        if (state)
        {
            pressedKeys.add(keyCode);
        }
        else
        {
            pressedKeys.remove(keyCode);
        }
    }

    private static void reCheckPressedKeys()
    {
        Iterator<Integer> iter = pressedKeys.iterator();

        while (iter.hasNext())
        {
            int keyCode = iter.next().intValue();

            if (isKeyDown(keyCode) == false)
            {
                iter.remove();
            }
        }
    }

    public enum KeyAction
    {
        PRESS,
        RELEASE;
    }
}
