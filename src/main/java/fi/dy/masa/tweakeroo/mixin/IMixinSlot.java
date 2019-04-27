package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.container.Slot;

@Mixin(Slot.class)
public interface IMixinSlot
{
    @Accessor("invSlot")
    int getSlotIndex();
}
