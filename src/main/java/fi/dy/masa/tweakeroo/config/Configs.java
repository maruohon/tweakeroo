package fi.dy.masa.tweakeroo.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mumfrey.liteloader.core.LiteLoader;
import fi.dy.masa.tweakeroo.LiteModTweakeroo;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.event.InputEventHandler;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import fi.dy.masa.tweakeroo.util.JsonUtils;
import fi.dy.masa.tweakeroo.util.PlacementTweaks;

public class Configs
{
    private static final String CONFIG_FILE_NAME = Reference.MOD_ID + ".json";

    public static void load()
    {
        File configFile = new File(LiteLoader.getCommonConfigFolder(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead())
        {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject())
            {
                JsonObject root = element.getAsJsonObject();
                JsonObject objTweakToggles      = JsonUtils.getNestedObject(root, "TweakToggles", false);
                JsonObject objTweakHotkeys      = JsonUtils.getNestedObject(root, "TweakHotkeys", false);
                JsonObject objGenericHotkeys    = JsonUtils.getNestedObject(root, "GenericHotkeys", false);
                JsonObject objGeneric           = JsonUtils.getNestedObject(root, "Generic", false);

                if (objGeneric != null)
                {
                    for (ConfigsGeneric gen : ConfigsGeneric.values())
                    {
                        if (objGeneric.has(gen.getName()) && objGeneric.get(gen.getName()).isJsonPrimitive())
                        {
                            gen.setValueFromJsonPrimitive(objGeneric.get(gen.getName()).getAsJsonPrimitive());
                        }
                    }
                }

                for (FeatureToggle toggle : FeatureToggle.values())
                {
                    if (objTweakToggles != null && JsonUtils.hasBoolean(objTweakToggles, toggle.getName()))
                    {
                        toggle.setBooleanValue(JsonUtils.getBoolean(objTweakToggles, toggle.getName()));
                    }

                    if (objTweakHotkeys != null && JsonUtils.hasString(objTweakHotkeys, toggle.getName()))
                    {
                        toggle.getKeybind().setKeysFromStorageString(JsonUtils.getString(objTweakHotkeys, toggle.getName()));
                    }
                }

                for (Hotkeys hotkey : Hotkeys.values())
                {
                    if (objGenericHotkeys != null && JsonUtils.hasString(objGenericHotkeys, hotkey.getName()))
                    {
                        hotkey.getKeybind().setKeysFromStorageString(JsonUtils.getString(objGenericHotkeys, hotkey.getName()));
                    }
                }
            }
        }

        InputEventHandler.getInstance().updateUsedKeys();
        PlacementTweaks.setFastPlacementModeFromConfigs();
        InventoryUtils.setUnstackingItems(ImmutableList.of("minecraft:bucket", "minecraft:glass_bottle")); // TODO add a string list config
    }

    public static void save()
    {
        File dir = LiteLoader.getCommonConfigFolder();

        if (dir.exists() && dir.isDirectory())
        {
            File configFile = new File(dir, CONFIG_FILE_NAME);
            FileWriter writer = null;
            JsonObject root = new JsonObject();
            JsonObject objTweakToggles      = JsonUtils.getNestedObject(root, "TweakToggles", true);
            JsonObject objTweakHotkeys      = JsonUtils.getNestedObject(root, "TweakHotkeys", true);
            JsonObject objGenericHotkeys    = JsonUtils.getNestedObject(root, "GenericHotkeys", true);
            JsonObject objGeneric           = JsonUtils.getNestedObject(root, "Generic", true);

            for (ConfigsGeneric gen : ConfigsGeneric.values())
            {
                objGeneric.add(gen.getName(), gen.getAsJsonPrimitive());
            }

            for (FeatureToggle toggle : FeatureToggle.values())
            {
                objTweakToggles.add(toggle.getName(), new JsonPrimitive(toggle.getBooleanValue()));
                objTweakHotkeys.add(toggle.getName(), new JsonPrimitive(toggle.getKeybind().getStorageString()));
            }

            for (Hotkeys hotkey : Hotkeys.values())
            {
                objGenericHotkeys.add(hotkey.getName(), new JsonPrimitive(hotkey.getKeybind().getStorageString()));
            }

            try
            {
                writer = new FileWriter(configFile);
                writer.write(JsonUtils.GSON.toJson(root));
                writer.close();
            }
            catch (IOException e)
            {
                LiteModTweakeroo.logger.warn("Failed to write configs to file '{}'", configFile.getAbsolutePath(), e);
            }
            finally
            {
                try
                {
                    if (writer != null)
                    {
                        writer.close();
                    }
                }
                catch (Exception e)
                {
                    LiteModTweakeroo.logger.warn("Failed to close config file", e);
                }
            }
        }
    }
}
