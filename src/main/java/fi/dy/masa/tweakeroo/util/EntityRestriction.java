package fi.dy.masa.tweakeroo.util;

import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import fi.dy.masa.tweakeroo.Tweakeroo;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class EntityRestriction extends UsageRestriction<EntityType<?>>
{
    @Override
    protected void setValuesForList(Set<EntityType<?>> set, List<String> names)
    {
        for (String name : names)
        {
            try
            {
                Optional<EntityType<?>> entityType = Registry.ENTITY_TYPE.getOrEmpty(new Identifier(name));
                if (entityType.isPresent())
                {
                    set.add(entityType.get());
                    continue;
                }
            }
            catch (Exception ignore) {}

            Tweakeroo.logger.warn("Invalid entity name in a black- or whitelist: '{}'", name);
        }
    }
}
