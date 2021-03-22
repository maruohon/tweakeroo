package fi.dy.masa.tweakeroo.event;

import java.util.List;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.HotkeyCategory;
import fi.dy.masa.malilib.input.HotkeyProvider;
import fi.dy.masa.malilib.input.KeyboardInputHandler;
import fi.dy.masa.malilib.input.MouseInputHandler;
import fi.dy.masa.malilib.input.callback.AdjustableKeyCallback;
import fi.dy.masa.malilib.message.MessageUtils;
import fi.dy.masa.malilib.util.PositionUtils;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.DisableToggle;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import fi.dy.masa.tweakeroo.util.SnapAimMode;

public class InputHandler implements HotkeyProvider, KeyboardInputHandler, MouseInputHandler
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
    public List<? extends Hotkey> getAllHotkeys()
    {
        ImmutableList.Builder<Hotkey> builder = ImmutableList.builder();

        builder.addAll(Configs.Generic.HOTKEY_LIST);
        builder.addAll(Hotkeys.HOTKEY_LIST);
        builder.addAll(FeatureToggle.TOGGLE_HOTKEYS);
        builder.addAll(DisableToggle.TOGGLE_HOTKEYS);

        return builder.build();
    }

    @Override
    public List<HotkeyCategory> getHotkeysByCategories()
    {
        return ImmutableList.of(
                new HotkeyCategory(Reference.MOD_INFO, "tweakeroo.hotkeys.category.generic", Configs.Generic.HOTKEY_LIST),
                new HotkeyCategory(Reference.MOD_INFO, "tweakeroo.hotkeys.category.generic_hotkeys", Hotkeys.HOTKEY_LIST),
                new HotkeyCategory(Reference.MOD_INFO, "tweakeroo.hotkeys.category.disable_toggle_hotkeys", DisableToggle.TOGGLE_HOTKEYS),
                new HotkeyCategory(Reference.MOD_INFO, "tweakeroo.hotkeys.category.tweak_toggle_hotkeys", FeatureToggle.TOGGLE_HOTKEYS)
        );
    }

    @Override
    public boolean onKeyInput(int keyCode, int scanCode, int modifiers, boolean eventKeyState)
    {
        Minecraft mc = Minecraft.getMinecraft();

        // Not in a GUI
        if (GuiUtils.getCurrentScreen() == null && eventKeyState)
        {
            this.storeLastMovementDirection(keyCode, mc);
        }

        MiscUtils.checkZoomStatus();

        return false;
    }

    @Override
    public boolean onMouseInput(int eventButton, int wheelDelta, boolean eventButtonState)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (GuiUtils.getCurrentScreen() == null && mc.player != null && mc.player.capabilities.isCreativeMode &&
            eventButtonState && eventButton == mc.gameSettings.keyBindUseItem.getKeyCode() + 100 &&
            FeatureToggle.TWEAK_ANGEL_BLOCK.getBooleanValue() &&
            mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.MISS)
        {
            BlockPos posFront = PositionUtils.getPositionInfrontOfEntity(mc.player);

            if (mc.world.isAirBlock(posFront))
            {
                EnumFacing facing = PositionUtils.getClosestLookingDirection(mc.player).getOpposite();
                Vec3d hitVec = PositionUtils.getHitVecCenter(posFront, facing);
                ItemStack stack = mc.player.getHeldItemMainhand();

                if (stack.isEmpty() == false && stack.getItem() instanceof ItemBlock)
                {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, posFront, facing, hitVec, EnumHand.MAIN_HAND);
                    return true;
                }

                stack = mc.player.getHeldItemOffhand();

                if (stack.isEmpty() == false && stack.getItem() instanceof ItemBlock)
                {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, posFront, facing, hitVec, EnumHand.OFF_HAND);
                    return true;
                }
            }
        }
        // Not in a GUI
        else if (GuiUtils.getCurrentScreen() == null && wheelDelta != 0)
        {
            String preGreen = BaseScreen.TXT_GREEN;
            String rst = BaseScreen.TXT_RST;

            if (FeatureToggle.TWEAK_HOTBAR_SCROLL.getBooleanValue() && Hotkeys.HOTBAR_SCROLL.getKeyBind().isKeyBindHeld())
            {
                int currentRow = Configs.Internal.HOTBAR_SCROLL_CURRENT_ROW.getIntegerValue();

                int newRow = currentRow + (wheelDelta < 0 ? 1 : -1);
                int max = 2;
                if      (newRow < 0) { newRow = max; }
                else if (newRow > max) { newRow = 0; }

                Configs.Internal.HOTBAR_SCROLL_CURRENT_ROW.setValue(newRow);

                return true;
            }
            else if (FeatureToggle.TWEAK_AFTER_CLICKER.getKeyBind().isKeyBindHeld())
            {
                int newValue = Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getIntegerValue() + (wheelDelta > 0 ? 1 : -1);
                Configs.Generic.AFTER_CLICKER_CLICK_COUNT.setValue(newValue);
                AdjustableKeyCallback.setValueChanged();

                String strValue = preGreen + Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getIntegerValue() + rst;
                MessageUtils.printActionbarMessage("tweakeroo.message.set_after_clicker_count_to", strValue);

                return true;
            }
            else if (FeatureToggle.TWEAK_PLACEMENT_LIMIT.getKeyBind().isKeyBindHeld())
            {
                int newValue = Configs.Generic.PLACEMENT_LIMIT.getIntegerValue() + (wheelDelta > 0 ? 1 : -1);
                Configs.Generic.PLACEMENT_LIMIT.setValue(newValue);
                AdjustableKeyCallback.setValueChanged();

                String strValue = preGreen + Configs.Generic.PLACEMENT_LIMIT.getIntegerValue() + rst;
                MessageUtils.printActionbarMessage("tweakeroo.message.set_placement_limit_to", strValue);

                return true;
            }
            else if (FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getKeyBind().isKeyBindHeld())
            {
                int newValue = Configs.Generic.HOTBAR_SLOT_CYCLE_MAX.getIntegerValue() + (wheelDelta > 0 ? 1 : -1);
                Configs.Generic.HOTBAR_SLOT_CYCLE_MAX.setValue(newValue);
                AdjustableKeyCallback.setValueChanged();

                String strValue = preGreen + Configs.Generic.HOTBAR_SLOT_CYCLE_MAX.getIntegerValue() + rst;
                MessageUtils.printActionbarMessage("tweakeroo.message.set_hotbar_slot_cycle_max_to", strValue);

                return true;
            }
            else if (FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER.getKeyBind().isKeyBindHeld())
            {
                int newValue = Configs.Generic.HOTBAR_SLOT_RANDOMIZER_MAX.getIntegerValue() + (wheelDelta > 0 ? 1 : -1);
                Configs.Generic.HOTBAR_SLOT_RANDOMIZER_MAX.setValue(newValue);
                AdjustableKeyCallback.setValueChanged();

                String strValue = preGreen + Configs.Generic.HOTBAR_SLOT_RANDOMIZER_MAX.getIntegerValue() + rst;
                MessageUtils.printActionbarMessage("tweakeroo.message.set_hotbar_slot_randomizer_max_to", strValue);

                return true;
            }
            else if (FeatureToggle.TWEAK_BREAKING_GRID.getKeyBind().isKeyBindHeld())
            {
                int newValue = Configs.Generic.BREAKING_GRID_SIZE.getIntegerValue() + (wheelDelta > 0 ? 1 : -1);
                Configs.Generic.BREAKING_GRID_SIZE.setValue(newValue);
                AdjustableKeyCallback.setValueChanged();

                String strValue = preGreen + Configs.Generic.BREAKING_GRID_SIZE.getIntegerValue() + rst;
                MessageUtils.printActionbarMessage("tweakeroo.message.set_breaking_grid_size_to", strValue);

                return true;
            }
            else if (FeatureToggle.TWEAK_PLACEMENT_GRID.getKeyBind().isKeyBindHeld())
            {
                int newValue = Configs.Generic.PLACEMENT_GRID_SIZE.getIntegerValue() + (wheelDelta > 0 ? 1 : -1);
                Configs.Generic.PLACEMENT_GRID_SIZE.setValue(newValue);
                AdjustableKeyCallback.setValueChanged();

                String strValue = preGreen + Configs.Generic.PLACEMENT_GRID_SIZE.getIntegerValue() + rst;
                MessageUtils.printActionbarMessage("tweakeroo.message.set_placement_grid_size_to", strValue);

                return true;
            }
            else if (FeatureToggle.TWEAK_SNAP_AIM.getKeyBind().isKeyBindHeld())
            {
                SnapAimMode mode = Configs.Generic.SNAP_AIM_MODE.getValue();
                DoubleConfig config = mode == SnapAimMode.PITCH ? Configs.Generic.SNAP_AIM_PITCH_STEP : Configs.Generic.SNAP_AIM_YAW_STEP;

                double newValue = config.getDoubleValue() * (wheelDelta > 0 ? 2 : 0.5);
                config.setValue(newValue);
                AdjustableKeyCallback.setValueChanged();

                String val = preGreen + config.getDoubleValue() + rst;
                String key = mode == SnapAimMode.PITCH ? "tweakeroo.message.set_snap_aim_pitch_step_to" : "tweakeroo.message.set_snap_aim_yaw_step_to";

                MessageUtils.printActionbarMessage(key, val);

                return true;
            }
            else if (FeatureToggle.TWEAK_ZOOM.getKeyBind().isKeyBindHeld() ||
                     (FeatureToggle.TWEAK_ZOOM.getBooleanValue() && Hotkeys.ZOOM_ACTIVATE.getKeyBind().isKeyBindHeld()))
            {
                double diff = BaseScreen.isCtrlDown() ? 5 : 1;
                double newValue = Configs.Generic.ZOOM_FOV.getDoubleValue() + (wheelDelta < 0 ? diff : -diff);
                Configs.Generic.ZOOM_FOV.setValue(newValue);

                // Only prevent the next trigger when adjusting the value with the actual toggle key held
                if (FeatureToggle.TWEAK_ZOOM.getKeyBind().isKeyBindHeld())
                {
                    AdjustableKeyCallback.setValueChanged();
                }

                if (FeatureToggle.TWEAK_ZOOM.getBooleanValue())
                {
                    MiscUtils.onZoomActivated();
                }

                String strValue = String.format("%s%.1f%s", preGreen, Configs.Generic.ZOOM_FOV.getDoubleValue(), rst);
                MessageUtils.printActionbarMessage("tweakeroo.message.set_zoom_fov_to", strValue);

                return true;
            }

            for (int i = 0; i < 4; ++i)
            {
                HotkeyConfig hotkey = Configs.getFlySpeedHotkey(i);

                if (hotkey.getKeyBind().isKeyBindHeld())
                {
                    DoubleConfig config = Configs.getFlySpeedConfig(i);
                    double newValue = config.getDoubleValue() + (wheelDelta > 0 ? 0.005 : -0.005);
                    config.setValue(newValue);
                    AdjustableKeyCallback.setValueChanged();

                    String strIndex = preGreen + (i + 1) + rst;
                    String strValue = preGreen + String.format("%.3f", config.getDoubleValue()) + rst;
                    MessageUtils.printActionbarMessage("tweakeroo.message.set_fly_speed_to", strIndex, strValue);

                    return true;
                }
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
