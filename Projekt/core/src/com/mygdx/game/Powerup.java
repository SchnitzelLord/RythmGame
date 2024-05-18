package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Powerup extends Sprite {

    private final Power power;

    public enum Power {
        moreJumps,
        doublespeed
    }


    public Powerup(Power power, Texture texture, int srcX, int srcY, int srcWidth,int srcHeigth) {
        super(texture, srcX, srcY, 64, 64);
        this.power = power;
    }


    public Power getPower() {
        return power;
    }

    public static Powerup createPowerup(Power power) {
        return new Powerup(power,getTexture(power), 0, 0, 64, 64);

    }

    private static Texture getTexture(Power power) {
        Texture texture = null;
        switch (power) {
            case moreJumps: texture = new Texture("characterSprite\\jump_placeholder.png");
                break;
            case doublespeed: texture = new Texture("characterSprite\\jump_placeholder.png"); // for testing jump_placeholder.png
                break;
        }
        return  texture;
    }
}
