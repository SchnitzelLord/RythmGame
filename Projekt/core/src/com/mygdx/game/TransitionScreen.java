package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.mygdx.game.ocarinagame.levels.OcarinaGameAppearing;
import com.mygdx.game.ocarinagame.levels.OcarinaGameFalling;

public class TransitionScreen implements Screen {
    final Start game;

    String nextScreen;
    Stage stage;
    Label text;
    Label.LabelStyle style;

    //tool to create BitmapFont from font file
    FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    BitmapFont pixelFont;
    Texture backgroundTexture;
    Image background;
    OrthographicCamera camera;

    Timer timer;

    public TransitionScreen(final Start game, String screen) {
        this.game = game;

        this.nextScreen = screen;
        camera = new OrthographicCamera(1920, 1080);

        timer = new Timer();

        generator = new FreeTypeFontGenerator(Gdx.files.internal("font\\PublicPixel.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        pixelFont = generator.generateFont(parameter);

        stage = new Stage(new FillViewport(1920, 1080, camera));
        Gdx.input.setInputProcessor(stage);

        style = new Label.LabelStyle(pixelFont, Color.WHITE);
        if (nextScreen.equals("WayThere")||nextScreen.equals("Homeway")) {
            text = new Label("Evade the Waves! \nJump - Space \nStomp - S \nWatch out for power ups", style);
            backgroundTexture = new Texture(Gdx.files.internal("Menus\\background.png"));
        } else if (nextScreen.equals("MazeLevel")) {
            text = new Label("Move to the beat \nMove - WASD \nExit the maze \nSurvive", style);
            backgroundTexture = new Texture(Gdx.files.internal("Menus\\background.png"));
        } else if (nextScreen.equals("OcarinaLevel")||nextScreen.equals("WakeUp")) {
            text = new Label("Press the buttons to the beat \nUp - W \nLeft - A \nDown - S \nRight - D", style);
            backgroundTexture = new Texture(Gdx.files.internal("Menus\\background.png"));
        }
        text.setAlignment(0);

        background = new Image(backgroundTexture);
        background.setFillParent(true);
    }

    @Override
    public void show() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (nextScreen.equals("Homeway")) {
                    game.setScreen(new JumpAndRun(game,0));
                } else if (nextScreen.equals("OcarinaLevel")) {
                    game.setScreen(new OcarinaGameFalling(game));
                } else if (nextScreen.equals("MazeLevel")) {
                    game.setScreen(new MazeLevel(game));
                } else if (nextScreen.equals("WakeUp")) {
                    game.setScreen(new OcarinaGameAppearing(game));
                } else if (nextScreen.equals("WayThere")) {
                    game.setScreen(new JumpAndRun(game,1));
                }

            }
        }, 5);

        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(false);
        stage.addActor(background);
        stage.addActor(table);


        table.add(text).center();
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
        generator.dispose();
        pixelFont.dispose();
        stage.dispose();
        backgroundTexture.dispose();
    }
}
