package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Powerup extends Sprite {

    private final Power power;

    public enum Power {
        moreJumps,
        shield,
        live;
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
            case moreJumps: texture = new Texture("jumpAndRunSprites\\jump.png");
                break;
            case shield: texture = new Texture("jumpAndRunSprites\\shield2.png"); // for testing jump_placeholder.png
                break;
            case live: texture = new Texture("jumpAndRunSprites\\heartsprite.png"); // for testing jump_placeholder.png
                break;
        }
        return  texture;
    }
}
