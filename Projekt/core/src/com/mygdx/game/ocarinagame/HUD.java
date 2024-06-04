package com.mygdx.game.ocarinagame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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

    private final ProgressBar progressBar;
    private final Texture progressBarTexture;
    private final Texture progressBarEmptyTexture;

    private final Skin skin;

    HUD(SpriteBatch spriteBatch, OcarinaGame game) {
        this.game = game;
        score = 0;

        viewport = new FillViewport(game.getWorldWidth(), game.getWorldHeight(), new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        skin = new Skin();

        // Setup progress bar
        progressBarTexture = new Texture(Gdx.files.internal("ocarina-game/progress-bar.png"));
        progressBarEmptyTexture = new Texture(Gdx.files.internal("ocarina-game/progress-bar-empty.png"));
        skin.add("progressBar", progressBarTexture);
        skin.add("progressBarEmpty", progressBarEmptyTexture);

        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
        progressBarStyle.background = skin.getDrawable("progressBarEmpty");
        progressBarStyle.knobBefore = skin.getDrawable("progressBar");
        progressBar = new ProgressBar(0, 100, 1, false, progressBarStyle);
        progressBar.setValue(0);
        progressBar.setAnimateDuration(0.5f);

        // Setup score label
        scoreLabel = new Label(String.format("Score: %d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel.setFontScale(0.3f);

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
        progressBarTexture.dispose();
        progressBarEmptyTexture.dispose();
        skin.dispose();
    }
}
