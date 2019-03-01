package fi.dy.masa.tweakeroo.util;

import java.util.List;
import java.util.Set;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import fi.dy.masa.tweakeroo.LiteModTweakeroo;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

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
                    Potion effect = Potion.REGISTRY.getObject(new ResourceLocation(name));

                    if (effect != null)
                    {
                        set.add(effect);
                    }
                }
            }
            catch (Exception e)
            {
                LiteModTweakeroo.logger.warn("Invalid potion effect name '{}'", name);
            }
        }
    }
}
