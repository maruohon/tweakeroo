package fi.dy.masa.tweakeroo.config;

import java.io.File;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.config.options.ConfigStringList;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.ListType;
import fi.dy.masa.tweakeroo.util.PlacementRestrictionMode;

public class Configs implements IConfigHandler
{
    private static final String CONFIG_FILE_NAME = Reference.MOD_ID + ".json";

    public static class Generic
    {
        public static final ConfigInteger       AFTER_CLICKER_CLICK_COUNT           = new ConfigInteger     ("afterClickerClickCount",  1, 1, 32, "The number of right clicks to do per placed block when\ntweakAfterClicker is enabled");
        public static final ConfigInteger       FAST_BLOCK_PLACEMENT_COUNT          = new ConfigInteger     ("fastBlockPlacementCount", 2, 1, 16, "The maximum number of blocks to place per game tick\nwith the Fast Block Placement tweak");
        public static final ConfigInteger       FAST_LEFT_CLICK_COUNT               = new ConfigInteger     ("fastLeftClickCount",  2, 1, 64, "The number of left clicks to do per game tick when\ntweakFastLeftClick is enabled and the attack button is held down");
        public static final ConfigInteger       FAST_RIGHT_CLICK_COUNT              = new ConfigInteger     ("fastRightClickCount", 2, 1, 64, "The number of right clicks to do per game tick when\ntweakFastRightClick is enabled and the use button is held down");
        public static final ConfigInteger       FILL_CLONE_LIMIT                    = new ConfigInteger     ("fillCloneLimit", 1000000, 1, 1000000000, "The new /fill and /clone block limit in single player,\nif the tweak to override them is enabled");
        public static final ConfigColor         FLEXIBLE_PLACEMENT_OVERLAY_COLOR    = new ConfigColor       ("flexibleBlockPlacementOverlayColor", "#C03030F0", "The color of the currently pointed-at\nregion in block placement the overlay");
        public static final ConfigInteger       GAMMA_OVERRIDE_VALUE                = new ConfigInteger     ("gammaOverrideValue", 16, 0, 1000, "The gamma value to use when the override option is enabled");
        public static final ConfigInteger       HOTBAR_SLOT_CYCLE_MAX               = new ConfigInteger     ("hotbarSlotCycleMax", 2, 1, 9, "This is the last hotbar slot to use/cycle through\nif the hotbar slot cycle tweak is enabled.\nBasically the cycle will jump back to the first slot\nwhen going over the maximum slot number set here.");
        public static final ConfigOptionList    HOTBAR_SWAP_OVERLAY_ALIGNMENT       = new ConfigOptionList  ("hotbarSwapOverlayAlignment", HudAlignment.BOTTOM_RIGHT, "The positioning of the hotbar swap overlay");
        public static final ConfigInteger       HOTBAR_SWAP_OVERLAY_OFFSET_X        = new ConfigInteger     ("hotbarSwapOverlayOffsetX", 4, "The horizontal offset of the hotbar swap overlay");
        public static final ConfigInteger       HOTBAR_SWAP_OVERLAY_OFFSET_Y        = new ConfigInteger     ("hotbarSwapOverlayOffsetY", 4, "The vertical offset of the hotbar swap overlay");
        public static final ConfigInteger       ITEM_SWAP_DURABILITY_THRESHOLD      = new ConfigInteger     ("itemSwapDurabilityThreshold", 20, 0, 10000, "This is the durability threshold (in uses left)\nfor the low-durability item swap feature.\nNote that items with low total durability will go lower\nand be swapped at 5%% left.");
        public static final ConfigBoolean       LAVA_VISIBILITY_OPTIFINE            = new ConfigBoolean     ("lavaVisibilityOptifineCompat", true, "Use an alternative version of the Lava Visibility,\nwhich is Optifine compatible (but more hacky).\nImplementation credit to Nessie.");
        public static final ConfigInteger       MAP_PREVIEW_SIZE                    = new ConfigInteger     ("mapPreviewSize", 160, 16, 512, "The size of the rendered map previews");
        public static final ConfigBoolean       PERMANENT_SNEAK_ALLOW_IN_GUIS       = new ConfigBoolean     ("permanentSneakAllowInGUIs", false, "If true, then the permanent sneak tweak will\nalso work while GUIs are open");
        public static final ConfigInteger       PLACEMENT_GRID_SIZE                 = new ConfigInteger     ("placementGridSize", 3, 1, 1000, "The grid interval size for the grid placement mode.\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind.");
        public static final ConfigInteger       PLACEMENT_LIMIT                     = new ConfigInteger     ("placementLimit", 3, 1, 10000, "The number of blocks you are able to place at maximum per\nright click, if tweakPlacementLimit is enabled.\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind.");
        public static final ConfigOptionList    PLACEMENT_RESTRICTION_MODE          = new ConfigOptionList  ("placementRestrictionMode", PlacementRestrictionMode.FACE, "The Placement Restriction mode to use (hotkey-selectable)");
        public static final ConfigBoolean       PLACEMENT_RESTRICTION_TIED_TO_FAST  = new ConfigBoolean     ("placementRestrictionTiedToFast", true, "When enabled, the Placement Restriction mode will toggle\nits state of/off when you toggle the Fast Placement mode.");
        public static final ConfigInteger       POTION_WARNING_THRESHOLD            = new ConfigInteger     ("potionWarningThreshold", 600, 1, 1000000, "The remaining duration of potion effects (in ticks)\nafter which the warning will start showing");
        public static final ConfigInteger       RENDER_LIMIT_ITEM                   = new ConfigInteger     ("renderLimitItem", -1, -1, 10000, "Maximum number of item entities rendered per frame.\nUse -1 for normal behaviour, ie. to disable this limit.");
        public static final ConfigInteger       RENDER_LIMIT_XP_ORB                 = new ConfigInteger     ("renderLimitXPOrb", -1, -1, 10000, "Maximum number of XP orb entities rendered per frame.\nUse -1 for normal behaviour, ie. to disable this limit.");
        public static final ConfigBoolean       SHULKER_DISPLAY_BACKGROUND_COLOR    = new ConfigBoolean     ("shulkerDisplayBgColor", true, "Enables tinting/coloring the Shulker Box display\nbackground texture with the dye color of the box");
        public static final ConfigBoolean       SLOT_SYNC_WORKAROUND                = new ConfigBoolean     ("slotSyncWorkaround", true, "This prevents the server from overriding the durability or\nstack size on items that are being used quickly for example\nwith the fast right click tweak.");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                LAVA_VISIBILITY_OPTIFINE,
                PERMANENT_SNEAK_ALLOW_IN_GUIS,
                PLACEMENT_RESTRICTION_TIED_TO_FAST,
                SHULKER_DISPLAY_BACKGROUND_COLOR,
                SLOT_SYNC_WORKAROUND,

                PLACEMENT_RESTRICTION_MODE,
                HOTBAR_SWAP_OVERLAY_ALIGNMENT,

                FLEXIBLE_PLACEMENT_OVERLAY_COLOR,

                AFTER_CLICKER_CLICK_COUNT,
                FAST_BLOCK_PLACEMENT_COUNT,
                FAST_LEFT_CLICK_COUNT,
                FAST_RIGHT_CLICK_COUNT,
                FILL_CLONE_LIMIT,
                GAMMA_OVERRIDE_VALUE,
                HOTBAR_SLOT_CYCLE_MAX,
                HOTBAR_SWAP_OVERLAY_OFFSET_X,
                HOTBAR_SWAP_OVERLAY_OFFSET_Y,
                ITEM_SWAP_DURABILITY_THRESHOLD,
                MAP_PREVIEW_SIZE,
                PLACEMENT_GRID_SIZE,
                PLACEMENT_LIMIT,
                POTION_WARNING_THRESHOLD,
                RENDER_LIMIT_ITEM,
                RENDER_LIMIT_XP_ORB
        );
    }

    public static class Fixes
    {
        public static final ConfigBoolean CLIENT_CHUNK_ENTITY_DUPE          = new ConfigBoolean("clientChunkEntityDupeFix", false, "Fixes the client world chunks essentially duping their\nentities when chunk packets are received");
        public static final ConfigBoolean ELYTRA_FIX                        = new ConfigBoolean("elytraFix", false, "Elytra deployment/landing fix by Earthcomputer and Nessie");
        public static final ConfigBoolean RAVAGER_CLIENT_BLOCK_BREAK_FIX    = new ConfigBoolean("ravagerClientBlockBreakFix", false, "Fixes Ravagers breaking blocks on the client side,\nwhich causes annoying ghost blocks/block desyncs");
        public static final ConfigBoolean TILE_UNLOAD_OPTIMIZATION          = new ConfigBoolean("tileEntityUnloadOptimization", false, "Optimizes the removal of unloading TileEntities from the World lists.\nThis can greatly improve performance if there are lots of\nTileEntities loaded and/or unloading at once.");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                CLIENT_CHUNK_ENTITY_DUPE,
                ELYTRA_FIX,
                RAVAGER_CLIENT_BLOCK_BREAK_FIX,
                TILE_UNLOAD_OPTIMIZATION
        );
    }

    public static class Lists
    {
        public static final ConfigOptionList FAST_RIGHT_CLICK_LIST_TYPE    = new ConfigOptionList("fastRightClickListType", ListType.NONE, "The item restriction type for the Fast Right Click tweak");
        public static final ConfigStringList FAST_RIGHT_CLICK_BLACKLIST    = new ConfigStringList("fastRightClickBlackList", ImmutableList.of("minecraft:firework_rocket"), "The items that are NOT allowed to be used for the Fast Right Click tweak,\nif the fastRightClickListType is set to Black List");
        public static final ConfigStringList FAST_RIGHT_CLICK_WHITELIST    = new ConfigStringList("fastRightClickWhiteList", ImmutableList.of("minecraft:bucket", "minecraft:water_bucket", "minecraft:lava_bucket", "minecraft:glass_bottle"), "The items that are allowed to be used for the Fast Right Click tweak,\nif the fastRightClickListType is set to White List");
        public static final ConfigStringList UNSTACKING_ITEMS              = new ConfigStringList("unstackingItems", ImmutableList.of("minecraft:bucket", "minecraft:glass_bottle"), "The items that should be considered for the\n'tweakItemUnstackingProtection' tweak");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                FAST_RIGHT_CLICK_LIST_TYPE,
                FAST_RIGHT_CLICK_BLACKLIST,
                FAST_RIGHT_CLICK_WHITELIST,
                UNSTACKING_ITEMS
        );
    }

    public static class Internal
    {
        public static final ConfigDouble        GAMMA_VALUE_ORIGINAL                = new ConfigDouble      ("gammaValueOriginal", 0, 0, 1, "The original gamma value, before the gamma override was enabled");
        public static final ConfigInteger       HOTBAR_SCROLL_CURRENT_ROW           = new ConfigInteger     ("hotbarScrollCurrentRow", 3, 0, 3, "This is just for the mod internally to track the\n\"current hotbar row\" for the hotbar scrolling feature");
        public static final ConfigDouble        SLIME_BLOCK_SLIPPERINESS_ORIGINAL   = new ConfigDouble      ("slimeBlockSlipperinessOriginal", 0.8, 0, 1, "The original slipperiness value of Slime Blocks");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                GAMMA_VALUE_ORIGINAL,
                HOTBAR_SCROLL_CURRENT_ROW,
                SLIME_BLOCK_SLIPPERINESS_ORIGINAL
        );
    }

    public static void loadFromFile()
    {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead())
        {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject())
            {
                JsonObject root = element.getAsJsonObject();

                ConfigUtils.readConfigBase(root, "Generic", Configs.Generic.OPTIONS);
                ConfigUtils.readConfigBase(root, "Fixes", Configs.Fixes.OPTIONS);
                ConfigUtils.readConfigBase(root, "Lists", Configs.Lists.OPTIONS);
                ConfigUtils.readConfigBase(root, "Internal", Configs.Internal.OPTIONS);
                ConfigUtils.readHotkeys(root, "GenericHotkeys", Hotkeys.HOTKEY_LIST);
                ConfigUtils.readHotkeyToggleOptions(root, "TweakHotkeys", "TweakToggles", ImmutableList.copyOf(FeatureToggle.values()));
            }
        }

        InventoryUtils.setUnstackingItems(Lists.UNSTACKING_ITEMS.getStrings());
        PlacementTweaks.FAST_RIGHT_CLICK_RESTRICTION.setValues(
                (ListType) Lists.FAST_RIGHT_CLICK_LIST_TYPE.getOptionListValue(),
                Lists.FAST_RIGHT_CLICK_BLACKLIST.getStrings(),
                Lists.FAST_RIGHT_CLICK_WHITELIST.getStrings());
    }

    public static void saveToFile()
    {
        File dir = FileUtils.getConfigDirectory();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs())
        {
            JsonObject root = new JsonObject();

            ConfigUtils.writeConfigBase(root, "Generic", Configs.Generic.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Fixes", Configs.Fixes.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Lists", Configs.Lists.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Internal", Configs.Internal.OPTIONS);
            ConfigUtils.writeHotkeys(root, "GenericHotkeys", Hotkeys.HOTKEY_LIST);
            ConfigUtils.writeHotkeyToggleOptions(root, "TweakHotkeys", "TweakToggles", ImmutableList.copyOf(FeatureToggle.values()));

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    @Override
    public void onConfigsChanged()
    {
        saveToFile();
        loadFromFile();
    }

    @Override
    public void load()
    {
        loadFromFile();
    }

    @Override
    public void save()
    {
        saveToFile();
    }
}
