package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.block.AbstractBlock.class)
public interface IMixinAbstractBlock
{
    @Mutable
    @Accessor("slipperiness")
    void setFriction(float friction);
}
