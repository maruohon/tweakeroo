package fi.dy.masa.tweakeroo.util;

import java.util.List;
import java.util.Set;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import fi.dy.masa.tweakeroo.Tweakeroo;

public class PotionRestriction extends UsageRestriction<StatusEffect>
{
    @Override
    protected void setValuesForList(Set<StatusEffect> set, List<String> names)
    {
        for (String name : names)
        {
            Identifier rl = null;

            try
            {
                rl = new Identifier(name);
            }
            catch (Exception e)
            {
            }

            StatusEffect effect = rl != null ? Registry.STATUS_EFFECT.get(rl) : null;

            if (effect != null)
            {
                set.add(effect);
            }
            else
            {
                Tweakeroo.logger.warn("Invalid potion effect name '{}'", name);
            }
        }
    }
}
