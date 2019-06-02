package fi.dy.masa.tweakeroo.mixin;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.MiscTweaks;
import net.minecraft.client.gui.GuiFlatPresets;
import net.minecraft.init.Biomes;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatLayerInfo;

@Mixin(GuiFlatPresets.class)
public abstract class MixinGuiFlatPresets
{
    // name;blocks;biome;options;iconitem
    private static final Pattern PATTERN_PRESET = Pattern.compile("^(?<name>[a-zA-Z0-9_ -]+);(?<blocks>[a-z0-9_:\\.\\*-]+);(?<biome>[a-z0-9_:]+);(?<options>[a-z0-9_, \\(\\)=]*);(?<icon>[a-z0-9_:-]+)$");

    @Shadow
    @Final
    private static List<Object> FLAT_WORLD_PRESETS;

    @Shadow
    private static void addPreset(String name, IItemProvider itemIn, Biome biomeIn, List<String> options, FlatLayerInfo... layers) {};

    @Inject(method = "initGui", at = @At("HEAD"))
    private void addCustomEntries(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_CUSTOM_FLAT_PRESETS.getBooleanValue())
        {
            int vanillaEntries = 9;
            int toRemove = FLAT_WORLD_PRESETS.size() - vanillaEntries;

            for (int i = 0; i < toRemove; ++i)
            {
                FLAT_WORLD_PRESETS.remove(0);
            }

            List<String> presets = Configs.Lists.FLAT_WORLD_PRESETS.getStrings();

            for (int i = presets.size() - 1; i >= 0; --i)
            {
                String str = presets.get(i);

                if (this.registerPresetFromString(str) && FLAT_WORLD_PRESETS.size() > vanillaEntries)
                {
                    Object o = FLAT_WORLD_PRESETS.remove(FLAT_WORLD_PRESETS.size() - 1);
                    FLAT_WORLD_PRESETS.add(0, o);
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
            String options = matcher.group("options");
            String iconItemName = matcher.group("icon");

            Biome biome = null;

            try
            {
                int biomeId = Integer.parseInt(biomeName);
                biome = Biome.getBiome(biomeId, Biomes.PLAINS);
            }
            catch (Exception e)
            {
                try
                {
                    biome = IRegistry.BIOME.get(new ResourceLocation(biomeName));
                }
                catch (Exception e2)
                {
                }
            }

            if (biome == null)
            {
                return false;
            }

            List<String> features = Arrays.asList(options.split(","));
            Item item = null;

            try
            {
                item = IRegistry.ITEM.get(new ResourceLocation(iconItemName));
            }
            catch (Exception e)
            {
            }

            if (item == null)
            {
                return false;
            }

            FlatLayerInfo[] layers = MiscTweaks.parseBlockString(blocksString);

            if (layers == null)
            {
                return false;
            }

            addPreset(name, item, biome, features, layers);

            return true;
        }

        return false;
    }
}
