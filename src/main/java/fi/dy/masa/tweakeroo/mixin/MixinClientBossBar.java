package fi.dy.masa.tweakeroo.mixin;

import java.util.UUID;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(ClientBossBar.class)
public abstract class MixinClientBossBar extends BossBar
{
    public MixinClientBossBar(UUID uniqueIdIn, Text nameIn, BossBar.Color colorIn, BossBar.Style styleIn)
    {
        super(uniqueIdIn, nameIn, colorIn, styleIn);
    }

    @Override
    public boolean shouldThickenFog()
    {
        if (Configs.Disable.DISABLE_BOSS_FOG.getBooleanValue())
        {
            return false;
        }

        return super.shouldThickenFog();
    }
}
