package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.inventory.SimpleInventory;

@Mixin(HorseBaseEntity.class)
public interface IMixinHorseBaseEntity
{
    @Accessor("items")
    SimpleInventory getHorseInventory();
}
