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
import com.badlogic.gdx.utils.viewport.FillViewport;

//Options menu when in main menu
public class OptionsMenu implements Screen {

    final Start game;

    GameScreens lastScreen;

    Stage stage;

    Skin skin;
    Texture minusButtonTexture;
    Texture plusButtonTexture;
    Texture backButtonTexture;
    Label.LabelStyle textStyle;
    BitmapFont font;
    Label volume;
    Texture backgroundTexture;
    Image background;

    public OptionsMenu(final Start game, GameScreens screen) {
        this.game = game;
        this.lastScreen = screen;

        stage = new Stage(new FillViewport(627, 420));
        Gdx.input.setInputProcessor(stage);

        minusButtonTexture = new Texture(Gdx.files.internal("buttons\\minusButton.png"));
        plusButtonTexture = new Texture(Gdx.files.internal("buttons\\plusButton.png"));
        backButtonTexture = new Texture(Gdx.files.internal("buttons\\backButton.png"));
        skin = new Skin();
        skin.add("back", backButtonTexture);
        skin.add("minus", minusButtonTexture);
        skin.add("plus", plusButtonTexture);
        font = new BitmapFont(Gdx.files.internal("font\\smallFont.fnt"));
        textStyle = new Label.LabelStyle(font, Color.WHITE);
        skin.add("font", textStyle);
        volume = new Label(String.format("%.0f %s", Start.volume * 100, "%"), skin, "font");
        volume.setAlignment(0);
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
        table.setDebug(false);
        stage.addActor(table);

        ImageButton.ImageButtonStyle backStyle = new ImageButton.ImageButtonStyle(skin.getDrawable("back"), skin.getDrawable("back"), skin.getDrawable("back"), skin.getDrawable("back"), skin.getDrawable("back"), skin.getDrawable("back"));
        ImageButton.ImageButtonStyle minusStyle = new ImageButton.ImageButtonStyle(skin.getDrawable("minus"), skin.getDrawable("minus"), skin.getDrawable("minus"), skin.getDrawable("minus"), skin.getDrawable("minus"), skin.getDrawable("minus"));
        ImageButton.ImageButtonStyle plusStyle = new ImageButton.ImageButtonStyle(skin.getDrawable("plus"), skin.getDrawable("plus"), skin.getDrawable("plus"), skin.getDrawable("plus"), skin.getDrawable("plus"), skin.getDrawable("plus"));
        ImageButton backButton = new ImageButton(backStyle);
        ImageButton plusButton = new ImageButton(plusStyle);
        ImageButton minusButton = new ImageButton(minusStyle);

        table.add("Volume", "font");
        table.add(minusButton);
        table.add(volume);
        table.add(plusButton);
        table.row().pad(50, 0, 0, 0);
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
        stage.dispose();
        skin.dispose();
        backButtonTexture.dispose();
        minusButtonTexture.dispose();
        plusButtonTexture.dispose();
        font.dispose();
    }
}
