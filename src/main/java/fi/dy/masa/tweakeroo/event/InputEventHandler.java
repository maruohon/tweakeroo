package fi.dy.masa.tweakeroo.event;

import fi.dy.masa.malilib.hotkeys.IKeybindEventHandler;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.tweakeroo.config.ConfigsGeneric;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class InputEventHandler implements IKeybindEventHandler
{
    private static final InputEventHandler INSTANCE = new InputEventHandler();
    private LeftRight lastSidewaysInput = LeftRight.NONE;
    private ForwardBack lastForwardInput = ForwardBack.NONE;

    private InputEventHandler()
    {
        super();
    }

    public static InputEventHandler getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void addKeysToMap(IKeybindManager manager)
    {
        for (FeatureToggle toggle : FeatureToggle.values())
        {
            manager.addKeybindToMap(toggle.getKeybind());
        }

        for (Hotkeys hotkey : Hotkeys.values())
        {
            manager.addKeybindToMap(hotkey.getKeybind());
        }
    }

    @Override
    public boolean onKeyInput(int eventKey, boolean eventKeyState)
    {
        Minecraft mc = Minecraft.getMinecraft();

        // Not in a GUI
        if (mc.currentScreen == null && eventKeyState)
        {
            this.storeLastMovementDirection(eventKey, mc);
        }

        return false;
    }

    @Override
    public boolean onMouseInput(int eventButton, int dWheel, boolean eventButtonState)
    {
        Minecraft mc = Minecraft.getMinecraft();

        // Not in a GUI
        if (mc.currentScreen == null && dWheel != 0)
        {
            if (FeatureToggle.TWEAK_AFTER_CLICKER.getKeybind().isKeybindHeld())
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

    public LeftRight getLastSidewaysInput()
    {
        return this.lastSidewaysInput;
    }

    public ForwardBack getLastForwardInput()
    {
        return this.lastForwardInput;
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
