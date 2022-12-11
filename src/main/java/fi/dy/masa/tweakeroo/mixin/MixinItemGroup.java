package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.item.ItemGroup.class)
public abstract class MixinItemGroup
{
    /* // TODO 1.19.3+
    @Inject(method = "appendStacks", at = @At("RETURN"))
    private void appendCustomItems(net.minecraft.util.collection.DefaultedList<net.minecraft.item.ItemStack> stacks, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_CREATIVE_EXTRA_ITEMS.getBooleanValue())
        {
            List<net.minecraft.item.ItemStack> extraStacks = CreativeExtraItems.getExtraStacksForGroup((net.minecraft.item.ItemGroup) (Object) this);
            stacks.addAll(extraStacks);
        }
    }
    */
}
