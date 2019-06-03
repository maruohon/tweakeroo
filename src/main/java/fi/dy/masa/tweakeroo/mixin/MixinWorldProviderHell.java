package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderHell;

@Mixin(WorldProviderHell.class)
public abstract class MixinWorldProviderHell extends WorldProvider
{
    @Override
    public boolean doesXZShowFog(int x, int z)
    {
        return ! Configs.Disable.DISABLE_NETHER_FOG.getBooleanValue();
    }
}
