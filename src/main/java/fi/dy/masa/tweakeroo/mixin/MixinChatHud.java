package fi.dy.masa.tweakeroo.mixin;

import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;

@Mixin(value = ChatHud.class, priority = 1100)
public abstract class MixinChatHud
{
    @ModifyVariable(method = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
                    at = @At("HEAD"), argsOnly = true)
    private Text tweakeroo_addMessageTimestamp(Text componentIn, Text parameterMessage, MessageSignatureData data, MessageIndicator indicator)
    {
        if (FeatureToggle.TWEAK_CHAT_TIMESTAMP.getBooleanValue())
        {
            MutableText newComponent = Text.literal(MiscUtils.getChatTimestamp() + " ");
            newComponent.append(componentIn);
            return newComponent;
        }

        return componentIn;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 0))
    private void overrideChatBackgroundColor(DrawContext drawableHelper, int left, int top, int right, int bottom, int color)
    {
        if (FeatureToggle.TWEAK_CHAT_BACKGROUND_COLOR.getBooleanValue())
        {
            color = MiscUtils.getChatBackgroundColor(color);
        }

        drawableHelper.fill(left, top, right, bottom, color);
    }
}
