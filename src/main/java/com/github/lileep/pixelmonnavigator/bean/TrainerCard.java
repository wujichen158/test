package com.github.lileep.pixelmonnavigator.bean;

import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;
import net.minecraft.network.PacketBuffer;

@ConfigSerializable
public class TrainerCard {
    private String registeredName;

    private boolean show = true;

    private transient boolean canBattle;

    private transient Integer aveLvl;

    private boolean showAveLvl = false;

    private String description;

    private String position;

    private String intro;

    private String signalInWorld;
    private transient boolean sameWorld;

    public TrainerCard() {
    }

    public TrainerCard(PacketBuffer packetBuffer) {
        decode(packetBuffer);
    }

    public String getRegisteredName() {
        return registeredName;
    }

    public void setRegisteredName(String registeredName) {
        this.registeredName = registeredName;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public boolean isCanBattle() {
        return canBattle;
    }

    public void setCanBattle(boolean canBattle) {
        this.canBattle = canBattle;
    }

    public Integer getAveLvl() {
        return aveLvl;
    }

    public void setAveLvl(Integer aveLvl) {
        this.aveLvl = aveLvl;
    }

    public boolean isShowAveLvl() {
        return showAveLvl;
    }

    public void setShowAveLvl(boolean showAveLvl) {
        this.showAveLvl = showAveLvl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getSignalInWorld() {
        return signalInWorld;
    }

    public void setSignalInWorld(String signalInWorld) {
        this.signalInWorld = signalInWorld;
    }

    public boolean isSameWorld() {
        return sameWorld;
    }

    public void setSameWorld(boolean sameWorld) {
        this.sameWorld = sameWorld;
    }

    /**
     * Packet sending usage. No need to send `signalInWorld`
     *
     * @param packetBuffer
     */
    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeUtf(this.registeredName);
        packetBuffer.writeBoolean(this.show);
        packetBuffer.writeBoolean(this.canBattle);
        packetBuffer.writeBoolean(this.showAveLvl);
        packetBuffer.writeUtf(this.description);
        packetBuffer.writeUtf(this.position);
        packetBuffer.writeUtf(this.intro);
        packetBuffer.writeBoolean(this.sameWorld);
    }

    /**
     * Packet receiving usage. No need to receive `signalInWorld`
     *
     * @param packetBuffer
     */
    public void decode(PacketBuffer packetBuffer) {
        this.registeredName = packetBuffer.readUtf();
        this.show = packetBuffer.readBoolean();
        this.canBattle = packetBuffer.readBoolean();
        this.showAveLvl = packetBuffer.readBoolean();
        this.description = packetBuffer.readUtf();
        this.position = packetBuffer.readUtf();
        this.intro = packetBuffer.readUtf();
        this.sameWorld = packetBuffer.readBoolean();
    }
}
