package tweakeroo.config;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import malilib.config.option.BooleanConfig;
import malilib.config.option.ConfigInfo;
import malilib.config.option.HotkeyConfig;
import malilib.input.KeyBind;
import malilib.input.KeyBindSettings;
import malilib.input.callback.HotkeyCallback;
import malilib.input.callback.ToggleBooleanWithMessageKeyCallback;
import malilib.overlay.message.MessageHelpers.BooleanConfigMessageFactory;
import malilib.util.data.ModInfo;
import tweakeroo.Reference;

public enum FeatureToggle implements ConfigInfo
{
    TWEAK_ACCURATE_BLOCK_PLACEMENT      ("tweakAccurateBlockPlacement",             false),
    TWEAK_AFTER_CLICKER                 ("tweakAfterClicker",                       false, KeyBindSettings.INGAME_BOTH),
    TWEAK_AIM_LOCK                      ("tweakAimLock",                            false),
    TWEAK_ANGEL_BLOCK                   ("tweakAngelBlock",                         false),
    TWEAK_BLOCK_BREAKING_PARTICLES      ("tweakBlockBreakingParticleTweaks",        false),
    TWEAK_BLOCK_MODEL_OVERRIDE          ("tweakBlockModelOverride",                 false),
    TWEAK_BLOCK_PLACEMENT_Y_MIRROR      ("tweakBlockPlacementYMirror",              false),
    TWEAK_BLOCK_REACH_OVERRIDE          ("tweakBlockReachOverride",                 false),
    TWEAK_BLOCK_RENDER_TYPE_OVERRIDE    ("tweakBlockRenderTypeOverride",            false),
    TWEAK_BREAKING_GRID                 ("tweakBreakingGrid",                       false, KeyBindSettings.INGAME_BOTH),
    TWEAK_BREAKING_RESTRICTION          ("tweakBreakingRestriction",                false, KeyBindSettings.INGAME_BOTH),
    TWEAK_CHAT_BACKGROUND_COLOR         ("tweakChatBackgroundColor",                false),
    TWEAK_CHAT_PERSISTENT_TEXT          ("tweakChatPersistentText",                 false),
    TWEAK_CHAT_TIMESTAMP                ("tweakChatTimestamp",                      false),
    TWEAK_CHUNK_RENDER_MAIN_THREAD      ("tweakChunkRenderOnMainThread",            false),
    TWEAK_CHUNK_RENDER_TIMEOUT          ("tweakChunkRenderTimeoutOverride",         false),
    TWEAK_CLOUD_HEIGHT_OVERRIDE         ("tweakCloudHeightOverride",                false),
    TWEAK_COMMAND_BLOCK_EXTRA_FIELDS    ("tweakCommandBlockExtraFields",            false),
    TWEAK_CUSTOM_FLAT_PRESETS           ("tweakCustomFlatPresets",                  false),
    TWEAK_DEBUG_PIE_CHART_SCALE         ("tweakDebugPieChartScale",                 false),
    TWEAK_ELYTRA_CAMERA                 ("tweakElytraCamera",                       false),
    TWEAK_SHULKERBOX_STACKING           ("tweakEmptyShulkerBoxesStack",             false),
    TWEAK_SHULKERBOX_STACK_GROUND       ("tweakEmptyShulkerBoxesStackOnGround",     false),
    TWEAK_EXPLOSION_REDUCED_PARTICLES   ("tweakExplosionReducedParticles",          false),
    TWEAK_F3_CURSOR                     ("tweakF3Cursor",                           false),
    TWEAK_FAKE_SNEAKING                 ("tweakFakeSneaking",                       false),
    TWEAK_FAST_BLOCK_PLACEMENT          ("tweakFastBlockPlacement",                 false, KeyBindSettings.INGAME_BOTH),
    TWEAK_FAST_LEFT_CLICK               ("tweakFastLeftClick",                      false, KeyBindSettings.INGAME_BOTH),
    TWEAK_FAST_RIGHT_CLICK              ("tweakFastRightClick",                     false, KeyBindSettings.INGAME_BOTH),
    TWEAK_FILL_CLONE_LIMIT              ("tweakFillCloneLimit",                     false),
    TWEAK_FLY_SPEED                     ("tweakFlySpeed",                           false, KeyBindSettings.INGAME_BOTH),
    TWEAK_FLEXIBLE_BLOCK_PLACEMENT      ("tweakFlexibleBlockPlacement",             false),
    TWEAK_FREE_CAMERA                   ("tweakFreeCamera",                         false),
    TWEAK_GAMMA_OVERRIDE                ("tweakGammaOverride",                      false, KeyBindSettings.INGAME_BOTH),
    TWEAK_HAND_RESTOCK                  ("tweakHandRestock",                        false),
    TWEAK_HANGABLE_ENTITY_BYPASS        ("tweakHangableEntityBypass",               false),
    TWEAK_HOLD_ATTACK                   ("tweakHoldAttack",                         false),
    TWEAK_HOLD_USE                      ("tweakHoldUse",                            false),
    TWEAK_HOTBAR_SCROLL                 ("tweakHotbarScroll",                       false),
    TWEAK_HOTBAR_SLOT_CYCLE             ("tweakHotbarSlotCycle",                    false, KeyBindSettings.INGAME_BOTH),
    TWEAK_HOTBAR_SLOT_RANDOMIZER        ("tweakHotbarSlotRandomizer",               false, KeyBindSettings.INGAME_BOTH),
    TWEAK_HOTBAR_SWAP                   ("tweakHotbarSwap",                         false),
    TWEAK_INVENTORY_PREVIEW             ("tweakInventoryPreview",                   false),
    TWEAK_ITEM_UNSTACKING_PROTECTION    ("tweakItemUnstackingProtection",           false),
    TWEAK_LAVA_VISIBILITY               ("tweakLavaVisibility",                     false),
    TWEAK_LLAMA_STEERING                ("tweakLlamaSteering",                      false),
    TWEAK_MAP_PREVIEW                   ("tweakMapPreview",                         false),
    TWEAK_MATCHING_SKY_FOG              ("tweakMatchingSkyFog",                     false),
    TWEAK_MOVEMENT_KEYS                 ("tweakMovementKeysLast",                   false),
    TWEAK_PERIODIC_ATTACK               ("tweakPeriodicAttack",                     false, KeyBindSettings.INGAME_BOTH),
    TWEAK_PERIODIC_USE                  ("tweakPeriodicUse",                        false, KeyBindSettings.INGAME_BOTH),
    TWEAK_PERMANENT_SNEAK               ("tweakPermanentSneak",                     false),
    TWEAK_PERMANENT_SPRINT              ("tweakPermanentSprint",                    false),
    TWEAK_PICK_BEFORE_PLACE             ("tweakPickBeforePlace",                    false),
    TWEAK_PLACEMENT_GRID                ("tweakPlacementGrid",                      false, KeyBindSettings.INGAME_BOTH),
    TWEAK_PLACEMENT_LIMIT               ("tweakPlacementLimit",                     false, KeyBindSettings.INGAME_BOTH),
    TWEAK_PLACEMENT_RESTRICTION         ("tweakPlacementRestriction",               false),
    TWEAK_PLACEMENT_REST_FIRST          ("tweakPlacementRestrictionFirst",          false),
    TWEAK_PLACEMENT_REST_HAND           ("tweakPlacementRestrictionHand",           false),
    TWEAK_PLAYER_INVENTORY_PEEK         ("tweakPlayerInventoryPeek",                false),
    TWEAK_PLAYER_LIST_ALWAYS_ON         ("tweakPlayerListAlwaysVisible",            false),
    TWEAK_PLAYER_ON_FIRE_SCALE          ("tweakPlayerOnFireScale",                  false),
    TWEAK_POTION_WARNING                ("tweakPotionWarning",                      false),
    TWEAK_PRINT_DEATH_COORDINATES       ("tweakPrintDeathCoordinates",              false),
    TWEAK_RELAXED_BLOCK_PLACEMENT       ("tweakRelaxedBlockPlacement",              false),
    TWEAK_RENDER_EDGE_CHUNKS            ("tweakRenderEdgeChunks",                   false),
    TWEAK_RENDER_INVISIBLE_ENTITIES     ("tweakRenderInvisibleEntities",            false),
    TWEAK_RENDER_LIMIT_ENTITIES         ("tweakRenderLimitEntities",                false),
    TWEAK_REPAIR_MODE                   ("tweakRepairMode",                         false),
    TWEAK_SHULKERBOX_DISPLAY            ("tweakShulkerBoxDisplay",                  false),
    TWEAK_SIGN_COPY                     ("tweakSignCopy",                           false),
    TWEAK_SNAP_AIM                      ("tweakSnapAim",                            false, KeyBindSettings.INGAME_BOTH),
    TWEAK_SNAP_AIM_LOCK                 ("tweakSnapAimLock",                        false),
    TWEAK_SPECTATOR_TELEPORT            ("tweakSpectatorTeleport",                  false),
    TWEAK_STATIC_FOV                    ("tweakStaticFov",                          false, KeyBindSettings.INGAME_BOTH),
    TWEAK_STRUCTURE_BLOCK_LIMIT         ("tweakStructureBlockLimit",                false),
    TWEAK_SWAP_ALMOST_BROKEN_TOOLS      ("tweakSwapAlmostBrokenTools",              false),
    TWEAK_TAB_COMPLETE_COORDINATE       ("tweakTabCompleteCoordinate",              false),
    TWEAK_TILE_RENDER_DISTANCE          ("tweakTileEntityRenderDistance",           false),
    TWEAK_TOOL_SWITCH                   ("tweakToolSwitch",                         false),
    TWEAK_WORLD_LIST_DATE_FORMAT        ("tweakWorldListDateFormat",                false),
    TWEAK_ZOOM                          ("tweakZoom",                               false, KeyBindSettings.INGAME_BOTH);

    public static final ImmutableList<FeatureToggle> VALUES = ImmutableList.copyOf(values());
    public static final ImmutableList<BooleanConfig> TOGGLE_CONFIGS = ImmutableList.copyOf(VALUES.stream().map(FeatureToggle::getBooleanConfig).collect(Collectors.toList()));
    public static final ImmutableList<HotkeyConfig> TOGGLE_HOTKEYS = ImmutableList.copyOf(VALUES.stream().map(FeatureToggle::getHotkeyConfig).collect(Collectors.toList()));

    private final BooleanConfig toggleStatus;
    private final HotkeyConfig toggleHotkey;

    FeatureToggle(String name, boolean defaultValue)
    {
        this(name, defaultValue, KeyBindSettings.INGAME_DEFAULT);
    }

    FeatureToggle(String name, boolean defaultValue, KeyBindSettings settings)
    {
        this.toggleStatus = new BooleanConfig(name, defaultValue);
        this.toggleHotkey = new HotkeyConfig(name, "", settings);

        String nameLower = name.toLowerCase(Locale.ROOT);
        String nameKey = "tweakeroo.feature_toggle.name." + nameLower;
        this.toggleHotkey.setNameTranslationKey(nameKey);
        this.toggleHotkey.setPrettyNameTranslationKey(nameKey);

        this.toggleStatus.setNameTranslationKey(nameKey);
        this.toggleStatus.setPrettyNameTranslationKey(nameKey);
        this.toggleStatus.setCommentTranslationKey("tweakeroo.feature_toggle.comment." + nameLower);

        this.setSpecialToggleMessageFactory(null);
    }

    /**
     * This will replace the default hotkey callback with the ToggleBooleanWithMessageKeyCallback
     * variant that takes in the message factory
     */
    public void setSpecialToggleMessageFactory(@Nullable BooleanConfigMessageFactory messageFactory)
    {
        HotkeyCallback callback = new ToggleBooleanWithMessageKeyCallback(this.toggleStatus, messageFactory);
        this.toggleHotkey.getKeyBind().setCallback(callback);
    }

    public void setHotkeyCallback(HotkeyCallback callback)
    {
        this.toggleHotkey.getKeyBind().setCallback(callback);
    }

    public boolean getBooleanValue()
    {
        return this.toggleStatus.getBooleanValue();
    }

    public BooleanConfig getBooleanConfig()
    {
        return this.toggleStatus;
    }

    public HotkeyConfig getHotkeyConfig()
    {
        return this.toggleHotkey;
    }

    public KeyBind getKeyBind()
    {
        return this.toggleHotkey.getKeyBind();
    }

    @Override
    public ModInfo getModInfo()
    {
        return Reference.MOD_INFO;
    }

    @Override
    public String getName()
    {
        return this.toggleStatus.getName();
    }

    @Override
    public String getDisplayName()
    {
        return this.toggleStatus.getDisplayName();
    }

    @Override
    public Optional<String> getComment()
    {
        return this.toggleStatus.getComment();
    }

    @Override
    public boolean isModified()
    {
        return this.toggleStatus.isModified() ||
               this.toggleHotkey.isModified();
    }

    @Override
    public void resetToDefault()
    {
        this.toggleStatus.resetToDefault();
        this.toggleHotkey.resetToDefault();
    }
}
