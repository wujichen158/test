package com.github.lileep.pixelmonnavigator.bean;

public class UvObj {
    private float us;
    private float vs;
    private float ue;
    private float ve;
    private float aspectRatio;

    public UvObj(float us, float vs, float ue, float ve, float uRes, float vRes) {
        this.us = us / uRes;
        this.vs = vs / vRes;
        this.ue = ue / uRes;
        this.ve = ve / vRes;
        this.aspectRatio = (ue - us) / (ve - vs);
    }

    public float getUs() {
        return us;
    }

    public float getVs() {
        return vs;
    }

    public float getUe() {
        return ue;
    }

    public float getVe() {
        return ve;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }
}
