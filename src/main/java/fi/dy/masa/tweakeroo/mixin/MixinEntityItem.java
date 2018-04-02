package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;

@Mixin(EntityItem.class)
public abstract class MixinEntityItem extends Entity
{
    public MixinEntityItem(World worldIn)
    {
        super(worldIn);
    }

    @Inject(method = "onUpdate()V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/item/EntityItem;move(Lnet/minecraft/entity/MoverType;DDD)V"))
    private void clientPushOutOfBlocks(CallbackInfo ci)
    {
        if (this.world.isRemote && FeatureToggle.TWEAK_FIX_ENTITY_ITEM_MOVEMENT.getBooleanValue())
        {
            this.noClip = this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);
        }
    }
}
