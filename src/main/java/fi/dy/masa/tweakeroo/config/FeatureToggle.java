package fi.dy.masa.tweakeroo.config;

import java.util.Locale;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.ValueChangeCallback;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.input.callback.ToggleBooleanWithMessageKeyCallback;

public enum FeatureToggle implements ConfigInfo
{
    TWEAK_ACCURATE_BLOCK_PLACEMENT      ("tweakAccurateBlockPlacement",             false),
    TWEAK_AFTER_CLICKER                 ("tweakAfterClicker",                       false, KeyBindSettings.INGAME_BOTH),
    TWEAK_AIM_LOCK                      ("tweakAimLock",                            false),
    TWEAK_ANGEL_BLOCK                   ("tweakAngelBlock",                         false),
    TWEAK_BLOCK_PLACEMENT_Y_MIRROR      ("tweakBlockPlacementYMirror",              false),
    TWEAK_BLOCK_BREAKING_PARTICLES      ("tweakBlockBreakingParticleTweaks",        false),
    TWEAK_BLOCK_REACH_OVERRIDE          ("tweakBlockReachOverride",                 false),
    TWEAK_BREAKING_GRID                 ("tweakBreakingGrid",                       false, KeyBindSettings.INGAME_BOTH),
    TWEAK_BREAKING_RESTRICTION          ("tweakBreakingRestriction",                false),
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
    TWEAK_FAST_BLOCK_PLACEMENT          ("tweakFastBlockPlacement",                 false),
    TWEAK_FAST_LEFT_CLICK               ("tweakFastLeftClick",                      false),
    TWEAK_FAST_RIGHT_CLICK              ("tweakFastRightClick",                     false),
    TWEAK_FILL_CLONE_LIMIT              ("tweakFillCloneLimit",                     false),
    TWEAK_FLY_SPEED                     ("tweakFlySpeed",                           false),
    TWEAK_FLEXIBLE_BLOCK_PLACEMENT      ("tweakFlexibleBlockPlacement",             false),
    TWEAK_FREE_CAMERA                   ("tweakFreeCamera",                         false),
    TWEAK_GAMMA_OVERRIDE                ("tweakGammaOverride",                      false),
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
    TWEAK_PERIODIC_ATTACK               ("tweakPeriodicAttack",                     false),
    TWEAK_PERIODIC_USE                  ("tweakPeriodicUse",                        false),
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
    TWEAK_STATIC_FOV                    ("tweakStaticFov",                          false),
    TWEAK_STRUCTURE_BLOCK_LIMIT         ("tweakStructureBlockLimit",                false),
    TWEAK_SWAP_ALMOST_BROKEN_TOOLS      ("tweakSwapAlmostBrokenTools",              false),
    TWEAK_TAB_COMPLETE_COORDINATE       ("tweakTabCompleteCoordinate",              false),
    TWEAK_TILE_RENDER_DISTANCE          ("tweakTileEntityRenderDistance",           false),
    TWEAK_TOOL_SWITCH                   ("tweakToolSwitch",                         false),
    TWEAK_ZOOM                          ("tweakZoom",                               false, KeyBindSettings.INGAME_BOTH);

    public static final ImmutableList<FeatureToggle> VALUES = ImmutableList.copyOf(values());
    public static final ImmutableList<BooleanConfig> TOGGLE_CONFIGS = ImmutableList.copyOf(VALUES.stream().map(FeatureToggle::getBooleanConfig).collect(Collectors.toList()));
    public static final ImmutableList<HotkeyConfig> TOGGLE_HOTKEYS = ImmutableList.copyOf(VALUES.stream().map(FeatureToggle::getHotkeyConfig).collect(Collectors.toList()));

    private final BooleanConfig toggleStatus;
    private final HotkeyConfig toggleHotkey;

    FeatureToggle(String name, boolean defaultValue)
    {
        this(name, defaultValue, KeyBindSettings.DEFAULT);
    }

    FeatureToggle(String name, boolean defaultValue, KeyBindSettings settings)
    {
        this.toggleStatus = new BooleanConfig(name, defaultValue);
        this.toggleHotkey = new HotkeyConfig(name, "", settings);
        this.toggleHotkey.getKeyBind().setCallback(new ToggleBooleanWithMessageKeyCallback(this.toggleStatus));

        String nameLower = name.toLowerCase(Locale.ROOT);
        String nameKey = "tweakeroo.feature_toggle.name." + nameLower;
        this.toggleStatus.setNameTranslationKey(nameKey).setPrettyNameTranslationKey(nameKey);
        this.toggleStatus.setCommentTranslationKey("tweakeroo.feature_toggle.comment." + nameLower);
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
    public String getName()
    {
        return this.toggleStatus.getName();
    }

    @Override
    public String getConfigNameTranslationKey()
    {
        return this.toggleStatus.getConfigNameTranslationKey();
    }

    @Nullable
    @Override
    public String getCommentTranslationKey()
    {
        return this.toggleStatus.getCommentTranslationKey();
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
