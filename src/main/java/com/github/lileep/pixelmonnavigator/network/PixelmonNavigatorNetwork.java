package com.github.lileep.pixelmonnavigator.network;

import com.github.lileep.pixelmonnavigator.lib.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Predicate;

public class PixelmonNavigatorNetwork {
    public static final String CHANNEL = "main";
    private static final String PROTOCOL_VERSION = ModList.get().getModFileById(Reference.MOD_ID).getModLoaderVersion().toString();
    private static final Predicate<String> ACCEPT_SEPARATED = PROTOCOL_VERSION::equals;

    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Reference.MOD_ID, CHANNEL))
            .clientAcceptedVersions(ACCEPT_SEPARATED)
            .serverAcceptedVersions(ACCEPT_SEPARATED)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();
}
