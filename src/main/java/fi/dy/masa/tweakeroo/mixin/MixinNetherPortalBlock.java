package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(NetherPortalBlock.class)
public abstract class MixinNetherPortalBlock
{
    @Redirect(method = "randomDisplayTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"))
    private void tweakeroo_disablePortalSound(World instance, double x, double y, double z, SoundEvent sound,
                                              SoundCategory category, float volume, float pitch, boolean useDistance)
    {
        if (Configs.Disable.DISABLE_NETHER_PORTAL_SOUND.getBooleanValue() == false)
        {
            instance.playSound(x, y, z, sound, category, volume, pitch, useDistance);
        }
    }
}
