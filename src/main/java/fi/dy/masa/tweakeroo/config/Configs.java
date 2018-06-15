package fi.dy.masa.tweakeroo.config;

import java.io.File;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mumfrey.liteloader.core.LiteLoader;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.PlacementTweaks;
import fi.dy.masa.tweakeroo.util.PlacementTweaks.FastMode;

public class Configs
{
    private static final String CONFIG_FILE_NAME = Reference.MOD_ID + ".json";

    public static class Generic
    {
        public static final ConfigInteger       AFTER_CLICKER_CLICK_COUNT           = new ConfigInteger     ("afterClickerClickCount",  1, 1, 32, "The number of right clicks to do per placed block when\ntweakAfterClicker is enabled");
        public static final ConfigInteger       FAST_LEFT_CLICK_COUNT               = new ConfigInteger     ("fastLeftClickCount",  10, 1, 64, "The number of left clicks to do per game tick when\ntweakFastLeftClick is enabled and the attack button is held down");
        public static final ConfigInteger       FAST_RIGHT_CLICK_COUNT              = new ConfigInteger     ("fastRightClickCount", 10, 1, 64, "The number of right clicks to do per game tick when\ntweakFastRightClick is enabled and the use button is held down");
        public static final ConfigOptionList    FAST_PLACEMENT_MODE                 = new ConfigOptionList  ("fastPlacementMode", FastMode.FACE, "The Fast Block Placement mode to use (hotkey-selectable)");
        public static final ConfigColor         FLEXIBLE_PLACEMENT_OVERLAY_COLOR    = new ConfigColor       ("flexibleBlockPlacementOverlayColor", "#C03030F0", "The color of the currently pointed-at\nregion in block placement the overlay");
        public static final ConfigInteger       GAMMA_OVERRIDE_VALUE                = new ConfigInteger     ("gammaOverrideValue", 1000, 0, 1000, "The gamma value to use when the override option is enabled");
        public static final ConfigOptionList    HOTBAR_SWAP_OVERLAY_ALIGNMENT       = new ConfigOptionList  ("hotbarSwapOverlayAlignment", HudAlignment.BOTTOM_RIGHT, "The positioning of the hotbar swap overlay");
        public static final ConfigInteger       HOTBAR_SWAP_OVERLAY_OFFSET_X        = new ConfigInteger     ("hotbarSwapOverlayOffsetX", 4, "The horizontal offset of the hotbar swap overlay");
        public static final ConfigInteger       HOTBAR_SWAP_OVERLAY_OFFSET_Y        = new ConfigInteger     ("hotbarSwapOverlayOffsetY", 4, "The vertical offset of the hotbar swap overlay");
        public static final ConfigInteger       ITEM_SWAP_DURABILITY_THRESHOLD      = new ConfigInteger     ("itemSwapDurabilityThreshold", 20, 0, 10000, "This is the durability threshold (in uses left) for the low-durability item swap feature.\nNote that items with low total durability will go lower and be swapped at 5%% left.");
        public static final ConfigBoolean       LAVA_VISIBILITY_OPTIFINE            = new ConfigBoolean     ("lavaVisibilityOptifineCompat", true, "Use an alternative version of the Lava Visibility,\nwhich is Optifine compatible (but more hacky).\nImplementation credit to Nessie.");
        public static final ConfigBoolean       PERMANENT_SNEAK_ALLOW_IN_GUIS       = new ConfigBoolean     ("permanentSneakAllowInGUIs", false, "If true, then the permanent sneak tweak will also work while GUIs are open");
        public static final ConfigBoolean       SLOT_SYNC_WORKAROUND                = new ConfigBoolean     ("slotSyncWorkaround", true, "This prevents the server from overriding the durability or stack size on items\nthat are being used quickly for example with the fast right click tweak");

        public static final ImmutableList<IConfigValue> OPTIONS = ImmutableList.of(
                AFTER_CLICKER_CLICK_COUNT,
                FAST_LEFT_CLICK_COUNT,
                FAST_RIGHT_CLICK_COUNT,
                FAST_PLACEMENT_MODE,
                FLEXIBLE_PLACEMENT_OVERLAY_COLOR,
                GAMMA_OVERRIDE_VALUE,
                HOTBAR_SWAP_OVERLAY_ALIGNMENT,
                HOTBAR_SWAP_OVERLAY_OFFSET_X,
                HOTBAR_SWAP_OVERLAY_OFFSET_Y,
                ITEM_SWAP_DURABILITY_THRESHOLD,
                LAVA_VISIBILITY_OPTIFINE,
                PERMANENT_SNEAK_ALLOW_IN_GUIS,
                SLOT_SYNC_WORKAROUND
                );
    }

    public static void load()
    {
        File configFile = new File(LiteLoader.getCommonConfigFolder(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead())
        {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject())
            {
                JsonObject root = element.getAsJsonObject();
                JsonObject objTweakToggles      = JsonUtils.getNestedObject(root, "TweakToggles", false);
                JsonObject objTweakHotkeys      = JsonUtils.getNestedObject(root, "TweakHotkeys", false);

                ConfigUtils.readConfigValues(root, "Generic", Configs.Generic.OPTIONS);
                ConfigUtils.readConfigBase(root, "GenericHotkeys", ImmutableList.copyOf(Hotkeys.values()));

                for (FeatureToggle toggle : FeatureToggle.values())
                {
                    if (objTweakToggles != null && JsonUtils.hasBoolean(objTweakToggles, toggle.getName()))
                    {
                        toggle.setBooleanValue(JsonUtils.getBoolean(objTweakToggles, toggle.getName()));
                    }

                    if (objTweakHotkeys != null && JsonUtils.hasString(objTweakHotkeys, toggle.getName()))
                    {
                        toggle.getKeybind().setValueFromString(JsonUtils.getString(objTweakHotkeys, toggle.getName()));
                    }
                }
            }
        }

        PlacementTweaks.setFastPlacementModeFromConfigs();
        InventoryUtils.setUnstackingItems(ImmutableList.of("minecraft:bucket", "minecraft:glass_bottle")); // TODO add a string list config
    }

    public static void save()
    {
        File dir = LiteLoader.getCommonConfigFolder();

        if (dir.exists() && dir.isDirectory())
        {
            JsonObject root = new JsonObject();

            JsonObject objTweakToggles      = JsonUtils.getNestedObject(root, "TweakToggles", true);
            JsonObject objTweakHotkeys      = JsonUtils.getNestedObject(root, "TweakHotkeys", true);

            ConfigUtils.writeConfigValues(root, "Generic", Configs.Generic.OPTIONS);
            ConfigUtils.writeConfigBase(root, "GenericHotkeys", ImmutableList.copyOf(Hotkeys.values()));

            for (FeatureToggle toggle : FeatureToggle.values())
            {
                objTweakToggles.add(toggle.getName(), new JsonPrimitive(toggle.getBooleanValue()));
                objTweakHotkeys.add(toggle.getName(), new JsonPrimitive(toggle.getKeybind().getStringValue()));
            }

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }
}
