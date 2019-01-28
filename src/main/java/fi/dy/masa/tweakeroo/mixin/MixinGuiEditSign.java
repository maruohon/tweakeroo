package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.tileentity.TileEntitySign;

@Mixin(GuiEditSign.class)
public abstract class MixinGuiEditSign
{
    @Shadow
    @Final
    private TileEntitySign tileSign;

    @Inject(method = "onGuiClosed", at = @At("HEAD"))
    private void storeText(CallbackInfo ci)
    {
        MiscUtils.copyTextFromSign(this.tileSign);
    }

    @Inject(method = "initGui", at = @At("HEAD"), cancellable = true)
    private void preventGuiOpen(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_SIGN_COPY.getBooleanValue())
        {
            MiscUtils.applyPreviousTextToSign(this.tileSign);
        }

        if (FeatureToggle.TWEAK_NO_SIGN_GUI.getBooleanValue())
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.displayGuiScreen(null);

            // Update the keybind state, because opening a GUI resets them all.
            // Also, KeyBinding.updateKeyBindState() only works for keyboard keys
            int keyCode = mc.gameSettings.keyBindUseItem.getKeyCode();
            KeyBinding.setKeyBindState(keyCode, KeybindMulti.isKeyDown(keyCode));

            ci.cancel();
        }
    }
}
