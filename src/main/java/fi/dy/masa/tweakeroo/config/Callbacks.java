package fi.dy.masa.tweakeroo.config;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeyCallbackAdjustable;
import fi.dy.masa.malilib.hotkeys.KeyCallbackToggleBooleanConfigWithMessage;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.tweakeroo.gui.GuiConfigs;
import fi.dy.masa.tweakeroo.mixin.IMixinAbstractBlock;
import fi.dy.masa.tweakeroo.mixin.IMixinSimpleOption;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import fi.dy.masa.tweakeroo.util.PlacementRestrictionMode;
import fi.dy.masa.tweakeroo.util.SnapAimMode;

public class Callbacks
{
    public static boolean skipWorldRendering;

    public static void init(MinecraftClient mc)
    {
        FeatureToggle.TWEAK_GAMMA_OVERRIDE.setValueChangeCallback(new FeatureCallbackGamma(mc));
        Configs.Disable.DISABLE_SLIME_BLOCK_SLOWDOWN.setValueChangeCallback(new FeatureCallbackSlime(Configs.Disable.DISABLE_SLIME_BLOCK_SLOWDOWN));

        FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getKeybind().setCallback(new KeyCallbackToggleFastMode(FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT));
        FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.setValueChangeCallback((cfg) -> {
            if (Configs.Generic.PLACEMENT_RESTRICTION_TIED_TO_FAST.getBooleanValue())
            {
                FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.setBooleanValue(cfg.getBooleanValue());
            }
        });
        FeatureToggle.TWEAK_FREE_CAMERA.setValueChangeCallback((cfg) -> CameraEntity.setCameraState(cfg.getBooleanValue()));
        FeatureToggle.TWEAK_HOLD_ATTACK.setValueChangeCallback(new FeatureCallbackHold(mc.options.attackKey));
        FeatureToggle.TWEAK_HOLD_USE.setValueChangeCallback(new FeatureCallbackHold(mc.options.useKey));

        IHotkeyCallback callbackGeneric = new KeyCallbackHotkeysGeneric(mc);
        IHotkeyCallback callbackMessage = new KeyCallbackHotkeyWithMessage(mc);

        Hotkeys.BREAKING_RESTRICTION_MODE_COLUMN.getKeybind().setCallback(callbackGeneric);
        Hotkeys.BREAKING_RESTRICTION_MODE_DIAGONAL.getKeybind().setCallback(callbackGeneric);
        Hotkeys.BREAKING_RESTRICTION_MODE_FACE.getKeybind().setCallback(callbackGeneric);
        Hotkeys.BREAKING_RESTRICTION_MODE_LAYER.getKeybind().setCallback(callbackGeneric);
        Hotkeys.BREAKING_RESTRICTION_MODE_LINE.getKeybind().setCallback(callbackGeneric);
        Hotkeys.BREAKING_RESTRICTION_MODE_PLANE.getKeybind().setCallback(callbackGeneric);
        Hotkeys.COPY_SIGN_TEXT.getKeybind().setCallback(callbackGeneric);
        Hotkeys.FLY_PRESET_1.getKeybind().setCallback(callbackGeneric);
        Hotkeys.FLY_PRESET_2.getKeybind().setCallback(callbackGeneric);
        Hotkeys.FLY_PRESET_3.getKeybind().setCallback(callbackGeneric);
        Hotkeys.FLY_PRESET_4.getKeybind().setCallback(callbackGeneric);
        Hotkeys.FREE_CAMERA_PLAYER_INPUTS.getKeybind().setCallback((action, key) -> {
            IConfigBoolean config = Configs.Generic.FREE_CAMERA_PLAYER_INPUTS;
            config.toggleBooleanValue();
            InfoUtils.printBooleanConfigToggleMessage(config.getPrettyName(), config.getBooleanValue());
            return true;
        });
        Hotkeys.FREE_CAMERA_PLAYER_MOVEMENT.getKeybind().setCallback((action, key) -> {
            IConfigBoolean config = Configs.Generic.FREE_CAMERA_PLAYER_MOVEMENT;
            config.toggleBooleanValue();
            InfoUtils.printBooleanConfigToggleMessage(config.getPrettyName(), config.getBooleanValue());
            return true;
        });
        Hotkeys.HOTBAR_SWAP_1.getKeybind().setCallback(callbackGeneric);
        Hotkeys.HOTBAR_SWAP_2.getKeybind().setCallback(callbackGeneric);
        Hotkeys.HOTBAR_SWAP_3.getKeybind().setCallback(callbackGeneric);
        Hotkeys.HOTBAR_SCROLL.getKeybind().setCallback(callbackGeneric);
        Hotkeys.OPEN_CONFIG_GUI.getKeybind().setCallback(callbackGeneric);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_COLUMN.getKeybind().setCallback(callbackGeneric);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_DIAGONAL.getKeybind().setCallback(callbackGeneric);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_FACE.getKeybind().setCallback(callbackGeneric);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_LAYER.getKeybind().setCallback(callbackGeneric);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_LINE.getKeybind().setCallback(callbackGeneric);
        Hotkeys.PLACEMENT_RESTRICTION_MODE_PLANE.getKeybind().setCallback(callbackGeneric);
        Hotkeys.SIT_DOWN_NEARBY_PETS.getKeybind().setCallback((a, k) -> MiscUtils.commandNearbyPets(true));
        Hotkeys.STAND_UP_NEARBY_PETS.getKeybind().setCallback((a, k) -> MiscUtils.commandNearbyPets(false));
        Hotkeys.SWAP_ELYTRA_CHESTPLATE.getKeybind().setCallback(callbackGeneric);
        Hotkeys.TOGGLE_CARPET_AP_PROTOCOL.getKeybind().setCallback(new KeyCallbackToggleBooleanConfigWithMessage(Configs.Generic.CARPET_ACCURATE_PLACEMENT_PROTOCOL));
        Hotkeys.TOGGLE_GRAB_CURSOR.getKeybind().setCallback(callbackGeneric);
        Hotkeys.TOOL_PICK.getKeybind().setCallback(callbackGeneric);
        Hotkeys.WRITE_MAPS_AS_IMAGES.getKeybind().setCallback((a, k) -> MiscUtils.writeAllMapsAsImages());
        Hotkeys.ZOOM_ACTIVATE.getKeybind().setCallback(callbackGeneric);

        Hotkeys.SKIP_ALL_RENDERING.getKeybind().setCallback(callbackMessage);
        Hotkeys.SKIP_WORLD_RENDERING.getKeybind().setCallback(callbackMessage);

        Configs.Generic.TOOL_SWITCHABLE_SLOTS.setValueChangeCallback((cfg) -> InventoryUtils.setToolSwitchableSlots(cfg.getStringValue()));
        // TODO 1.19.3+
        //Configs.Lists.CREATIVE_EXTRA_ITEMS.setValueChangeCallback((cfg) -> CreativeExtraItems.setCreativeExtraItems(cfg.getStrings()));

        FeatureToggle.TWEAK_AFTER_CLICKER.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_AFTER_CLICKER));
        FeatureToggle.TWEAK_BREAKING_GRID.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_BREAKING_GRID));
        FeatureToggle.TWEAK_FLY_SPEED.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_FLY_SPEED));
        FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE));
        FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER));
        FeatureToggle.TWEAK_PLACEMENT_GRID.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_PLACEMENT_GRID));
        FeatureToggle.TWEAK_PLACEMENT_LIMIT.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_PLACEMENT_LIMIT));
        FeatureToggle.TWEAK_SNAP_AIM.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_SNAP_AIM));
        FeatureToggle.TWEAK_ZOOM.getKeybind().setCallback(KeyCallbackAdjustableFeature.createCallback(FeatureToggle.TWEAK_ZOOM));

        Configs.Disable.DISABLE_RENDERING_SCAFFOLDING.setValueChangeCallback((cfg) -> mc.worldRenderer.reload());
    }

    public static class FeatureCallbackHold implements IValueChangeCallback<IConfigBoolean>
    {
        private final KeyBinding keyBind;

        public FeatureCallbackHold(KeyBinding keyBind)
        {
            this.keyBind = keyBind;
        }

        @Override
        public void onValueChanged(IConfigBoolean config)
        {
            if (config.getBooleanValue())
            {
                KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(this.keyBind.getBoundKeyTranslationKey()), true);
                KeyBinding.onKeyPressed(InputUtil.fromTranslationKey(this.keyBind.getBoundKeyTranslationKey()));
            }
            else
            {
                KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(this.keyBind.getBoundKeyTranslationKey()), false);
            }
        }
    }

    public static class FeatureCallbackGamma implements IValueChangeCallback<IConfigBoolean>
    {
        private final MinecraftClient mc;

        public FeatureCallbackGamma(MinecraftClient mc)
        {
            this.mc = mc;
            double gamma = this.mc.options.getGamma().getValue();

            if (gamma <= 1.0F)
            {
                Configs.Internal.GAMMA_VALUE_ORIGINAL.setDoubleValue(gamma);
            }

            // If the feature is enabled on game launch, apply it here
//            if (feature.getBooleanValue())
//            {
//                this.applyValue(Configs.Generic.GAMMA_OVERRIDE_VALUE.getDoubleValue());
//            }

            // The config file loaded after Callback.init(), so this code is useless.
        }

        @Override
        public void onValueChanged(IConfigBoolean config)
        {
            double gamma;

            if (config.getBooleanValue())
            {
                Configs.Internal.GAMMA_VALUE_ORIGINAL.setDoubleValue(this.mc.options.getGamma().getValue());
                gamma = Configs.Generic.GAMMA_OVERRIDE_VALUE.getDoubleValue();
            }
            else
            {
                gamma = Configs.Internal.GAMMA_VALUE_ORIGINAL.getDoubleValue();
            }

            this.applyValue(gamma);
        }

        private void applyValue(double gamma)
        {
            @SuppressWarnings("unchecked")
            IMixinSimpleOption<Double> opt = (IMixinSimpleOption<Double>) (Object) this.mc.options.getGamma();
            opt.tweakeroo_setValueWithoutCheck(gamma);
        }
    }

    public static class FeatureCallbackSlime implements IValueChangeCallback<ConfigBoolean>
    {
        public FeatureCallbackSlime(ConfigBoolean feature)
        {
            Configs.Internal.SLIME_BLOCK_SLIPPERINESS_ORIGINAL.setDoubleValue(Blocks.SLIME_BLOCK.getSlipperiness());

            // If the feature is enabled on game launch, apply the overridden value here
            if (feature.getBooleanValue())
            {
                ((IMixinAbstractBlock) Blocks.SLIME_BLOCK).setFriction(Blocks.STONE.getSlipperiness());
            }
        }

        @Override
        public void onValueChanged(ConfigBoolean config)
        {
            if (config.getBooleanValue())
            {
                ((IMixinAbstractBlock) Blocks.SLIME_BLOCK).setFriction(Blocks.STONE.getSlipperiness());
            }
            else
            {
                ((IMixinAbstractBlock) Blocks.SLIME_BLOCK).setFriction((float) Configs.Internal.SLIME_BLOCK_SLIPPERINESS_ORIGINAL.getDoubleValue());
            }
        }
    }

    public static class KeyCallbackHotkeyWithMessage implements IHotkeyCallback
    {
        private final MinecraftClient mc;

        public KeyCallbackHotkeyWithMessage(MinecraftClient mc)
        {
            this.mc = mc;
        }

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            if (key == Hotkeys.SKIP_ALL_RENDERING.getKeybind())
            {
                this.mc.skipGameRender = ! this.mc.skipGameRender;

                String pre = mc.skipGameRender ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
                String status = StringUtils.translate("tweakeroo.message.value." + (this.mc.skipGameRender ? "on" : "off"));
                String message = StringUtils.translate("tweakeroo.message.toggled", "Skip All Rendering", pre + status + GuiBase.TXT_RST);
                InfoUtils.printActionbarMessage(message);
            }
            else if (key == Hotkeys.SKIP_WORLD_RENDERING.getKeybind())
            {
                skipWorldRendering = ! skipWorldRendering;

                boolean enabled = skipWorldRendering;
                String pre = enabled ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
                String status = StringUtils.translate("tweakeroo.message.value." + (enabled ? "on" : "off"));
                String message = StringUtils.translate("tweakeroo.message.toggled", "Skip World Rendering", pre + status + GuiBase.TXT_RST);
                InfoUtils.printActionbarMessage(message);
            }

            return true;
        }
    }

    private static class KeyCallbackHotkeysGeneric implements IHotkeyCallback
    {
        private final MinecraftClient mc;

        public KeyCallbackHotkeysGeneric(MinecraftClient mc)
        {
            this.mc = mc;
        }

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            if (key == Hotkeys.TOOL_PICK.getKeybind())
            {
                if (this.mc.crosshairTarget != null && this.mc.crosshairTarget.getType() == HitResult.Type.BLOCK)
                {
                    InventoryUtils.trySwitchToEffectiveTool(((BlockHitResult) this.mc.crosshairTarget).getBlockPos());
                    return true;
                }
            }
            else if (key == Hotkeys.COPY_SIGN_TEXT.getKeybind())
            {
                HitResult trace = this.mc.crosshairTarget;

                if (trace != null && trace.getType() == HitResult.Type.BLOCK)
                {
                    BlockPos pos = ((BlockHitResult) trace).getBlockPos();
                    BlockEntity te = this.mc.world.getBlockEntity(pos);

                    if (te instanceof SignBlockEntity)
                    {
                        MiscUtils.copyTextFromSign((SignBlockEntity) te, ((SignBlockEntity) te).isPlayerFacingFront(mc.player));
                        InfoUtils.printActionbarMessage("tweakeroo.message.sign_text_copied");
                    }
                }
                return true;
            }
            else if (key == Hotkeys.HOTBAR_SWAP_1.getKeybind())
            {
                if (FeatureToggle.TWEAK_HOTBAR_SWAP.getBooleanValue())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(this.mc.player, 0);
                    return true;
                }
            }
            else if (key == Hotkeys.HOTBAR_SWAP_2.getKeybind())
            {
                if (FeatureToggle.TWEAK_HOTBAR_SWAP.getBooleanValue())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(this.mc.player, 1);
                    return true;
                }
            }
            else if (key == Hotkeys.HOTBAR_SWAP_3.getKeybind())
            {
                if (FeatureToggle.TWEAK_HOTBAR_SWAP.getBooleanValue())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(this.mc.player, 2);
                    return true;
                }
            }
            else if (key == Hotkeys.FLY_PRESET_1.getKeybind())
            {
                this.setFlySpeedPreset(0);
                return true;
            }
            else if (key == Hotkeys.FLY_PRESET_2.getKeybind())
            {
                this.setFlySpeedPreset(1);
                return true;
            }
            else if (key == Hotkeys.FLY_PRESET_3.getKeybind())
            {
                this.setFlySpeedPreset(2);
                return true;
            }
            else if (key == Hotkeys.FLY_PRESET_4.getKeybind())
            {
                this.setFlySpeedPreset(3);
                return true;
            }
            else if (key == Hotkeys.HOTBAR_SCROLL.getKeybind())
            {
                if (FeatureToggle.TWEAK_HOTBAR_SCROLL.getBooleanValue())
                {
                    int currentRow = Configs.Internal.HOTBAR_SCROLL_CURRENT_ROW.getIntegerValue();
                    InventoryUtils.swapHotbarWithInventoryRow(mc.player, currentRow);
                    return true;
                }
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_COLUMN.getKeybind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.COLUMN);
                return true;
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_DIAGONAL.getKeybind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.DIAGONAL);
                return true;
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_FACE.getKeybind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.FACE);
                return true;
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_LAYER.getKeybind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.LAYER);
                return true;
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_LINE.getKeybind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.LINE);
                return true;
            }
            else if (key == Hotkeys.BREAKING_RESTRICTION_MODE_PLANE.getKeybind())
            {
                this.setBreakingRestrictionMode(PlacementRestrictionMode.PLANE);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_COLUMN.getKeybind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.COLUMN);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_DIAGONAL.getKeybind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.DIAGONAL);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_FACE.getKeybind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.FACE);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_LAYER.getKeybind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.LAYER);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_LINE.getKeybind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.LINE);
                return true;
            }
            else if (key == Hotkeys.PLACEMENT_RESTRICTION_MODE_PLANE.getKeybind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.PLANE);
                return true;
            }
            else if (key == Hotkeys.OPEN_CONFIG_GUI.getKeybind())
            {
                GuiBase.openGui(new GuiConfigs());
                return true;
            }
            else if (key == Hotkeys.SWAP_ELYTRA_CHESTPLATE.getKeybind())
            {
                InventoryUtils.swapElytraWithChestPlate(this.mc.player);
                return true;
            }
            else if (key == Hotkeys.TOGGLE_GRAB_CURSOR.getKeybind())
            {
                if (this.mc.isWindowFocused())
                {
                    if (this.mc.mouse.isCursorLocked())
                    {
                        this.mc.mouse.unlockCursor();
                        InfoUtils.printActionbarMessage("tweakeroo.message.unfocusing_game");
                    }
                    else
                    {
                        this.mc.mouse.lockCursor();
                        InfoUtils.printActionbarMessage("tweakeroo.message.focusing_game");
                    }
                }
            }
            else if (key == Hotkeys.ZOOM_ACTIVATE.getKeybind())
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
            String strPreset = GuiBase.TXT_GREEN + (preset + 1) + GuiBase.TXT_RST;
            String strSpeed = String.format("%s%.3f%s", GuiBase.TXT_GREEN, speed, GuiBase.TXT_RST);
            InfoUtils.printActionbarMessage("tweakeroo.message.set_fly_speed_preset_to", strPreset, strSpeed);
        }

        private void setBreakingRestrictionMode(PlacementRestrictionMode mode)
        {
            Configs.Generic.BREAKING_RESTRICTION_MODE.setOptionListValue(mode);

            String str = GuiBase.TXT_GREEN + mode.name() + GuiBase.TXT_RST;
            InfoUtils.printActionbarMessage("tweakeroo.message.set_breaking_restriction_mode_to", str);
        }

        private void setPlacementRestrictionMode(PlacementRestrictionMode mode)
        {
            Configs.Generic.PLACEMENT_RESTRICTION_MODE.setOptionListValue(mode);

            String str = GuiBase.TXT_GREEN + mode.name() + GuiBase.TXT_RST;
            InfoUtils.printActionbarMessage("tweakeroo.message.set_placement_restriction_mode_to", str);
        }
    }

    private static class KeyCallbackToggleFastMode implements IHotkeyCallback
    {
        private final FeatureToggle feature;

        private KeyCallbackToggleFastMode(FeatureToggle feature)
        {
            this.feature = feature;
        }

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            this.feature.toggleBooleanValue();

            boolean enabled = this.feature.getBooleanValue();
            String strStatus = StringUtils.translate("tweakeroo.message.value." + (enabled ? "on" : "off"));
            String preGreen = GuiBase.TXT_GREEN;
            String preRed = GuiBase.TXT_RED;
            String rst = GuiBase.TXT_RST;
            strStatus = (enabled ? preGreen : preRed) + strStatus + rst;

            if (enabled)
            {
                String strMode = ((PlacementRestrictionMode) Configs.Generic.PLACEMENT_RESTRICTION_MODE.getOptionListValue()).name();
                InfoUtils.printActionbarMessage("tweakeroo.message.toggled_fast_placement_mode_on", strStatus, preGreen + strMode + rst);
            }
            else
            {
                InfoUtils.printActionbarMessage("tweakeroo.message.toggled", this.feature.getPrettyName(), strStatus);
            }

            return true;
        }
    }

    private static class KeyCallbackAdjustableFeature implements IHotkeyCallback
    {
        private final IConfigBoolean config;

        private static IHotkeyCallback createCallback(IConfigBoolean config)
        {
            return new KeyCallbackAdjustable(config, new KeyCallbackAdjustableFeature(config));
        }

        private KeyCallbackAdjustableFeature(IConfigBoolean config)
        {
            this.config = config;
        }

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            this.config.toggleBooleanValue();

            boolean enabled = this.config.getBooleanValue();
            String strStatus = StringUtils.translate("tweakeroo.message.value." + (enabled ? "on" : "off"));
            String preGreen = GuiBase.TXT_GREEN;
            String preRed = GuiBase.TXT_RED;
            String rst = GuiBase.TXT_RST;
            String prettyName = this.config.getPrettyName();
            strStatus = (enabled ? preGreen : preRed) + strStatus + rst;

            if (key == FeatureToggle.TWEAK_AFTER_CLICKER.getKeybind())
            {
                if (enabled)
                {
                    String strValue = Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getStringValue();
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled_after_clicker_on", strStatus, preGreen + strValue + rst);
                }
                else
                {
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_FLY_SPEED.getKeybind())
            {
                if (enabled)
                {
                    String strPreset = preGreen + (Configs.Internal.FLY_SPEED_PRESET.getIntegerValue() + 1) + rst;
                    String strSpeed = String.format("%s%.3f%s", preGreen, Configs.getActiveFlySpeedConfig().getDoubleValue(), rst);
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled_fly_speed_on", strStatus, strPreset, strSpeed);
                }
                else
                {
                    PlayerEntity player = MinecraftClient.getInstance().player;

                    if (player != null)
                    {
                        player.getAbilities().setFlySpeed(0.05f);
                    }

                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_PLACEMENT_LIMIT.getKeybind())
            {
                if (enabled)
                {
                    String strValue = Configs.Generic.PLACEMENT_LIMIT.getStringValue();
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled_placement_limit_on", strStatus, preGreen + strValue + rst);
                }
                else
                {
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getKeybind())
            {
                if (enabled)
                {
                    String strValue = Configs.Generic.HOTBAR_SLOT_CYCLE_MAX.getStringValue();
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled_slot_cycle_on", strStatus, preGreen + strValue + rst);
                }
                else
                {
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER.getKeybind())
            {
                if (enabled)
                {
                    String strValue = Configs.Generic.HOTBAR_SLOT_RANDOMIZER_MAX.getStringValue();
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled_slot_randomizer_on", strStatus, preGreen + strValue + rst);
                }
                else
                {
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_PLACEMENT_GRID.getKeybind())
            {
                if (enabled)
                {
                    String strValue = Configs.Generic.PLACEMENT_GRID_SIZE.getStringValue();
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled_placement_grid_on", strStatus, preGreen + strValue + rst);
                }
                else
                {
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_BREAKING_GRID.getKeybind())
            {
                if (enabled)
                {
                    String strValue = Configs.Generic.BREAKING_GRID_SIZE.getStringValue();
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled_breaking_grid_on", strStatus, preGreen + strValue + rst);
                }
                else
                {
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_SNAP_AIM.getKeybind())
            {
                if (enabled)
                {
                    SnapAimMode mode = (SnapAimMode) Configs.Generic.SNAP_AIM_MODE.getOptionListValue();

                    if (mode == SnapAimMode.YAW)
                    {
                        String yaw = String.valueOf(Configs.Generic.SNAP_AIM_YAW_STEP.getDoubleValue());
                        InfoUtils.printActionbarMessage("tweakeroo.message.toggled_snap_aim_on_yaw", strStatus, preGreen + yaw + rst);
                    }
                    else if (mode == SnapAimMode.PITCH)
                    {
                        String pitch = String.valueOf(Configs.Generic.SNAP_AIM_PITCH_STEP.getDoubleValue());
                        InfoUtils.printActionbarMessage("tweakeroo.message.toggled_snap_aim_on_pitch", strStatus, preGreen + pitch + rst);
                    }
                    else
                    {
                        String yaw = String.valueOf(Configs.Generic.SNAP_AIM_YAW_STEP.getDoubleValue());
                        String pitch = String.valueOf(Configs.Generic.SNAP_AIM_PITCH_STEP.getDoubleValue());
                        InfoUtils.printActionbarMessage("tweakeroo.message.toggled_snap_aim_on_both", strStatus, preGreen + yaw + rst, preGreen + pitch + rst);
                    }
                }
                else
                {
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_ZOOM.getKeybind())
            {
                if (enabled)
                {
                    String strValue = String.format("%s%.1f%s", preGreen, Configs.Generic.ZOOM_FOV.getDoubleValue(), rst);
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled_zoom_on", strStatus, strValue);
                }
                else
                {
                    InfoUtils.printActionbarMessage("tweakeroo.message.toggled", prettyName, strStatus);
                }
            }

            return true;
        }
    }
}
