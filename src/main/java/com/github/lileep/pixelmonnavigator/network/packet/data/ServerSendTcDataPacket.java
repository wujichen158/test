package com.github.lileep.pixelmonnavigator.network.packet.data;

import com.github.lileep.pixelmonnavigator.bean.TrainerCard;
import com.github.lileep.pixelmonnavigator.gui.PixelmonNavigatorScreen;
import com.github.lileep.pixelmonnavigator.network.packet.Packet;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class ServerSendTcDataPacket extends Packet {

    private List<TrainerCard> trainerCards;

    public ServerSendTcDataPacket(List<TrainerCard> trainerCards) {
        this.trainerCards = trainerCards;
    }

    public ServerSendTcDataPacket(PacketBuffer packetBuffer) {
        super(packetBuffer);
    }

    @Override
    public void decode(PacketBuffer packetBuffer) {
        int size = packetBuffer.readInt();
        this.trainerCards = Lists.newArrayList();
        for (int i = 0; i < size; i++) {
            this.trainerCards.add(new TrainerCard(packetBuffer));
        }
    }

    @Override
    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeInt(this.trainerCards.size());
        this.trainerCards.forEach(trainerCard -> trainerCard.encode(packetBuffer));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
//        Screen screen = new PixelmonNavigatorScreen(this.trainerCards);
        Screen screen = new PixelmonNavigatorScreen();
        Minecraft.getInstance().setScreen(screen);
    }
}
