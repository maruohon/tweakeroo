package fi.dy.masa.tweakeroo.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import fi.dy.masa.malilib.config.ValueChangeCallback;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.callback.AdjustableKeyCallback;
import fi.dy.masa.malilib.input.callback.HotkeyCallback;
import fi.dy.masa.malilib.render.message.MessageUtils;
import fi.dy.masa.malilib.util.PositionUtils;
import fi.dy.masa.malilib.util.RayTraceUtils.RayTraceFluidHandling;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.tweakeroo.gui.ConfigScreen;
import fi.dy.masa.tweakeroo.tweaks.MiscTweaks;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import fi.dy.masa.tweakeroo.util.PlacementRestrictionMode;
import fi.dy.masa.tweakeroo.util.SnapAimMode;

public class Callbacks
{
    public static boolean skipWorldRendering;

    public static void init()
    {
        FeatureToggle.TWEAK_GAMMA_OVERRIDE.getBooleanConfig().setValueLoadCallback((newValue) -> {
            // If the feature is enabled on game launch, apply it here. Note: This does require the Generic configs to be read first.
            if (newValue) { Minecraft.getMinecraft().gameSettings.gammaSetting = Configs.Generic.GAMMA_OVERRIDE_VALUE.getIntegerValue(); }
        });

        FeatureToggle.TWEAK_GAMMA_OVERRIDE.getBooleanConfig().setValueChangeCallback((newValue, oldValue) -> {
            Minecraft mc = Minecraft.getMinecraft();
            if (newValue) {
                Configs.Internal.GAMMA_VALUE_ORIGINAL.setDoubleValue(mc.gameSettings.gammaSetting);
                mc.gameSettings.gammaSetting = Configs.Generic.GAMMA_OVERRIDE_VALUE.getIntegerValue();
            }
            else { mc.gameSettings.gammaSetting = Configs.Internal.GAMMA_VALUE_ORIGINAL.getFloatValue(); }
        });

        DisableToggle.DISABLE_SLIME_BLOCK_SLOWDOWN.getBooleanConfig().setValueLoadCallback((newValue) -> { if (newValue) { Blocks.SLIME_BLOCK.slipperiness = Blocks.STONE.slipperiness; } });
        DisableToggle.DISABLE_SLIME_BLOCK_SLOWDOWN.getBooleanConfig().setValueChangeCallback((newValue, oldValue) -> {
            if (newValue) { Blocks.SLIME_BLOCK.slipperiness = Blocks.STONE.slipperiness; }
            else { Blocks.SLIME_BLOCK.slipperiness = Configs.Internal.SLIME_BLOCK_SLIPPERINESS_ORIGINAL.getFloatValue(); }
        });

        Minecraft mc = Minecraft.getMinecraft();
        Configs.Internal.GAMMA_VALUE_ORIGINAL.setDoubleValue(mc.gameSettings.gammaSetting);
        Configs.Internal.SLIME_BLOCK_SLIPPERINESS_ORIGINAL.setDoubleValue(Blocks.SLIME_BLOCK.slipperiness);

        Configs.Lists.REPAIR_MODE_SLOTS.setValueLoadCallback(InventoryUtils::setRepairModeSlots);
        Configs.Lists.REPAIR_MODE_SLOTS.setValueChangeCallback((newValue, oldValue) -> InventoryUtils.setRepairModeSlots(newValue));
        Configs.Lists.SWAP_BROKEN_TOOLS_SLOTS.setValueLoadCallback(InventoryUtils::setSwapBrokenToolsSlots);
        Configs.Lists.SWAP_BROKEN_TOOLS_SLOTS.setValueChangeCallback((newValue, oldValue) -> InventoryUtils.setSwapBrokenToolsSlots(newValue));
        Configs.Lists.UNSTACKING_ITEMS.setValueLoadCallback(InventoryUtils::setUnstackingItems);
        Configs.Lists.UNSTACKING_ITEMS.setValueChangeCallback((newValue, oldValue) -> InventoryUtils.setUnstackingItems(newValue));

        Configs.Lists.FAST_RIGHT_CLICK_BLOCK_LIST.setValueLoadCallback(PlacementTweaks::updateFastRightClickBlockRestriction);
        Configs.Lists.FAST_RIGHT_CLICK_BLOCK_LIST.setValueChangeCallback((newValue, oldValue) -> PlacementTweaks.updateFastRightClickBlockRestriction(newValue));

        Configs.Lists.FAST_RIGHT_CLICK_ITEM_LIST.setValueLoadCallback(PlacementTweaks::updateFastRightClickItemRestriction);
        Configs.Lists.FAST_RIGHT_CLICK_ITEM_LIST.setValueChangeCallback((newValue, oldValue) -> PlacementTweaks.updateFastRightClickItemRestriction(newValue));

        Configs.Lists.FAST_PLACEMENT_ITEM_LIST.setValueLoadCallback(PlacementTweaks::updateFastPlacementItemRestriction);
        Configs.Lists.FAST_PLACEMENT_ITEM_LIST.setValueChangeCallback((newValue, oldValue) -> PlacementTweaks.updateFastPlacementItemRestriction(newValue));

        Configs.Lists.ITEM_GLINT_ITEM_LIST.setValueLoadCallback(MiscTweaks::updateItemGlintRestriction);
        Configs.Lists.ITEM_GLINT_ITEM_LIST.setValueChangeCallback((newValue, oldValue) -> MiscTweaks.updateItemGlintRestriction(newValue));

        Configs.Lists.POTION_WARNING_LIST.setValueLoadCallback(MiscTweaks::updatePotionRestrictionLists);
        Configs.Lists.POTION_WARNING_LIST.setValueChangeCallback((newValue, oldValue) -> MiscTweaks.updatePotionRestrictionLists(newValue));

        Configs.Lists.SOUND_DISABLE_LIST.setValueLoadCallback(MiscTweaks::updateSoundRestrictionLists);
        Configs.Lists.SOUND_DISABLE_LIST.setValueChangeCallback((newValue, oldValue) -> MiscTweaks.updateSoundRestrictionLists(newValue));

        FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getKeyBind().setCallback(new KeyCallbackToggleWithSpecialMessage(FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getBooleanConfig()));
        FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getBooleanConfig().setValueChangeCallback((newValue, oldValue) -> {
            if (Configs.Generic.PLACEMENT_RESTRICTION_TIED_TO_FAST.getBooleanValue())
            {
                FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.getBooleanConfig().setBooleanValue(newValue);
            }
        });
        FeatureToggle.TWEAK_FREE_CAMERA.getBooleanConfig().setValueChangeCallback((newValue, oldValue) -> CameraEntity.setCameraState(newValue));
        FeatureToggle.TWEAK_HOLD_ATTACK.getBooleanConfig().setValueChangeCallback(new FeatureCallbackHold(mc.gameSettings.keyBindAttack.getKeyCode()));
        FeatureToggle.TWEAK_HOLD_USE.getBooleanConfig().setValueChangeCallback(new FeatureCallbackHold(mc.gameSettings.keyBindUseItem.getKeyCode()));

        HotkeyCallback callbackGeneric = new KeyCallbackHotkeysGeneric(mc);
        HotkeyCallback callbackMessage = new KeyCallbackHotkeyWithMessage(mc);

        Hotkeys.BLINK_DRIVE.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.BLINK_DRIVE_Y_LEVEL.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.BREAKING_RESTRICTION_MODE_COLUMN.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.BREAKING_RESTRICTION_MODE_DIAGONAL.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.BREAKING_RESTRICTION_MODE_FACE.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.BREAKING_RESTRICTION_MODE_LAYER.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.BREAKING_RESTRICTION_MODE_LINE.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.BREAKING_RESTRICTION_MODE_PLANE.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.COPY_SIGN_TEXT.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.FLY_PRESET_1.getKeyBind().setCallback(new AdjustableKeyCallback(null, callbackGeneric));
        Hotkeys.FLY_PRESET_2.getKeyBind().setCallback(new AdjustableKeyCallback(null, callbackGeneric));
        Hotkeys.FLY_PRESET_3.getKeyBind().setCallback(new AdjustableKeyCallback(null, callbackGeneric));
        Hotkeys.FLY_PRESET_4.getKeyBind().setCallback(new AdjustableKeyCallback(null, callbackGeneric));
        Hotkeys.FREE_CAMERA_PLAYER_INPUTS.getKeyBind().setCallback((action, key) -> {
            BooleanConfig config = Configs.Generic.FREE_CAMERA_PLAYER_INPUTS;
            config.toggleBooleanValue();
            MessageUtils.printBooleanConfigToggleMessage(config.getPrettyName(), config.getBooleanValue());
            return true;
        });
        Hotkeys.FREE_CAMERA_PLAYER_MOVEMENT.getKeyBind().setCallback((action, key) -> {
            BooleanConfig config = Configs.Generic.FREE_CAMERA_PLAYER_MOVEMENT;
            config.toggleBooleanValue();
            MessageUtils.printBooleanConfigToggleMessage(config.getPrettyName(), config.getBooleanValue());
            return true;
        });
        Hotkeys.GHOST_BLOCK_REMOVER.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.HOTBAR_SWAP_1.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.HOTBAR_SWAP_2.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.HOTBAR_SWAP_3.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.HOTBAR_SCROLL.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.OPEN_CONFIG_GUI.getKeyBind().setCallback((a, k) -> BaseScreen.openGui(ConfigScreen.create()));
        Hotkeys.PLACEMENT_RESTRICTION_MODE_COLUMN.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_DIAGONAL.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_FACE.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_LAYER.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_LINE.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_PLANE.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.TOGGLE_GRAB_CURSOR.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.TOOL_PICK.getKeyBind().setCallback(callbackGeneric);
        Hotkeys.ZOOM_ACTIVATE.getKeyBind().setCallback(callbackGeneric);

        Hotkeys.SKIP_ALL_RENDERING.getKeyBind().setCallback(callbackMessage);
        Hotkeys.SKIP_WORLD_RENDERING.getKeyBind().setCallback(callbackMessage);

        createAdjustableCallbackFor(FeatureToggle.TWEAK_AFTER_CLICKER);
        createAdjustableCallbackFor(FeatureToggle.TWEAK_BREAKING_GRID);
        createAdjustableCallbackFor(FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE);
        createAdjustableCallbackFor(FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER);
        createAdjustableCallbackFor(FeatureToggle.TWEAK_PLACEMENT_GRID);
        createAdjustableCallbackFor(FeatureToggle.TWEAK_PLACEMENT_LIMIT);
        createAdjustableCallbackFor(FeatureToggle.TWEAK_SNAP_AIM);
        createAdjustableCallbackFor(FeatureToggle.TWEAK_ZOOM);
    }

    private static void createAdjustableCallbackFor(FeatureToggle feature)
    {
        feature.getKeyBind().setCallback(new AdjustableKeyCallback(null, new KeyCallbackToggleWithSpecialMessage(feature.getBooleanConfig())));
    }

    public static class FeatureCallbackHold implements ValueChangeCallback<Boolean>
    {
        private final int keyCode;

        public FeatureCallbackHold(int keyCode)
        {
            this.keyCode = keyCode;
        }

        @Override
        public void onValueChanged(Boolean newValue, Boolean oldValue)
        {
            if (newValue)
            {
                KeyBinding.setKeyBindState(this.keyCode, true);
                KeyBinding.onTick(this.keyCode);
            }
            else
            {
                KeyBinding.setKeyBindState(this.keyCode, false);
            }
        }
    }

    public static class KeyCallbackHotkeyWithMessage implements HotkeyCallback
    {
        private final Minecraft mc;

        public KeyCallbackHotkeyWithMessage(Minecraft mc)
        {
            this.mc = mc;
        }

        @Override
        public boolean onKeyAction(KeyAction action, KeyBind key)
        {
            if (key == Hotkeys.SKIP_ALL_RENDERING.getKeyBind())
            {
                this.mc.skipRenderWorld = ! this.mc.skipRenderWorld;

                String pre = mc.skipRenderWorld ? BaseScreen.TXT_GREEN : BaseScreen.TXT_RED;
                String status = StringUtils.translate("tweakeroo.message.value." + (this.mc.skipRenderWorld ? "on" : "off"));
                String message = StringUtils.translate("tweakeroo.message.toggled", "Skip All Rendering", pre + status + BaseScreen.TXT_RST);
                MessageUtils.printActionbarMessage(message);
            }
            else if (key == Hotkeys.SKIP_WORLD_RENDERING.getKeyBind())
            {
                skipWorldRendering = ! skipWorldRendering;

                boolean enabled = skipWorldRendering;
                String pre = enabled ? BaseScreen.TXT_GREEN : BaseScreen.TXT_RED;
                String status = StringUtils.translate("tweakeroo.message.value." + (enabled ? "on" : "off"));
                String message = StringUtils.translate("tweakeroo.message.toggled", "Skip World Rendering", pre + status + BaseScreen.TXT_RST);
                MessageUtils.printActionbarMessage(message);
            }

            return true;
        }
    }

    private static class KeyCallbackHotkeysGeneric implements HotkeyCallback
    {
        private final Minecraft mc;

        public KeyCallbackHotkeysGeneric(Minecraft mc)
        {
            this.mc = mc;
        }

        @Override
        public boolean onKeyAction(KeyAction action, KeyBind key)
        {
            if (key == Hotkeys.TOOL_PICK.getKeyBind())
            {
                if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    InventoryUtils.trySwitchToEffectiveTool(this.mc.objectMouseOver.getBlockPos());
                    return true;
                }
            }
            else if (key == Hotkeys.BLINK_DRIVE.getKeyBind())
            {
                this.blinkDriveTeleport(false);
                return true;
            }
            else if (key == Hotkeys.BLINK_DRIVE_Y_LEVEL.getKeyBind())
            {
                this.blinkDriveTeleport(true);
                return true;
            }
            else if (key == Hotkeys.COPY_SIGN_TEXT.getKeyBind())
            {
                RayTraceResult trace = this.mc.objectMouseOver;

                if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    BlockPos pos = trace.getBlockPos();
                    TileEntity te = this.mc.world.getTileEntity(pos);

                    if (te instanceof TileEntitySign)
                    {
                        MiscUtils.copyTextFromSign((TileEntitySign) te);
                        MessageUtils.printActionbarMessage("tweakeroo.message.sign_text_copied");
                    }
                }
                return true;
            }
            else if (key == Hotkeys.GHOST_BLOCK_REMOVER.getKeyBind())
            {
                MiscUtils.antiGhostBlock(this.mc);
                return true;
            }
            else if (key == Hotkeys.HOTBAR_SWAP_1.getKeyBind())
            {
                if (FeatureToggle.TWEAK_HOTBAR_SWAP.getBooleanValue())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(this.mc.player, 0);
                    return true;
                }
            }
            else if (key == Hotkeys.HOTBAR_SWAP_2.getKeyBind())
            {
                if (FeatureToggle.TWEAK_HOTBAR_SWAP.getBooleanValue())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(this.mc.player, 1);
                    return true;
                }
            }
            else if (key == Hotkeys.HOTBAR_SWAP_3.getKeyBind())
            {
                if (FeatureToggle.TWEAK_HOTBAR_SWAP.getBooleanValue())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(this.mc.player, 2);
                    return true;
                }
            }
            else if (key == Hotkeys.FLY_PRESET_1.getKeyBind())
            {
                this.setFlySpeedPreset(0);
                return true;
            }
            else if (key == Hotkeys.FLY_PRESET_2.getKeyBind())
            {
                this.setFlySpeedPreset(1);
                return true;
            }
            else if (key == Hotkeys.FLY_PRESET_3.getKeyBind())
            {
                this.setFlySpeedPreset(2);
                return true;
            }
            else if (key == Hotkeys.FLY_PRESET_4.getKeyBind())
            {
                this.setFlySpeedPreset(3);
                return true;
            }
            else if (key == Hotkeys.HOTBAR_SCROLL.getKeyBind())
            {
                if (FeatureToggle.TWEAK_HOTBAR_SCROLL.getBooleanValue())
                {
                    int currentRow = Configs.Internal.HOTBAR_SCROLL_CURRENT_ROW.getIntegerValue();
                    InventoryUtils.swapHotbarWithInventoryRow(mc.player, currentRow);
                    return true;
                }
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_COLUMN.getKeyBind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.COLUMN);
                return true;
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_DIAGONAL.getKeyBind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.DIAGONAL);
                return true;
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_FACE.getKeyBind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.FACE);
                return true;
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_LAYER.getKeyBind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.LAYER);
                return true;
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_LINE.getKeyBind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.LINE);
                return true;
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_PLANE.getKeyBind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.PLANE);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_COLUMN.getKeyBind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.COLUMN);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_DIAGONAL.getKeyBind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.DIAGONAL);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_FACE.getKeyBind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.FACE);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_LAYER.getKeyBind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.LAYER);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_LINE.getKeyBind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.LINE);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_PLANE.getKeyBind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.PLANE);
                return true;
            }
            else if (key == Hotkeys.TOGGLE_GRAB_CURSOR.getKeyBind())
            {
                if (this.mc.inGameHasFocus)
                {
                    this.mc.setIngameNotInFocus();
                    MessageUtils.printActionbarMessage("tweakeroo.message.unfocusing_game");
                }
                else
                {
                    this.mc.setIngameFocus();
                    MessageUtils.printActionbarMessage("tweakeroo.message.focusing_game");
                }
            }
            else if (key == Hotkeys.ZOOM_ACTIVATE.getKeyBind())
            {
                if (action == KeyAction.PRESS)
                {
                    MiscUtils.onZoomActivated();
                }
                else
                {
                    MiscUtils.onZoomDeactivated();
                }
            }

            return false;
        }

        private void setFlySpeedPreset(int preset)
        {
            Configs.Internal.FLY_SPEED_PRESET.setIntegerValue(preset);

            float speed = (float) Configs.getActiveFlySpeedConfig().getDoubleValue();
            String strPreset = BaseScreen.TXT_GREEN + (preset + 1) + BaseScreen.TXT_RST;
            String strSpeed = String.format("%s%.3f%s", BaseScreen.TXT_GREEN, speed, BaseScreen.TXT_RST);
            MessageUtils.printActionbarMessage("tweakeroo.message.set_fly_speed_preset_to", strPreset, strSpeed);
        }

        private void setBreakingRestrictionMode(PlacementRestrictionMode mode)
        {
            Configs.Generic.BREAKING_RESTRICTION_MODE.setOptionListValue(mode);

            String str = BaseScreen.TXT_GREEN + mode.name() + BaseScreen.TXT_RST;
            MessageUtils.printActionbarMessage("tweakeroo.message.set_breaking_restriction_mode_to", str);
        }

        private void setPlacementRestrictionMode(PlacementRestrictionMode mode)
        {
            Configs.Generic.PLACEMENT_RESTRICTION_MODE.setOptionListValue(mode);

            String str = BaseScreen.TXT_GREEN + mode.name() + BaseScreen.TXT_RST;
            MessageUtils.printActionbarMessage("tweakeroo.message.set_placement_restriction_mode_to", str);
        }

        private void blinkDriveTeleport(boolean maintainY)
        {
            if (this.mc.player.capabilities.isCreativeMode)
            {
                Entity entity = fi.dy.masa.malilib.util.EntityUtils.getCameraEntity();
                RayTraceResult trace = fi.dy.masa.malilib.util.RayTraceUtils
                        .getRayTraceFromEntity(this.mc.world, entity, RayTraceFluidHandling.SOURCE_ONLY,
                                false, this.mc.gameSettings.renderDistanceChunks * 16 + 200);

                if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    Vec3d pos = trace.hitVec;
                    pos = PositionUtils.adjustPositionToSideOfEntity(pos, this.mc.player, trace.sideHit);

                    this.mc.player.sendChatMessage(String.format("/tp @p %.6f %.6f %.6f", pos.x, maintainY ? this.mc.player.posY : pos.y, pos.z));
                }
            }
        }
    }

    private static class KeyCallbackToggleWithSpecialMessage implements HotkeyCallback
    {
        private final BooleanConfig config;

        private KeyCallbackToggleWithSpecialMessage(BooleanConfig config)
        {
            this.config = config;
        }

        @Override
        public boolean onKeyAction(KeyAction action, KeyBind key)
        {
            this.config.toggleBooleanValue();

            boolean enabled = this.config.getBooleanValue();
            String strStatus = StringUtils.translate("tweakeroo.message.value." + (enabled ? "on" : "off"));
            String preGreen = BaseScreen.TXT_GREEN;
            String preRed = BaseScreen.TXT_RED;
            String rst = BaseScreen.TXT_RST;
            String prettyName = this.config.getPrettyName();
            strStatus = (enabled ? preGreen : preRed) + strStatus + rst;

            if (key == FeatureToggle.TWEAK_FLY_SPEED.getKeyBind())
            {
                if (enabled)
                {
                    String strPreset = preGreen + (Configs.Internal.FLY_SPEED_PRESET.getIntegerValue() + 1) + rst;
                    String strSpeed = String.format("%s%.3f%s", preGreen, Configs.getActiveFlySpeedConfig().getDoubleValue(), rst);
                    MessageUtils.printActionbarMessage("tweakeroo.message.toggled_fly_speed_on", strStatus, strPreset, strSpeed);
                }
                else
                {
                    EntityPlayer player = Minecraft.getMinecraft().player;

                    if (player != null)
                    {
                        player.capabilities.setFlySpeed(0.05f);
                    }

                    MessageUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }
            else
            {
                if (enabled == false)
                {
                    MessageUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                    return true;
                }

                if (key == FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getKeyBind())
                {
                    String strMode = Configs.Generic.PLACEMENT_RESTRICTION_MODE.getOptionListValue().name();
                    MessageUtils.printActionbarMessage("tweakeroo.message.toggled_fast_placement_mode_on", strStatus, preGreen + strMode + rst);
                }
                else if (key == FeatureToggle.TWEAK_AFTER_CLICKER.getKeyBind())
                {
                    String strValue = Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getStringValue();
                    MessageUtils.printActionbarMessage("tweakeroo.message.toggled_after_clicker_on", strStatus, preGreen + strValue + rst);
                }
                else if (key == FeatureToggle.TWEAK_PLACEMENT_LIMIT.getKeyBind())
                {
                    String strValue = Configs.Generic.PLACEMENT_LIMIT.getStringValue();
                    MessageUtils.printActionbarMessage("tweakeroo.message.toggled_placement_limit_on", strStatus, preGreen + strValue + rst);
                }
                else if (key == FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getKeyBind())
                {
                    String strValue = Configs.Generic.HOTBAR_SLOT_CYCLE_MAX.getStringValue();
                    MessageUtils.printActionbarMessage("tweakeroo.message.toggled_slot_cycle_on", strStatus, preGreen + strValue + rst);
                }
                else if (key == FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER.getKeyBind())
                {
                    String strValue = Configs.Generic.HOTBAR_SLOT_RANDOMIZER_MAX.getStringValue();
                    MessageUtils.printActionbarMessage("tweakeroo.message.toggled_slot_randomizer_on", strStatus, preGreen + strValue + rst);
                }
                else if (key == FeatureToggle.TWEAK_PLACEMENT_GRID.getKeyBind())
                {
                    String strValue = Configs.Generic.PLACEMENT_GRID_SIZE.getStringValue();
                    MessageUtils.printActionbarMessage("tweakeroo.message.toggled_placement_grid_on", strStatus, preGreen + strValue + rst);
                }
                else if (key == FeatureToggle.TWEAK_BREAKING_GRID.getKeyBind())
                {
                    String strValue = Configs.Generic.BREAKING_GRID_SIZE.getStringValue();
                    MessageUtils.printActionbarMessage("tweakeroo.message.toggled_breaking_grid_on", strStatus, preGreen + strValue + rst);
                }
                else if (key == FeatureToggle.TWEAK_ZOOM.getKeyBind())
                {
                    String strValue = String.format("%s%.1f%s", preGreen, Configs.Generic.ZOOM_FOV.getDoubleValue(), rst);
                    MessageUtils.printActionbarMessage("tweakeroo.message.toggled_zoom_on", strStatus, strValue);
                }
                else if (key == FeatureToggle.TWEAK_SNAP_AIM.getKeyBind())
                {
                    SnapAimMode mode = Configs.Generic.SNAP_AIM_MODE.getOptionListValue();

                    if (mode == SnapAimMode.YAW)
                    {
                        String yaw = String.valueOf(Configs.Generic.SNAP_AIM_YAW_STEP.getDoubleValue());
                        MessageUtils.printActionbarMessage("tweakeroo.message.toggled_snap_aim_on_yaw", strStatus, preGreen + yaw + rst);
                    }
                    else if (mode == SnapAimMode.PITCH)
                    {
                        String pitch = String.valueOf(Configs.Generic.SNAP_AIM_PITCH_STEP.getDoubleValue());
                        MessageUtils.printActionbarMessage("tweakeroo.message.toggled_snap_aim_on_pitch", strStatus, preGreen + pitch + rst);
                    }
                    else
                    {
                        String yaw = String.valueOf(Configs.Generic.SNAP_AIM_YAW_STEP.getDoubleValue());
                        String pitch = String.valueOf(Configs.Generic.SNAP_AIM_PITCH_STEP.getDoubleValue());
                        MessageUtils.printActionbarMessage("tweakeroo.message.toggled_snap_aim_on_both", strStatus, preGreen + yaw + rst, preGreen + pitch + rst);
                    }
                }
            }

            return true;
        }
    }
}
