package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;

@Mixin(value = ChatHud.class, priority = 1100)
public abstract class MixinChatHud extends net.minecraft.client.gui.DrawableHelper
{
    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
                    at = @At("HEAD"), argsOnly = true)
    private Text tweakeroo_addMessageTimestamp(Text componentIn)
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
                target = "Lnet/minecraft/client/gui/hud/ChatHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V", ordinal = 0))
    private void overrideChatBackgroundColor(net.minecraft.client.util.math.MatrixStack matrixStack, int left, int top, int right, int bottom, int color)
    {
        if (FeatureToggle.TWEAK_CHAT_BACKGROUND_COLOR.getBooleanValue())
        {
            color = MiscUtils.getChatBackgroundColor(color);
        }

        fill(matrixStack, left, top, right, bottom, color);
    }
}
