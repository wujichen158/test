package com.github.lileep.pixelmonnavigator.gui;

import com.github.lileep.pixelmonnavigator.bean.TrainerCard;
import com.github.lileep.pixelmonnavigator.bean.UvObj;
import com.github.lileep.pixelmonnavigator.lib.Reference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import com.pixelmonmod.pixelmon.client.gui.ScreenHelper;
import com.pixelmonmod.pixelmon.client.gui.pokedex.AnimationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class PixelmonNavigatorScreen extends Screen {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
            ResourceLocationHelper.of(Reference.MOD_ID + ":textures/gui/pixelmon_navigator_gui.png")
    };

    private static final ResourceLocation UI_ELEM_TEXTURE = ResourceLocationHelper.of(Reference.MOD_ID + ":textures/gui/ui_elem.png");
    private static final ResourceLocation UI_ELEM_SELECTED_TEXTURE = ResourceLocationHelper.of(Reference.MOD_ID + ":textures/gui/ui_elem_selected.png");
    private static final ResourceLocation CAN_BATTLE_ICON = ResourceLocationHelper.of(Reference.MOD_ID + ":textures/gui/can_battle_icon.png");
    private static final ResourceLocation CANNOT_BATTLE_ICON = ResourceLocationHelper.of(Reference.MOD_ID + ":textures/gui/cannot_battle_icon.png");
    private static final ResourceLocation NO_SIGNAL_ICON = ResourceLocationHelper.of(Reference.MOD_ID + ":textures/gui/no_signal_icon.png");
    private static final ResourceLocation DEFAULT_AVATAR = ResourceLocationHelper.of(Reference.MOD_ID + ":textures/avatars/default_avatar.png");
    private Map<String, AnimationHelper> animations = Maps.newConcurrentMap();
    private float centerW;
    private TrainerCard currentTrainerCard;
    private ResourceLocation currentAvatar = DEFAULT_AVATAR;
    private String currentAveLvl = I18n.get("pixelmonnavigator.gui.hidden_ave_lvl");
    private int currentRow = 0;
    private int dexVersion = 0;
    private final List<TrainerCard> availableTrainerCards;
    private final List<ResourceLocation> availableTrainerSprites = Lists.newArrayList();
    private TextFieldWidget searchBar;

    /**
     * Y decreases from top to bottom
     */
    private static final int TOP_BOUND = 77;
    private static final int BOTTOM_BOUND = 202;

    private static final int ELEM_HEIGHT = 12;
    private static final float ELEM_WIDTH = 21.9f;
    private static final int LINE_WIDTH = 140;

    private static final int LEFT_BOUND = -53;
    private static final int RIGHT_BOUND = -10;

    private static final int ELEM_PER_COL = 7;

    private static final float U_RES = 1800;
    private static final float V_RES = 1300;

    // Bg part

    private static final UvObj BLACK_SCREEN = new UvObj(1243, 38, 1740, 688, U_RES, V_RES);
    private static final UvObj OPENED_SCREEN = new UvObj(722, 38, 1219, 688, U_RES, V_RES);
    private static final UvObj BG_SHELL = new UvObj(0, 0, 670, 1297, U_RES, V_RES);


    // UI elem part

    private static final UvObj UI_BG = new UvObj(714, 726, 1202, 1283, U_RES, V_RES);
    private static final UvObj LIST_ELEM = new UvObj(1259, 735, 1417, 785, U_RES, V_RES);
    private static final UvObj LIST_ELEM_SELECTED = new UvObj(1448, 735, 1606, 785, U_RES, V_RES);
    private static final UvObj SCROLL_ELEM = new UvObj(1208, 756, 1240, 827, U_RES, V_RES);


    // Status part

//    private static final UvObj CAN_BATTLE_ICON = new UvObj(1256, 811, 1291, 846, U_RES, V_RES);
//    private static final UvObj CANNOT_BATTLE_ICON = new UvObj(1296, 811, 1331, 846, U_RES, V_RES);
//    private static final UvObj NO_SIGNAL_ICON = new UvObj(1339, 811, 1374, 846, U_RES, V_RES);


    // Avatar

//    private static final UvObj DEFAULT_AVATAR = new UvObj(1259, 898, 1386, 1025, U_RES, V_RES);

    private static float shellHeight;
    private static float shellWidth;

    public PixelmonNavigatorScreen(List<TrainerCard> trainerCards) {
        super(StringTextComponent.EMPTY);
        this.availableTrainerCards = trainerCards;
        this.availableTrainerCards.forEach(trainerCard -> {
            this.availableTrainerSprites.add(Optional.ofNullable(trainerCard.getRegisteredName())
                    .map(registeredName -> checkAndGetTrainerResource("sprites", registeredName, null))
                    .orElse(null));
        });
    }

    /**
     * Test method
     */
    @Deprecated
    public PixelmonNavigatorScreen() {
        super(StringTextComponent.EMPTY);
        this.availableTrainerCards = getTrainerList();
        this.availableTrainerCards.forEach(trainerCard -> {
            this.availableTrainerSprites.add(Optional.ofNullable(trainerCard.getRegisteredName())
                    .map(registeredName -> checkAndGetTrainerResource("sprites", registeredName, null))
                    .orElse(null));
        });
    }


    @Override
    public void init() {
        super.init();
        Optional.ofNullable(this.minecraft).ifPresent(mc -> mc.keyboardHandler.setSendRepeatsToGui(true));

        this.animations.put("open", new AnimationHelper(120, -1, this::drawScreenBackground));
        this.centerW = this.width / 2f;
//        this.searchBar = (new TabCompleteTextField(0, this.minecraft.font, this.centerW - 84, 141, 150, 10)).setCompletions(PixelmonSpecies.getFormattedEnglishNameSet());
//        this.searchBar.setBordered(false);
//        this.searchBar.setResponder((s) -> {
//            this.generatePokemonList();
//        });
//        this.children.add(this.searchBar);

        this.prepareTrainerCards();
        this.currentRow = getCurrentTrainerIndex();
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        for (AnimationHelper value : this.animations.values()) {
            if (value.getPokemonLevel() < 0) {
                value.update(matrix);
            }
        }

        if (this.animations.get("open").isComplete()) {
            if (!this.animations.containsKey("selection")) {
                this.beginSelectAnimation();
            }

            this.drawLeftPage(matrix, mouseX, mouseY, partialTicks);
            this.animations.values().stream()
                    .filter(anim -> anim.getPokemonLevel() >= 0)
                    .sorted(Comparator.comparingInt(AnimationHelper::getPokemonLevel))
                    .forEach(ah -> ah.update(matrix));

            this.drawRightPage(matrix, ScreenHelper.getBufferImpl(), mouseX, mouseY, partialTicks);
        }
    }

    private void drawScreenBackground(MatrixStack matrix, int frame) {
//        double thing = frame >= 60 ? 46 : frame++ < 20 ? 0 : ( .54431 / 3.0 ) * Math.pow(( frame - 20 ), 3 / 2.0);
//        double thing = 46;
        ResourceLocation texture = TEXTURES[dexVersion];
//        if (frame < 60) {
//            Minecraft.getInstance().getTextureManager().bind(texture);
//            ScreenHelper.simpleDrawImageQuad(matrix, (float) (centerW - 119), (float) (59 + thing), 15.08118f, 134, 811 / 1748f, 399 / 945f, 872 / 1748f, 941 / 945f, 1);
//        } else {
//        int xPos = this.centerW - 119;

        Minecraft.getInstance().getTextureManager().bind(texture);
//    }

        float shellYOffset = 3f;
        shellHeight = this.height - shellYOffset;
        shellWidth = shellHeight * BG_SHELL.getAspectRatio();

        // 373 is calculated from texture img
//        float screeY = calRelativeHeight(373f);
//        float screenWidth = calRelativeWidth(495f);
//        float screenHeight = calRelativeHeight(650f);
        float screeY = 68.5f;
        float screenWidth = 91f;
        float screenHeight = screenWidth / BLACK_SCREEN.getAspectRatio();

        // 415 is calculated from texture img
//        float uiY = calRelativeHeight(415f);
//        float uiWidth = calRelativeWidth(453f);
//        float uiHeight = calRelativeHeight(557f);
        float uiY = 78.5f;
        float uiWidth = 90f;
        float uiHeight = uiWidth / UI_BG.getAspectRatio();

        // Black screen
        ScreenHelper.simpleDrawImageQuad(matrix, centerW - screenWidth / 2, screeY + shellYOffset, screenWidth, screenHeight,
                BLACK_SCREEN.getUs(), BLACK_SCREEN.getVs(), BLACK_SCREEN.getUe(), BLACK_SCREEN.getVe(), 0);

        int totalFrames = 90;
        float rectHeight = calRectHeight(frame, totalFrames, screenHeight);
        float rectTop = (screenHeight - rectHeight) / 2;
        // Blue screen
        ScreenHelper.simpleDrawImageQuad(matrix, centerW - screenWidth / 2, screeY + shellYOffset + rectTop, screenWidth, rectHeight,
                OPENED_SCREEN.getUs(), OPENED_SCREEN.getVs(), OPENED_SCREEN.getUe(), OPENED_SCREEN.getVe(), 0);

        // Background shell
        ScreenHelper.simpleDrawImageQuad(matrix, this.centerW - 123f / 2f, shellYOffset, 123f, 123f / BG_SHELL.getAspectRatio(),
                BG_SHELL.getUs(), BG_SHELL.getVs(), BG_SHELL.getUe(), BG_SHELL.getVe(), 0);

        if (frame > totalFrames) {
            // Transparent UI elements
            ScreenHelper.simpleDrawImageQuad(matrix, this.centerW - uiWidth / 2f, uiY + shellYOffset, uiWidth, uiHeight,
                    UI_BG.getUs(), UI_BG.getVs(), UI_BG.getUe(), UI_BG.getVe(), 0);
        }
    }

    private float calRectHeight(int frame, int totalFrames, float screenHeight) {
        if (frame < totalFrames / 4) {
            return (screenHeight / 15f) * (frame / (totalFrames / 4f));
        } else if (frame < totalFrames / 2) {
            return (screenHeight / 15f) * (1f - (frame - totalFrames / 4f) / (totalFrames / 4f));
        } else if (frame < totalFrames * 3 / 4) {
            return 0;
        } else if (frame <= totalFrames) {
            return (float) (screenHeight * Math.sqrt((frame - totalFrames * 3f / 4f) / (totalFrames / 4f)));
        }
        return screenHeight;
    }

    private void drawRightPage(MatrixStack matrix, IRenderTypeBuffer buffer, int mouseX, int mouseY, float partialTicks) {
        if (currentTrainerCard == null) {
            return;
        }

        float avatarX = this.centerW + 2f;
        float avatarY = 84f;

        float nameX = this.centerW + 21f;
        float nameY = 90f;

        float nameFontSize = 12;
        float nameWidthFactor = 30;

        float infoX = this.centerW + 13f;
        float infoY = 106f;
        float infoYGap = 8.5f;

        float infoFontSize = 10;
        float infoWidthFactor = 45;

        // Avatar
        ScreenHelper.drawImageQuad(currentAvatar, matrix, avatarX, avatarY, 18, 18,
                0, 0, 1, 1, 0);

        // Infos
        ScreenHelper.drawScaledSquashedString(matrix, currentTrainerCard.getRegisteredName(),
                nameX, nameY, 0xffffff, nameFontSize, nameWidthFactor);
        ScreenHelper.drawScaledSquashedString(matrix, currentTrainerCard.getPosition(),
                infoX, infoY, 0xffffff, infoFontSize, infoWidthFactor);
        ScreenHelper.drawScaledSquashedString(matrix, this.currentAveLvl,
                infoX, infoY + infoYGap, 0xffffff, infoFontSize, infoWidthFactor);
        ScreenHelper.drawScaledSquashedString(matrix, currentTrainerCard.getDescription(),
                infoX, infoY + infoYGap * 2, 0xffffff, infoFontSize, infoWidthFactor);


        float introX = this.centerW + 6f;
        float introY = 145f;

        // Intro
        String[] trainerCardLines = ScreenHelper.splitStringToFit(currentTrainerCard.getIntro(),
                8, 35).split("\n");
        // TODO: Currently only show 7 lines intro
        if (minecraft != null) {
            for (int i = 0; i < Math.min(trainerCardLines.length, 7); i++) {
                ScreenHelper.drawScaledString(matrix, trainerCardLines[i], introX, introY + minecraft.font.lineHeight * (8 / 16f) * i, 0xffffff, 8);
            }
        }
    }

    private void drawLeftPage(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
//        this.searchBar.render(matrix, mouseX, mouseY, partialTicks);

        ResourceLocation texture = TEXTURES[dexVersion];
        Minecraft.getInstance().getTextureManager().bind(texture);

        // Width is taken into consideration
        float x = this.centerW - 41f;
        float y = 91.5f;
        int elemGap = 7;

        float elemWidth = 36f;
        float elemHeight = elemWidth / LIST_ELEM.getAspectRatio();

        for (int i = currentRow; 0 <= i && i < Math.min(availableTrainerCards.size(), currentRow + ELEM_PER_COL); i++) {
//            float y = (i - currentRow) * ELEM_HEIGHT + calRelativeHeight(435) + 5;
            TrainerCard tc = availableTrainerCards.get(i);


            // Draw elem bg
            if (isMouseInArea(mouseX, mouseY, x, x + elemWidth, y, y + elemHeight) && tc.isSameWorld()) {
//                ScreenHelper.simpleDrawImageQuad(matrix, x, y, elemWidth, elemHeight,
//                        LIST_ELEM_SELECTED.getUs(), LIST_ELEM_SELECTED.getVs(), LIST_ELEM_SELECTED.getUe(), LIST_ELEM_SELECTED.getVe(), 0);

                ScreenHelper.drawImageQuad(UI_ELEM_SELECTED_TEXTURE, matrix, x, y, elemWidth, elemHeight,
                        0, 0, 1, 1, 0);
            } else {
//                ScreenHelper.simpleDrawImageQuad(matrix, x, y, elemWidth, elemHeight,
//                        LIST_ELEM.getUs(), LIST_ELEM.getVs(), LIST_ELEM.getUe(), LIST_ELEM.getVe(), 0);

                ScreenHelper.drawImageQuad(UI_ELEM_TEXTURE, matrix, x, y, elemWidth, elemHeight,
                        0, 0, 1, 1, 0);
            }

            // Draw status icon
            if (tc.isSameWorld()) {
                if (tc.isCanBattle()) {
//                    ScreenHelper.simpleDrawImageQuad(matrix,
//                            x + 2, y + 4, 5, 5,
//                            CAN_BATTLE_ICON.getUs(), CAN_BATTLE_ICON.getVs(), CAN_BATTLE_ICON.getUe(), CAN_BATTLE_ICON.getVe(), 0);
                    ScreenHelper.drawImageQuad(CAN_BATTLE_ICON, matrix,
                            x + 2, y + 4, 5, 5,
                            0, 0, 1, 1, 0);
                } else {
//                    ScreenHelper.simpleDrawImageQuad(matrix,
//                            x + 2, y + 4, 5, 5,
//                            CANNOT_BATTLE_ICON.getUs(), CANNOT_BATTLE_ICON.getVs(), CANNOT_BATTLE_ICON.getUe(), CANNOT_BATTLE_ICON.getVe(), 0);
                    ScreenHelper.drawImageQuad(CANNOT_BATTLE_ICON, matrix,
                            x + 2, y + 4, 5, 5,
                            0, 0, 1, 1, 0);
                }
            } else {
//                ScreenHelper.simpleDrawImageQuad(matrix,
//                        x + 2, y + 4, 5, 5,
//                        NO_SIGNAL_ICON.getUs(), NO_SIGNAL_ICON.getVs(), NO_SIGNAL_ICON.getUe(), NO_SIGNAL_ICON.getVe(), 0);
                ScreenHelper.drawImageQuad(NO_SIGNAL_ICON, matrix,
                        x + 2, y + 4, 5, 5,
                        0, 0, 1, 1, 0);
            }

            // drawImageQuad: Won't scale
            // simpleDrawImageQuad: Auto scale

            // Do not create extra Optional here, may affect the performance
            ResourceLocation trainerSprite = this.availableTrainerSprites.get(i);
            if (null != trainerSprite) {
                ScreenHelper.drawImageQuad(trainerSprite, matrix, x + elemGap, y + 4, 5, 5,
                        0, 0, 1, 1, 0);
            }

            // Draw npc name
            ScreenHelper.drawScaledSquashedString(matrix, tc.getRegisteredName(),
                    x + elemGap * 2, y + 4, 0xffffff, 8, 42.5);

            y += ELEM_HEIGHT;
        }
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!animations.get("open").isComplete()) {
            return false;
        }

//        int truncatedMouseX = (int) mouseX;
//        int truncatedMouseY = (int) mouseY;
//        int centerX = this.width / 2;
//        double mouseCenterX = truncatedMouseX - centerX;
//
//        System.out.printf("mouse info: x: %f, y: %f, screen w: %d center: %d, mouse center: %f\n", mouseX, mouseY, this.width, centerX, mouseCenterX);

        float x = this.centerW - 41f;
        float y = 91.5f;

        float elemWidth = 36f;
        float elemHeight = elemWidth / LIST_ELEM.getAspectRatio();

        // Preprocess the mouse pos to avoid unnecessary calculation
        if (isMouseInArea(mouseX, mouseY, x, x + elemWidth, y, y + ELEM_HEIGHT * ELEM_PER_COL)) {
            for (int i = currentRow; 0 <= i && i < Math.min(availableTrainerCards.size(), currentRow + ELEM_PER_COL); i++) {
                if (isMouseInArea(mouseX, mouseY, x, x + elemWidth, y, y + elemHeight)) {
//                    System.out.println("current i is: " + i);
                    TrainerCard tc = availableTrainerCards.get(i);
                    if (tc.isSameWorld()) {
                        setCurrentTrainerCard(tc);
                        Optional.ofNullable(minecraft).ifPresent(mc ->
                                mc.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1f)));
                        this.beginSelectAnimation();
                    }
                    break;
                }

                y += ELEM_HEIGHT;
            }
        }

//        if (mouseCenterX >= LEFT_BOUND && mouseCenterX <= RIGHT_BOUND && mouseY >= TOP_BOUND && mouseY <= BOTTOM_BOUND) {
//            int i = currentRow + (truncatedMouseY - TOP_BOUND) / ELEM_HEIGHT;
//            if (i >= 0 && i < availableTrainerCards.size()) {
//                System.out.println("current i is: " + i);
//                setCurrentTrainerCard(availableTrainerCards.get(i));
//                minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
////                beginSelectAnimation();
//            }
//        }
        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (scroll > 0.0) {
            this.currentRow = Math.max(0, this.currentRow - 1);
        } else if (scroll < 0.0) {
            this.currentRow = Math.min(this.availableTrainerCards.size() - 1, this.currentRow + 1);
        }
        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void beginSelectAnimation() {
        int i = getCurrentTrainerIndex();
        if (i != -1) {
            this.animations.put("selection", new AnimationHelper(4, (matrix, frame) -> {
                if (i < this.currentRow + ELEM_PER_COL && i >= this.currentRow) {
//                    float x = centerW + LEFT_BOUND;
//                    int y = (i - currentRow) * ELEM_HEIGHT + TOP_BOUND;
//                    ScreenHelper.drawImageQuad(Resources.pixelmonCreativeInventory, matrix, x - 2, y + 2, 140, 20, 81f / 256f, 185f / 256f, 105f / 256f, 205f / 256f, 1, 1, 1, 1, 1);
//                    float offset = frame / 2f;
//                    ScreenHelper.drawImageQuad(currentPokemon.getDefaultForms().get(0).getGenderProperties(Gender.MALE).getDefaultPalette().getSprite(),
//                            matrix, x - offset, y - offset, 20 + offset * 2, 20 + offset * 2, 0, 0, 1, 1, 1, 1, 1, 1, 1);

                    // Do not create extra Optional here, may affect the performance
                    float x = this.centerW - 41f;
                    float y = 91.5f + (i - currentRow) * ELEM_HEIGHT;
                    int elemGap = 7;
                    float offset = frame / 2f;

                    ResourceLocation trainerSprite = this.availableTrainerSprites.get(i);
                    if (null != trainerSprite) {
                        ScreenHelper.drawImageQuad(trainerSprite, matrix, x + elemGap - offset, y + 4 - offset,
                                5 + offset * 2, 5 + offset * 2,
                                0, 0, 1, 1, 0);
                    }

//                    // Draw npc name
//                    ScreenHelper.drawScaledSquashedString(matrix, this.currentTrainerCard.getRegisteredName(),
//                            x + elemGap * 2 - offset, y + 4 - offset,
//                            0xffffff, 8 + offset * 2, 42.5);
                }
            }));
        }

    }

    public void prepareTrainerCards() {
        if (!this.availableTrainerCards.contains(this.currentTrainerCard)) {
            this.setCurrentTrainerCard(this.availableTrainerCards.stream()
                    .filter(TrainerCard::isShow)
                    .filter(TrainerCard::isSameWorld)
                    .findFirst().orElse(null));
        }

        this.currentRow = this.currentTrainerCard == null ? 0 : getCurrentTrainerIndex();

        this.animations.remove("selection");
    }

    /**
     * Currently generate a random list
     *
     * @return
     */
    @Deprecated
    private List<TrainerCard> getTrainerList() {
        List<TrainerCard> trainers = Lists.newArrayList();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            TrainerCard trainerCard = new TrainerCard();
            trainerCard.setRegisteredName("registeredName" + i);
            trainerCard.setShow(true);
            trainerCard.setCanBattle(random.nextBoolean());
            trainerCard.setShowAveLvl(random.nextBoolean());
            if (trainerCard.isShowAveLvl()) {
                trainerCard.setAveLvl(random.nextInt(101));
            }
            trainerCard.setDescription("des" + i);
            trainerCard.setPosition("position" + i);
            StringBuilder introBuilder = new StringBuilder();
            for (int j = 0; j < 10; j++) {
                introBuilder.append("intro").append(i);
            }
            trainerCard.setIntro(introBuilder.toString());
            trainerCard.setSignalInWorld(random.nextBoolean() ? "signalInWorld" + i : null);
            trainerCard.setSameWorld(true);
            Optional.ofNullable(trainerCard.getSignalInWorld()).ifPresent(world -> {
                if (!"signalInWorld1".equals(world)) {
                    trainerCard.setSameWorld(false);
                }
            });

            trainers.add(trainerCard);

        }
        return trainers;
    }

    /**
     * Better done all logics and null judgement here
     *
     * @param trainer
     */
    public void setCurrentTrainerCard(TrainerCard trainer) {
        this.currentTrainerCard = trainer;

        currentAvatar = Optional.ofNullable(currentTrainerCard.getRegisteredName())
                .map(registeredName -> checkAndGetTrainerResource("avatars", registeredName, DEFAULT_AVATAR))
                .orElseGet(() -> {
                    currentTrainerCard.setRegisteredName(I18n.get("pixelmonnavigator.gui.no_registered_name"));
                    return DEFAULT_AVATAR;
                });

        if (Optional.ofNullable(currentTrainerCard.getPosition()).isEmpty()) {
            currentTrainerCard.setPosition(I18n.get("pixelmonnavigator.gui.no_position"));
        }

        if (currentTrainerCard.isShowAveLvl()) {
            this.currentAveLvl = Optional.ofNullable(currentTrainerCard.getAveLvl())
                    .map(aveLvl -> {
                        if (aveLvl <= 0) {
                            return I18n.get("enum.trainerBoss.equal");
                        } else if (aveLvl > 100) {
                            return "+ " + (aveLvl - 100);
                        } else {
                            return aveLvl.toString();
                        }
                    }).orElse(I18n.get("pixelmonnavigator.gui.hidden_ave_lvl"));
        } else {
            this.currentAveLvl = I18n.get("pixelmonnavigator.gui.hidden_ave_lvl");
        }

        if (Optional.ofNullable(currentTrainerCard.getDescription()).isEmpty()) {
            currentTrainerCard.setDescription(I18n.get("pixelmonnavigator.gui.no_description"));
        }

        if (Optional.ofNullable(currentTrainerCard.getIntro()).isEmpty()) {
            currentTrainerCard.setIntro(I18n.get("pixelmonnavigator.gui.no_intro"));
        }
    }

    private int getCurrentTrainerIndex() {
        return this.availableTrainerCards.indexOf(this.currentTrainerCard);
    }

    private float calRelativeHeight(float originalHeight) {
        return shellHeight * originalHeight / 1297f;
    }

    private float calRelativeWidth(float originalWidth) {
        return shellWidth * originalWidth / 670f;
    }

    private boolean isMouseInArea(double mouseX, double mouseY, float xMin, float xMax, float yMin, float yMax) {
        return xMin <= mouseX && mouseX <= xMax && yMin <= mouseY && mouseY <= yMax;
    }

    /**
     * Check and get trainer's ResourceLocation. Return `defaultVal` if the corresponding rl is not present
     *
     * @param path
     * @param registeredName
     * @param defaultVal
     * @return
     */
    private ResourceLocation checkAndGetTrainerResource(String path, String registeredName, ResourceLocation defaultVal) {
        return Optional.ofNullable(
                        ResourceLocationHelper.of(String.format("%s:textures/%s/%s.png",
                                Reference.MOD_ID, path, registeredName)))
                // Check ResourceLocation presentation
                .filter(resourceLocation -> Minecraft.getInstance().getResourceManager().hasResource(resourceLocation))
                .orElse(defaultVal);
    }
}