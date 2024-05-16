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

    private final float playerX;
    private final float playerY;

    private final float hitPosition;

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
    private float speed;
    private float spawnRateNano;

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

        hitPosition = player.getY() + player.getHeight() + 10;
        hits = 0;
        lastArrowSpawn = 0;
    }

    @Override
    public void show() {
        isRunning = true;

        conductor.start();
        song.setVolume(Start.volume);
        song.play();

        speed = (HEIGHT - hitPosition) / conductor.crochet;
        spawnRateNano = conductor.crochet * 1000000000;
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
            for (Arrow a : arrows) {
                a.getSprite().draw(game.batch);
            }

            game.batch.end();

            moveArrowDown();

            if (TimeUtils.nanoTime() - lastArrowSpawn > spawnRateNano) spawnArrow();

            checkWinCondition();
        }
    }

    private void moveArrowDown() {
        for (Iterator<Arrow> iter = arrows.iterator(); iter.hasNext(); ) {
            Arrow arrow = iter.next();
            arrow.getSprite().translateY(-speed * Gdx.graphics.getDeltaTime());
            // Remove arrow if it falls to far down
            if (arrow.getSprite().getY() < hitPosition - HIT_OFFSET) iter.remove();
        }
    }

    private void spawnArrow() {
        Sprite sprite = new Sprite(arrowTexture, arrowTexture.getWidth(), arrowTexture.getHeight());
        int direction = MathUtils.random(0, 3);

        sprite.setRotation(direction * 90); // counter-clock rotation
        sprite.setPosition(playerX, HEIGHT + sprite.getHeight());

        arrows.add(new Arrow(sprite, Arrow.Direction.fromInt(direction)));

        lastArrowSpawn = TimeUtils.nanoTime();
    }

    private void controls() {
        // Check pause key being pressed
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) pauseGame();

        // Get direction and idx of arrow that is around hitPosition
        Arrow.Direction direction = null;
        int idx = 0;
        for (int i = 0; i < arrows.size; i++) {
            Arrow a = arrows.get(i);
            float y = a.getSprite().getY();
            if (hitPosition - HIT_OFFSET <= y && y <= hitPosition + HIT_OFFSET) {
                direction = a.getDirection();
                idx = i;
            }
        }

        // Check if arrow direction match input direction
        if (Gdx.input.isKeyPressed(Input.Keys.W) && direction == Arrow.Direction.UP ||
            Gdx.input.isKeyPressed(Input.Keys.A) && direction == Arrow.Direction.LEFT ||
            Gdx.input.isKeyPressed(Input.Keys.S) && direction == Arrow.Direction.DOWN ||
            Gdx.input.isKeyPressed(Input.Keys.D) && direction == Arrow.Direction.RIGHT) {

            hits++;
            arrows.removeIndex(idx);
        }
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
