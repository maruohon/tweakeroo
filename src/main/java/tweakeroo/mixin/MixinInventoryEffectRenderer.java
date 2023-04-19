package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.Container;

import tweakeroo.config.DisableToggle;

@Mixin(InventoryEffectRenderer.class)
public abstract class MixinInventoryEffectRenderer extends GuiContainer
{
    @Shadow protected boolean hasActivePotionEffects;

    public MixinInventoryEffectRenderer(Container inventorySlotsIn)
    {
        super(inventorySlotsIn);
    }

    @Inject(method = "updateActivePotionEffects", at = @At("HEAD"), cancellable = true)
    private void disableEffectRendering(CallbackInfo ci)
    {
        if (DisableToggle.DISABLE_INVENTORY_EFFECTS.getBooleanValue())
        {
            this.guiLeft = (this.width - this.xSize) / 2;
            this.hasActivePotionEffects = false;
            ci.cancel();
        }
    }
}
