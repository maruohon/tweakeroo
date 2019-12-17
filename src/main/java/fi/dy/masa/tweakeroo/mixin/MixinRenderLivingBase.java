package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(value = RenderLivingBase.class)
public abstract class MixinRenderLivingBase
{
    @Redirect(method = "renderModel", require = 0, at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/EntityLivingBase;isInvisibleToPlayer(Lnet/minecraft/entity/player/EntityPlayer;)Z"))
    private boolean redirectedIsInvisibleToPlayer(EntityLivingBase livingBase, EntityPlayer player)
    {
        if (FeatureToggle.TWEAK_RENDER_INVISIBLE_ENTITIES.getBooleanValue())
        {
            return false;
        }
        else
        {
            return livingBase.isInvisibleToPlayer(player);
        }
    }
}
