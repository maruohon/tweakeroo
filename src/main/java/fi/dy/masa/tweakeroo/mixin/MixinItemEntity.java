package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
    @Shadow private int age;
    @Shadow private int pickupDelay;

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

    /*
    @Inject(method = "combineItems", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 0), cancellable = true)
    private void tryStackShulkerBoxes(ItemEntity other, CallbackInfoReturnable<Boolean> cir)
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
                stackSelf.getAmount() < 64 && stackSelf.getAmount() >= stackOther.getAmount() &&
                ItemStack.areTagsEqual(stackSelf, stackOther))
            {
                int amount = Math.min(stackOther.getAmount(), 64 - stackSelf.getAmount());
                stackSelf.addAmount(amount);
                self.setStack(stackSelf);
                this.pickupDelay = Math.max(((IEntityItem) other).getPickupDelay(), this.pickupDelay);
                this.age = Math.min(other.getAge(), this.age);

                if (amount >= stackOther.getAmount())
                {
                    other.remove();
                }
                else
                {
                    stackOther.subtractAmount(amount);
                    other.setStack(stackOther);
                }

                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
    */
}
