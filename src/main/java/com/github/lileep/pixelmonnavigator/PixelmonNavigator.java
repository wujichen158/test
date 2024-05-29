package com.github.lileep.pixelmonnavigator;

import com.github.lileep.pixelmonnavigator.config.TrainerCardCfg;
import com.github.lileep.pixelmonnavigator.handler.ConnectionHandler;
import com.github.lileep.pixelmonnavigator.lib.Reference;
import com.github.lileep.pixelmonnavigator.network.PixelmonNavigatorNetwork;
import com.github.lileep.pixelmonnavigator.network.packet.data.ServerSendTcDataPacket;
import com.github.lileep.pixelmonnavigator.register.ItemRegister;
import com.pixelmonmod.pixelmon.api.config.api.yaml.YamlConfigFactory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Mod(Reference.MOD_ID)
public class PixelmonNavigator {
    public static final Logger LOGGER = LogManager.getLogger();

    private static PixelmonNavigator INSTANCE;

    private TrainerCardCfg trainerCardCfg;

    public PixelmonNavigator() {
        INSTANCE = this;

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setupNetwork);

        ItemRegister.init(bus);

        MinecraftForge.EVENT_BUS.register(new ConnectionHandler());
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static PixelmonNavigator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PixelmonNavigator();
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public void preInit(final FMLServerAboutToStartEvent event) {
        createDirsIfNotExist();
        loadYaml();
    }

    private void createDirsIfNotExist() {
        try {
            Path spritePath = Paths.get(Reference.SPRITE_PATH);
            if (Files.notExists(spritePath)) {
                Files.createDirectories(spritePath);
            }

            Path avatarPath = Paths.get(Reference.AVATAR_PATH);
            if (Files.notExists(avatarPath)) {
                Files.createDirectories(avatarPath);
            }
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
    }

    private void loadYaml() {
        try {
            this.trainerCardCfg = YamlConfigFactory.getInstance(TrainerCardCfg.class);
//            YamlLoadUtil.loadPlayerContacts();
        } catch (IOException ignored) {
            LOGGER.warn("Trainer card data loading failed, please have a check!");
        }
    }

    public TrainerCardCfg getTrainerCardCfg() {
        return trainerCardCfg;
    }

    private void setupNetwork(final FMLCommonSetupEvent event) {
        int messageNumber = 0;
        // Trainer Card data sending and open screen part
        PixelmonNavigatorNetwork.INSTANCE.messageBuilder(ServerSendTcDataPacket.class, messageNumber++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ServerSendTcDataPacket::encode)
                .decoder(ServerSendTcDataPacket::new)
                .consumer(ServerSendTcDataPacket::handle)
                .add();

        //Config sync part
//        PDNetwork.INSTANCE.messageBuilder(ClientJoinPacket.class, messageNumber++, NetworkDirection.PLAY_TO_SERVER)
//                .encoder(ClientJoinPacket::encode)
//                .decoder(ClientJoinPacket::new)
//                .consumer(ClientJoinPacket::handle)
//                .add();
//        PDNetwork.INSTANCE.messageBuilder(ServerPVBInstalledPacket.class, messageNumber++, NetworkDirection.PLAY_TO_CLIENT)
//                .encoder(ServerPVBInstalledPacket::encode)
//                .decoder(ServerPVBInstalledPacket::new)
//                .consumer(ServerPVBInstalledPacket::handle)
//                .add();
    }
}
