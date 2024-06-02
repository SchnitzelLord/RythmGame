package com.mygdx.game.ocarinagame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
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
    private static final int WORLD_HEIGHT = 250;
    private static final float HIT_OFFSET = 10;
    private static final long ARROW_UPTIME = 400_000_000;
    private static final long SPAWN_OFFSET = 50_000_000;
    private static final boolean IS_PENALTY_ON = false;
    private static final boolean IS_ARROW_POSITION_OFFSET_ACTIVE = false;

    private final float playerX;
    private final float playerY;

    private final Start game;
    private final Conductor conductor;
    private final Music song;
    private final OrthographicCamera camera;
    private final Viewport viewport;

    private final Texture playerTexture;
    private final Texture arrowTexture;

    private final BitmapFont font;

    private final Sprite player;
    private final Array<Arrow> allArrows;
    private final Array<Arrow> upArrows;
    private final Array<Arrow> leftArrows;
    private final Array<Arrow> downArrows;
    private final Array<Arrow> rightArrows;

    private boolean isRunning;
    private int hits;
    private long lastArrowSpawn;
    private float spawnRateNano;


    public OcarinaGame(Start game) {
        this.game = game;

        // Setup UI
        font = new BitmapFont();
        font.getData().setScale(0.7f);

        // Setup audio
        conductor = new Conductor(120, 0);
        song = Gdx.audio.newMusic(Gdx.files.internal("Music\\testBeat.mp3"));

        // Setup camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Setup viewport for scaling
        viewport = new FillViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);

        // Setup textures
        playerTexture = new Texture("ocarina-game\\player.png");
        arrowTexture = new Texture("ocarina-game\\arrow-up.png");

        // Setup player
        player = new Sprite(playerTexture, playerTexture.getWidth(), playerTexture.getHeight());
        //playerX = (WIDTH - player.getWidth()) * 0.5f;
        //playerY = (HEIGHT - player.getHeight()) * 0.5f;
        playerX = (WORLD_WIDTH - player.getWidth()) * 0.5f;
        playerY = (WORLD_HEIGHT - player.getHeight()) * 0.5f;

        player.setPosition(playerX, playerY);

        allArrows = new Array<>();
        upArrows = new Array<>();
        leftArrows = new Array<>();
        downArrows = new Array<>();
        rightArrows = new Array<>();
    }

    @Override
    public void show() {
        isRunning = true;

        conductor.start();
        song.setVolume(Start.volume);
        song.play();

        // Spawnrate = 60 * BPM^-1
        // BPM^-1 ... time between beat in minutes
        spawnRateNano = conductor.crochet * 1_000_000_000;
    }

    @Override
    public void render(float delta) {
        if (isRunning) {
            ScreenUtils.clear(Color.PURPLE);

            camera.update();
            controls();

            // Setup SpriteBatch and draw sprites
            game.batch.setProjectionMatrix(camera.combined);
            game.batch.begin();

            player.draw(game.batch);
            font.draw(game.batch, "Hits: " + hits, 5, WORLD_HEIGHT - 60);
            for (Arrow currArrow : allArrows) {
                currArrow.getSprite().draw(game.batch);
                if (TimeUtils.nanoTime() - currArrow.getSpawnTime() > ARROW_UPTIME) {
                    removeArrowOfDirection(currArrow.getDirection());
                    if (IS_PENALTY_ON) reduceScore();
                }
            }

            game.batch.end();

            // Spawn arrow at certain interval
            if (TimeUtils.nanoTime() - lastArrowSpawn > spawnRateNano - SPAWN_OFFSET) spawnArrow();

            checkWinCondition();
        }
    }

    private void reduceScore() {
        if (hits > 0) hits--;
    }

    private void removeArrowOfDirection(Arrow.Direction direction) {
        // Remove the oldest arrow with specific direction and adjust position
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

    private void spawnArrow() {
        Sprite sprite = new Sprite(arrowTexture, arrowTexture.getWidth(), arrowTexture.getHeight());
        int dirInt = MathUtils.random(0, 3);

        sprite.setRotation(dirInt * 90); // counter-clock rotation, beginning with up-arrow
        Arrow.Direction direction = Arrow.Direction.fromInt(dirInt);
        lastArrowSpawn = TimeUtils.nanoTime();
        Arrow a = new Arrow(sprite, direction, lastArrowSpawn);
        allArrows.add(a);

        int offsetFactor = IS_ARROW_POSITION_OFFSET_ACTIVE ? 1 : 0;

        // Setup spawnpoint depending on arrow direction and duplicates
        switch (direction) {
            case UP:
                float multUpOffset = sprite.getHeight() * upArrows.size;
                sprite.setPosition(playerX, playerY + player.getHeight() + HIT_OFFSET + multUpOffset* offsetFactor);
                upArrows.add(a);
                break;
            case DOWN:
                float multDownOffset = sprite.getHeight() * downArrows.size;
                sprite.setPosition(playerX, playerY - player.getHeight() - HIT_OFFSET - multDownOffset * offsetFactor);
                downArrows.add(a);
                break;
            case LEFT:
                float multLeftOffset = sprite.getWidth() * leftArrows.size;
                sprite.setPosition(playerX - player.getWidth() - HIT_OFFSET - multLeftOffset * offsetFactor, playerY);
                leftArrows.add(a);
                break;
            case RIGHT:
                float multRightOffset = sprite.getWidth() * rightArrows.size;
                sprite.setPosition(playerX + player.getWidth() + HIT_OFFSET + multRightOffset * offsetFactor, playerY);
                rightArrows.add(a);
                break;
        }
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

                hits++;
                removeArrowOfDirection(direction);

                // Penalty for just pressing at random
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
        if (hits >= 50) {
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
        song.dispose();
        playerTexture.dispose();
        arrowTexture.dispose();
        font.dispose();
    }
}
