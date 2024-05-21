package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;

import javax.sound.sampled.AudioInputStream;
import java.util.NoSuchElementException;

public class MazeLevel implements Screen {

    final Start game;
    Sprite player;

    Sprite enemy;

    Texture playerTexture;

    private boolean isPaused;

    OrthographicCamera camera;

    Conductor conductor;

    Music song;

    float volume;

    public boolean canMove = false;

    Queue<Runnable> movementQueue;

    public MazeLevel(final Start game) {
        this.game = game;
        playerTexture = new Texture("characterSprite\\playerSprite.png");
        player = new Sprite(playerTexture, 64, 64);
        player.setX(1920 / 2);
        player.setY(1080 / 2);
        isPaused = false;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        song = Gdx.audio.newMusic(Gdx.files.internal("Music\\testBeat.mp3"));
        conductor = new Conductor(120, 0);
        enemy = new Sprite(playerTexture, 64, 64);
        enemy.setX(player.getX() - 300);
        enemy.setY(player.getY());
        movementQueue = new Queue<>();
        for (int i = 0; i < 3; i++) {
            movementQueue.addLast(() -> enemy.setX(enemy.getX() + 100));
        }
    }

    @Override
    public void show() {
        setIsPaused(false);
        song.setVolume(Start.volume);
        song.play();
        conductor.start();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY);

        //begin drawing objets
        game.batch.begin();
        player.draw(game.batch);
        enemy.draw(game.batch);
        if (!isPaused) {
            camera.update();
            game.batch.setProjectionMatrix(camera.combined);
            update();
        }
        game.batch.end();

        //pause game and switch to pause menu
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            setIsPaused(true);
            song.pause();
            game.setScreen(new PauseScreen(game, this));
        }

        //player move inputs
        if(Gdx.input.isKeyPressed(Input.Keys.D) && canMove) {
            player.setX(player.getX() + 100);
            movementQueue.addLast(() -> enemy.setX(enemy.getX() + 100));
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A) && canMove) {
            player.setX(player.getX() - 100);
            movementQueue.addLast(() -> enemy.setX(enemy.getX() - 100));
        }
        if(Gdx.input.isKeyPressed(Input.Keys.W) && canMove) {
            player.setY(player.getY() + 100);
            movementQueue.addLast(() -> enemy.setY(enemy.getY() + 100));
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S) && canMove) {
            player.setY(player.getY() - 100);
            movementQueue.addLast(() -> enemy.setY(enemy.getY() - 100));
        }

        //enemy movement
        if (song.getPosition() > 5 && canMove) {
            try {
                moveEnemy(movementQueue.removeFirst());
            } catch (NoSuchElementException e) {

            }

        }

        //death
        if (hit()) {
            song.stop();
            game.setScreen(new MainMenuScreen(game));
        }
    }

    public void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    public void update() {
        //update current song position and check if on beat
        if (song.getPosition() >= conductor.lastBeat + conductor.crochet - 0.3f && song.getPosition() <= conductor.lastBeat + conductor.crochet + 0.3f) {
            canMove = true;
            conductor.lastBeat += conductor.crochet;
        } else {
            canMove = false;
        }
    }

    boolean hit() {
        if(player.getX() == enemy.getX() && player.getY() == enemy.getY()) {
            return true;
        }
        return false;
    }

    void moveEnemy(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        playerTexture.dispose();
        song.dispose();
    }
}