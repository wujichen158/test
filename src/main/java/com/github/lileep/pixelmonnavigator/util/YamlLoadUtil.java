package com.github.lileep.pixelmonnavigator.util;

public class YamlLoadUtil {

//    public static void loadPlayerContacts() {
//        PlayerContactsFactory.clear();
//
//        try (Stream<Path> paths = Files.walk(Paths.get(Reference.DATA_PATH))) {
//            paths.filter(Files::isRegularFile)
//                    .filter(path -> path.toString().endsWith(Reference.YAML_SUFFIX))
//                    .forEach(path -> {
//                        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
//                                .path(path)
//                                .build();
//                        try {
//                            ConfigurationNode node = loader.load();
//                            Optional.ofNullable(node.get(PlayerContacts.class)).ifPresent(playerContacts ->
//                                    PlayerContactsFactory.register(
//                                            UUID.fromString(path.getFileName().toString().replace(Reference.YAML_SUFFIX, "")),
//                                            playerContacts));
//                        } catch (ConfigurateException ignored) {
//                            PixelmonNavigator.LOGGER.warn("Player data " + path.getFileName() + " has something wrong or isn't a player data, please have a check");
//                        }
//                    });
//        } catch (IOException ignored) {
//            PixelmonNavigator.LOGGER.warn("Reading dir: " + Reference.DATA_PATH + " failed, please have a check");
//        }
//    }
}
