package com.github.lileep.pixelmonnavigator.bean;

import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class PlayerContacts {
    private List<String> registeredNpc;

    public PlayerContacts() {
        super();
    }

    public PlayerContacts(List<String> registeredNpc) {
        this.registeredNpc = registeredNpc;
    }

    public List<String> getRegisteredNpc() {
        return registeredNpc;
    }

    public void setRegisteredNpc(List<String> registeredNpc) {
        this.registeredNpc = registeredNpc;
    }
}
