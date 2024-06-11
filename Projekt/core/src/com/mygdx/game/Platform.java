package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Platform extends Sprite {

    private final Type type;

    public Type getType() {
        return type;
    }

    public enum Type {
        test,
        car1,
    }

    public Platform(Type type, Texture texture, int srcX, int srcY, int srcWidth, int srcHeigth) {
        super(texture, srcX, srcY, srcWidth, srcHeigth);
        this.type = type;
    }



    public boolean overlap(Sprite player) {
        if (getType() == Type.test) {
            if (player.getX() + player.getWidth() < this.getX()) return false;
            if (player.getY() + player.getHeight() < this.getY()) return false;
            if (player.getY() > this.getY() + this.getHeight()) return false;
            if (player.getX() > this.getX() + this.getWidth()) return false;
            return true;
        }
        else if (getType() == Type.car1) {
            if (player.getX() + player.getWidth() < this.getX() + 180) return false;
            if (player.getY() + player.getHeight() < this.getY() + 180) return false;
            if (player.getY() > this.getY() + this.getHeight()) return false;
            if (player.getX() > this.getX() + 380) return false;
            return true;
        }
        return false;
    }

    public static Platform createPlatform(Type type) {
        Platform plat = new Platform(type, getTexture(type), 0, 0, 100, 10); // default shouldn't be the case
        switch (type) {
            case test:
                plat = new Platform(type, getTexture(type), 0, 0, 100, 10);
            break;
            case car1:
                plat =  new Platform(type, getTexture(type), 0, 0, 460, 200);
            break;
        }
        return plat;
    }



    private static Texture getTexture(Type type) {
        Texture texture = null;
        switch (type) {
            case test: texture = new Texture("jumpAndRunSprites\\platform.png");
                break;
            case car1: texture = new Texture("jumpAndRunSprites\\car1.png");
                break;
        }
        return  texture;
    }


}