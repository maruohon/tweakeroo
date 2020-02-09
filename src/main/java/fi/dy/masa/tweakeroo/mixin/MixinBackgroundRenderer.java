package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;

@Mixin(net.minecraft.client.render.BackgroundRenderer.class)
public abstract class MixinBackgroundRenderer
{
    @Shadow @Final private net.minecraft.client.render.GameRenderer gameRenderer;

    @Inject(method = "applyFog(Lnet/minecraft/client/render/Camera;I)V", require = 0,
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/tag/FluidTags;LAVA:Lnet/minecraft/tag/Tag;")),
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;fogDensity(F)V",
                     ordinal = 0, shift = At.Shift.AFTER))
    private void onSetupLavaFog(net.minecraft.client.render.Camera camera, int startCoords, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_LAVA_VISIBILITY.getBooleanValue() &&
            Configs.Generic.LAVA_VISIBILITY_OPTIFINE.getBooleanValue() == false)
        {
            RenderUtils.overrideLavaFog(net.minecraft.client.MinecraftClient.getInstance().getCameraEntity());
        }
    }

    @ModifyArg(method = "applyFog(Lnet/minecraft/client/render/Camera;I)V",
               slice = @Slice(
                       from = @At(value = "FIELD", target = "Lnet/minecraft/tag/FluidTags;LAVA:Lnet/minecraft/tag/Tag;"),
                       to = @At(value = "INVOKE", target = "Lnet/minecraft/world/dimension/Dimension;shouldRenderFog(II)Z")),
               at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;fogStart(F)V"))
    private float disableRenderDistanceFogStart(float original)
    {
        if (Configs.Disable.DISABLE_RENDER_DISTANCE_FOG.getBooleanValue())
        {
            return this.gameRenderer.getViewDistance() * 1.6F;
        }

        return original;
    }

    @ModifyArg(method = "applyFog(Lnet/minecraft/client/render/Camera;I)V",
               slice = @Slice(
                       from = @At(value = "FIELD", target = "Lnet/minecraft/tag/FluidTags;LAVA:Lnet/minecraft/tag/Tag;"),
                       to = @At(value = "INVOKE", target = "Lnet/minecraft/world/dimension/Dimension;shouldRenderFog(II)Z")),
               at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;fogEnd(F)V"))
    private float disableRenderDistanceFogEnd(float original)
    {
        if (Configs.Disable.DISABLE_RENDER_DISTANCE_FOG.getBooleanValue())
        {
            return this.gameRenderer.getViewDistance() * 2F;
        }

        return original;
    }
}
