package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import fi.dy.masa.tweakeroo.config.Configs;

/**
 * Separated out for Iris compatibility by adjusting the Mixin Priority
 */
@Mixin(value = GameRenderer.class, priority = 999)
public abstract class MixinGameRenderer_ViewBob
{
    @Shadow
    protected abstract void bobView(MatrixStack matrices, float tickDelta);

    @Redirect(method = "renderWorld", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/GameRenderer;bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
    private void disableWorldViewBob(GameRenderer renderer, MatrixStack matrices, float tickDelta)
    {
        if (Configs.Disable.DISABLE_WORLD_VIEW_BOB.getBooleanValue() == false)
        {
            this.bobView(matrices, tickDelta);
        }
    }
}
