package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.event.RenderEventHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;

@Mixin(GuiIngame.class)
public class MixinGuiIngame extends Gui
{
    @Inject(method = "renderGameOverlay", at = @At("RETURN"))
    public void onRenderGameOverlayPost(float partialTicks, CallbackInfo ci)
    {
        RenderEventHandler.onRenderGameOverlayPost(partialTicks);
    }
}
