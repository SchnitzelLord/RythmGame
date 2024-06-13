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
        carRed,
        carGrey,
        carGreen,
        truck1,
        truck2,
        truck3,
    }

    public Platform(Type type, Texture texture, int srcX, int srcY, int srcWidth, int srcHeigth) {
        super(texture, srcX, srcY, srcWidth, srcHeigth);
        this.type = type;
    }



    public boolean checkIfspritesOverlap(Sprite sp) {
        if (sp.getX() + sp.getWidth() < this.getX()) return false;
        if (sp.getY() + sp.getHeight() < this.getY()) return false;
        if (sp.getY() > this.getY() + this.getHeight()) return false;
        if (sp.getX() > this.getX() + this.getWidth()) return false;
        return  true;
    }

    public Data overlap(Sprite player) {
        if (getType() == Type.test) { // old design used for testing
            if (player.getX() + player.getWidth() < this.getX()) return new Data(false,0);
            if (player.getY() + player.getHeight() < this.getY()) return new Data(false,0);
            if (player.getY() > this.getY() + this.getHeight()) return new Data(false,0);
            if (player.getX() > this.getX() + this.getWidth()) return new Data(false,0);
            return new Data(true, player.getY() );
        }
        else if (getType() == Type.carRed) {
            if (checkHitbox(180,380,20,0,player)) return new Data(true, player.getY());
            else if (checkHitbox(0,180,80,60,player)) return new Data(true, player.getY()); // above the engine
            return new Data(false, 0);
        }
        else if (getType() == Type.carGrey) {
            if (checkHitbox(0,160,60,40,player)) return new Data(true, player.getY()); // above the engine
            else if (checkHitbox(160,370,20,0,player)) return new Data(true, player.getY()); // middle part
            else if (checkHitbox(370,480,60,20,player)) return new Data(true, player.getY()); // end art
            return new Data(false, 0);
        }

        else if (getType() == Type.carGreen) {
            if (checkHitbox(50,550,100,80,player)) return new Data(true, player.getY());
            return new Data(false, 0);
        }
        else if (getType() == Type.truck1) {

            if (checkHitbox(40,150,(int)getHeight() - 20,(int)getHeight() - 220,player)) return new Data(true, player.getY());
            else if (checkHitbox(200,600,(int)getHeight() - 280,(int)getHeight() - 300,player)) return new Data(true, player.getY());
            new Data(false,0);
        }
        else if (getType() == Type.truck2) {
            if (checkHitbox(0,570,20,0,player)) return new Data(true, player.getY());
            return new Data(false,0);
        }
        else if (getType() == Type.truck3) {
            if (checkHitbox(270,600,210,180,player)) return new Data(true, player.getY());
            else if (checkHitbox(155,270,80,60,player)) return new Data(true, player.getY());
            else if (checkHitbox(13,155,130,100,player)) return new Data(true, player.getY());
            return new Data(false, 0);
        }
        return new Data(false,0);

    }

    // helper function that reduces the amount of code duplication in the overlap function
    // the Y values are from the top as it is in gimp
    private boolean checkHitbox(int leftX, int rightX, int lowY,int highY, Sprite player) {
        return player.getX() + player.getWidth() > this.getX() + leftX &&
                player.getX() < this.getX() + rightX &&
                player.getY() > this.getY() + this.getHeight() - lowY &&
                player.getY() < this.getY() + this.getHeight() - highY;
    }

    public static Platform createPlatform(Type type) {
        Platform plat = new Platform(type, getTexture(type), 0, 0, 100, 10); // default shouldn't be the case
        switch (type) {
            case test:
                plat = new Platform(type, getTexture(type), 0, 0, 100, 10);
            break;
            case carRed:
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
            case truck3:
                plat =  new Platform(type, getTexture(type), 0, 0, 600, 263);
                break;

        }
        return plat;
    }

    public static Platform createRandomPlatform() {
        Platform plat;
        double random = Math.random()*100; // random number to decide which platfrom is chossen
        if (random < 15) plat = createPlatform(Type.carRed);
        else if (random < 35) plat = createPlatform(Type.carGrey);
        else if (random < 55) plat = createPlatform(Type.carGreen);
        else if (random < 70) plat = createPlatform(Type.truck1);
        else if (random < 85) plat = createPlatform(Type.truck2);
        else plat = createPlatform(Type.truck3);
        return plat;
    }

    private static Texture getTexture(Type type) {
        Texture texture = null;
        switch (type) {
            case test: texture = new Texture("jumpAndRunSprites\\platform.png");
                break;
            case carRed: texture = new Texture("jumpAndRunSprites\\car1.png");
                break;
            case carGrey: texture = new Texture("jumpAndRunSprites\\car_grey.png");
                break;
            case carGreen: texture = new Texture("jumpAndRunSprites\\car_green.png");
                break;
            case truck1: texture = new Texture("jumpAndRunSprites\\truck1.png");
                break;
            case truck2: texture = new Texture("jumpAndRunSprites\\truck2.png");
                break;
            case truck3: texture = new Texture("jumpAndRunSprites\\truck3.png");
                break;
        }
        return  texture;
    }


}
