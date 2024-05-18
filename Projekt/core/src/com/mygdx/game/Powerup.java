package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Powerup extends Sprite {

    final Power power;

    public Powerup(Power power, Texture texture, int srcX, int srcY, int srcWidth,int srcHeigth) {
        super(texture, srcX, srcY, 64, 64);
        this.power = power;
    }

    public Power getPower() {
        return power;
    }
}
