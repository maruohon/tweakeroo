package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;

@Mixin(ChatHud.class)
public abstract class MixinChatHud extends DrawableHelper
{
    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/TextComponent;I)V", at = @At("HEAD"), argsOnly = true)
    private TextComponent addTimestamp(TextComponent componentIn)
    {
        if (FeatureToggle.TWEAK_CHAT_TIMESTAMP.getBooleanValue())
        {
            TextComponent newComponent = new StringTextComponent(MiscUtils.getChatTimestamp() + " ");
            newComponent.append(componentIn);
            return newComponent;
        }

        return componentIn;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/gui/DrawableHelper;fill(IIIII)V", ordinal = 0))
    private void overrideChatBackgroundColor(int left, int top, int right, int bottom, int color)
    {
        if (FeatureToggle.TWEAK_CHAT_BACKGROUND_COLOR.getBooleanValue())
        {
            color = MiscUtils.getChatBackgroundColor(color);
        }

        fill(left, top, right, bottom, color);
    }
}
