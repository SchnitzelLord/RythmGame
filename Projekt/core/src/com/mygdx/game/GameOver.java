package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameOver implements Screen {

    Start game;
    Stage stage;
    Texture gameOverTexture;
    Image gameOverScreen;
    OrthographicCamera camera;
    Skin skin;
    Texture buttonTexture;
    String level;


    public GameOver(final Start game, String level) {
        this.game = game;
        this.level = level;
        camera = new OrthographicCamera(158, 106);
        stage = new Stage(new ScreenViewport(camera));
        Gdx.input.setInputProcessor(stage);
        gameOverTexture = new Texture(Gdx.files.internal("Menus\\gameOver.png"));
        gameOverScreen = new Image(gameOverTexture);
        gameOverScreen.setFillParent(true);
        buttonTexture = new Texture(Gdx.files.internal("Menus\\yes.png"));
        skin = new Skin();
        skin.add("trans", buttonTexture);

    }

    @Override
    public void show() {

        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        stage.addActor(gameOverScreen);
        stage.addActor(table);

        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle(skin.getDrawable("trans"), skin.getDrawable("trans"), skin.getDrawable("trans"), skin.getDrawable("trans"), skin.getDrawable("trans"), skin.getDrawable("trans"));
        ImageButton yesButton = new ImageButton(buttonStyle);
        yesButton.setSize(180, 100);
        ImageButton noButton = new ImageButton(buttonStyle);
        noButton.setSize(180, 100);

        table.row().pad(700, 40, 0, 0);
        table.add(yesButton).width(180).height(100);
        table.add(noButton).width(180).height(100);

        yesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (level.equals("JumpAndRun")) {
                    game.setScreen(new JumpAndRun(game));
                } else if (level.equals("OcarinaLevel")) {
                    game.setScreen(new MazeLevel(game));
                } else if (level.equals("MazeLevel")) {
                    game.setScreen(new MazeLevel(game));
                }
                yesButton.remove();
            }
        });

        noButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
                noButton.remove();
            }
        });


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
        gameOverTexture.dispose();
        stage.dispose();
        buttonTexture.dispose();
        skin.dispose();
    }
}
