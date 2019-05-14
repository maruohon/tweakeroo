package fi.dy.masa.tweakeroo.mixin;

import java.util.UUID;
import org.spongepowered.asm.mixin.Mixin;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;

@Mixin(BossInfoClient.class)
public abstract class MixinBossInfoClient extends BossInfo
{
    public MixinBossInfoClient(UUID uniqueIdIn, ITextComponent nameIn, Color colorIn, Overlay overlayIn)
    {
        super(uniqueIdIn, nameIn, colorIn, overlayIn);
    }

    @Override
    public boolean shouldCreateFog()
    {
        if (FeatureToggle.TWEAK_NO_BOSS_FOG.getBooleanValue())
        {
            return false;
        }

        return super.shouldCreateFog();
    }
}
