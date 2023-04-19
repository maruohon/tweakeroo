package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;

import tweakeroo.config.Configs;
import tweakeroo.config.FeatureToggle;
import tweakeroo.util.MiscUtils;

@Mixin(GuiChat.class)
public abstract class MixinGuiChat
{
    @Shadow protected GuiTextField inputField;
    @Shadow private String defaultInputFieldText;

    @Inject(method = "onGuiClosed", at = @At("HEAD"))
    private void storeChatText(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_CHAT_PERSISTENT_TEXT.getBooleanValue())
        {
            MiscUtils.setLastChatText(this.inputField.getText());
        }
    }

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void restoreText(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_CHAT_PERSISTENT_TEXT.getBooleanValue() && MiscUtils.getLastChatText().isEmpty() == false)
        {
            this.defaultInputFieldText = MiscUtils.getLastChatText();
        }
    }

    @Inject(method = "<init>(Ljava/lang/String;)V", at = @At("RETURN"))
    private void restoreText(String defaultText, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_CHAT_PERSISTENT_TEXT.getBooleanValue() && MiscUtils.getLastChatText().isEmpty() == false)
        {
            this.defaultInputFieldText = MiscUtils.getLastChatText();
        }
    }

    @Inject(method = "keyTyped(CI)V",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiChat;sendChatMessage(Ljava/lang/String;)V")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V", shift = Shift.AFTER))
    private void onSendMessage(char typedChar, int keyCode, CallbackInfo ci)
    {
        MiscUtils.setLastChatText("");
    }

    @ModifyConstant(method = "drawScreen", constant = @Constant(intValue = Integer.MIN_VALUE))
    private int overrideChatBackgroundColor(int original)
    {
        if (FeatureToggle.TWEAK_CHAT_BACKGROUND_COLOR.getBooleanValue())
        {
            return Configs.Generic.CHAT_BACKGROUND_COLOR.getIntegerValue();
        }

        return original;
    }
}
