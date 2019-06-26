package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.IItemStackLimit;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(BlockItem.class)
public abstract class MixinBlockItem extends Item implements IItemStackLimit
{
    public MixinBlockItem(Block blockIn, Item.Settings builder)
    {
        super(builder);
    }

    @Override
    public int getMaxStackSize(ItemStack stack)
    {
        if (FeatureToggle.TWEAK_SHULKERBOX_STACKING.getBooleanValue() &&
            ((BlockItem) (Object) this).getBlock() instanceof ShulkerBoxBlock &&
            InventoryUtils.shulkerBoxHasItems(stack) == false)
        {
            return 64;
        }

        // FIXME How to call the stack-sensitive version on the super class?
        return super.getMaxCount();
    }
}
