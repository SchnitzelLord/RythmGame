package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Credits implements Screen {

    Start game;
    Stage stage;
    Texture creditTexture;
    Image credits;
    OrthographicCamera camera;
    Timer timer;

    public Credits(Start game) {
        this.game = game;

        timer = new Timer();
        camera = new OrthographicCamera(158, 106);
        stage = new Stage(new ScreenViewport(camera));
        Gdx.input.setInputProcessor(stage);
        creditTexture = new Texture(Gdx.files.internal("Menus\\Credits.png"));
        credits = new Image(creditTexture);
        credits.setFillParent(true);
    }

    @Override
    public void show() {
        stage.addActor(credits);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                game.setScreen(new MainMenuScreen(game));
            }
        }, 10);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        stage.act();
        stage.draw();
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
        stage.dispose();
        creditTexture.dispose();
    }
}
