package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FillViewport;

public class MazeHud {

    private Texture beatSymbolTexture;

    private Image beatSymbol;

    MazeHud(Start game) {
        beatSymbolTexture = new Texture(Gdx.files.internal("MazeLevel\\beat.png"));
        beatSymbol = new Image(beatSymbolTexture);
        beatSymbol.setPosition(1920 / 2, 1080 / 2);

    }

    public Image getSymbol() {
        return beatSymbol;
    }

    public void dispose() {
        beatSymbolTexture.dispose();
    }
}
