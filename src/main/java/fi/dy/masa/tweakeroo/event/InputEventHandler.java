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
    private final Set<Integer> genericHotkeysUsedKeys = new HashSet<>();
    private final Set<Integer> tweakTogglesUsedKeys = new HashSet<>();
    private final Set<Integer> modifierKeys = new HashSet<>();

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

    public void updateUsedKeys()
    {
        this.tweakTogglesUsedKeys.clear();
        this.genericHotkeysUsedKeys.clear();

        for (FeatureToggle toggle : FeatureToggle.values())
        {
            this.tweakTogglesUsedKeys.addAll(toggle.getKeybind().getKeys());
        }

        for (Hotkeys hotkey : Hotkeys.values())
        {
            this.genericHotkeysUsedKeys.addAll(hotkey.getKeybind().getKeys());
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

            if (this.tweakTogglesUsedKeys.contains(eventKey))
            {
                for (FeatureToggle toggle : FeatureToggle.values())
                {
                    // Note: isPressed() has to get called for key releases too, to reset the state
                    cancel |= toggle.getKeybind().isPressed();
                }
            }

            if (this.genericHotkeysUsedKeys.contains(eventKey))
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
            return cancel && this.modifierKeys.contains(eventKey) == false;
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
