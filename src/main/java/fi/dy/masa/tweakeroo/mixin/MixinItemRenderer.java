package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemRenderer;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer
{
    @Redirect(method = "updateEquippedItem()V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;getCooledAttackStrength(F)F"))
    public float redirectedGetCooledAttackStrength(EntityPlayerSP player, float adjustTicks)
    {
        return FeatureToggle.TWEAK_NO_ITEM_SWITCH_COOLDOWN.getBooleanValue() ? 1.0F : player.getCooledAttackStrength(adjustTicks);
    }
}
