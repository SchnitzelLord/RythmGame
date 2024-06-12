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
        truck1,
        truck2,
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
            if (player.getY() > this.getY() + this.getHeight() -10) return new Data(false,0);
            if (player.getX() > this.getX() + 350) return new Data(false,0);
            return new Data(true,player.getY() - this.getY());
        }

        else if (getType() == Type.carGreen) {
            if (player.getX() + player.getWidth() < this.getX() + 50) return new Data(false,0);
            if (player.getY() + player.getHeight() < this.getY() + 110) return new Data(false,0);
            if (player.getY() > this.getY() + this.getHeight() - 80) return new Data(false,0);
            if (player.getX() > this.getX() + 580) return new Data(false,0);
            return new Data(true,player.getY());
        }
        else if (getType() == Type.truck1) {
            /*
            I did it the other way in comparison with the others because it is easier for two platforms in one
             when testing I noticed the values for the boundaries are not the same as if i would look them up in gimp,
             so I tested them until they feel right
             */
            if (player.getX() + player.getWidth() > this.getX() + 40 && player.getX() + player.getWidth() < this.getX() +200
            &&player.getY() > this.getY() + 205 && player.getY() < this.getY() + 230 ) return new Data(true,player.getY());
            else if (player.getX() + player.getWidth() > this.getX() + 200 && player.getX() + player.getWidth() < this.getX() +620
                    &&player.getY() > this.getY() + 280 && player.getY() < this.getY() + 295 ) return new Data(true,player.getY());
            return new Data(false,0);
        }
        else if (getType() == Type.truck2) {
            /*
            I did it the other way in comparison with the others because it is easier for two platforms in one
             when testing I noticed the values for the boundaries are not the same as if i would look them up in gimp,
             so I tested them until they feel right
             */
            if (player.getX() + player.getWidth() > this.getX()  && player.getX() + player.getWidth() < this.getX() +600
                    &&player.getY() > this.getY() + this.getHeight()-20 && player.getY() < this.getY() + this.getHeight() ) return new Data(true, player.getY());
            return new Data(false,0);
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
            case truck1:
                plat =  new Platform(type, getTexture(type), 0, 0, 600, 294);
                break;
            case truck2:
                plat =  new Platform(type, getTexture(type), 0, 0, 600, 260);
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
            case truck1: texture = new Texture("jumpAndRunSprites\\truck1.png");
                break;
            case truck2: texture = new Texture("jumpAndRunSprites\\truck2.png");
                break;
        }
        return  texture;
    }


}
