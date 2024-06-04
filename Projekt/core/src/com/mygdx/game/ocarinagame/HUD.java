package com.mygdx.game.ocarinagame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


class HUD implements Disposable {
    private final OcarinaGame game;

    private final Stage stage;
    private final Viewport viewport;

    private int score;
    private final Label scoreLabel;

    private final ScoreProgressBar progressBar;

    HUD(SpriteBatch spriteBatch, OcarinaGame game) {
        this.game = game;
        score = 0;

        viewport = new FillViewport(game.getWorldWidth(), game.getWorldHeight(), new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        // Setup score label
        scoreLabel = new Label(String.format("Score: %d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel.setFontScale(0.3f);

        // Create progress bar
        progressBar = new ScoreProgressBar(0, 100, 1, 0.5f);

        // Organize HUD in table
        Table table = new Table();
        table.center();
        table.setFillParent(true);
        table.setPosition(0, 60);

        table.add(progressBar).expandX().left().padLeft(5).top();
        table.add(scoreLabel).expandX().right().padRight(10).top();

        stage.addActor(table);
    }

    void update() {
        score = game.getScore();
        scoreLabel.setText(String.format("Score: %d", score));
        progressBar.setValue(score);

        stage.draw();
        stage.act();
    }

    @Override
    public void dispose() {
        stage.dispose();
        progressBar.dispose();
    }
}
