package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.network.play.client.CPacketUpdateStructureBlock;

@Mixin(value = CPacketUpdateStructureBlock.class, priority = 999)
public abstract class MixinCPacketUpdateStructureBlock
{
    @ModifyConstant(method = "readPacketData",
            slice = @Slice(from = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
                                      target = "Lnet/minecraft/network/play/client/CPacketUpdateStructureBlock;field_210395_e:Lnet/minecraft/util/math/BlockPos;"),
                           to = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
                                    target = "Lnet/minecraft/network/play/client/CPacketUpdateStructureBlock;mirror:Lnet/minecraft/util/Mirror;")),
            constant = @Constant(intValue = 32))
    private int overrideStructureBlockSizeLimit(int original)
    {
        if (FeatureToggle.TWEAK_STRUCTURE_BLOCK_LIMIT.getBooleanValue())
        {
            return Configs.Generic.STRUCTURE_BLOCK_MAX_SIZE.getIntegerValue();
        }

        return original;
    }
}
