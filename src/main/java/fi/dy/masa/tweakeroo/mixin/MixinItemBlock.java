package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.PlacementHandler;
import fi.dy.masa.tweakeroo.tweaks.PlacementHandler.UseContext;
import fi.dy.masa.tweakeroo.util.IItemStackLimit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

@Mixin(ItemBlock.class)
public abstract class MixinItemBlock extends Item implements IItemStackLimit
{
    public MixinItemBlock(Block blockIn, Item.Properties builder)
    {
        super(builder);
    }

    @Shadow
    protected abstract IBlockState getStateForPlacement(BlockItemUseContext context);

    @Redirect(method = "tryPlace", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/item/ItemBlock;getStateForPlacement(" +
                         "Lnet/minecraft/item/BlockItemUseContext;)" +
                         "Lnet/minecraft/block/state/IBlockState;"), require = 0)
    private IBlockState modifyPlacementState(ItemBlock item, BlockItemUseContext ctx)
    {
        IBlockState stateOriginal = this.getStateForPlacement(ctx);

        // the state can be null in 1.13+
        if (stateOriginal != null && Configs.Generic.CLIENT_PLACEMENT_ROTATION.getBooleanValue())
        {
            UseContext context = UseContext.from(ctx, EnumHand.MAIN_HAND);
            return PlacementHandler.getStateForPlacement(stateOriginal, context);
        }

        return stateOriginal;
    }

    @Override
    public int getMaxStackSize(ItemStack stack)
    {
        if (FeatureToggle.TWEAK_SHULKERBOX_STACKING.getBooleanValue() &&
            ((ItemBlock) (Object) this).getBlock() instanceof BlockShulkerBox &&
            InventoryUtils.shulkerBoxHasItems(stack) == false)
        {
            return 64;
        }

        // FIXME How to call the stack-sensitive version on the super class?
        return super.getMaxStackSize();
    }
}
