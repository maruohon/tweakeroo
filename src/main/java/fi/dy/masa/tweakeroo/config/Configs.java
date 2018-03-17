package fi.dy.masa.tweakeroo.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mumfrey.liteloader.core.LiteLoader;
import fi.dy.masa.tweakeroo.LiteModTweakeroo;
import fi.dy.masa.tweakeroo.Reference;
import fi.dy.masa.tweakeroo.event.InputEventHandler;
import fi.dy.masa.tweakeroo.util.JsonUtils;

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
                JsonObject objToggles   = JsonUtils.getNestedObject(root, "TweakToggles", false);
                JsonObject objHotkeys   = JsonUtils.getNestedObject(root, "TweakHotkeys", false);
                JsonObject objGeneric   = JsonUtils.getNestedObject(root, "Generic", false);

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
                    if (objToggles != null && JsonUtils.hasBoolean(objToggles, toggle.getName()))
                    {
                        toggle.setBooleanValue(JsonUtils.getBoolean(objToggles, toggle.getName()));
                    }

                    if (objHotkeys != null && JsonUtils.hasString(objHotkeys, toggle.getName()))
                    {
                        toggle.getKeybind().setKeysFromStorageString(JsonUtils.getString(objHotkeys, toggle.getName()));
                    }
                }
            }
        }

        InputEventHandler.updateUsedKeys();
    }

    public static void save()
    {
        File dir = LiteLoader.getCommonConfigFolder();

        if (dir.exists() && dir.isDirectory())
        {
            File configFile = new File(dir, CONFIG_FILE_NAME);
            FileWriter writer = null;
            JsonObject root = new JsonObject();
            JsonObject objToggles   = JsonUtils.getNestedObject(root, "TweakToggles", true);
            JsonObject objHotkeys   = JsonUtils.getNestedObject(root, "TweakHotkeys", true);
            JsonObject objGeneric   = JsonUtils.getNestedObject(root, "Generic", true);

            for (ConfigsGeneric gen : ConfigsGeneric.values())
            {
                objGeneric.add(gen.getName(), gen.getAsJsonPrimitive());
            }

            for (FeatureToggle toggle : FeatureToggle.values())
            {
                objToggles.add(toggle.getName(), new JsonPrimitive(toggle.getBooleanValue()));
                objHotkeys.add(toggle.getName(), new JsonPrimitive(toggle.getKeybind().getStorageString()));
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
