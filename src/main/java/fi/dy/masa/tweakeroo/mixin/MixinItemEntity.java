package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.IEntityItem;
import fi.dy.masa.tweakeroo.util.InventoryUtils;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity implements IEntityItem
{
    @Shadow private int itemAge;
    @Shadow private int pickupDelay;

    @Shadow public abstract ItemStack getStack();

    public MixinItemEntity(EntityType<?> entityTypeIn, World worldIn)
    {
        super(entityTypeIn, worldIn);
    }

    @Override
    public int getPickupDelay()
    {
        return this.pickupDelay;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", at = @At("RETURN"))
    private void removeEmptyShulkerBoxTags(World worldIn, double x, double y, double z, ItemStack stack, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_SHULKERBOX_STACK_GROUND.getBooleanValue())
        {
            if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock)
            {
                if (InventoryUtils.cleanUpShulkerBoxNBT(stack))
                {
                    ((ItemEntity) (Object) this).setStack(stack);
                }
            }
        }
    }

    @Inject(method = "canMerge()Z", at = @At("HEAD"), cancellable = true)
    private void allowStackingEmptyShulkerBoxes(CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureToggle.TWEAK_SHULKERBOX_STACK_GROUND.getBooleanValue())
        {
            ItemStack stack = this.getStack();

            if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock)
            {
                boolean canMerge = this.isAlive()
                                    && this.pickupDelay != 32767
                                    && this.itemAge != -32768
                                    && this.itemAge < 6000
                                    && stack.getCount() < 64;

                cir.setReturnValue(canMerge);
            }
        }
    }

    @Inject(method = "tryMerge(Lnet/minecraft/entity/ItemEntity;)V", at = @At("HEAD"), cancellable = true)
    private void stackEmptyShulkerBoxes(ItemEntity other, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_SHULKERBOX_STACK_GROUND.getBooleanValue())
        {
            ItemEntity self = (ItemEntity) (Object) this;
            ItemStack stackSelf = self.getStack();
            ItemStack stackOther = other.getStack();

            if (stackSelf.getItem() instanceof BlockItem && ((BlockItem) stackSelf.getItem()).getBlock() instanceof ShulkerBoxBlock &&
                stackSelf.getItem() == stackOther.getItem() &&
                fi.dy.masa.malilib.util.InventoryUtils.shulkerBoxHasItems(stackSelf) == false &&
                // Only stack up to 64, and don't steal from other stacks that are larger
                stackSelf.getCount() < 64 && stackSelf.getCount() >= stackOther.getCount() &&
                ItemStack.areNbtEqual(stackSelf, stackOther))
            {
                int amount = Math.min(stackOther.getCount(), 64 - stackSelf.getCount());

                stackSelf.increment(amount);
                self.setStack(stackSelf);
                this.pickupDelay = Math.max(((IEntityItem) other).getPickupDelay(), this.pickupDelay);
                this.itemAge = Math.min(other.getItemAge(), this.itemAge);

                if (amount >= stackOther.getCount())
                {
                    other.discard();
                }
                else
                {
                    stackOther.decrement(amount);
                    other.setStack(stackOther);
                }

                ci.cancel();
            }
        }
    }
}
