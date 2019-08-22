package fi.dy.masa.tweakeroo.event;

import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import fi.dy.masa.tweakeroo.tweaks.MiscTweaks;
import net.minecraft.client.MinecraftClient;

public class ClientTickHandler implements IClientTickHandler
{
    @Override
    public void onClientTick(MinecraftClient mc)
    {
        if (mc.world != null && mc.player != null)
        {
            MiscTweaks.onTick(mc);
        }
    }
}
