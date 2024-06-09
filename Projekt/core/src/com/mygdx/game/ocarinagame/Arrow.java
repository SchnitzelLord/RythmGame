package com.mygdx.game.ocarinagame;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Arrow {

    public enum Direction {
        UP, LEFT, DOWN, RIGHT;

        public static Direction fromInt(int integer) {
            switch (integer) {
                case 0: return UP;
                case 1: return LEFT;
                case 2: return DOWN;
                case 3: return RIGHT;
            }
            return null;
        }
    }

    private final Direction direction;
    private final Sprite sprite;
    private final float spawnTime;

    public Arrow(Sprite sprite, Direction direction, float spawnTime) {
        this.sprite = sprite;
        this.direction = direction;
        this.spawnTime = spawnTime;
    }

    public Direction getDirection() {
        return direction;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public float getSpawnTime() {
        return spawnTime;
    }
}