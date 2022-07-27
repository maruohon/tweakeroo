package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.util.math.MatrixStack;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(BossBarHud.class)
public abstract class MixinBossBarHud
{
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void tweakeroo_disableBossBarRendering(MatrixStack matrices, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_BOSS_BAR.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
