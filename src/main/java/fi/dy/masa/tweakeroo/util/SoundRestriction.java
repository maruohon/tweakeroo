package fi.dy.masa.tweakeroo.util;

import java.util.List;
import java.util.Set;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.restriction.UsageRestriction;
import fi.dy.masa.tweakeroo.LiteModTweakeroo;

public class SoundRestriction extends UsageRestriction<net.minecraft.util.ResourceLocation>
{
    @Override
    protected void setValuesForList(Set<net.minecraft.util.ResourceLocation> set, List<String> names)
    {
        for (String name : names)
        {
            try
            {
                if (name.isEmpty() == false)
                {
                    net.minecraft.util.SoundEvent soundEvent = net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation(name));

                    if (soundEvent != null && soundEvent.getSoundName() != null)
                    {
                        set.add(soundEvent.getSoundName());
                        continue;
                    }
                }
            }
            catch (Exception e)
            {
            }

            LiteModTweakeroo.logger.warn(StringUtils.translate("tweakeroo.error.invalid_sound_blacklist_entry", name));
        }
    }
}
