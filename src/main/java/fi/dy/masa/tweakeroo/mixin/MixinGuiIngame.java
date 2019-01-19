package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame extends Gui
{
    @Inject(method = "renderAttackIndicator", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/settings/GameSettings;showDebugInfo:Z", ordinal = 0), cancellable = true)
    private void overrideCursorRender(float partialTicks, ScaledResolution sr, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_F3_CURSOR.getBooleanValue())
        {
            RenderUtils.renderDirectionsCursor(sr, this.zLevel, partialTicks);
            ci.cancel();
        }
    }
}
