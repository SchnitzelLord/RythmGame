package com.mygdx.game.ocarinagame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Conductor;
import com.mygdx.game.MainMenuScreen;
import com.mygdx.game.PauseScreen;
import com.mygdx.game.Start;

import java.util.*;

public class OcarinaGame implements Screen {
    private static final int SCREEN_WIDTH = 1920;
    private static final int SCREEN_HEIGHT = 1080;
    private static final int WORLD_WIDTH = 250;
    private static final int WORLD_HEIGHT = WORLD_WIDTH * 9 / 16;
    private static final float SPAWN_POSITION_OFFSET = 10;
    private static final float ARROW_UPTIME = 0.5f;
    private static final float SPAWN_OFFSET = 0.35f;
    private static final boolean IS_PENALTY_ON = true;
    private static final boolean IS_ARROW_POSITION_OFFSET_ACTIVE = false;
    private static final int FINISH_SCORE = 50;

    private final float playerX;
    private final float playerY;

    private final Start game;
    private final Music song;
    private final Conductor conductor;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final HUD hud;

    private final Texture playerTexture;
    private final Texture arrowTexture;

    private final Sprite player;
    private final Array<Arrow> allArrows;
    private final Array<Arrow> upArrows;
    private final Array<Arrow> leftArrows;
    private final Array<Arrow> downArrows;
    private final Array<Arrow> rightArrows;

    private boolean isRunning;
    private int score;
    private int lastArrowDirectionInt;


    // Constructor

    public OcarinaGame(Start game) {
        this.game = game;

        // Setup audio
        song = Gdx.audio.newMusic(Gdx.files.internal("Music\\testBeat.mp3"));
        conductor = new Conductor(120, 0);

        // Setup camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Setup viewport for scaling
        viewport = new FillViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);

        // Setup UI
        hud = new HUD(game.batch, this);

        // Setup textures
        playerTexture = new Texture("ocarina-game\\player.png");
        arrowTexture = new Texture("ocarina-game\\arrow-up.png");

        // Setup player
        player = new Sprite(playerTexture, playerTexture.getWidth(), playerTexture.getHeight());
        playerX = (WORLD_WIDTH - player.getWidth()) * 0.5f;
        playerY = (WORLD_HEIGHT - player.getHeight()) * 0.5f;

        player.setPosition(playerX, playerY);

        allArrows = new Array<>();
        upArrows = new Array<>();
        leftArrows = new Array<>();
        downArrows = new Array<>();
        rightArrows = new Array<>();

        lastArrowDirectionInt = -1;
        conductor.lastBeat = SPAWN_OFFSET;
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

    public int getScore() {
        return score;
    }

    // Utility and functionality methods

    private void reduceScore() {
        if (score > 0) score--;
    }

    private void removeArrowOfDirection(Arrow.Direction direction) {
        // Remove the oldest arrow with specific direction and adjust position by filling the gap after removal
        // Sort array to easily remove arrow with smallest spawntime (earliest spawn)
        Arrow toRemove = null;
        switch (direction) {
            case UP:
                upArrows.sort(Comparator.comparingDouble(Arrow::getSpawnTime));
                if (IS_ARROW_POSITION_OFFSET_ACTIVE) {
                    for (int i = 1; i < upArrows.size; i++) {
                        transferPositionFromTo(upArrows.get(i-1), upArrows.get(i));
                    }
                }
                toRemove = upArrows.removeIndex(0);
                break;
            case LEFT:
                leftArrows.sort(Comparator.comparingDouble(Arrow::getSpawnTime));
                if (IS_ARROW_POSITION_OFFSET_ACTIVE) {
                    for (int i = 1; i < leftArrows.size; i++) {
                        transferPositionFromTo(leftArrows.get(i - 1), leftArrows.get(i));
                    }
                }
                toRemove = leftArrows.removeIndex(0);
                break;
            case DOWN:
                downArrows.sort(Comparator.comparingDouble(Arrow::getSpawnTime));
                if (IS_ARROW_POSITION_OFFSET_ACTIVE) {
                    for (int i = 1; i < downArrows.size; i++) {
                        transferPositionFromTo(downArrows.get(i - 1), downArrows.get(i));
                    }
                }
                toRemove = downArrows.removeIndex(0);
                break;
            case RIGHT:
                rightArrows.sort(Comparator.comparingDouble(Arrow::getSpawnTime));
                if (IS_ARROW_POSITION_OFFSET_ACTIVE) {
                    for (int i = 1; i < rightArrows.size; i++) {
                        transferPositionFromTo(rightArrows.get(i - 1), rightArrows.get(i));
                    }
                }
                toRemove = rightArrows.removeIndex(0);
                break;
        }
        allArrows.removeValue(toRemove, true);
    }

    private void transferPositionFromTo(Arrow arrow1, Arrow arrow2) {
        Sprite arr1 = arrow1.getSprite();
        arrow2.getSprite().setPosition(arr1.getX(), arr1.getY());
    }

    private boolean arrowCanSpawn() {
        // Calculate time when next beat will happen
        conductor.nextBeatTime = conductor.lastBeat + conductor.crochet;

        // Spawn arrow when nextBeatTime is reached
        if (song.getPosition() >= conductor.nextBeatTime) {
            conductor.lastBeat = song.getPosition();
            return true;
        }
        return false;
    }

    private void spawnArrow() {
        Sprite sprite = new Sprite(arrowTexture, arrowTexture.getWidth(), arrowTexture.getHeight());
        int dirInt = MathUtils.random(0, 3);

        // Prevent arrow of same direction to spawn consecutively
        while (lastArrowDirectionInt == dirInt) dirInt = MathUtils.random(0, 3);

        sprite.setRotation(dirInt * 90); // counter-clock rotation, beginning with up-arrow
        Arrow.Direction direction = Arrow.Direction.fromInt(dirInt);

        Arrow a = new Arrow(sprite, direction, song.getPosition());
        allArrows.add(a);

        int offsetFactor = IS_ARROW_POSITION_OFFSET_ACTIVE ? 1 : 0;

        // Setup spawnpoint depending on arrow direction and duplicates
        switch (direction) {
            case UP:
                float multUpOffset = sprite.getHeight() * upArrows.size;
                sprite.setPosition(playerX, playerY + player.getHeight() + SPAWN_POSITION_OFFSET + multUpOffset* offsetFactor);
                upArrows.add(a);
                break;
            case DOWN:
                float multDownOffset = sprite.getHeight() * downArrows.size;
                sprite.setPosition(playerX, playerY - player.getHeight() - SPAWN_POSITION_OFFSET - multDownOffset * offsetFactor);
                downArrows.add(a);
                break;
            case LEFT:
                float multLeftOffset = sprite.getWidth() * leftArrows.size;
                sprite.setPosition(playerX - player.getWidth() - SPAWN_POSITION_OFFSET - multLeftOffset * offsetFactor, playerY);
                leftArrows.add(a);
                break;
            case RIGHT:
                float multRightOffset = sprite.getWidth() * rightArrows.size;
                sprite.setPosition(playerX + player.getWidth() + SPAWN_POSITION_OFFSET + multRightOffset * offsetFactor, playerY);
                rightArrows.add(a);
                break;
        }

        lastArrowDirectionInt = dirInt;
    }

    private void controls() {
        // Check pause key being pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) pauseGame();

        // Check for every arrow if correct key has been pressed
        for (Arrow currArrow : allArrows) {
            Arrow.Direction direction = currArrow.getDirection();

            if (Gdx.input.isKeyJustPressed(Input.Keys.W) && direction == Arrow.Direction.UP ||
                    Gdx.input.isKeyJustPressed(Input.Keys.A) && direction == Arrow.Direction.LEFT ||
                    Gdx.input.isKeyJustPressed(Input.Keys.S) && direction == Arrow.Direction.DOWN ||
                    Gdx.input.isKeyJustPressed(Input.Keys.D) && direction == Arrow.Direction.RIGHT) {

                    score++;
                    removeArrowOfDirection(direction);

                // Penalty for just pressing keys at random
            } else if (IS_PENALTY_ON &&
                    (Gdx.input.isKeyJustPressed(Input.Keys.W) ||
                    Gdx.input.isKeyJustPressed(Input.Keys.A) ||
                    Gdx.input.isKeyJustPressed(Input.Keys.S) ||
                    Gdx.input.isKeyJustPressed(Input.Keys.D))) {

                reduceScore();
            }
        }
    }

    private void checkWinCondition() {
        if (score >= FINISH_SCORE) {
            isRunning = false;
            song.pause();
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
    }

    private void pauseGame() {
        isRunning = false;
        song.pause();
        game.setScreen(new PauseScreen(game, this));
    }

    @Override
    public void show() {
        isRunning = true;
        song.setVolume(Start.volume);
        song.play();
        conductor.start();
    }

    private void draw() {
        // Setup SpriteBatch and draw sprites
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        player.draw(game.batch);
        for (Arrow currArrow : allArrows) {
            currArrow.getSprite().draw(game.batch);
            // Remove arrow at certain time interval
            if (song.getPosition() - currArrow.getSpawnTime() > ARROW_UPTIME) {
                removeArrowOfDirection(currArrow.getDirection());
                if (IS_PENALTY_ON) reduceScore();
            }
        }

        game.batch.end();
    }

    @Override
    public void render(float delta) {
        if (isRunning) {
            ScreenUtils.clear(Color.PURPLE);

            camera.update();
            controls();
            draw();

            // Spawn arrow at certain interval
            if (arrowCanSpawn()) spawnArrow();

            checkWinCondition();

            hud.update();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
        pauseGame();
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        isRunning = false;
        song.dispose();
        playerTexture.dispose();
        arrowTexture.dispose();
        hud.dispose();
    }
}