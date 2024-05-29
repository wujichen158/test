package com.github.lileep.pixelmonnavigator.util;

import java.util.Optional;
import java.util.UUID;

public class PVBInteractUtil {
    public static boolean isPlayerCanBattleWith(UUID playerUuid, String npcName) {
        return true;
//        return Optional.ofNullable(PlayerFactory.get(playerUuid))
//                .flatMap(playerData ->
//                        Optional.ofNullable(playerData.getBeatNpcMap().get(npcName))
//                                .flatMap(npcStatics ->
//                                        Optional.ofNullable(PixelmonVBattleNPC.getInstance().getNpcs().getNpcs().get(npcName))
//                                                .filter(npcBean -> npcStatics.canBattle(npcBean) &&
//                                                        npcStatics.canBattleToday(npcBean) &&
//                                                        npcStatics.canBattleThisWeek(npcBean))))
//                .isPresent();
    }

    // TODO: Complete this

    public static int getAveLevel(String npcName) {
        return 0;
    }
}
