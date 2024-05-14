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

import java.awt.*;
import java.util.Iterator;

public class JumpAndRun implements Screen {

    private static final int MAX_HEIGTH = 1080;
    private static final int MAX_WIDTH = 1920;

    private long lastRectangleTime;

    final Start game;
    Sprite player;

    private Array<Rectangle> rectangles;

    private int lives = 10;


    Texture playerTexture;

    private boolean isPaused;

    OrthographicCamera camera;

    Conductor conductor;

    Music song;

    float volume;

    public boolean canMove = false;
    float move;

    BitmapFont font = new BitmapFont();

    public JumpAndRun(final Start game) {
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
        song.setVolume(Start.volume);
        song.play();
        conductor.start();

        rectangles =  new Array<Rectangle>();
    }

    public void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }



    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLUE);

        game.batch.begin();
        player.draw(game.batch);
        if (!isPaused) {
            camera.update();
            game.batch.setProjectionMatrix(camera.combined);
            //conductor.songPosition = song.getPosition();
            update();

            for(Rectangle rectangle: rectangles) {
                game.batch.draw(playerTexture, rectangle.x, rectangle.y);
            }

            font.draw(game.batch, "Lives : " + lives, MAX_WIDTH / 2, 900);
        }


        game.batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            setIsPaused(true);
            song.pause();
            game.setScreen(new PauseScreen(game, this));
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            move = player.getX() - 200 * Gdx.graphics.getDeltaTime();
            if (move < 0) move = player.getX();
            player.setX(move);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            move = player.getX() + 200 * Gdx.graphics.getDeltaTime();
            if (move > MAX_WIDTH - player.getWidth()) move = player.getX();
            player.setX(move);

        }

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE )) {
            move = player.getY() + 200 * Gdx.graphics.getDeltaTime();
            if (move + player.getHeight() > MAX_HEIGTH) move = player.getY();

            player.setY(move);
        } else {
            move = player.getY() - 400 * Gdx.graphics.getDeltaTime();
            if (move < 0) move = player.getY();
            player.setY(move);
        }

        for (Iterator<Rectangle> iter = rectangles.iterator(); iter.hasNext(); ) {
            Rectangle rectangle = iter.next();
            rectangle.x -= 150 * Gdx.graphics.getDeltaTime();
            if(rectangle.x < 0) iter.remove();

            if(overlap(rectangle)) {
                iter.remove();
                lives -= 1;
            }
        }


        if(TimeUtils.nanoTime() - lastRectangleTime > 1000000000) spawnRectangle();

        if (lives <= 0) Gdx.app.exit();

    }

    private boolean overlap (Rectangle rec) {
        if (player.getX() + player.getWidth() < rec.x) return false;
        if (player.getY() + player.getHeight() < rec.y) return false;
        if (player.getY() > rec.y + rec.height) return false;
        if (player.getX() > rec.x + rec.width) return false;
        return true;
    }

    private void spawnRectangle() {
        Rectangle rectangle = new Rectangle();
        rectangle.x = MAX_WIDTH;
        rectangle.y = 0;
        rectangle.width = (int) player.getWidth();
        rectangle.height = (int) player.getHeight();
        rectangles.add(rectangle);
        lastRectangleTime = TimeUtils.nanoTime();
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

    public void update() {
        if (song.getPosition() >= conductor.lastBeat + conductor.crochet - 0.3f && song.getPosition() <= conductor.lastBeat + conductor.crochet + 0.3f) {
            canMove = true;
            conductor.lastBeat += conductor.crochet;
        } else {
            canMove = false;
        }
    }


}
