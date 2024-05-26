package com.mygdx.game;

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

import java.util.Iterator;

public class OcarinaGame implements Screen {
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final float HIT_OFFSET = 10;
    private static long ARROW_UPTIME = 1_000_000_000;

    private final float playerX;
    private final float playerY;

    private final Start game;
    private final Conductor conductor;
    private final Music song;
    private final OrthographicCamera camera;

    private final Texture playerTexture;
    private final Texture arrowTexture;

    private final BitmapFont font;

    private final Sprite player;
    private final Array<Arrow> arrows;

    private boolean isRunning;
    private int hits;
    private long lastArrowSpawn;
    private float spawnRateNano;

    private int nLeftArrows;
    private int nRightArrows;
    private int nUpArrows;
    private int nDownArrows;

    public OcarinaGame(Start game) {
        this.game = game;

        // Setup UI
        font = new BitmapFont();

        // Setup audio
        conductor = new Conductor(120, 0);
        song = Gdx.audio.newMusic(Gdx.files.internal("Music\\testBeat.mp3"));

        // Setup camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);

        // Setup textures
        playerTexture = new Texture("characterSprite\\playerSprite.png");
        arrowTexture = new Texture("ocarina-game\\arrow-up.png");

        // Setup player
        player = new Sprite(playerTexture, playerTexture.getWidth(), playerTexture.getHeight());
        playerX = WIDTH / 2.0f - player.getWidth();
        playerY = HEIGHT / 2.0f - player.getHeight();
        player.setPosition(playerX, playerY);

        arrows = new Array<>();
    }

    @Override
    public void show() {
        isRunning = true;

        conductor.start();
        song.setVolume(Start.volume);
        song.play();

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
            font.draw(game.batch, "Hits: " + hits, 20, HEIGHT - 20);
            for (Arrow currArrow : arrows) {
                currArrow.getSprite().draw(game.batch);
                if (TimeUtils.nanoTime() - currArrow.getSpawnTime() > ARROW_UPTIME) {
                    remove(currArrow);
                    reduceScore();
                }
            }

            game.batch.end();

            // moveArrowDown();

            // Spawn arrow at certain interval
            if (TimeUtils.nanoTime() - lastArrowSpawn > spawnRateNano) spawnArrow();

            checkWinCondition();
        }
    }

    private void reduceScore() {
        if (hits > 0) hits--;
    }

    private void remove(Arrow arrow) {
        int idx = arrows.indexOf(arrow, true);
        arrows.removeIndex(idx);

        // Reduce counter
        switch (arrow.getDirection()) {
            case UP:
                nUpArrows--;
                break;
            case DOWN:
                nDownArrows--;
                break;
            case LEFT:
                nLeftArrows--;
                break;
            case RIGHT:
                nRightArrows--;
                break;
        }
    }

    private void spawnArrow() {
        Sprite sprite = new Sprite(arrowTexture, arrowTexture.getWidth(), arrowTexture.getHeight());
        int dirInt = MathUtils.random(0, 3);

        sprite.setRotation(dirInt * 90); // counter-clock rotation, beginning with up-arrow
        Arrow.Direction direction = Arrow.Direction.fromInt(dirInt);

        // Setup spawnpoint depending on arrow direction
        switch (direction) {
            case UP:
                sprite.setPosition(playerX, playerY + player.getHeight() + (HIT_OFFSET + sprite.getHeight()) * nUpArrows);
                nUpArrows++;
                break;
            case DOWN:
                sprite.setPosition(playerX, playerY - player.getHeight() - (HIT_OFFSET + sprite.getHeight()) * nDownArrows);
                nDownArrows++;
                break;
            case LEFT:
                sprite.setPosition(playerX - player.getWidth() - (HIT_OFFSET + sprite.getWidth()) * nLeftArrows, playerY);
                nLeftArrows++;
                break;
            case RIGHT:
                sprite.setPosition(playerX + player.getWidth() + (HIT_OFFSET + sprite.getWidth()) * nRightArrows, playerY);
                nRightArrows++;
                break;
        }

        lastArrowSpawn = TimeUtils.nanoTime();

        arrows.add(new Arrow(sprite, direction, lastArrowSpawn));
    }

    private void controls() {
        // Check pause key being pressed
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) pauseGame();

        // Check for every arrow if correct key has been pressed
        for (Iterator<Arrow> iter = arrows.iterator(); iter.hasNext(); ) {
            Arrow currArrow = iter.next();
            Arrow.Direction direction = currArrow.getDirection();

            if (Gdx.input.isKeyPressed(Input.Keys.W) && direction == Arrow.Direction.UP ||
                Gdx.input.isKeyPressed(Input.Keys.A) && direction == Arrow.Direction.LEFT ||
                Gdx.input.isKeyPressed(Input.Keys.S) && direction == Arrow.Direction.DOWN ||
                Gdx.input.isKeyPressed(Input.Keys.D) && direction == Arrow.Direction.RIGHT) {

                hits++;
                remove(currArrow);

            // Penalty for just pressing at random
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.W) ||
                        Gdx.input.isKeyJustPressed(Input.Keys.A) ||
                        Gdx.input.isKeyJustPressed(Input.Keys.S) ||
                        Gdx.input.isKeyJustPressed(Input.Keys.D)) {

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
