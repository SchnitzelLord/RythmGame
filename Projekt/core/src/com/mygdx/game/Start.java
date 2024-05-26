package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Start extends Game {

    public SpriteBatch batch;
    public static float volume;
    public static Texture playerTexture;

    @Override
    public void create() {
        volume = 0.5f;
        batch = new SpriteBatch();
        this.setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        batch.dispose();
        playerTexture.dispose();

    }
}
