package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.screen.PresetsScreen;

@Mixin(PresetsScreen.class)
public abstract class MixinPresetsScreen
{
    /*
    @Inject(method = "init", at = @At("HEAD"))
    private void addCustomEntries(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_CUSTOM_FLAT_PRESETS.getBooleanValue())
        {
            int vanillaEntries = 9;
            int toRemove = PRESETS.size() - vanillaEntries;

            if (toRemove > 0)
            {
                PRESETS.subList(0, toRemove).clear();
            }

            List<String> presetStrings = Configs.Lists.FLAT_WORLD_PRESETS.getStrings();

            for (int i = presetStrings.size() - 1; i >= 0; --i)
            {
                String str = presetStrings.get(i);

                if (this.registerPresetFromString(str) && PRESETS.size() > vanillaEntries)
                {
                    Object o = PRESETS.remove(PRESETS.size() - 1);
                    PRESETS.add(0, o);
                }
            }
        }
    }

    private boolean registerPresetFromString(String str)
    {
        Matcher matcher = MiscUtils.PATTERN_WORLD_PRESET.matcher(str);

        if (matcher.matches())
        {
            String name = matcher.group("name");
            String blocksString = matcher.group("blocks");
            String biomeName = matcher.group("biome");
            // TODO add back the features
            String iconItemName = matcher.group("icon");

            RegistryKey<Biome> biome = null;

            try
            {
                biome = RegistryKey.of(Registry.BIOME_KEY, new Identifier(biomeName));
            }
            catch (Exception ignore) {}

            if (biome == null)
            {
                Tweakeroo.logger.error("Invalid biome while parsing flat world string: '{}'", biomeName);
                return false;
            }

            Item item = null;

            try
            {
                item = Registry.ITEM.get(new Identifier(iconItemName));
            }
            catch (Exception ignore) {}

            if (item == null)
            {
                Tweakeroo.logger.error("Invalid item for icon while parsing flat world string: '{}'", iconItemName);
                return false;
            }

            FlatChunkGeneratorLayer[] layers = MiscTweaks.parseBlockString(blocksString);

            if (layers == null)
            {
                Tweakeroo.logger.error("Failed to get the layers for the flat world preset");
                return false;
            }
            //new PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry(null);

            //addPreset(Text.translatable(name), item, biome, ImmutableSet.of(), false, false, layers);

            return true;
        }
        else
        {
            Tweakeroo.logger.error("Flat world preset string did not match the regex");
        }

        return false;
    }
    */
}
