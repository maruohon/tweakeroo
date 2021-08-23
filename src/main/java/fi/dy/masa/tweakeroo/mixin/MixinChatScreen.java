package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen
{
    @Shadow protected TextFieldWidget chatField;
    @Mutable @Shadow @Final private String originalChatText;

    @Inject(method = "removed", at = @At("HEAD"))
    private void storeChatText(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_CHAT_PERSISTENT_TEXT.getBooleanValue())
        {
            MiscUtils.setLastChatText(this.chatField.getText());
        }
    }

    @Inject(method = "<init>(Ljava/lang/String;)V", at = @At("RETURN"))
    private void restoreText(String defaultText, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_CHAT_PERSISTENT_TEXT.getBooleanValue() && MiscUtils.getLastChatText().isEmpty() == false)
        {
            this.originalChatText = MiscUtils.getLastChatText();
        }
    }

    @Inject(method = "keyPressed(III)Z",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;sendMessage(Ljava/lang/String;)V")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", shift = Shift.AFTER))
    private void onSendMessage(int keyCode, int scancode, int modifiers, CallbackInfoReturnable<Boolean> cir)
    {
        MiscUtils.setLastChatText("");
    }

    @ModifyConstant(method = "render", constant = @Constant(intValue = Integer.MIN_VALUE))
    private int overrideChatBackgroundColor(int original)
    {
        if (FeatureToggle.TWEAK_CHAT_BACKGROUND_COLOR.getBooleanValue())
        {
            return Configs.Generic.CHAT_BACKGROUND_COLOR.getIntegerValue();
        }

        return original;
    }
}
