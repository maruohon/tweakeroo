package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.MiscUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemRenderer;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer
{
    @Redirect(method = "updateEquippedItem()V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;getCooledAttackStrength(F)F"))
    public float redirectedGetCooledAttackStrength(EntityPlayerSP player, float adjustTicks)
    {
        return Configs.Disable.DISABLE_ITEM_SWITCH_COOLDOWN.getBooleanValue() ? 1.0F : player.getCooledAttackStrength(adjustTicks);
    }

    @ModifyVariable(method = "renderItemInFirstPerson(F)V", ordinal = 1,
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/entity/AbstractClientPlayer;isHandActive()Z"))
    private boolean preventOffhandRendering(boolean original)
    {
        if (Configs.Disable.DISABLE_OFFHAND_RENDERING.getBooleanValue())
        {
            return false;
        }

        return original;
    }

    /*
    @Inject(method = "renderFireInFirstPerson", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFF)V", shift = At.Shift.AFTER))
    private void modifyPlayerOnFireRendering(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_PLAYER_ON_FIRE_SCALE.getBooleanValue())
        {
            MiscUtils.doPlayerOnFireRenderModifications();
        }
    }
    */
}
