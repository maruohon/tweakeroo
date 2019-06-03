package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.IGuiEditSign;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraft.tileentity.TileEntitySign;

@Mixin(GuiEditSign.class)
public abstract class MixinGuiEditSign extends GuiScreen implements IGuiEditSign
{
    @Shadow
    @Final
    private TileEntitySign tileSign;

    @Override
    public TileEntitySign getTile()
    {
        return this.tileSign;
    }

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

        if (Configs.Disable.DISABLE_SIGN_GUI.getBooleanValue())
        {
            this.close();

            // Update the keybind state, because opening a GUI resets them all.
            // Also, KeyBinding.updateKeyBindState() only works for keyboard keys
            KeyBinding keybind = Minecraft.getInstance().gameSettings.keyBindUseItem;
            Input input = InputMappings.getInputByName(keybind.getTranslationKey());

            if (input != null)
            {
                KeyBinding.setKeyBindState(input, KeybindMulti.isKeyDown(KeybindMulti.getKeyCode(keybind)));
            }

            ci.cancel();
        }
    }
}
