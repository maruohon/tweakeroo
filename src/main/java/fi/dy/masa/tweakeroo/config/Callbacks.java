package fi.dy.masa.tweakeroo.config;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.tweakeroo.gui.GuiConfigs;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.PlacementRestrictionMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.RayTraceResult;

public class Callbacks
{
    public static boolean skipWorldRendering;

    public static void init(Minecraft mc)
    {
        FeatureToggle.TWEAK_GAMMA_OVERRIDE.setValueChangeCallback(new FeatureCallbackGamma(FeatureToggle.TWEAK_GAMMA_OVERRIDE, mc));
        FeatureToggle.TWEAK_NO_SLIME_BLOCK_SLOWDOWN.setValueChangeCallback(new FeatureCallbackSlime(FeatureToggle.TWEAK_NO_SLIME_BLOCK_SLOWDOWN));

        FeatureCallbackSpecial featureCallback = new FeatureCallbackSpecial();
        FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getKeybind().setCallback(new KeyCallbackToggleFastMode(FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT));
        FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.setValueChangeCallback(featureCallback);
        FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.setValueChangeCallback(featureCallback);

        IHotkeyCallback callbackGeneric = new KeyCallbackHotkeysGeneric(mc);
        IHotkeyCallback callbackMessage = new KeyCallbackHotkeyWithMessage(mc);

        Hotkeys.FLY_PRESET_1.getKeybind().setCallback(callbackGeneric);
        Hotkeys.FLY_PRESET_2.getKeybind().setCallback(callbackGeneric);
        Hotkeys.FLY_PRESET_3.getKeybind().setCallback(callbackGeneric);
        Hotkeys.HOTBAR_SWAP_1.getKeybind().setCallback(callbackGeneric);
        Hotkeys.HOTBAR_SWAP_2.getKeybind().setCallback(callbackGeneric);
        Hotkeys.HOTBAR_SWAP_3.getKeybind().setCallback(callbackGeneric);
        Hotkeys.HOTBAR_SCROLL.getKeybind().setCallback(callbackGeneric);
        Hotkeys.OPEN_CONFIG_GUI.getKeybind().setCallback(callbackGeneric);
        Hotkeys.RESTRICTION_MODE_PLANE.getKeybind().setCallback(callbackGeneric);
        Hotkeys.RESTRICTION_MODE_FACE.getKeybind().setCallback(callbackGeneric);
        Hotkeys.RESTRICTION_MODE_COLUMN.getKeybind().setCallback(callbackGeneric);
        Hotkeys.RESTRICTION_MODE_LINE.getKeybind().setCallback(callbackGeneric);
        Hotkeys.RESTRICTION_MODE_DIAGONAL.getKeybind().setCallback(callbackGeneric);
        Hotkeys.TOOL_PICK.getKeybind().setCallback(callbackGeneric);

        Hotkeys.SKIP_ALL_RENDERING.getKeybind().setCallback(callbackMessage);
        Hotkeys.SKIP_WORLD_RENDERING.getKeybind().setCallback(callbackMessage);

        FeatureToggle.TWEAK_AFTER_CLICKER.getKeybind().setCallback(new KeyCallbackToggleOnRelease(FeatureToggle.TWEAK_AFTER_CLICKER));
        FeatureToggle.TWEAK_FLY_SPEED.getKeybind().setCallback(new KeyCallbackToggleOnRelease(FeatureToggle.TWEAK_FLY_SPEED));
        FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getKeybind().setCallback(new KeyCallbackToggleOnRelease(FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE));
        FeatureToggle.TWEAK_PLACEMENT_GRID.getKeybind().setCallback(new KeyCallbackToggleOnRelease(FeatureToggle.TWEAK_PLACEMENT_GRID));
        FeatureToggle.TWEAK_PLACEMENT_LIMIT.getKeybind().setCallback(new KeyCallbackToggleOnRelease(FeatureToggle.TWEAK_PLACEMENT_LIMIT));
        FeatureToggle.TWEAK_ZOOM.getKeybind().setCallback(new KeyCallbackToggleOnRelease(FeatureToggle.TWEAK_ZOOM));
    }

    public static class FeatureCallbackGamma implements IValueChangeCallback
    {
        private final Minecraft mc;
        private final FeatureToggle feature;

        public FeatureCallbackGamma(FeatureToggle feature, Minecraft mc)
        {
            this.mc = mc;
            this.feature = feature;

            if (this.mc.gameSettings.gammaSetting <= 1.0F)
            {
                Configs.Internal.GAMMA_VALUE_ORIGINAL.setDoubleValue(this.mc.gameSettings.gammaSetting);
            }

            // If the feature is enabled on game launch, apply it here
            if (feature.getBooleanValue())
            {
                this.mc.gameSettings.gammaSetting = Configs.Generic.GAMMA_OVERRIDE_VALUE.getIntegerValue();
            }
        }

        @Override
        public void onValueChanged(IConfigBase config)
        {
            if (this.feature.getBooleanValue())
            {
                Configs.Internal.GAMMA_VALUE_ORIGINAL.setDoubleValue(this.mc.gameSettings.gammaSetting);
                this.mc.gameSettings.gammaSetting = Configs.Generic.GAMMA_OVERRIDE_VALUE.getIntegerValue();
            }
            else
            {
                this.mc.gameSettings.gammaSetting = (float) Configs.Internal.GAMMA_VALUE_ORIGINAL.getDoubleValue();
            }
        }
    }

    public static class FeatureCallbackSlime implements IValueChangeCallback
    {
        private final FeatureToggle feature;

        public FeatureCallbackSlime(FeatureToggle feature)
        {
            this.feature = feature;
            Configs.Internal.SLIME_BLOCK_SLIPPERINESS_ORIGINAL.setDoubleValue(Blocks.SLIME_BLOCK.slipperiness);

            // If the feature is enabled on game launch, apply the overridden value here
            if (feature.getBooleanValue())
            {
                Blocks.SLIME_BLOCK.slipperiness = Blocks.STONE.slipperiness;
            }
        }

        @Override
        public void onValueChanged(IConfigBase config)
        {
            if (this.feature.getBooleanValue())
            {
                Blocks.SLIME_BLOCK.slipperiness = Blocks.STONE.slipperiness;
            }
            else
            {
                Blocks.SLIME_BLOCK.slipperiness = (float) Configs.Internal.SLIME_BLOCK_SLIPPERINESS_ORIGINAL.getDoubleValue();
            }
        }
    }

    public static class FeatureCallbackSpecial implements IValueChangeCallback
    {
        public FeatureCallbackSpecial()
        {
        }

        @Override
        public void onValueChanged(IConfigBase config)
        {
            if (Configs.Generic.PLACEMENT_RESTRICTION_TIED_TO_FAST.getBooleanValue())
            {
                if (config == FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT)
                {
                    FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.setBooleanValue(FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getBooleanValue());
                }
            }
        }
    }

    public static class KeyCallbackHotkeyWithMessage implements IHotkeyCallback
    {
        private final Minecraft mc;

        public KeyCallbackHotkeyWithMessage(Minecraft mc)
        {
            this.mc = mc;
        }

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            if (key == Hotkeys.SKIP_ALL_RENDERING.getKeybind())
            {
                this.mc.skipRenderWorld = ! this.mc.skipRenderWorld;

                String pre = mc.skipRenderWorld ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
                String status = I18n.format("tweakeroo.message.value." + (this.mc.skipRenderWorld ? "on" : "off"));
                String message = I18n.format("tweakeroo.message.toggled", "Skip All Rendering", pre + status + GuiBase.TXT_RST);
                StringUtils.printActionbarMessage(message);
            }
            else if (key == Hotkeys.SKIP_WORLD_RENDERING.getKeybind())
            {
                skipWorldRendering = ! skipWorldRendering;

                boolean enabled = skipWorldRendering;
                String pre = enabled ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
                String status = I18n.format("tweakeroo.message.value." + (enabled ? "on" : "off"));
                String message = I18n.format("tweakeroo.message.toggled", "Skip World Rendering", pre + status + GuiBase.TXT_RST);
                StringUtils.printActionbarMessage(message);
            }

            return true;
        }
    }

    private static class KeyCallbackHotkeysGeneric implements IHotkeyCallback
    {
        private final Minecraft mc;

        public KeyCallbackHotkeysGeneric(Minecraft mc)
        {
            this.mc = mc;
        }

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            if (key == Hotkeys.TOOL_PICK.getKeybind())
            {
                if (this.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    InventoryUtils.trySwitchToEffectiveTool(this.mc.objectMouseOver.getBlockPos());
                    return true;
                }
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
            else if (key == Hotkeys.HOTBAR_SCROLL.getKeybind())
            {
                if (FeatureToggle.TWEAK_HOTBAR_SCROLL.getBooleanValue())
                {
                    int currentRow = Configs.Internal.HOTBAR_SCROLL_CURRENT_ROW.getIntegerValue();
                    InventoryUtils.swapHotbarWithInventoryRow(mc.player, currentRow);
                    return true;
                }
            }
            // The values will be toggled after the callback (see above), thus inversed check here
            else if (key == Hotkeys.RESTRICTION_MODE_PLANE.getKeybind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.PLANE);
                return true;
            }
            else if (key == Hotkeys.RESTRICTION_MODE_FACE.getKeybind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.FACE);
                return true;
            }
            else if (key == Hotkeys.RESTRICTION_MODE_COLUMN.getKeybind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.COLUMN);
                return true;
            }
            else if (key == Hotkeys.RESTRICTION_MODE_LINE.getKeybind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.LINE);
                return true;
            }
            else if (key == Hotkeys.RESTRICTION_MODE_DIAGONAL.getKeybind())
            {
                this.setPlacementRestrictionMode(PlacementRestrictionMode.DIAGONAL);
                return true;
            }
            else if (key == Hotkeys.OPEN_CONFIG_GUI.getKeybind())
            {
                this.mc.displayGuiScreen(new GuiConfigs());
                return true;
            }

            return false;
        }

        private void setFlySpeedPreset(int preset)
        {
            Configs.Internal.FLY_SPEED_PRESET.setIntegerValue(preset);

            float speed = (float) Configs.getActiveFlySpeedConfig().getDoubleValue();
            String strPreset = GuiBase.TXT_GREEN + (preset + 1) + GuiBase.TXT_RST;
            String strSpeed = String.format("%s%.3f%s", GuiBase.TXT_GREEN, speed, GuiBase.TXT_RST);
            StringUtils.printActionbarMessage("tweakeroo.message.set_fly_speed_preset_to", strPreset, strSpeed);
        }

        private void setPlacementRestrictionMode(PlacementRestrictionMode mode)
        {
            Configs.Generic.PLACEMENT_RESTRICTION_MODE.setOptionListValue(mode);

            String str = GuiBase.TXT_GREEN + mode.name() + GuiBase.TXT_RST;
            StringUtils.printActionbarMessage("tweakeroo.message.set_placement_restriction_mode_to", str);
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
            this.feature.setBooleanValue(this.feature.getBooleanValue() == false);

            boolean enabled = this.feature.getBooleanValue();
            String strStatus = I18n.format("tweakeroo.message.value." + (enabled ? "on" : "off"));
            String preGreen = GuiBase.TXT_GREEN;
            String preRed = GuiBase.TXT_RED;
            String rst = GuiBase.TXT_RST;
            strStatus = (enabled ? preGreen : preRed) + strStatus + rst;

            if (enabled)
            {
                String strMode = ((PlacementRestrictionMode) Configs.Generic.PLACEMENT_RESTRICTION_MODE.getOptionListValue()).name();
                StringUtils.printActionbarMessage("tweakeroo.message.toggled_fast_placement_mode_on", strStatus, preGreen + strMode + rst);
            }
            else
            {
                StringUtils.printActionbarMessage("tweakeroo.message.toggled", this.feature.getPrettyName(), strStatus);
            }

            return true;
        }
    }

    public static class KeyCallbackToggleOnRelease implements IHotkeyCallback
    {
        private final FeatureToggle feature;
        private static boolean valueChanged;

        public static void setValueChanged()
        {
            valueChanged = true;
        }

        private KeyCallbackToggleOnRelease(FeatureToggle feature)
        {
            this.feature = feature;
        }

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            // These keybinds activate on both edges to be able to cancel further processing
            // of the press event.
            if (action == KeyAction.PRESS)
            {
                return true;
            }

            // Don't toggle the state if a value was adjusted
            if (valueChanged)
            {
                valueChanged = false;
                return true;
            }

            this.feature.setBooleanValue(this.feature.getBooleanValue() == false);

            boolean enabled = this.feature.getBooleanValue();
            String strStatus = I18n.format("tweakeroo.message.value." + (enabled ? "on" : "off"));
            String preGreen = GuiBase.TXT_GREEN;
            String preRed = GuiBase.TXT_RED;
            String rst = GuiBase.TXT_RST;
            strStatus = (enabled ? preGreen : preRed) + strStatus + rst;

            if (key == FeatureToggle.TWEAK_AFTER_CLICKER.getKeybind())
            {
                if (enabled)
                {
                    String strValue = Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getStringValue();
                    StringUtils.printActionbarMessage("tweakeroo.message.toggled_after_clicker_on", strStatus, preGreen + strValue + rst);
                }
                else
                {
                    StringUtils.printActionbarMessage("tweakeroo.message.toggled", this.feature.getPrettyName(), strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_FLY_SPEED.getKeybind())
            {
                if (enabled)
                {
                    String strPreset = preGreen + (Configs.Internal.FLY_SPEED_PRESET.getIntegerValue() + 1) + rst;
                    String strSpeed = String.format("%s%.3f%s", preGreen, Configs.getActiveFlySpeedConfig().getDoubleValue(), rst);
                    StringUtils.printActionbarMessage("tweakeroo.message.toggled_fly_speed_on", strStatus, strPreset, strSpeed);
                }
                else
                {
                    StringUtils.printActionbarMessage("tweakeroo.message.toggled", this.feature.getPrettyName(), strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_PLACEMENT_LIMIT.getKeybind())
            {
                if (enabled)
                {
                    String strValue = Configs.Generic.PLACEMENT_LIMIT.getStringValue();
                    StringUtils.printActionbarMessage("tweakeroo.message.toggled_placement_limit_on", strStatus, preGreen + strValue + rst);
                }
                else
                {
                    StringUtils.printActionbarMessage("tweakeroo.message.toggled", this.feature.getPrettyName(), strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getKeybind())
            {
                if (enabled)
                {
                    String strValue = Configs.Generic.HOTBAR_SLOT_CYCLE_MAX.getStringValue();
                    StringUtils.printActionbarMessage("tweakeroo.message.toggled_slot_cycle_on", strStatus, preGreen + strValue + rst);
                }
                else
                {
                    StringUtils.printActionbarMessage("tweakeroo.message.toggled", this.feature.getPrettyName(), strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_PLACEMENT_GRID.getKeybind())
            {
                if (enabled)
                {
                    String strValue = Configs.Generic.PLACEMENT_GRID_SIZE.getStringValue();
                    StringUtils.printActionbarMessage("tweakeroo.message.toggled_placement_grid_on", strStatus, preGreen + strValue + rst);
                }
                else
                {
                    StringUtils.printActionbarMessage("tweakeroo.message.toggled", this.feature.getPrettyName(), strStatus);
                }
            }
            else if (key == FeatureToggle.TWEAK_ZOOM.getKeybind())
            {
                if (enabled)
                {
                    String strValue = String.format("%s%.1f%s", preGreen, Configs.Generic.ZOOM_FOV.getDoubleValue(), rst);
                    StringUtils.printActionbarMessage("tweakeroo.message.toggled_zoom_on", strStatus, strValue);
                }
                else
                {
                    StringUtils.printActionbarMessage("tweakeroo.message.toggled", this.feature.getPrettyName(), strStatus);
                }
            }

            return true;
        }
    }
}
