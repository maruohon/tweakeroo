package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer
{
    @Redirect(method = "doRender", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/entity/AbstractClientPlayer;isUser()Z"))
    private boolean overrideIsUser(AbstractClientPlayer entity)
    {
        // This allows the real player entity to be rendered in the free camera mode
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && entity == Minecraft.getInstance().player)
        {
            return false;
        }

        return entity.isUser();
    }
}
