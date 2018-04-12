package fi.dy.masa.tweakeroo.event;

import java.util.HashSet;
import java.util.Set;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import net.minecraft.client.Minecraft;

public class InputEventHandler
{
    private static final InputEventHandler INSTANCE = new InputEventHandler();
    private static final Set<Integer> TWEAK_TOGGLES_USED_KEYS = new HashSet<>();
    private static final Set<Integer> GENERIC_HOTKEYS_USED_KEYS = new HashSet<>();
    private static final Set<Integer> MODIFIER_KEYS = new HashSet<>();

    private InputEventHandler()
    {
        MODIFIER_KEYS.add(Keyboard.KEY_LSHIFT);
        MODIFIER_KEYS.add(Keyboard.KEY_RSHIFT);
        MODIFIER_KEYS.add(Keyboard.KEY_LCONTROL);
        MODIFIER_KEYS.add(Keyboard.KEY_RCONTROL);
        MODIFIER_KEYS.add(Keyboard.KEY_LMENU);
        MODIFIER_KEYS.add(Keyboard.KEY_RMENU);
    }

    public static InputEventHandler getInstance()
    {
        return INSTANCE;
    }

    public static void updateUsedKeys()
    {
        TWEAK_TOGGLES_USED_KEYS.clear();
        GENERIC_HOTKEYS_USED_KEYS.clear();

        for (FeatureToggle toggle : FeatureToggle.values())
        {
            TWEAK_TOGGLES_USED_KEYS.addAll(toggle.getKeybind().getKeys());
        }

        for (Hotkeys hotkey : Hotkeys.values())
        {
            GENERIC_HOTKEYS_USED_KEYS.addAll(hotkey.getKeybind().getKeys());
        }
    }

    public boolean onKeyInput()
    {
        Minecraft mc = Minecraft.getMinecraft();

        // Not in a GUI
        if (mc.currentScreen == null)
        {
            int eventKey = Keyboard.getEventKey();
            boolean cancel = false;

            if (TWEAK_TOGGLES_USED_KEYS.contains(eventKey))
            {
                for (FeatureToggle toggle : FeatureToggle.values())
                {
                    // Note: isPressed() has to get called for key releases too, to reset the state
                    cancel |= toggle.getKeybind().isPressed();
                }
            }

            if (GENERIC_HOTKEYS_USED_KEYS.contains(eventKey))
            {
                for (Hotkeys hotkey : Hotkeys.values())
                {
                    // Note: isPressed() has to get called for key releases too, to reset the state
                    cancel |= hotkey.getKeybind().isPressed();
                }
            }

            // Somewhat hacky fix to prevent eating the modifier keys... >_>
            // A proper fix would likely require adding a context for the keys,
            // and only cancel if the context is currently active/valid.
            return cancel && MODIFIER_KEYS.contains(eventKey) == false;
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
}
