package com.mygdx.game.ocarinagame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class HUD implements Disposable {
    private final AbstractOcarinaGame game;

    private final Stage stage;
    private final Viewport viewport;

    private final ScoreProgressBar progressBar;

    // Constructor

    HUD(SpriteBatch spriteBatch, AbstractOcarinaGame game) {
        this.game = game;

        viewport = new FillViewport(game.getWorldWidth(), game.getWorldHeight(), new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        // Create progress bar
        progressBar = new ScoreProgressBar(0, game.getFinishScore(), 1, 0.25f);
        progressBar.setFillParent(false);

        stage.addActor(progressBar);
    }

    // Getter

    public float getProgressBarHeight() {
        return progressBar.getHeight();
    }

    public ScoreProgressBar getProgressBar() {
        return progressBar;
    }

    // Functional methods

    void update() {
        progressBar.setValue(game.getScore());
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
