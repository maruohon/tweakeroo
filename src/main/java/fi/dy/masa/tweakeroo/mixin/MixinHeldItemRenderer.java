package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import fi.dy.masa.tweakeroo.config.Configs;

@Mixin(HeldItemRenderer.class)
public abstract class MixinHeldItemRenderer
{
    @Redirect(method = "updateHeldItems()V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"))
    public float redirectedGetCooledAttackStrength(ClientPlayerEntity player, float adjustTicks)
    {
        return Configs.Disable.DISABLE_ITEM_SWITCH_COOLDOWN.getBooleanValue() ? 1.0F : player.getAttackCooldownProgress(adjustTicks);
    }

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
    private void preventOffhandRendering(AbstractClientPlayerEntity player, float tickDelta,
                                         float pitch, Hand hand, float swingProgress, ItemStack item,
                                         float equipProgress, MatrixStack matrices,
                                         VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci)
    {
        if (hand == Hand.OFF_HAND && Configs.Disable.DISABLE_OFFHAND_RENDERING.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
