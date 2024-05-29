package com.github.lileep.pixelmonnavigator.api;

import com.github.lileep.pixelmonnavigator.bean.factory.PlayerContactsFactory;
import net.minecraft.entity.player.PlayerEntity;

public class TrainerCardRegister {
    public static void register(PlayerEntity player, String npcName) {
        PlayerContactsFactory.addToContacts(player, npcName);
    }
}
