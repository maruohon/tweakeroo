package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.block.AbstractBlock.class)
public interface IMixinAbstractBlock
{
    @Accessor("slipperiness")
    void setFriction(float friction);

    @Accessor("velocityMultiplier")
    void setVelocity(float friction);

    @Accessor("jumpVelocityMultiplier")
    void setJumpVelocity(float friction);
}
