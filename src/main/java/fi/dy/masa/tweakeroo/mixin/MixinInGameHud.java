package fi.dy.masa.tweakeroo.mixin;

import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;

import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud
{
    @Shadow @Final private PlayerListHud playerListHud;
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "getCameraPlayer", at = @At("HEAD"), cancellable = true)
    private void overridePlayerForRendering(CallbackInfoReturnable<PlayerEntity> cir)
    {
        // Fix the hotbar rendering in the Free Camera mode by using the actual player
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && this.client.player != null)
        {
            cir.setReturnValue(this.client.player);
        }
    }

    @Inject(method = "renderCrosshair", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowDebugHud()Z", ordinal = 0), cancellable = true)
    private void overrideCursorRender(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_F3_CURSOR.getBooleanValue())
        {
            RenderUtils.renderDirectionsCursor(0, this.client.getTickDelta());
            ci.cancel();
        }
    }

    @Inject(method = "renderPlayerList",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/gui/hud/PlayerListHud;setVisible(Z)V",
                     ordinal = 1, shift = At.Shift.AFTER))
    private void alwaysRenderPlayerList(DrawContext drawContext, float tickDelta, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_PLAYER_LIST_ALWAYS_ON.getBooleanValue())
        {
            Scoreboard scoreboard = this.client.world.getScoreboard();
            ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.LIST);

            this.playerListHud.setVisible(true);
            this.playerListHud.render(drawContext, drawContext.getScaledWindowWidth(), scoreboard, objective);
        }
    }

    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            at = @At("HEAD"), cancellable = true)
    private void disableScoreboardRendering(CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_SCOREBOARD_RENDERING.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void disableStatusEffectHudRendering(CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_STATUS_EFFECT_HUD.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
