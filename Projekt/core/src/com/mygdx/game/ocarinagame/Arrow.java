package com.mygdx.game.ocarinagame;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Arrow {
    private final Sprite sprite;
    private final Direction direction;
    private final float spawnTime;

    // Member class for arrow directions
    public enum Direction {
        UP, LEFT, DOWN, RIGHT;

        // Static method to get direction from int
        public static Direction fromInt(int integer) {
            switch (integer) {
                case 0: return UP;
                case 1: return LEFT;
                case 2: return DOWN;
                case 3: return RIGHT;
                default: return null;
            }
        }
    }

    // Constructor

    public Arrow(Sprite sprite, Direction direction, float spawnTime) {
        this.sprite = sprite;
        this.direction = direction;
        this.spawnTime = spawnTime;
    }

    // Getter

    public Sprite getSprite() {
        return sprite;
    }

    public Direction getDirection() {
        return direction;
    }

    public float getSpawnTime() {
        return spawnTime;
    }
}
