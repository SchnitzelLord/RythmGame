package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.NoSuchElementException;

public class MazeLevel implements Screen {

    final Start game;

    final SpriteBatch hudBatch;

    Sprite player;

    Sprite enemy;

    private boolean isPaused;

    OrthographicCamera camera;

    OrthographicCamera hudCamera;
    OrthographicCamera mapCamera;

    Conductor conductor;

    Conductor monsterConductor;

    Music song;

    public boolean canMove = false;

    public boolean monsterCanMove = false;

    Queue<Runnable> movementQueue;

    Texture blackScreenTexture;

    Image blackScreen;

    MazeHud hud;

    static final float blackScreenAlpha = 1.0f;

    float blackScreenCounter = 0.0f;

    boolean blackScreenTimer = false;

    TiledMap map;
    MapLayer wallLayer;
    MapObjects walls;
    MapRenderer renderer;

    public MazeLevel(final Start game) {
        this.game = game;
        hudBatch = new SpriteBatch();

        player = new Sprite(Start.playerTexture, 64, 64);
        hud = new MazeHud(game);
        player.setX(20);
        player.setY(1200);
        isPaused = false;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        camera.position.set(player.getX(), player.getY(), 0);
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, 125, 125);
        hudCamera.position.set(player.getX(), player.getY(), 0);
        mapCamera = new OrthographicCamera();
        mapCamera.setToOrtho(false, 1250, 1250);
        song = Gdx.audio.newMusic(Gdx.files.internal("Music\\Nightmare_145bpm.mp3"));
        conductor = new Conductor(145, 0);
        conductor.start();
        monsterConductor = new Conductor(145, 0);
        monsterConductor.start();
        enemy = new Sprite(Start.playerTexture, 64, 64);
        enemy.setX(player.getX() - 60);
        enemy.setY(player.getY());
        //initializing map
        map = new TmxMapLoader().load("maps\\MazeMap\\MazeMap.tmx");
        wallLayer = map.getLayers().get("Walls");
        walls = wallLayer.getObjects();
        renderer = new OrthogonalTiledMapRenderer(map);
        movementQueue = new Queue<>();
        for (int i = 0; i < 3; i++) {
            movementQueue.addLast(() -> enemy.setX(enemy.getX() + 60));
        }
        blackScreenTexture = new Texture(Gdx.files.internal("black.jpg"));
        blackScreen = new Image(blackScreenTexture);
        blackScreen.setColor(blackScreen.getColor().r, blackScreen.getColor().g, blackScreen.getColor().b, 0.0f);

    }

    @Override
    public void show() {
        setIsPaused(false);
        song.setVolume(Start.volume);
        song.play();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY);

        //begin drawing objets
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.setView(mapCamera);
        renderer.render();
        game.batch.begin();
        player.draw(game.batch);
        enemy.draw(game.batch);
        if (!isPaused) {
            camera.update();
            hudCamera.update();
            mapCamera.update();
            game.batch.setProjectionMatrix(camera.combined);
            hudBatch.setProjectionMatrix(hudCamera.combined);
            update();
            updateMonster();
            updateBlackScreenTimer();
            if (blackScreenTimer) {
                darken();
            }
            blackScreen.draw(game.batch, blackScreenAlpha);
            if (canMove) {
                hudBatch.begin();
                hud.getSymbol().draw(hudBatch, 1.0f);
                hudBatch.end();
            }
            float lerp = 0.5f;
            Vector3 position = camera.position;
            position.x += (player.getX() - position.x) * lerp * delta;
            position.y += (player.getY() - position.y) * lerp * delta;
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
            player.setX(player.getX() + 20);
            movementQueue.addLast(() -> enemy.setX(enemy.getX() + 20));
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A) && canMove) {
            player.setX(player.getX() - 20);
            movementQueue.addLast(() -> enemy.setX(enemy.getX() - 20));
        }
        if(Gdx.input.isKeyPressed(Input.Keys.W) && canMove) {
            player.setY(player.getY() + 20);
            movementQueue.addLast(() -> enemy.setY(enemy.getY() + 20));
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S) && canMove) {
            player.setY(player.getY() - 20);
            movementQueue.addLast(() -> enemy.setY(enemy.getY() - 20));
        }

        //enemy movement
        if (song.getPosition() > 5 && monsterCanMove) {
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

    public void updateMonster() {
        //update current song position and check if on beat
        if (song.getPosition() >= monsterConductor.lastBeat + 2*conductor.crochet - 0.3f && song.getPosition() <= monsterConductor.lastBeat + 2*conductor.crochet + 0.3f) {
            monsterCanMove = true;
            monsterConductor.lastBeat += 2*conductor.crochet;
        } else {
            monsterCanMove = false;
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

    void darken() {
        Color c = blackScreen.getColor();
        blackScreen.setColor(c.r, c.g, c.b, c.a + 0.2f);
    }

    void updateBlackScreenTimer() {
        if (song.getPosition() >= blackScreenCounter + 9.9f && song.getPosition() <= blackScreenCounter + 10.1f) {
            blackScreenTimer = true;
            blackScreenCounter += 10;
        } else {
            blackScreenTimer = false;
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
        song.dispose();
        blackScreenTexture.dispose();
        map.dispose();
        hud.dispose();
    }
}