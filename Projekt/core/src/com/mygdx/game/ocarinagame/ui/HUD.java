package com.mygdx.game.ocarinagame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.ocarinagame.levels.AbstractOcarinaGame;


public final class HUD implements Disposable {
    private static final int POSITION_OFFSET = 20;
    private static final boolean isProgressBarActive = true;
    private static final boolean isScoreProgressBarActive = false;

    private final AbstractOcarinaGame game;
    private final SpriteBatch spriteBatch;

    private final Stage stage;
    private final Viewport viewport;

    private ProgressBar scoreProgressBar;
    private ProgressBar progressBar;
    private final Array<Sprite> hearts;

    private final Texture heartTexture;

    private float progress = 0.0f;

    // Constructor

    public HUD(SpriteBatch spriteBatch, AbstractOcarinaGame game) {
        this.game = game;
        this.spriteBatch = spriteBatch;

        viewport = new FillViewport(game.getWorldWidth(), game.getWorldHeight(), new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        createProgressBars();

        stage.addActor(progressBar);

        hearts = new Array<>();
        heartTexture = new Texture("ocarina-game\\heartsprite.png");
        addHeartBar();
    }

    // Getter

    public ProgressBar getScoreProgressBar() {
        return scoreProgressBar;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    // Functional methods

    public void updateScore() {
        scoreProgressBar.setValue(game.getScore() + getRenderOffset(scoreProgressBar));
    }

    public void updateProgress() {
        progressBar.setValue(game.getMusic().getSong().getPosition() + getRenderOffset(progressBar));
    }

    public void draw() {
        stage.draw();
        stage.act();
        spriteBatch.setProjectionMatrix(stage.getCamera().combined);
        spriteBatch.begin();
        for (int i = 0; i < game.getLives(); i++) {
            hearts.get(i).draw(spriteBatch);
        }
        spriteBatch.end();
    }

    // Utility methods

    private float getRenderOffset(ProgressBar progressBar) {
        // Calculate offset for progress bar value since TEXTURE_OFFSET pixel on the left will (and should) not be stretched
        float pxPerValue = progressBar.getWidth() / progressBar.getMaxValue();
        float offset = progressBar.getTextureOffset() / pxPerValue;
        return offset;
    }

    private void createProgressBars() {
        if (isScoreProgressBarActive) {
            // Create progress bar for score with default values
            scoreProgressBar = new ProgressBar(0, 10, 1, 0);
            // Position at top right corner
            scoreProgressBar.setPosition(game.getScreenWidth() - scoreProgressBar.getWidth() - POSITION_OFFSET, game.getScreenHeight() - scoreProgressBar.getHeight() - POSITION_OFFSET);
        }

        if (isProgressBarActive) {
            // Create progress bar for progress with default values
            progressBar = new ProgressBar(0, 10, 0.1f, 0.25f);
            // Position at top right corner
            progressBar.setPosition(game.getScreenWidth() - progressBar.getWidth() - POSITION_OFFSET, game.getScreenHeight() - progressBar.getHeight() - POSITION_OFFSET);
        }
    }

    private void addHeartBar() {
        float positionOffset = 10;
        for (int i = 0;i < game.getLives(); i++) {
            Sprite heart = new Sprite(heartTexture, heartTexture.getWidth(), heartTexture.getHeight());
            hearts.add(heart);
            heart.setX(positionOffset + (heart.getWidth() + positionOffset) * i);
            heart.setY(game.getWorldHeight() - heart.getHeight() - positionOffset);
        }
    }

    private void progressBarTest() {
        // Test animation of progress bar by increasing value by time
        progress += (Gdx.graphics.getDeltaTime() * 5);
        if (progress >= getScoreProgressBar().getMaxValue()+1) progress = 0;
        scoreProgressBar.setValue(progress);
    }

    // Overrides

    @Override
    public void dispose() {
        stage.dispose();
        scoreProgressBar.dispose();
        heartTexture.dispose();
    }
}
