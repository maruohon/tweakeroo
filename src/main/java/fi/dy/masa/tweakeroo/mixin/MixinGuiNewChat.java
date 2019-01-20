package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat
{
    @ModifyVariable(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"))
    private ITextComponent addTimestamp(ITextComponent componentIn)
    {
        if (FeatureToggle.TWEAK_CHAT_TIMESTAMP.getBooleanValue())
        {
            ITextComponent newComponent = new TextComponentString(MiscUtils.getChatTimestamp() + " ");
            newComponent.appendSibling(componentIn);
            return newComponent;
        }

        return componentIn;
    }
}
