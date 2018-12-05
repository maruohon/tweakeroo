package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.block.Block;

@Mixin(Block.class)
public interface IMixinBlock
{
    @Accessor
    void setSlipperiness(float slipperiness);
}
