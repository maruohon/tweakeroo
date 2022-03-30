package fi.dy.masa.tweakeroo.input;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.HotkeyCategory;
import fi.dy.masa.malilib.input.HotkeyProvider;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.DisableToggle;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;

public class TweakerooHotkeyProvider implements HotkeyProvider
{
    public static final TweakerooHotkeyProvider INSTANCE = new TweakerooHotkeyProvider();

    @Override
    public List<? extends Hotkey> getAllHotkeys()
    {
        ImmutableList.Builder<Hotkey> builder = ImmutableList.builder();

        builder.addAll(Configs.Generic.HOTKEY_LIST);
        builder.addAll(Hotkeys.HOTKEY_LIST);
        builder.addAll(FeatureToggle.TOGGLE_HOTKEYS);
        builder.addAll(DisableToggle.TOGGLE_HOTKEYS);

        return builder.build();
    }

    @Override
    public List<HotkeyCategory> getHotkeysByCategories()
    {
        return ImmutableList.of(
                new HotkeyCategory(Reference.MOD_INFO, "tweakeroo.hotkeys.category.generic", Configs.Generic.HOTKEY_LIST),
                new HotkeyCategory(Reference.MOD_INFO, "tweakeroo.hotkeys.category.generic_hotkeys", Hotkeys.HOTKEY_LIST),
                new HotkeyCategory(Reference.MOD_INFO, "tweakeroo.hotkeys.category.disable_toggle_hotkeys", DisableToggle.TOGGLE_HOTKEYS),
                new HotkeyCategory(Reference.MOD_INFO, "tweakeroo.hotkeys.category.tweak_toggle_hotkeys", FeatureToggle.TOGGLE_HOTKEYS)
        );
    }
}
