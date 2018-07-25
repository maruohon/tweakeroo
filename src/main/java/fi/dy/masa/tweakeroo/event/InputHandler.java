package fi.dy.masa.tweakeroo.event;

import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import fi.dy.masa.malilib.hotkeys.IKeyboardInputHandler;
import fi.dy.masa.malilib.hotkeys.IMouseInputHandler;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.config.Callbacks;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.util.PositionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class InputHandler implements IKeybindProvider, IKeyboardInputHandler, IMouseInputHandler
{
    private static final InputHandler INSTANCE = new InputHandler();
    private LeftRight lastSidewaysInput = LeftRight.NONE;
    private ForwardBack lastForwardInput = ForwardBack.NONE;

    private InputHandler()
    {
        super();
    }

    public static InputHandler getInstance()
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
    public void addHotkeys(IKeybindManager manager)
    {
        manager.addHotkeysForCategory(Reference.MOD_NAME, "tweakeroo.hotkeys.category.generic_hotkeys", Hotkeys.values());
        manager.addHotkeysForCategory(Reference.MOD_NAME, "tweakeroo.hotkeys.category.tweak_toggle_hotkeys", FeatureToggle.values());
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

        if (mc.currentScreen == null && mc.player != null && mc.player.capabilities.isCreativeMode &&
            eventButtonState && eventButton == mc.gameSettings.keyBindUseItem.getKeyCode() + 100 &&
            FeatureToggle.TWEAK_ANGEL_BLOCK.getBooleanValue() &&
            mc.objectMouseOver.typeOfHit == RayTraceResult.Type.MISS)
        {
            BlockPos posFront = PositionUtils.getPositionInfrontOfEntity(mc.player);

            if (mc.world.isAirBlock(posFront))
            {
                EnumFacing facing = PositionUtils.getClosestLookingDirection(mc.player).getOpposite();
                Vec3d hitVec = PositionUtils.getHitVecCenter(posFront, facing);

                EnumActionResult result = mc.playerController.processRightClickBlock(mc.player, mc.world, posFront, facing, hitVec, EnumHand.MAIN_HAND);

                if (result != EnumActionResult.SUCCESS)
                {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, posFront, facing, hitVec, EnumHand.OFF_HAND);
                }

                return true;
            }
        }
        // Not in a GUI
        else if (mc.currentScreen == null && dWheel != 0)
        {
            String preGreen = TextFormatting.GREEN.toString();
            String rst = TextFormatting.RESET.toString();

            if (FeatureToggle.TWEAK_HOTBAR_SCROLL.getBooleanValue() && Hotkeys.HOTBAR_SCROLL.getKeybind().isKeybindHeld())
            {
                int currentRow = Configs.Generic.HOTBAR_SCROLL_CURRENT_ROW.getIntegerValue();

                int newRow = currentRow + (dWheel < 0 ? 1 : -1);
                int max = 2;
                if      (newRow < 0) { newRow = max; }
                else if (newRow > max) { newRow = 0; }

                Configs.Generic.HOTBAR_SCROLL_CURRENT_ROW.setIntegerValue(newRow);

                return true;
            }
            else if (FeatureToggle.TWEAK_AFTER_CLICKER.getKeybind().isKeybindHeld())
            {
                int newValue = Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getIntegerValue() + (dWheel > 0 ? 1 : -1);
                Configs.Generic.AFTER_CLICKER_CLICK_COUNT.setIntegerValue(newValue);
                Callbacks.KeyCallbackToggleOnRelease.setValueChanged();

                String strValue = preGreen + Integer.valueOf(Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getIntegerValue()) + rst;
                mc.ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentTranslation("tweakeroo.message.set_after_clicker_count_to", strValue));

                return true;
            }
            else if (FeatureToggle.TWEAK_PLACEMENT_LIMIT.getKeybind().isKeybindHeld())
            {
                int newValue = Configs.Generic.PLACEMENT_LIMIT.getIntegerValue() + (dWheel > 0 ? 1 : -1);
                Configs.Generic.PLACEMENT_LIMIT.setIntegerValue(newValue);
                Callbacks.KeyCallbackToggleOnRelease.setValueChanged();

                String strValue = preGreen + Integer.valueOf(Configs.Generic.PLACEMENT_LIMIT.getIntegerValue()) + rst;
                mc.ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentTranslation("tweakeroo.message.set_placement_limit_to", strValue));

                return true;
            }
            else if (FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getKeybind().isKeybindHeld())
            {
                int newValue = Configs.Generic.HOTBAR_SLOT_CYCLE_MAX.getIntegerValue() + (dWheel > 0 ? 1 : -1);
                Configs.Generic.HOTBAR_SLOT_CYCLE_MAX.setIntegerValue(newValue);
                Callbacks.KeyCallbackToggleOnRelease.setValueChanged();

                String strValue = preGreen + Integer.valueOf(Configs.Generic.HOTBAR_SLOT_CYCLE_MAX.getIntegerValue()) + rst;
                mc.ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentTranslation("tweakeroo.message.set_hotbar_slot_cycle_max_to", strValue));

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
