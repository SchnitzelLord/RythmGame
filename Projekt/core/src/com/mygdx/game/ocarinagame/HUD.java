package com.mygdx.game.ocarinagame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class HUD implements Disposable {
    private final AbstractOcarinaGame game;

    private final Stage stage;
    private final Viewport viewport;

    private final ScoreProgressBar progressBar;

    private float progress = 0.0f;

    // Constructor

    HUD(SpriteBatch spriteBatch, AbstractOcarinaGame game) {
        this.game = game;

        viewport = new FillViewport(game.getWorldWidth(), game.getWorldHeight(), new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        // Create progress bar
        progressBar = new ScoreProgressBar(0, game.getFinishScore(), 1, 0);;

        stage.addActor(progressBar);
    }

    // Getter

    public ScoreProgressBar getProgressBar() {
        return progressBar;
    }

    // Functional methods

    void update() {
        // Calculate offset for progress bar value since some pixel on the left will (and should) not be stretched
        int pxPerScore = MathUtils.round((1.0f / game.getFinishScore()) * progressBar.getWidth());
        progressBar.setValue(game.getScore() + pxPerScore);
//        progress += (Gdx.graphics.getDeltaTime() * 5);
//        if (progress >= getProgressBar().getMaxValue()+1) progress = 0;
//        progressBar.setValue(progress);
    }

    void draw() {
        stage.draw();
        stage.act();
    }

    @Override
    public void dispose() {
        stage.dispose();
        progressBar.dispose();
    }
}
