package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat
{
    @ModifyVariable(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"))
    private net.minecraft.util.text.ITextComponent addTimestamp(net.minecraft.util.text.ITextComponent componentIn)
    {
        if (FeatureToggle.TWEAK_CHAT_TIMESTAMP.getBooleanValue())
        {
            net.minecraft.util.text.TextComponentString newComponent = new net.minecraft.util.text.TextComponentString(MiscUtils.getChatTimestamp() + " ");
            newComponent.appendSibling(componentIn);
            return newComponent;
        }

        return componentIn;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 0))
    private void overrideChatBackgroundColor(int left, int top, int right, int bottom, int color)
    {
        if (FeatureToggle.TWEAK_CHAT_BACKGROUND_COLOR.getBooleanValue())
        {
            color = MiscUtils.getChatBackgroundColor(color);
        }

        Gui.drawRect(left, top, right, bottom, color);
    }
}
