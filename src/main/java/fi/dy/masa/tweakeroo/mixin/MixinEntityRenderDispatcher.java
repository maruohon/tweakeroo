package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakeroo.Tweakeroo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher
{
    @Inject(method = "method_3950", at = @At("HEAD"), cancellable = true) // MCP: shouldRender
    private void onShouldRender(Entity entityIn, VisibleRegion region, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> ci)
    {
        if (FeatureToggle.TWEAK_NO_ENTITY_RENDERING.getBooleanValue() && (entityIn instanceof PlayerEntity) == false)
        {
            ci.setReturnValue(false);
            ci.cancel();
            return;
        }

        if (entityIn instanceof FallingBlockEntity && FeatureToggle.TWEAK_NO_FALLING_BLOCK_RENDER.getBooleanValue())
        {
            ci.setReturnValue(false);
            ci.cancel();
        }
        else if (entityIn instanceof ExperienceOrbEntity)
        {
            if (FeatureToggle.TWEAK_RENDER_LIMIT_ENTITIES.getBooleanValue())
            {
                int max = Configs.Generic.RENDER_LIMIT_XP_ORB.getIntegerValue();

                if (max >= 0 && ++Tweakeroo.renderCountXPOrbs > max)
                {
                    ci.setReturnValue(false);
                    ci.cancel();
                    return;
                }
            }
        }
        else if (entityIn instanceof ItemEntity)
        {
            if (FeatureToggle.TWEAK_RENDER_LIMIT_ENTITIES.getBooleanValue())
            {
                int max = Configs.Generic.RENDER_LIMIT_ITEM.getIntegerValue();

                if (max >= 0 && ++Tweakeroo.renderCountItems > max)
                {
                    ci.setReturnValue(false);
                    ci.cancel();
                    return;
                }
            }
        }
    }
}
