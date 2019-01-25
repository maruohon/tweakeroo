package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;

@Mixin(GuiEditSign.class)
public abstract class MixinGuiEditSign
{
    @Inject(method = "initGui", at = @At("HEAD"), cancellable = true)
    private void preventGuiOpen(CallbackInfo ci)
    {
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
