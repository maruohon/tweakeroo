package fi.dy.masa.tweakeroo.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(value = net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket.class, priority = 999)
public abstract class MixinUpdateStructureBlockC2SPacket
{
    @ModifyConstant(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V",
            slice = @Slice(from = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
                                      target = "Lnet/minecraft/network/packet/c2s/play/UpdateStructureBlockC2SPacket;structureName:Ljava/lang/String;"),
                           to   = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
                                      target = "Lnet/minecraft/network/packet/c2s/play/UpdateStructureBlockC2SPacket;mirror:Lnet/minecraft/util/BlockMirror;")),
            constant = { @Constant(intValue = -48), @Constant(intValue = 48) }, require = 0)
    private int overrideStructureBlockSizeLimit(int original)
    {
        if (FeatureToggle.TWEAK_STRUCTURE_BLOCK_LIMIT.getBooleanValue())
        {
            int overridden = Configs.Generic.STRUCTURE_BLOCK_MAX_SIZE.getIntegerValue();
            return original == -48 ? -overridden : overridden;
        }

        return original;
    }
}
