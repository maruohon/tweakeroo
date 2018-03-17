package fi.dy.masa.tweakeroo.event;

import java.util.HashSet;
import java.util.Set;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.interfaces.IKeybind;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;

public class InputEventHandler
{
    private static final InputEventHandler INSTANCE = new InputEventHandler();
    private static final Set<Integer> USED_KEYS = new HashSet<>();

    public static InputEventHandler getInstance()
    {
        return INSTANCE;
    }

    public static void updateUsedKeys()
    {
        USED_KEYS.clear();

        for (FeatureToggle toggle : FeatureToggle.values())
        {
            USED_KEYS.addAll(toggle.getKeybind().getKeys());
        }
    }

    public void onKeyInput()
    {
        Minecraft mc = Minecraft.getMinecraft();

        // Not in a GUI
        if (mc.currentScreen == null)
        {
            int eventKey = Keyboard.getEventKey();

            if (USED_KEYS.contains(eventKey))
            {
                for (FeatureToggle toggle : FeatureToggle.values())
                {
                    IKeybind keybind = toggle.getKeybind();

                    // Note: isPressed() has to get called for key releases too, to reset the state
                    if (keybind.isPressed() && Keyboard.getEventKeyState())
                    {
                        toggle.setBooleanValue(! toggle.getBooleanValue());
                        String str = toggle.getBooleanValue() ? "ON" : "OFF";
                        this.printMessage(mc, toggle.getToggleMessage(), str);
                    }
                }
            }
        }
    }

    private void printMessage(Minecraft mc, String key, Object... args)
    {
        mc.ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentTranslation(key, args));
    }
}
