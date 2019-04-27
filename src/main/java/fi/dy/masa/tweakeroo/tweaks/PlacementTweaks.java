package fi.dy.masa.tweakeroo.tweaks;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.util.BlockUtils;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.util.IMinecraftAccessor;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.ItemRestriction;
import fi.dy.masa.tweakeroo.util.PlacementRestrictionMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.packet.PlayerMoveC2SPacket;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlacementTweaks
{
    private static BlockPos posFirst = null;
    private static BlockPos posLast = null;
    private static HitPart hitPartFirst = null;
    private static Hand handFirst = Hand.MAIN;
    private static Vec3d hitVecFirst = null;
    private static Direction sideFirst = null;
    private static Direction sideRotatedFirst = null;
    private static float playerYawFirst;
    private static ItemStack stackFirst = ItemStack.EMPTY;
    private static ItemStack[] stackBeforeUse = new ItemStack[] { ItemStack.EMPTY, ItemStack.EMPTY };
    private static boolean isFirstClick;
    private static boolean isEmulatedClick;
    private static boolean firstWasRotation;
    private static boolean firstWasOffset;
    private static int placementCount;
    private static ItemStack stackClickedOn = ItemStack.EMPTY;
    @Nullable private static BlockState stateClickedOn = null;
    public static final ItemRestriction FAST_RIGHT_CLICK_RESTRICTION = new ItemRestriction();

    public static void onTick(MinecraftClient mc)
    {
        if (mc.currentScreen == null)
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
            clearClickedBlockInfo();

            // Clear the cached stack when releasing both keys, so that the restock doesn't happen when
            // using another another item or an empty hand.
            if (mc.options.keyAttack.isPressed() == false)
            {
                stackBeforeUse[0] = ItemStack.EMPTY;
                stackBeforeUse[1] = ItemStack.EMPTY;
            }
        }
    }

    public static boolean onProcessRightClickPre(PlayerEntity player, Hand hand)
    {
        InventoryUtils.trySwapCurrentToolIfNearlyBroken();

        ItemStack stackOriginal = player.getStackInHand(hand);

        if (isEmulatedClick == false &&
            FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue() &&
            stackOriginal.isEmpty() == false)
        {
            //System.out.printf("onProcessRightClickPre storing stack: %s\n", stackOriginal);
            stackBeforeUse[hand.ordinal()] = stackOriginal.copy();
        }

        return InventoryUtils.canUnstackingItemNotFitInInventory(stackOriginal, player);
    }

    public static void onProcessRightClickPost(PlayerEntity player, Hand hand)
    {
        if (FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue())
        {
            //System.out.printf("onProcessRightClickPost -> tryRestockHand with: %s, current: %s\n", stackBeforeUse[hand.ordinal()], player.getStackInHand(hand));
            tryRestockHand(player, hand, stackBeforeUse[hand.ordinal()]);
        }
    }

    public static void onLeftClickMousePre()
    {
        onProcessRightClickPre(MinecraftClient.getInstance().player, Hand.MAIN);
    }

    public static void onLeftClickMousePost()
    {
        onProcessRightClickPost(MinecraftClient.getInstance().player, Hand.MAIN);
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
                ((IMinecraftAccessor) mc).leftClickMouseAccessor();
                isEmulatedClick = false;
            }
        }
        else
        {
            InventoryUtils.trySwapCurrentToolIfNearlyBroken();
        }
    }

    private static void onUsingTick()
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null)
        {
            return;
        }

        if (posFirst != null && FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getBooleanValue())
        {
            ClientPlayerEntity player = mc.player;
            World world = player.getEntityWorld();
            final double reach = mc.interactionManager.getReachDistance();
            final int maxCount = Configs.Generic.FAST_BLOCK_PLACEMENT_COUNT.getIntegerValue();

            mc.hitResult = player.rayTrace(reach, mc.getTickDelta(), false);

            for (int i = 0; i < maxCount; ++i)
            {
                HitResult trace = mc.hitResult;

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
                        mc.hitResult = player.rayTrace(reach, mc.getTickDelta(), false);
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
            ((IMinecraftAccessor) mc).setRightClickDelayTimer(4);
        }
        else if (FeatureToggle.TWEAK_FAST_RIGHT_CLICK.getBooleanValue() &&
                mc.options.keyUse.isPressed() &&
                canUseFastRightClick(mc.player))
        {
            final int count = Configs.Generic.FAST_RIGHT_CLICK_COUNT.getIntegerValue();

            for (int i = 0; i < count; ++i)
            {
                isEmulatedClick = true;
                ((IMinecraftAccessor) mc).rightClickMouseAccessor();
                isEmulatedClick = false;
            }
        }
    }

    public static ActionResult onProcessRightClickBlock(
            ClientPlayerInteractionManager controller,
            ClientPlayerEntity player,
            ClientWorld world,
            BlockPos posIn,
            Direction sideIn,
            Vec3d hitVec,
            Hand hand)
    {
        boolean restricted = FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.getBooleanValue() || FeatureToggle.TWEAK_PLACEMENT_GRID.getBooleanValue();
        ItemStack stackPre = player.getStackInHand(hand).copy();
        Direction playerFacingH = player.getHorizontalFacing();
        HitPart hitPart = getHitPart(sideIn, playerFacingH, posIn, hitVec);
        Direction sideRotated = getRotatedFacing(sideIn, playerFacingH, hitPart);

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
            BlockHitResult hitResult = new BlockHitResult(hitVec, sideIn, posIn, false);
            ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));
            posFirst = getPlacementPositionForTargetedPosition(world, posIn, sideIn, ctx);
            posLast = posFirst;
            hitPartFirst = hitPart;
            handFirst = hand;
            hitVecFirst = hitVec.subtract(posFirst.getX(), posFirst.getY(), posFirst.getZ());
            sideFirst = sideIn;
            sideRotatedFirst = sideRotated;
            playerYawFirst = player.yaw;
            stackFirst = stackPre;
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
        boolean handle = false;
        BlockPos posNew = null;
        boolean flexible = FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue();
        boolean rotationHeld = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ROTATION.getKeybind().isKeybindHeld();
        boolean offsetHeld = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_OFFSET.getKeybind().isKeybindHeld();
        boolean rememberFlexible = FeatureToggle.REMEMBER_FLEXIBLE.getBooleanValue();
        boolean rotation = rotationHeld || (rememberFlexible && firstWasRotation);
        boolean offset = offsetHeld || (rememberFlexible && firstWasOffset);
        ItemStack stack = player.getStackInHand(hand);

        if (flexible)
        {
            BlockHitResult hitResult = new BlockHitResult(hitVec, sideIn, posIn, false);
            ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));
            posNew = isFirstClick && (rotation || offset) ? getPlacementPositionForTargetedPosition(world, posIn, sideIn, ctx) : posIn;

            // Place the block facing/against the adjacent block (= just rotated from normal)
            if (rotation)
            {
                side = sideRotatedIn;
                handle = true;
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
                handle = true;
            }
        }

        if (FeatureToggle.TWEAK_ACCURATE_BLOCK_PLACEMENT.getBooleanValue())
        {
            Direction facing = side;

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

            if (Hotkeys.ACCURATE_BLOCK_PLACEMENT_IN.getKeybind().isKeybindHeld())
            {
                facing = sideIn;
                hitPart = null;
                handle = true;
                //System.out.printf("accurate - IN - facing: %s\n", facing);
            }
            else if (flexible == false || (rotation == false && offset == false))
            {
                if (stack.getItem() instanceof BlockItem)
                {
                    BlockHitResult hitResult = new BlockHitResult(hitVec, sideIn, posNew, false);
                    ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));
                    BlockItem item = (BlockItem) stack.getItem();
                    BlockState state = item.getBlock().getPlacementState(ctx);

                    // getStateForPlacement can return null in 1.13+
                    if (state == null)
                    {
                        return ActionResult.PASS;
                    }

                    Direction facingTmp = BlockUtils.getFirstPropertyFacingValue(state);

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

            if (Hotkeys.ACCURATE_BLOCK_PLACEMENT_REVERSE.getKeybind().isKeybindHeld())
            {
                facing = facing.getOpposite();
                hitPart = null;
                handle = true;
                //System.out.printf("accurate - REVERSE - facing: %s\n", facing);
            }

            if (handle || flexible)
            {
                // Carpet mod accurate block placement protocol support, for Carpet v18_04_24 or later
                double x = isFacingValidFor(facing, stack) ? facing.getId() + 2 + posNew.getX() : hitVec.x;
                int afterClickerClickCount = MathHelper.clamp(Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getIntegerValue(), 0, 32);

                if (FeatureToggle.TWEAK_AFTER_CLICKER.getBooleanValue())
                {
                    x += afterClickerClickCount * 10;
                }

                //System.out.printf("processRightClickBlockWrapper facing: %s, x: %.3f, pos: %s, side: %s\n", facing, x, pos, side);
                hitVec = new Vec3d(x, hitVec.y, hitVec.z);
                //System.out.printf("accurate - hitVec: %s\n", hitVec);
            }

            //System.out.printf("accurate - facing: %s, side: %s, posNew: %s, hit: %s\n", facing, side, posNew, hitVec);
            return processRightClickBlockWrapper(controller, player, world, posNew, side, hitVec, hand);
        }

        if (handle)
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

        return processRightClickBlockWrapper(controller, player, world, posIn, sideIn, hitVec, hand);
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

    private static boolean canUseFastRightClick(PlayerEntity player)
    {
        ItemStack stack = player.getMainHandStack();

        if (stack.isEmpty() == false && FAST_RIGHT_CLICK_RESTRICTION.isItemAllowed(stack) == false)
        {
            return false;
        }

        stack = player.getOffHandStack();

        if (stack.isEmpty() == false && FAST_RIGHT_CLICK_RESTRICTION.isItemAllowed(stack) == false)
        {
            return false;
        }

        return true;
    }

    private static void tryRestockHand(PlayerEntity player, Hand hand, ItemStack stackOriginal)
    {
        ItemStack stackCurrent = player.getStackInHand(hand);

        if (stackOriginal.isEmpty() == false &&
            (stackCurrent.isEmpty() || fi.dy.masa.malilib.util.InventoryUtils.areStacksEqualIgnoreDurability(stackOriginal, stackCurrent) == false))
        {
            // Don't allow taking stacks from elsewhere in the hotbar, if the cycle tweak is on
            boolean allowHotbar = FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getBooleanValue() == false;
            InventoryUtils.restockNewStackToHand(player, hand, stackOriginal, allowHotbar);
        }
    }

    private static ActionResult processRightClickBlockWrapper(
            ClientPlayerInteractionManager controller,
            ClientPlayerEntity player,
            ClientWorld world,
            BlockPos pos,
            Direction side,
            Vec3d hitVec,
            Hand hand)
    {
        //System.out.printf("processRightClickBlockWrapper() start @ %s, side: %s\n", pos, side);
        if (FeatureToggle.TWEAK_PLACEMENT_LIMIT.getBooleanValue() &&
            placementCount >= Configs.Generic.PLACEMENT_LIMIT.getIntegerValue())
        {
            return ActionResult.PASS;
        }

        // We need to grab the stack here if the cached stack is still empty,
        // because this code runs before the cached stack gets set on the first click/use.
        ItemStack stackOriginal = stackFirst.isEmpty() == false && FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getBooleanValue() == false ? stackFirst : player.getStackInHand(hand).copy();
        BlockHitResult hitResult = new BlockHitResult(hitVec, side, pos, false);
        ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));
        BlockPos posPlacement = getPlacementPositionForTargetedPosition(world, pos, side, ctx);
        BlockState stateBefore = world.getBlockState(posPlacement);
        BlockState state = world.getBlockState(pos);

        if (FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.getBooleanValue() &&
            state.canReplace(ctx) == false && state.getMaterial().isReplaceable())
        {
            // If the block itself says it's not replaceable, but the material is (fluids),
            // then we need to offset the position back, otherwise the check in ItemBlock
            // will offset the position by one forward from the desired position.
            // FIXME This will break if the block behind the desired position is replaceable though... >_>
            pos = pos.offset(side.getOpposite());
        }

        if (posFirst != null && isPositionAllowedByPlacementRestriction(pos, side) == false)
        {
            //System.out.printf("processRightClickBlockWrapper() PASS @ %s, side: %s\n", pos, side);
            return ActionResult.PASS;
        }

        final int afterClickerClickCount = MathHelper.clamp(Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getIntegerValue(), 0, 32);

        //Direction facing = side;
        //boolean flexible = FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue();
        //boolean accurate = FeatureToggle.TWEAK_ACCURATE_BLOCK_PLACEMENT.getBooleanValue();
        //boolean accurateActive = FeatureToggle.TWEAK_ACCURATE_BLOCK_PLACEMENT.getBooleanValue() || (Hotkeys.ACCURATE_BLOCK_PLACEMENT_IN.getKeybind().isKeybindHeld() || Hotkeys.ACCURATE_BLOCK_PLACEMENT_REVERSE.getKeybind().isKeybindHeld());

        /*
        if (accurate)
        {
            if (Hotkeys.ACCURATE_BLOCK_PLACEMENT_IN.getKeybind().isKeybindHeld())
            {
                facing = side;
                accurateActive = true;
            }

            if (Hotkeys.ACCURATE_BLOCK_PLACEMENT_REVERSE.getKeybind().isKeybindHeld())
            {
                facing = facing.getOpposite();
                accurateActive = true;
            }
        }

        // Carpet mod accurate block placement protocol support, for Carpet v18_04_24 or later
        if ((accurateActive || flexible) &&
            FeatureToggle.CARPET_ACCURATE_BLOCK_PLACEMENT.getBooleanValue() &&
            isFacingValidFor(facing, stackOriginal))
        {
            double x = facing.getIndex() + 2 + pos.getX();

            if (FeatureToggle.TWEAK_AFTER_CLICKER.getBooleanValue())
            {
                x += afterClickerClickCount * 10;
            }

            //System.out.printf("processRightClickBlockWrapper facing: %s, x: %.3f, pos: %s, side: %s\n", facing, x, pos, side);
            hitVec = new Vec3d(x, hitVec.y, hitVec.z);
        }
        */

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
            BlockHitResult context = new BlockHitResult(hitVec, side, pos, false);
            result = controller.interactBlock(player, world, hand, context);
        }

        if (result == ActionResult.SUCCESS)
        {
            placementCount++;
        }

        if (FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue())
        {
            // This restock needs to happen even with the pick-before-place tweak active,
            // otherwise the fast placement mode's checks (getHandWithItem()) will fail...
            //System.out.printf("processRightClickBlockWrapper -> tryRestockHand with: %s, current: %s\n", stackOriginal, player.getStackInHand(hand));
            tryRestockHand(player, hand, stackOriginal);
        }

        if (FeatureToggle.TWEAK_AFTER_CLICKER.getBooleanValue() &&
            FeatureToggle.CARPET_ACCURATE_BLOCK_PLACEMENT.getBooleanValue() == false &&
            world.getBlockState(posPlacement) != stateBefore)
        {
            for (int i = 0; i < afterClickerClickCount; i++)
            {
                //System.out.printf("processRightClickBlockWrapper() after-clicker - i: %d, pos: %s, side: %s, hitVec: %s\n", i, pos, side, hitVec);
                BlockHitResult context = new BlockHitResult(hitVec, side, posPlacement, false);
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
        player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(player.yaw, player.pitch, player.onGround));

        //System.out.printf("handleFlexibleBlockPlacement() pos: %s, side: %s, facing orig: %s facing new: %s\n", pos, side, facingOrig, facing);
        ActionResult result = processRightClickBlockWrapper(controller, player, world, pos, side, hitVec, hand);

        player.yaw = yawOrig;
        player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(player.yaw, player.pitch, player.onGround));

        return result;
    }

    private static void clearClickedBlockInfo()
    {
        posFirst = null;
        hitPartFirst = null;
        hitVecFirst = null;
        sideFirst = null;
        sideRotatedFirst = null;
        stackFirst = ItemStack.EMPTY;
        firstWasRotation = false;
        firstWasOffset = false;
        isFirstClick = true;
        placementCount = 0;
        stackClickedOn = ItemStack.EMPTY;
        stateClickedOn = null;
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

    public static HitPart getHitPart(Direction originalSide, Direction playerFacingH, BlockPos pos, Vec3d hitVec)
    {
        double x = hitVec.x - pos.getX();
        double y = hitVec.y - pos.getY();
        double z = hitVec.z - pos.getZ();
        double posH = 0;
        double posV = 0;

        switch (originalSide)
        {
            case DOWN:
            case UP:
                switch (playerFacingH)
                {
                    case NORTH:
                        posH = x;
                        posV = 1.0d - z;
                        break;
                    case SOUTH:
                        posH = 1.0d - x;
                        posV = z;
                        break;
                    case WEST:
                        posH = 1.0d - z;
                        posV = 1.0d - x;
                        break;
                    case EAST:
                        posH = z;
                        posV = x;
                        break;
                    default:
                }

                if (originalSide == Direction.DOWN)
                {
                    posV = 1.0d - posV;
                }

                break;
            case NORTH:
            case SOUTH:
                posH = originalSide.getDirection() == AxisDirection.POSITIVE ? x : 1.0d - x;
                posV = y;
                break;
            case WEST:
            case EAST:
                posH = originalSide.getDirection() == AxisDirection.NEGATIVE ? z : 1.0d - z;
                posV = y;
                break;
        }

        double offH = Math.abs(posH - 0.5d);
        double offV = Math.abs(posV - 0.5d);

        if (offH > 0.25d || offV > 0.25d)
        {
            if (offH > offV)
            {
                return posH < 0.5d ? HitPart.LEFT : HitPart.RIGHT;
            }
            else
            {
                return posV < 0.5d ? HitPart.BOTTOM : HitPart.TOP;
            }
        }
        else
        {
            return HitPart.CENTER;
        }
    }

    private static boolean isPositionAllowedByPlacementRestriction(BlockPos pos, Direction side)
    {
        PlacementRestrictionMode mode = (PlacementRestrictionMode) Configs.Generic.PLACEMENT_RESTRICTION_MODE.getOptionListValue();

        if (FeatureToggle.TWEAK_PLACEMENT_GRID.getBooleanValue())
        {
            int grid = Configs.Generic.PLACEMENT_GRID_SIZE.getIntegerValue();

            if ((Math.abs(pos.getX() - posFirst.getX()) % grid) != 0 ||
                (Math.abs(pos.getY() - posFirst.getY()) % grid) != 0 ||
                (Math.abs(pos.getZ() - posFirst.getZ()) % grid) != 0)
            {
                return false;
            }
        }

        if (FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.getBooleanValue())
        {
            switch (mode)
            {
                case PLANE:     return isNewPositionValidForPlaneMode(pos);
                case FACE:      return isNewPositionValidForFaceMode(pos, side);
                case COLUMN:    return isNewPositionValidForColumnMode(pos);
                case LINE:      return isNewPositionValidForLineMode(pos);
                case DIAGONAL:  return isNewPositionValidForDiagonalMode(pos);
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

    private static boolean isNewPositionValidForPlaneMode(BlockPos posNew)
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

    private static boolean isNewPositionValidForFaceMode(BlockPos posNew, Direction side)
    {
        return side == sideFirst;
    }

    private static boolean isNewPositionValidForColumnMode(BlockPos posNew)
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

    private static boolean isNewPositionValidForLineMode(BlockPos posNew)
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

    private static boolean isNewPositionValidForDiagonalMode(BlockPos posNew)
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
        Container container = player != null ? player.container : null;

        if (Configs.Generic.SLOT_SYNC_WORKAROUND.getBooleanValue() &&
            FeatureToggle.TWEAK_PICK_BEFORE_PLACE.getBooleanValue() == false &&
            container != null && container == player.playerContainer &&
            (slotNumber == 45 || (slotNumber - 36) == player.inventory.selectedSlot))
        {
            boolean featRight = FeatureToggle.TWEAK_FAST_RIGHT_CLICK.getBooleanValue();
            boolean featLeft = FeatureToggle.TWEAK_FAST_LEFT_CLICK.getBooleanValue();

            if ((featRight && mc.options.keyUse.isPressed()) ||
                (featLeft && mc.options.keyAttack.isPressed()))
            {
                return true;
            }
        }

        return false;
    }

    public enum HitPart
    {
        CENTER,
        LEFT,
        RIGHT,
        BOTTOM,
        TOP;
    }
}
