package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(BackgroundRenderer.class)
public abstract class MixinBackgroundRenderer
{
    // FIXME 1.17 (also was already broken since ~1.15)
    /*
    @Inject(method = "applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZ)V", require = 0,
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/tag/FluidTags;LAVA:Lnet/minecraft/tag/Tag$Identified;")),
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;fogDensity(F)V",
                     ordinal = 0, shift = At.Shift.AFTER))
    private static void reduceLavaFog(
            net.minecraft.client.render.Camera camera,
            net.minecraft.client.render.BackgroundRenderer.FogType fogType,
            float viewDistance, boolean thickFog, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_LAVA_VISIBILITY.getBooleanValue() &&
            Configs.Generic.LAVA_VISIBILITY_OPTIFINE.getBooleanValue() == false)
        {
            RenderUtils.overrideLavaFog(net.minecraft.client.MinecraftClient.getInstance().getCameraEntity());
        }
    }
    */

    @Inject(method = "applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZ)V", require = 0,
            at = @At(value = "INVOKE", remap = false,
                     target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogEnd(F)V",
                     shift = At.Shift.AFTER))
    private static void disableRenderDistanceFog(
            Camera camera,
            BackgroundRenderer.FogType fogType,
            float viewDistance, boolean thickFog, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_RENDER_DISTANCE_FOG.getBooleanValue() && thickFog == false)
        {
            float distance = Math.max(512, MinecraftClient.getInstance().gameRenderer.getViewDistance());
            RenderSystem.setShaderFogStart(distance * 1.6F);
            RenderSystem.setShaderFogEnd(distance * 2.0F);
        }
    }
}
