package fi.dy.masa.tweakeroo.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import fi.dy.masa.malilib.action.Action;
import fi.dy.masa.malilib.action.ActionContext;
import fi.dy.masa.malilib.action.ActionUtils;
import fi.dy.masa.malilib.action.ConfigActions;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.action.ParameterizedAction;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import fi.dy.masa.malilib.overlay.message.MessageUtils;
import fi.dy.masa.malilib.util.GameUtils;
import fi.dy.masa.malilib.util.PositionUtils;
import fi.dy.masa.malilib.util.RayTraceUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.wrap.EntityWrap;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.DisableToggle;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.gui.ConfigScreen;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import fi.dy.masa.tweakeroo.util.PlacementRestrictionMode;

public class Actions
{
    public static final NamedAction BLINK_DRIVE_TELEPORT_GROUND             = register("blinkDriveTeleportGround", (ctx) -> blinkDriveTeleport(false));
    public static final NamedAction BLINK_DRIVE_TELEPORT_SAME_Y             = register("blinkDriveTeleportSameY", (ctx) -> blinkDriveTeleport(true));
    public static final NamedAction COPY_SIGN_TEXT                          = register("copySignText", Actions::copySignText);
    public static final NamedAction GHOST_BLOCK_REMOVER_MANUAL              = register("ghostBlockRemoverManual", MiscUtils::antiGhostBlock);
    public static final NamedAction HOTBAR_SWAP_ROW_1                       = register("hotbarSwapRow1", (ctx) -> hotbarSwapRow(1, ctx));
    public static final NamedAction HOTBAR_SWAP_ROW_2                       = register("hotbarSwapRow2", (ctx) -> hotbarSwapRow(2, ctx));
    public static final NamedAction HOTBAR_SWAP_ROW_3                       = register("hotbarSwapRow3", (ctx) -> hotbarSwapRow(3, ctx));
    public static final NamedAction OPEN_CONFIG_SCREEN                      = register("openConfigScreen", ConfigScreen::open);
    public static final NamedAction RELOAD_LANGUAGE_PACKS                   = register("reloadLanguagePacks", (ctx) -> reloadLanguagePacks());
    public static final NamedAction SET_BREAKING_RESTRICTION_MODE_COLUMN    = register("setBreakingRestrictionModeColumn", () -> setBreakingRestrictionMode(PlacementRestrictionMode.COLUMN));
    public static final NamedAction SET_BREAKING_RESTRICTION_MODE_DIAGONAL  = register("setBreakingRestrictionModeDiagonal", () -> setBreakingRestrictionMode(PlacementRestrictionMode.DIAGONAL));
    public static final NamedAction SET_BREAKING_RESTRICTION_MODE_FACE      = register("setBreakingRestrictionModeFace", () -> setBreakingRestrictionMode(PlacementRestrictionMode.FACE));
    public static final NamedAction SET_BREAKING_RESTRICTION_MODE_LAYER     = register("setBreakingRestrictionModeLayer", () -> setBreakingRestrictionMode(PlacementRestrictionMode.LAYER));
    public static final NamedAction SET_BREAKING_RESTRICTION_MODE_LINE      = register("setBreakingRestrictionModeLine", () -> setBreakingRestrictionMode(PlacementRestrictionMode.LINE));
    public static final NamedAction SET_BREAKING_RESTRICTION_MODE_PLANE     = register("setBreakingRestrictionModePlane", () -> setBreakingRestrictionMode(PlacementRestrictionMode.PLANE));
    public static final NamedAction SET_PLACEMENT_RESTRICTION_MODE_COLUMN   = register("setPlacementRestrictionModeColumn", () -> setPlacementRestrictionMode(PlacementRestrictionMode.COLUMN));
    public static final NamedAction SET_PLACEMENT_RESTRICTION_MODE_DIAGONAL = register("setPlacementRestrictionModeDiagonal", () -> setPlacementRestrictionMode(PlacementRestrictionMode.DIAGONAL));
    public static final NamedAction SET_PLACEMENT_RESTRICTION_MODE_FACE     = register("setPlacementRestrictionModeFace", () -> setPlacementRestrictionMode(PlacementRestrictionMode.FACE));
    public static final NamedAction SET_PLACEMENT_RESTRICTION_MODE_LAYER    = register("setPlacementRestrictionModeLayer", () -> setPlacementRestrictionMode(PlacementRestrictionMode.LAYER));
    public static final NamedAction SET_PLACEMENT_RESTRICTION_MODE_LINE     = register("setPlacementRestrictionModeLine", () -> setPlacementRestrictionMode(PlacementRestrictionMode.LINE));
    public static final NamedAction SET_PLACEMENT_RESTRICTION_MODE_PLANE    = register("setPlacementRestrictionModePlane", () -> setPlacementRestrictionMode(PlacementRestrictionMode.PLANE));
    public static final NamedAction TOGGLE_GRAB_CURSOR                      = register("toggleGrabCursor", (ctx) -> toggleGrabCursor());
    public static final NamedAction TOGGLE_SKIP_ALL_RENDERING               = register("toggleSkipAllRendering", (ctx) -> toggleSkipAllRendering());
    public static final NamedAction TOGGLE_SKIP_WORLD_RENDERING             = register("toggleSkipWorldRendering", Actions::toggleSkipWorldRendering);
    public static final NamedAction TOOL_PICK                               = register("toolPick", (ctx) -> toolPick());

    public static void init()
    {
        for (FeatureToggle feature : FeatureToggle.VALUES)
        {
            ActionUtils.registerBooleanConfigActions(Reference.MOD_INFO, feature.getBooleanConfig(), feature.getKeyBind());
        }

        for (DisableToggle feature : DisableToggle.VALUES)
        {
            ActionUtils.registerBooleanConfigActions(Reference.MOD_INFO, feature.getBooleanConfig(), feature.getKeyBind());
        }

        register("hotbarScroll", Actions::hotbarScroll);
        register("setFlySpeedPreset1", ctx -> setFlySpeedPreset(ctx, 1));
        register("setFlySpeedPreset2", ctx -> setFlySpeedPreset(ctx, 2));
        register("setFlySpeedPreset3", ctx -> setFlySpeedPreset(ctx, 3));
        register("setFlySpeedPreset4", ctx -> setFlySpeedPreset(ctx, 4));
        register("setFlySpeedOverrideValue", Actions::setFlySpeedValue);
        register("zoomActivate", (ctx) -> zoomActivate(true));
        register("zoomDeactivate", (ctx) -> zoomActivate(false));

        register("setAfterClickerCount",        ConfigActions.createSetIntValueAction(Configs.Generic.AFTER_CLICKER_CLICK_COUNT));
        register("setBreakingGridSize",         ConfigActions.createSetIntValueAction(Configs.Generic.BREAKING_GRID_SIZE));
        register("setFastLeftClickCount",       ConfigActions.createSetIntValueAction(Configs.Generic.FAST_LEFT_CLICK_COUNT));
        register("setFastRightClickCount",      ConfigActions.createSetIntValueAction(Configs.Generic.FAST_RIGHT_CLICK_COUNT));
        register("setHotbarSlotCycleMax",       ConfigActions.createSetIntValueAction(Configs.Generic.HOTBAR_SLOT_CYCLE_MAX));
        register("setHotbarSlotRandomizerMax",  ConfigActions.createSetIntValueAction(Configs.Generic.HOTBAR_SLOT_RANDOMIZER_MAX));
        register("setPeriodicAttackInterval",   ConfigActions.createSetIntValueAction(Configs.Generic.PERIODIC_ATTACK_INTERVAL));
        register("setPeriodicUseInterval",      ConfigActions.createSetIntValueAction(Configs.Generic.PERIODIC_USE_INTERVAL));
        register("setPlacementGridSize",        ConfigActions.createSetIntValueAction(Configs.Generic.PLACEMENT_GRID_SIZE));
        register("setPlacementLimit",           ConfigActions.createSetIntValueAction(Configs.Generic.PLACEMENT_LIMIT));
        register("setRenderLimitItem",          ConfigActions.createSetIntValueAction(Configs.Generic.RENDER_LIMIT_ITEM));
        register("setRenderLimitXP",            ConfigActions.createSetIntValueAction(Configs.Generic.RENDER_LIMIT_XP_ORB));

        register("setSnapAimPitchStep",         ConfigActions.createSetDoubleValueAction(Configs.Generic.SNAP_AIM_PITCH_STEP));
        register("setSnapAimYawStep",           ConfigActions.createSetDoubleValueAction(Configs.Generic.SNAP_AIM_YAW_STEP));
        register("setZoomFoV",                  ConfigActions.createSetDoubleValueAction(Configs.Generic.ZOOM_FOV));

        ActionUtils.registerBooleanConfigActions(Configs.Generic.OPTIONS);
        ActionUtils.registerBooleanConfigActions(Configs.Fixes.OPTIONS);
    }

    private static NamedAction register(String name, EventListener action)
    {
        return ActionUtils.register(Reference.MOD_INFO, name, action);
    }

    private static NamedAction register(String name, Action action)
    {
        return ActionUtils.register(Reference.MOD_INFO, name, action);
    }

    private static NamedAction register(String name, ParameterizedAction action)
    {
        return ActionUtils.register(Reference.MOD_INFO, name, action);
    }

    private static ActionResult blinkDriveTeleport(boolean maintainY)
    {
        World world = GameUtils.getClientWorld();
        EntityPlayerSP player = GameUtils.getClientPlayer();

        if (world != null && player != null && player.capabilities.isCreativeMode)
        {
            RayTraceResult trace = RayTraceUtils.getRayTraceFromEntity(world, GameUtils.getCameraEntity(),
                                                                       RayTraceUtils.RayTraceFluidHandling.SOURCE_ONLY, false,
                                                                       Math.max(GameUtils.getRenderDistanceChunks() * 16, world.getHeight()) + 32);

            if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                Vec3d pos = PositionUtils.adjustPositionToSideOfEntity(trace.hitVec, player, trace.sideHit);
                double y = maintainY ? EntityWrap.getY(player) : pos.y;
                player.sendChatMessage(String.format("/tp @p %s %s %s", pos.x, y, pos.z));
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    private static ActionResult copySignText(ActionContext ctx)
    {
        RayTraceResult trace = GameUtils.getRayTrace();

        if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            BlockPos pos = trace.getBlockPos();
            TileEntity te = ctx.getWorld().getTileEntity(pos);

            if (te instanceof TileEntitySign)
            {
                MiscUtils.copyTextFromSign((TileEntitySign) te);
                MessageUtils.printCustomActionbarMessage("tweakeroo.message.sign_text_copied");
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.FAIL;
    }

    public static ActionResult hotbarScroll(ActionContext ctx)
    {
        if (FeatureToggle.TWEAK_HOTBAR_SCROLL.getBooleanValue() && ctx.getPlayer() != null)
        {
            int currentRow = Configs.Internal.HOTBAR_SCROLL_CURRENT_ROW.getIntegerValue();
            InventoryUtils.swapHotbarWithInventoryRow(ctx.getPlayer(), currentRow);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private static ActionResult hotbarSwapRow(int row, ActionContext ctx)
    {
        if (FeatureToggle.TWEAK_HOTBAR_SWAP.getBooleanValue() && ctx.getPlayer() != null)
        {
            InventoryUtils.swapHotbarWithInventoryRow(ctx.getPlayer(), row - 1);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private static ActionResult reloadLanguagePacks()
    {
        Minecraft mc = GameUtils.getClient();
        mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());
        MessageDispatcher.success("tweakeroo.message.language_packs_reloaded");
        return ActionResult.SUCCESS;
    }

    public static ActionResult setFlySpeedValue(ActionContext ctx, String str)
    {
        try
        {
            double speed = Double.parseDouble(str);
            Configs.Internal.ACTIVE_FLY_SPEED_OVERRIDE_VALUE.setDoubleValue(speed);
            // Apply possible clamping
            speed = Configs.Internal.ACTIVE_FLY_SPEED_OVERRIDE_VALUE.getFloatValue();
            String strSpeed = String.format("%.3f", speed);
            MessageUtils.printCustomActionbarMessage("tweakeroo.message.set_fly_speed_non_preset_value_to", strSpeed);
            return ActionResult.SUCCESS;
        }
        catch (Exception ignore)
        {
            MessageUtils.printCustomActionbarMessage("tweakeroo.message.error.invalid_fly_speed_value", str);
            return ActionResult.FAIL;
        }
    }

    public static ActionResult setFlySpeedPreset(ActionContext ctx, int preset)
    {
        float speed = Configs.getFlySpeedConfig(preset - 1).getFloatValue();
        Configs.Internal.FLY_SPEED_PRESET.setIntegerValue(preset - 1);
        Configs.Internal.ACTIVE_FLY_SPEED_OVERRIDE_VALUE.setDoubleValue(speed);
        // Apply possible clamping
        speed = Configs.Internal.ACTIVE_FLY_SPEED_OVERRIDE_VALUE.getFloatValue();
        String strSpeed = String.format("%.3f", speed);

        MessageOutput output = ctx.getMessageOutputOrDefault(MessageOutput.CUSTOM_HOTBAR);
        MessageUtils.printMessage(output, "tweakeroo.message.set_fly_speed_preset_to", preset, strSpeed);

        return ActionResult.SUCCESS;
    }

    private static void setBreakingRestrictionMode(PlacementRestrictionMode mode)
    {
        Configs.Generic.BREAKING_RESTRICTION_MODE.setValue(mode);
        MessageUtils.printCustomActionbarMessage("tweakeroo.message.set_breaking_restriction_mode_to", mode.getDisplayName());
    }

    private static void setPlacementRestrictionMode(PlacementRestrictionMode mode)
    {
        Configs.Generic.PLACEMENT_RESTRICTION_MODE.setValue(mode);
        MessageUtils.printCustomActionbarMessage("tweakeroo.message.set_placement_restriction_mode_to", mode.getDisplayName());
    }

    private static ActionResult toggleGrabCursor()
    {
        Minecraft mc = GameUtils.getClient();

        if (mc.inGameHasFocus)
        {
            mc.setIngameNotInFocus();
            MessageUtils.printCustomActionbarMessage("tweakeroo.message.unfocusing_game");
        }
        else
        {
            mc.setIngameFocus();
            MessageUtils.printCustomActionbarMessage("tweakeroo.message.focusing_game");
        }

        return ActionResult.SUCCESS;
    }

    private static ActionResult toggleSkipAllRendering()
    {
        Minecraft mc = GameUtils.getClient();
        mc.skipRenderWorld = ! mc.skipRenderWorld;

        boolean enabled = mc.skipRenderWorld;
        String toggleKey = enabled ? "malilib.message.info.toggled_config_on" : "malilib.message.info.toggled_config_off";
        String name = StringUtils.translate("tweakeroo.hotkey.name.skipallrendering");
        MessageUtils.printCustomActionbarMessage(toggleKey, name);

        return ActionResult.SUCCESS;
    }

    private static ActionResult toggleSkipWorldRendering()
    {
        MiscUtils.skipWorldRendering = ! MiscUtils.skipWorldRendering;

        boolean enabled = MiscUtils.skipWorldRendering;
        String toggleKey = enabled ? "malilib.message.info.toggled_config_on" : "malilib.message.info.toggled_config_off";
        String name = StringUtils.translate("tweakeroo.hotkey.name.skipworldrendering");
        MessageUtils.printCustomActionbarMessage(toggleKey, name);

        return ActionResult.SUCCESS;
    }

    private static ActionResult toolPick()
    {
        RayTraceResult trace = GameUtils.getRayTrace();

        if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            InventoryUtils.trySwitchToEffectiveTool(trace.getBlockPos());
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    public static ActionResult zoomActivate(boolean activate)
    {
        if (activate)
        {
            MiscUtils.onZoomActivated();
        }
        else
        {
            MiscUtils.onZoomDeactivated();
        }

        return ActionResult.SUCCESS;
    }
}
