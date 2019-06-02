package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

@Mixin(GlStateManager.class)
public abstract class MixinGlStateManager
{
    @ModifyVariable(method = "fogDensity", at = @At("HEAD"), argsOnly = true)
    private static float adjustFogDensity(float fogDensity)
    {
        // This is nessie's nice & clean lava visibility hack :P

        // In vanilla code, this method is only called with fogDensity = 2.0F when in lava.
        // We're changing the fog density in here to remain compatible with OptiFine.
        if (fogDensity == 2.0F &&
            FeatureToggle.TWEAK_LAVA_VISIBILITY.getBooleanValue() &&
            Configs.Generic.LAVA_VISIBILITY_OPTIFINE.getBooleanValue())
        {
            return RenderUtils.getLavaFog(Minecraft.getInstance().getRenderViewEntity(), fogDensity);
        }
        else
        {
            return fogDensity;
        }
    }
}
