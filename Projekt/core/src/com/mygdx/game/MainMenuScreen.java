package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.audio.Music;


public class MainMenuScreen implements Screen {

    //game instance
    final Start game;

    //Stage to add buttons and listen for inputs
    Stage stage;

    //objecs for buttons
    Skin skin;

    Texture newGameButtonTexture;
    Texture optionsButtonTexture;
    Texture quitButtonTexture;

    Texture backgroundTexture;
    Image background;

    Camera camera;

    Music backgroundMusic;

    public MainMenuScreen(final Start game) {
        this.game = game;

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Music\\8_bit_clash.mp3"));
        camera = new OrthographicCamera(627, 420);
        // make the stage an input processor
        stage = new Stage(new FillViewport(627, 420, camera));
        Gdx.input.setInputProcessor(stage);
        //initialize texture for buttons
        newGameButtonTexture = new Texture(Gdx.files.internal("buttons\\NewGameButton.png"));
        optionsButtonTexture = new Texture(Gdx.files.internal("buttons\\OptionsButton.png"));
        quitButtonTexture = new Texture(Gdx.files.internal("buttons\\QuitButton.png"));
        skin = new Skin();
        skin.add("newGame", newGameButtonTexture);
        skin.add("options", optionsButtonTexture);
        skin.add("quit", quitButtonTexture);
        backgroundTexture = new Texture(Gdx.files.internal("Menus\\image.png"));
        background = new Image(backgroundTexture);
    }


    @Override
    public void show() {
        backgroundMusic.setVolume(Start.volume);
        backgroundMusic.play();
        backgroundMusic.setLooping(true);
        // table to organize buttons; fills screen
        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(false);
        stage.addActor(table);

        ImageButton.ImageButtonStyle newGameStyle = new ImageButton.ImageButtonStyle(skin.getDrawable("newGame"), skin.getDrawable("newGame"), skin.getDrawable("newGame"), skin.getDrawable("newGame"), skin.getDrawable("newGame"), skin.getDrawable("newGame"));
        ImageButton.ImageButtonStyle optionsStyle = new ImageButton.ImageButtonStyle(skin.getDrawable("options"), skin.getDrawable("options"), skin.getDrawable("options"), skin.getDrawable("options"), skin.getDrawable("options"), skin.getDrawable("options"));
        ImageButton.ImageButtonStyle quitStyle = new ImageButton.ImageButtonStyle(skin.getDrawable("quit"), skin.getDrawable("quit"), skin.getDrawable("quit"), skin.getDrawable("quit"), skin.getDrawable("quit"), skin.getDrawable("quit"));

        ImageButton newGame = new ImageButton(newGameStyle);
        newGame.getImage().setFillParent(true);
        ImageButton options = new ImageButton(optionsStyle);
        options.getImage().setFillParent(true);
        ImageButton quit = new ImageButton(quitStyle);

        // add title and buttons to screen
        table.row().pad(-38, 5, 4, 0);
        table.add(newGame);
        table.row().pad(0, 2, 4, 0);
        table.add(options);
        table.row().pad(0, 5, 5, 0);
        table.add(quit);

        //add event listeners to buttons
        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                backgroundMusic.stop();
                newGame.removeListener(this);
                game.setScreen(new TransitionScreen(game, "MazeLevel"));
            }
        });

        options.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                backgroundMusic.stop();
                options.removeListener(this);
                game.setScreen(new OptionsMenu(game, null));
            }
        });

        quit.addListener(new ChangeListener() {
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
        game.batch.begin();
        game.batch.setProjectionMatrix(camera.combined);
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
        //get rid of objects to keep memory free
        stage.dispose();
        skin.dispose();
        newGameButtonTexture.dispose();
        optionsButtonTexture.dispose();
        quitButtonTexture.dispose();
        backgroundTexture.dispose();
        backgroundMusic.dispose();
    }
}
