package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import malilib.util.game.wrap.GameWrap;
import tweakeroo.config.Configs;
import tweakeroo.config.DisableToggle;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity
{
    public MixinEntityLivingBase(World worldIn)
    {
        super(worldIn);
    }

    @Redirect(method = "travel", at = @At(value = "FIELD", ordinal = 1,
            target = "Lnet/minecraft/world/World;isRemote:Z"))
    private boolean fixElytraLanding(World world)
    {
        return world.isRemote && (Configs.Fixes.ELYTRA_FIX.getBooleanValue() == false || ((Object) this instanceof EntityPlayerSP) == false);
    }

    @Inject(method = "updatePotionEffects", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/network/datasync/EntityDataManager;get(Lnet/minecraft/network/datasync/DataParameter;)Ljava/lang/Object;"),
            cancellable = true)
    private void removeOwnPotionEffects(CallbackInfo ci)
    {
        if (DisableToggle.DISABLE_FP_POTION_EFFECTS.getBooleanValue() &&
            ((Object) this) == GameWrap.getClientPlayer() &&
            GameWrap.getClient().gameSettings.thirdPersonView == 0)
        {
            ci.cancel();
        }
    }
}
