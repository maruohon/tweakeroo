package fi.dy.masa.tweakeroo.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.options.IConfigBoolean;
import fi.dy.masa.malilib.config.options.IConfigNotifiable;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyCallbackToggleBooleanConfigWithMessage;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.tweakeroo.LiteModTweakeroo;

public enum FeatureToggle implements IConfigBoolean, IHotkey, IConfigNotifiable<IConfigBoolean>
{
    CARPET_ACCURATE_PLACEMENT_PROTOCOL ("carpetAccuratePlacementProtocol",  false, "",    "If enabled, then the Flexible Block Placement and the\nAccurate Block Placement use the protocol implemented\nin the recent carpet mod versions", "Carpet protocol Accurate Placement"),
    FAST_PLACEMENT_REMEMBER_ALWAYS  ("fastPlacementRememberOrientation",    true, "",     "If enabled, then the fast placement mode will always remember\nthe orientation of the first block you place.\nWithout this, the orientation will only be remembered\nwith the flexible placement enabled and active.", "Fast Placement Remember Orientation"),
    REMEMBER_FLEXIBLE               ("rememberFlexibleFromClick",           true, "",     "If enabled, then the flexible block placement status\nwill be remembered from the first placed block,\nas long as the use key is held down.", "Remember Flexible Orientation From First Click"),
    TWEAK_ACCURATE_BLOCK_PLACEMENT  ("tweakAccurateBlockPlacement",         false, "",    "Enables a simpler version of Flexible placement, similar to\nthe Carpet mod, so basically either facing into or out\nfrom the block face clicked on."),
    TWEAK_AFTER_CLICKER             ("tweakAfterClicker",                   false, "",    KeybindSettings.INGAME_BOTH, "Enables a \"after clicker\" tweak, which does automatic right\nclicks on the just-placed block.\nUseful for example for Repeaters (setting the delay).\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind."),
    TWEAK_AIM_LOCK                  ("tweakAimLock",                        false, "",    "Enables an aim lock, locking the yaw and pitch rotations\nto the current values.\nThis is separate from the snap aim lock,\nwhich locks them to the snapped value.\nThis allows locking them \"freely\" to the current value."),
    TWEAK_ANGEL_BLOCK               ("tweakAngelBlock",                     false, "",    "Enables an \"Angel Block\" tweak, which allows\nplacing blocks in mid-air in Creative mode"),
    TWEAK_BLOCK_REACH_OVERRIDE      ("tweakBlockReachOverride",             false, "",    "Overrides the block reach distance with\nthe one set in Generic -> blockReachDistance"),
    TWEAK_BREAKING_GRID             ("tweakBreakingGrid",                   false, "",    KeybindSettings.INGAME_BOTH, "When enabled, you can only break blocks in\na grid pattern, with a configurable interval.\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind."),
    TWEAK_BREAKING_RESTRICTION      ("tweakBreakingRestriction",            false, "",    "Enables the Breaking Restriction mode\n  (Plane, Layer, Face, Column, Line, Diagonal)"),
    TWEAK_CHAT_BACKGROUND_COLOR     ("tweakChatBackgroundColor",            false, "",    "Overrides the default chat background color\nwith the one from Generics -> 'chatBackgroundColor'"),
    TWEAK_CHAT_PERSISTENT_TEXT      ("tweakChatPersistentText",             false, "",    "Stores the text from the chat input text field\nand restores it when the chat is opened again"),
    TWEAK_CHAT_TIMESTAMP            ("tweakChatTimestamp",                  false, "",    "Adds timestamps to chat messages"),
    TWEAK_CLOUD_HEIGHT_OVERRIDE     ("tweakCloudHeightOverride",            false, "",    "Overrides the normal cloud height with the one set in Generic -> 'cloudHeightOverride'"),
    TWEAK_COMMAND_BLOCK_EXTRA_FIELDS("tweakCommandBlockExtraFields",        false, "",    "Adds extra fields to the Command Block GUI, for settings\nthe name of the command block, and seeing the stats results"),
    TWEAK_CUSTOM_FLAT_PRESETS       ("tweakCustomFlatPresets",              false, "",    "Allows adding custom flat world presets to the list.\nThe presets are defined in Lists -> flatWorldPresets"),
    TWEAK_ELYTRA_CAMERA             ("tweakElytraCamera",                   false, "",    "Allows locking the real player rotations while holding the 'elytraCamera' activation key.\nThe controls will then only affect the separate 'camera rotations' for the rendering/camera.\nMeant for things like looking down/around while elytra flying nice and straight."),
    TWEAK_SHULKERBOX_STACKING       ("tweakEmptyShulkerBoxesStack",         false, true, "",    "Enables empty Shulker Boxes stacking up to 64.\nNOTE: They will also stack inside inventories!\nOn servers this will cause desyncs/glitches\nunless the server has a mod that does the same.\nIn single player this changes shulker box based system behaviour."),
    TWEAK_SHULKERBOX_STACK_GROUND   ("tweakEmptyShulkerBoxesStackOnGround", false, true, "",    "Enables empty Shulker Boxes stacking up to 64\nwhen as items on the ground"),
    TWEAK_EXPLOSION_REDUCED_PARTICLES ("tweakExplosionReducedParticles",    false, "",    "If enabled, then all explosion particles will use the\nEXPLOSION_NORMAL particle instead of possibly\nthe EXPLOSION_LARGE or EXPLOSION_HUGE particles"),
    TWEAK_F3_CURSOR                 ("tweakF3Cursor",                       false, "",    "Enables always rendering the F3 screen cursor"),
    TWEAK_FAKE_SNEAKING             ("tweakFakeSneaking",                   false, "",    "Enables \"fake sneaking\" ie. prevents you from falling from edges\nwithout slowing down the movement speed"),
    TWEAK_FAST_BLOCK_PLACEMENT      ("tweakFastBlockPlacement",             false, "",    "Enables fast/convenient block placement when moving\nthe cursor over new blocks"),
    TWEAK_FAST_LEFT_CLICK           ("tweakFastLeftClick",                  false, "",    "Enables automatic fast left clicking while holding down\nthe attack button (left click).\nThe number of clicks per tick is set in the Generic configs."),
    TWEAK_FAST_RIGHT_CLICK          ("tweakFastRightClick",                 false, "",    "Enables automatic fast right clicking while holding down\nthe use button (right click).\nThe number of clicks per tick is set in the Generic configs."),
    TWEAK_FILL_CLONE_LIMIT          ("tweakFillCloneLimit",                 false, true, "",    "Enables overriding the /fill and /clone command\nblock limits in single player.\nThe new limit can be set in the Generic configs,\nin the 'fillCloneLimit' config value"),
    TWEAK_FLY_SPEED                 ("tweakFlySpeed",                       false, "",    KeybindSettings.INGAME_BOTH, "Enables overriding the fly speed in creative or spectator mode\nand using some presets for it"),
    TWEAK_FLEXIBLE_BLOCK_PLACEMENT  ("tweakFlexibleBlockPlacement",         false, "",    "Enables placing blocks in different orientations\nor with an offset, while holding down the\nhotkeys for those modes."),
    TWEAK_FREE_CAMERA               ("tweakFreeCamera",                     false, "",    "Enables a free camera mode, similar to spectator mode,\nbut where the player will remain in place where\nyou first activate the free camera mode"),
    TWEAK_FREE_CAMERA_MOTION        ("tweakFreeCameraMotion",               false, "",    "When the Free Camera mode is enabled, if this feature is also enabled,\nthe movement inputs will affect the camera entity instead of the actual player.\nThis option is provided so that it's also possible to control\nthe actual player while in Free Camera mode, by disabling this option.\nNormally, if you just want to control the camera entity while in Free camera mode,\nthen just set the same hotkey for both features."),
    TWEAK_GAMMA_OVERRIDE            ("tweakGammaOverride",                  false, "",    "Overrides the video settings gamma value with\nthe one set in the Generic configs"),
    TWEAK_HAND_RESTOCK              ("tweakHandRestock",                    false, "",    "Enables swapping a new stack to the main or the offhand\nwhen the previous stack runs out"),
    TWEAK_HOLD_ATTACK               ("tweakHoldAttack",                     false, "",    "Emulates holding down the attack button\n§6NOTE: You should ONLY toggle this via the toggle hotkey!\n§6You should also make the toggle hotkey have the attack\n§6key as part of the keybind, so that it properly activates/deactivates\n§6 the vanilla keybind when you toggle the tweak on/off"),
    TWEAK_HOLD_USE                  ("tweakHoldUse",                        false, "",    "Emulates holding down the use button\n§6NOTE: You should ONLY toggle this via the toggle hotkey!\n§6You should also make the toggle hotkey have the use\n§6key as part of the keybind, so that it properly activates/deactivates\n§6 the vanilla keybind when you toggle the tweak on/off"),
    TWEAK_HOTBAR_SCROLL             ("tweakHotbarScroll",                   false, "",    "Enables the hotbar swapping via scrolling feature"),
    TWEAK_HOTBAR_SLOT_CYCLE         ("tweakHotbarSlotCycle",                false, "",    KeybindSettings.INGAME_BOTH, "Enables cycling the selected hotbar slot after each placed\nblock, up to the set max slot number.\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind."),
    TWEAK_HOTBAR_SLOT_RANDOMIZER    ("tweakHotbarSlotRandomizer",           false, "",    KeybindSettings.INGAME_BOTH, "Enables randomizing the selected hotbar slot after each placed\nblock, up to the set max slot number.\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind."),
    TWEAK_HOTBAR_SWAP               ("tweakHotbarSwap",                     false, "",    "Enables the hotbar swapping via hotkeys feature"),
    TWEAK_INVENTORY_PREVIEW         ("tweakInventoryPreview",               false, true, "",    "Enables an inventory preview while having the cursor over\na block or an entity with an inventory and holding down\nthe configured hotkey."),
    TWEAK_ITEM_UNSTACKING_PROTECTION("tweakItemUnstackingProtection",       false, "",    "If enabled, then items configured in Lists -> unstackingItems\nwon't be allowed to spill out when using.\nThis is meant for example to prevent throwing buckets\ninto lava when filling them."),
    TWEAK_LAVA_VISIBILITY           ("tweakLavaVisibility",                 false, "",    "If enabled and the player has a Respiration helmet and/or\nWather Breathing active, the lava fog is greatly reduced."),
    TWEAK_MAP_PREVIEW               ("tweakMapPreview",                     false, "",    "If enabled, then holding shift over maps in an inventory\nwill render a preview of the map"),
    TWEAK_MOVEMENT_KEYS             ("tweakMovementKeysLast",               false, "",    "If enabled, then opposite movement keys won't cancel each other,\nbut instead the last pressed key is the active input."),
    TWEAK_PERIODIC_ATTACK           ("tweakPeriodicAttack",                 false, "",    "Enables periodic attacks (left clicks)\nConfigure the interval in Generic -> periodicAttackInterval"),
    TWEAK_PERIODIC_USE              ("tweakPeriodicUse",                    false, "",    "Enables periodic uses (right clicks)\nConfigure the interval in Generic -> periodicUseInterval"),
    TWEAK_PERMANENT_SNEAK           ("tweakPermanentSneak",                 false, "",    "If enabled, the player will be sneaking the entire time"),
    TWEAK_PERMANENT_SPRINT          ("tweakPermanentSprint",                false, "",    "If enabled, the player will be always sprinting when moving forward"),
    TWEAK_PLACEMENT_GRID            ("tweakPlacementGrid",                  false, "",    KeybindSettings.INGAME_BOTH, "When enabled, you can only place blocks in\na grid pattern, with a configurable interval.\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind."),
    TWEAK_PLACEMENT_LIMIT           ("tweakPlacementLimit",                 false, "",    KeybindSettings.INGAME_BOTH, "When enabled, you can only place a set number\nof blocks per use/right click.\nTo quickly adjust the value, scroll while\nholding down the tweak toggle keybind."),
    TWEAK_PLACEMENT_RESTRICTION     ("tweakPlacementRestriction",           false, "",    "Enables the Placement Restriction mode\n  (Plane, Layer, Face, Column, Line, Diagonal)"),
    TWEAK_PLACEMENT_REST_FIRST      ("tweakPlacementRestrictionFirst",      false, "",    "Restricts block placement so that you can only\nplace blocks against the same block type\nyou first clicked on"),
    TWEAK_PLACEMENT_REST_HAND       ("tweakPlacementRestrictionHand",       false, "",    "Restricts block placement so that you can only\nplace blocks against the same block type\nyou are holding in your hand"),
    TWEAK_PLAYER_INVENTORY_PEEK     ("tweakPlayerInventoryPeek",            false, "",    "Enables a player inventory peek/preview, while holding the\nconfigured hotkey key for it."),
    TWEAK_PLAYER_ON_FIRE_SCALE      ("tweakPlayerOnFireScale",              false, "",    "Enables scaling the on-fire effect on the player themselves,\nusing the scaling factor from Generic -> playerOnFireScale"),
    TWEAK_POTION_WARNING            ("tweakPotionWarning",                  false, "",    "Prints a warning message to the hotbar when\nnon-ambient potion effects are about to run out"),
    TWEAK_PRINT_DEATH_COORDINATES   ("tweakPrintDeathCoordinates",          false, "",    "Enables printing the player's coordinates to chat on death.\nThis feature is originally from usefulmod by nessie."),
    TWEAK_PICK_BEFORE_PLACE         ("tweakPickBeforePlace",                false, "",    "If enabled, then before each block placement, the same block\nis switched to hand that you are placing against"),
    TWEAK_PLAYER_LIST_ALWAYS_ON     ("tweakPlayerListAlwaysVisible",        false, "",    "If enabled, then the player list is always rendered without\nhaving to hold down the key (tab by default)"),
    TWEAK_RELAXED_BLOCK_PLACEMENT   ("tweakRelaxedBlockPlacement",          false, true, "",    "Allows placing certain blocks without the normal restrictions,\nsuch as fence gates and pumpkins in mid-air"),
    TWEAK_REMOVE_OWN_POTION_EFFECTS ("tweakRemoveOwnPotionEffects",         false, "",    "Removes the potion effect particles from the\nplayer itself in first person mode"),
    TWEAK_RENDER_INVISIBLE_ENTITIES ("tweakRenderInvisibleEntities",        false, "",    "When enabled, invisible entities are rendered like\nthey would be in spectator mode."),
    TWEAK_RENDER_LIMIT_ENTITIES     ("tweakRenderLimitEntities",            false, "",    "Enables limiting the number of certain types of entities\nto render per frame. Currently XP Orbs and Item entities\nare supported, see Generic configs for the limits."),
    TWEAK_REPAIR_MODE               ("tweakRepairMode",                     false, "",    "If enabled, then fully repaired items held in hand will\nbe swapped to damaged items that have Mending on them."),
    TWEAK_SHULKERBOX_DISPLAY        ("tweakShulkerBoxDisplay",              false, "",    "Enables the Shulker Box contents display when hovering\nover them in an inventory and holding shift"),
    TWEAK_SIGN_COPY                 ("tweakSignCopy",                       false, "",    "When enabled, placed signs will use the text from\nthe previously placed sign.\nCan be combined with tweakNoSignGui to quickly place copies\nof a sign, by enabling that tweak after making the first sign."),
    TWEAK_SNAP_AIM                  ("tweakSnapAim",                        false, "",    KeybindSettings.INGAME_BOTH, "Enabled a snap aim tweak, to make the player face to pre-set exact yaw rotations"),
    TWEAK_SNAP_AIM_LOCK             ("tweakSnapAimLock",                    false, "",    "Enables a snap aim lock, locking the yaw and/or pitch rotations\nto the currently snapped value"),
    TWEAK_SPECTATOR_TELEPORT        ("tweakSpectatorTeleport",              false, "",    "Allows spectators to teleport to other spectators.\nThis is originally from usefulmod by nessie."),
    TWEAK_STRUCTURE_BLOCK_LIMIT     ("tweakStructureBlockLimit",            false, true, "",    "Allows overriding the structure block limit.\nThe new limit is set in Generic -> structureBlockMaxSize"),
    TWEAK_SWAP_ALMOST_BROKEN_TOOLS  ("tweakSwapAlmostBrokenTools",          false, "",    "If enabled, then any damageable items held in the hand that\nare about to break will be swapped to fresh ones"),
    TWEAK_TAB_COMPLETE_COORDINATE   ("tweakTabCompleteCoordinate",          false, "",    "If enabled, then tab-completing coordinates while not\nlooking at a block, will use the player's position\ninstead of adding the ~ character."),
    TWEAK_TOOL_SWITCH               ("tweakToolSwitch",                     false, "",    "Enables automatically switching to an effective tool for the targeted block"),
    TWEAK_Y_MIRROR                  ("tweakYMirror",                        false, "",    "Mirrors the targeted y-position within the block bounds.\nThis is basically for placing slabs or stairs\nin the opposite top/bottom state from normal,\nif you have to place them against another slab for example."),
    TWEAK_ZOOM                      ("tweakZoom",                           false, "",    KeybindSettings.INGAME_BOTH, "Enables using the zoom hotkey to, well, zoom in");

    private final String name;
    private final String comment;
    private final String prettyName;
    private final IKeybind keybind;
    private final boolean defaultValueBoolean;
    private final boolean singlePlayer;
    private boolean valueBoolean;
    private boolean lastSavedValueBoolean;
    private IValueChangeCallback<IConfigBoolean> callback;

    private FeatureToggle(String name, boolean defaultValue, String defaultHotkey, String comment)
    {
        this(name, defaultValue, false, defaultHotkey, KeybindSettings.DEFAULT, comment);
    }

    private FeatureToggle(String name, boolean defaultValue, boolean singlePlayer, String defaultHotkey, String comment)
    {
        this(name, defaultValue, singlePlayer, defaultHotkey, KeybindSettings.DEFAULT, comment);
    }

    private FeatureToggle(String name, boolean defaultValue, String defaultHotkey, KeybindSettings settings, String comment)
    {
        this(name, defaultValue, false, defaultHotkey, settings, comment);
    }

    private FeatureToggle(String name, boolean defaultValue, boolean singlePlayer, String defaultHotkey, KeybindSettings settings, String comment)
    {
        this(name, defaultValue, singlePlayer, defaultHotkey, settings, comment, StringUtils.splitCamelCase(name.substring(5)));
    }

    private FeatureToggle(String name, boolean defaultValue, String defaultHotkey, String comment, String prettyName)
    {
        this(name, defaultValue, false, defaultHotkey, comment, prettyName);
    }

    private FeatureToggle(String name, boolean defaultValue, boolean singlePlayer, String defaultHotkey, String comment, String prettyName)
    {
        this(name, defaultValue, singlePlayer, defaultHotkey, KeybindSettings.DEFAULT, comment, prettyName);
    }

    private FeatureToggle(String name, boolean defaultValue, boolean singlePlayer, String defaultHotkey, KeybindSettings settings, String comment, String prettyName)
    {
        this.name = name;
        this.valueBoolean = defaultValue;
        this.defaultValueBoolean = defaultValue;
        this.singlePlayer = singlePlayer;
        this.comment = comment;
        this.prettyName = prettyName;
        this.keybind = KeybindMulti.fromStorageString(defaultHotkey, settings);
        this.keybind.setCallback(new KeyCallbackToggleBooleanConfigWithMessage(this));

        this.cacheSavedValue();
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
        if (this.singlePlayer)
        {
            return GuiBase.TXT_GOLD + this.getName() + GuiBase.TXT_RST;
        }

        return this.getName();
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
        if (this.comment == null)
        {
            return "";
        }

        if (this.singlePlayer)
        {
            return this.comment + "\n" + StringUtils.translate("tweakeroo.label.config_comment.single_player_only");
        }
        else
        {
            return this.comment;
        }
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
    public boolean isDirty()
    {
        return this.lastSavedValueBoolean != this.valueBoolean || this.keybind.isDirty();
    }

    @Override
    public void cacheSavedValue()
    {
        this.lastSavedValueBoolean = this.valueBoolean;
        this.keybind.cacheSavedValue();
    }

    @Override
    public void resetToDefault()
    {
        this.valueBoolean = this.defaultValueBoolean;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.valueBoolean = element.getAsBoolean();
            }
            else
            {
                LiteModTweakeroo.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            LiteModTweakeroo.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }

        this.cacheSavedValue();
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.valueBoolean);
    }
}
