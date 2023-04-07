package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import malilib.render.RenderContext;
import malilib.render.TextRenderUtils;
import malilib.render.text.StyledText;
import malilib.util.StringUtils;
import tweakeroo.config.FeatureToggle;
import tweakeroo.util.MiscUtils;

@Mixin(net.minecraft.client.gui.GuiCommandBlock.class)
public abstract class MixinGuiCommandBlock extends net.minecraft.client.gui.GuiScreen
{
    @Shadow @Final private net.minecraft.tileentity.TileEntityCommandBlock commandBlock;

    @Shadow private net.minecraft.client.gui.GuiButton doneBtn;
    @Shadow private net.minecraft.client.gui.GuiButton cancelBtn;
    @Shadow private net.minecraft.client.gui.GuiButton modeBtn;
    @Shadow private net.minecraft.client.gui.GuiButton conditionalBtn;
    @Shadow private net.minecraft.client.gui.GuiButton autoExecBtn;

    private net.minecraft.client.gui.GuiTextField textFieldName;
    private net.minecraft.client.gui.GuiButton buttonUpdateExec;
    private boolean updateExecValue;

    @Inject(method = "initGui", at = @At("RETURN"))
    private void addExtraFields(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_COMMAND_BLOCK_EXTRA_FIELDS.getBooleanValue())
        {
            int x1 = this.width / 2 - 152;
            int x2 = x1 + 204;
            int y = 158;
            int width = 200;

            // Move the vanilla buttons a little bit tighter, otherwise the large GUI scale is a mess
            this.modeBtn.y = y;
            this.conditionalBtn.y = y;
            this.autoExecBtn.y = y;

            y += 46;
            this.doneBtn.y = y;
            this.cancelBtn.y = y;

            String str = StringUtils.translate("tweakeroo.gui.button.misc.command_block.set_name");
            int widthBtn = this.fontRenderer.getStringWidth(str) + 10;

            y = 181;
            this.textFieldName = new net.minecraft.client.gui.GuiTextField(100, this.fontRenderer, x1, y, width, 20);
            this.textFieldName.setText(this.commandBlock.getCommandBlockLogic().getName());

            this.addButton(new net.minecraft.client.gui.GuiButton(101, x2, y, widthBtn, 20, str));

            this.updateExecValue = MiscUtils.getUpdateExec(this.commandBlock);

            str = this.getDisplayStringForCurrentStatus();
            width = this.mc.fontRenderer.getStringWidth(str) + 10;
            this.buttonUpdateExec = new net.minecraft.client.gui.GuiButton(102, x2 + widthBtn + 4, y, width, 20, str);

            this.addButton(this.buttonUpdateExec);
        }
    }

    // This is needed because otherwise the name updating is delayed by "one GUI opening" >_>
    @Inject(method = "updateGui", at = @At("RETURN"))
    private void onUpdateGui(CallbackInfo ci)
    {
        if (this.textFieldName != null)
        {
            this.textFieldName.setText(this.commandBlock.getCommandBlockLogic().getName());
        }

        if (this.buttonUpdateExec != null)
        {
            this.updateExecValue = MiscUtils.getUpdateExec(this.commandBlock);
            this.buttonUpdateExec.displayString = this.getDisplayStringForCurrentStatus();
            this.buttonUpdateExec.setWidth(this.mc.fontRenderer.getStringWidth(this.buttonUpdateExec.displayString) + 10);
        }
    }

    @Inject(method = "keyTyped", at = @At("RETURN"))
    private void onKeyTyped(char typedChar, int keyCode, CallbackInfo ci)
    {
        if (this.textFieldName != null)
        {
            this.textFieldName.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Inject(method = "mouseClicked", at = @At("RETURN"))
    private void onMouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci)
    {
        if (this.textFieldName != null)
        {
            this.textFieldName.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci)
    {
        if (this.textFieldName != null)
        {
            this.textFieldName.drawTextBox();
        }

        if (this.buttonUpdateExec != null && this.buttonUpdateExec.isMouseOver())
        {
            StyledText hover = StyledText.translate("tweakeroo.gui.button.misc.command_block.hover.update_execution");
            TextRenderUtils.renderStyledHoverText(mouseX, mouseY, 1, hover, RenderContext.DUMMY);
        }
    }

    @Inject(method = "actionPerformed", at = @At("RETURN"))
    private void handleButtons(net.minecraft.client.gui.GuiButton button, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_COMMAND_BLOCK_EXTRA_FIELDS.getBooleanValue() && button.enabled)
        {
            if (this.mc.player != null)
            {
                net.minecraft.util.math.BlockPos pos = this.commandBlock.getPos();

                // Set name
                if (button.id == 101 && this.textFieldName != null)
                {
                    String name = this.textFieldName.getText();
                    this.mc.player.sendChatMessage(String.format("/blockdata %d %d %d {\"CustomName\":\"%s\"}", pos.getX(), pos.getY(), pos.getZ(), name));
                }
                // Toggle Update Last Execution
                else if (button.id == 102 && this.buttonUpdateExec != null)
                {
                    this.updateExecValue = ! this.updateExecValue;
                    this.buttonUpdateExec.displayString = this.getDisplayStringForCurrentStatus();
                    this.buttonUpdateExec.setWidth(this.mc.fontRenderer.getStringWidth(this.buttonUpdateExec.displayString) + 10);

                    String cmd = String.format("/blockdata %d %d %d {\"UpdateLastExecution\":%s}",
                            pos.getX(), pos.getY(), pos.getZ(), this.updateExecValue ? "1b" : "0b");
                    this.mc.player.sendChatMessage(cmd);
                }
            }
        }
    }

    private String getDisplayStringForCurrentStatus()
    {
        String translationKey = "tweakeroo.gui.button.misc.command_block.update_execution";
        boolean isCurrentlyOn = ! this.updateExecValue;
        String strStatus = "malilib.gui.label_colored." + (isCurrentlyOn ? "on" : "off");
        return StringUtils.translate(translationKey, StringUtils.translate(strStatus));
    }
}
