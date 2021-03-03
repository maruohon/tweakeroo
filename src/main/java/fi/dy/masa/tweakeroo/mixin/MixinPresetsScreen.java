package fi.dy.masa.tweakeroo.mixin;

import java.util.List;
import java.util.regex.Matcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.PresetsScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.gen.feature.StructureFeature;
import fi.dy.masa.tweakeroo.Tweakeroo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.MiscTweaks;
import fi.dy.masa.tweakeroo.util.MiscUtils;

@Mixin(PresetsScreen.class)
public abstract class MixinPresetsScreen
{
    @Shadow @Final private static List<Object> PRESETS;

    @Shadow
    private static void addPreset(Text name, ItemConvertible itemIn, RegistryKey<Biome> biomeIn, List<StructureFeature<?>> structures, boolean b1, boolean b2, boolean b3, FlatChunkGeneratorLayer... layers) {};

    @Inject(method = "init", at = @At("HEAD"))
    private void addCustomEntries(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_CUSTOM_FLAT_PRESETS.getBooleanValue())
        {
            int vanillaEntries = 9;
            int toRemove = PRESETS.size() - vanillaEntries;

            for (int i = 0; i < toRemove; ++i)
            {
                PRESETS.remove(0);
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
            catch (Exception ignore)
            {
            }

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
            catch (Exception ignore)
            {
            }

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

            addPreset(new TranslatableText(name), item, biome, ImmutableList.of(), false, false, false, layers);

            return true;
        }
        else
        {
            Tweakeroo.logger.error("Flat world preset string did not match the regex");
        }

        return false;
    }
}
