package tweakeroo.mixin;

import java.util.UUID;
import org.spongepowered.asm.mixin.Mixin;

import tweakeroo.config.DisableToggle;

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
        if (DisableToggle.DISABLE_BOSS_FOG.getBooleanValue())
        {
            return false;
        }

        return super.shouldCreateFog();
    }
}
