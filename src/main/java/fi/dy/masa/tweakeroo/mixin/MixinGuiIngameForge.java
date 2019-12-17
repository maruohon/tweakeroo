package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.GuiIngameForge;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(GuiIngameForge.class)
public abstract class MixinGuiIngameForge extends GuiIngame
{
    private MixinGuiIngameForge(Minecraft mcIn)
    {
        super(mcIn);
    }

    @Shadow
    private boolean pre(net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType type) { return false; }

    @Shadow
    private void post(net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType type) {}

    @Inject(method = "renderPlayerList",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;updatePlayerList(Z)V",
                     ordinal = 1, shift = At.Shift.AFTER))
    private void alwaysRenderPlayerList(int width, int height, CallbackInfo ci)
    {
        ScoreObjective scoreobjective = this.mc.world.getScoreboard().getObjectiveInDisplaySlot(0);
        NetHandlerPlayClient handler = this.mc.player.connection;

        if (FeatureToggle.TWEAK_PLAYER_LIST_ALWAYS_ON.getBooleanValue() == false ||
            (this.mc.gameSettings.keyBindPlayerList.isKeyDown() && (! this.mc.isIntegratedServerRunning() || handler.getPlayerInfoMap().size() > 1 || scoreobjective != null)))
        {
            return;
        }

        Scoreboard scoreboard = this.mc.world.getScoreboard();
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(0);

        this.overlayPlayerList.updatePlayerList(true);

        if (this.pre(net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.PLAYER_LIST)) return;

        this.overlayPlayerList.renderPlayerlist(width, scoreboard, objective);

        this.post(net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.PLAYER_LIST);
    }
}
