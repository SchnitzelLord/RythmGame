package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

//Options menu when in main menu
public class OptionsMenu implements Screen {

    final Start game;

    GameScreens lastScreen;

    Stage stage;

    Skin skin;
    Texture buttonBackground;
    Texture minusButtonTexture;
    Texture plusButtonTexture;
    Label.LabelStyle textStyle;
    BitmapFont font;
    Label volume;
    FreeTypeFontGenerator fontGenerator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    public OptionsMenu(final Start game, GameScreens screen) {
        this.game = game;
        this.lastScreen = screen;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        buttonBackground = new Texture(Gdx.files.internal("buttons\\buttonLayout.png"));
        minusButtonTexture = new Texture(Gdx.files.internal("buttons\\minusButton.png"));
        plusButtonTexture = new Texture(Gdx.files.internal("buttons\\plusButton.png"));
        skin = new Skin();
        skin.add("button", buttonBackground);
        skin.add("minus", minusButtonTexture);
        skin.add("plus", plusButtonTexture);
        font = new BitmapFont(Gdx.files.internal("font\\font.fnt"));
        textStyle = new Label.LabelStyle(font, Color.WHITE);
        skin.add("font", textStyle);
        volume = new Label(String.format("%.0f %s", Start.volume * 100, "%"), skin, "font");
        volume.setAlignment(0);

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
        table.add(volume).width(150);
        table.add(plusButton);
        table.row().pad(20, 0, 20, 0);
        table.add(backButton);

        minusButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Start.volume - 0.1f >= 0.0f) {
                    Start.volume -= 0.1f;
                    volume.setText(String.format("%.0f %s", Start.volume * 100, "%"));
                }

            }
        });
        plusButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Start.volume + 0.1f <= 1.0f) {
                    Start.volume += 0.1f;
                    volume.setText(String.format("%.0f %s", Start.volume * 100, "%"));
                }

            }
        });
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (lastScreen == null) {
                    game.setScreen(new MainMenuScreen(game));
                } else {
                    game.setScreen(new PauseScreen(game, lastScreen.getScreen()));
                }

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
        minusButtonTexture.dispose();
        plusButtonTexture.dispose();
    }
}
