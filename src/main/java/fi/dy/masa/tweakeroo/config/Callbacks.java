package fi.dy.masa.tweakeroo.config;

import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.IConfigValueChangeCallback;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.PlacementRestrictionMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class Callbacks
{
    public static final FeatureCallbackGamma FEATURE_CALLBACK_GAMMA = new FeatureCallbackGamma(FeatureToggle.TWEAK_GAMMA_OVERRIDE, Minecraft.getMinecraft());

    public static boolean skipWorldRendering;

    public static void init(Minecraft mc)
    {
        FeatureToggle.TWEAK_GAMMA_OVERRIDE.setValueChangeCallback(FEATURE_CALLBACK_GAMMA);

        FeatureCallbackSpecial featureCallback = new FeatureCallbackSpecial();
        FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.setValueChangeCallback(featureCallback);
        FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.setValueChangeCallback(featureCallback);

        IHotkeyCallback callbackPress = new KeyCallbackHotkeysPress(mc);
        IHotkeyCallback callbackRelease = new KeyCallbackHotkeysRelease(mc);
        IHotkeyCallback callbackMessage = new KeyCallbackHotkeyWithMessage(mc);

        Hotkeys.RESTRICTION_MODE_PLANE.getKeybind().setCallback(callbackPress);
        Hotkeys.RESTRICTION_MODE_FACE.getKeybind().setCallback(callbackPress);
        Hotkeys.RESTRICTION_MODE_COLUMN.getKeybind().setCallback(callbackPress);
        Hotkeys.RESTRICTION_MODE_LINE.getKeybind().setCallback(callbackPress);
        Hotkeys.RESTRICTION_MODE_DIAGONAL.getKeybind().setCallback(callbackPress);
        Hotkeys.HOTBAR_SWAP_1.getKeybind().setCallback(callbackPress);
        Hotkeys.HOTBAR_SWAP_2.getKeybind().setCallback(callbackPress);
        Hotkeys.HOTBAR_SWAP_3.getKeybind().setCallback(callbackPress);
        Hotkeys.HOTBAR_SCROLL.getKeybind().setCallback(callbackRelease);

        Hotkeys.SKIP_ALL_RENDERING.getKeybind().setCallback(callbackMessage);
        Hotkeys.SKIP_WORLD_RENDERING.getKeybind().setCallback(callbackMessage);

        FeatureToggle.TWEAK_AFTER_CLICKER.getKeybind().setCallback(new KeyCallbackToggleOnRelease(FeatureToggle.TWEAK_AFTER_CLICKER));
        FeatureToggle.TWEAK_PLACEMENT_LIMIT.getKeybind().setCallback(new KeyCallbackToggleOnRelease(FeatureToggle.TWEAK_PLACEMENT_LIMIT));
        FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getKeybind().setCallback(new KeyCallbackToggleFastMode(FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT));
    }

    public static class FeatureCallbackGamma implements IConfigValueChangeCallback
    {
        private final Minecraft mc;
        private final FeatureToggle feature;
        private float originalGamma;

        public FeatureCallbackGamma(FeatureToggle feature, Minecraft mc)
        {
            this.mc = mc;
            this.feature = feature;
            this.originalGamma = this.mc.gameSettings.gammaSetting;

            // If the feature is enabled on game launch, apply it here
            if (feature.getBooleanValue())
            {
                this.mc.gameSettings.gammaSetting = Configs.Generic.GAMMA_OVERRIDE_VALUE.getIntegerValue();
            }
        }

        @Override
        public void onValueChanged(IConfigValue config)
        {
            if (this.feature.getBooleanValue())
            {
                this.originalGamma = this.mc.gameSettings.gammaSetting;
                this.mc.gameSettings.gammaSetting = Configs.Generic.GAMMA_OVERRIDE_VALUE.getIntegerValue();
            }
            else
            {
                this.restoreOriginalGamma();
            }
        }

        public void restoreOriginalGamma()
        {
            this.mc.gameSettings.gammaSetting = this.originalGamma;
        }
    }

    public static class FeatureCallbackSpecial implements IConfigValueChangeCallback
    {
        public FeatureCallbackSpecial()
        {
        }

        @Override
        public void onValueChanged(IConfigValue config)
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
            if (action == KeyAction.PRESS)
            {
                if (key == Hotkeys.SKIP_ALL_RENDERING.getKeybind())
                {
                    this.mc.skipRenderWorld = ! this.mc.skipRenderWorld;

                    String pre = mc.skipRenderWorld ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString();
                    String status = I18n.format("tweakeroo.message.value." + (this.mc.skipRenderWorld ? "on" : "off"));
                    String message = I18n.format("tweakeroo.message.toggled", "Skip All Rendering", pre + status + TextFormatting.RESET);
                    StringUtils.printActionbarMessage(message);
                }
                else if (key == Hotkeys.SKIP_WORLD_RENDERING.getKeybind())
                {
                    skipWorldRendering = ! skipWorldRendering;

                    boolean enabled = skipWorldRendering;
                    String pre = enabled ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString();
                    String status = I18n.format("tweakeroo.message.value." + (enabled ? "on" : "off"));
                    String message = I18n.format("tweakeroo.message.toggled", "Skip World Rendering", pre + status + TextFormatting.RESET);
                    StringUtils.printActionbarMessage(message);
                }

                return true;
            }

            return false;
        }
    }

    private static class KeyCallbackHotkeysPress implements IHotkeyCallback
    {
        private final Minecraft mc;

        public KeyCallbackHotkeysPress(Minecraft mc)
        {
            this.mc = mc;
        }

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            if (action == KeyAction.PRESS)
            {
                if (key == Hotkeys.HOTBAR_SWAP_1.getKeybind())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(this.mc.player, 0);
                }
                else if (key == Hotkeys.HOTBAR_SWAP_2.getKeybind())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(this.mc.player, 1);
                }
                else if (key == Hotkeys.HOTBAR_SWAP_3.getKeybind())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(this.mc.player, 2);
                }
                // The values will be toggled after the callback (see above), thus inversed check here
                else if (key == Hotkeys.RESTRICTION_MODE_PLANE.getKeybind())
                {
                    this.setPlacementRestrictionMode(PlacementRestrictionMode.PLANE);
                }
                else if (key == Hotkeys.RESTRICTION_MODE_FACE.getKeybind())
                {
                    this.setPlacementRestrictionMode(PlacementRestrictionMode.FACE);
                }
                else if (key == Hotkeys.RESTRICTION_MODE_COLUMN.getKeybind())
                {
                    this.setPlacementRestrictionMode(PlacementRestrictionMode.COLUMN);
                }
                else if (key == Hotkeys.RESTRICTION_MODE_LINE.getKeybind())
                {
                    this.setPlacementRestrictionMode(PlacementRestrictionMode.LINE);
                }
                else if (key == Hotkeys.RESTRICTION_MODE_DIAGONAL.getKeybind())
                {
                    this.setPlacementRestrictionMode(PlacementRestrictionMode.DIAGONAL);
                }

                return true;
            }

            return false;
        }

        private void setPlacementRestrictionMode(PlacementRestrictionMode mode)
        {
            Configs.Generic.PLACEMENT_RESTRICTION_MODE.setOptionListValue(mode);

            String str = TextFormatting.GREEN + mode.name() + TextFormatting.RESET;
            StringUtils.printActionbarMessage("tweakeroo.message.set_placement_restriction_mode_to", str);
        }
    }

    private static class KeyCallbackHotkeysRelease implements IHotkeyCallback
    {
        private final Minecraft mc;

        public KeyCallbackHotkeysRelease(Minecraft mc)
        {
            this.mc = mc;
        }

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            if (action == KeyAction.RELEASE)
            {
                if (key == Hotkeys.HOTBAR_SCROLL.getKeybind())
                {
                    int currentRow = Configs.Generic.HOTBAR_SCROLL_CURRENT_ROW.getIntegerValue();
                    InventoryUtils.swapHotbarWithInventoryRow(mc.player, currentRow);

                    return true;
                }
            }

            return false;
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
            if (action == KeyAction.PRESS)
            {
                this.feature.setBooleanValue(this.feature.getBooleanValue() == false);

                boolean enabled = this.feature.getBooleanValue();
                String strStatus = I18n.format("tweakeroo.message.value." + (enabled ? "on" : "off"));
                String preGreen = TextFormatting.GREEN.toString();
                String preRed = TextFormatting.RED.toString();
                String rst = TextFormatting.RESET.toString();
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

            return false;
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
            if (action == KeyAction.RELEASE)
            {
                // Don't toggle the state if the integer values were adjusted
                if (valueChanged)
                {
                    valueChanged = false;
                    return false;
                }

                this.feature.setBooleanValue(this.feature.getBooleanValue() == false);

                boolean enabled = this.feature.getBooleanValue();
                String strStatus = I18n.format("tweakeroo.message.value." + (enabled ? "on" : "off"));
                String preGreen = TextFormatting.GREEN.toString();
                String preRed = TextFormatting.RED.toString();
                String rst = TextFormatting.RESET.toString();
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
            }
            else if (action == KeyAction.PRESS)
            {
                return true;
            }

            return false;
        }
    }
}
