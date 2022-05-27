package fi.dy.masa.tweakeroo.input;

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
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.input.KeyboardInputHandler;
import fi.dy.masa.malilib.input.MouseInputHandler;
import fi.dy.masa.malilib.util.game.wrap.GameUtils;
import fi.dy.masa.malilib.util.game.wrap.ItemWrap;
import fi.dy.masa.malilib.util.position.PositionUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;

public class InputHandler implements KeyboardInputHandler, MouseInputHandler
{
    public static final InputHandler INSTANCE = new InputHandler();

    private LeftRight lastSidewaysInput = LeftRight.NONE;
    private ForwardBack lastForwardInput = ForwardBack.NONE;

    private InputHandler()
    {
        super();
    }

    @Override
    public boolean onKeyInput(int keyCode, int scanCode, int modifiers, boolean eventKeyState)
    {
        // Not in a GUI
        if (GuiUtils.getCurrentScreen() == null && eventKeyState)
        {
            this.storeLastMovementDirection(keyCode, GameUtils.getClient());
        }

        MiscUtils.checkZoomStatus();

        return false;
    }

    @Override
    public boolean onMouseInput(int eventButton, int wheelDelta, boolean eventButtonState)
    {
        Minecraft mc = GameUtils.getClient();
        RayTraceResult hitResult = GameUtils.getHitResult();

        if (GuiUtils.getCurrentScreen() == null && mc.player != null && mc.player.capabilities.isCreativeMode &&
            eventButtonState && eventButton == mc.gameSettings.keyBindUseItem.getKeyCode() + 100 &&
            FeatureToggle.TWEAK_ANGEL_BLOCK.getBooleanValue() &&
            hitResult != null && hitResult.typeOfHit == RayTraceResult.Type.MISS)
        {
            BlockPos posFront = PositionUtils.getPositionInfrontOfEntity(mc.player);

            if (mc.world.isAirBlock(posFront))
            {
                EnumFacing facing = PositionUtils.getClosestLookingDirection(mc.player).getOpposite();
                Vec3d hitVec = PositionUtils.getHitVecCenter(posFront, facing);
                ItemStack stack = mc.player.getHeldItemMainhand();

                if (ItemWrap.notEmpty(stack) && stack.getItem() instanceof ItemBlock)
                {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, posFront, facing, hitVec, EnumHand.MAIN_HAND);
                    return true;
                }

                stack = mc.player.getHeldItemOffhand();

                if (ItemWrap.notEmpty(stack) && stack.getItem() instanceof ItemBlock)
                {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, posFront, facing, hitVec, EnumHand.OFF_HAND);
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
        GameSettings settings = GameUtils.getClient().gameSettings;

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
