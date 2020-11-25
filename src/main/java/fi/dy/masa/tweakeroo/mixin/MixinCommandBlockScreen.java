package fi.dy.masa.tweakeroo.mixin;

import java.util.Arrays;

import net.minecraft.client.gui.widget.CyclingButtonWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity.Type;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;

@Mixin(CommandBlockScreen.class)
public abstract class MixinCommandBlockScreen extends AbstractCommandBlockScreen
{
    @Shadow
    @Final
    private CommandBlockBlockEntity blockEntity;

    @Shadow private CyclingButtonWidget<Type> modeButton;
    @Shadow private CyclingButtonWidget<Boolean> conditionalModeButton;
    @Shadow private CyclingButtonWidget<Boolean> redstoneTriggerButton;

    private TextFieldWidget textFieldName;
    private CyclingButtonWidget<Boolean> buttonUpdateExec;
    private boolean updateExecValue;
    private String lastName = "";

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

            Text str = new TranslatableText("tweakeroo.gui.button.misc.command_block.set_name");
            int widthBtn = this.textRenderer.getWidth(str) + 10;

            y = 181;
            this.textFieldName = new TextFieldWidget(this.textRenderer, x1, y, width, 20, new LiteralText(""));
            this.textFieldName.setText(this.blockEntity.getCommandExecutor().getCustomName().getString());
            this.children.add(this.textFieldName);
            final TextFieldWidget tf = this.textFieldName;
            final BlockPos pos = this.blockEntity.getPos();

            this.addButton(new ButtonWidget(x2, y, widthBtn, 20, str, (button) ->
            {
                String name = tf.getText();
                name = String.format("{\"CustomName\":\"{\\\"text\\\":\\\"%s\\\"}\"}", name);
                this.client.player.sendChatMessage(String.format("/data merge block %d %d %d %s", pos.getX(), pos.getY(), pos.getZ(), name));
            }));

            this.updateExecValue = MiscUtils.getUpdateExec(this.blockEntity);

            Text strOn = new TranslatableText("tweakeroo.gui.button.misc.command_block.update_execution.on");
            Text strOff = new TranslatableText("tweakeroo.gui.button.misc.command_block.update_execution.off");
            width = this.textRenderer.getWidth(strOff) + 10;

            this.buttonUpdateExec = CyclingButtonWidget.method_32607(strOn, strOff).method_32616().value(this.updateExecValue).build(x2 + widthBtn + 4, y, width, 20, new TranslatableText("tweakeroo.gui.button.misc.command_block.update_execution.looping"), (button, boolean_) ->
            {
                this.updateExecValue = boolean_;
                MiscUtils.setUpdateExec(this.blockEntity, this.updateExecValue);

                //Text strBtn = getDisplayStringForCurrentStatus(this.updateExecValue);
                //button.setMessage(strBtn);
                //button.setWidth(this.textRenderer.getWidth(strBtn) + 10);

                String cmd = String.format("/data merge block %d %d %d {\"UpdateLastExecution\":%s}",
                        pos.getX(), pos.getY(), pos.getZ(), this.updateExecValue ? "1b" : "0b");
                this.client.player.sendChatMessage(cmd);
            });

            this.addButton(this.buttonUpdateExec);
        }
    }

    // This is needed because otherwise the name updating is delayed by "one GUI opening" >_>
    @Override
    public void tick()
    {
        super.tick();

        if (this.textFieldName != null)
        {
            String currentName = this.blockEntity.getCommandExecutor().getCustomName().getString();

            if (currentName.equals(this.lastName) == false)
            {
                this.textFieldName.setText(currentName);
                this.lastName = currentName;
            }
        }

        if (this.buttonUpdateExec != null)
        {
            boolean updateExec = MiscUtils.getUpdateExec(this.blockEntity);

            if (this.updateExecValue != updateExec)
            {
                this.updateExecValue = updateExec;
                Text str = getDisplayStringForCurrentStatus(this.updateExecValue);
                this.buttonUpdateExec.setMessage(str);
                this.buttonUpdateExec.setWidth(this.textRenderer.getWidth(str) + 10);
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (this.textFieldName != null)
        {
            this.textFieldName.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        if (this.buttonUpdateExec != null && this.buttonUpdateExec.isHovered())
        {
            String hover = "tweakeroo.gui.button.misc.command_block.hover.update_execution";
            RenderUtils.drawHoverText(mouseX, mouseY, Arrays.asList(StringUtils.translate(hover)), matrixStack);
        }
    }

    private static Text getDisplayStringForCurrentStatus(boolean updateExecValue)
    {
        String translationKey = "tweakeroo.gui.button.misc.command_block.update_execution";
        boolean isCurrentlyOn = ! updateExecValue;
        String strStatus = "malilib.gui.label_colored." + (isCurrentlyOn ? "on" : "off");
        return new TranslatableText(translationKey, StringUtils.translate(strStatus));
    }
}
