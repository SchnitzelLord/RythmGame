package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;

import java.time.format.TextStyle;

public abstract class CharSelect implements Screen {

    final Start game;

    private Stage stage;

    Skin skin;

    FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    BitmapFont pixelFont;

    Label.LabelStyle textStyle;

    Texture maleCharTexture;

    Texture femalCharTexture;

    Texture backgroundTexture;
    Image background;

    public CharSelect(final Start game) {
        this.game = game;
        // make the stage an input processor
        stage = new Stage(new FillViewport(627, 420));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("font\\PublicPixel.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        pixelFont = generator.generateFont(parameter);
        textStyle = new Label.LabelStyle(pixelFont, Color.WHITE);
        skin.add("font", textStyle);
        maleCharTexture = new Texture(Gdx.files.internal("characterSprite\\maleSprite.png"));
        femalCharTexture = new Texture(Gdx.files.internal("characterSprite\\femaleSprite.png"));
        skin.add("maleChar", maleCharTexture);
        skin.add("femaleChar", femalCharTexture);
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        background = new Image(backgroundTexture);
        background.setHeight(1080);
        background.setWidth(1920);
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

        table.row().pad(0, 60, 0, 0);
        table.add("Choose your Character", "font").center();
        table.row().pad(15, 0, 50, 0);
        table.add(maleSelectButton).center();
        table.add(femaleSelectButton).center();

        maleSelectButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //playerTexture = new Texture(Gdx.files.internal("characterSprite\\maleSprite.png"));
                game.setScreen(new TransitionScreen(game, "MazeLevel"));
                maleSelectButton.remove();
            }
        });

        femaleSelectButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //playerTexture = new Texture(Gdx.files.internal("characterSprite\\femaleSprite.png"));
                game.setScreen(new MazeLevel(game));
                femaleSelectButton.remove();
            }
        });



    }

    @Override
    public void render(float delta) {
        //set screen background
        ScreenUtils.clear(Color.BLACK);

        //draw stage
        game.batch.begin();
        background.draw(game.batch, 1.0f);
        game.batch.end();
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
        generator.dispose();
        pixelFont.dispose();
    }
}
