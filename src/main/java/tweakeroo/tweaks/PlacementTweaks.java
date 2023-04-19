package tweakeroo.tweaks;

import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import malilib.config.value.BlackWhiteList;
import malilib.gui.util.GuiUtils;
import malilib.input.Keys;
import malilib.util.game.BlockUtils;
import malilib.util.game.PlacementUtils;
import malilib.util.game.wrap.EntityWrap;
import malilib.util.game.wrap.GameUtils;
import malilib.util.game.wrap.ItemWrap;
import malilib.util.position.PositionUtils;
import malilib.util.position.PositionUtils.HitPart;
import malilib.util.restriction.UsageRestriction;
import tweakeroo.config.Configs;
import tweakeroo.config.FeatureToggle;
import tweakeroo.config.Hotkeys;
import tweakeroo.util.CameraUtils;
import tweakeroo.util.IMinecraftAccessor;
import tweakeroo.util.InventoryUtils;
import tweakeroo.util.PlacementRestrictionMode;

public class PlacementTweaks
{
    private static BlockPos posFirst = null;
    private static BlockPos posFirstBreaking = null;
    private static BlockPos posLast = null;
    private static HitPart hitPartFirst = null;
    private static EnumHand handFirst = EnumHand.MAIN_HAND;
    private static Vec3d hitVecFirst = null;
    private static EnumFacing sideFirst = null;
    private static EnumFacing sideFirstBreaking = null;
    private static EnumFacing sideRotatedFirst = null;
    private static float playerYawFirst;
    private static ItemStack[] stackBeforeUse = new ItemStack[] { ItemStack.EMPTY, ItemStack.EMPTY };
    private static boolean isFirstClick;
    private static boolean isEmulatedClick;
    private static boolean firstWasRotation;
    private static boolean firstWasOffset;
    private static int placementCount;
    private static ItemStack stackClickedOn = ItemStack.EMPTY;
    @Nullable private static IBlockState stateClickedOn = null;
    public static final UsageRestriction<Block> FAST_RIGHT_CLICK_BLOCK_RESTRICTION = new UsageRestriction<>();
    public static final UsageRestriction<Item> FAST_RIGHT_CLICK_ITEM_RESTRICTION = new UsageRestriction<>();
    public static final UsageRestriction<Item> FAST_PLACEMENT_ITEM_RESTRICTION = new UsageRestriction<>();

    public static void onTick(Minecraft mc)
    {
        boolean attackHeld = isVanillaKeybindHeld(mc.gameSettings.keyBindAttack);
        boolean useHeld = isVanillaKeybindHeld(mc.gameSettings.keyBindUseItem);

        if (GuiUtils.getCurrentScreen() == null)
        {
            if (useHeld)
            {
                onUsingTick();
            }

            if (attackHeld)
            {
                onAttackTick(mc);
            }
        }
        else
        {
            stackBeforeUse[0] = ItemStack.EMPTY;
            stackBeforeUse[1] = ItemStack.EMPTY;
        }

        if (useHeld == false)
        {
            clearClickedBlockInfoUse();

            // Clear the cached stack when releasing both keys, so that the restock doesn't happen when
            // using another another item or an empty hand.
            if (attackHeld == false)
            {
                stackBeforeUse[0] = ItemStack.EMPTY;
                stackBeforeUse[1] = ItemStack.EMPTY;
            }
        }

        if (attackHeld == false)
        {
            clearClickedBlockInfoAttack();
        }

        if (Configs.Generic.HAND_RESTOCK_CONTINUOUS.getBooleanValue() && GuiUtils.getCurrentScreen() == null)
        {
            InventoryUtils.preRestockHand(mc.player, EnumHand.MAIN_HAND, true);
            InventoryUtils.preRestockHand(mc.player, EnumHand.OFF_HAND, true);
        }
    }

    public static boolean onProcessRightClickPre(EntityPlayer player, EnumHand hand)
    {
        InventoryUtils.trySwapCurrentToolIfNearlyBroken(hand);

        ItemStack stackOriginal = player.getHeldItem(hand);

        if (FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue() &&
            ItemWrap.notEmpty(stackOriginal))
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

    public static void onProcessRightClickPost(EntityPlayer player, EnumHand hand)
    {
        //System.out.printf("onProcessRightClickPost -> tryRestockHand with: %s, current: %s\n", stackBeforeUse[hand.ordinal()], player.getHeldItem(hand));
        tryRestockHand(player, hand, stackBeforeUse[hand.ordinal()]);
    }

    public static void onLeftClickMousePre()
    {
        Minecraft mc = GameUtils.getClient();
        RayTraceResult trace = GameUtils.getHitResult();

        // Only set the position if it was null, otherwise the fast left click tweak
        // would just reset it every time.
        if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK && posFirstBreaking == null)
        {
            posFirstBreaking = trace.getBlockPos();
            sideFirstBreaking = trace.sideHit;
        }

        onProcessRightClickPre(mc.player, EnumHand.MAIN_HAND);
    }

    public static void onLeftClickMousePost()
    {
        onProcessRightClickPost(GameUtils.getClientPlayer(), EnumHand.MAIN_HAND);
    }

    public static void cacheStackInHand(EnumHand hand)
    {
        ItemStack stackOriginal = GameUtils.getClientPlayer().getHeldItem(hand);

        if (FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue() &&
            ItemWrap.notEmpty(stackOriginal))
        {
            stackBeforeUse[hand.ordinal()] = stackOriginal.copy();
        }
    }

    private static void onAttackTick(Minecraft mc)
    {
        if (FeatureToggle.TWEAK_FAST_LEFT_CLICK.getBooleanValue())
        {
            final int count = Configs.Generic.FAST_LEFT_CLICK_COUNT.getIntegerValue();

            for (int i = 0; i < count; ++i)
            {
                InventoryUtils.trySwapCurrentToolIfNearlyBroken(EnumHand.MAIN_HAND);
                isEmulatedClick = true;
                ((IMinecraftAccessor) mc).leftClickMouseAccessor();
                isEmulatedClick = false;
            }
        }
        else
        {
            EnumHand hand = EnumHand.MAIN_HAND;
            InventoryUtils.trySwapCurrentToolIfNearlyBroken(hand);
            tryRestockHand(mc.player, hand, stackBeforeUse[hand.ordinal()]);
        }
    }

    @Nullable
    public static BlockPos getOverriddenPlacementPosition()
    {
        Minecraft mc = GameUtils.getClient();

        if (mc.player != null)
        {
            RayTraceResult trace = GameUtils.getHitResult();

            if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                World world = mc.world;
                EntityPlayer player = mc.player;
                EnumFacing side = trace.sideHit;
                BlockPos posTargeted = trace.getBlockPos();

                if (FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue())
                {
                    boolean rememberFlexible = Configs.Generic.REMEMBER_FLEXIBLE.getBooleanValue();
                    boolean offsetHeld = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_OFFSET.getKeyBind().isKeyBindHeld();
                    boolean adjacentHeld = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ADJACENT.getKeyBind().isKeyBindHeld();
                    boolean offset = offsetHeld || (rememberFlexible && firstWasOffset);

                    if (offset || adjacentHeld)
                    {
                        EnumFacing playerFacingH = player.getHorizontalFacing();
                        HitPart hitPart = PositionUtils.getHitPart(side, playerFacingH, posTargeted, trace.hitVec);
                        EnumFacing sideRotated = getRotatedFacing(side, playerFacingH, hitPart);
                        BlockPos posNew = isFirstClick && (offset || adjacentHeld) ? getPlacementPositionForTargetedPosition(posTargeted, side, world) : posTargeted;
                        boolean handleFlexible = false;

                        // Place the block into the adjacent position
                        if (adjacentHeld && hitPart != null && hitPart != HitPart.CENTER)
                        {
                            posNew = posNew.offset(sideRotated.getOpposite()).offset(side.getOpposite());
                            handleFlexible = true;
                        }

                        // Place the block into the diagonal position
                        if (offset)
                        {
                            posNew = posNew.offset(sideRotated.getOpposite());
                            handleFlexible = true;
                        }

                        if (handleFlexible)
                        {
                            if (PlacementUtils.isReplaceable(world, posNew, true) == false)
                            {
                                posNew = posNew.offset(side); // the side here is probably wrong with Flexible Placement Rotation...
                            }

                            return posNew;
                        }
                    }
                }

                // Fast Block Placement
                if (posFirst != null && handFirst != null &&
                    FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getBooleanValue() &&
                    canUseItemWithRestriction(FAST_PLACEMENT_ITEM_RESTRICTION, player))
                {
                    BlockPos pos = getPlacementPositionForTargetedPosition(posTargeted, side, world);

                    if (pos.equals(posLast) == false &&
                        canPlaceBlockIntoPosition(pos, world) &&
                        isPositionAllowedByPlacementRestriction(pos, side) &&
                        canPlaceBlockAgainst(world, posTargeted, mc.player, handFirst)
                    )
                    {
                        return pos;
                    }
                }
            }
        }

        return null;
    }

    private static void onUsingTick()
    {
        EntityPlayerSP player = GameUtils.getClientPlayer();

        if (player == null)
        {
            return;
        }

        Minecraft mc = GameUtils.getClient();

        if (posFirst != null && FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getBooleanValue() &&
            canUseItemWithRestriction(FAST_PLACEMENT_ITEM_RESTRICTION, player))
        {
            World world = GameUtils.getClientWorld();
            final double reach = mc.playerController.getBlockReachDistance();
            final int maxCount = Configs.Generic.FAST_BLOCK_PLACEMENT_COUNT.getIntegerValue();

            mc.objectMouseOver = player.rayTrace(reach, mc.getRenderPartialTicks());

            for (int i = 0; i < maxCount; ++i)
            {
                RayTraceResult trace = mc.objectMouseOver;

                if (trace == null || trace.typeOfHit != RayTraceResult.Type.BLOCK)
                {
                    break;
                }

                EnumHand hand = handFirst;
                EnumFacing side = trace.sideHit;
                BlockPos pos = trace.getBlockPos();
                BlockPos posNew = getPlacementPositionForTargetedPosition(pos, side, world);

                if (hand != null &&
                    posNew.equals(posLast) == false &&
                    canPlaceBlockIntoPosition(posNew, world) &&
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

                    Vec3d hitVec = hitVecFirst.add(posNew.getX(), posNew.getY(), posNew.getZ());
                    EnumActionResult result = tryPlaceBlock(mc.playerController, player, mc.world,
                            posNew, sideFirst, sideRotatedFirst, playerYawFirst, hitVec, hand, hitPartFirst, false);

                    if (result == EnumActionResult.SUCCESS)
                    {
                        posLast = posNew;
                        mc.objectMouseOver = player.rayTrace(reach, mc.getRenderPartialTicks());
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
                isVanillaKeybindHeld(mc.gameSettings.keyBindUseItem) &&
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

    private static boolean isVanillaKeybindHeld(KeyBinding key)
    {
        return Keys.isKeyDown(key.getKeyCode());
    }

    public static EnumActionResult onProcessRightClickBlock(
            PlayerControllerMP controller,
            EntityPlayerSP player,
            WorldClient world,
            BlockPos posIn,
            EnumFacing sideIn,
            Vec3d hitVecIn,
            EnumHand hand)
    {
        if (CameraUtils.shouldPreventPlayerInputs())
        {
            return EnumActionResult.PASS;
        }

        boolean restricted = FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.getBooleanValue() || FeatureToggle.TWEAK_PLACEMENT_GRID.getBooleanValue();
        ItemStack stackPre = player.getHeldItem(hand).copy();
        EnumFacing playerFacingH = player.getHorizontalFacing();
        HitPart hitPart = PositionUtils.getHitPart(sideIn, playerFacingH, posIn, hitVecIn);
        EnumFacing sideRotated = getRotatedFacing(sideIn, playerFacingH, hitPart);

        cacheStackInHand(hand);

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

        float yaw = EntityWrap.getYaw(player);
        //System.out.printf("onProcessRightClickBlock() pos: %s, side: %s, part: %s, hitVec: %s\n", posIn, sideIn, hitPart, hitVec);
        EnumActionResult result = tryPlaceBlock(controller, player, world, posIn, sideIn, sideRotated, yaw, hitVecIn, hand, hitPart, true);

        // Store the initial click data for the fast placement mode
        if (posFirst == null && result == EnumActionResult.SUCCESS && restricted)
        {
            boolean flexible = FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue();
            boolean accurate = FeatureToggle.TWEAK_ACCURATE_BLOCK_PLACEMENT.getBooleanValue();
            boolean rotation = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ROTATION.getKeyBind().isKeyBindHeld();
            boolean offset = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_OFFSET.getKeyBind().isKeyBindHeld();
            boolean accurateIn = Hotkeys.ACCURATE_BLOCK_PLACEMENT_INTO.getKeyBind().isKeyBindHeld();
            boolean accurateReverse = Hotkeys.ACCURATE_BLOCK_PLACEMENT_REVERSE.getKeyBind().isKeyBindHeld();

            firstWasRotation = (flexible && rotation) || (accurate && (accurateIn || accurateReverse));
            firstWasOffset = flexible && offset;
            posFirst = getPlacementPositionForTargetedPosition(posIn, sideIn, world);
            posLast = posFirst;
            hitPartFirst = hitPart;
            handFirst = hand;
            hitVecFirst = hitVecIn.subtract(posFirst.getX(), posFirst.getY(), posFirst.getZ());
            sideFirst = sideIn;
            sideRotatedFirst = sideRotated;
            playerYawFirst = yaw;
            stackBeforeUse[hand.ordinal()] = stackPre;
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
        boolean handleFlexible = false;
        BlockPos posNew = null;
        boolean flexible = FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue();
        boolean rotationHeld = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ROTATION.getKeyBind().isKeyBindHeld();
        boolean offsetHeld = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_OFFSET.getKeyBind().isKeyBindHeld();
        boolean adjacent = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ADJACENT.getKeyBind().isKeyBindHeld();
        boolean rememberFlexible = Configs.Generic.REMEMBER_FLEXIBLE.getBooleanValue();
        boolean rotation = rotationHeld || (rememberFlexible && firstWasRotation);
        boolean offset = offsetHeld || (rememberFlexible && firstWasOffset);

        if (flexible)
        {
            posNew = isFirstClick && (rotation || offset || adjacent) ? getPlacementPositionForTargetedPosition(posIn, sideIn, world) : posIn;

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

        boolean accurate = FeatureToggle.TWEAK_ACCURATE_BLOCK_PLACEMENT.getBooleanValue();
        boolean accurateIn = Hotkeys.ACCURATE_BLOCK_PLACEMENT_INTO.getKeyBind().isKeyBindHeld();
        boolean accurateReverse = Hotkeys.ACCURATE_BLOCK_PLACEMENT_REVERSE.getKeyBind().isKeyBindHeld();
        boolean afterClicker = FeatureToggle.TWEAK_AFTER_CLICKER.getBooleanValue();

        if (accurate && (accurateIn || accurateReverse || afterClicker))
        {
            EnumFacing facing = side;
            boolean handleAccurate = false;

            if (posNew == null)
            {
                if (flexible == false || isFirstClick == false)
                {
                    posNew = posIn;
                }
                else
                {
                    posNew = getPlacementPositionForTargetedPosition(posIn, side, world);
                }
            }

            ItemStack stack = player.getHeldItem(hand);

            if (accurateIn)
            {
                facing = sideIn;
                hitPart = null;
                handleAccurate = true;

                // Pistons, Droppers, Dispensers should face into the block, but Observers should point their back/output
                // side into the block when the Accurate Placement In hotkey is used
                if ((stack.getItem() instanceof ItemBlock) == false || ((ItemBlock) stack.getItem()).getBlock() != Blocks.OBSERVER)
                {
                    facing = facing.getOpposite();
                }
                //System.out.printf("accurate - IN - facing: %s\n", facing);
            }
            else if (flexible == false || rotation == false)
            {
                if (stack.getItem() instanceof ItemBlock)
                {
                    ItemBlock item = (ItemBlock) stack.getItem();
                    int meta = item.getMetadata(stack.getMetadata());
                    BlockPos posPlacement = getPlacementPositionForTargetedPosition(posNew, sideIn, world);
                    IBlockState state = item.getBlock().getStateForPlacement(world, posPlacement, sideIn,
                            (float) hitVec.x, (float) hitVec.y, (float) hitVec.z, meta, player);
                    Optional<EnumFacing> facingTmp = BlockUtils.getFirstPropertyFacingValue(state);
                    //System.out.printf("accurate - sideIn: %s, state: %s, hit: %s, f: %s, posNew: %s\n", sideIn, state, hitVec, EnumFacing.getDirectionFromEntityLiving(posIn, player), posNew);

                    if (facingTmp.isPresent())
                    {
                        facing = facingTmp.get();
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

            if ((handleAccurate || afterClicker) && Configs.Generic.CARPET_ACCURATE_PLACEMENT_PROTOCOL.getBooleanValue())
            {
                // Carpet mod accurate block placement protocol support, for Carpet v18_04_24 or later
                double x = handleAccurate && isFacingValidFor(facing, stack) ? facing.getIndex() + 2 + posNew.getX() : hitVec.x;
                int afterClickerClickCount = MathHelper.clamp(Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getIntegerValue(), 0, 32);

                if (afterClicker)
                {
                    x += afterClickerClickCount * 10;
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
            if (canPlaceBlockIntoPosition(posNew, world))
            {
                //System.out.printf("tryPlaceBlock() pos: %s, side: %s, part: %s, hitVec: %s\n", posNew, side, hitPart, hitVec);
                return handleFlexibleBlockPlacement(controller, player, world, posNew, side, playerYaw, hitVec, hand, hitPart);
            }
            else
            {
                return EnumActionResult.PASS;
            }
        }

        if (isFirstClick == false && Configs.Generic.FAST_PLACEMENT_REMEMBER.getBooleanValue())
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

            if (ItemWrap.notEmpty(stackClickedOn))
            {
                ItemStack stack = state.getBlock().getItem(world, pos, state);

                if (malilib.util.inventory.InventoryUtils.areStacksEqual(stackClickedOn, stack) == false)
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

            if (malilib.util.inventory.InventoryUtils.areStacksEqual(stackClicked, stackHand) == false)
            {
                return false;
            }
        }

        return true;
    }

    private static boolean canUseItemWithRestriction(UsageRestriction<Item> restriction, EntityPlayer player)
    {
        ItemStack stack = player.getHeldItemMainhand();

        if (ItemWrap.notEmpty(stack) && restriction.isAllowed(stack.getItem()) == false)
        {
            return false;
        }

        stack = player.getHeldItemOffhand();

        if (ItemWrap.notEmpty(stack) && restriction.isAllowed(stack.getItem()) == false)
        {
            return false;
        }

        return true;
    }

    private static boolean canUseFastRightClick(EntityPlayer player)
    {
        if (canUseItemWithRestriction(FAST_RIGHT_CLICK_ITEM_RESTRICTION, player) == false)
        {
            return false;
        }

        RayTraceResult trace = player.rayTrace(6, 0f);

        if (trace == null || trace.typeOfHit != RayTraceResult.Type.BLOCK)
        {
            return FAST_RIGHT_CLICK_BLOCK_RESTRICTION.isAllowed(Blocks.AIR);
        }

        Block block = GameUtils.getClientWorld().getBlockState(trace.getBlockPos()).getBlock();

        return FAST_RIGHT_CLICK_BLOCK_RESTRICTION.isAllowed(block);
    }

    private static void tryRestockHand(EntityPlayer player, EnumHand hand, ItemStack stackOriginal)
    {
        if (FeatureToggle.TWEAK_HAND_RESTOCK.getBooleanValue())
        {
            ItemStack stackCurrent = player.getHeldItem(hand);

            if (ItemWrap.notEmpty(stackOriginal) &&
                (ItemWrap.isEmpty(stackCurrent) || stackCurrent.isItemEqualIgnoreDurability(stackOriginal) == false))
            {
                // Don't allow taking stacks from elsewhere in the hotbar, if the cycle tweak is on
                boolean allowHotbar = FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getBooleanValue() == false &&
                                      FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER.getBooleanValue() == false;
                InventoryUtils.restockNewStackToHand(player, hand, stackOriginal, allowHotbar);
            }
        }
    }

    private static EnumActionResult processRightClickBlockWrapper(
            PlayerControllerMP controller,
            EntityPlayerSP player,
            WorldClient world,
            BlockPos posIn,
            EnumFacing sideIn,
            Vec3d hitVecIn,
            EnumHand hand)
    {
        //System.out.printf("processRightClickBlockWrapper() start @ %s, side: %s, hand: %s\n", pos, side, hand);
        if (FeatureToggle.TWEAK_PLACEMENT_LIMIT.getBooleanValue() &&
            placementCount >= Configs.Generic.PLACEMENT_LIMIT.getIntegerValue())
        {
            return EnumActionResult.PASS;
        }

        // Don't allow taking stacks from elsewhere in the hotbar, if the cycle tweak is on
        boolean allowHotbar = FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getBooleanValue() == false &&
                              FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER.getBooleanValue() == false;

        InventoryUtils.preRestockHand(player, hand, allowHotbar);

        // We need to grab the stack here if the cached stack is still empty,
        // because this code runs before the cached stack gets set on the first click/use.
        ItemStack stackOriginal;

        if (ItemWrap.notEmpty(stackBeforeUse[hand.ordinal()]) &&
            FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getBooleanValue() == false &&
            FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER.getBooleanValue() == false)
        {
            stackOriginal = stackBeforeUse[hand.ordinal()];
        }
        else
        {
            stackOriginal = player.getHeldItem(hand).copy();
        }

        BlockPos posPlacement = getPlacementPositionForTargetedPosition(posIn, sideIn, world);
        IBlockState stateBefore = world.getBlockState(posPlacement);
        IBlockState state = world.getBlockState(posIn);

        if (FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.getBooleanValue() &&
            state.getBlock().isReplaceable(world, posIn) == false && state.getMaterial().isReplaceable())
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
            return EnumActionResult.PASS;
        }

        final int afterClickerClickCount = MathHelper.clamp(Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getIntegerValue(), 0, 32);

        EnumFacing facing = sideIn;
        boolean flexible = FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue();
        boolean rotationHeld = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ROTATION.getKeyBind().isKeyBindHeld();
        boolean rememberFlexible = Configs.Generic.REMEMBER_FLEXIBLE.getBooleanValue();
        boolean rotation = rotationHeld || (rememberFlexible && firstWasRotation);
        boolean accurate = FeatureToggle.TWEAK_ACCURATE_BLOCK_PLACEMENT.getBooleanValue();
        boolean keys = Hotkeys.ACCURATE_BLOCK_PLACEMENT_INTO.getKeyBind().isKeyBindHeld() || Hotkeys.ACCURATE_BLOCK_PLACEMENT_REVERSE.getKeyBind().isKeyBindHeld();
        accurate = accurate && keys;

        // Carpet mod accurate block placement protocol support, for Carpet v18_04_24 or later
        if (flexible && rotation && accurate == false &&
            Configs.Generic.CARPET_ACCURATE_PLACEMENT_PROTOCOL.getBooleanValue() &&
            isFacingValidFor(facing, stackOriginal))
        {
            facing = facing.getOpposite(); // go from block face to click on to the requested facing
            double x = facing.getIndex() + 2 + posIn.getX();

            if (FeatureToggle.TWEAK_AFTER_CLICKER.getBooleanValue())
            {
                x += afterClickerClickCount * 10;
            }

            //System.out.printf("processRightClickBlockWrapper req facing: %s, x: %.3f, pos: %s, sideIn: %s\n", facing, x, posIn, sideIn);
            hitVecIn = new Vec3d(x, hitVecIn.y, hitVecIn.z);
        }

        if (FeatureToggle.TWEAK_BLOCK_PLACEMENT_Y_MIRROR.getBooleanValue() && Hotkeys.PLACEMENT_Y_MIRROR.getKeyBind().isKeyBindHeld())
        {
            double y = 1 - hitVecIn.y + 2 * posIn.getY(); // = 1 - (hitVec.y - pos.getY()) + pos.getY();
            hitVecIn = new Vec3d(hitVecIn.x, y, hitVecIn.z);

            if (sideIn.getAxis() == EnumFacing.Axis.Y)
            {
                posIn = posIn.offset(sideIn);
                sideIn = sideIn.getOpposite();
            }
        }

        if (FeatureToggle.TWEAK_PICK_BEFORE_PLACE.getBooleanValue())
        {
            InventoryUtils.switchToPickedBlock();
        }

        InventoryUtils.trySwapCurrentToolIfNearlyBroken(hand);

        //System.out.printf("processRightClickBlockWrapper() pos: %s, side: %s, hitVec: %s\n", pos, side, hitVec);
        EnumActionResult result;

        if (InventoryUtils.canUnstackingItemNotFitInInventory(stackOriginal, player))
        {
            result = EnumActionResult.PASS;
        }
        else
        {
            //System.out.printf("processRightClickBlockWrapper() PLACE @ %s, side: %s, hit: %s\n", pos, side, hitVec);
            result = controller.processRightClickBlock(player, world, posIn, sideIn, hitVecIn, hand);
        }

        if (result == EnumActionResult.SUCCESS)
        {
            placementCount++;
        }

        // This restock needs to happen even with the pick-before-place tweak active,
        // otherwise the fast placement mode's checks (getHandWithItem()) will fail...
        //System.out.printf("processRightClickBlockWrapper -> tryRestockHand with: %s, current: %s\n", stackOriginal, player.getHeldItem(hand));
        tryRestockHand(player, hand, stackOriginal);

        if (FeatureToggle.TWEAK_AFTER_CLICKER.getBooleanValue() &&
            Configs.Generic.CARPET_ACCURATE_PLACEMENT_PROTOCOL.getBooleanValue() == false &&
            world.getBlockState(posPlacement) != stateBefore)
        {
            for (int i = 0; i < afterClickerClickCount; i++)
            {
                //System.out.printf("processRightClickBlockWrapper() after-clicker - i: %d, pos: %s, side: %s, hitVec: %s\n", i, pos, side, hitVec);
                controller.processRightClickBlock(player, world, posPlacement, sideIn, hitVecIn, hand);
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
            else if (FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER.getBooleanValue())
            {
                int newSlot = player.getRNG().nextInt(Configs.Generic.HOTBAR_SLOT_RANDOMIZER_MAX.getIntegerValue());
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
        float yawOrig = EntityWrap.getYaw(player);
        float pitchOrig = EntityWrap.getPitch(player);

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

        EntityWrap.setYaw(player, facing.getHorizontalAngle());
        player.connection.sendPacket(new CPacketPlayer.Rotation(EntityWrap.getYaw(player), pitchOrig, player.onGround));

        //System.out.printf("handleFlexibleBlockPlacement() pos: %s, side: %s, orig: %s new: %s, hv: %s\n", pos, side, EnumFacing.byHorizontalIndex(MathHelper.floor((playerYaw * 4.0F / 360.0F) + 0.5D) & 3), facing, hitVec);
        EnumActionResult result = processRightClickBlockWrapper(controller, player, world, pos, side, hitVec, hand);

        EntityWrap.setYaw(player, yawOrig);
        player.connection.sendPacket(new CPacketPlayer.Rotation(yawOrig, pitchOrig, player.onGround));

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

    private static boolean isPositionAllowedByPlacementRestriction(BlockPos pos, EnumFacing side)
    {
        boolean restrictionEnabled = FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.getBooleanValue();
        boolean gridEnabled = FeatureToggle.TWEAK_PLACEMENT_GRID.getBooleanValue();

        if (restrictionEnabled == false && gridEnabled == false)
        {
            return true;
        }

        int gridSize = Configs.Generic.PLACEMENT_GRID_SIZE.getIntegerValue();
        PlacementRestrictionMode mode = Configs.Generic.PLACEMENT_RESTRICTION_MODE.getValue();

        return isPositionAllowedByRestrictions(pos, side, posFirst, sideFirst, restrictionEnabled, mode, gridEnabled, gridSize);
    }

    public static boolean isPositionAllowedByBreakingRestriction(BlockPos pos, EnumFacing side)
    {
        boolean restrictionEnabled = FeatureToggle.TWEAK_BREAKING_RESTRICTION.getBooleanValue();
        boolean gridEnabled = FeatureToggle.TWEAK_BREAKING_GRID.getBooleanValue();

        if (restrictionEnabled == false && gridEnabled == false)
        {
            return true;
        }

        int gridSize = Configs.Generic.BREAKING_GRID_SIZE.getIntegerValue();
        PlacementRestrictionMode mode = Configs.Generic.BREAKING_RESTRICTION_MODE.getValue();

        return posFirstBreaking == null || isPositionAllowedByRestrictions(pos, side, posFirstBreaking, sideFirstBreaking, restrictionEnabled, mode, gridEnabled, gridSize);
    }

    private static boolean isPositionAllowedByRestrictions(BlockPos pos, EnumFacing side,
            BlockPos posFirst, EnumFacing sideFirst, boolean restrictionEnabled, PlacementRestrictionMode mode, boolean gridEnabled, int gridSize)
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

        return restrictionEnabled == false || mode.isPositionValid(pos, side, posFirst, sideFirst);
    }

    private static boolean isFacingValidFor(EnumFacing facing, ItemStack stack)
    {
        Item item = stack.getItem();

        if (ItemWrap.notEmpty(stack) && item instanceof ItemBlock)
        {
            Block block = ((ItemBlock) item).getBlock();
            IBlockState state = block.getDefaultState();

            for (IProperty<?> prop : state.getProperties().keySet())
            {
                if (prop instanceof PropertyDirection)
                {
                    return ((PropertyDirection) prop).getAllowedValues().contains(facing);
                }
            }
        }

        return false;
    }

    private static BlockPos getPlacementPositionForTargetedPosition(BlockPos pos, EnumFacing side, World world)
    {
        if (canPlaceBlockIntoPosition(pos, world))
        {
            return pos;
        }

        return pos.offset(side);
    }

    private static boolean canPlaceBlockIntoPosition(BlockPos pos, World world)
    {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isReplaceable(world, pos) || state.getMaterial().isLiquid() || state.getMaterial().isReplaceable();
    }

    public static boolean isNewPositionValidForColumnMode(BlockPos posNew, EnumFacing side, BlockPos posFirst, EnumFacing sideFirst)
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

    public static boolean isNewPositionValidForDiagonalMode(BlockPos posNew, EnumFacing side, BlockPos posFirst, EnumFacing sideFirst)
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

    public static boolean isNewPositionValidForFaceMode(BlockPos posNew, EnumFacing side, BlockPos posFirst, EnumFacing sideFirst)
    {
        return side == sideFirst;
    }

    public static boolean isNewPositionValidForLayerMode(BlockPos posNew, EnumFacing side, BlockPos posFirst, EnumFacing sideFirst)
    {
        return posNew.getY() == posFirst.getY();
    }

    public static boolean isNewPositionValidForLineMode(BlockPos posNew, EnumFacing side, BlockPos posFirst, EnumFacing sideFirst)
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

    public static boolean isNewPositionValidForPlaneMode(BlockPos posNew, EnumFacing side, BlockPos posFirst, EnumFacing sideFirst)
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
        Minecraft mc = GameUtils.getClient();
        EntityPlayer player = mc.player;
        Container container = player != null ? player.openContainer : null;

        if (Configs.Generic.SLOT_SYNC_WORKAROUND.getBooleanValue() &&
            FeatureToggle.TWEAK_PICK_BEFORE_PLACE.getBooleanValue() == false &&
            container != null && container == player.inventoryContainer &&
            (slotNumber == 45 || (slotNumber - 36) == player.inventory.currentItem))
        {
            if (mc.gameSettings.keyBindUseItem.isKeyDown() &&
                (Configs.Generic.SLOT_SYNC_WORKAROUND_ALWAYS.getBooleanValue() ||
                 FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getBooleanValue() ||
                 FeatureToggle.TWEAK_FAST_RIGHT_CLICK.getBooleanValue()))
            {
                return true;
            }

            if (mc.gameSettings.keyBindAttack.isKeyDown() &&
                FeatureToggle.TWEAK_FAST_LEFT_CLICK.getBooleanValue())
            {
                return true;
            }
        }

        return false;
    }

    public static void updateFastRightClickBlockRestriction(BlackWhiteList<Block> list)
    {
        FAST_RIGHT_CLICK_BLOCK_RESTRICTION.setListContents(list);
    }

    public static void updateFastRightClickItemRestriction(BlackWhiteList<Item> list)
    {
        FAST_RIGHT_CLICK_ITEM_RESTRICTION.setListContents(list);
    }

    public static void updateFastPlacementItemRestriction(BlackWhiteList<Item> list)
    {
        FAST_PLACEMENT_ITEM_RESTRICTION.setListContents(list);
    }

    public interface PlacementRestrictionCheck
    {
        boolean isPositionValid(BlockPos posNew, EnumFacing side, BlockPos posFirst, EnumFacing sideFirst);
    }
}
