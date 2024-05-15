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
    private final Start game;
    private final Conductor conductor;
    private final Music song;
    private final OrthographicCamera camera;

    private final Texture playerTexture;
    private final Texture arrowTexture;

    private final BitmapFont font;

    private final Sprite player;
    private final Array<Arrow> arrows;

    private final int WIDTH = 1920;
    private final int HEIGHT = 1080;

    private final int playerX = WIDTH / 2 - 32;
    private final int playerY = HEIGHT / 2 - 32;

    private boolean isRunning;
    private boolean canSpawn;
    private int hits;
    private long lastArrowSpawn;
    private float speed;
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

        arrows = new Array<>();

        System.out.println(speed);
    }

    @Override
    public void show() {
        isRunning = true;
        canSpawn = false;
        conductor.start();
        song.setVolume(Start.volume);
        song.play();
        speed = (HEIGHT - hitPosition) / conductor.crochet;
    }

    @Override
    public void render(float delta) {
        if (isRunning) {
            ScreenUtils.clear(Color.PURPLE);

            camera.update();
            controls();

            //checkIsHittable();

            game.batch.setProjectionMatrix(camera.combined);

            game.batch.begin();

            player.draw(game.batch);
            font.draw(game.batch, "Hits: " + hits, 20, HEIGHT - 20);
            for (Arrow a : arrows) {
                a.getSprite().draw(game.batch);
            }

            game.batch.end();

            for (Arrow arrow : arrows) {
                arrow.getSprite().translateY(-speed * Gdx.graphics.getDeltaTime());
            }

            if (TimeUtils.nanoTime() - lastArrowSpawn > conductor.crochet * 1000000000) spawnArrow();

            checkWinCondition();
        }
    }

    private void spawnArrow() {
        Sprite sprite = new Sprite(arrowTexture, 64, 64);

        int direction = MathUtils.random(0, 3);

        sprite.setRotation(direction * 90);
        sprite.setPosition(playerX, HEIGHT + sprite.getHeight());

        arrows.add(new Arrow(sprite, Arrow.Direction.fromInt(direction)));

        lastArrowSpawn = TimeUtils.nanoTime();
    }

    private void controls() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) pauseGame();

        Arrow.Direction direction = null;

        for (Iterator<Arrow> iter = arrows.iterator(); iter.hasNext(); ) {
            Arrow a = iter.next();
            if (a.getSprite().getY() < hitPosition) {
                direction = a.getDirection();
                iter.remove();
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) && direction == Arrow.Direction.UP ||
            Gdx.input.isKeyPressed(Input.Keys.A) && direction == Arrow.Direction.LEFT ||
            Gdx.input.isKeyPressed(Input.Keys.S) && direction == Arrow.Direction.DOWN ||
            Gdx.input.isKeyPressed(Input.Keys.D) && direction == Arrow.Direction.RIGHT) hits++;

//        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
//            for (Arrow arrow : arrows) {
//                System.out.println(arrow.getDirection());
//            }
//        }
    }

    private void checkWinCondition() {
        if (hits >= 20) {
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

    private void checkIsHittable() {
        float offset = 0.25f;

        float leftInterval = conductor.lastBeat + conductor.crochet - offset;
        if (leftInterval < 0) leftInterval = 0;
        float rightInterval = conductor.lastBeat + conductor.crochet + offset;

        if (leftInterval <= song.getPosition() && song.getPosition() <= rightInterval) {
            canSpawn = true;
            conductor.lastBeat += conductor.crochet;
        } else {
            canSpawn = false;
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
