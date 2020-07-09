package fi.dy.masa.tweakeroo.mixin;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.CreativeExtraItems;

@Mixin(net.minecraft.item.ItemGroup.class)
public abstract class MixinItemGroup
{
    @Inject(method = "appendStacks", at = @At("RETURN"))
    private void appendCustomItems(net.minecraft.util.collection.DefaultedList<net.minecraft.item.ItemStack> stacks, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_CREATIVE_EXTRA_ITEMS.getBooleanValue())
        {
            List<net.minecraft.item.ItemStack> extraStacks = CreativeExtraItems.getExtraStacksForGroup((net.minecraft.item.ItemGroup) (Object) this);
            stacks.addAll(extraStacks);
        }
    }
}
