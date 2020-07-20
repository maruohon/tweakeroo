package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer
{
    @Redirect(method = "render",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isMainPlayer()Z"))
    private boolean overrideIsUser(AbstractClientPlayerEntity entity)
    {
        // This allows the real player entity to be rendered in the free camera mode
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && entity == MinecraftClient.getInstance().player)
        {
            return false;
        }

        return entity.isMainPlayer();
    }
}
