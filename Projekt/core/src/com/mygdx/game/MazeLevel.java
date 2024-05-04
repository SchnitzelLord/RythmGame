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
import com.badlogic.gdx.utils.ScreenUtils;

import javax.sound.sampled.AudioInputStream;

public class MazeLevel implements Screen {

    final Start game;
    Sprite player;

    Texture playerTexture;

    private boolean isPaused;

    OrthographicCamera camera;

    Conductor conductor;

    Music song;

    public boolean canMove = false;

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
    }

    @Override
    public void show() {
        setIsPaused(false);
        song.play();
        conductor.start();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.RED);

        game.batch.begin();
        player.draw(game.batch);
        if (!isPaused) {
            camera.update();
            game.batch.setProjectionMatrix(camera.combined);
            //conductor.songPosition = song.getPosition();
            update();
        }
        game.batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            setIsPaused(true);
            song.pause();
            game.setScreen(new PauseScreen(game, this));
        }

        if(Gdx.input.isKeyPressed(Input.Keys.D) && canMove) {
            player.setX(player.getX() + 100);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A) && canMove) {
            player.setX(player.getX() - 100);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.W) && canMove) {
            player.setY(player.getY() + 100);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S) && canMove) {
            player.setY(player.getY() - 100);
        }
    }

    public void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    public void update() {
        if (song.getPosition() >= conductor.lastBeat + conductor.crochet - 0.3f && song.getPosition() <= conductor.lastBeat + conductor.crochet + 0.3f) {
            canMove = true;
            conductor.lastBeat += conductor.crochet;
        } else {
            canMove = false;
        }
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
