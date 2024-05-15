package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class OcarinaGame implements Screen {
    private final Start game;
    private final Conductor conductor;
    private final Music song;
    private final OrthographicCamera camera;
    private boolean isRunning;
    private boolean canSpawn;

    private final Texture playerTexture;
    private final Texture arrowTexture;

    private final Array<Sprite> arrows;

    private final int WIDTH = 1920;
    private final int HEIGHT = 1080;

    public OcarinaGame(Start game) {
        this.game = game;

        conductor = new Conductor(120, 0);
        song = Gdx.audio.newMusic(Gdx.files.internal("Music\\testBeat.mp3"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);

        playerTexture = new Texture("characterSprite\\playerSprite.png");
        arrowTexture = new Texture("ocarina-game\\arrow-up.png");

        arrows = new Array<>(4);
        instantiateArrows();
    }

    @Override
    public void show() {
        isRunning = true;
        conductor.start();
        song.play();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.PURPLE);

        game.batch.setProjectionMatrix(camera.combined);
        camera.update();

        game.batch.begin();
        game.batch.draw(playerTexture, WIDTH / 2 - 32, HEIGHT / 2 - 32);
        arrows.get(0).draw(game.batch);
        game.batch.end();

        controls();
    }

    private void instantiateArrows() {
        Sprite arrow = new Sprite(arrowTexture, 64, 64);
        for (int i = 0; i < arrows.size; i++) {
            arrow.setRotation(i*90);
            arrows.add(arrow);
        }
    }

    private void controls() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) pauseGame();
    }

    private void pauseGame() {
        isRunning = false;
        song.pause();
        game.setScreen(new PauseScreen(game, this));
    }

    private void update() {
        float leftInterval = conductor.lastBeat + conductor.crochet - 0.3f;
        float rightInterval = conductor.lastBeat + conductor.crochet + 0.3f;

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
    }
}
