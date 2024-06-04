package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;

public class TransitionScreen implements Screen {
    final Start game;

    String nextScreen;

    FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    BitmapFont pixelFont;
    Texture backgroundTexture;
    Image background;

    Timer timer = new Timer();

    public TransitionScreen(final Start game, String screen) {
        this.game = game;

        this.nextScreen = screen;

        generator = new FreeTypeFontGenerator(Gdx.files.internal("font\\PublicPixel.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        pixelFont = generator.generateFont(parameter);
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        background = new Image(backgroundTexture);
        background.setHeight(1080);
        background.setWidth(1920);
    }

    @Override
    public void show() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (nextScreen.equals("JumpAndRun")) {
                    game.setScreen(new JumpAndRun(game));
                } else if (nextScreen.equals("OcarinaLevel")) {
                    game.setScreen(new MazeLevel(game));
                } else if (nextScreen.equals("MazeLevel")) {
                    game.setScreen(new MazeLevel(game));
                }

            }
        }, 5);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        game.batch.begin();
        background.draw(game.batch, 1.0f);
        if (nextScreen.equals("JumpAndRun")) {
            pixelFont.draw(game.batch, "Evade the Waves!", 740, 800);
            pixelFont.draw(game.batch, "Jump - SPACE", 780, 750);
            pixelFont.draw(game.batch, "Watch out for power ups", 710, 700);

        } else if (nextScreen.equals("MazeLevel")) {
            pixelFont.draw(game.batch, "Move to the beat", 730, 800);
            pixelFont.draw(game.batch, "Move - WASD", 790, 750);
            pixelFont.draw(game.batch, "Exit the maze", 770, 700);
            pixelFont.draw(game.batch, "Survive", 850, 650);
        } else if (nextScreen.equals("OcarinaLevel")) {
            pixelFont.draw(game.batch, "Press the buttons to the beat", 740, 800);
            pixelFont.draw(game.batch, "Up - W \nLeft - A \nDown - S \nRight - D", 780, 750);
        }
        game.batch.end();
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
    }
}
