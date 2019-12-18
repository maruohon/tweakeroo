package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(AbstractInventoryScreen.class)
public abstract class MixinAbstractPlayerInventoryScreen<T extends Container> extends AbstractContainerScreen<T>
{
    @Shadow protected boolean offsetGuiForEffects;

    public MixinAbstractPlayerInventoryScreen(T container, PlayerInventory playerInventory, Text textComponent)
    {
        super(container, playerInventory, textComponent);
    }

    @Inject(method = "method_2476", at = @At("HEAD"), cancellable = true) // updateActivePotionEffect
    private void disableEffectRendering(CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_INVENTORY_EFFECTS.getBooleanValue())
        {
            this.left = (this.width - this.containerWidth) / 2;
            this.offsetGuiForEffects = false;
            ci.cancel();
        }
    }
}
