package com.github.lileep.pixelmonnavigator.handler;

import com.github.lileep.pixelmonnavigator.bean.factory.PlayerContactsFactory;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ConnectionHandler {

//    @SubscribeEvent
//    public void onLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
//        PixelmonNavigatorNetwork.INSTANCE.sendToServer(new ClientJoinPacket());
//    }

    @SubscribeEvent
    public void onPlayerIn(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerContactsFactory.register(event.getPlayer().getUUID());
    }

    @SubscribeEvent
    public void onPlayerOut(PlayerEvent.PlayerLoggedOutEvent event) {
        //Don't need to write files last, since we write file every time
        PlayerContactsFactory.unregister(event.getPlayer().getUUID());
    }
}
