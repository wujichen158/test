package com.github.lileep.pixelmonnavigator.gui;

import com.github.lileep.pixelmonnavigator.bean.TrainerCard;
import com.github.lileep.pixelmonnavigator.bean.UvObj;
import com.github.lileep.pixelmonnavigator.lib.Reference;
import com.github.lileep.pixelmonnavigator.register.SoundRegister;
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
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

/**
 * Y decreases from top to bottom
 */
@OnlyIn(Dist.CLIENT)
public class PixelmonNavigatorScreen extends Screen {

    // Texture part

    private static final ResourceLocation GUI_TEXTURE = ResourceLocationHelper
            .of(Reference.MOD_ID + ":textures/gui/pixelmon_navigator_gui.png");
    private static final ResourceLocation UI_ELEM_TEXTURE = ResourceLocationHelper
            .of(Reference.MOD_ID + ":textures/gui/ui_elem.png");
    private static final ResourceLocation UI_ELEM_SELECTED_TEXTURE = ResourceLocationHelper
            .of(Reference.MOD_ID + ":textures/gui/ui_elem_selected.png");
    private static final ResourceLocation SCROLLER_TEXTURE = ResourceLocationHelper
            .of(Reference.MOD_ID + ":textures/gui/scroller.png");
    private static final ResourceLocation CAN_BATTLE_ICON = ResourceLocationHelper
            .of(Reference.MOD_ID + ":textures/gui/can_battle_icon.png");
    private static final ResourceLocation CANNOT_BATTLE_ICON = ResourceLocationHelper
            .of(Reference.MOD_ID + ":textures/gui/cannot_battle_icon.png");
    private static final ResourceLocation NO_SIGNAL_ICON = ResourceLocationHelper
            .of(Reference.MOD_ID + ":textures/gui/no_signal_icon.png");
    private static final ResourceLocation DEFAULT_AVATAR = ResourceLocationHelper
            .of(Reference.MOD_ID + ":textures/avatars/default_avatar.png");

    private static final String SPRITE_PATH = "sprites";
    private static final String AVATAR_PATH = "avatars";

    // Base param part

    private float centerW;
    private TrainerCard currentTrainerCard;
    private int currentRow = 0;
    private ResourceLocation currentAvatar = DEFAULT_AVATAR;
    private String currentAveLvl = I18n.get("pixelmonnavigator.gui.hidden_ave_lvl");
    private String[] currentIntros;
    private final List<TrainerCard> availableTrainerCards;
    private final List<ResourceLocation> availableTrainerSprites = Lists.newArrayList();
    private final int sizeWithoutLastPg;
    private TextFieldWidget searchBar;

    // Bg part

    private static final float U_RES = 1800;
    private static final float V_RES = 1300;
    private static final UvObj BLACK_SCREEN = new UvObj(1243, 38, 1740, 688, U_RES, V_RES);
    private static final UvObj OPENED_SCREEN = new UvObj(722, 38, 1219, 688, U_RES, V_RES);
    private static final UvObj BG_SHELL = new UvObj(0, 0, 670, 1297, U_RES, V_RES);
    private static final UvObj UI_BG = new UvObj(714, 726, 1202, 1283, U_RES, V_RES);

    private float shellYOffset;
    private float shellHeight;
    private float shellWidth;
    private float screeY;
    private float screenWidth;
    private float screenHeight;


    // UI elem part

    private static final int ELEM_PER_PG = 7;
    private static final int ELEM_GAPED_HEIGHT = 12;

//    private static final UvObj LIST_ELEM = new UvObj(1259, 735, 1417, 785, U_RES, V_RES);
//    private static final UvObj LIST_ELEM_SELECTED = new UvObj(1448, 735, 1606, 785, U_RES, V_RES);
//    private static final UvObj SCROLL_ELEM = new UvObj(1208, 756, 1240, 827, U_RES, V_RES);

    private float uiY;

    // Width must be taken into consideration

    private float uiWidth;
    private float uiHeight;
    private float elemX;
    //    private float elemY;
    private int elemGap;
    private float elemWidth;
    private float elemHeight;
    private float scrollerX;
    private float scrollerY;
    private float scrollerWidth;
    private float scrollerHeight;
    private float scrollBarHeight;
    private float scrollOffs;
    private boolean scrolling;


    // UI Info part

    private float avatarX;
    private float avatarY;
    private float nameX;
    private float nameY;
    private float nameFontSize;
    private float nameWidthFactor;
    private float infoX;
    private float infoY;
    private float infoYGap;
    private float infoFontSize;
    private float infoWidthFactor;
    private float introX;
    private float introY;

    // Animation

    private Map<String, AnimationHelper> animations = Maps.newConcurrentMap();

    private static final int ANIME_FRAMES = 80;
    private static final int TOTAL_FRAMES = 100;

    private static final String OPEN_ANIME = "open";
    private static final String SELECTION_ANIME = "selection";


    // Status part

//    private static final UvObj CAN_BATTLE_ICON = new UvObj(1256, 811, 1291, 846, U_RES, V_RES);
//    private static final UvObj CANNOT_BATTLE_ICON = new UvObj(1296, 811, 1331, 846, U_RES, V_RES);
//    private static final UvObj NO_SIGNAL_ICON = new UvObj(1339, 811, 1374, 846, U_RES, V_RES);


    // Avatar

//    private static final UvObj DEFAULT_AVATAR = new UvObj(1259, 898, 1386, 1025, U_RES, V_RES);


    public PixelmonNavigatorScreen(List<TrainerCard> trainerCards) {
        super(StringTextComponent.EMPTY);
        this.availableTrainerCards = trainerCards;
        this.sizeWithoutLastPg = trainerCards.size() - ELEM_PER_PG;
        this.availableTrainerCards.forEach(trainerCard -> {
            this.availableTrainerSprites.add(Optional.ofNullable(trainerCard.getRegisteredName())
                    .map(registeredName -> checkAndGetTrainerResource(SPRITE_PATH, registeredName, null))
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
        this.sizeWithoutLastPg = this.availableTrainerCards.size() - ELEM_PER_PG;
        this.availableTrainerCards.forEach(trainerCard -> {
            this.availableTrainerSprites.add(Optional.ofNullable(trainerCard.getRegisteredName())
                    .map(registeredName -> checkAndGetTrainerResource(SPRITE_PATH, registeredName, null))
                    .orElse(null));
        });
    }


    @Override
    public void init() {
        super.init();
        Optional.ofNullable(this.minecraft).ifPresent(mc -> mc.keyboardHandler.setSendRepeatsToGui(true));

        this.animations.put(OPEN_ANIME, new AnimationHelper(TOTAL_FRAMES, -1, this::drawScreenBackground));
        this.centerW = this.width / 2f;


        shellYOffset = 3f;
        shellHeight = this.height - shellYOffset;
        shellWidth = shellHeight * BG_SHELL.getAspectRatio();

        screeY = 68.5f;
        screenWidth = 91f;
        screenHeight = screenWidth / BLACK_SCREEN.getAspectRatio();

        uiY = 78.5f;
        uiWidth = 90f;
        uiHeight = uiWidth / UI_BG.getAspectRatio();


        elemX = this.centerW - 41f;
        // elemY should be refreshing, so don't create it
        elemGap = 7;

        elemWidth = 36f;
        elemHeight = 11.54716981132075f;

        scrollerX = this.centerW - 2.5f;
        scrollerY = 90f;

        scrollerWidth = 1.5f;
        scrollerHeight = 8.333333f;

        scrollBarHeight = 86f;
        scrolling = false;


        avatarX = this.centerW + 2f;
        avatarY = 84f;

        nameX = this.centerW + 21f;
        nameY = 90f;

        nameFontSize = 12;
        nameWidthFactor = 30;

        infoX = this.centerW + 13f;
        infoY = 106f;
        infoYGap = 8.5f;

        infoFontSize = 10;
        infoWidthFactor = 45;

        introX = this.centerW + 6f;
        introY = 145f;

//        this.searchBar = (new TabCompleteTextField(0, this.minecraft.font, this.centerW - 84, 141, 150, 10)).setCompletions(PixelmonSpecies.getFormattedEnglishNameSet());
//        this.searchBar.setBordered(false);
//        this.searchBar.setResponder((s) -> {
//            this.generatePokemonList();
//        });
//        this.children.add(this.searchBar);

        this.prepareTrainerCards();

        //Recalculate scroll offset after current row is refreshed
        recalScrollOffs();
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        for (AnimationHelper value : this.animations.values()) {
            if (value.getPokemonLevel() < 0) {
                value.update(matrix);
            }
        }

        if (this.animations.get(OPEN_ANIME).isComplete()) {
            if (!this.animations.containsKey(SELECTION_ANIME)) {
                this.beginSelectAnimation();
            }

            this.drawLeftPage(matrix, mouseX, mouseY);
            this.animations.values().stream()
                    .filter(anim -> anim.getPokemonLevel() >= 0)
                    .sorted(Comparator.comparingInt(AnimationHelper::getPokemonLevel))
                    .forEach(ah -> ah.update(matrix));

            this.drawRightPage(matrix);
        }
    }

    private void drawScreenBackground(MatrixStack matrix, int frame) {
//        double thing = frame >= 60 ? 46 : frame++ < 20 ? 0 : ( .54431 / 3.0 ) * Math.pow(( frame - 20 ), 3 / 2.0);
//        double thing = 46;
//        if (frame < 60) {
//            Minecraft.getInstance().getTextureManager().bind(texture);
//            ScreenHelper.simpleDrawImageQuad(matrix, (float) (centerW - 119), (float) (59 + thing), 15.08118f, 134, 811 / 1748f, 399 / 945f, 872 / 1748f, 941 / 945f, 1);
//        } else {
//        int xPos = this.centerW - 119;
//    }
        Minecraft.getInstance().getTextureManager().bind(GUI_TEXTURE);

        // Black screen
        ScreenHelper.simpleDrawImageQuad(matrix, centerW - screenWidth / 2, screeY + shellYOffset, screenWidth, screenHeight,
                BLACK_SCREEN.getUs(), BLACK_SCREEN.getVs(), BLACK_SCREEN.getUe(), BLACK_SCREEN.getVe(), 0);

        float rectHeight = calRectHeight(frame, ANIME_FRAMES, screenHeight);
        float rectTop = (screenHeight - rectHeight) / 2;
        // Blue screen
        ScreenHelper.simpleDrawImageQuad(matrix, centerW - screenWidth / 2, screeY + shellYOffset + rectTop, screenWidth, rectHeight,
                OPENED_SCREEN.getUs(), OPENED_SCREEN.getVs(), OPENED_SCREEN.getUe(), OPENED_SCREEN.getVe(), 0);

        // Background shell
        ScreenHelper.simpleDrawImageQuad(matrix, this.centerW - 123f / 2f, shellYOffset, 123f, 123f / BG_SHELL.getAspectRatio(),
                BG_SHELL.getUs(), BG_SHELL.getVs(), BG_SHELL.getUe(), BG_SHELL.getVe(), 0);

        if (frame > ANIME_FRAMES) {
            // Transparent UI elements
            ScreenHelper.simpleDrawImageQuad(matrix, this.centerW - uiWidth / 2f, uiY + shellYOffset, uiWidth, uiHeight,
                    UI_BG.getUs(), UI_BG.getVs(), UI_BG.getUe(), UI_BG.getVe(), 0);
        }
    }

    private float calRectHeight(int frame, int animeFrames, float screenHeight) {
        if (frame < animeFrames / 4) {
            return (screenHeight / 15f) * (frame / (animeFrames / 4f));
        } else if (frame < animeFrames / 2) {
            return (screenHeight / 15f) * (1f - (frame - animeFrames / 4f) / (animeFrames / 4f));
        } else if (frame < animeFrames * 3 / 4) {
            return 0;
        } else if (frame <= animeFrames) {
            return (float) (screenHeight * Math.sqrt((frame - animeFrames * 3f / 4f) / (animeFrames / 4f)));
        }
        return screenHeight;
    }

    private void drawLeftPage(MatrixStack matrix, int mouseX, int mouseY) {
//        this.searchBar.render(matrix, mouseX, mouseY, partialTicks);
        float elemY = 91.5f;
        for (int i = currentRow; 0 <= i && i < Math.min(availableTrainerCards.size(), currentRow + ELEM_PER_PG); i++) {
//            float elemY = (i - currentRow) * ELEM_HEIGHT + calRelativeHeight(435) + 5;
            TrainerCard tc = availableTrainerCards.get(i);

            boolean isSelected = currentTrainerCard == tc ||
                    (isMouseInArea(mouseX, mouseY, elemX, elemX + elemWidth, elemY, elemY + elemHeight) && tc.isSameWorld());

            // Draw elem bg
            if (isSelected) {
//                ScreenHelper.simpleDrawImageQuad(matrix, elemX, elemY, elemWidth, elemHeight,
//                        LIST_ELEM_SELECTED.getUs(), LIST_ELEM_SELECTED.getVs(), LIST_ELEM_SELECTED.getUe(), LIST_ELEM_SELECTED.getVe(), 0);

                ScreenHelper.drawImageQuad(UI_ELEM_SELECTED_TEXTURE, matrix, elemX, elemY, elemWidth, elemHeight,
                        0, 0, 1, 1, 0);
            } else {
//                ScreenHelper.simpleDrawImageQuad(matrix, elemX, elemY, elemWidth, elemHeight,
//                        LIST_ELEM.getUs(), LIST_ELEM.getVs(), LIST_ELEM.getUe(), LIST_ELEM.getVe(), 0);

                ScreenHelper.drawImageQuad(UI_ELEM_TEXTURE, matrix, elemX, elemY, elemWidth, elemHeight,
                        0, 0, 1, 1, 0);
            }

            // Draw scroller
            ScreenHelper.drawImageQuad(SCROLLER_TEXTURE, matrix, scrollerX, scrollerY + this.scrollOffs, scrollerWidth, scrollerHeight,
                    0, 0, 1, 1, 0);
//            this.blit(p_230450_1_, i + 119, j + 15 + k, 176 + (this.isScrollBarActive() ? 0 : 12), 0, 12, 15);

            // Draw status icon
            if (tc.isSameWorld()) {
                if (tc.isCanBattle()) {
//                    ScreenHelper.simpleDrawImageQuad(matrix,
//                            elemX + 2, elemY + 4, 5, 5,
//                            CAN_BATTLE_ICON.getUs(), CAN_BATTLE_ICON.getVs(), CAN_BATTLE_ICON.getUe(), CAN_BATTLE_ICON.getVe(), 0);
                    ScreenHelper.drawImageQuad(CAN_BATTLE_ICON, matrix,
                            elemX + 2, elemY + 4, 5, 5,
                            0, 0, 1, 1, 0);
                } else {
//                    ScreenHelper.simpleDrawImageQuad(matrix,
//                            elemX + 2, elemY + 4, 5, 5,
//                            CANNOT_BATTLE_ICON.getUs(), CANNOT_BATTLE_ICON.getVs(), CANNOT_BATTLE_ICON.getUe(), CANNOT_BATTLE_ICON.getVe(), 0);
                    ScreenHelper.drawImageQuad(CANNOT_BATTLE_ICON, matrix,
                            elemX + 2, elemY + 4, 5, 5,
                            0, 0, 1, 1, 0);
                }
            } else {
//                ScreenHelper.simpleDrawImageQuad(matrix,
//                        elemX + 2, elemY + 4, 5, 5,
//                        NO_SIGNAL_ICON.getUs(), NO_SIGNAL_ICON.getVs(), NO_SIGNAL_ICON.getUe(), NO_SIGNAL_ICON.getVe(), 0);
                ScreenHelper.drawImageQuad(NO_SIGNAL_ICON, matrix,
                        elemX + 2, elemY + 4, 5, 5,
                        0, 0, 1, 1, 0);
            }

            // drawImageQuad: Won't scale
            // simpleDrawImageQuad: Auto scale

            // Do not create extra Optional here, may affect the performance
            ResourceLocation trainerSprite = this.availableTrainerSprites.get(i);
            if (null != trainerSprite) {
                ScreenHelper.drawImageQuad(trainerSprite, matrix, elemX + elemGap, elemY + 4, 5, 5,
                        0, 0, 1, 1, 0);
            }

            // Draw npc name
            ScreenHelper.drawScaledSquashedString(matrix, tc.getRegisteredName(),
                    elemX + elemGap * 2, elemY + 4, 0xffffff, 8, 42.5);

            elemY += ELEM_GAPED_HEIGHT;
        }
    }

    private void drawRightPage(MatrixStack matrix) {
        if (currentTrainerCard == null) {
            return;
        }

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

        // Intro
        // TODO: Currently only show 7 lines intro
        if (minecraft != null) {
            for (int i = 0; i < Math.min(currentIntros.length, 7); i++) {
                ScreenHelper.drawScaledString(matrix, currentIntros[i],
                        introX, introY + minecraft.font.lineHeight * (8 / 16f) * i, 0xffffff, 8);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!animations.get(OPEN_ANIME).isComplete()) {
            return false;
        }

//        int truncatedMouseX = (int) mouseX;
//        int truncatedMouseY = (int) mouseY;
//        int centerX = this.width / 2;
//        double mouseCenterX = truncatedMouseX - centerX;
//
//        System.out.printf("mouse info: elemX: %f, elemY: %f, screen w: %d center: %d, mouse center: %f\n", mouseX, mouseY, this.width, centerX, mouseCenterX);

        this.scrolling = false;
        float elemY = 91.5f;
        // Preprocess the mouse pos to avoid unnecessary calculation
        if (isMouseInArea(mouseX, mouseY, elemX, elemX + elemWidth, elemY, elemY + ELEM_GAPED_HEIGHT * ELEM_PER_PG)) {
            for (int i = currentRow; 0 <= i && i < Math.min(availableTrainerCards.size(), currentRow + ELEM_PER_PG); i++) {
                if (isMouseInArea(mouseX, mouseY, elemX, elemX + elemWidth, elemY, elemY + elemHeight)) {
//                    System.out.println("current i is: " + i);
                    TrainerCard tc = availableTrainerCards.get(i);
                    if (tc.isSameWorld()) {
                        setCurrentTrainerCard(tc);
                        Optional.ofNullable(minecraft).ifPresent(mc ->
                                mc.getSoundManager().play(SimpleSound.forUI(SoundRegister.ELEM_CLICK.get(), 1f)));
                        this.beginSelectAnimation();
                    }
                    break;
                }

                elemY += ELEM_GAPED_HEIGHT;
            }
        } else if (isMouseInArea(mouseX, mouseY,
                scrollerX - 0.5f, scrollerX + scrollerWidth + 0.5f, scrollerY, scrollerY + scrollBarHeight)) {
            this.scrolling = true;
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
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrolling && this.isScrollBarActive()) {
            float ratio = MathHelper.clamp(
                    ((float) mouseY - this.scrollerY - this.scrollerHeight / 2f) / (this.scrollBarHeight - this.scrollerHeight),
                    0.0F, 1.0F);
            recalScrollOffs(ratio);
            this.currentRow = MathHelper.clamp((int) (ratio * this.sizeWithoutLastPg), 0, this.sizeWithoutLastPg);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (isScrollBarActive()) {
            if (scroll > 0.0) {
                this.currentRow = Math.max(0, this.currentRow - 1);
            } else if (scroll < 0.0) {
                this.currentRow = Math.min(this.sizeWithoutLastPg, this.currentRow + 1);
            }

            recalScrollOffs();
        }

        return true;
//        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    private boolean isScrollBarActive() {
        return this.sizeWithoutLastPg > 0;
    }

    /**
     * Recalculate the offset of scroller
     */
    private void recalScrollOffs() {
        recalScrollOffs((float) this.currentRow / (float) this.sizeWithoutLastPg);
    }

    /**
     * Recalculate the offset of scroller using current line / total valid lines
     *
     * @param ratio current line / total valid lines
     */
    private void recalScrollOffs(float ratio) {
        this.scrollOffs = (this.scrollBarHeight - this.scrollerHeight) * ratio;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void beginSelectAnimation() {
        int i = getCurrentTrainerIndex();
        if (i != -1) {
            this.animations.put(SELECTION_ANIME, new AnimationHelper(4, (matrix, frame) -> {
                if (i < this.currentRow + ELEM_PER_PG && i >= this.currentRow) {
//                    float x = centerW + LEFT_BOUND;
//                    int y = (i - currentRow) * ELEM_HEIGHT + TOP_BOUND;
//                    ScreenHelper.drawImageQuad(Resources.pixelmonCreativeInventory, matrix, x - 2, y + 2, 140, 20, 81f / 256f, 185f / 256f, 105f / 256f, 205f / 256f, 1, 1, 1, 1, 1);
//                    float offset = frame / 2f;
//                    ScreenHelper.drawImageQuad(currentPokemon.getDefaultForms().get(0).getGenderProperties(Gender.MALE).getDefaultPalette().getSprite(),
//                            matrix, x - offset, y - offset, 20 + offset * 2, 20 + offset * 2, 0, 0, 1, 1, 1, 1, 1, 1, 1);

                    // Do not create extra Optional here, may affect the performance
                    float elemY = 91.5f + (i - currentRow) * ELEM_GAPED_HEIGHT;
                    float offset = frame / 2f;

                    ResourceLocation trainerSprite = this.availableTrainerSprites.get(i);
                    if (null != trainerSprite) {
                        ScreenHelper.drawImageQuad(trainerSprite, matrix, elemX + elemGap - offset, elemY + 4 - offset,
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

        this.currentRow = this.currentTrainerCard == null ? 0 :
                Math.min(Math.max(this.sizeWithoutLastPg, 0), getCurrentTrainerIndex());

        this.animations.remove(SELECTION_ANIME);
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
                .map(registeredName -> checkAndGetTrainerResource(AVATAR_PATH, registeredName, DEFAULT_AVATAR))
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

        currentIntros = ScreenHelper.splitStringToFit(currentTrainerCard.getIntro(), 8, 35).split("\n");
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