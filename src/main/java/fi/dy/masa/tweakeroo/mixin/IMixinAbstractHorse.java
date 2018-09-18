package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.inventory.ContainerHorseChest;

@Mixin(AbstractHorse.class)
public interface IMixinAbstractHorse
{
    @Accessor
    ContainerHorseChest getHorseChest();
}
