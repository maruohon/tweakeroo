package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;

import malilib.util.game.wrap.GameUtils;
import tweakeroo.config.FeatureToggle;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer
{
    @Redirect(method = "doRender", require = 0, at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/entity/AbstractClientPlayer;isUser()Z"))
    private boolean overrideIsUser(AbstractClientPlayer entity)
    {
        // This allows the real player entity to be rendered in the free camera mode
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && entity == GameUtils.getClientPlayer())
        {
            return false;
        }

        return entity.isUser();
    }
}
