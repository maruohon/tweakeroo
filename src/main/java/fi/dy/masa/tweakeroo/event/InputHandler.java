package fi.dy.masa.tweakeroo.event;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.options.GameOptions;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import fi.dy.masa.malilib.hotkeys.IKeyboardInputHandler;
import fi.dy.masa.malilib.hotkeys.IMouseInputHandler;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.PositionUtils;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.config.Callbacks;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;

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

        for (IHotkey hotkey : Hotkeys.HOTKEY_LIST)
        {
            manager.addKeybindToMap(hotkey.getKeybind());
        }
    }

    @Override
    public void addHotkeys(IKeybindManager manager)
    {
        manager.addHotkeysForCategory(Reference.MOD_NAME, "tweakeroo.hotkeys.category.generic_hotkeys", Hotkeys.HOTKEY_LIST);
        manager.addHotkeysForCategory(Reference.MOD_NAME, "tweakeroo.hotkeys.category.tweak_toggle_hotkeys", ImmutableList.copyOf(FeatureToggle.values()));
    }

    @Override
    public boolean onKeyInput(int keyCode, int scanCode, int modifiers, boolean eventKeyState)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        // Not in a GUI
        if (mc.currentScreen == null && eventKeyState)
        {
            this.storeLastMovementDirection(keyCode, scanCode, mc);
        }

        return false;
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int eventButton, boolean eventButtonState)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.currentScreen == null && mc.player != null && mc.player.abilities.creativeMode &&
            eventButtonState && mc.options.keyUse.matchesMouse(eventButton) &&
            FeatureToggle.TWEAK_ANGEL_BLOCK.getBooleanValue() &&
            mc.hitResult.getType() == HitResult.Type.MISS)
        {
            BlockPos posFront = PositionUtils.getPositionInfrontOfEntity(mc.player);

            if (mc.world.isAir(posFront))
            {
                Direction facing = PositionUtils.getClosestLookingDirection(mc.player).getOpposite();
                Vec3d hitVec = PositionUtils.getHitVecCenter(posFront, facing);
                BlockHitResult context = new BlockHitResult(hitVec, facing, posFront, false);

                ActionResult result = mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, context);

                if (result != ActionResult.SUCCESS)
                {
                    mc.interactionManager.interactBlock(mc.player, mc.world, Hand.OFF_HAND, context);
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onMouseScroll(int mouseX, int mouseY, double amount)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        int dWheel = (int) amount;

        // Not in a GUI
        if (mc.currentScreen == null && dWheel != 0)
        {
            String preGreen = GuiBase.TXT_GREEN;
            String rst = GuiBase.TXT_RST;

            if (FeatureToggle.TWEAK_HOTBAR_SCROLL.getBooleanValue() && Hotkeys.HOTBAR_SCROLL.getKeybind().isKeybindHeld())
            {
                int currentRow = Configs.Internal.HOTBAR_SCROLL_CURRENT_ROW.getIntegerValue();

                int newRow = currentRow + (dWheel < 0 ? 1 : -1);
                int max = 2;
                if      (newRow < 0) { newRow = max; }
                else if (newRow > max) { newRow = 0; }

                Configs.Internal.HOTBAR_SCROLL_CURRENT_ROW.setIntegerValue(newRow);

                return true;
            }
            else if (FeatureToggle.TWEAK_AFTER_CLICKER.getKeybind().isKeybindHeld())
            {
                int newValue = Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getIntegerValue() + (dWheel > 0 ? 1 : -1);
                Configs.Generic.AFTER_CLICKER_CLICK_COUNT.setIntegerValue(newValue);
                Callbacks.KeyCallbackToggleOnRelease.setValueChanged();

                String strValue = preGreen + Integer.valueOf(Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getIntegerValue()) + rst;
                InfoUtils.printActionbarMessage("tweakeroo.message.set_after_clicker_count_to", strValue);

                return true;
            }
            else if (FeatureToggle.TWEAK_PLACEMENT_LIMIT.getKeybind().isKeybindHeld())
            {
                int newValue = Configs.Generic.PLACEMENT_LIMIT.getIntegerValue() + (dWheel > 0 ? 1 : -1);
                Configs.Generic.PLACEMENT_LIMIT.setIntegerValue(newValue);
                Callbacks.KeyCallbackToggleOnRelease.setValueChanged();

                String strValue = preGreen + Integer.valueOf(Configs.Generic.PLACEMENT_LIMIT.getIntegerValue()) + rst;
                InfoUtils.printActionbarMessage("tweakeroo.message.set_placement_limit_to", strValue);

                return true;
            }
            else if (FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getKeybind().isKeybindHeld())
            {
                int newValue = Configs.Generic.HOTBAR_SLOT_CYCLE_MAX.getIntegerValue() + (dWheel > 0 ? 1 : -1);
                Configs.Generic.HOTBAR_SLOT_CYCLE_MAX.setIntegerValue(newValue);
                Callbacks.KeyCallbackToggleOnRelease.setValueChanged();

                String strValue = preGreen + Integer.valueOf(Configs.Generic.HOTBAR_SLOT_CYCLE_MAX.getIntegerValue()) + rst;
                InfoUtils.printActionbarMessage("tweakeroo.message.set_hotbar_slot_cycle_max_to", strValue);

                return true;
            }
            else if (FeatureToggle.TWEAK_PLACEMENT_GRID.getKeybind().isKeybindHeld())
            {
                int newValue = Configs.Generic.PLACEMENT_GRID_SIZE.getIntegerValue() + (dWheel > 0 ? 1 : -1);
                Configs.Generic.PLACEMENT_GRID_SIZE.setIntegerValue(newValue);
                Callbacks.KeyCallbackToggleOnRelease.setValueChanged();

                String strValue = preGreen + Integer.valueOf(Configs.Generic.PLACEMENT_GRID_SIZE.getIntegerValue()) + rst;
                InfoUtils.printActionbarMessage("tweakeroo.message.set_placement_grid_size_to", strValue);

                return true;
            }
            else if (FeatureToggle.TWEAK_ZOOM.getKeybind().isKeybindHeld())
            {
                double newValue = Configs.Generic.ZOOM_FOV.getDoubleValue() + (dWheel > 0 ? 1 : -1);
                Configs.Generic.ZOOM_FOV.setDoubleValue(newValue);
                Callbacks.KeyCallbackToggleOnRelease.setValueChanged();

                String strValue = String.format("%s%.1f%s", preGreen, Configs.Generic.ZOOM_FOV.getDoubleValue(), rst);
                InfoUtils.printActionbarMessage("tweakeroo.message.set_zoom_fov_to", strValue);

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

    private void storeLastMovementDirection(int eventKey, int scanCode, MinecraftClient mc)
    {
        if (mc.options.keyForward.matchesKey(eventKey, scanCode))
        {
            this.lastForwardInput = ForwardBack.FORWARD;
        }
        else if (mc.options.keyBack.matchesKey(eventKey, scanCode))
        {
            this.lastForwardInput = ForwardBack.BACK;
        }
        else if (mc.options.keyLeft.matchesKey(eventKey, scanCode))
        {
            this.lastSidewaysInput = LeftRight.LEFT;
        }
        else if (mc.options.keyRight.matchesKey(eventKey, scanCode))
        {
            this.lastSidewaysInput = LeftRight.RIGHT;
        }
    }

    public void handleMovementKeys(Input movement)
    {
        GameOptions settings = MinecraftClient.getInstance().options;

        if (settings.keyLeft.isPressed() && settings.keyRight.isPressed())
        {
            if (this.lastSidewaysInput == LeftRight.LEFT)
            {
                movement.movementSideways = 1;
                movement.pressingLeft = true;
                movement.pressingRight = false;
            }
            else if (this.lastSidewaysInput == LeftRight.RIGHT)
            {
                movement.movementSideways = -1;
                movement.pressingLeft = false;
                movement.pressingRight = true;
            }
        }

        if (settings.keyBack.isPressed() && settings.keyForward.isPressed())
        {
            if (this.lastForwardInput == ForwardBack.FORWARD)
            {
                movement.movementForward = 1;
                movement.pressingForward = true;
                movement.pressingBack = false;
            }
            else if (this.lastForwardInput == ForwardBack.BACK)
            {
                movement.movementForward = -1;
                movement.pressingForward = false;
                movement.pressingBack = true;
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
