package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import fi.dy.masa.tweakeroo.config.Configs;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;

@Mixin(WorldClient.class)
public abstract class MixinWorldClient extends World
{
    protected MixinWorldClient(WorldSettings settings, DimensionType dimType, Profiler profiler)
    {
        super(new SaveHandlerMP(), new SaveDataMemoryStorage(), new WorldInfo(settings, "MpServer"), dimType.create(), profiler, true);
    }

    @Override
    public boolean checkLight(BlockPos pos)
    {
        if (Configs.Disable.DISABLE_LIGHT_UPDATES_ALL.getBooleanValue() == false)
        {
            return super.checkLight(pos);
        }

        return false;
    }

    @Override
    public void setLightFor(EnumLightType type, BlockPos pos, int lightValue)
    {
        if (Configs.Disable.DISABLE_LIGHT_UPDATES_ALL.getBooleanValue() == false)
        {
            super.setLightFor(type, pos, lightValue);
        }
    }

    @Override
    public void tickEntity(Entity entity)
    {
        if (Configs.Disable.DISABLE_CLIENT_ENTITY_UPDATES.getBooleanValue() == false || entity instanceof EntityPlayer)
        {
            super.tickEntity(entity);
        }
    }
}
