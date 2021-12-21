package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(AbstractInventoryScreen.class)
public abstract class MixinAbstractInventoryScreen<T extends ScreenHandler> extends HandledScreen<T>
{
    private MixinAbstractInventoryScreen(T container, PlayerInventory playerInventory, Text textComponent)
    {
        super(container, playerInventory, textComponent);
    }

    @Inject(method = "drawStatusEffects", at = @At("HEAD"), cancellable = true)
    private void disableStatusEffectRendering(MatrixStack matrices, int mouseX, int mouseY, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_INVENTORY_EFFECTS.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
