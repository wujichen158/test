package com.github.lileep.pixelmonnavigator.config;

import com.github.lileep.pixelmonnavigator.bean.TrainerCard;
import com.github.lileep.pixelmonnavigator.lib.Reference;
import com.github.lileep.pixelmonnavigator.util.PVBInteractUtil;
import com.pixelmonmod.pixelmon.api.config.api.data.ConfigPath;
import com.pixelmonmod.pixelmon.api.config.api.yaml.AbstractYamlConfig;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ServerWorldInfo;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ConfigPath(Reference.TC_PATH)
@ConfigSerializable
public class TrainerCardCfg extends AbstractYamlConfig {
    private Map<String, TrainerCard> trainerCardMap;

    public Map<String, TrainerCard> getTrainerCardMap() {
        return trainerCardMap;
    }

    public void setTrainerCardMap(Map<String, TrainerCard> trainerCardMap) {
        this.trainerCardMap = trainerCardMap;
    }

    public List<TrainerCard> getAndCalAvailableTcs(List<String> playerContacts, PlayerEntity player) {
        return playerContacts.stream()
                .filter(trainerCardMap::containsKey)
                .map(npcName -> new AbstractMap.SimpleEntry<>(npcName, trainerCardMap.get(npcName)))
                .peek(entry -> {
                    TrainerCard trainerCard = entry.getValue();
//                    trainerCard.setCanBattle(PVBInteractUtil.isPlayerCanBattleWith(player.getUUID(), entry.getKey()));
                    System.out.println("world name: " + player.level.dimension().getRegistryName());
                    trainerCard.setSameWorld(getWorldName(player.level).equals(trainerCard.getSignalInWorld()));
                })
                .map(AbstractMap.SimpleEntry::getValue)
                .collect(Collectors.toList());
    }

    private String getWorldName(World level) {
        return level instanceof ServerWorld && level.getLevelData() instanceof ServerWorldInfo ? ((ServerWorldInfo) level.getLevelData()).getLevelName() : "NONE";
    }
}
