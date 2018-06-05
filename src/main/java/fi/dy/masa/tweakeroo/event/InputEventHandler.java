package fi.dy.masa.tweakeroo.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import fi.dy.masa.tweakeroo.config.ConfigsGeneric;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.config.KeybindMulti;
import fi.dy.masa.tweakeroo.config.interfaces.IKeybind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class InputEventHandler
{
    private static final InputEventHandler INSTANCE = new InputEventHandler();
    private final Multimap<Integer, IKeybind> hotkeyMap = ArrayListMultimap.create();
    private final Set<Integer> modifierKeys = new HashSet<>();
    private LeftRight lastSidewaysInput = LeftRight.NONE;
    private ForwardBack lastForwardInput = ForwardBack.NONE;

    private InputEventHandler()
    {
        this.modifierKeys.add(Keyboard.KEY_LSHIFT);
        this.modifierKeys.add(Keyboard.KEY_RSHIFT);
        this.modifierKeys.add(Keyboard.KEY_LCONTROL);
        this.modifierKeys.add(Keyboard.KEY_RCONTROL);
        this.modifierKeys.add(Keyboard.KEY_LMENU);
        this.modifierKeys.add(Keyboard.KEY_RMENU);
    }

    public static InputEventHandler getInstance()
    {
        return INSTANCE;
    }

    public LeftRight getLastSidewaysInput()
    {
        return this.lastSidewaysInput;
    }

    public ForwardBack getLastForwardInput()
    {
        return this.lastForwardInput;
    }

    public void updateUsedKeys()
    {
        this.hotkeyMap.clear();

        for (FeatureToggle toggle : FeatureToggle.values())
        {
            this.addKeybindsToMap(toggle.getKeybind());
        }

        for (Hotkeys hotkey : Hotkeys.values())
        {
            this.addKeybindsToMap(hotkey.getKeybind());
        }
    }

    private void addKeybindsToMap(IKeybind keybind)
    {
        Collection<Integer> keys = keybind.getKeys();

        for (int key : keys)
        {
            this.hotkeyMap.put(key, keybind);
        }
    }

    public boolean onKeyInput()
    {
        Minecraft mc = Minecraft.getMinecraft();
        final int eventKey = Keyboard.getEventKey();
        final boolean eventKeyState = Keyboard.getEventKeyState();

        KeybindMulti.onKeyInput(eventKey, eventKeyState);

        // Not in a GUI
        if (mc.currentScreen == null)
        {
            boolean cancel = false;

            Collection<IKeybind> keybinds = this.hotkeyMap.get(eventKey);

            if (keybinds.isEmpty() == false)
            {
                for (IKeybind keybind : keybinds)
                {
                    // Note: isPressed() has to get called for key releases too, to reset the state
                    cancel |= keybind.isPressed();
                }
            }

            if (eventKeyState)
            {
                this.storeLastMovementDirection(eventKey, mc);
            }

            // Somewhat hacky fix to prevent eating the modifier keys... >_>
            // A proper fix would likely require adding a context for the keys,
            // and only cancel if the context is currently active/valid.
            return cancel && this.modifierKeys.contains(eventKey) == false;
        }

        return false;
    }

    public boolean onMouseInput()
    {
        Minecraft mc = Minecraft.getMinecraft();

        // Not in a GUI
        if (mc.currentScreen == null)
        {
            int dWheel = Mouse.getEventDWheel();

            if (dWheel != 0 && FeatureToggle.TWEAK_AFTER_CLICKER.getKeybind().isKeybindHeld(false))
            {
                int change = dWheel > 0 ? 1 : -1;
                int clicks = MathHelper.clamp(ConfigsGeneric.AFTER_CLICKER_CLICK_COUNT.getIntegerValue() + change, 1, 32);

                ConfigsGeneric.AFTER_CLICKER_CLICK_COUNT.setIntegerValue(clicks);

                String preGreen = TextFormatting.GREEN.toString();
                String rst = TextFormatting.RESET.toString();
                String strValue = preGreen + Integer.valueOf(clicks) + rst;
                mc.ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentTranslation("tweakeroo.message.set_after_clicker_count_to", strValue));

                return true;
            }
        }

        return false;
    }

    public static void onTick()
    {
        for (FeatureToggle toggle : FeatureToggle.values())
        {
            toggle.getKeybind().tick();
        }

        for (Hotkeys hotkey : Hotkeys.values())
        {
            hotkey.getKeybind().tick();
        }
    }

    private void storeLastMovementDirection(int eventKey, Minecraft mc)
    {
        if (eventKey == mc.gameSettings.keyBindForward.getKeyCode())
        {
            this.lastForwardInput = ForwardBack.FORWARD;
        }
        else if (eventKey == mc.gameSettings.keyBindBack.getKeyCode())
        {
            this.lastForwardInput = ForwardBack.BACK;
        }
        else if (eventKey == mc.gameSettings.keyBindLeft.getKeyCode())
        {
            this.lastSidewaysInput = LeftRight.LEFT;
        }
        else if (eventKey == mc.gameSettings.keyBindRight.getKeyCode())
        {
            this.lastSidewaysInput = LeftRight.RIGHT;
        }
    }

    public void handleMovementKeys(MovementInput movement)
    {
        GameSettings settings = Minecraft.getMinecraft().gameSettings;

        if (settings.keyBindLeft.isKeyDown() && settings.keyBindRight.isKeyDown())
        {
            if (this.lastSidewaysInput == LeftRight.LEFT)
            {
                movement.moveStrafe = 1;
                movement.leftKeyDown = true;
                movement.rightKeyDown = false;
            }
            else if (this.lastSidewaysInput == LeftRight.RIGHT)
            {
                movement.moveStrafe = -1;
                movement.leftKeyDown = false;
                movement.rightKeyDown = true;
            }
        }

        if (settings.keyBindBack.isKeyDown() && settings.keyBindForward.isKeyDown())
        {
            if (this.lastForwardInput == ForwardBack.FORWARD)
            {
                movement.moveForward = 1;
                movement.forwardKeyDown = true;
                movement.backKeyDown = false;
            }
            else if (this.lastForwardInput == ForwardBack.BACK)
            {
                movement.moveForward = -1;
                movement.forwardKeyDown = false;
                movement.backKeyDown = true;
            }
        }
    }

    public enum LeftRight
    {
        NONE,
        LEFT,
        RIGHT
    }

    public enum ForwardBack
    {
        NONE,
        FORWARD,
        BACK
    }
}
