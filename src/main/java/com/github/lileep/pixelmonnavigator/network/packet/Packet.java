package com.github.lileep.pixelmonnavigator.network.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class Packet {
    public abstract void decode(PacketBuffer packetBuffer);

    public abstract void encode(PacketBuffer packetBuffer);

    public abstract void handle(Supplier<NetworkEvent.Context> ctx);

    public Packet() {
    }

    public Packet(PacketBuffer buffer) {
        decode(buffer);
    }

}
