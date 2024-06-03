package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;

public class PauseScreen extends GameScreens {

    //game instance
    final Start game;

    //reference to this object
    PauseScreen thisScreen = this;

    //last screen before pausing
    Screen lastScreen;

    //Stage to add buttons and listen for inputs
    Stage stage;

    //objects for buttons
    Skin skin;
    Texture buttonBackground;
    Texture optionButtonTexture;
    Texture quitButtonTexture;

    public PauseScreen(final Start game, Screen screen) {
        this.game = game;
        this.lastScreen = screen;

        // make the stage an input processor
        stage = new Stage(new FillViewport(627, 420));
        Gdx.input.setInputProcessor(stage);
        //initialize texture for buttons
        buttonBackground = new Texture(Gdx.files.internal("buttons\\buttonLayout.png"));
        optionButtonTexture = new Texture(Gdx.files.internal("buttons\\OptionsButton.png"));
        quitButtonTexture = new Texture(Gdx.files.internal("buttons\\QuitButton.png"));
        skin = new Skin();
        skin.add("button", buttonBackground);
        skin.add("options", optionButtonTexture);
        skin.add("quit", quitButtonTexture);
    }


    @Override
    public void show() {
        // table to organize buttons; fills screen
        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(false);
        stage.addActor(table);

        // create buttons
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(skin.getDrawable("button"), skin.getDrawable("button"), skin.getDrawable("button"), new BitmapFont(Gdx.files.internal("font\\font.fnt")));
        TextButton resume = new TextButton("Resume", style);
        ImageButton.ImageButtonStyle optionButtonStyle = new ImageButton.ImageButtonStyle(skin.getDrawable("options"), skin.getDrawable("options"), skin.getDrawable("options"), skin.getDrawable("options"), skin.getDrawable("options"), skin.getDrawable("options"));
        ImageButton.ImageButtonStyle quitButtonStyle = new ImageButton.ImageButtonStyle(skin.getDrawable("quit"), skin.getDrawable("quit"), skin.getDrawable("quit"), skin.getDrawable("quit"), skin.getDrawable("quit"), skin.getDrawable("quit"));
        ImageButton optionButton = new ImageButton(optionButtonStyle);
        ImageButton quitButton = new ImageButton(quitButtonStyle);

        // add title and buttons to screen
        table.add(resume);
        table.row();
        table.add(optionButton);
        table.row();
        table.add(quitButton);

        //add event listeners to buttons
        resume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(lastScreen);
            }
        });

        optionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new OptionsMenu(game, thisScreen));
            }
        });

        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render(float delta) {
        //set screen background
        ScreenUtils.clear(Color.BLACK);

        //draw stage
        stage.act();
        stage.draw();

    }

    public Screen getScreen() {
        return lastScreen;
    }

    @Override
    public void resize(int width, int height) {
        //resize window
        stage.getViewport().update(width, height, true);
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
        //get rid of objects to keep memory free
        stage.dispose();
        skin.dispose();
        buttonBackground.dispose();
    }
}

