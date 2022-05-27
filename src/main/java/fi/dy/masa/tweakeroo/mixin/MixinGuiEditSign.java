package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.tileentity.TileEntitySign;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.input.Keys;
import fi.dy.masa.malilib.util.game.wrap.GameUtils;
import fi.dy.masa.tweakeroo.config.DisableToggle;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;

@Mixin(GuiEditSign.class)
public abstract class MixinGuiEditSign
{
    @Shadow @Final private TileEntitySign tileSign;

    @Inject(method = "onGuiClosed", at = @At("HEAD"))
    private void storeText(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_SIGN_COPY.getBooleanValue())
        {
            MiscUtils.copyTextFromSign(this.tileSign);
        }
    }

    @Inject(method = "initGui", at = @At("HEAD"), cancellable = true)
    private void preventGuiOpen(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_SIGN_COPY.getBooleanValue())
        {
            MiscUtils.applyPreviousTextToSign(this.tileSign);
        }

        if (DisableToggle.DISABLE_SIGN_GUI.getBooleanValue())
        {
            BaseScreen.openScreen(null);

            // Update the keybind state, because opening a GUI resets them all.
            // Also, KeyBinding.updateKeyBindState() only works for keyboard keys
            int keyCode = GameUtils.getClient().gameSettings.keyBindUseItem.getKeyCode();
            KeyBinding.setKeyBindState(keyCode, Keys.isKeyDown(keyCode));

            ci.cancel();
        }
    }
}
