package fi.dy.masa.tweakeroo.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IConfigNotifiable;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyCallbackToggleBooleanConfigWithMessage;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.tweakeroo.Tweakeroo;

public enum FeatureToggle implements IHotkeyTogglable, IConfigNotifiable<IConfigBoolean>
{
    TWEAK_ACCURATE_BLOCK_PLACEMENT  ("tweakAccurateBlockPlacement",         false, "",    "Enables a simpler version of Flexible placement, similar to\nthe Carpet mod, so basically either facing into or out\nfrom the block face clicked on."),
    TWEAK_AFTER_CLICKER             ("tweakAfterClicker",                   false, "",    KeybindSettings.INGAME_BOTH, "Enables a \"after clicker\" tweak, which does automatic right\nclicks on the just-placed block.\nUseful for example for Repeaters (setting the delay).\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind."),
    TWEAK_AIM_LOCK                  ("tweakAimLock",                        false, "",    "Enables an aim lock, locking the yaw and pitch rotations\nto the current values.\nThis is separate from the snap aim lock,\nwhich locks them to the snapped value.\nThis allows locking them \"freely\" to the current value."),
    TWEAK_ANGEL_BLOCK               ("tweakAngelBlock",                     false, "",    "Enables an \"Angel Block\" tweak, which allows\nplacing blocks in mid-air in Creative mode"),
    TWEAK_BLOCK_REACH_OVERRIDE      ("tweakBlockReachOverride",             false, true,  "",    "Overrides the block reach distance with\nthe one set in Generic -> blockReachDistance"),
    TWEAK_BLOCK_TYPE_BREAK_RESTRICTION("tweakBlockTypeBreakRestriction",    false, "",    "Restricts which blocks you are able to break (manually).\nSee the corresponding 'blockBreakRestriction*' configs in the Lists category."),
    TWEAK_BREAKING_GRID             ("tweakBreakingGrid",                   false, "",    KeybindSettings.INGAME_BOTH, "When enabled, you can only break blocks in\na grid pattern, with a configurable interval.\nTo quickly adjust the interval, scroll while\nholding down the tweak toggle keybind."),
    TWEAK_BREAKING_RESTRICTION      ("tweakBreakingRestriction",            false, "",    "Enables the Breaking Restriction mode\n  (Plane, Layer, Face, Column, Line, Diagonal).\nBasically only allows you to break blocks\nin those patterns, while holding down the attack key."),
    TWEAK_CHAT_BACKGROUND_COLOR     ("tweakChatBackgroundColor",            false, "",    "Overrides the default chat background color\nwith the one from Generics -> 'chatBackgroundColor'"),
    TWEAK_CHAT_PERSISTENT_TEXT      ("tweakChatPersistentText",             false, "",    "Stores the text from the chat input text field\nand restores it when the chat is opened again"),
    TWEAK_CHAT_TIMESTAMP            ("tweakChatTimestamp",                  false, "",    "Adds timestamps to chat messages"),
    TWEAK_COMMAND_BLOCK_EXTRA_FIELDS("tweakCommandBlockExtraFields",        false, "",    "Adds extra fields to the Command Block GUI, for settings\nthe name of the command block, and seeing the stats results"),
    // TODO 1.19.3+
    //TWEAK_CREATIVE_EXTRA_ITEMS      ("tweakCreativeExtraItems",             false, "",    "Adds custom items to item groups.\nSee Lists -> 'creativeExtraItems' to control which items are added to the groups.\nNote: Currently these will be added to the Transportation group\n(because it has the elast items), but in the future\nthe groups will be configurable per added item"),
    // TODO/FIXME 1.19+ the mixin needs an access widener now
    //TWEAK_CUSTOM_FLAT_PRESETS       ("tweakCustomFlatPresets",              false, "",    "Allows adding custom flat world presets to the list.\nThe presets are defined in Lists -> flatWorldPresets"),
    TWEAK_CUSTOM_FLY_DECELERATION   ("tweakCustomFlyDeceleration",          false, "",    "Allows changing the fly deceleration in creative or spectator mode.\nThis is mainly meant for faster deceleration ie. less \"glide\"\nwhen releasing the movement keys.\nSee Generic -> flyDecelerationRampValue"),
    TWEAK_CUSTOM_INVENTORY_GUI_SCALE("tweakCustomInventoryScreenScale",     false, "",    "Allows using a custom GUI scale for any inventory screen.\nSee Generic -> §ecustomInventoryGuiScale§r for the scale value"),
    TWEAK_ELYTRA_CAMERA             ("tweakElytraCamera",                   false, "",    "Allows locking the real player rotations while holding the 'elytraCamera' activation key.\nThe controls will then only affect the separate 'camera rotations' for the rendering/camera.\nMeant for things like looking down/around while elytra flying nice and straight."),
    TWEAK_ENTITY_REACH_OVERRIDE      ("tweakEntityReachOverride",           false, true, "",    "Overrides the entity reach distance with\nthe one set in Generic -> entityReachDistance"),
    TWEAK_ENTITY_TYPE_ATTACK_RESTRICTION("tweakEntityTypeAttackRestriction",false, "",    "Restricts which entities you are able to attack (manually).\nSee the corresponding 'entityAttackRestriction*' configs in the Lists category."),
    TWEAK_SHULKERBOX_STACKING       ("tweakEmptyShulkerBoxesStack",         false, true, "",    "Enables empty Shulker Boxes stacking up to 64.\nNOTE: They will also stack inside inventories!\nOn servers this will cause desyncs/glitches\nunless the server has a mod that does the same.\nIn single player this changes shulker box based system behaviour."),
    TWEAK_SHULKERBOX_STACK_GROUND   ("tweakEmptyShulkerBoxesStackOnGround", false, true, "",    "Enables empty Shulker Boxes stacking up to 64\nwhen as items on the ground"),
    TWEAK_EXPLOSION_REDUCED_PARTICLES ("tweakExplosionReducedParticles",    false, "",    "If enabled, then all explosion particles will use the\nEXPLOSION_NORMAL particle instead of possibly\nthe EXPLOSION_LARGE or EXPLOSION_HUGE particles"),
    TWEAK_F3_CURSOR                 ("tweakF3Cursor",                       false, "",    "Enables always rendering the F3 screen cursor"),
    TWEAK_FAKE_SNEAKING             ("tweakFakeSneaking",                   false, "",    "Enables \"fake sneaking\" ie. prevents you from falling from edges\nwithout slowing down the movement speed"),
    TWEAK_FAKE_SNEAK_PLACEMENT      ("tweakFakeSneakPlacement",             false, "",    "This tweak offsets the click position to the adjacent air block\nfrom the block that you actually click on.\nThis basically allows you to place blocks against blocks\nthat have a click action, such as opening inventory GUIs,\nwithout having to sneak. Note that this doesn't not actually\nfake sneaking in any way, just the apparent effect is similar."),
    TWEAK_FAST_BLOCK_PLACEMENT      ("tweakFastBlockPlacement",             false, "",    "Enables fast/convenient block placement when moving\nthe cursor over new blocks"),
    TWEAK_FAST_LEFT_CLICK           ("tweakFastLeftClick",                  false, "",    "Enables automatic fast left clicking while holding down\nthe attack button (left click).\nThe number of clicks per tick is set in the Generic configs."),
    TWEAK_FAST_RIGHT_CLICK          ("tweakFastRightClick",                 false, "",    "Enables automatic fast right clicking while holding down\nthe use button (right click).\nThe number of clicks per tick is set in the Generic configs."),
    TWEAK_FILL_CLONE_LIMIT          ("tweakFillCloneLimit",                 false, true, "",    "Enables overriding the /fill and /clone command\nblock limits in single player.\nThe new limit can be set in the Generic configs,\nin the 'fillCloneLimit' config value"),
    TWEAK_FLY_SPEED                 ("tweakFlySpeed",                       false, "",    KeybindSettings.INGAME_BOTH, "Enables overriding the fly speed in creative or spectator mode\nand using some presets for it"),
    TWEAK_FLEXIBLE_BLOCK_PLACEMENT  ("tweakFlexibleBlockPlacement",         false, "",    "Enables placing blocks in different orientations\nor with an offset, while holding down the\nhotkeys for those modes."),
    TWEAK_FREE_CAMERA               ("tweakFreeCamera",                     false, "",    "Enables a free camera mode, similar to spectator mode,\nbut where the player will remain in place where\nyou first activate the free camera mode"),
    TWEAK_GAMMA_OVERRIDE            ("tweakGammaOverride",                  false, "",    "Overrides the video settings gamma value with\nthe one set in the Generic configs"),
    TWEAK_HAND_RESTOCK              ("tweakHandRestock",                    false, "",    "Enables swapping a new stack to the main or the offhand\nwhen the previous stack runs out"),
    TWEAK_HANGABLE_ENTITY_BYPASS    ("tweakHangableEntityBypass",           false, "",    "Allows not targeting hangable entities (Item Frames and Paintings).\nThe Generic -> hangableEntityBypassInverse option can be used to control\nwhether you must be sneaking or not sneaking to be able to target the entity."),
    TWEAK_HOLD_ATTACK               ("tweakHoldAttack",                     false, "",    "Emulates holding down the attack button"),
    TWEAK_HOLD_USE                  ("tweakHoldUse",                        false, "",    "Emulates holding down the use button"),
    TWEAK_HOTBAR_SCROLL             ("tweakHotbarScroll",                   false, "",    "Enables the hotbar swapping via scrolling feature"),
    TWEAK_HOTBAR_SLOT_CYCLE         ("tweakHotbarSlotCycle",                false, "",    KeybindSettings.INGAME_BOTH, "Enables cycling the selected hotbar slot after each placed\nblock, up to the set max slot number.\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind."),
    TWEAK_HOTBAR_SLOT_RANDOMIZER    ("tweakHotbarSlotRandomizer",           false, "",    KeybindSettings.INGAME_BOTH, "Enables randomizing the selected hotbar slot after each placed\nblock, up to the set max slot number.\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind."),
    TWEAK_HOTBAR_SWAP               ("tweakHotbarSwap",                     false, "",    "Enables the hotbar swapping via hotkeys feature"),
    TWEAK_INVENTORY_PREVIEW         ("tweakInventoryPreview",               false, true, "",    "Enables an inventory preview while having the cursor over\na block or an entity with an inventory and holding down\nthe configured hotkey."),
    TWEAK_ITEM_UNSTACKING_PROTECTION("tweakItemUnstackingProtection",       false, "",    "If enabled, then items configured in Lists -> unstackingItems\nwon't be allowed to spill out when using.\nThis is meant for example to prevent throwing buckets\ninto lava when filling them."),
    TWEAK_LAVA_VISIBILITY           ("tweakLavaVisibility",                 false, "",    "If enabled, then the level of Respiration and Aqua Affinity enchantments,\nand having the Fire Resistance effect active,\nwill greatly increase the visibility under lava."),
    TWEAK_MAP_PREVIEW               ("tweakMapPreview",                     false, "",    "If enabled, then holding shift over maps in an inventory\nwill render a preview of the map"),
    TWEAK_MOVEMENT_KEYS             ("tweakMovementKeysLast",               false, "",    "If enabled, then opposite movement keys won't cancel each other,\nbut instead the last pressed key is the active input."),
    TWEAK_PERIODIC_ATTACK           ("tweakPeriodicAttack",                 false, "",    "Enables periodic attacks (left clicks)\nConfigure the interval in Generic -> periodicAttackInterval"),
    TWEAK_PERIODIC_USE              ("tweakPeriodicUse",                    false, "",    "Enables periodic uses (right clicks)\nConfigure the interval in Generic -> periodicUseInterval"),
    TWEAK_PERIODIC_HOLD_ATTACK      ("tweakPeriodicHoldAttack",             false, "",    "Enables periodically holding attack for a configurable amount of time.\nConfigure the interval in Generic -> periodicHoldAttackInterval\nand the duration in periodicHoldAttackDuration\n§6Note: You should not use the normal hold attack\n§6or the periodic attack at the same time"),
    TWEAK_PERIODIC_HOLD_USE         ("tweakPeriodicHoldUse",                false, "",    "Enables periodically holding use for a configurable amount of time.\nConfigure the interval in Generic -> periodicHoldUseInterval\nand the duration in periodicHoldUseDuration\n§6Note: You should not use the normal hold use\n§6or the periodic use at the same time"),
    TWEAK_PERMANENT_SNEAK           ("tweakPermanentSneak",                 false, "",    "If enabled, the player will be sneaking the entire time"),
    TWEAK_PERMANENT_SPRINT          ("tweakPermanentSprint",                false, "",    "If enabled, the player will be always sprinting when moving forward"),
    TWEAK_PLACEMENT_GRID            ("tweakPlacementGrid",                  false, "",    KeybindSettings.INGAME_BOTH, "When enabled, you can only place blocks in\na grid pattern, with a configurable interval.\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind."),
    TWEAK_PLACEMENT_LIMIT           ("tweakPlacementLimit",                 false, "",    KeybindSettings.INGAME_BOTH, "When enabled, you can only place a set number\nof blocks per use/right click.\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind."),
    TWEAK_PLACEMENT_RESTRICTION     ("tweakPlacementRestriction",           false, "",    "Enables the Placement Restriction mode\n  (Plane, Layer, Face, Column, Line, Diagonal)"),
    TWEAK_PLACEMENT_REST_FIRST      ("tweakPlacementRestrictionFirst",      false, "",    "Restricts block placement so that you can only\nplace blocks against the same block type\nyou first clicked on"),
    TWEAK_PLACEMENT_REST_HAND       ("tweakPlacementRestrictionHand",       false, "",    "Restricts block placement so that you can only\nplace blocks against the same block type\nyou are holding in your hand"),
    TWEAK_PLAYER_INVENTORY_PEEK     ("tweakPlayerInventoryPeek",            false, "",    "Enables a player inventory peek/preview, while holding the\nconfigured hotkey key for it."),
    TWEAK_POTION_WARNING            ("tweakPotionWarning",                  false, "",    "Prints a warning message to the hotbar when\nnon-ambient potion effects are about to run out"),
    TWEAK_PRINT_DEATH_COORDINATES   ("tweakPrintDeathCoordinates",          false, "",    "Enables printing the player's coordinates to chat on death.\nThis feature is originally from usefulmod by nessie."),
    TWEAK_PICK_BEFORE_PLACE         ("tweakPickBeforePlace",                false, "",    "If enabled, then before each block placement, the same block\nis switched to hand that you are placing against"),
    TWEAK_PLAYER_LIST_ALWAYS_ON     ("tweakPlayerListAlwaysVisible",        false, "",    "If enabled, then the player list is always rendered without\nhaving to hold down the key (tab by default)"),
    TWEAK_RENDER_EDGE_CHUNKS        ("tweakRenderEdgeChunks",               false, "",    "Allows the edge-most client-loaded chunks to render.\nVanilla doesn't allow rendering chunks that don't have\nall the adjacent chunks loaded, meaning that the edge-most chunk\nof the client's loaded won't render in vanilla.\n§lThis is also very helpful in the Free Camera mode!§r"),
    TWEAK_RENDER_INVISIBLE_ENTITIES ("tweakRenderInvisibleEntities",        false, "",    "When enabled, invisible entities are rendered like\nthey would be in spectator mode."),
    TWEAK_RENDER_LIMIT_ENTITIES     ("tweakRenderLimitEntities",            false, "",    "Enables limiting the number of certain types of entities\nto render per frame. Currently XP Orbs and Item entities\nare supported, see Generic configs for the limits."),
    TWEAK_REPAIR_MODE               ("tweakRepairMode",                     false, "",    "If enabled, then fully repaired items held in hand will\nbe swapped to damaged items that have Mending on them."),
    TWEAK_SCULK_PULSE_LENGTH        ("tweakSculkPulseLength",               false, true, "",    "Allows modifying the Sculk Sensor pulse length. Set the pulse length in Generic -> sculkSensorPulseLength"),
    TWEAK_SHULKERBOX_DISPLAY        ("tweakShulkerBoxDisplay",              false, "",    "Enables the Shulker Box contents display when hovering\nover them in an inventory and holding shift"),
    TWEAK_SIGN_COPY                 ("tweakSignCopy",                       false, "",    "When enabled, placed signs will use the text from\nthe previously placed sign.\nCan be combined with tweakNoSignGui to quickly place copies\nof a sign, by enabling that tweak after making the first sign."),
    TWEAK_SNAP_AIM                  ("tweakSnapAim",                        false, "",    KeybindSettings.INGAME_BOTH, "Enabled a snap aim tweak, to make the player face to pre-set exact yaw rotations"),
    TWEAK_SNAP_AIM_LOCK             ("tweakSnapAimLock",                    false, "",    "Enables a snap aim lock, locking the yaw and/or pitch rotations\nto the currently snapped value"),
    TWEAK_SNEAK_1_15_2              ("tweakSneak_1.15.2",                   false, "",    "Restores the 1.15.2 sneaking behavior"),
    TWEAK_SPECTATOR_TELEPORT        ("tweakSpectatorTeleport",              false, "",    "Allows spectators to teleport to other spectators.\nThis is originally from usefulmod by nessie."),
    TWEAK_STRUCTURE_BLOCK_LIMIT     ("tweakStructureBlockLimit",            false, true, "",    "Allows overriding the structure block limit.\nThe new limit is set in Generic -> structureBlockMaxSize"),
    TWEAK_SWAP_ALMOST_BROKEN_TOOLS  ("tweakSwapAlmostBrokenTools",          false, "",    "If enabled, then any damageable items held in the hand that\nare about to break will be swapped to fresh ones"),
    TWEAK_TAB_COMPLETE_COORDINATE   ("tweakTabCompleteCoordinate",          false, "",    "If enabled, then tab-completing coordinates while not\nlooking at a block, will use the player's position\ninstead of adding the ~ character."),
    TWEAK_TOOL_SWITCH               ("tweakToolSwitch",                     false, "",    "Enables automatically switching to an effective tool for the targeted block"),
    TWEAK_WEAPON_SWITCH             ("tweakWeaponSwitch",                   false, "",    "Enables automatically switching to a weapon for the targeted entity"),
    TWEAK_Y_MIRROR                  ("tweakYMirror",                        false, "",    "Mirrors the targeted y-position within the block bounds.\nThis is basically for placing slabs or stairs\nin the opposite top/bottom state from normal,\nif you have to place them against another slab for example."),
    TWEAK_ZOOM                      ("tweakZoom",                           false, "",    KeybindSettings.INGAME_BOTH, "Enables using the zoom hotkey to, well, zoom in");

    public static final ImmutableList<FeatureToggle> VALUES = ImmutableList.copyOf(values());

    private final String name;
    private final String comment;
    private final String prettyName;
    private final IKeybind keybind;
    private final boolean defaultValueBoolean;
    private final boolean singlePlayer;
    private boolean valueBoolean;
    private IValueChangeCallback<IConfigBoolean> callback;

    FeatureToggle(String name, boolean defaultValue, String defaultHotkey, String comment)
    {
        this(name, defaultValue, false, defaultHotkey, KeybindSettings.DEFAULT, comment);
    }

    FeatureToggle(String name, boolean defaultValue, boolean singlePlayer, String defaultHotkey, String comment)
    {
        this(name, defaultValue, singlePlayer, defaultHotkey, KeybindSettings.DEFAULT, comment);
    }

    FeatureToggle(String name, boolean defaultValue, String defaultHotkey, KeybindSettings settings, String comment)
    {
        this(name, defaultValue, false, defaultHotkey, settings, comment);
    }

    FeatureToggle(String name, boolean defaultValue, boolean singlePlayer, String defaultHotkey, KeybindSettings settings, String comment)
    {
        this(name, defaultValue, singlePlayer, defaultHotkey, settings, comment, StringUtils.splitCamelCase(name.substring(5)));
    }

    FeatureToggle(String name, boolean defaultValue, String defaultHotkey, String comment, String prettyName)
    {
        this(name, defaultValue, false, defaultHotkey, comment, prettyName);
    }

    FeatureToggle(String name, boolean defaultValue, boolean singlePlayer, String defaultHotkey, String comment, String prettyName)
    {
        this(name, defaultValue, singlePlayer, defaultHotkey, KeybindSettings.DEFAULT, comment, prettyName);
    }

    FeatureToggle(String name, boolean defaultValue, boolean singlePlayer, String defaultHotkey, KeybindSettings settings, String comment, String prettyName)
    {
        this.name = name;
        this.valueBoolean = defaultValue;
        this.defaultValueBoolean = defaultValue;
        this.singlePlayer = singlePlayer;
        this.comment = comment;
        this.prettyName = prettyName;
        this.keybind = KeybindMulti.fromStorageString(defaultHotkey, settings);
        this.keybind.setCallback(new KeyCallbackToggleBooleanConfigWithMessage(this));
    }

    @Override
    public ConfigType getType()
    {
        return ConfigType.HOTKEY;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getConfigGuiDisplayName()
    {
        String name = StringUtils.getTranslatedOrFallback("config.name." + this.getName().toLowerCase(), this.getName());

        if (this.singlePlayer)
        {
            return GuiBase.TXT_GOLD + name + GuiBase.TXT_RST;
        }

        return name;
    }

    @Override
    public String getPrettyName()
    {
        return this.prettyName;
    }

    @Override
    public String getStringValue()
    {
        return String.valueOf(this.valueBoolean);
    }

    @Override
    public String getDefaultStringValue()
    {
        return String.valueOf(this.defaultValueBoolean);
    }

    @Override
    public void setValueFromString(String value)
    {
    }

    @Override
    public void onValueChanged()
    {
        if (this.callback != null)
        {
            this.callback.onValueChanged(this);
        }
    }

    @Override
    public void setValueChangeCallback(IValueChangeCallback<IConfigBoolean> callback)
    {
        this.callback = callback;
    }

    @Override
    public String getComment()
    {
        String comment = StringUtils.getTranslatedOrFallback("config.comment." + this.getName().toLowerCase(), this.comment);

        if (comment != null && this.singlePlayer)
        {
            return comment + "\n" + StringUtils.translate("tweakeroo.label.config_comment.single_player_only");
        }

        return comment;
    }

    @Override
    public IKeybind getKeybind()
    {
        return this.keybind;
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.valueBoolean;
    }

    @Override
    public boolean getDefaultBooleanValue()
    {
        return this.defaultValueBoolean;
    }

    @Override
    public void setBooleanValue(boolean value)
    {
        boolean oldValue = this.valueBoolean;
        this.valueBoolean = value;

        if (oldValue != this.valueBoolean)
        {
            this.onValueChanged();
        }
    }

    @Override
    public boolean isModified()
    {
        return this.valueBoolean != this.defaultValueBoolean;
    }

    @Override
    public boolean isModified(String newValue)
    {
        return Boolean.parseBoolean(newValue) != this.defaultValueBoolean;
    }

    @Override
    public void resetToDefault()
    {
        this.valueBoolean = this.defaultValueBoolean;
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.valueBoolean);
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.valueBoolean = element.getAsBoolean();
            }
            else
            {
                Tweakeroo.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
            }
        }
        catch (Exception e)
        {
            Tweakeroo.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
        }
    }
}
