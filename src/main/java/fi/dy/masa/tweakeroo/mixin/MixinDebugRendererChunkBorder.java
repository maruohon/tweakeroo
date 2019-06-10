package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.debug.DebugRendererChunkBorder;
import net.minecraft.entity.Entity;

@Mixin(DebugRendererChunkBorder.class)
public abstract class MixinDebugRendererChunkBorder
{
    @Redirect(method = "render", at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/entity/EntityPlayerSP;"))
    private EntityPlayerSP useCameraEntity(Minecraft mc)
    {
        // Fix the chunk border renderer using the client player instead of the camera entity
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            Entity entity = mc.getRenderViewEntity();

            if (entity instanceof EntityPlayerSP)
            {
                return (EntityPlayerSP) entity;
            }
        }

        return mc.player;
    }
}
