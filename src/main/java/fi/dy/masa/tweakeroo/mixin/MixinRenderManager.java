package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.Tweakeroo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;

@Mixin(RenderManager.class)
public abstract class MixinRenderManager
{
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void onShouldRender(Entity entity, ICamera camera, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_NO_ENTITY_RENDERING.getBooleanValue() && (entity instanceof EntityPlayer) == false)
        {
            cir.setReturnValue(false);
        }
        else if (entity instanceof EntityFallingBlock && FeatureToggle.TWEAK_NO_FALLING_BLOCK_RENDER.getBooleanValue())
        {
            cir.setReturnValue(false);
        }
        else if (entity instanceof EntityXPOrb)
        {
            if (FeatureToggle.TWEAK_RENDER_LIMIT_ENTITIES.getBooleanValue())
            {
                int max = Configs.Generic.RENDER_LIMIT_XP_ORB.getIntegerValue();

                if (max >= 0 && ++Tweakeroo.renderCountXPOrbs > max)
                {
                    cir.setReturnValue(false);
                }
            }
        }
        else if (entity instanceof EntityItem)
        {
            if (FeatureToggle.TWEAK_RENDER_LIMIT_ENTITIES.getBooleanValue())
            {
                int max = Configs.Generic.RENDER_LIMIT_ITEM.getIntegerValue();

                if (max >= 0 && ++Tweakeroo.renderCountItems > max)
                {
                    cir.setReturnValue(false);
                }
            }
        }
        else if (FeatureToggle.TWEAK_NO_DEAD_MOB_RENDERING.getBooleanValue() &&
                 entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getHealth() <= 0f)
        {
            cir.setReturnValue(false);
        }
    }
}
