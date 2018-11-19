package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

@Mixin(WorldClient.class)
public abstract class MixinWorldClient extends World
{
    protected MixinWorldClient(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client)
    {
        super(saveHandlerIn, info, providerIn, profilerIn, client);
    }

    @Override
    public boolean checkLight(BlockPos pos)
    {
        if (FeatureToggle.TWEAK_NO_LIGHT_UPDATES_ALL.getBooleanValue() == false)
        {
            return super.checkLight(pos);
        }

        return false;
    }

    @Override
    public void setLightFor(EnumSkyBlock type, BlockPos pos, int lightValue)
    {
        if (FeatureToggle.TWEAK_NO_LIGHT_UPDATES_ALL.getBooleanValue() == false)
        {
            super.setLightFor(type, pos, lightValue);
        }
    }

    @Override
    public void updateEntity(Entity entity)
    {
        if (FeatureToggle.TWEAK_NO_CLIENT_ENTITY_UPDATES.getBooleanValue() == false || entity instanceof EntityPlayer)
        {
            super.updateEntity(entity);
        }
    }
}
