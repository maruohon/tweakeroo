package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import net.minecraft.server.network.packet.UpdateStructureBlockC2SPacket;

@Mixin(value = UpdateStructureBlockC2SPacket.class, priority = 999)
public abstract class MixinUpdateStructureBlockC2SPacket
{
    @ModifyConstant(method = "read",
            slice = @Slice(from = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
                                      target = "Lnet/minecraft/server/network/packet/UpdateStructureBlockC2SPacket;offset:Lnet/minecraft/util/math/BlockPos;"),
                           to   = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
                                      target = "Lnet/minecraft/server/network/packet/UpdateStructureBlockC2SPacket;mirror:Lnet/minecraft/util/BlockMirror;")),
            constant = @Constant(intValue = 32), require = 0)
    private int overrideStructureBlockSizeLimit(int original)
    {
        if (FeatureToggle.TWEAK_STRUCTURE_BLOCK_LIMIT.getBooleanValue())
        {
            return Configs.Generic.STRUCTURE_BLOCK_MAX_SIZE.getIntegerValue();
        }

        return original;
    }
}
