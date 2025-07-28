package fi.dy.masa.tweakeroo.util;

import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PendingPackets {
    private static final Map<Class<?>, List<Packet<?>>> packets = new HashMap<>();

    public static void addPacket(Packet<?> packet, Class<?> after) {
        if (packets.containsKey(after)) {
            packets.get(after).add(packet);
        } else {
            LinkedList<Packet<?>> p = new LinkedList<>();
            p.add(packet);
            packets.put(after, p);
        }
    }

    @Nullable
    public static List<Packet<?>> getPackets(Class<?> currentPacketClass) {
        if (packets.containsKey(currentPacketClass)) {
            return packets.remove(currentPacketClass);
        } else {
            return null;
        }
    }
}
