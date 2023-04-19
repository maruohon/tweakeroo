package tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;

import malilib.util.inventory.StorageItemInventoryUtils;
import tweakeroo.config.FeatureToggle;
import tweakeroo.util.IItemStackLimit;

@Mixin(ItemShulkerBox.class)
public abstract class MixinItemShulkerBox extends ItemBlock implements IItemStackLimit
{
    public MixinItemShulkerBox(Block block)
    {
        super(block);
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        if (FeatureToggle.TWEAK_SHULKERBOX_STACKING.getBooleanValue() && StorageItemInventoryUtils.shulkerBoxHasItems(stack) == false)
        {
            return 64;
        }

        // FIXME How to call the stack-sensitive version on the super class?
        return super.getItemStackLimit();
    }
}
