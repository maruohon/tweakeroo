package fi.dy.masa.tweakeroo.mixin;

import java.util.Arrays;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.ICommandBlockGui;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiCommandBlockBase;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;

@Mixin(GuiCommandBlock.class)
public abstract class MixinGuiCommandBlock extends GuiCommandBlockBase implements ICommandBlockGui
{
    @Shadow
    @Final
    private TileEntityCommandBlock commandBlock;

    @Shadow private GuiButton modeBtn;
    @Shadow private GuiButton conditionalBtn;
    @Shadow private GuiButton autoExecBtn;

    private GuiTextField textFieldName;
    private GuiButton buttonUpdateExec;
    private boolean updateExecValue;

    @Override
    public boolean getUpdateExec()
    {
        return this.updateExecValue;
    }

    @Override
    public void setUpdateExec(boolean update)
    {
        this.updateExecValue = update;
    }

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
            this.doneButton.y = y;
            this.cancelButton.y = y;

            String str = StringUtils.translate("tweakeroo.gui.button.misc.command_block.set_name");
            int widthBtn = this.fontRenderer.getStringWidth(str) + 10;

            y = 181;
            this.textFieldName = new GuiTextField(100, this.fontRenderer, x1, y, width, 20);
            this.textFieldName.setText(this.commandBlock.getCommandBlockLogic().getName().getString());
            this.children.add(this.textFieldName);
            final GuiTextField tf = this.textFieldName;
            final BlockPos pos = this.commandBlock.getPos();
            final Minecraft mc = this.mc;
            final ICommandBlockGui gui = this;
            EntityPlayerSP player = this.mc.player;

            this.addButton(new GuiButton(101, x2, y, widthBtn, 20, str)
            {
                @Override
                public void onClick(double mouseX, double mouseY)
                {
                    String name = tf.getText();
                    name = String.format("{\"CustomName\":\"{\\\"text\\\":\\\"%s\\\"}\"}", name);
                    player.sendChatMessage(String.format("/data merge block %d %d %d %s", pos.getX(), pos.getY(), pos.getZ(), name));
                }
            });

            this.updateExecValue = MiscUtils.getUpdateExec(this.commandBlock);

            str = getDisplayStringForCurrentStatus(this.updateExecValue);
            width = this.mc.fontRenderer.getStringWidth(str) + 10;

            this.buttonUpdateExec = new GuiButton(102, x2 + widthBtn + 4, y, width, 20, str)
            {
                @Override
                public void onClick(double mouseX, double mouseY)
                {
                    gui.setUpdateExec(! gui.getUpdateExec());
                    this.displayString = getDisplayStringForCurrentStatus(gui.getUpdateExec());
                    this.setWidth(mc.fontRenderer.getStringWidth(this.displayString) + 10);

                    String cmd = String.format("/data merge block %d %d %d {\"UpdateLastExecution\":%s}",
                            pos.getX(), pos.getY(), pos.getZ(), gui.getUpdateExec() ? "1b" : "0b");
                    player.sendChatMessage(cmd);
                }
            };

            this.addButton(this.buttonUpdateExec);
        }
    }

    // This is needed because otherwise the name updating is delayed by "one GUI opening" >_>
    @Inject(method = "updateGui", at = @At("RETURN"))
    public void onUpdateGui(CallbackInfo ci)
    {
        if (this.textFieldName != null)
        {
            this.textFieldName.setText(this.commandBlock.getCommandBlockLogic().getName().getString());
        }

        if (this.buttonUpdateExec != null)
        {
            this.updateExecValue = MiscUtils.getUpdateExec(this.commandBlock);
            this.buttonUpdateExec.displayString = getDisplayStringForCurrentStatus(this.updateExecValue);
            this.buttonUpdateExec.setWidth(this.mc.fontRenderer.getStringWidth(this.buttonUpdateExec.displayString) + 10);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        super.render(mouseX, mouseY, partialTicks);

        if (this.textFieldName != null)
        {
            this.textFieldName.drawTextField(mouseX, mouseY, partialTicks);
        }

        if (this.buttonUpdateExec != null && this.buttonUpdateExec.isMouseOver())
        {
            String hover = "tweakeroo.gui.button.misc.command_block.hover.update_execution";
            RenderUtils.drawHoverText(mouseX, mouseY, Arrays.asList(StringUtils.translate(hover)));
        }
    }

    private static String getDisplayStringForCurrentStatus(boolean updateExecValue)
    {
        String translationKey = "tweakeroo.gui.button.misc.command_block.update_execution";
        boolean isCurrentlyOn = ! updateExecValue;
        String strStatus = "malilib.gui.label_colored." + (isCurrentlyOn ? "on" : "off");
        return StringUtils.translate(translationKey, StringUtils.translate(strStatus));
    }
}
