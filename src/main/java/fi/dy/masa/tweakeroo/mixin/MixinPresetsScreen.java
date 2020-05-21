package fi.dy.masa.tweakeroo.mixin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.gen.feature.StructureFeature;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.MiscTweaks;

@Mixin(PresetsScreen.class)
public abstract class MixinPresetsScreen
{
    // name;blocks;biome;options;iconitem
    private static final Pattern PATTERN_PRESET = Pattern.compile("^(?<name>[a-zA-Z0-9_ -]+);(?<blocks>[a-z0-9_:\\.\\*-]+);(?<biome>[a-z0-9_:]+);(?<options>[a-z0-9_, \\(\\)=]*);(?<icon>[a-z0-9_:-]+)$");

    @Shadow
    @Final
    private static List<Object> presets;

    @Shadow
    private static void addPreset(Text name, ItemConvertible itemIn, Biome biomeIn, List<StructureFeature<?>> structures, boolean b1, boolean b2, boolean b3, FlatChunkGeneratorLayer... layers) {};

    @Inject(method = "init", at = @At("HEAD"))
    private void addCustomEntries(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_CUSTOM_FLAT_PRESETS.getBooleanValue())
        {
            int vanillaEntries = 9;
            int toRemove = presets.size() - vanillaEntries;

            for (int i = 0; i < toRemove; ++i)
            {
                presets.remove(0);
            }

            List<String> presetStrings = Configs.Lists.FLAT_WORLD_PRESETS.getStrings();

            for (int i = presetStrings.size() - 1; i >= 0; --i)
            {
                String str = presetStrings.get(i);

                if (this.registerPresetFromString(str) && presets.size() > vanillaEntries)
                {
                    Object o = presets.remove(presets.size() - 1);
                    presets.add(0, o);
                }
            }
        }
    }

    private boolean registerPresetFromString(String str)
    {
        Matcher matcher = PATTERN_PRESET.matcher(str);

        if (matcher.matches())
        {
            String name = matcher.group("name");
            String blocksString = matcher.group("blocks");
            String biomeName = matcher.group("biome");
            // TODO add back the features
            String iconItemName = matcher.group("icon");

            Biome biome = null;

            try
            {
                int biomeId = Integer.parseInt(biomeName);
                biome = Registry.BIOME.get(biomeId);
            }
            catch (Exception e)
            {
                try
                {
                    biome = Registry.BIOME.get(new Identifier(biomeName));
                }
                catch (Exception e2)
                {
                }
            }

            if (biome == null)
            {
                return false;
            }

            Item item = null;

            try
            {
                item = Registry.ITEM.get(new Identifier(iconItemName));
            }
            catch (Exception e)
            {
            }

            if (item == null)
            {
                return false;
            }

            FlatChunkGeneratorLayer[] layers = MiscTweaks.parseBlockString(blocksString);

            if (layers == null)
            {
                return false;
            }

            addPreset(new TranslatableText(name), item, biome, ImmutableList.of(), false, false, false, layers);

            return true;
        }

        return false;
    }
}
