package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

//Options menu when pausing
public class OptionsMenuTwo implements Screen {

    //game instance
    final Start game;

    //reference to last screen (pause menu)
    GameScreens lastScreen;

    //stage needed for menu layout
    Stage stage;

    //assets for buttons
    Skin skin;
    Texture buttonBackground;

    public OptionsMenuTwo(final Start game, GameScreens screen) {
        this.game = game;
        lastScreen = screen;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        //initializing texture for button
        buttonBackground = new Texture(Gdx.files.internal("buttons\\buttonLayout.png"));
        skin = new Skin();
        skin.add("button", buttonBackground);
    }

    @Override
    public void show() {
        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        stage.addActor(table);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(skin.getDrawable("button"), skin.getDrawable("button"), skin.getDrawable("button"), new BitmapFont());
        TextButton backButton = new TextButton("Back", style);


        table.row().pad(20, 0, 20, 0);
        /* table.add("Volume", "Aerial", Color.BLUE);

        table.row();
        table.add("Keybindings", "Aerial", Color.BLUE);

        table.row();
         */
        table.add(backButton);

        //add event listeners to buttons
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new PauseScreen(game, lastScreen.getScreen()));
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        //draw stage
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
        skin.dispose();
        buttonBackground.dispose();
    }
}
