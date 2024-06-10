package com.mygdx.game.ocarinagame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class HUD implements Disposable {
    private final AbstractOcarinaGame game;

    private final Stage stage;
    private final Viewport viewport;

//    private final Label scoreLabel;

    private final ScoreProgressBar progressBar;

    HUD(SpriteBatch spriteBatch, AbstractOcarinaGame game) {
        this.game = game;

        viewport = new FillViewport(game.getWorldWidth(), game.getWorldHeight(), new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        // Setup score label
//        scoreLabel = new Label(String.format("Score: %d", game.getScore()), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
//        scoreLabel.setFontScale(0.3f);

        // Create progress bar
        progressBar = new ScoreProgressBar(0, game.getFinishScore(), 1, 0.25f);
        System.out.println("Finish Score: " + game.getFinishScore());
        progressBar.setFillParent(true);

        // Organize HUD in table
        Table table = new Table();
        table.center(); // center has coordinates (0, 0)
        table.setFillParent(true);
        table.setPosition(0, -game.getWorldHeight() + progressBar.getHeight());

        table.add(progressBar).left().expandX();
        //table.add(scoreLabel).right().padRight(10);

        stage.addActor(table);
    }

    void update() {
//        scoreLabel.setText(String.format("Score: %d", game.getScore()));
        progressBar.setValue(game.getScore());
        //System.out.println("Score: " + game.getScore());

        stage.draw();
        stage.act();
    }

    @Override
    public void dispose() {
        stage.dispose();
        progressBar.dispose();
    }
}
