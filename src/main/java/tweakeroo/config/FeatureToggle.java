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
    TWEAK_ACCURATE_BLOCK_PLACEMENT      ("tweakAccurateBlockPlacement"),
    TWEAK_AFTER_CLICKER                 ("tweakAfterClicker",                       KeyBindSettings.INGAME_RELEASE),
    TWEAK_AIM_LOCK                      ("tweakAimLock"),
    TWEAK_ANGEL_BLOCK                   ("tweakAngelBlock"),
    TWEAK_BLOCK_BREAKING_PARTICLES      ("tweakBlockBreakingParticleTweaks"),
    TWEAK_BLOCK_MODEL_OVERRIDE          ("tweakBlockModelOverride"),
    TWEAK_BLOCK_PLACEMENT_Y_MIRROR      ("tweakBlockPlacementYMirror"),
    TWEAK_BLOCK_REACH_OVERRIDE          ("tweakBlockReachOverride"),
    TWEAK_BLOCK_RENDER_TYPE_OVERRIDE    ("tweakBlockRenderTypeOverride"),
    TWEAK_BREAKING_GRID                 ("tweakBreakingGrid",                       KeyBindSettings.INGAME_RELEASE),
    TWEAK_BREAKING_RESTRICTION          ("tweakBreakingRestriction",                KeyBindSettings.INGAME_RELEASE),
    TWEAK_CHAT_BACKGROUND_COLOR         ("tweakChatBackgroundColor"),
    TWEAK_CHAT_PERSISTENT_TEXT          ("tweakChatPersistentText"),
    TWEAK_CHAT_TIMESTAMP                ("tweakChatTimestamp"),
    TWEAK_CHUNK_RENDER_MAIN_THREAD      ("tweakChunkRenderOnMainThread"),
    TWEAK_CHUNK_RENDER_TIMEOUT          ("tweakChunkRenderTimeoutOverride"),
    TWEAK_CLOUD_HEIGHT_OVERRIDE         ("tweakCloudHeightOverride"),
    TWEAK_COMMAND_BLOCK_EXTRA_FIELDS    ("tweakCommandBlockExtraFields"),
    TWEAK_CUSTOM_FLAT_PRESETS           ("tweakCustomFlatPresets"),
    TWEAK_DEBUG_PIE_CHART_SCALE         ("tweakDebugPieChartScale"),
    TWEAK_ELYTRA_CAMERA                 ("tweakElytraCamera"),
    TWEAK_SHULKERBOX_STACKING           ("tweakEmptyShulkerBoxesStack"),
    TWEAK_SHULKERBOX_STACK_GROUND       ("tweakEmptyShulkerBoxesStackOnGround"),
    TWEAK_EXPLOSION_REDUCED_PARTICLES   ("tweakExplosionReducedParticles"),
    TWEAK_F3_CURSOR                     ("tweakF3Cursor"),
    TWEAK_FAKE_SNEAKING                 ("tweakFakeSneaking"),
    TWEAK_FAST_BLOCK_PLACEMENT          ("tweakFastBlockPlacement",                 KeyBindSettings.INGAME_RELEASE),
    TWEAK_FAST_LEFT_CLICK               ("tweakFastLeftClick",                      KeyBindSettings.INGAME_RELEASE),
    TWEAK_FAST_RIGHT_CLICK              ("tweakFastRightClick",                     KeyBindSettings.INGAME_RELEASE),
    TWEAK_FILL_CLONE_LIMIT              ("tweakFillCloneLimit"),
    TWEAK_FLY_SPEED                     ("tweakFlySpeed",                           KeyBindSettings.INGAME_RELEASE),
    TWEAK_FLEXIBLE_BLOCK_PLACEMENT      ("tweakFlexibleBlockPlacement"),
    TWEAK_FREE_CAMERA                   ("tweakFreeCamera"),
    TWEAK_GAMMA_OVERRIDE                ("tweakGammaOverride",                      KeyBindSettings.INGAME_RELEASE),
    TWEAK_HAND_RESTOCK                  ("tweakHandRestock"),
    TWEAK_HANGABLE_ENTITY_BYPASS        ("tweakHangableEntityBypass"),
    TWEAK_HOLD_ATTACK                   ("tweakHoldAttack"),
    TWEAK_HOLD_USE                      ("tweakHoldUse"),
    TWEAK_HOTBAR_SCROLL                 ("tweakHotbarScroll"),
    TWEAK_HOTBAR_SLOT_CYCLE             ("tweakHotbarSlotCycle",                    KeyBindSettings.INGAME_RELEASE),
    TWEAK_HOTBAR_SLOT_RANDOMIZER        ("tweakHotbarSlotRandomizer",               KeyBindSettings.INGAME_RELEASE),
    TWEAK_HOTBAR_SWAP                   ("tweakHotbarSwap"),
    TWEAK_INVENTORY_PREVIEW             ("tweakInventoryPreview"),
    TWEAK_ITEM_UNSTACKING_PROTECTION    ("tweakItemUnstackingProtection"),
    TWEAK_LAVA_VISIBILITY               ("tweakLavaVisibility"),
    TWEAK_LLAMA_STEERING                ("tweakLlamaSteering"),
    TWEAK_MAP_PREVIEW                   ("tweakMapPreview"),
    TWEAK_MATCHING_SKY_FOG              ("tweakMatchingSkyFog"),
    TWEAK_MOVEMENT_KEYS                 ("tweakMovementKeysLast"),
    TWEAK_PERIODIC_ATTACK               ("tweakPeriodicAttack",                     KeyBindSettings.INGAME_RELEASE),
    TWEAK_PERIODIC_USE                  ("tweakPeriodicUse",                        KeyBindSettings.INGAME_RELEASE),
    TWEAK_PERMANENT_SNEAK               ("tweakPermanentSneak"),
    TWEAK_PERMANENT_SPRINT              ("tweakPermanentSprint"),
    TWEAK_PICK_BEFORE_PLACE             ("tweakPickBeforePlace"),
    TWEAK_PLACEMENT_GRID                ("tweakPlacementGrid",                      KeyBindSettings.INGAME_RELEASE),
    TWEAK_PLACEMENT_LIMIT               ("tweakPlacementLimit",                     KeyBindSettings.INGAME_RELEASE),
    TWEAK_PLACEMENT_RESTRICTION         ("tweakPlacementRestriction",               KeyBindSettings.INGAME_RELEASE),
    TWEAK_PLACEMENT_REST_FIRST          ("tweakPlacementRestrictionFirst"),
    TWEAK_PLACEMENT_REST_HAND           ("tweakPlacementRestrictionHand"),
    TWEAK_PLAYER_INVENTORY_PEEK         ("tweakPlayerInventoryPeek"),
    TWEAK_PLAYER_LIST_ALWAYS_ON         ("tweakPlayerListAlwaysVisible"),
    TWEAK_PLAYER_ON_FIRE_SCALE          ("tweakPlayerOnFireScale"),
    TWEAK_POTION_WARNING                ("tweakPotionWarning"),
    TWEAK_PRINT_DEATH_COORDINATES       ("tweakPrintDeathCoordinates"),
    TWEAK_RELAXED_BLOCK_PLACEMENT       ("tweakRelaxedBlockPlacement"),
    TWEAK_RENDER_EDGE_CHUNKS            ("tweakRenderEdgeChunks"),
    TWEAK_RENDER_INVISIBLE_ENTITIES     ("tweakRenderInvisibleEntities"),
    TWEAK_RENDER_LIMIT_ENTITIES         ("tweakRenderLimitEntities"),
    TWEAK_REPAIR_MODE                   ("tweakRepairMode"),
    TWEAK_SHULKERBOX_DISPLAY            ("tweakShulkerBoxDisplay"),
    TWEAK_SIGN_COPY                     ("tweakSignCopy"),
    TWEAK_SNAP_AIM                      ("tweakSnapAim",                            KeyBindSettings.INGAME_RELEASE),
    TWEAK_SNAP_AIM_LOCK                 ("tweakSnapAimLock"),
    TWEAK_SPECTATOR_TELEPORT            ("tweakSpectatorTeleport"),
    TWEAK_STATIC_FOV                    ("tweakStaticFov",                          KeyBindSettings.INGAME_RELEASE),
    TWEAK_STRUCTURE_BLOCK_LIMIT         ("tweakStructureBlockLimit"),
    TWEAK_SWAP_ALMOST_BROKEN_TOOLS      ("tweakSwapAlmostBrokenTools"),
    TWEAK_TAB_COMPLETE_COORDINATE       ("tweakTabCompleteCoordinate"),
    TWEAK_TILE_RENDER_DISTANCE          ("tweakTileEntityRenderDistance"),
    TWEAK_TOOL_SWITCH                   ("tweakToolSwitch"),
    TWEAK_WORLD_LIST_DATE_FORMAT        ("tweakWorldListDateFormat"),
    TWEAK_ZOOM                          ("tweakZoom",                               KeyBindSettings.INGAME_RELEASE);

    public static final ImmutableList<FeatureToggle> VALUES = ImmutableList.copyOf(values());
    public static final ImmutableList<BooleanConfig> TOGGLE_CONFIGS = ImmutableList.copyOf(VALUES.stream().map(FeatureToggle::getBooleanConfig).collect(Collectors.toList()));
    public static final ImmutableList<HotkeyConfig> TOGGLE_HOTKEYS = ImmutableList.copyOf(VALUES.stream().map(FeatureToggle::getHotkeyConfig).collect(Collectors.toList()));

    private final BooleanConfig toggleStatus;
    private final HotkeyConfig toggleHotkey;

    FeatureToggle(String name)
    {
        this(name, KeyBindSettings.INGAME_DEFAULT);
    }

    FeatureToggle(String name, KeyBindSettings settings)
    {
        this.toggleStatus = new BooleanConfig(name, false);
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
