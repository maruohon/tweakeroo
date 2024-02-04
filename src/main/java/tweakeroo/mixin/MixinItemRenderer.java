package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemRenderer;

import malilib.render.RenderContext;
import tweakeroo.config.DisableToggle;
import tweakeroo.config.FeatureToggle;
import tweakeroo.util.MiscUtils;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer
{
    @Redirect(method = "updateEquippedItem()V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;getCooledAttackStrength(F)F"))
    public float redirectedGetCooledAttackStrength(EntityPlayerSP player, float adjustTicks)
    {
        return DisableToggle.DISABLE_ITEM_SWITCH_COOLDOWN.getBooleanValue() ? 1.0F : player.getCooledAttackStrength(adjustTicks);
    }

    @ModifyVariable(method = "renderItemInFirstPerson(F)V", ordinal = 1,
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/entity/AbstractClientPlayer;isHandActive()Z"))
    private boolean preventOffhandRendering(boolean original)
    {
        if (DisableToggle.DISABLE_OFFHAND_RENDERING.getBooleanValue())
        {
            return false;
        }

        return original;
    }

    @Inject(method = "renderFireInFirstPerson", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V", shift = At.Shift.AFTER))
    private void modifyPlayerOnFireRendering(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_PLAYER_ON_FIRE_SCALE.getBooleanValue())
        {
            MiscUtils.doPlayerOnFireRenderModifications(RenderContext.DUMMY);
        }
    }
}
