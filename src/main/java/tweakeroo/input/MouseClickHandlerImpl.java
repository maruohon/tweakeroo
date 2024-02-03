package tweakeroo.input;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import malilib.gui.util.GuiUtils;
import malilib.input.MouseClickHandler;
import malilib.util.game.wrap.EntityWrap;
import malilib.util.game.wrap.GameUtils;
import malilib.util.game.wrap.ItemWrap;
import malilib.util.position.BlockPos;
import malilib.util.position.Direction;
import malilib.util.position.HitResult;
import malilib.util.position.PositionUtils;
import malilib.util.position.Vec3d;
import tweakeroo.config.FeatureToggle;

public class MouseClickHandlerImpl implements MouseClickHandler
{
    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int mouseButton, boolean buttonState)
    {
        EntityPlayerSP player = GameUtils.getClientPlayer();
        WorldClient world = GameUtils.getClientWorld();
        HitResult hitResult = GameUtils.getHitResult();

        if (GuiUtils.noScreenOpen() && player != null && player.capabilities.isCreativeMode &&
            buttonState && mouseButton == GameUtils.getClient().gameSettings.keyBindUseItem.getKeyCode() + 100 &&
            FeatureToggle.TWEAK_ANGEL_BLOCK.getBooleanValue() &&
            hitResult.type == HitResult.Type.MISS)
        {
            BlockPos posFront = PositionUtils.getPositionInFrontOfEntity(player);

            if (world.isAirBlock(posFront))
            {
                Direction direction = EntityWrap.getClosestLookingDirection(player).getOpposite();
                Vec3d hitPos = PositionUtils.getHitVecCenter(posFront, direction);
                EnumFacing facing = direction.getVanillaDirection();
                net.minecraft.util.math.Vec3d pos = hitPos.toVanilla();
                ItemStack stack = player.getHeldItemMainhand();

                if (ItemWrap.notEmpty(stack) && stack.getItem() instanceof ItemBlock)
                {
                    GameUtils.getInteractionManager().processRightClickBlock(player, world, posFront, facing, pos, EnumHand.MAIN_HAND);
                    return true;
                }

                stack = player.getHeldItemOffhand();

                if (ItemWrap.notEmpty(stack) && stack.getItem() instanceof ItemBlock)
                {
                    GameUtils.getInteractionManager().processRightClickBlock(player, world, posFront, facing, pos, EnumHand.OFF_HAND);
                    return true;
                }
            }
        }

        return false;
    }
}
