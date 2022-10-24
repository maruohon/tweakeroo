package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import malilib.gui.util.GuiUtils;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.DisableToggle;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;

@Mixin(net.minecraft.client.gui.GuiIngame.class)
public abstract class MixinGuiIngame extends net.minecraft.client.gui.Gui
{
    @Shadow @Final private net.minecraft.client.gui.GuiPlayerTabOverlay overlayPlayerList;
    @Shadow @Final private net.minecraft.client.Minecraft mc;

    @Redirect(method = "renderHotbar", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/client/Minecraft;getRenderViewEntity()Lnet/minecraft/entity/Entity;"))
    private net.minecraft.entity.Entity returnRealPlayer(net.minecraft.client.Minecraft mc)
    {
        // Fix the hotbar rendering in the Free Camera mode by using the actual player
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() &&
            Configs.Generic.FREE_CAMERA_PLAYER_MOVEMENT.getBooleanValue() &&
            mc.player != null)
        {
            return mc.player;
        }

        return mc.getRenderViewEntity();
    }

    @Inject(method = "renderAttackIndicator", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/settings/GameSettings;showDebugInfo:Z", ordinal = 0), cancellable = true)
    private void overrideCursorRender(float partialTicks, net.minecraft.client.gui.ScaledResolution sr, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_F3_CURSOR.getBooleanValue())
        {
            RenderUtils.renderDirectionsCursor(this.zLevel, partialTicks);
            ci.cancel();
        }
    }

    @Inject(method = "renderGameOverlay",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;updatePlayerList(Z)V",
                     ordinal = 1, shift = At.Shift.AFTER)) // Note: The correct ordinal is 1 is the built mod, 0 in the 1.12.x dev environment...
    private void alwaysRenderPlayerList(float partialTicks, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_PLAYER_LIST_ALWAYS_ON.getBooleanValue())
        {
            net.minecraft.scoreboard.Scoreboard scoreboard = this.mc.world.getScoreboard();
            net.minecraft.scoreboard.ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(0);
            int width = GuiUtils.getScaledWindowWidth();

            this.overlayPlayerList.updatePlayerList(true);
            this.overlayPlayerList.renderPlayerlist(width, scoreboard, objective);
        }
    }

    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    private void disableScoreboardRendering(CallbackInfo ci)
    {
        if (DisableToggle.DISABLE_SCOREBOARD_RENDERING.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
