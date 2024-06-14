package com.mygdx.game.ocarinagame.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Conductor;
import com.mygdx.game.PauseScreen;
import com.mygdx.game.Start;
import com.mygdx.game.ocarinagame.Arrow;
import com.mygdx.game.ocarinagame.BeatMusic;
import com.mygdx.game.ocarinagame.ui.HUD;

public abstract class AbstractOcarinaGame implements Screen {
    // Game and screen sizes
    protected static final int SCREEN_WIDTH = 1920;
    protected static final int SCREEN_HEIGHT = 1080;
    protected static final int WORLD_HEIGHT = 1080;
    protected static final int WORLD_WIDTH = 1920;

    // Offsets
    protected static final float FLOAT_COMPARE_OFFSET = 0.01f; // for float comparisons (changing bpm at specific time)
    protected static final float ARROW_SPAWN_POSITION_OFFSET = 10;

    // Game state constants
    protected static final float WIN_RATE = 0.5f;
    protected static final float GAME_OVER_DELAY = 5;

    // Game elements
    protected final Start game;
    protected final OrthographicCamera camera;
    protected final Viewport viewport;

    protected HUD hud;
    protected Conductor conductor;
    protected BeatMusic music;

    // Game state
    protected boolean areLivesActive;
    protected boolean isPenaltyOn;
    protected boolean isRunning;
    protected int score;
    protected int lastArrowDirectionInt;
    protected static int lives;

    // Textures
    protected final Texture arrowTexture;
    protected Texture backgroundTexture;
    protected Image background;

    // Collections
    protected final Array<Arrow> allArrows;

    // Constructor

    protected AbstractOcarinaGame(Start game) {
        this.game = game;

        // Setup camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Setup viewport for scaling
        viewport = new FillViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);

        // Setup textures
        arrowTexture = new Texture("ocarina-game\\arrow-up-fhd.png");

        allArrows = new Array<>();

        // Default values for game state
        isPenaltyOn = false;
        isRunning = false;
        score = 0;
        lastArrowDirectionInt = -1;
        lives = 3;
        areLivesActive = true;
    }

    // Getter

    public int getWorldWidth() {
        return WORLD_WIDTH;
    }

    public int getWorldHeight() {
        return WORLD_HEIGHT;
    }

    public int getScreenWidth() {
        return SCREEN_WIDTH;
    }

    public int getScreenHeight() {
        return SCREEN_HEIGHT;
    }

    public BeatMusic getMusic() {
        return music;
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    // Overrides of functionality methods

    @Override
    public void render(float delta) {
        if (isRunning) {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            camera.update();
            //hud.updateScore();
            hud.updateProgress();
        }
    }

    @Override
    public void show() {
        isRunning = true;
        music.setVolume(Start.volume);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
        switchToScreen(new PauseScreen(game, this));
    }

    @Override
    public void resume() {
        isRunning = true;
        music.play();
    }

    @Override
    public void hide() {
        isRunning = false;
        music.pause();
    }

    @Override
    public void dispose() {
        isRunning = false;
        music.dispose();
        arrowTexture.dispose();
        backgroundTexture.dispose();
        hud.dispose();
    }

    // Abstract methods

    protected abstract void setupArrowSpawnPosition(Arrow arrow);

    protected abstract void setBPM(int bpm);

    protected abstract void controlsAction();

    protected abstract boolean canArrowSpawn();

    // Functionality & utility methods for subclasses

    protected void reduceScore() {
        // Reduce score only if player has any score points, no negative score
        if (score > 0) score--;
    }

    protected void reduceLives() {
        if (lives > 0) lives--;
    }

    protected void spawnArrow() {
        Sprite sprite = new Sprite(arrowTexture, arrowTexture.getWidth(), arrowTexture.getHeight());
        // Choose random direction for arrow
        int dirInt = MathUtils.random(0, 3);

        // Prevent arrow of same direction to spawn consecutively
        while (lastArrowDirectionInt == dirInt) dirInt = MathUtils.random(0, 3);

        sprite.setRotation(dirInt * 90); // counter-clock rotation, beginning with up-arrow
        Arrow.Direction direction = Arrow.Direction.fromInt(dirInt);

        Arrow a = new Arrow(sprite, direction, music.getPosition());
        setupArrowSpawnPosition(a);
        allArrows.add(a);

        lastArrowDirectionInt = dirInt;
    }

    protected boolean isInputEqualsDirection(Arrow.Direction direction) {
        // Check if correct key has been pressed
        return isUpKeyPressed() && direction == Arrow.Direction.UP ||
                isLeftKeyPressed() && direction == Arrow.Direction.LEFT ||
                isDownKeyPressed() && direction == Arrow.Direction.DOWN ||
                isRightKeyPressed() && direction == Arrow.Direction.RIGHT;
    }

    protected boolean isAnyDirectionKeyPressed() {
        // Check if any direction key has been pressed
        return isUpKeyPressed() || isLeftKeyPressed() || isDownKeyPressed() || isRightKeyPressed();
    }

    protected void pauseGameOnEscape() {
        // Check pause key being pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            switchToScreen(new PauseScreen(game, this));
        }
    }

    protected boolean isUpKeyPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP);
    }

    protected boolean isLeftKeyPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT);
    }

    protected boolean isRightKeyPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT);
    }

    protected boolean isDownKeyPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN);
    }

    // For differentiating full score win with "acceptable" win
    protected boolean isGamePerfectlyWon() {
        return score >= music.getTotalBeatCount();
    }

    protected boolean hasWinRateBeenReached() {
        return score >= music.getTotalBeatCount() * WIN_RATE;
    }

    protected void switchToScreen(Screen screen) {
        isRunning = false;
        music.pause();
        game.setScreen(screen);
    }

    protected void drawArrows() {
        // Setup SpriteBatch and draw arrows
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        for (Arrow currArrow : allArrows) {
            currArrow.getSprite().draw(game.batch);
        }

        game.batch.end();
    }

    // Check whether game has been won or not after song is over + GAME_OVER_DELAY
    // and switch screen accordingly
    protected Timer delayedSongOverSwitchScreenTimer(float songLength, Screen winScreen, Screen loseScreen) {
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (hasWinRateBeenReached())
                    switchToScreen(winScreen);
                else
                    switchToScreen(loseScreen);
                dispose();
            }
        }, songLength + GAME_OVER_DELAY);
        return timer;
    }
}