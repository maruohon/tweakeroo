package fi.dy.masa.tweakeroo.util;

import java.util.List;
import java.util.Set;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import fi.dy.masa.tweakeroo.Tweakeroo;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class PotionRestriction extends UsageRestriction<Potion>
{
    @Override
    protected void setValuesForList(Set<Potion> set, List<String> names)
    {
        for (String name : names)
        {
            try
            {
                if (name.isEmpty() == false)
                {
                    Potion effect = IRegistry.MOB_EFFECT.get(new ResourceLocation(name));

                    if (effect != null)
                    {
                        set.add(effect);
                    }
                }
            }
            catch (Exception e)
            {
                Tweakeroo.logger.warn("Invalid potion effect name '{}'", name);
            }
        }
    }
}
