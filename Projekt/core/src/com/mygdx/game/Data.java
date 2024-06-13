package com.mygdx.game;

public class Data {
    boolean isOverlap;
    float y;

    public Data(boolean isOverlap, float y) {
        this.isOverlap = isOverlap;
        this.y = y;
    }


    public boolean isOverlap() {
        return isOverlap;
    }

    public float getY() {
        return y;
    }

    public void setOverlap(boolean overlap) {
        isOverlap = overlap;
    }

    public void setY(float y) {
        this.y = y;
    }
}
