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
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IProperty;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlacementTweaks
{
    private static BlockPos posFirst = null;
    private static BlockPos posLast = null;
    private static HitPart hitPartFirst = null;
    private static EnumHand handFirst = EnumHand.MAIN_HAND;
    private static Vec3d hitVecFirst = null;
    private static EnumFacing sideFirst = null;
    private static EnumFacing sideRotatedFirst = null;
    private static float playerYawFirst;
    private static ItemStack stackFirst = ItemStack.EMPTY;
    private static ItemStack[] stackBeforeUse = new ItemStack[] { ItemStack.EMPTY, ItemStack.EMPTY };
    private static boolean isFirstClick;
    private static boolean isEmulatedClick;
    private static boolean firstWasRotation;
    private static boolean firstWasOffset;
    private static int placementCount;
    private static ItemStack stackClickedOn = ItemStack.EMPTY;
    @Nullable private static IBlockState stateClickedOn = null;
    public static final ItemRestriction FAST_RIGHT_CLICK_RESTRICTION = new ItemRestriction();

    public static void onTick(Minecraft mc)
    {
        if (mc.currentScreen == null)
        {
            if (mc.gameSettings.keyBindUseItem.isKeyDown())
            {
                onUsingTick();
            }

            if (mc.gameSettings.keyBindAttack.isKeyDown())
            {
                onAttackTick(mc);
            }
        }
        else
        {
            stackBeforeUse[0] = ItemStack.EMPTY;
            stackBeforeUse[1] = ItemStack.EMPTY;
        }

        if (mc.gameSettings.keyBindUseItem.isKeyDown() == false)
        {
            clearClickedBlockInfo();

            // Clear the cached stack when releasing both keys, so that the restock doesn't happen when
            // using another another item or an empty hand.
            if (mc.gameSettings.keyBindAttack.isKeyDown() == false)
            {
                stackBeforeUse[0] = ItemStack.EMPTY;
                stackBeforeUse[1] = ItemStack.EMPTY;
            }
        }
    }

    public static boolean onProcessRightClickPre(EntityPlayer player, EnumHand hand)
    {
        InventoryUtils.trySwapCurrentToolIfNearlyBroken();

        ItemStack stackOriginal = player.getHeldItem(hand);

        if (isEmulatedClick == false &&
            FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue() &&
            stackOriginal.isEmpty() == false)
        {
            //System.out.printf("onProcessRightClickPre storing stack: %s\n", stackOriginal);
            stackBeforeUse[hand.ordinal()] = stackOriginal.copy();
        }

        return InventoryUtils.canUnstackingItemNotFitInInventory(stackOriginal, player);
    }

    public static void onProcessRightClickPost(EntityPlayer player, EnumHand hand)
    {
        if (FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue())
        {
            //System.out.printf("onProcessRightClickPost -> tryRestockHand with: %s, current: %s\n", stackBeforeUse[hand.ordinal()], player.getHeldItem(hand));
            tryRestockHand(player, hand, stackBeforeUse[hand.ordinal()]);
        }
    }

    public static void onLeftClickMousePre()
    {
        onProcessRightClickPre(Minecraft.getInstance().player, EnumHand.MAIN_HAND);
    }

    public static void onLeftClickMousePost()
    {
        onProcessRightClickPost(Minecraft.getInstance().player, EnumHand.MAIN_HAND);
    }

    private static void onAttackTick(Minecraft mc)
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
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null)
        {
            return;
        }

        if (posFirst != null && FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getBooleanValue())
        {
            EntityPlayerSP player = mc.player;
            World world = player.getEntityWorld();
            final double reach = mc.playerController.getBlockReachDistance();
            final int maxCount = Configs.Generic.FAST_BLOCK_PLACEMENT_COUNT.getIntegerValue();

            mc.objectMouseOver = player.rayTrace(reach, mc.getRenderPartialTicks(), RayTraceFluidMode.NEVER);

            for (int i = 0; i < maxCount; ++i)
            {
                RayTraceResult trace = mc.objectMouseOver;

                if (trace == null || trace.type != RayTraceResult.Type.BLOCK)
                {
                    break;
                }

                EnumHand hand = handFirst;
                EnumFacing side = trace.sideHit;
                BlockPos pos = trace.getBlockPos();
                Vec3d hitVec = trace.hitVec;
                ItemStack stack = player.getHeldItem(hand);
                BlockItemUseContext ctx = new BlockItemUseContext(new ItemUseContext(player, stack, pos, side, (float) hitVec.x, (float) hitVec.y, (float) hitVec.z));
                BlockPos posNew = getPlacementPositionForTargetedPosition(world, pos, side, ctx);
                ctx = new BlockItemUseContext(new ItemUseContext(player, stack, posNew, side, (float) hitVec.x, (float) hitVec.y, (float) hitVec.z));

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
                    EnumActionResult result = tryPlaceBlock(mc.playerController, player, mc.world,
                            posNew, sideFirst, sideRotatedFirst, playerYawFirst, hitVec, hand, hitPartFirst, false);

                    if (result == EnumActionResult.SUCCESS)
                    {
                        posLast = posNew;
                        mc.objectMouseOver = player.rayTrace(reach, mc.getRenderPartialTicks(), RayTraceFluidMode.NEVER);
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
                mc.gameSettings.keyBindUseItem.isKeyDown() &&
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

    public static EnumActionResult onProcessRightClickBlock(
            PlayerControllerMP controller,
            EntityPlayerSP player,
            WorldClient world,
            BlockPos posIn,
            EnumFacing sideIn,
            Vec3d hitVec,
            EnumHand hand)
    {
        boolean restricted = FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.getBooleanValue() || FeatureToggle.TWEAK_PLACEMENT_GRID.getBooleanValue();
        ItemStack stackPre = player.getHeldItem(hand).copy();
        EnumFacing playerFacingH = player.getHorizontalFacing();
        HitPart hitPart = getHitPart(sideIn, playerFacingH, posIn, hitVec);
        EnumFacing sideRotated = getRotatedFacing(sideIn, playerFacingH, hitPart);

        if (FeatureToggle.TWEAK_PLACEMENT_REST_FIRST.getBooleanValue() && stateClickedOn == null)
        {
            IBlockState state = world.getBlockState(posIn);
            stackClickedOn = state.getBlock().getItem(world, posIn, state);
            stateClickedOn = state;
        }

        if (canPlaceBlockAgainst(world, posIn, player, hand) == false)
        {
            return EnumActionResult.PASS;
        }

        //System.out.printf("onProcessRightClickBlock() pos: %s, side: %s, part: %s, hitVec: %s\n", posIn, sideIn, hitPart, hitVec);
        EnumActionResult result = tryPlaceBlock(controller, player, world, posIn, sideIn, sideRotated, player.rotationYaw, hitVec, hand, hitPart, true);

        // Store the initial click data for the fast placement mode
        if (posFirst == null && result == EnumActionResult.SUCCESS && restricted)
        {
            boolean flexible = FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue();
            boolean accurate = FeatureToggle.TWEAK_ACCURATE_BLOCK_PLACEMENT.getBooleanValue();
            boolean rotation = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ROTATION.getKeybind().isKeybindHeld();
            boolean offset = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_OFFSET.getKeybind().isKeybindHeld();
            boolean accurateIn = Hotkeys.ACCURATE_BLOCK_PLACEMENT_IN.getKeybind().isKeybindHeld();
            boolean accurateReverse = Hotkeys.ACCURATE_BLOCK_PLACEMENT_REVERSE.getKeybind().isKeybindHeld();

            firstWasRotation = (flexible && rotation) || (accurate && (accurateIn || accurateReverse));
            firstWasOffset = flexible && offset;
            BlockItemUseContext ctx = new BlockItemUseContext(new ItemUseContext(player, stackPre, posIn, sideIn, (float) hitVec.x, (float) hitVec.y, (float) hitVec.z));
            posFirst = getPlacementPositionForTargetedPosition(world, posIn, sideIn, ctx);
            posLast = posFirst;
            hitPartFirst = hitPart;
            handFirst = hand;
            hitVecFirst = hitVec.subtract(posFirst.getX(), posFirst.getY(), posFirst.getZ());
            sideFirst = sideIn;
            sideRotatedFirst = sideRotated;
            playerYawFirst = player.rotationYaw;
            stackFirst = stackPre;
            //System.out.printf("plop store @ %s\n", posFirst);
        }

        return result;
    }

    private static EnumActionResult tryPlaceBlock(
            PlayerControllerMP controller,
            EntityPlayerSP player,
            WorldClient world,
            BlockPos posIn,
            EnumFacing sideIn,
            EnumFacing sideRotatedIn,
            float playerYaw,
            Vec3d hitVec,
            EnumHand hand,
            HitPart hitPart,
            boolean isFirstClick)
    {
        EnumFacing side = sideIn;
        boolean handle = false;
        BlockPos posNew = null;
        boolean flexible = FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue();
        boolean rotationHeld = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ROTATION.getKeybind().isKeybindHeld();
        boolean offsetHeld = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_OFFSET.getKeybind().isKeybindHeld();
        boolean rememberFlexible = FeatureToggle.REMEMBER_FLEXIBLE.getBooleanValue();
        boolean rotation = rotationHeld || (rememberFlexible && firstWasRotation);
        boolean offset = offsetHeld || (rememberFlexible && firstWasOffset);
        ItemStack stack = player.getHeldItem(hand);

        if (flexible)
        {
            BlockItemUseContext ctx = new BlockItemUseContext(new ItemUseContext(player, stack, posIn, sideIn, (float) hitVec.x, (float) hitVec.y, (float) hitVec.z));
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
            EnumFacing facing = side;

            if (posNew == null)
            {
                if (flexible == false || isFirstClick == false)
                {
                    posNew = posIn;
                }
                else
                {
                    BlockItemUseContext ctx = new BlockItemUseContext(new ItemUseContext(player, stack, posIn, side, (float) hitVec.x, (float) hitVec.y, (float) hitVec.z));
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
                if (stack.getItem() instanceof ItemBlock)
                {
                    BlockItemUseContext ctx = new BlockItemUseContext(new ItemUseContext(player, stack, posNew, sideIn, (float) hitVec.x, (float) hitVec.y, (float) hitVec.z));
                    ItemBlock item = (ItemBlock) stack.getItem();
                    IBlockState state = item.getBlock().getStateForPlacement(ctx);

                    // getStateForPlacement can return null in 1.13+
                    if (state == null)
                    {
                        return EnumActionResult.PASS;
                    }

                    EnumFacing facingTmp = BlockUtils.getFirstPropertyFacingValue(state);

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
                double x = isFacingValidFor(facing, stack) ? facing.getIndex() + 2 + posNew.getX() : hitVec.x;
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
            BlockItemUseContext ctx = new BlockItemUseContext(new ItemUseContext(player, stack, posNew, side, (float) hitVec.x, (float) hitVec.y, (float) hitVec.z));

            if (canPlaceBlockIntoPosition(world, posNew, ctx))
            {
                //System.out.printf("tryPlaceBlock() pos: %s, side: %s, part: %s, hitVec: %s\n", posNew, side, hitPart, hitVec);
                return handleFlexibleBlockPlacement(controller, player, world, posNew, side, playerYaw, hitVec, hand, hitPart);
            }
            else
            {
                return EnumActionResult.PASS;
            }
        }

        if (isFirstClick == false && FeatureToggle.FAST_PLACEMENT_REMEMBER_ALWAYS.getBooleanValue())
        {
            return handleFlexibleBlockPlacement(controller, player, world, posIn, sideIn, playerYaw, hitVec, hand, null);
        }

        return processRightClickBlockWrapper(controller, player, world, posIn, sideIn, hitVec, hand);
    }

    private static boolean canPlaceBlockAgainst(World world, BlockPos pos, EntityPlayer player, EnumHand hand)
    {
        if (FeatureToggle.TWEAK_PLACEMENT_REST_FIRST.getBooleanValue())
        {
            IBlockState state = world.getBlockState(pos);

            if (stackClickedOn.isEmpty() == false)
            {
                ItemStack stack = state.getBlock().getItem(world, pos, state);

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
            IBlockState state = world.getBlockState(pos);
            ItemStack stackClicked = state.getBlock().getItem(world, pos, state);
            ItemStack stackHand = player.getHeldItem(hand);

            if (fi.dy.masa.malilib.util.InventoryUtils.areStacksEqual(stackClicked, stackHand) == false)
            {
                return false;
            }
        }

        return true;
    }

    private static boolean canUseFastRightClick(EntityPlayer player)
    {
        ItemStack stack = player.getHeldItemMainhand();

        if (stack.isEmpty() == false && FAST_RIGHT_CLICK_RESTRICTION.isItemAllowed(stack) == false)
        {
            return false;
        }

        stack = player.getHeldItemOffhand();

        if (stack.isEmpty() == false && FAST_RIGHT_CLICK_RESTRICTION.isItemAllowed(stack) == false)
        {
            return false;
        }

        return true;
    }

    private static void tryRestockHand(EntityPlayer player, EnumHand hand, ItemStack stackOriginal)
    {
        ItemStack stackCurrent = player.getHeldItem(hand);

        if (stackOriginal.isEmpty() == false &&
            (stackCurrent.isEmpty() || fi.dy.masa.malilib.util.InventoryUtils.areStacksEqualIgnoreDurability(stackOriginal, stackCurrent) == false))
        {
            InventoryUtils.restockNewStackToHand(player, hand, stackOriginal);
        }
    }

    private static EnumActionResult processRightClickBlockWrapper(
            PlayerControllerMP controller,
            EntityPlayerSP player,
            WorldClient world,
            BlockPos pos,
            EnumFacing side,
            Vec3d hitVec,
            EnumHand hand)
    {
        //System.out.printf("processRightClickBlockWrapper() start @ %s, side: %s\n", pos, side);
        if (FeatureToggle.TWEAK_PLACEMENT_LIMIT.getBooleanValue() &&
            placementCount >= Configs.Generic.PLACEMENT_LIMIT.getIntegerValue())
        {
            return EnumActionResult.PASS;
        }

        // We need to grab the stack here if the cached stack is still empty,
        // because this code runs before the cached stack gets set on the first click/use.
        ItemStack stackOriginal = stackFirst.isEmpty() == false && FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getBooleanValue() == false ? stackFirst : player.getHeldItem(hand).copy();
        BlockItemUseContext ctx = new BlockItemUseContext(new ItemUseContext(player, stackOriginal, pos, side, (float) hitVec.x, (float) hitVec.y, (float) hitVec.z));
        BlockPos posPlacement = getPlacementPositionForTargetedPosition(world, pos, side, ctx);
        IBlockState stateBefore = world.getBlockState(posPlacement);
        IBlockState state = world.getBlockState(pos);

        if (FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.getBooleanValue() &&
            state.isReplaceable(ctx) == false && state.getMaterial().isReplaceable())
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
            return EnumActionResult.PASS;
        }

        final int afterClickerClickCount = MathHelper.clamp(Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getIntegerValue(), 0, 32);

        //EnumFacing facing = side;
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
        EnumActionResult result;

        if (InventoryUtils.canUnstackingItemNotFitInInventory(stackOriginal, player))
        {
            result = EnumActionResult.PASS;
        }
        else
        {
            //System.out.printf("processRightClickBlockWrapper() PLACE @ %s, side: %s, hit: %s\n", pos, side, hitVec);
            result = controller.processRightClickBlock(player, world, pos, side, hitVec, hand);
        }

        if (result == EnumActionResult.SUCCESS)
        {
            placementCount++;
        }

        if (FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue())
        {
            // This restock needs to happen even with the pick-before-place tweak active,
            // otherwise the fast placement mode's checks (getHandWithItem()) will fail...
            tryRestockHand(player, hand, stackOriginal);
        }

        if (FeatureToggle.TWEAK_AFTER_CLICKER.getBooleanValue() &&
            FeatureToggle.CARPET_ACCURATE_BLOCK_PLACEMENT.getBooleanValue() == false &&
            world.getBlockState(posPlacement) != stateBefore)
        {
            for (int i = 0; i < afterClickerClickCount; i++)
            {
                //System.out.printf("processRightClickBlockWrapper() after-clicker - i: %d, pos: %s, side: %s, hitVec: %s\n", i, pos, side, hitVec);
                controller.processRightClickBlock(player, world, posPlacement, side, hitVec, hand);
            }
        }

        if (result == EnumActionResult.SUCCESS)
        {
            if (FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getBooleanValue())
            {
                int newSlot = player.inventory.currentItem + 1;

                if (newSlot >= 9 || newSlot >= Configs.Generic.HOTBAR_SLOT_CYCLE_MAX.getIntegerValue())
                {
                    newSlot = 0;
                }

                player.inventory.currentItem = newSlot;
            }
        }

        return result;
    }

    private static EnumActionResult handleFlexibleBlockPlacement(
            PlayerControllerMP controller,
            EntityPlayerSP player,
            WorldClient world,
            BlockPos pos,
            EnumFacing side,
            float playerYaw,
            Vec3d hitVec,
            EnumHand hand,
            @Nullable HitPart hitPart)
    {
        EnumFacing facing = EnumFacing.byHorizontalIndex(MathHelper.floor((playerYaw * 4.0F / 360.0F) + 0.5D) & 3);
        float yawOrig = player.rotationYaw;

        if (hitPart == HitPart.CENTER)
        {
            facing = facing.getOpposite();
        }
        else if (hitPart == HitPart.LEFT)
        {
            facing = facing.rotateYCCW();
        }
        else if (hitPart == HitPart.RIGHT)
        {
            facing = facing.rotateY();
        }

        player.rotationYaw = facing.getHorizontalAngle();
        player.connection.sendPacket(new CPacketPlayer.Rotation(player.rotationYaw, player.rotationPitch, player.onGround));

        //System.out.printf("handleFlexibleBlockPlacement() pos: %s, side: %s, facing orig: %s facing new: %s\n", pos, side, facingOrig, facing);
        EnumActionResult result = processRightClickBlockWrapper(controller, player, world, pos, side, hitVec, hand);

        player.rotationYaw = yawOrig;
        player.connection.sendPacket(new CPacketPlayer.Rotation(player.rotationYaw, player.rotationPitch, player.onGround));

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

    private static EnumFacing getRotatedFacing(EnumFacing originalSide, EnumFacing playerFacingH, HitPart hitPart)
    {
        if (originalSide.getAxis().isVertical())
        {
            switch (hitPart)
            {
                case LEFT:      return playerFacingH.rotateY();
                case RIGHT:     return playerFacingH.rotateYCCW();
                case BOTTOM:    return originalSide == EnumFacing.UP   ? playerFacingH : playerFacingH.getOpposite();
                case TOP:       return originalSide == EnumFacing.DOWN ? playerFacingH : playerFacingH.getOpposite();
                case CENTER:    return originalSide.getOpposite();
                default:        return originalSide;
            }
        }
        else
        {
            switch (hitPart)
            {
                case LEFT:      return originalSide.rotateYCCW();
                case RIGHT:     return originalSide.rotateY();
                case BOTTOM:    return EnumFacing.UP;
                case TOP:       return EnumFacing.DOWN;
                case CENTER:    return originalSide.getOpposite();
                default:        return originalSide;
            }
        }
    }

    public static HitPart getHitPart(EnumFacing originalSide, EnumFacing playerFacingH, BlockPos pos, Vec3d hitVec)
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

                if (originalSide == EnumFacing.DOWN)
                {
                    posV = 1.0d - posV;
                }

                break;
            case NORTH:
            case SOUTH:
                posH = originalSide.getAxisDirection() == AxisDirection.POSITIVE ? x : 1.0d - x;
                posV = y;
                break;
            case WEST:
            case EAST:
                posH = originalSide.getAxisDirection() == AxisDirection.NEGATIVE ? z : 1.0d - z;
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

    private static boolean isPositionAllowedByPlacementRestriction(BlockPos pos, EnumFacing side)
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

    private static boolean isFacingValidFor(EnumFacing facing, ItemStack stack)
    {
        Item item = stack.getItem();

        if (stack.isEmpty() == false && item instanceof ItemBlock)
        {
            Block block = ((ItemBlock) item).getBlock();
            IBlockState state = block.getDefaultState();

            for (IProperty<?> prop : state.getProperties())
            {
                if (prop instanceof DirectionProperty)
                {
                    return ((DirectionProperty) prop).getAllowedValues().contains(facing);
                }
            }
        }

        return false;
    }

    private static BlockPos getPlacementPositionForTargetedPosition(World world, BlockPos pos, EnumFacing side, BlockItemUseContext useContext)
    {
        if (canPlaceBlockIntoPosition(world, pos, useContext))
        {
            return pos;
        }

        return pos.offset(side);
    }

    private static boolean canPlaceBlockIntoPosition(World world, BlockPos pos, BlockItemUseContext useContext)
    {
        IBlockState state = world.getBlockState(pos);
        return state.isReplaceable(useContext) || state.getMaterial().isLiquid() || state.getMaterial().isReplaceable();
    }

    private static boolean isNewPositionValidForPlaneMode(BlockPos posNew)
    {
        EnumFacing.Axis axis = sideFirst.getAxis();

        switch (axis)
        {
            case X: return posNew.getX() == posFirst.getX();
            case Y: return posNew.getY() == posFirst.getY();
            case Z: return posNew.getZ() == posFirst.getZ();

            default:
                return false;
        }
    }

    private static boolean isNewPositionValidForFaceMode(BlockPos posNew, EnumFacing side)
    {
        return side == sideFirst;
    }

    private static boolean isNewPositionValidForColumnMode(BlockPos posNew)
    {
        EnumFacing.Axis axis = sideFirst.getAxis();

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
        EnumFacing.Axis axis = sideFirst.getAxis();

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
        EnumFacing.Axis axis = sideFirst.getAxis();
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
    private static EnumFacing getPlayerMovementDirection(EntityPlayerSP player)
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
                return dx > 0 ? EnumFacing.EAST : EnumFacing.WEST;
            }
            else
            {
                return dy > 0 ? EnumFacing.UP : EnumFacing.DOWN;
            }
        }
        else
        {
            if (az > ay)
            {
                return dz > 0 ? EnumFacing.SOUTH : EnumFacing.NORTH;
            }
            else
            {
                return dy > 0 ? EnumFacing.UP : EnumFacing.DOWN;
            }
        }
    }

    @Nullable
    private static EnumHand getHandWithItem(ItemStack stack, EntityPlayerSP player)
    {
        if (InventoryUtils.areStacksEqualIgnoreDurability(player.getHeldItemMainhand(), stackFirst))
        {
            return EnumHand.MAIN_HAND;
        }

        if (InventoryUtils.areStacksEqualIgnoreDurability(player.getHeldItemOffhand(), stackFirst))
        {
            return EnumHand.OFF_HAND;
        }

        return null;
    }
    */

    public static boolean shouldSkipSlotSync(int slotNumber, ItemStack newStack)
    {
        Minecraft mc = Minecraft.getInstance();
        EntityPlayer player = mc.player;
        Container container = player != null ? player.openContainer : null;

        if (Configs.Generic.SLOT_SYNC_WORKAROUND.getBooleanValue() &&
            FeatureToggle.TWEAK_PICK_BEFORE_PLACE.getBooleanValue() == false &&
            container != null && container == player.inventoryContainer &&
            (slotNumber == 45 || (slotNumber - 36) == player.inventory.currentItem))
        {
            boolean featRight = FeatureToggle.TWEAK_FAST_RIGHT_CLICK.getBooleanValue();
            boolean featLeft = FeatureToggle.TWEAK_FAST_LEFT_CLICK.getBooleanValue();

            if ((featRight && mc.gameSettings.keyBindUseItem.isKeyDown()) ||
                (featLeft && mc.gameSettings.keyBindAttack.isKeyDown()))
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
