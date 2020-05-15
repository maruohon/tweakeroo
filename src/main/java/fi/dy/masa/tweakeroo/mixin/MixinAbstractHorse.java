package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.inventory.ContainerHorseChest;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(AbstractHorse.class)
public class MixinAbstractHorse
{
    @Shadow protected ContainerHorseChest horseChest;

    @Redirect(method = "travel", require = 0, at = @At(value = "INVOKE",
              target = "Lnet/minecraft/entity/passive/AbstractHorse;isHorseSaddled()Z"))
    public boolean spoofIsSaddled(AbstractHorse entity)
    {
        if (FeatureToggle.TWEAK_LLAMA_STEERING.getBooleanValue() &&
            (Object) this instanceof EntityLlama &&
            ((EntityLlama)(Object) this).hasColor()) // The only way to know on the client that the Llama has a Carpet
        {
            return true;
        }

        return entity.isHorseSaddled();
    }
}
