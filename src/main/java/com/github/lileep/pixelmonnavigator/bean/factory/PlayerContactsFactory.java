package com.github.lileep.pixelmonnavigator.bean.factory;

import com.github.lileep.pixelmonnavigator.PixelmonNavigator;
import com.github.lileep.pixelmonnavigator.bean.PlayerContacts;
import com.github.lileep.pixelmonnavigator.bean.TrainerCard;
import com.github.lileep.pixelmonnavigator.lib.Reference;
import com.github.lileep.pixelmonnavigator.util.AsyncUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import info.pixelmon.repack.org.spongepowered.ConfigurateException;
import info.pixelmon.repack.org.spongepowered.ConfigurationNode;
import info.pixelmon.repack.org.spongepowered.yaml.YamlConfigurationLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerContactsFactory {

    private static final Map<UUID, PlayerContacts> PLAYER_CONTACTS = Maps.newHashMap();

    public static void register(UUID playerUuid) {
        Optional.ofNullable(getOrCreatePlayerContactsFile(playerUuid)).ifPresent(playerContacts ->
                PLAYER_CONTACTS.put(playerUuid, playerContacts));
    }

    public static void addToContacts(PlayerEntity player, String npcName) {
        UUID playerUuid = player.getUUID();
        Optional.ofNullable(PLAYER_CONTACTS.get(playerUuid)).ifPresent(playerContacts -> {
            // Add to cache and file
            // Temporarily use this list way since its simple. But its efficiency is low
            if (!playerContacts.getRegisteredNpc().contains(npcName)) {
                playerContacts.getRegisteredNpc().add(npcName);
                updatePlayerContactsFile(playerUuid, playerContacts);

                // Send msg
                String npcDisplayName = Optional.ofNullable(PixelmonNavigator.getInstance().getTrainerCardCfg().getTrainerCardMap().get(npcName))
                        .map(TrainerCard::getRegisteredName).orElse(npcName);
                player.sendMessage(new TranslationTextComponent("pixelmonnavigator.msg.contacts_added", npcDisplayName), playerUuid);
            }
        });
    }

    public static void unregister(UUID playerUuid) {
        PLAYER_CONTACTS.remove(playerUuid);
    }

    public static PlayerContacts tryGet(PlayerEntity player) {
        return PLAYER_CONTACTS.get(player.getUUID());
    }

    public static void clear() {
        PLAYER_CONTACTS.clear();
    }

    /**
     * To make sure this process is safe enough for data registration,
     * don't use async operation here
     */
    private static PlayerContacts getOrCreatePlayerContactsFile(UUID playerUuid) {
        String playerFileName = playerUuid + Reference.YAML_SUFFIX;
        Path playerFile = Paths.get(Reference.DATA_PATH).resolve(playerFileName);
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(playerFile)
                .build();
        ConfigurationNode rootNode;
        try {
            if (Files.exists(playerFile)) {
                // Read from file
                rootNode = loader.load();

                // Return cache
                return rootNode.get(PlayerContacts.class);
            } else {
                rootNode = loader.createNode();
                PlayerContacts newPlayerData = new PlayerContacts(Lists.newArrayList());

                // Write to file
                rootNode.set(PlayerContacts.class, newPlayerData);
                loader.save(rootNode);

                // Return cache
                return newPlayerData;
            }
        } catch (ConfigurateException ignored) {
            PixelmonNavigator.LOGGER.warn("Player file " + playerFileName + " has something wrong while creating/reading, please have a check.");
        }
        return null;
    }

    private static void updatePlayerContactsFile(UUID playerUuid, PlayerContacts playerContacts) {
        AsyncUtil.runAsync(() -> {
            String playerFileName = playerUuid + Reference.YAML_SUFFIX;
            Path playerFile = Paths.get(Reference.DATA_PATH).resolve(playerFileName);
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                    .path(playerFile)
                    .build();
            ConfigurationNode rootNode;
            try {
                if (Files.exists(playerFile)) {
                    // Read from file
                    rootNode = loader.load();
                } else {
                    rootNode = loader.createNode();
                }
                // Write to file
                rootNode.set(PlayerContacts.class, playerContacts);
                loader.save(rootNode);
            } catch (ConfigurateException ignored) {
                PixelmonNavigator.LOGGER.warn("Player file " + playerFileName + " has something wrong, please have a check.");
            }
        });
    }
}
