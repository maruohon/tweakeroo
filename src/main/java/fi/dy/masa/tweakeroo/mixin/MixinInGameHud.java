package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud extends DrawableHelper
{
    @Shadow @Final private PlayerListHud playerListHud;
    @Shadow @Final private MinecraftClient client;
    @Shadow private int scaledWidth;

    @Inject(method = "getCameraPlayer", at = @At("HEAD"), cancellable = true)
    private void overridePlayerForRendering(CallbackInfoReturnable<PlayerEntity> cir)
    {
        // Fix the hotbar rendering in the Free Camera mode by using the actual player
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue() && this.client.player != null)
        {
            cir.setReturnValue(this.client.player);
        }
    }

    @Inject(method = "renderCrosshair", at = @At(value = "FIELD",
                target = "Lnet/minecraft/client/option/GameOptions;debugEnabled:Z", ordinal = 0), cancellable = true)
    private void overrideCursorRender(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_F3_CURSOR.getBooleanValue())
        {
            RenderUtils.renderDirectionsCursor(this.getZOffset(), this.client.getTickDelta());
            ci.cancel();
        }
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/gui/hud/PlayerListHud;setVisible(Z)V",
                     ordinal = 1, shift = At.Shift.AFTER))
    private void alwaysRenderPlayerList(MatrixStack matrixStack, float partialTicks, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_PLAYER_LIST_ALWAYS_ON.getBooleanValue())
        {
            Scoreboard scoreboard = this.client.world.getScoreboard();
            ScoreboardObjective objective = scoreboard.getObjectiveForSlot(0);

            this.playerListHud.setVisible(true);
            this.playerListHud.render(matrixStack, this.scaledWidth, scoreboard, objective);
        }
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
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
