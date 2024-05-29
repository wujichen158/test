package com.github.lileep.pixelmonnavigator.item;

import com.github.lileep.pixelmonnavigator.PixelmonNavigator;
import com.github.lileep.pixelmonnavigator.bean.factory.PlayerContactsFactory;
import com.github.lileep.pixelmonnavigator.bean.TrainerCard;
import com.github.lileep.pixelmonnavigator.network.PixelmonNavigatorNetwork;
import com.github.lileep.pixelmonnavigator.network.packet.data.ServerSendTcDataPacket;
import com.pixelmonmod.pixelmon.items.group.PixelmonItemGroups;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;
import java.util.Optional;

public class PixelmonNavigatorItem extends Item {
    public PixelmonNavigatorItem() {
        super((new Item.Properties()).stacksTo(1).tab(PixelmonItemGroups.TAB_UTILITY));
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        if (!level.isClientSide()) {
//            if (ModList.get().isLoaded("pixelmonvbattlenpc")) {
                // Prepare trainer card data
                Optional.ofNullable(PlayerContactsFactory.tryGet(player)).ifPresent(playerContacts -> {
                    List<TrainerCard> tcs = PixelmonNavigator
                            .getInstance()
                            .getTrainerCardCfg()
                            .getAndCalAvailableTcs(playerContacts.getRegisteredNpc(), player);
                    // Send data to client screen
                    PixelmonNavigatorNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new ServerSendTcDataPacket(tcs));
                });
                return ActionResult.success(player.getItemInHand(hand));
//            } else {
//                player.sendMessage(new TranslationTextComponent("pixelmonnavigator.msg.missing_dependency", "pixelmonvbattlenpc"), player.getUUID());
//            }
        }

        return ActionResult.fail(player.getItemInHand(hand));
    }
}
