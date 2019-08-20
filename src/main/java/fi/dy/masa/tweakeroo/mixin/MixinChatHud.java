package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;

@Mixin(net.minecraft.client.gui.hud.ChatHud.class)
public abstract class MixinChatHud extends net.minecraft.client.gui.DrawableHelper
{
    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/TextComponent;I)V", at = @At("HEAD"), argsOnly = true)
    private net.minecraft.text.TextComponent addTimestamp(net.minecraft.text.TextComponent componentIn)
    {
        if (FeatureToggle.TWEAK_CHAT_TIMESTAMP.getBooleanValue())
        {
            net.minecraft.text.TextComponent newComponent = new net.minecraft.text.StringTextComponent(MiscUtils.getChatTimestamp() + " ");
            newComponent.append(componentIn);
            return newComponent;
        }

        return componentIn;
    }

    @Redirect(method = "draw", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/gui/hud/ChatHud;fill(IIIII)V", ordinal = 0))
    private void overrideChatBackgroundColor(int left, int top, int right, int bottom, int color)
    {
        if (FeatureToggle.TWEAK_CHAT_BACKGROUND_COLOR.getBooleanValue())
        {
            color = MiscUtils.getChatBackgroundColor(color);
        }

        fill(left, top, right, bottom, color);
    }
}
