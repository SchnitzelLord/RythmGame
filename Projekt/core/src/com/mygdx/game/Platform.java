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
        carGrey,
        carGreen,
    }

    public Platform(Type type, Texture texture, int srcX, int srcY, int srcWidth, int srcHeigth) {
        super(texture, srcX, srcY, srcWidth, srcHeigth);
        this.type = type;
    }



    public Data overlap(Sprite player) {
        if (getType() == Type.test) {
            if (player.getX() + player.getWidth() < this.getX()) return new Data(false,0);
            if (player.getY() + player.getHeight() < this.getY()) return new Data(false,0);
            if (player.getY() > this.getY() + this.getHeight()) return new Data(false,0);
            if (player.getX() > this.getX() + this.getWidth()) return new Data(false,0);
            return new Data(true, player.getY() - this.getY());
        }
        else if (getType() == Type.car1) {
            if (player.getX() + player.getWidth() < this.getX() + 180) return new Data(false,0);
            if (player.getY() + player.getHeight() < this.getY() + 180) return new Data(false,0);
            if (player.getY() > this.getY() + this.getHeight()) return new Data(false,0);
            if (player.getX() > this.getX() + 380) return new Data(false,0);
            return new Data(true,player.getY() - this.getY());
        }
        else if (getType() == Type.carGrey) {
            if (player.getX() + player.getWidth() < this.getX() + 160) return new Data(false,0);
            if (player.getY() + player.getHeight() < this.getY() + 140) return new Data(false,0);
            if (player.getY() > this.getY() + this.getHeight()) return new Data(false,0);
            if (player.getX() > this.getX() + 350) return new Data(false,0);
            return new Data(true,player.getY() - this.getY());
        }
        return new Data(false,0);
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
            case carGrey:
                plat =  new Platform(type, getTexture(type), 0, 0, 500, 170);
                break;
            case carGreen:
                plat =  new Platform(type, getTexture(type), 0, 0, 600, 210);
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
            case carGrey: texture = new Texture("jumpAndRunSprites\\car_grey.png");
                break;
            case carGreen: texture = new Texture("jumpAndRunSprites\\car_green.png");
                break;
        }
        return  texture;
    }


}
