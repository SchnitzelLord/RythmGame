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
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.ScreenUtils;
import jdk.tools.jmod.Main;

import java.util.NoSuchElementException;

public class MazeLevel implements Screen {

    final Start game;

    Sprite player;

    Sprite enemy;

    Texture playerTexture;
    Texture leftWalk;
    Texture rightWalk;
    Texture upWalk;
    Texture monsterTexture;

    private boolean isPaused;

    OrthographicCamera camera;

    OrthographicCamera hudCamera;

    Conductor conductor;

    Conductor monsterConductor;

    Music song;

    public boolean canMove = false;

    public boolean monsterCanMove = false;

    Queue<Runnable> movementQueue;

    Texture blackScreenTexture;

    Image blackScreen;

    static final float blackScreenAlpha = 1.0f;

    float blackScreenCounter = 0.0f;

    boolean blackScreenTimer = false;

    float move;

    TiledMap map;
    MapLayer wallLayer;
    MapObjects walls;
    MapRenderer renderer;
    RectangleMapObject finish;

    public MazeLevel(final Start game) {
        this.game = game;

        leftWalk = new Texture(Gdx.files.internal("MazeLevel\\leftWalk.png"));
        rightWalk = new Texture(Gdx.files.internal("MazeLevel\\rightWalk.png"));
        upWalk = new Texture(Gdx.files.internal("MazeLevel\\upWalk.png"));
        monsterTexture = new Texture(Gdx.files.internal("MazeLevel\\monster.png"));
        player = new Sprite(leftWalk);
        player.setX(15);
        player.setY(335);
        isPaused = false;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 192, 108);
        camera.position.set(player.getX(), player.getY(), 0);
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, 192, 108);
        hudCamera.position.set(player.getX(), player.getY(), 0);
        song = Gdx.audio.newMusic(Gdx.files.internal("Music\\Nightmare_145bpm.mp3"));
        conductor = new Conductor(145, 0);
        conductor.start();
        monsterConductor = new Conductor(145, 0);
        monsterConductor.start();
        enemy = new Sprite(monsterTexture, 30, 24);
        enemy.setX(player.getX() - 60);
        enemy.setY(player.getY());
        //initializing map
        map = new TmxMapLoader().load("maps\\MazeMap\\Map.tmx");
        wallLayer = map.getLayers().get("Walls");
        walls = wallLayer.getObjects();
        renderer = new OrthogonalTiledMapRenderer(map);
        finish = map.getLayers().get("Goal").getObjects().getByType(RectangleMapObject.class).get(0);
        movementQueue = new Queue<>();
        for (int i = 0; i < 3; i++) {
            movementQueue.addLast(() -> enemy.setX(enemy.getX() + 20));
        }
        blackScreenTexture = new Texture(Gdx.files.internal("MazeLevel\\black.jpg"));
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
        ScreenUtils.clear(Color.BLACK);
        System.out.println(walls.getCount());

        //begin drawing objets
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (!isPaused) {
            renderer.setView(camera);
            renderer.render();
            game.batch.begin();
            player.draw(game.batch);
            enemy.draw(game.batch);
            camera.update();
            hudCamera.update();
            game.batch.setProjectionMatrix(camera.combined);
            updateBlackScreenTimer();
            if (blackScreenTimer) {
                darken();
            }
            blackScreen.draw(game.batch, blackScreenAlpha);
            //move the camera smoothly
            float lerp = 1.0f;
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
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (!blocked(player.getX() + 30 * Gdx.graphics.getDeltaTime(), player.getY())) {
                player.setTexture(rightWalk);
                move = player.getX() + 30 * Gdx.graphics.getDeltaTime();
                player.setX(move);
                float enemyPos = player.getX();
                movementQueue.addLast(() -> enemy.setX(enemyPos));
            }

        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (!blocked(player.getX() - 30 * Gdx.graphics.getDeltaTime(), player.getY())) {
                player.setTexture(leftWalk);
                move = player.getX() - 30 * Gdx.graphics.getDeltaTime();
                player.setX(move);
                float enemyPos = player.getX();
                movementQueue.addLast(() -> enemy.setX(enemyPos));
            }

        }
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (!blocked(player.getX(), player.getY() + 30 * Gdx.graphics.getDeltaTime())) {
                player.setTexture(upWalk);
                move = player.getY() + 30 * Gdx.graphics.getDeltaTime();
                player.setY(move);
                float enemyPos = player.getY();
                movementQueue.addLast(() -> enemy.setY(enemyPos));
            }

        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (!blocked(player.getX(), player.getY() - 30 * Gdx.graphics.getDeltaTime())) {
                move = player.getY() - 30 * Gdx.graphics.getDeltaTime();
                player.setY(move);
                float enemyPos = player.getY();
                movementQueue.addLast(() -> enemy.setY(enemyPos));
            }

        }

        //enemy movement
        if (song.getPosition() > 5) {
            try {
                moveEnemy(movementQueue.removeFirst());
            } catch (NoSuchElementException e) {

            }

        }

        //death
        if (hit()) {
            song.stop();
            game.setScreen(new GameOver(game, "MazeLevel"));
        }

        if (finish()) {
            song.stop();
            game.setScreen(new Credits(game));
        }


    }

    public void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    boolean hit() {
        if(Intersector.overlaps(player.getBoundingRectangle(), enemy.getBoundingRectangle())) {
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
        if (song.getPosition() >= blackScreenCounter + 19.9f && song.getPosition() <= blackScreenCounter + 20.1f) {
            blackScreenTimer = true;
            blackScreenCounter += 20;
        } else {
            blackScreenTimer = false;
        }
    }

    boolean blocked(float x, float y) {
        for (RectangleMapObject rect : walls.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rect.getRectangle();
            if (Intersector.overlaps(rectangle, new Rectangle().set(x, y, 10, 12))) {
                return true;
            }
        }
        return false;
    }

    boolean finish() {
        if (Intersector.overlaps(finish.getRectangle(), player.getBoundingRectangle())) {
            return true;
        }
        return false;
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
    }
}