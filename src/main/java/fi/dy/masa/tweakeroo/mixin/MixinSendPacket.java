package fi.dy.masa.tweakeroo.mixin;

import fi.dy.masa.tweakeroo.util.PendingPackets;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientCommonNetworkHandler.class)
public class MixinSendPacket {
    @Shadow
    protected ClientConnection connection;

    @Inject(method = "sendPacket(Lnet/minecraft/network/packet/Packet;)V", at = @At("TAIL"))
    private void afterSendPacket(Packet<?> packet, CallbackInfo ci) {
        List<Packet<?>> pendingPackets = PendingPackets.getPackets(packet.getClass());
        if (pendingPackets == null) return;
        pendingPackets.forEach(p -> this.connection.send(p));
    }
}