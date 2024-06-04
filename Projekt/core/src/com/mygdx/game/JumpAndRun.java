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

    // sprite sizes
    private static final int boosterWidth = 100;
    private static final int boosterHeigth = 100;
    private static final int waveWidth = 64;
    private static final int waveHeigth = 64;

    private static final int platformWidth = 100;
    private static final int platformHeigth = 10;
    final Start game;
    private Sprite player;
    private Texture playerTexture;
    private Texture heartTexture;
    private Texture zoneTexture;
    private Texture waveTexture;
    private Texture boosterTexture;
    private Texture platformTexture;

    private Texture background1Texture;
    private OrthographicCamera camera;
    private Conductor conductor;
    private Music song;
    private Array<Sprite> hearts;
    private Array<Sprite> waves;
    private Array<Sprite> platforms;
    private Array<Sprite> boosters;
    private Array<Powerup> powerups;
    private Array<Sprite> backgrounds;
    private final BitmapFont font = new BitmapFont();
    private int lives = 10;
    private int jumps = 2;
    private boolean isPaused;
    private int jumpTime;
    float volume = 1;
    float move;

    float currentCenterX; // used to spawn and move other objects relative to it
    float currentCornerX; // used as a new zero point for drawing/spawning the sprites relative to the screen

    float fallSpeedMod;
    int fallSpeedChangeTime;

    float speedModHor = 1;
    int speedModHorChangeTime;

    // last variables used to prevent items from spawning to often

    private long lastWaveTime;
    private long lastBoosterTime;
    private long lastPlatformTime;
    private long lastPowerupTime;
    private boolean canSpawn;

    // Powerups

    private int shield;

    float test1 = 1;
    float test2 = 1;
    int counter = 0;

    Rectangle zone; // used to give points
    private static final boolean DEBUGGING = true; // so that i can see various stats if enabled

    public JumpAndRun(final Start game) {
        this.game = game;
        // initialise textures
        playerTexture = new Texture("characterSprite\\playerSprite.png");
        waveTexture = new Texture("characterSprite\\playerSprite.png");
        heartTexture = new Texture("jumpAndRunSprites\\heartsprite_test.png");
        boosterTexture = new Texture("jumpAndRunSprites\\booster.png");
        platformTexture = new Texture("jumpAndRunSprites\\platform.png");
        zoneTexture = new Texture("jumpAndRunSprites\\heartsprite_test.png");
        background1Texture = new Texture("jumpAndRunSprites\\test.png");

        player = new Sprite(playerTexture, 64,64 );
        player.setX(1920 / 2);
        player.setY(1080 / 2);

        isPaused = false;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        song = Gdx.audio.newMusic(Gdx.files.internal("Music\\testBeat.mp3"));
        conductor = new Conductor(120, 0);
        shield = 0;
        conductor.start();


        fallSpeedChangeTime = 0;
        currentCenterX = MAX_WIDTH/2;
        currentCornerX = currentCenterX - MAX_WIDTH/2;


        // initilising Arrays

        hearts = new Array<>();
        waves = new Array<>();
        boosters = new Array<>();
        platforms = new Array<>();
        powerups = new Array<>();
        // array that holds the hearts and sets them
        for (int i = 0;i < 10; i++) {
            Sprite heart = new Sprite(heartTexture,64,64);
            hearts.add(heart);
            heart.setX(10 + (i* heart.getWidth()) +10 );
            heart.setY(MAX_HEIGTH - heart.getHeight() * 2);
        }
        // initilised the first two backgrounds
        backgrounds = new Array<>();
        Sprite background = new Sprite(background1Texture,0,0,MAX_WIDTH,MAX_HEIGTH);// had a problem where the second spawned bugged when not created similar to the first (srX doesnt seem to do anything)
        backgrounds.add(background);
        spawnBackground(0); //



    }

    @Override
    public void show() {
        setIsPaused(false);
        song.setVolume(Start.volume);
        song.play();

    }

    public void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLUE);



        game.batch.begin();
        // draw point zone






        if (!isPaused) {

            for(Sprite background: backgrounds) {
                background.draw(game.batch);
                if (counter == 0) test1 = background.getX();
                if (counter == 1) test2=  background.getX();
                counter ++;
            }

            player.draw(game.batch);
            camera.position.x = player.getX();

            camera.update();
            game.batch.setProjectionMatrix(camera.combined);
            //conductor.songPosition = song.getPosition();
            update();

            currentCenterX += 300 * Gdx.graphics.getDeltaTime();
            currentCornerX = currentCenterX - MAX_WIDTH/ 2;

            // Draw Hearts
            for (int i = 0; i <lives; i++) { // draw as many hearts as there are lives
                hearts.get(i).setX( currentCornerX + 20 +(i* hearts.get(i).getWidth()));
                hearts.get(i).draw(game.batch);
            }
            // Draw Waves
            for(Sprite wave: waves) {
                wave.draw(game.batch);
            }

            for(Powerup powerup: powerups) {
                powerup.draw(game.batch);
            }

            for(Sprite platform: platforms) {
                platform.draw(game.batch);
            }

            // Draw Boosters
            for(Sprite booster: boosters) {
                game.batch.draw(boosterTexture, booster.getX(), booster.getY());
            }


            if (DEBUGGING) {
                font.draw(game.batch,"X1 " + test1 + "X2 " + test2 +"spedMod = " + speedModHor + "Speed time = " + speedModHorChangeTime + "Lives : " + lives + " Nr_Boosters : " + boosters.size + "  Jumptime = " + jumpTime + " Nr of jumps = " + jumps + " playery = " + player.getY() , MAX_WIDTH / 2 + currentCornerX, 900);
            }
        }

        game.batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            setIsPaused(true);
            song.pause();
            game.setScreen(new PauseScreen(game, this));
        }

        player.setX(currentCenterX);


        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && jumps > 0 ) { // just pressed so that the player has to press space again to double jump
            move = player.getY() + 800 * Gdx.graphics.getDeltaTime();
            if (move + player.getHeight() > MAX_HEIGTH) move = player.getY();
            player.setY(move);
            jumpTime = 20;
            jumps -= 1;

        } else if (jumpTime <= 0) {
            float fallspeed = fallSpeedMod * 600 * Gdx.graphics.getDeltaTime();
            move = player.getY() - fallspeed;
            float checkPlatform = checkPlatforms();
            if (move < 0) move = 0;
            else if (checkPlatform!= -100 ) {
                if (jumps < 2)jumps = 2;
                move = checkPlatform;
            }
            player.setY(move);
            if (player.getY() == 0 && jumps < 2)jumps = 2; // when the player has hit the ground he can jump again

        } else {
            jumpTime -= 1;
            move = player.getY() + 800 * Gdx.graphics.getDeltaTime();
            if (move + player.getHeight() > MAX_HEIGTH) move = player.getY();
            player.setY(move);
        }

        // Wave Objects

        for (Iterator<Sprite> iter = waves.iterator(); iter.hasNext(); ) {
            Sprite wave = iter.next();
            if(wave.getX() < currentCornerX - wave.getWidth()) iter.remove();

            if(overlap(wave)) {
                iter.remove();
                if (shield > 0) shield -=1;
                else lives -= 1;
            }
        }

        // Boosters
        for (Iterator<Sprite> iter = boosters.iterator(); iter.hasNext(); ) {
            Sprite booster = iter.next();
            if(booster.getX() < currentCornerX - booster.getWidth()) iter.remove();

            if(overlap(booster)) {
                fallSpeedMod = (float) -2; // negative because the fallspeed is multiplied with it
                fallSpeedChangeTime = 20;
                iter.remove();
            }
        }

        // platforms
        for (Iterator<Sprite> iter = platforms.iterator(); iter.hasNext(); ) {
            Sprite platform = iter.next();
            if(platform.getX() < currentCornerX - platform.getWidth()) iter.remove();
            }

        // powerups
        for (Iterator<Powerup> iter = powerups.iterator(); iter.hasNext(); ) {
            Powerup powerup = iter.next();
            if(powerup.getX() < currentCornerX - powerup.getWidth()) iter.remove();

            if(overlap(powerup)) {
                iter.remove();
                if(powerup.getPower() == Powerup.Power.moreJumps)jumps = 3;
                else if(powerup.getPower() == Powerup.Power.shield)shield = 1;
                else if(powerup.getPower() == Powerup.Power.live) {
                    if (lives < 10) lives++;
                };

            }
        }

        // backgrounds
        for (Iterator<Sprite> iter = backgrounds.iterator(); iter.hasNext(); ) {
            Sprite background= iter.next();
            if(background.getX()  < currentCornerX - background.getWidth()) {
                iter.remove();
                spawnBackground(currentCornerX);
            }
        }


        if (fallSpeedChangeTime > 0 ) fallSpeedChangeTime -= 1;
        else if (fallSpeedChangeTime == 0) fallSpeedMod = 1;

        if(TimeUtils.nanoTime() - lastWaveTime > 1000000000 && canSpawn) spawnWave(currentCornerX);
        if(TimeUtils.nanoTime() - lastBoosterTime > 10000000000L  && canSpawn) spawnBooster(currentCornerX);
        if(TimeUtils.nanoTime() - lastPlatformTime > 1000000000 && (Math.random() > 0.5)  && canSpawn) spawnPlatform(currentCornerX);
        if(TimeUtils.nanoTime() - lastPowerupTime > 10000000000L && (Math.random() > 0.75)  && canSpawn) spawnPowerup(currentCornerX);
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

    private boolean overlap(Sprite sp1, Sprite sp2) {
        if (sp2.getX() + sp2.getWidth() < sp1.getX()) return false;
        if (sp2.getY() < sp1.getY()) return false;
        if (sp2.getY() > sp1.getY() + sp1.getHeight()) return false;
        if (sp2.getX() > sp1.getX() + sp1.getWidth()) return false;
        return true;
    }

    private boolean overlap (Sprite sp) {
        return overlap(sp,player);
    }

    private boolean overlap (Array<Sprite> arr, Sprite sp) {
        for (Sprite item : arr) {
            if (overlap(item,sp)) return true;
        }
        return false;
    }

    private float checkPlatforms() {
        for (Sprite platform : platforms) {
            if (overlap(platform) && jumpTime == 0) return platform.getY()+platform.getHeight();
        }
        return -100; // return as a false
    }

    private void spawnWave(float leftXCorner) {
        double random = Math.random();
        Sprite wave = new Sprite(waveTexture,waveWidth,waveHeigth);
        int x =  (int)leftXCorner + MAX_WIDTH;
        int y = 0;

        if (random < 0.33) y = (int) (200 +  100*Math.random());
        else if (random < 0.66) y = (int) (450 +  100*Math.random());
        spawnSpritesetup(waves,wave,x,y);
        lastWaveTime = TimeUtils.nanoTime();
    }

    private void spawnBooster(float leftXCorner) {
        double random = Math.random();
        Sprite booster = new Sprite(boosterTexture,boosterWidth,boosterHeigth );
        booster.setX((int)leftXCorner + MAX_WIDTH);
        if (random < 0.33) booster.setY(0);
        else if (random < 0.66)booster.setY((int) (200 +  100*Math.random())) ;
        else booster.setY((int) (450 +  100*Math.random()));
        boosters.add(booster);
        lastBoosterTime = TimeUtils.nanoTime();
    }


    private void spawnPlatform(float leftXCorner) {
        double random = Math.random();
        Sprite platform = new Sprite(platformTexture,platformWidth,platformHeigth);
        int x = (int)leftXCorner + MAX_WIDTH;

        int y = (int) (200 +  150*Math.random());
        if (random > 0.5) y = (int) (450 +  150*Math.random());
        spawnSpritesetup(platforms,platform,x,y);
        lastPlatformTime = TimeUtils.nanoTime();
    }

    private void spawnBackground(float leftXCorner) {
        Sprite background = new Sprite(background1Texture,MAX_WIDTH,MAX_HEIGTH);
        int x = (int)leftXCorner + MAX_WIDTH;
        spawnSpritesetup(backgrounds,background,x,0);
    }

    private void spawnPowerup(float leftXCorner) {
        Powerup powerup;

        double random = Math.random();
        int x = (int)leftXCorner + MAX_WIDTH;
        int y = (int) (200 +  150*Math.random());
        if (random > 0.5) y = (int) (450 +  150*Math.random());
        double effect = Math.random();
        if (effect < 0.33) powerup = Powerup.createPowerup(Powerup.Power.moreJumps);
        else if (effect < 0.66)powerup = Powerup.createPowerup(Powerup.Power.live);
        else powerup = Powerup.createPowerup(Powerup.Power.shield);

        spawnSpritesetup(powerups,powerup,x,y);
        lastPowerupTime = TimeUtils.nanoTime();
    }

    private void spawnSpritesetup(Array arr, Sprite sp, int x, int y) { // used to avoid code compilation try later to remove unsafe operation
        sp.setX(x);
        sp.setY(y);
        arr.add(sp);
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
