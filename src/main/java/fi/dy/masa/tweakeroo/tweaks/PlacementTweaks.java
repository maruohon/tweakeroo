package fi.dy.masa.tweakeroo.tweaks;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import fi.dy.masa.malilib.util.BlockUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.PositionUtils;
import fi.dy.masa.malilib.util.PositionUtils.HitPart;
import fi.dy.masa.malilib.util.restrictions.BlockRestriction;
import fi.dy.masa.malilib.util.restrictions.ItemRestriction;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.util.CameraUtils;
import fi.dy.masa.tweakeroo.util.IMinecraftClientInvoker;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.PlacementRestrictionMode;

public class PlacementTweaks
{
    private static BlockPos posFirst = null;
    private static BlockPos posFirstBreaking = null;
    private static BlockPos posLast = null;
    private static HitPart hitPartFirst = null;
    private static Hand handFirst = Hand.MAIN_HAND;
    private static Vec3d hitVecFirst = null;
    private static Direction sideFirst = null;
    private static Direction sideFirstBreaking = null;
    private static Direction sideRotatedFirst = null;
    private static float playerYawFirst;
    private static ItemStack[] stackBeforeUse = new ItemStack[] { ItemStack.EMPTY, ItemStack.EMPTY };
    private static boolean isFirstClick;
    private static boolean isEmulatedClick;
    private static boolean firstWasRotation;
    private static boolean firstWasOffset;
    private static int placementCount;
    private static int hotbarSlot = -1;
    private static ItemStack stackClickedOn = ItemStack.EMPTY;
    @Nullable private static BlockState stateClickedOn = null;
    public static final BlockRestriction FAST_RIGHT_CLICK_BLOCK_RESTRICTION = new BlockRestriction();
    public static final ItemRestriction FAST_RIGHT_CLICK_ITEM_RESTRICTION = new ItemRestriction();
    public static final ItemRestriction FAST_PLACEMENT_ITEM_RESTRICTION = new ItemRestriction();

    public static void onTick()
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (GuiUtils.getCurrentScreen() == null)
        {
            if (mc.options.keyUse.isPressed())
            {
                onUsingTick();
            }

            if (mc.options.keyAttack.isPressed())
            {
                onAttackTick(mc);
            }
        }
        else
        {
            stackBeforeUse[0] = ItemStack.EMPTY;
            stackBeforeUse[1] = ItemStack.EMPTY;
        }

        if (mc.options.keyUse.isPressed() == false)
        {
            clearClickedBlockInfoUse();

            // Clear the cached stack when releasing both keys, so that the restock doesn't happen when
            // using another another item or an empty hand.
            if (mc.options.keyAttack.isPressed() == false)
            {
                stackBeforeUse[0] = ItemStack.EMPTY;
                stackBeforeUse[1] = ItemStack.EMPTY;
            }
        }

        if (mc.options.keyAttack.isPressed() == false)
        {
            clearClickedBlockInfoAttack();
        }
    }

    public static boolean onProcessRightClickPre(PlayerEntity player, Hand hand)
    {
        InventoryUtils.trySwapCurrentToolIfNearlyBroken();

        ItemStack stackOriginal = player.getStackInHand(hand);

        if (FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue() && stackOriginal.isEmpty() == false)
        {
            if (isEmulatedClick == false)
            {
                //System.out.printf("onProcessRightClickPre storing stack: %s\n", stackOriginal);
                cacheStackInHand(hand);
            }

            // Don't allow taking stacks from elsewhere in the hotbar, if the cycle tweak is on
            boolean allowHotbar = FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getBooleanValue() == false &&
                                  FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER.getBooleanValue() == false;
            InventoryUtils.preRestockHand(player, hand, allowHotbar);
        }

        return InventoryUtils.canUnstackingItemNotFitInInventory(stackOriginal, player);
    }

    public static void onProcessRightClickPost(PlayerEntity player, Hand hand)
    {
        //System.out.printf("onProcessRightClickPost -> tryRestockHand with: %s, current: %s\n", stackBeforeUse[hand.ordinal()], player.getHeldItem(hand));
        tryRestockHand(player, hand, stackBeforeUse[hand.ordinal()]);
    }

    public static void onLeftClickMousePre()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        HitResult trace = mc.crosshairTarget;

        // Only set the position if it was null, otherwise the fast left click tweak
        // would just reset it every time.
        if (trace != null && trace.getType() == HitResult.Type.BLOCK && posFirstBreaking == null)
        {
            posFirstBreaking = ((BlockHitResult) trace).getBlockPos();
            sideFirstBreaking = ((BlockHitResult) trace).getSide();
        }

        onProcessRightClickPre(mc.player, Hand.MAIN_HAND);
    }

    public static void onLeftClickMousePost()
    {
        onProcessRightClickPost(MinecraftClient.getInstance().player, Hand.MAIN_HAND);
    }

    public static void cacheStackInHand(Hand hand)
    {
        PlayerEntity player = MinecraftClient.getInstance().player;
        ItemStack stackOriginal = player.getStackInHand(hand);

        if (FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue() && stackOriginal.isEmpty() == false)
        {
            stackBeforeUse[hand.ordinal()] = stackOriginal.copy();
            hotbarSlot = player.inventory.selectedSlot;
        }
    }

    private static void onAttackTick(MinecraftClient mc)
    {
        if (FeatureToggle.TWEAK_FAST_LEFT_CLICK.getBooleanValue())
        {
            final int count = Configs.Generic.FAST_LEFT_CLICK_COUNT.getIntegerValue();

            for (int i = 0; i < count; ++i)
            {
                InventoryUtils.trySwapCurrentToolIfNearlyBroken();
                isEmulatedClick = true;
                ((IMinecraftClientInvoker) mc).leftClickMouseAccessor();
                isEmulatedClick = false;
            }
        }
        else
        {
            InventoryUtils.trySwapCurrentToolIfNearlyBroken();
            Hand hand = Hand.MAIN_HAND;
            tryRestockHand(mc.player, hand, stackBeforeUse[hand.ordinal()]);
        }
    }

    private static void onUsingTick()
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null)
        {
            return;
        }

        if (posFirst != null && FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getBooleanValue() &&
            canUseItemWithRestriction(FAST_PLACEMENT_ITEM_RESTRICTION, mc.player))
        {
            ClientPlayerEntity player = mc.player;
            World world = player.getEntityWorld();
            final double reach = mc.interactionManager.getReachDistance();
            final int maxCount = Configs.Generic.FAST_BLOCK_PLACEMENT_COUNT.getIntegerValue();

            mc.crosshairTarget = player.raycast(reach, mc.getTickDelta(), false);

            for (int i = 0; i < maxCount; ++i)
            {
                HitResult trace = mc.crosshairTarget;

                if (trace == null || trace.getType() != HitResult.Type.BLOCK)
                {
                    break;
                }

                BlockHitResult blockHitResult = (BlockHitResult) trace;
                Hand hand = handFirst;
                Direction side = blockHitResult.getSide();
                BlockPos pos = blockHitResult.getBlockPos();
                Vec3d hitVec = blockHitResult.getPos();
                BlockHitResult hitResult = new BlockHitResult(hitVec, side, pos, false);
                ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));
                BlockPos posNew = getPlacementPositionForTargetedPosition(world, pos, side, ctx);
                hitResult = new BlockHitResult(hitVec, side, posNew, false);
                ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));

                if (hand != null &&
                    posNew.equals(posLast) == false &&
                    canPlaceBlockIntoPosition(world, posNew, ctx) &&
                    isPositionAllowedByPlacementRestriction(posNew, side) &&
                    canPlaceBlockAgainst(world, pos, player, hand)
                )
                {
                    /*
                    IBlockState state = world.getBlockState(pos);
                    float x = (float) (trace.hitVec.x - pos.getX());
                    float y = (float) (trace.hitVec.y - pos.getY());
                    float z = (float) (trace.hitVec.z - pos.getZ());

                    if (state.getBlock().onBlockActivated(world, posNew, state, player, hand, side, x, y, z))
                    {
                        return;
                    }
                    */

                    hitVec = hitVecFirst.add(posNew.getX(), posNew.getY(), posNew.getZ());
                    ActionResult result = tryPlaceBlock(mc.interactionManager, player, mc.world,
                            posNew, sideFirst, sideRotatedFirst, playerYawFirst, hitVec, hand, hitPartFirst, false);

                    if (result == ActionResult.SUCCESS)
                    {
                        posLast = posNew;
                        mc.crosshairTarget = player.raycast(reach, mc.getTickDelta(), false);
                    }
                    else
                    {
                        break;
                    }
                }
                else
                {
                    break;
                }
            }

            // Reset the timer to prevent the regular process method from re-firing
            ((IMinecraftClientInvoker) mc).setItemUseCooldown(4);
        }
        else if (FeatureToggle.TWEAK_FAST_RIGHT_CLICK.getBooleanValue() &&
                mc.options.keyUse.isPressed() &&
                canUseFastRightClick(mc.player))
        {
            final int count = Configs.Generic.FAST_RIGHT_CLICK_COUNT.getIntegerValue();

            for (int i = 0; i < count; ++i)
            {
                isEmulatedClick = true;
                ((IMinecraftClientInvoker) mc).rightClickMouseAccessor();
                isEmulatedClick = false;
            }
        }
    }

    public static ActionResult onProcessRightClickBlock(
            ClientPlayerInteractionManager controller,
            ClientPlayerEntity player,
            ClientWorld world,
            Hand hand,
            BlockHitResult hitResult)
    {
        if (CameraUtils.shouldPreventPlayerInputs())
        {
            return ActionResult.PASS;
        }

        boolean restricted = FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.getBooleanValue() || FeatureToggle.TWEAK_PLACEMENT_GRID.getBooleanValue();
        ItemStack stackPre = player.getStackInHand(hand).copy();
        Direction sideIn = hitResult.getSide();
        BlockPos posIn = hitResult.getBlockPos();
        Vec3d hitVec = hitResult.getPos();
        Direction playerFacingH = player.getHorizontalFacing();
        HitPart hitPart = PositionUtils.getHitPart(sideIn, playerFacingH, posIn, hitVec);
        Direction sideRotated = getRotatedFacing(sideIn, playerFacingH, hitPart);

        cacheStackInHand(hand);

        if (FeatureToggle.TWEAK_PLACEMENT_REST_FIRST.getBooleanValue() && stateClickedOn == null)
        {
            BlockState state = world.getBlockState(posIn);
            stackClickedOn = state.getBlock().getPickStack(world, posIn, state);
            stateClickedOn = state;
        }

        if (canPlaceBlockAgainst(world, posIn, player, hand) == false)
        {
            return ActionResult.PASS;
        }

        //System.out.printf("onProcessRightClickBlock() pos: %s, side: %s, part: %s, hitVec: %s\n", posIn, sideIn, hitPart, hitVec);
        ActionResult result = tryPlaceBlock(controller, player, world, posIn, sideIn, sideRotated, player.yaw, hitVec, hand, hitPart, true);

        // Store the initial click data for the fast placement mode
        if (posFirst == null && result == ActionResult.SUCCESS && restricted)
        {
            boolean flexible = FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue();
            boolean accurate = FeatureToggle.TWEAK_ACCURATE_BLOCK_PLACEMENT.getBooleanValue();
            boolean rotation = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ROTATION.getKeybind().isKeybindHeld();
            boolean offset = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_OFFSET.getKeybind().isKeybindHeld();
            boolean accurateIn = Hotkeys.ACCURATE_BLOCK_PLACEMENT_IN.getKeybind().isKeybindHeld();
            boolean accurateReverse = Hotkeys.ACCURATE_BLOCK_PLACEMENT_REVERSE.getKeybind().isKeybindHeld();

            firstWasRotation = (flexible && rotation) || (accurate && (accurateIn || accurateReverse));
            firstWasOffset = flexible && offset;
            BlockHitResult hitResultTmp = new BlockHitResult(hitVec, sideIn, posIn, false);
            ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResultTmp));
            posFirst = getPlacementPositionForTargetedPosition(world, posIn, sideIn, ctx);
            posLast = posFirst;
            hitPartFirst = hitPart;
            handFirst = hand;
            hitVecFirst = hitVec.subtract(posFirst.getX(), posFirst.getY(), posFirst.getZ());
            sideFirst = sideIn;
            sideRotatedFirst = sideRotated;
            playerYawFirst = player.yaw;
            stackBeforeUse[hand.ordinal()] = stackPre;
            //System.out.printf("plop store @ %s\n", posFirst);
        }

        return result;
    }

    private static ActionResult tryPlaceBlock(
            ClientPlayerInteractionManager controller,
            ClientPlayerEntity player,
            ClientWorld world,
            BlockPos posIn,
            Direction sideIn,
            Direction sideRotatedIn,
            float playerYaw,
            Vec3d hitVec,
            Hand hand,
            HitPart hitPart,
            boolean isFirstClick)
    {
        Direction side = sideIn;
        boolean handleFlexible = false;
        BlockPos posNew = null;
        boolean flexible = FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue();
        boolean rotationHeld = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ROTATION.getKeybind().isKeybindHeld();
        boolean offsetHeld = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_OFFSET.getKeybind().isKeybindHeld();
        boolean adjacent = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ADJACENT.getKeybind().isKeybindHeld();
        boolean rememberFlexible = FeatureToggle.REMEMBER_FLEXIBLE.getBooleanValue();
        boolean rotation = rotationHeld || (rememberFlexible && firstWasRotation);
        boolean offset = offsetHeld || (rememberFlexible && firstWasOffset);
        ItemStack stack = player.getStackInHand(hand);

        if (flexible)
        {
            BlockHitResult hitResult = new BlockHitResult(hitVec, sideIn, posIn, false);
            ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));
            posNew = isFirstClick && (rotation || offset || adjacent) ? getPlacementPositionForTargetedPosition(world, posIn, sideIn, ctx) : posIn;

            // Place the block into the adjacent position
            if (adjacent && hitPart != null && hitPart != HitPart.CENTER)
            {
                posNew = posNew.offset(sideRotatedIn.getOpposite()).offset(sideIn.getOpposite());
                handleFlexible = true;
            }

            // Place the block facing/against the adjacent block (= just rotated from normal)
            if (rotation)
            {
                side = sideRotatedIn;
                handleFlexible = true;
            }
            else
            {
                // Don't rotate the player facing in handleFlexibleBlockPlacement()
                hitPart = null;
            }

            // Place the block into the diagonal position
            if (offset)
            {
                posNew = posNew.offset(sideRotatedIn.getOpposite());
                handleFlexible = true;
            }
        }

        boolean simpleOffset = false;

        if (handleFlexible == false &&
            FeatureToggle.TWEAK_FAKE_SNEAK_PLACEMENT.getBooleanValue() &&
            stack.isEmpty() == false)
        {
            BlockHitResult hitResult = new BlockHitResult(hitVec, sideIn, posIn, false);
            ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));
            posNew = getPlacementPositionForTargetedPosition(world, posIn, sideIn, ctx);
            simpleOffset = true;
        }

        boolean accurate = FeatureToggle.TWEAK_ACCURATE_BLOCK_PLACEMENT.getBooleanValue();
        boolean accurateIn = Hotkeys.ACCURATE_BLOCK_PLACEMENT_IN.getKeybind().isKeybindHeld();
        boolean accurateReverse = Hotkeys.ACCURATE_BLOCK_PLACEMENT_REVERSE.getKeybind().isKeybindHeld();
        boolean afterClicker = FeatureToggle.TWEAK_AFTER_CLICKER.getBooleanValue();

        if (accurate && (accurateIn || accurateReverse || afterClicker))
        {
            Direction facing = side;
            boolean handleAccurate = false;

            if (posNew == null)
            {
                if (flexible == false || isFirstClick == false)
                {
                    posNew = posIn;
                }
                else
                {
                    BlockHitResult hitResult = new BlockHitResult(hitVec, side, posIn, false);
                    ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));
                    posNew = getPlacementPositionForTargetedPosition(world, posIn, side, ctx);
                }
            }

            if (accurateIn)
            {
                facing = sideIn;
                hitPart = null;
                handleAccurate = true;

                // Pistons, Droppers, Dispensers should face into the block, but Observers should point their back/output
                // side into the block when the Accurate Placement In hotkey is used
                if ((stack.getItem() instanceof BlockItem) == false || ((BlockItem) stack.getItem()).getBlock() != Blocks.OBSERVER)
                {
                    facing = facing.getOpposite();
                }
                //System.out.printf("accurate - IN - facing: %s\n", facing);
            }
            else if (flexible == false || rotation == false)
            {
                if (stack.getItem() instanceof BlockItem)
                {

                    BlockHitResult hitResult = new BlockHitResult(hitVec, sideIn, posNew, false);
                    ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));

                    BlockPos posPlacement = getPlacementPositionForTargetedPosition(world, posNew, sideIn, ctx);

                    hitResult = new BlockHitResult(hitVec, sideIn, posPlacement, false);
                    ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));

                    BlockItem item = (BlockItem) stack.getItem();
                    BlockState state = item.getBlock().getPlacementState(ctx);

                    // getStateForPlacement can return null in 1.13+
                    if (state == null)
                    {
                        return ActionResult.PASS;
                    }

                    Direction facingTmp = BlockUtils.getFirstPropertyFacingValue(state);
                    //System.out.printf("accurate - sideIn: %s, state: %s, hit: %s, f: %s, posNew: %s\n", sideIn, state, hitVec, EnumFacing.getDirectionFromEntityLiving(posIn, player), posNew);

                    if (facingTmp != null)
                    {
                        facing = facingTmp;
                    }
                }
                else
                {
                    facing = player.getHorizontalFacing();
                }
            }

            if (accurateReverse)
            {
                //System.out.printf("accurate - REVERSE - facingOrig: %s, facingNew: %s\n", facing, facing.getOpposite());
                if (accurateIn || flexible == false || rotation == false)
                {
                    facing = facing.getOpposite();
                }

                hitPart = null;
                handleAccurate = true;
            }

            if ((handleAccurate || afterClicker) && FeatureToggle.CARPET_ACCURATE_PLACEMENT_PROTOCOL.getBooleanValue())
            {
                // Carpet-Extra mod accurate block placement protocol support
                double relX = hitVec.x - posNew.getX();
                double x = hitVec.x;
                int afterClickerClickCount = MathHelper.clamp(Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getIntegerValue(), 0, 32);

                if (handleAccurate && isFacingValidFor(facing, stack))
                {
                    x = posNew.getX() + relX + 2 + (facing.getId() * 2);
                }

                if (afterClicker)
                {
                    x += afterClickerClickCount * 16;
                }

                //System.out.printf("accurate - pre hitVec: %s\n", hitVec);
                //System.out.printf("processRightClickBlockWrapper facing: %s, x: %.3f, pos: %s, side: %s\n", facing, x, pos, side);
                hitVec = new Vec3d(x, hitVec.y, hitVec.z);
                //System.out.printf("accurate - post hitVec: %s\n", hitVec);
            }

            //System.out.printf("accurate - facing: %s, side: %s, posNew: %s, hit: %s\n", facing, side, posNew, hitVec);
            return processRightClickBlockWrapper(controller, player, world, posNew, side, hitVec, hand);
        }

        if (handleFlexible)
        {
            BlockHitResult hitResult = new BlockHitResult(hitVec, side, posNew, false);
            ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));

            if (canPlaceBlockIntoPosition(world, posNew, ctx))
            {
                //System.out.printf("tryPlaceBlock() pos: %s, side: %s, part: %s, hitVec: %s\n", posNew, side, hitPart, hitVec);
                return handleFlexibleBlockPlacement(controller, player, world, posNew, side, playerYaw, hitVec, hand, hitPart);
            }
            else
            {
                return ActionResult.PASS;
            }
        }

        if (isFirstClick == false && FeatureToggle.FAST_PLACEMENT_REMEMBER_ALWAYS.getBooleanValue())
        {
            return handleFlexibleBlockPlacement(controller, player, world, posIn, sideIn, playerYaw, hitVec, hand, null);
        }

        return processRightClickBlockWrapper(controller, player, world, simpleOffset ? posNew : posIn, sideIn, hitVec, hand);
    }

    private static boolean canPlaceBlockAgainst(World world, BlockPos pos, PlayerEntity player, Hand hand)
    {
        if (FeatureToggle.TWEAK_PLACEMENT_REST_FIRST.getBooleanValue())
        {
            BlockState state = world.getBlockState(pos);

            if (stackClickedOn.isEmpty() == false)
            {
                ItemStack stack = state.getBlock().getPickStack(world, pos, state);

                if (fi.dy.masa.malilib.util.InventoryUtils.areStacksEqual(stackClickedOn, stack) == false)
                {
                    return false;
                }
            }
            else
            {
                if (state != stateClickedOn)
                {
                    return false;
                }
            }
        }

        if (FeatureToggle.TWEAK_PLACEMENT_REST_HAND.getBooleanValue())
        {
            BlockState state = world.getBlockState(pos);
            ItemStack stackClicked = state.getBlock().getPickStack(world, pos, state);
            ItemStack stackHand = player.getStackInHand(hand);

            if (fi.dy.masa.malilib.util.InventoryUtils.areStacksEqual(stackClicked, stackHand) == false)
            {
                return false;
            }
        }

        return true;
    }

    private static boolean canUseItemWithRestriction(ItemRestriction restriction, PlayerEntity player)
    {
        ItemStack stack = player.getMainHandStack();

        if (stack.isEmpty() == false && restriction.isAllowed(stack.getItem()) == false)
        {
            return false;
        }

        stack = player.getOffHandStack();

        if (stack.isEmpty() == false && restriction.isAllowed(stack.getItem()) == false)
        {
            return false;
        }

        return true;
    }

    private static boolean canUseFastRightClick(PlayerEntity player)
    {
        if (canUseItemWithRestriction(FAST_RIGHT_CLICK_ITEM_RESTRICTION, player) == false)
        {
            return false;
        }

        HitResult trace = player.raycast(6, 0f, false);

        if (trace == null || trace.getType() != HitResult.Type.BLOCK)
        {
            return FAST_RIGHT_CLICK_BLOCK_RESTRICTION.isAllowed(Blocks.AIR);
        }

        Block block = player.getEntityWorld().getBlockState(((BlockHitResult) trace).getBlockPos()).getBlock();

        return FAST_RIGHT_CLICK_BLOCK_RESTRICTION.isAllowed(block);
    }

    private static void tryRestockHand(PlayerEntity player, Hand hand, ItemStack stackOriginal)
    {
        if (FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue())
        {
            ItemStack stackCurrent = player.getStackInHand(hand);

            if (stackOriginal.isEmpty() == false && player.inventory.selectedSlot == hotbarSlot &&
                (stackCurrent.isEmpty() || stackCurrent.isItemEqualIgnoreDamage(stackOriginal) == false))
            {
                // Don't allow taking stacks from elsewhere in the hotbar, if the cycle tweak is on
                boolean allowHotbar = FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getBooleanValue() == false &&
                                      FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER.getBooleanValue() == false;
                InventoryUtils.restockNewStackToHand(player, hand, stackOriginal, allowHotbar);
            }
        }
    }

    private static ActionResult processRightClickBlockWrapper(
            ClientPlayerInteractionManager controller,
            ClientPlayerEntity player,
            ClientWorld world,
            BlockPos posIn,
            Direction sideIn,
            Vec3d hitVecIn,
            Hand hand)
    {
        //System.out.printf("processRightClickBlockWrapper() start @ %s, side: %s, hand: %s\n", pos, side, hand);
        if (FeatureToggle.TWEAK_PLACEMENT_LIMIT.getBooleanValue() &&
            placementCount >= Configs.Generic.PLACEMENT_LIMIT.getIntegerValue())
        {
            return ActionResult.PASS;
        }

        // Don't allow taking stacks from elsewhere in the hotbar, if the cycle tweak is on
        boolean allowHotbar = FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getBooleanValue() == false &&
                              FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER.getBooleanValue() == false;

        InventoryUtils.preRestockHand(player, hand, allowHotbar);

        // We need to grab the stack here if the cached stack is still empty,
        // because this code runs before the cached stack gets set on the first click/use.
        BlockHitResult hitResult = new BlockHitResult(hitVecIn, sideIn, posIn, false);
        ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));
        BlockPos posPlacement = getPlacementPositionForTargetedPosition(world, posIn, sideIn, ctx);
        BlockState stateBefore = world.getBlockState(posPlacement);
        BlockState state = world.getBlockState(posIn);
        ItemStack stackOriginal;

        if (stackBeforeUse[hand.ordinal()].isEmpty() == false &&
            FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getBooleanValue() == false &&
            FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER.getBooleanValue() == false)
        {
            stackOriginal = stackBeforeUse[hand.ordinal()];
        }
        else
        {
            stackOriginal = player.getStackInHand(hand).copy();
        }

        if (FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.getBooleanValue() &&
            state.canReplace(ctx) == false && state.getMaterial().isReplaceable())
        {
            // If the block itself says it's not replaceable, but the material is (fluids),
            // then we need to offset the position back, otherwise the check in ItemBlock
            // will offset the position by one forward from the desired position.
            // FIXME This will break if the block behind the desired position is replaceable though... >_>
            posIn = posIn.offset(sideIn.getOpposite());
        }

        if (posFirst != null && isPositionAllowedByPlacementRestriction(posIn, sideIn) == false)
        {
            //System.out.printf("processRightClickBlockWrapper() PASS @ %s, side: %s\n", pos, side);
            return ActionResult.PASS;
        }

        final int afterClickerClickCount = MathHelper.clamp(Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getIntegerValue(), 0, 32);

        Direction facing = sideIn;
        boolean flexible = FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue();
        boolean rotationHeld = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ROTATION.getKeybind().isKeybindHeld();
        boolean rememberFlexible = FeatureToggle.REMEMBER_FLEXIBLE.getBooleanValue();
        boolean rotation = rotationHeld || (rememberFlexible && firstWasRotation);
        boolean accurate = FeatureToggle.TWEAK_ACCURATE_BLOCK_PLACEMENT.getBooleanValue();
        boolean keys = Hotkeys.ACCURATE_BLOCK_PLACEMENT_IN.getKeybind().isKeybindHeld() || Hotkeys.ACCURATE_BLOCK_PLACEMENT_REVERSE.getKeybind().isKeybindHeld();
        accurate = accurate && keys;

        // Carpet-Extra mod accurate block placement protocol support
        if (flexible && rotation && accurate == false &&
            FeatureToggle.CARPET_ACCURATE_PLACEMENT_PROTOCOL.getBooleanValue() &&
            isFacingValidFor(facing, stackOriginal))
        {
            facing = facing.getOpposite(); // go from block face to click on to the requested facing
            //double relX = hitVecIn.x - posIn.getX();
            //double x = posIn.getX() + relX + 2 + (facing.getId() * 2);
            double x = posIn.getX() + 2 + (facing.getId() * 2);

            if (FeatureToggle.TWEAK_AFTER_CLICKER.getBooleanValue())
            {
                x += afterClickerClickCount * 16;
            }

            //System.out.printf("processRightClickBlockWrapper req facing: %s, x: %.3f, pos: %s, sideIn: %s\n", facing, x, posIn, sideIn);
            hitVecIn = new Vec3d(x, hitVecIn.y, hitVecIn.z);
        }

        if (FeatureToggle.TWEAK_Y_MIRROR.getBooleanValue() && Hotkeys.PLACEMENT_Y_MIRROR.getKeybind().isKeybindHeld())
        {
            double y = 1 - hitVecIn.y + 2 * posIn.getY(); // = 1 - (hitVec.y - pos.getY()) + pos.getY();
            hitVecIn = new Vec3d(hitVecIn.x, y, hitVecIn.z);

            if (sideIn.getAxis() == Direction.Axis.Y)
            {
                posIn = posIn.offset(sideIn);
                sideIn = sideIn.getOpposite();
            }
        }

        if (FeatureToggle.TWEAK_PICK_BEFORE_PLACE.getBooleanValue())
        {
            InventoryUtils.switchToPickedBlock();
        }

        InventoryUtils.trySwapCurrentToolIfNearlyBroken();

        //System.out.printf("processRightClickBlockWrapper() pos: %s, side: %s, hitVec: %s\n", pos, side, hitVec);
        ActionResult result;

        if (InventoryUtils.canUnstackingItemNotFitInInventory(stackOriginal, player))
        {
            result = ActionResult.PASS;
        }
        else
        {
            //System.out.printf("processRightClickBlockWrapper() PLACE @ %s, side: %s, hit: %s\n", pos, side, hitVec);
            BlockHitResult context = new BlockHitResult(hitVecIn, sideIn, posIn, false);
            result = controller.interactBlock(player, world, hand, context);
        }

        if (result == ActionResult.SUCCESS)
        {
            placementCount++;
        }

        // This restock needs to happen even with the pick-before-place tweak active,
        // otherwise the fast placement mode's checks (getHandWithItem()) will fail...
        //System.out.printf("processRightClickBlockWrapper -> tryRestockHand with: %s, current: %s\n", stackOriginal, player.getHeldItem(hand));
        tryRestockHand(player, hand, stackOriginal);

        if (FeatureToggle.TWEAK_AFTER_CLICKER.getBooleanValue() &&
            FeatureToggle.CARPET_ACCURATE_PLACEMENT_PROTOCOL.getBooleanValue() == false &&
            world.getBlockState(posPlacement) != stateBefore)
        {
            for (int i = 0; i < afterClickerClickCount; i++)
            {
                //System.out.printf("processRightClickBlockWrapper() after-clicker - i: %d, pos: %s, side: %s, hitVec: %s\n", i, pos, side, hitVec);
                BlockHitResult context = new BlockHitResult(hitVecIn, sideIn, posPlacement, false);
                result = controller.interactBlock(player, world, hand, context);
            }
        }

        if (result == ActionResult.SUCCESS)
        {
            if (FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getBooleanValue())
            {
                int newSlot = player.inventory.selectedSlot + 1;

                if (newSlot >= 9 || newSlot >= Configs.Generic.HOTBAR_SLOT_CYCLE_MAX.getIntegerValue())
                {
                    newSlot = 0;
                }

                player.inventory.selectedSlot = newSlot;
            }
            else if (FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER.getBooleanValue())
            {
                int newSlot = player.getRandom().nextInt(Configs.Generic.HOTBAR_SLOT_RANDOMIZER_MAX.getIntegerValue());
                player.inventory.selectedSlot = newSlot;
            }
        }

        return result;
    }

    private static ActionResult handleFlexibleBlockPlacement(
            ClientPlayerInteractionManager controller,
            ClientPlayerEntity player,
            ClientWorld world,
            BlockPos pos,
            Direction side,
            float playerYaw,
            Vec3d hitVec,
            Hand hand,
            @Nullable HitPart hitPart)
    {
        Direction facing = Direction.fromHorizontal(MathHelper.floor((playerYaw * 4.0F / 360.0F) + 0.5D) & 3);
        float yawOrig = player.yaw;

        if (hitPart == HitPart.CENTER)
        {
            facing = facing.getOpposite();
        }
        else if (hitPart == HitPart.LEFT)
        {
            facing = facing.rotateYCounterclockwise();
        }
        else if (hitPart == HitPart.RIGHT)
        {
            facing = facing.rotateYClockwise();
        }

        player.yaw = facing.asRotation();
        player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(player.yaw, player.pitch, player.isOnGround()));

        //System.out.printf("handleFlexibleBlockPlacement() pos: %s, side: %s, facing orig: %s facing new: %s\n", pos, side, facingOrig, facing);
        ActionResult result = processRightClickBlockWrapper(controller, player, world, pos, side, hitVec, hand);

        player.yaw = yawOrig;
        player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(player.yaw, player.pitch, player.isOnGround()));

        return result;
    }

    private static void clearClickedBlockInfoUse()
    {
        posFirst = null;
        hitPartFirst = null;
        hitVecFirst = null;
        sideFirst = null;
        sideRotatedFirst = null;
        firstWasRotation = false;
        firstWasOffset = false;
        isFirstClick = true;
        placementCount = 0;
        stackClickedOn = ItemStack.EMPTY;
        stateClickedOn = null;
    }

    private static void clearClickedBlockInfoAttack()
    {
        posFirstBreaking = null;
        sideFirstBreaking = null;
    }

    private static Direction getRotatedFacing(Direction originalSide, Direction playerFacingH, HitPart hitPart)
    {
        if (originalSide.getAxis().isVertical())
        {
            switch (hitPart)
            {
                case LEFT:      return playerFacingH.rotateYClockwise();
                case RIGHT:     return playerFacingH.rotateYCounterclockwise();
                case BOTTOM:    return originalSide == Direction.UP   ? playerFacingH : playerFacingH.getOpposite();
                case TOP:       return originalSide == Direction.DOWN ? playerFacingH : playerFacingH.getOpposite();
                case CENTER:    return originalSide.getOpposite();
                default:        return originalSide;
            }
        }
        else
        {
            switch (hitPart)
            {
                case LEFT:      return originalSide.rotateYCounterclockwise();
                case RIGHT:     return originalSide.rotateYClockwise();
                case BOTTOM:    return Direction.UP;
                case TOP:       return Direction.DOWN;
                case CENTER:    return originalSide.getOpposite();
                default:        return originalSide;
            }
        }
    }

    private static boolean isPositionAllowedByPlacementRestriction(BlockPos pos, Direction side)
    {
        boolean restrictionEnabled = FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.getBooleanValue();
        boolean gridEnabled = FeatureToggle.TWEAK_PLACEMENT_GRID.getBooleanValue();

        if (restrictionEnabled == false && gridEnabled == false)
        {
            return true;
        }

        int gridSize = Configs.Generic.PLACEMENT_GRID_SIZE.getIntegerValue();
        PlacementRestrictionMode mode = (PlacementRestrictionMode) Configs.Generic.PLACEMENT_RESTRICTION_MODE.getOptionListValue();

        return isPositionAllowedByRestrictions(pos, side, posFirst, sideFirst, restrictionEnabled, mode, gridEnabled, gridSize);
    }

    public static boolean isPositionAllowedByBreakingRestriction(BlockPos pos, Direction side)
    {
        boolean restrictionEnabled = FeatureToggle.TWEAK_BREAKING_RESTRICTION.getBooleanValue();
        boolean gridEnabled = FeatureToggle.TWEAK_BREAKING_GRID.getBooleanValue();

        if (restrictionEnabled == false && gridEnabled == false)
        {
            return true;
        }

        int gridSize = Configs.Generic.BREAKING_GRID_SIZE.getIntegerValue();
        PlacementRestrictionMode mode = (PlacementRestrictionMode) Configs.Generic.BREAKING_RESTRICTION_MODE.getOptionListValue();

        return posFirstBreaking == null || isPositionAllowedByRestrictions(pos, side, posFirstBreaking, sideFirstBreaking, restrictionEnabled, mode, gridEnabled, gridSize);
    }

    private static boolean isPositionAllowedByRestrictions(BlockPos pos, Direction side,
            BlockPos posFirst, Direction sideFirst, boolean restrictionEnabled, PlacementRestrictionMode mode, boolean gridEnabled, int gridSize)
    {
        if (gridEnabled)
        {
            if ((Math.abs(pos.getX() - posFirst.getX()) % gridSize) != 0 ||
                (Math.abs(pos.getY() - posFirst.getY()) % gridSize) != 0 ||
                (Math.abs(pos.getZ() - posFirst.getZ()) % gridSize) != 0)
            {
                return false;
            }
        }

        if (restrictionEnabled)
        {
            switch (mode)
            {
                case COLUMN:    return isNewPositionValidForColumnMode(pos, posFirst, sideFirst);
                case DIAGONAL:  return isNewPositionValidForDiagonalMode(pos, posFirst, sideFirst);
                case FACE:      return isNewPositionValidForFaceMode(pos, side, sideFirst);
                case LAYER:     return isNewPositionValidForLayerMode(pos, posFirst, sideFirst);
                case LINE:      return isNewPositionValidForLineMode(pos, posFirst, sideFirst);
                case PLANE:     return isNewPositionValidForPlaneMode(pos, posFirst, sideFirst);
                default:        return true;
            }
        }
        else
        {
            return true;
        }
    }

    private static boolean isFacingValidFor(Direction facing, ItemStack stack)
    {
        Item item = stack.getItem();

        if (stack.isEmpty() == false && item instanceof BlockItem)
        {
            Block block = ((BlockItem) item).getBlock();
            BlockState state = block.getDefaultState();

            for (Property<?> prop : state.getProperties())
            {
                if (prop instanceof DirectionProperty)
                {
                    return ((DirectionProperty) prop).getValues().contains(facing);
                }
            }
        }

        return false;
    }

    private static BlockPos getPlacementPositionForTargetedPosition(World world, BlockPos pos, Direction side, ItemPlacementContext useContext)
    {
        if (canPlaceBlockIntoPosition(world, pos, useContext))
        {
            return pos;
        }

        return pos.offset(side);
    }

    private static boolean canPlaceBlockIntoPosition(World world, BlockPos pos, ItemPlacementContext useContext)
    {
        BlockState state = world.getBlockState(pos);
        return state.canReplace(useContext) || state.getMaterial().isLiquid() || state.getMaterial().isReplaceable();
    }

    private static boolean isNewPositionValidForColumnMode(BlockPos posNew, BlockPos posFirst, Direction sideFirst)
    {
        Direction.Axis axis = sideFirst.getAxis();

        switch (axis)
        {
            case X: return posNew.getY() == posFirst.getY() && posNew.getZ() == posFirst.getZ();
            case Y: return posNew.getX() == posFirst.getX() && posNew.getZ() == posFirst.getZ();
            case Z: return posNew.getX() == posFirst.getX() && posNew.getY() == posFirst.getY();

            default:
                return false;
        }
    }

    private static boolean isNewPositionValidForDiagonalMode(BlockPos posNew, BlockPos posFirst, Direction sideFirst)
    {
        Direction.Axis axis = sideFirst.getAxis();
        BlockPos relativePos = posNew.subtract(posFirst);

        switch (axis)
        {
            case X: return posNew.getX() == posFirst.getX() && Math.abs(relativePos.getY()) == Math.abs(relativePos.getZ());
            case Y: return posNew.getY() == posFirst.getY() && Math.abs(relativePos.getX()) == Math.abs(relativePos.getZ());
            case Z: return posNew.getZ() == posFirst.getZ() && Math.abs(relativePos.getX()) == Math.abs(relativePos.getY());

            default:
                return false;
        }
    }

    private static boolean isNewPositionValidForFaceMode(BlockPos posNew, Direction side, Direction sideFirst)
    {
        return side == sideFirst;
    }

    private static boolean isNewPositionValidForLayerMode(BlockPos posNew, BlockPos posFirst, Direction sideFirst)
    {
        return posNew.getY() == posFirst.getY();
    }

    private static boolean isNewPositionValidForLineMode(BlockPos posNew, BlockPos posFirst, Direction sideFirst)
    {
        Direction.Axis axis = sideFirst.getAxis();

        switch (axis)
        {
            case X: return posNew.getX() == posFirst.getX() && (posNew.getY() == posFirst.getY() || posNew.getZ() == posFirst.getZ());
            case Y: return posNew.getY() == posFirst.getY() && (posNew.getX() == posFirst.getX() || posNew.getZ() == posFirst.getZ());
            case Z: return posNew.getZ() == posFirst.getZ() && (posNew.getX() == posFirst.getX() || posNew.getY() == posFirst.getY());

            default:
                return false;
        }
    }

    private static boolean isNewPositionValidForPlaneMode(BlockPos posNew, BlockPos posFirst, Direction sideFirst)
    {
        Direction.Axis axis = sideFirst.getAxis();

        switch (axis)
        {
            case X: return posNew.getX() == posFirst.getX();
            case Y: return posNew.getY() == posFirst.getY();
            case Z: return posNew.getZ() == posFirst.getZ();

            default:
                return false;
        }
    }

    /*
    @Nullable
    private static Direction getPlayerMovementDirection(PlayerEntitySP player)
    {
        double dx = player.posX - playerPosLast.x;
        double dy = player.posY - playerPosLast.y;
        double dz = player.posZ - playerPosLast.z;
        double ax = Math.abs(dx);
        double ay = Math.abs(dy);
        double az = Math.abs(dz);

        if (Math.max(Math.max(ax, az), ay) < 0.001)
        {
            return null;
        }

        if (ax > az)
        {
            if (ax > ay)
            {
                return dx > 0 ? Direction.EAST : Direction.WEST;
            }
            else
            {
                return dy > 0 ? Direction.UP : Direction.DOWN;
            }
        }
        else
        {
            if (az > ay)
            {
                return dz > 0 ? Direction.SOUTH : Direction.NORTH;
            }
            else
            {
                return dy > 0 ? Direction.UP : Direction.DOWN;
            }
        }
    }

    @Nullable
    private static Hand getHandWithItem(ItemStack stack, PlayerEntitySP player)
    {
        if (InventoryUtils.areStacksEqualIgnoreDurability(player.getHeldItemMainhand(), stackFirst))
        {
            return Hand.MAIN;
        }

        if (InventoryUtils.areStacksEqualIgnoreDurability(player.getHeldItemOffhand(), stackFirst))
        {
            return Hand.OFF;
        }

        return null;
    }
    */

    public static boolean shouldSkipSlotSync(int slotNumber, ItemStack newStack)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        ScreenHandler container = player != null ? player.currentScreenHandler : null;

        if (Configs.Generic.SLOT_SYNC_WORKAROUND.getBooleanValue() &&
            FeatureToggle.TWEAK_PICK_BEFORE_PLACE.getBooleanValue() == false &&
            container != null && container == player.playerScreenHandler &&
            (slotNumber == 45 || (slotNumber - 36) == player.inventory.selectedSlot))
        {
            if (mc.options.keyUse.isPressed() &&
                (Configs.Generic.SLOT_SYNC_WORKAROUND_ALWAYS.getBooleanValue() ||
                 FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getBooleanValue() ||
                 FeatureToggle.TWEAK_FAST_RIGHT_CLICK.getBooleanValue()))
            {
                return true;
            }

            if (mc.options.keyAttack.isPressed() &&
                FeatureToggle.TWEAK_FAST_LEFT_CLICK.getBooleanValue())
            {
                return true;
            }
        }

        return false;
    }
}
