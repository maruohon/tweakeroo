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
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractCommandBlockScreen;
import net.minecraft.client.gui.CommandBlockScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;

@Mixin(CommandBlockScreen.class)
public abstract class MixinCommandBlockScreen extends AbstractCommandBlockScreen implements ICommandBlockGui
{
    @Shadow
    @Final
    private CommandBlockBlockEntity blockEntity;

    @Shadow private ButtonWidget modeButton;
    @Shadow private ButtonWidget conditionalModeButton;
    @Shadow private ButtonWidget redstoneTriggerButton;

    private TextFieldWidget textFieldName;
    private ButtonWidget buttonUpdateExec;
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

    @Inject(method = "init", at = @At("RETURN"))
    private void addExtraFields(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_COMMAND_BLOCK_EXTRA_FIELDS.getBooleanValue())
        {
            int x1 = this.width / 2 - 152;
            int x2 = x1 + 204;
            int y = 158;
            int width = 200;

            // Move the vanilla buttons a little bit tighter, otherwise the large GUI scale is a mess
            this.modeButton.y = y;
            this.conditionalModeButton.y = y;
            this.redstoneTriggerButton.y = y;

            y += 46;
            this.doneButton.y = y;
            this.cancelButton.y = y;

            String str = StringUtils.translate("tweakeroo.gui.button.misc.command_block.set_name");
            int widthBtn = this.font.getStringWidth(str) + 10;

            y = 181;
            this.textFieldName = new TextFieldWidget(this.font, x1, y, width, 20, "");
            this.textFieldName.setText(this.blockEntity.getCommandExecutor().getCustomName().getString());
            this.children.add(this.textFieldName);
            final TextFieldWidget tf = this.textFieldName;
            final BlockPos pos = this.blockEntity.getPos();
            final MinecraftClient mc = this.minecraft;
            final ICommandBlockGui gui = this;
            ClientPlayerEntity player = mc.player;

            this.addButton(new ButtonWidget(x2, y, widthBtn, 20, str, (button) ->
            {
                String name = tf.getText();
                name = String.format("{\"CustomName\":\"{\\\"text\\\":\\\"%s\\\"}\"}", name);
                player.sendChatMessage(String.format("/data merge block %d %d %d %s", pos.getX(), pos.getY(), pos.getZ(), name));
            }));

            this.updateExecValue = MiscUtils.getUpdateExec(this.blockEntity);

            str = getDisplayStringForCurrentStatus(this.updateExecValue);
            width = this.font.getStringWidth(str) + 10;

            this.buttonUpdateExec = new ButtonWidget(x2 + widthBtn + 4, y, width, 20, str, (button) ->
            {
                gui.setUpdateExec(! gui.getUpdateExec());
                String strBtn = getDisplayStringForCurrentStatus(gui.getUpdateExec());
                button.setMessage(strBtn);
                button.setWidth(this.font.getStringWidth(strBtn) + 10);

                String cmd = String.format("/data merge block %d %d %d {\"UpdateLastExecution\":%s}",
                        pos.getX(), pos.getY(), pos.getZ(), gui.getUpdateExec() ? "1b" : "0b");
                player.sendChatMessage(cmd);
            });

            this.addButton(this.buttonUpdateExec);
        }
    }

    // This is needed because otherwise the name updating is delayed by "one GUI opening" >_>
    @Inject(method = "tick", at = @At("RETURN"))
    public void onUpdateGui(CallbackInfo ci)
    {
        if (this.textFieldName != null)
        {
            this.textFieldName.setText(this.blockEntity.getCommandExecutor().getCustomName().getString());
        }

        if (this.buttonUpdateExec != null)
        {
            this.updateExecValue = MiscUtils.getUpdateExec(this.blockEntity);
            String str = getDisplayStringForCurrentStatus(this.updateExecValue);
            this.buttonUpdateExec.setMessage(str);
            this.buttonUpdateExec.setWidth(this.font.getStringWidth(str) + 10);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        super.render(mouseX, mouseY, partialTicks);

        if (this.textFieldName != null)
        {
            this.textFieldName.render(mouseX, mouseY, partialTicks);
        }

        if (this.buttonUpdateExec != null && this.buttonUpdateExec.isHovered())
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
