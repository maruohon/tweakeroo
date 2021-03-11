package fi.dy.masa.tweakeroo.config;

import java.util.Locale;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.input.callback.ToggleBooleanWithMessageKeyCallback;

public enum DisableToggle implements ConfigInfo
{
    DISABLE_BLOCK_BREAK_PARTICLES   ("disableBlockBreakingParticles",       false),
    DISABLE_DOUBLE_TAP_SPRINT       ("disableDoubleTapSprint",              false),
    DISABLE_BOSS_FOG                ("disableBossFog",                      false),
    DISABLE_CHRISTMAS_CHESTS        ("disableChristmasChests",              false),
    DISABLE_CLIENT_ENTITY_UPDATES   ("disableClientEntityUpdates",          false),
    DISABLE_DEAD_MOB_RENDERING      ("disableDeadMobRendering",             false),
    DISABLE_DEAD_MOB_TARGETING      ("disableDeadMobTargeting",             false),
    DISABLE_ENTITY_RENDERING        ("disableEntityRendering",              false),
    DISABLE_ENTITY_TICKING          ("disableEntityTicking",                false),
    DISABLE_FALLING_BLOCK_RENDER    ("disableFallingBlockEntityRendering",  false),
    DISABLE_FP_POTION_EFFECTS       ("disableFirstPersonPotionEffects",     false),
    DISABLE_INVENTORY_EFFECTS       ("disableInventoryEffectRendering",     false),
    DISABLE_ITEM_GLINT              ("disableItemGlint",                    false),
    DISABLE_ITEM_SWITCH_COOLDOWN    ("disableItemSwitchRenderCooldown",     false),
    DISABLE_LIGHT_UPDATES           ("disableLightUpdates",                 false),
    DISABLE_LIGHT_UPDATES_ALL       ("disableLightUpdatesAll",              false),
    DISABLE_MOB_SPAWNER_MOB_RENDER  ("disableMobSpawnerMobRendering",       false),
    DISABLE_NETHER_FOG              ("disableNetherFog",                    false),
    DISABLE_OBSERVER                ("disableObserver",                     false),
    DISABLE_OBSERVER_PLACE_UPDATE   ("disableObserverPlaceUpdate",          false),
    DISABLE_OFFHAND_RENDERING       ("disableOffhandRendering",             false),
    DISABLE_PARTICLES               ("disableParticles",                    false),
    DISABLE_PORTAL_GUI_CLOSING      ("disablePortalGuiClosing",             false),
    DISABLE_RAIN_EFFECTS            ("disableRainEffects",                  false),
    DISABLE_RENDER_DISTANCE_FOG     ("disableRenderDistanceFog",            false),
    DISABLE_SCOREBOARD_RENDERING    ("disableScoreboardRendering",          false),
    DISABLE_SIGN_GUI                ("disableSignGui",                      false),
    DISABLE_SHULKER_BOX_TOOLTIP     ("disableShulkerBoxTooltip",            false),
    DISABLE_SLIME_BLOCK_SLOWDOWN    ("disableSlimeBlockSlowdown",           false),
    DISABLE_SOUNDS_ALL              ("disableSoundsAll",                    false),
    DISABLE_SOUNDS_LIST             ("disableSoundsList",                   false),
    DISABLE_TILE_ENTITY_RENDERING   ("disableTileEntityRendering",          false),
    DISABLE_TILE_ENTITY_TICKING     ("disableTileEntityTicking",            false),
    DISABLE_VILLAGER_TRADE_LOCKING  ("disableVillagerTradeLocking",         false),
    DISABLE_WALL_UNSPRINT           ("disableWallUnsprint",                 false);

    public static final ImmutableList<DisableToggle> VALUES = com.google.common.collect.ImmutableList.copyOf(values());
    public static final ImmutableList<BooleanConfig> TOGGLE_CONFIGS = com.google.common.collect.ImmutableList.copyOf(VALUES.stream().map(DisableToggle::getBooleanConfig).collect(Collectors.toList()));
    public static final ImmutableList<HotkeyConfig> TOGGLE_HOTKEYS = com.google.common.collect.ImmutableList.copyOf(VALUES.stream().map(DisableToggle::getHotkeyConfig).collect(Collectors.toList()));

    private final BooleanConfig toggleStatus;
    private final HotkeyConfig toggleHotkey;

    DisableToggle(String name, boolean defaultValue)
    {
        this(name, defaultValue, KeyBindSettings.INGAME_DEFAULT);
    }

    DisableToggle(String name, boolean defaultValue, KeyBindSettings settings)
    {
        this.toggleStatus = new BooleanConfig(name, defaultValue);
        this.toggleHotkey = new HotkeyConfig(name, "", settings);
        this.toggleHotkey.getKeyBind().setCallback(new ToggleBooleanWithMessageKeyCallback(this.toggleStatus));

        String nameLower = name.toLowerCase(Locale.ROOT);
        String nameKey = "tweakeroo.disable_toggle.name." + nameLower;
        this.toggleHotkey.setNameTranslationKey(nameKey).setPrettyNameTranslationKey(nameKey);
        this.toggleStatus.setNameTranslationKey(nameKey).setPrettyNameTranslationKey(nameKey);
        this.toggleStatus.setCommentTranslationKey("tweakeroo.disable_toggle.comment." + nameLower);
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
    public String getDisplayName()
    {
        return this.toggleStatus.getDisplayName();
    }

    @Nullable
    @Override
    public String getComment()
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
