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

    public PauseScreen(final Start game, Screen screen) {
        this.game = game;
        this.lastScreen = screen;

        // make the stage an input processor
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        //initialize texture for buttons
        buttonBackground = new Texture(Gdx.files.internal("buttons\\buttonLayout.png"));
        skin = new Skin();
        skin.add("button", buttonBackground);
    }


    @Override
    public void show() {
        // table to organize buttons; fills screen
        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        stage.addActor(table);

        // create buttons
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(skin.getDrawable("button"), skin.getDrawable("button"), skin.getDrawable("button"), new BitmapFont(Gdx.files.internal("font\\font.fnt")));
        TextButton resume = new TextButton("Resume", style);
        TextButton options = new TextButton("Options", style);
        TextButton exit = new TextButton("Exit", style);

        // add title and buttons to screen
        table.row().pad(20, 0, 20, 0);
        table.add(resume).fillX().uniformX();
        table.row().pad(20, 0, 20, 0);;
        table.add(options).fillX().uniformX();
        table.row().pad(20, 0, 20, 0);;
        table.add(exit).fillX().uniformX();

        //add event listeners to buttons
        resume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(lastScreen);
            }
        });

        options.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new OptionsMenu(game, thisScreen));
            }
        });

        exit.addListener(new ChangeListener() {
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

