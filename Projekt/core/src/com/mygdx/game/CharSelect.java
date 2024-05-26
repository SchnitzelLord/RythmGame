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

import java.time.format.TextStyle;

public class CharSelect implements Screen {

    final Start game;

    private Stage stage;

    Skin skin;

    BitmapFont font;

    Label.LabelStyle textStyle;

    Texture maleCharTexture;

    Texture femalCharTexture;

    ImageButton maleSelect;

    ImageButton femaleSelect;

    public CharSelect(final Start game) {
        this.game = game;
        // make the stage an input processor
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin();
        font = new BitmapFont(Gdx.files.internal("font\\font.fnt"));
        textStyle = new Label.LabelStyle(font, Color.WHITE);
        skin.add("font", textStyle);
        maleCharTexture = new Texture(Gdx.files.internal("characterSprite\\maleSprite.png"));
        femalCharTexture = new Texture(Gdx.files.internal("characterSprite\\femaleSprite.png"));
        skin.add("maleChar", maleCharTexture);
        skin.add("femaleChar", femalCharTexture);
    }

    @Override
    public void show() {
        Table table = new Table();
        table.setSkin(skin);
        table.setFillParent(true);
        table.setDebug(true);
        stage.addActor(table);

        ImageButton.ImageButtonStyle maleButtonStyle = new ImageButton.ImageButtonStyle(skin.getDrawable("maleChar"), skin.getDrawable("maleChar"), skin.getDrawable("maleChar"), skin.getDrawable("maleChar"), skin.getDrawable("maleChar"), skin.getDrawable("maleChar"));
        ImageButton.ImageButtonStyle femaleButtonStyle = new ImageButton.ImageButtonStyle(skin.getDrawable("femaleChar"), skin.getDrawable("femaleChar"), skin.getDrawable("femaleChar"), skin.getDrawable("femaleChar"), skin.getDrawable("femaleChar"), skin.getDrawable("femaleChar"));
        ImageButton maleSelectButton = new ImageButton(maleButtonStyle);
        ImageButton femaleSelectButton = new ImageButton(femaleButtonStyle);

        table.add("Choose your Character", "font").center();
        table.row().pad(15, 0, 50, 0);
        table.add(maleSelectButton).center();
        table.add(femaleSelectButton).center();

        maleSelectButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Start.playerTexture = new Texture(Gdx.files.internal("characterSprite\\maleSprite.png"));
                game.setScreen(new MazeLevel(game));
            }
        });

        femaleSelectButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Start.playerTexture = new Texture(Gdx.files.internal("characterSprite\\femaleSprite.png"));
                game.setScreen(new MazeLevel(game));
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
        skin.dispose();
        stage.dispose();
        maleCharTexture.dispose();
        femalCharTexture.dispose();
        font.dispose();
    }
}