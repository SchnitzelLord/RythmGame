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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class OcarinaGame implements Screen {
    private final Start game;
    private final Conductor conductor;
    private final Music song;
    private final OrthographicCamera camera;

    private final Texture playerTexture;
    private final Texture arrowTexture;

    private final BitmapFont font;

    private final Sprite player;
    private final Array<Sprite> arrows;
    private final Array<Sprite> arrowBuffer;

    private final int WIDTH = 1920;
    private final int HEIGHT = 1080;

    private final int playerX = WIDTH / 2 - 32;
    private final int playerY = HEIGHT / 2 - 32;

    private boolean isRunning;
    private boolean canHit;
    private int hits;
    private long lastArrowSpawn;
    private float SPEED = 900;
    private float hitPosition;

    public OcarinaGame(Start game) {
        this.game = game;
        hits = 0;
        lastArrowSpawn = 0;

        font = new BitmapFont();

        conductor = new Conductor(120, 0);
        song = Gdx.audio.newMusic(Gdx.files.internal("Music\\testBeat.mp3"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);

        playerTexture = new Texture("characterSprite\\playerSprite.png");
        arrowTexture = new Texture("ocarina-game\\arrow-up.png");

        player = new Sprite(playerTexture, 64, 64);
        player.setPosition(playerX, playerY);

        hitPosition = player.getY() + player.getHeight() + 10;

        arrowBuffer = new Array<>();
        arrows = new Array<>();
    }

    @Override
    public void show() {
        isRunning = true;
        canHit = false;
        conductor.start();
        song.setVolume(Start.volume);
        song.play();
    }

    @Override
    public void render(float delta) {
        if (isRunning) {
            ScreenUtils.clear(Color.PURPLE);

            camera.update();
            controls();

            checkIsHittable();

            game.batch.setProjectionMatrix(camera.combined);

            game.batch.begin();

            player.draw(game.batch);
            font.draw(game.batch, "Hits: " + hits, 20, HEIGHT - 20);
            for (Sprite s : arrows) {
                s.draw(game.batch);
            }

            game.batch.end();

            for (Iterator<Sprite> iter = arrows.iterator(); iter.hasNext(); ) {
                Sprite s = iter.next();
                s.translateY(-SPEED * Gdx.graphics.getDeltaTime());
                if (s.getY() < hitPosition) {
                    iter.remove();
                }
            }

            if (TimeUtils.nanoTime() - lastArrowSpawn > 1000000000) spawnArrow();
        }
    }

    private void spawnArrow() {
        Sprite arrow = new Sprite(arrowTexture, 64, 64);
        arrow.setRotation(0);
        arrow.setPosition(playerX, HEIGHT + 64);
        arrows.add(arrow);
        lastArrowSpawn = TimeUtils.nanoTime();
    }

    private void controls() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) pauseGame();

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (canHit) hits++;
        }
    }

    private void pauseGame() {
        isRunning = false;
        song.pause();
        game.setScreen(new PauseScreen(game, this));
    }

    private void checkIsHittable() {
        float offset = 5f;

        float leftInterval = conductor.lastBeat + conductor.crochet - offset;
        if (leftInterval < 0) leftInterval = 0;
        float rightInterval = conductor.lastBeat + conductor.crochet + offset;

        if (leftInterval <= song.getPosition() && song.getPosition() <= rightInterval) {
            canHit = true;
            conductor.lastBeat += conductor.crochet;
        } else {
            canHit = false;
        }
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
