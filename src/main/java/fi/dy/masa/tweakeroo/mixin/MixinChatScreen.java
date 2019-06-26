package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import net.minecraft.client.gui.ingame.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen
{
    @Shadow
    protected TextFieldWidget chatField;
    @Shadow
    private String field_18973; // default text

    @Inject(method = "removed", at = @At("HEAD"))
    private void storeChatText(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_CHAT_PERSISTENT_TEXT.getBooleanValue())
        {
            MiscUtils.setLastChatText(this.chatField.getText());
        }
    }

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void restoreText(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_CHAT_PERSISTENT_TEXT.getBooleanValue())
        {
            this.field_18973 = MiscUtils.getLastChatText();
        }
    }

    @Inject(method = "<init>(Ljava/lang/String;)V", at = @At("RETURN"))
    private void restoreText(String defaultText, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_CHAT_PERSISTENT_TEXT.getBooleanValue())
        {
            this.field_18973 = MiscUtils.getLastChatText();
        }
    }

    @Inject(method = "keyPressed(III)V",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ingame/ChatScreen;sendMessage(Ljava/lang/String;)V")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/Screen;)V", shift = Shift.AFTER))
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
