package com.mygdx.game.ocarinagame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.ocarinagame.levels.AbstractOcarinaGame;


public final class HUD implements Disposable {
    private final AbstractOcarinaGame game;

    private final Stage stage;
    private final Viewport viewport;

    private final ScoreProgressBar progressBar;

    private float progress = 0.0f;

    // Constructor

    public HUD(SpriteBatch spriteBatch, AbstractOcarinaGame game) {
        this.game = game;

        viewport = new FillViewport(game.getWorldWidth(), game.getWorldHeight(), new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        // Create progress bar with default values
        progressBar = new ScoreProgressBar(0, 10, 1, 0);;

        stage.addActor(progressBar);
    }

    // Getter

    public ScoreProgressBar getProgressBar() {
        return progressBar;
    }

    // Functional methods

    public void update() {
        // Calculate offset for progress bar value since one pixel on the left will (and should) not be stretched
        float pxPerValue = progressBar.getWidth() / progressBar.getMaxValue();
        float scoreOffset = 1 / pxPerValue;
        progressBar.setValue(game.getScore() + scoreOffset);
    }

    public void draw() {
        stage.draw();
        stage.act();
    }

    // Utility methods

    private void progressBarTest() {
        // Test animation of progress bar by increasing value by time
        progress += (Gdx.graphics.getDeltaTime() * 5);
        if (progress >= getProgressBar().getMaxValue()+1) progress = 0;
        progressBar.setValue(progress);
    }

    // Overrides

    @Override
    public void dispose() {
        stage.dispose();
        progressBar.dispose();
    }
}
