package fi.dy.masa.tweakeroo.event;

import java.util.HashSet;
import java.util.Set;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.tweakeroo.config.ConfigsGeneric;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.config.IHotkeyCallback;
import fi.dy.masa.tweakeroo.config.KeybindMulti.KeyAction;
import fi.dy.masa.tweakeroo.config.interfaces.IKeybind;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.PlacementTweaks;
import fi.dy.masa.tweakeroo.util.PlacementTweaks.FastMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class InputEventHandler
{
    private static final InputEventHandler INSTANCE = new InputEventHandler();
    private static final Set<Integer> TWEAK_TOGGLES_USED_KEYS = new HashSet<>();
    private static final Set<Integer> GENERIC_HOTKEYS_USED_KEYS = new HashSet<>();

    private InputEventHandler()
    {
        Minecraft mc = Minecraft.getMinecraft();
        FeatureToggle.TWEAK_GAMMA_OVERRIDE.getKeybind().setCallback(new KeyCallbackGamma(mc));

        IHotkeyCallback callback = new KeyCallbackHotkeys(mc);
        Hotkeys.FAST_MODE_PLANE.getKeybind().setCallback(callback);
        Hotkeys.FAST_MODE_FACE.getKeybind().setCallback(callback);
        Hotkeys.FAST_MODE_COLUMN.getKeybind().setCallback(callback);
        Hotkeys.HOTBAR_SWAP_1.getKeybind().setCallback(callback);
        Hotkeys.HOTBAR_SWAP_2.getKeybind().setCallback(callback);
        Hotkeys.HOTBAR_SWAP_3.getKeybind().setCallback(callback);

        FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT.getKeybind().setCallback(new KeyCallbackToggleFastMode(FeatureToggle.TWEAK_FAST_BLOCK_PLACEMENT, mc));
    }

    public static InputEventHandler getInstance()
    {
        return INSTANCE;
    }

    public static void updateUsedKeys()
    {
        TWEAK_TOGGLES_USED_KEYS.clear();
        GENERIC_HOTKEYS_USED_KEYS.clear();

        for (FeatureToggle toggle : FeatureToggle.values())
        {
            TWEAK_TOGGLES_USED_KEYS.addAll(toggle.getKeybind().getKeys());
        }

        for (Hotkeys hotkey : Hotkeys.values())
        {
            GENERIC_HOTKEYS_USED_KEYS.addAll(hotkey.getKeybind().getKeys());
        }
    }

    public void onKeyInput()
    {
        Minecraft mc = Minecraft.getMinecraft();

        // Not in a GUI
        if (mc.currentScreen == null)
        {
            int eventKey = Keyboard.getEventKey();

            if (TWEAK_TOGGLES_USED_KEYS.contains(eventKey))
            {
                for (FeatureToggle toggle : FeatureToggle.values())
                {
                    // Note: isPressed() has to get called for key releases too, to reset the state
                    toggle.getKeybind().isPressed();
                }
            }

            if (GENERIC_HOTKEYS_USED_KEYS.contains(eventKey))
            {
                for (Hotkeys hotkey : Hotkeys.values())
                {
                    // Note: isPressed() has to get called for key releases too, to reset the state
                    hotkey.getKeybind().isPressed();
                }
            }
        }
    }

    public static void onTick()
    {
        for (FeatureToggle toggle : FeatureToggle.values())
        {
            toggle.getKeybind().tick();
        }

        for (Hotkeys hotkey : Hotkeys.values())
        {
            hotkey.getKeybind().tick();
        }
    }

    public static void printMessage(Minecraft mc, String key, Object... args)
    {
        mc.ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentTranslation(key, args));
    }

    private static class KeyCallbackGamma implements IHotkeyCallback
    {
        private final Minecraft mc;
        private float originalGamma;

        public KeyCallbackGamma(Minecraft mc)
        {
            this.mc = mc;
            this.originalGamma = this.mc.gameSettings.gammaSetting;

            // If the feature is enabled on game launch, apply it here
            if (FeatureToggle.TWEAK_GAMMA_OVERRIDE.getBooleanValue())
            {
                this.mc.gameSettings.gammaSetting = ConfigsGeneric.GAMMA_OVERRIDE_VALUE.getIntegerValue();
            }
        }

        @Override
        public void onKeyAction(KeyAction action, IKeybind key)
        {
            if (action == KeyAction.PRESS)
            {
                // The values will be toggled after the callback (see above), thus inversed check here
                if (FeatureToggle.TWEAK_GAMMA_OVERRIDE.getBooleanValue() == false)
                {
                    this.originalGamma = this.mc.gameSettings.gammaSetting;
                    this.mc.gameSettings.gammaSetting = ConfigsGeneric.GAMMA_OVERRIDE_VALUE.getIntegerValue();
                }
                else
                {
                    this.mc.gameSettings.gammaSetting = this.originalGamma;
                }
            }
        }
    }

    private static class KeyCallbackHotkeys implements IHotkeyCallback
    {
        private final Minecraft mc;

        public KeyCallbackHotkeys(Minecraft mc)
        {
            this.mc = mc;
        }

        @Override
        public void onKeyAction(KeyAction action, IKeybind key)
        {
            if (action == KeyAction.PRESS)
            {
                if (key == Hotkeys.HOTBAR_SWAP_1.getKeybind())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(mc.player, 0);
                }
                else if (key == Hotkeys.HOTBAR_SWAP_2.getKeybind())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(mc.player, 1);
                }
                else if (key == Hotkeys.HOTBAR_SWAP_3.getKeybind())
                {
                    InventoryUtils.swapHotbarWithInventoryRow(mc.player, 2);
                }
                // The values will be toggled after the callback (see above), thus inversed check here
                else if (key == Hotkeys.FAST_MODE_PLANE.getKeybind())
                {
                    PlacementTweaks.setFastPlacementMode(FastMode.PLANE);
                }
                else if (key == Hotkeys.FAST_MODE_FACE.getKeybind())
                {
                    PlacementTweaks.setFastPlacementMode(FastMode.FACE);
                }
                else if (key == Hotkeys.FAST_MODE_COLUMN.getKeybind())
                {
                    PlacementTweaks.setFastPlacementMode(FastMode.COLUMN);
                }
            }
        }
    }

    private static class KeyCallbackToggleFastMode implements IHotkeyCallback
    {
        private final FeatureToggle feature;
        private final Minecraft mc;

        private KeyCallbackToggleFastMode(FeatureToggle feature, Minecraft mc)
        {
            this.feature = feature;
            this.mc = mc;
        }

        @Override
        public void onKeyAction(KeyAction action, IKeybind key)
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
                    String strMode = PlacementTweaks.getFastPlacementMode().name();
                    printMessage(this.mc, "tweakeroo.message.toggled_fast_placement_mode_on", strStatus, preGreen + strMode + rst);
                }
                else
                {
                    printMessage(this.mc, "tweakeroo.message.toggled", this.feature.getToggleMessage(), strStatus);
                }
            }
        }
    }
}
