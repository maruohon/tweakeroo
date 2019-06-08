package fi.dy.masa.tweakeroo.mixin;

import java.util.UUID;
import org.spongepowered.asm.mixin.Mixin;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(net.minecraft.client.gui.BossInfoClient.class)
public abstract class MixinBossInfoClient extends net.minecraft.world.BossInfo
{
    public MixinBossInfoClient(UUID uniqueIdIn, net.minecraft.util.text.ITextComponent nameIn, Color colorIn, Overlay overlayIn)
    {
        super(uniqueIdIn, nameIn, colorIn, overlayIn);
    }

    @Override
    public boolean shouldCreateFog()
    {
        if (Configs.Disable.DISABLE_BOSS_FOG.getBooleanValue())
        {
            return false;
        }

        return super.shouldCreateFog();
    }
}
