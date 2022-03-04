package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.Vec3d;
import fi.dy.masa.tweakeroo.config.Configs.Generic;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler
{
    @Redirect(method = "onPlayerInteractBlock",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/util/math/Vec3d;subtract(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d tweakeroo_removeHitPosCheck(Vec3d hitVec, Vec3d blockCenter)
    {
        if (Generic.CLIENT_PLACEMENT_ROTATION.getBooleanValue())
        {
            return Vec3d.ZERO;
        }

        return hitVec.subtract(blockCenter);
    }
}
