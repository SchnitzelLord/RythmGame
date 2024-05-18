package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.awt.*;
import java.util.Iterator;

public class JumpAndRun implements Screen {

    private static final int MAX_HEIGTH = 1080;
    private static final int MAX_WIDTH = 1920;
    final Start game;
    private Sprite player;
    private Texture playerTexture;
    private Texture heartTexture;
    private Texture waveTexture;
    private Texture boosterTexture;
    private OrthographicCamera camera;
    private Conductor conductor;
    private Music song;
    private Array<Sprite> hearts;
    private Array<Sprite> waves;
    private Array<Rectangle> boosters;
    private Array<Rectangle> rectangles;
    private BitmapFont font = new BitmapFont();
    private int lives = 10;
    private int jumps = 2;
    private boolean isPaused;
    private int jumpTime;
    float volume = 1;
    float move;

    float fallSpeedMod;
    int fallSpeedChangeTime;
    private long lastRectangleTime;
    private long lastWaveTime;
    private long lastBoosterTime;
    private boolean canSpawn;
    
    private static final boolean debugging = true; // so that i can see various stats if enabled

    // sprite sizes
    private int boosterWidth = 100;
    private int boosterHeigth = 100;

    public JumpAndRun(final Start game) {
        this.game = game;
        // initialise textures
        playerTexture = new Texture("characterSprite\\playerSprite.png");
        waveTexture = new Texture("characterSprite\\playerSprite.png");
        heartTexture = new Texture("characterSprite\\heartsprite_test.png");
        boosterTexture = new Texture("characterSprite\\booster.png");

        player = new Sprite(playerTexture, 64,64 );
        player.setX(1920 / 2);
        player.setY(1080 / 2);

        isPaused = false;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        song = Gdx.audio.newMusic(Gdx.files.internal("Music\\testBeat.mp3"));
        conductor = new Conductor(120, 0);
    }

    @Override
    public void show() {
        setIsPaused(false);
        song.setVolume(Start.volume);
        song.play();
        conductor.start();
        fallSpeedChangeTime = 0;

        rectangles =  new Array<>(); // used for platforms
        hearts = new Array<>();
        waves = new Array<>();
        boosters = new Array<>();
        // array that holds the hearts
        for (int i = 0;i < 10; i++) {
            Sprite heart = new Sprite(heartTexture,64,64);
            hearts.add(heart);
            heart.setX(10 + (i* heart.getWidth()) +10 );
            heart.setY(MAX_HEIGTH - heart.getHeight() * 2);
        }
    }

    public void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLUE);

        game.batch.begin();
        player.draw(game.batch);
        if (!isPaused) {
            camera.update();
            game.batch.setProjectionMatrix(camera.combined);
            //conductor.songPosition = song.getPosition();
            update();

            // Draw Hearts
            for (int i = 0; i <lives; i++) { // draw as many hearts as there are lives
                hearts.get(i).draw(game.batch);
            }
            // Draw Waves
            for(Sprite wave: waves) {
                wave.draw(game.batch);
            }

            // Draw Boosters
            for(Rectangle booster: boosters) {
                game.batch.draw(boosterTexture, booster.x, booster.y);
            }



            if (debugging) font.draw(game.batch, "Lives : " + lives + " Nr_Boosters : " + boosters.size + "  Jumptime = " + jumpTime + " Nr of jumps = " + jumps + " playery = " + player.getY() , MAX_WIDTH / 2, 900);
        }

        game.batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            setIsPaused(true);
            song.pause();
            game.setScreen(new PauseScreen(game, this));
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            move = player.getX() - 300 * Gdx.graphics.getDeltaTime();
            if (move <= 0) move = 0;
            player.setX(move);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            move = player.getX() + 300 * Gdx.graphics.getDeltaTime();
            if (move + player.getWidth()> MAX_WIDTH) move = MAX_WIDTH - player.getWidth(); // minus player.getwidth so that set places the sprite with the rigth lower corner at the rigth limit
            player.setX(move);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && jumps > 0 ) { // just pressed so that the player has to press space again to double jump
            move = player.getY() + 800 * Gdx.graphics.getDeltaTime();
            if (move + player.getHeight() > MAX_HEIGTH) move = player.getY();
            player.setY(move);
            jumpTime = 20;
            jumps -= 1;

        } else if (jumpTime <= 0) {
            float fallspeed = fallSpeedMod * 600 * Gdx.graphics.getDeltaTime();
            move = player.getY() - fallspeed;
            if (move < 0) move = 0;
            player.setY(move);
            if (player.getY() == 0)jumps = 2; // when the player has hit the ground he can jump again

        } else {
            jumpTime -= 1;
            move = player.getY() + 800 * Gdx.graphics.getDeltaTime();
            if (move + player.getHeight() > MAX_HEIGTH) move = player.getY();
            player.setY(move);
        }
        // Testing for Rectangles
        for (Iterator<Rectangle> iter = rectangles.iterator(); iter.hasNext(); ) {
            Rectangle rectangle = iter.next();
            rectangle.x -= (int) (150 * Gdx.graphics.getDeltaTime());
            if(rectangle.x < 0) iter.remove();

            if(overlap(rectangle)) {
                iter.remove();
                lives -= 1;
            }
        }

        // Wave Objects

        for (Iterator<Sprite> iter = waves.iterator(); iter.hasNext(); ) {
            Sprite wave = iter.next();
            wave.setX(wave.getX() - 150 * Gdx.graphics.getDeltaTime());
            if(wave.getX() < 0) iter.remove();

            if(overlap(wave)) {
                iter.remove();
                lives -= 1;
            }
        }

        // Boosters
        for (Iterator<Rectangle> iter = boosters.iterator(); iter.hasNext(); ) {
            Rectangle booster = iter.next();
            booster.x -= (int) (150 * Gdx.graphics.getDeltaTime());
            if(booster.x < 0) iter.remove();

            if(overlap(booster)) {
                fallSpeedMod = (float) -2; // negative because the fallspeed is multiplied with it
                fallSpeedChangeTime = 20;
                iter.remove();
            }
        }

        // Fallspeed
        if (fallSpeedChangeTime > 0 ) fallSpeedChangeTime -= 1;
        else if (fallSpeedChangeTime == 0) fallSpeedMod = 1;



        if(TimeUtils.nanoTime() - lastWaveTime > 1000000000 && canSpawn) spawnWave();
        if(TimeUtils.nanoTime() - lastBoosterTime > 10000000000L  && canSpawn) spawnBooster();
        if (lives <= 0) Gdx.app.exit();
    }

    public void update() {
        if (song.getPosition() >= conductor.lastBeat + conductor.crochet - 0.3f && song.getPosition() <= conductor.lastBeat + conductor.crochet + 0.3f) {
            canSpawn = true;
            conductor.lastBeat += conductor.crochet;
        } else {
            canSpawn = false;
        }
    }

    private boolean overlap (Rectangle rec) {
        if (player.getX() + player.getWidth() < rec.x) return false;
        if (player.getY() + player.getHeight() < rec.y) return false;
        if (player.getY() > rec.y + rec.height) return false;
        if (player.getX() > rec.x + rec.width) return false;
        return true;
    }

    private boolean overlap (Sprite sp) {
        if (player.getX() + player.getWidth() < sp.getX()) return false;
        if (player.getY() + player.getHeight() < sp.getY()) return false;
        if (player.getY() > sp.getY() + sp.getHeight()) return false;
        if (player.getX() > sp.getX() + sp.getWidth()) return false;
        return true;
    }

    private void spawnRectangle() {  // old version used for testing
        double random = Math.random();
        Rectangle rectangle = new Rectangle();
        rectangle.x = MAX_WIDTH;
        if (random < 0.33) rectangle.y = 0;
        else if (random < 0.66)rectangle.y = (int) (200 +  100*Math.random()) ;
        else rectangle.y = (int) (450 +  100*Math.random());
        rectangle.width = (int) player.getWidth();
        rectangle.height = (int) player.getHeight();
        rectangles.add(rectangle);
        lastRectangleTime = TimeUtils.nanoTime();
    }

    private void spawnWave() {
        double random = Math.random();
        Sprite wave = new Sprite(waveTexture,64,64);
        int x = MAX_WIDTH;
        int y = 0;

        if (random < 0.33) y = (int) (200 +  100*Math.random());
        else if (random < 0.66) y = (int) (450 +  100*Math.random());

        wave.setX(x);
        wave.setY(y);
        waves.add(wave);
        lastWaveTime = TimeUtils.nanoTime();
    }

    private void spawnBooster() {
        double random = Math.random();
        Rectangle booster = new Rectangle();
        booster.x = MAX_WIDTH;
        if (random < 0.33) booster.y = 0;
        else if (random < 0.66)booster.y = (int) (200 +  100*Math.random()) ;
        else booster.y = (int) (450 +  100*Math.random());
        booster.width = boosterWidth;
        booster.height = boosterHeigth;
        boosters.add(booster);
        lastBoosterTime = TimeUtils.nanoTime();
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
        playerTexture.dispose();
        song.dispose();
    }
}
