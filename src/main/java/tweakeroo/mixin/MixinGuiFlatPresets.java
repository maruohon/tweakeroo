package tweakeroo.mixin;

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

import net.minecraft.client.gui.GuiFlatPresets;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatLayerInfo;

import tweakeroo.config.Configs;
import tweakeroo.config.FeatureToggle;
import tweakeroo.tweaks.MiscTweaks;

@Mixin(GuiFlatPresets.class)
public abstract class MixinGuiFlatPresets
{
    // name;blocks;biome;options;iconitem
    private static final Pattern PATTERN_PRESET = Pattern.compile("^(?<name>[a-zA-Z0-9_ -]+);(?<blocks>[a-z0-9_:\\.\\*-]+);(?<biome>[a-z0-9_:]+);(?<options>[a-z0-9_, \\(\\)=]*);(?<icon>[a-z0-9_:-]+(?:@[0-9]+)?)$");

    @Shadow
    @Final
    private static List<Object> FLAT_WORLD_PRESETS;

    @Shadow
    private static void registerPreset(String name, Item icon, int iconMetadata, Biome biome, List<String> features, FlatLayerInfo... layers) {}

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

            List<String> presets = Configs.Lists.FLAT_WORLD_PRESETS.getValue();

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
            String iconString = matcher.group("icon");

            Biome biome = null;

            try
            {
                int biomeId = Integer.parseInt(biomeName);
                biome = Biome.getBiome(biomeId);
            }
            catch (Exception e)
            {
                biome = Biome.REGISTRY.getObject(new ResourceLocation(biomeName));
            }

            if (biome == null)
            {
                return false;
            }

            List<String> features = Arrays.asList(options.split(","));

            int index = iconString.indexOf("@");
            String iconItemName;
            int meta = 0;

            if (index != -1 && iconString.length() > index + 1)
            {
                iconItemName = iconString.substring(0, index);

                try
                {
                    meta = Integer.parseInt(iconString.substring(index + 1));
                }
                catch (Exception e)
                {
                    return false;
                }
            }
            else
            {
                iconItemName = iconString;
            }

            Item item = Item.REGISTRY.getObject(new ResourceLocation(iconItemName));

            if (item == null)
            {
                return false;
            }

            FlatLayerInfo[] layers = MiscTweaks.parseBlockString(blocksString);

            if (layers == null)
            {
                return false;
            }

            registerPreset(name, item, meta, biome, features, layers);

            return true;
        }

        return false;
    }
}
