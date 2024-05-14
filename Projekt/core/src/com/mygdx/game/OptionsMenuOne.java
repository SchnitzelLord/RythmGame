package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

//Options menu when in main menu
public class OptionsMenuOne implements Screen {

    final Start game;

    Stage stage;

    Skin skin;
    Texture buttonBackground;
    Texture minusButtonTexture;
    Texture plusButtonTexture;
    Label.LabelStyle textStyle;
    BitmapFont font;
    String volumeLevel;

    public OptionsMenuOne(final Start game) {
        this.game = game;
        volumeLevel = String.valueOf(Start.volume);

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        buttonBackground = new Texture(Gdx.files.internal("buttons\\buttonLayout.png"));
        minusButtonTexture = new Texture(Gdx.files.internal("buttons\\minusButton.png"));
        plusButtonTexture = new Texture(Gdx.files.internal("buttons\\plusButton.png"));
        skin = new Skin();
        skin.add("button", buttonBackground);
        skin.add("minus", minusButtonTexture);
        skin.add("plus", plusButtonTexture);
        textStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        skin.add("font", textStyle);

    }

    @Override
    public void show() {
        Table table = new Table();
        table.setSkin(skin);
        table.setFillParent(true);
        table.setDebug(true);
        stage.addActor(table);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(skin.getDrawable("button"), skin.getDrawable("button"), skin.getDrawable("button"), new BitmapFont());
        TextButton backButton = new TextButton("Back", style);
        ImageButton.ImageButtonStyle minusStyle = new ImageButton.ImageButtonStyle(skin.getDrawable("minus"), skin.getDrawable("minus"), skin.getDrawable("minus"), skin.getDrawable("minus"), skin.getDrawable("minus"), skin.getDrawable("minus"));
        ImageButton.ImageButtonStyle plusStyle = new ImageButton.ImageButtonStyle(skin.getDrawable("plus"), skin.getDrawable("plus"), skin.getDrawable("plus"), skin.getDrawable("plus"), skin.getDrawable("plus"), skin.getDrawable("plus"));
        ImageButton plusButton = new ImageButton(plusStyle);
        ImageButton minusButton = new ImageButton(minusStyle);

        table.bottom().left();
        table.add("Volume", "font").width(100);
        table.add(minusButton).expandY();
        table.add(volumeLevel, "font").width(100);
        table.add(plusButton);
        table.row().pad(20, 0, 20, 0);
        table.add(backButton);

        minusButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Start.volume -= 0.1f;
            }
        });
        plusButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Start.volume += 0.1f;
            }
        });
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        //draw stage
        stage.act();
        stage.draw();

        volumeLevel = String.valueOf(Start.volume);

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
        minusButtonTexture.dispose();
        plusButtonTexture.dispose();
    }
}
