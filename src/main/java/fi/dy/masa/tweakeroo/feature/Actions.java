package fi.dy.masa.tweakeroo.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import fi.dy.masa.malilib.action.Action;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.overlay.message.MessageUtils;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.PositionUtils;
import fi.dy.masa.malilib.util.RayTraceUtils;
import fi.dy.masa.malilib.util.StringUtils;
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
    public static final NamedAction BLINK_DRIVE_TELEPORT_GROUND             = register("blockDriveTeleportGround", (ctx) -> blinkDriveTeleport(ctx.mc, false));
    public static final NamedAction BLINK_DRIVE_TELEPORT_SAME_Y             = register("blockDriveTeleportSameY", (ctx) -> blinkDriveTeleport(ctx.mc, true));
    public static final NamedAction COPY_SIGN_TEXT                          = register("copySignText", (ctx) -> copySignText(ctx.mc));
    public static final NamedAction GHOST_BLOCK_REMOVER_MANUAL              = register("ghostBlockRemoverManual", (ctx) -> MiscUtils.antiGhostBlock(ctx.mc));
    public static final NamedAction HOTBAR_SCROLL                           = register("hotbarScroll", (ctx) -> hotbarScroll(ctx.mc));
    public static final NamedAction HOTBAR_SWAP_ROW_1                       = register("hotbarSwapRow1", (ctx) -> hotbarSwapRow(ctx.mc, 1));
    public static final NamedAction HOTBAR_SWAP_ROW_2                       = register("hotbarSwapRow2", (ctx) -> hotbarSwapRow(ctx.mc, 2));
    public static final NamedAction HOTBAR_SWAP_ROW_3                       = register("hotbarSwapRow3", (ctx) -> hotbarSwapRow(ctx.mc, 3));
    public static final NamedAction OPEN_CONFIG_SCREEN                      = register("openConfigScreen", ConfigScreen::open);
    public static final NamedAction RELOAD_LANGUAGE_PACKS                   = register("reloadLanguagePacks", (ctx) -> reloadLanguagePacks(ctx.mc));
    public static final NamedAction SET_BREAKING_RESTRICTION_MODE_COLUMN    = register("setBreakingRestrictionModeColumn",   () -> setBreakingRestrictionMode(PlacementRestrictionMode.COLUMN));
    public static final NamedAction SET_BREAKING_RESTRICTION_MODE_DIAGONAL  = register("setBreakingRestrictionModeDiagonal", () -> setBreakingRestrictionMode(PlacementRestrictionMode.DIAGONAL));
    public static final NamedAction SET_BREAKING_RESTRICTION_MODE_FACE      = register("setBreakingRestrictionModeFace",     () -> setBreakingRestrictionMode(PlacementRestrictionMode.FACE));
    public static final NamedAction SET_BREAKING_RESTRICTION_MODE_LAYER     = register("setBreakingRestrictionModeLayer",    () -> setBreakingRestrictionMode(PlacementRestrictionMode.LAYER));
    public static final NamedAction SET_BREAKING_RESTRICTION_MODE_LINE      = register("setBreakingRestrictionModeLine",     () -> setBreakingRestrictionMode(PlacementRestrictionMode.LINE));
    public static final NamedAction SET_BREAKING_RESTRICTION_MODE_PLANE     = register("setBreakingRestrictionModePlane",    () -> setBreakingRestrictionMode(PlacementRestrictionMode.PLANE));
    public static final NamedAction SET_FLY_SPEED_PRESET_1                  = register("setFlySpeedPreset1", () -> setFlySpeedPreset(1));
    public static final NamedAction SET_FLY_SPEED_PRESET_2                  = register("setFlySpeedPreset2", () -> setFlySpeedPreset(2));
    public static final NamedAction SET_FLY_SPEED_PRESET_3                  = register("setFlySpeedPreset3", () -> setFlySpeedPreset(3));
    public static final NamedAction SET_FLY_SPEED_PRESET_4                  = register("setFlySpeedPreset4", () -> setFlySpeedPreset(4));
    public static final NamedAction SET_PLACEMENT_RESTRICTION_MODE_COLUMN   = register("setPlacementRestrictionModeColumn",   () -> setPlacementRestrictionMode(PlacementRestrictionMode.COLUMN));
    public static final NamedAction SET_PLACEMENT_RESTRICTION_MODE_DIAGONAL = register("setPlacementRestrictionModeDiagonal", () -> setPlacementRestrictionMode(PlacementRestrictionMode.DIAGONAL));
    public static final NamedAction SET_PLACEMENT_RESTRICTION_MODE_FACE     = register("setPlacementRestrictionModeFace",     () -> setPlacementRestrictionMode(PlacementRestrictionMode.FACE));
    public static final NamedAction SET_PLACEMENT_RESTRICTION_MODE_LAYER    = register("setPlacementRestrictionModeLayer",    () -> setPlacementRestrictionMode(PlacementRestrictionMode.LAYER));
    public static final NamedAction SET_PLACEMENT_RESTRICTION_MODE_LINE     = register("setPlacementRestrictionModeLine",     () -> setPlacementRestrictionMode(PlacementRestrictionMode.LINE));
    public static final NamedAction SET_PLACEMENT_RESTRICTION_MODE_PLANE    = register("setPlacementRestrictionModePlane",    () -> setPlacementRestrictionMode(PlacementRestrictionMode.PLANE));
    public static final NamedAction TOGGLE_GRAB_CURSOR                      = register("toggleGrabCursor", (ctx) -> toggleGrabCursor(ctx.mc));
    public static final NamedAction TOGGLE_SKIP_ALL_RENDERING               = register("toggleSkipAllRendering", (ctx) -> toggleSkipAllRendering(ctx.mc));
    public static final NamedAction TOGGLE_SKIP_WORLD_RENDERING             = register("toggleSkipWorldRendering", Actions::toggleSkipWorldRendering);
    public static final NamedAction TOOL_PICK                               = register("toolPick", (ctx) -> toolPick(ctx.mc));
    public static final NamedAction ZOOM_ACTIVATE                           = register("zoomActivate", (ctx) -> zoomActivate(true));
    public static final NamedAction ZOOM_DEACTIVATE                         = register("zoomDeactivate", (ctx) -> zoomActivate(false));

    public static void init()
    {
        for (FeatureToggle feature : FeatureToggle.VALUES)
        {
            NamedAction.registerToggle(Reference.MOD_INFO, feature.getName(), feature.getBooleanConfig());
        }

        for (DisableToggle feature : DisableToggle.VALUES)
        {
            NamedAction.registerToggle(Reference.MOD_INFO, feature.getName(), feature.getBooleanConfig());
        }
    }

    private static NamedAction register(String name, EventListener action)
    {
        return NamedAction.register(Reference.MOD_INFO, name, action);
    }

    private static NamedAction register(String name, Action action)
    {
        return NamedAction.register(Reference.MOD_INFO, name, action);
    }

    private static ActionResult blinkDriveTeleport(Minecraft mc, boolean maintainY)
    {
        if (mc.player.capabilities.isCreativeMode)
        {
            Entity entity = EntityUtils.getCameraEntity();
            RayTraceResult trace = RayTraceUtils.getRayTraceFromEntity(mc.world, entity,
                RayTraceUtils.RayTraceFluidHandling.SOURCE_ONLY, false,
                mc.gameSettings.renderDistanceChunks * 16 + 200);

            if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                Vec3d pos = PositionUtils.adjustPositionToSideOfEntity(trace.hitVec, mc.player, trace.sideHit);
                double y = maintainY ? mc.player.posY : pos.y;
                mc.player.sendChatMessage(String.format("/tp @p %s %s %s", pos.x, y, pos.z));
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    private static ActionResult copySignText(Minecraft mc)
    {
        RayTraceResult trace = mc.objectMouseOver;

        if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            BlockPos pos = trace.getBlockPos();
            TileEntity te = mc.world.getTileEntity(pos);

            if (te instanceof TileEntitySign)
            {
                MiscUtils.copyTextFromSign((TileEntitySign) te);
                MessageUtils.printCustomActionbarMessage("tweakeroo.message.sign_text_copied");
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.FAIL;
    }

    private static ActionResult hotbarScroll(Minecraft mc)
    {
        if (FeatureToggle.TWEAK_HOTBAR_SCROLL.getBooleanValue())
        {
            int currentRow = Configs.Internal.HOTBAR_SCROLL_CURRENT_ROW.getIntegerValue();
            InventoryUtils.swapHotbarWithInventoryRow(mc.player, currentRow);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private static ActionResult hotbarSwapRow(Minecraft mc, int row)
    {
        if (FeatureToggle.TWEAK_HOTBAR_SWAP.getBooleanValue())
        {
            InventoryUtils.swapHotbarWithInventoryRow(mc.player, row - 1);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private static ActionResult reloadLanguagePacks(Minecraft mc)
    {
        mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());
        MessageUtils.success("tweakeroo.message.language_packs_reloaded");
        return ActionResult.SUCCESS;
    }

    private static void setFlySpeedPreset(int preset)
    {
        Configs.Internal.FLY_SPEED_PRESET.setValue(preset - 1);
        String strSpeed = String.format("%.3f", Configs.getActiveFlySpeedConfig().getFloatValue());
        MessageUtils.printCustomActionbarMessage("tweakeroo.message.set_fly_speed_preset_to", preset, strSpeed);
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

    private static ActionResult toggleGrabCursor(Minecraft mc)
    {
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

    private static ActionResult toggleSkipAllRendering(Minecraft mc)
    {
        mc.skipRenderWorld = ! mc.skipRenderWorld;

        boolean enabled = mc.skipRenderWorld;
        String toggleKey = enabled ? "malilib.message.toggled_config_on" : "malilib.message.toggled_config_off";
        String name = StringUtils.translate("tweakeroo.hotkey.name.skipallrendering");
        MessageUtils.printCustomActionbarMessage(toggleKey, name);
        return ActionResult.SUCCESS;
    }

    private static ActionResult toggleSkipWorldRendering()
    {
        MiscUtils.skipWorldRendering = ! MiscUtils.skipWorldRendering;

        boolean enabled = MiscUtils.skipWorldRendering;
        String toggleKey = enabled ? "malilib.message.toggled_config_on" : "malilib.message.toggled_config_off";
        String name = StringUtils.translate("tweakeroo.hotkey.name.skipworldrendering");
        MessageUtils.printCustomActionbarMessage(toggleKey, name);
        return ActionResult.SUCCESS;
    }

    private static ActionResult toolPick(Minecraft mc)
    {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            InventoryUtils.trySwitchToEffectiveTool(mc.objectMouseOver.getBlockPos());
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
