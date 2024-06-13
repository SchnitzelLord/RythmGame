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

import java.util.Iterator;

public class JumpAndRun implements Screen {
    final Start game;
    private OrthographicCamera camera;
    private Conductor conductor;
    private Music song;
    private static final int MAX_HEIGTH = 1080;
    private static final int MAX_WIDTH = 1920;
    private static final int itemSpeed = 300;

    private static final boolean DEBUGGING = false; // so that one can view see various stats if enabled

    // sprite sizes
    private static final int boosterWidth = 100;
    private static final int boosterHeigth = 100;
    private static final int waveWidth = 63;
    private static final int waveHeigth = 36;
    private static final int waveSPawnVariantion = 150;

    // Textures -----------------------------------------------------------------------
    private Texture playerTexture;  // this is used for debugging with the playerSprite
    // 3 textrues for the three sprites in the walkcycle

    private Texture playerTexture0;
    private Texture playerTexture1;
    private Texture playerTexture2;
    private Texture debugBeatTexture;
    private Texture heartTexture;
    private Texture waveTexture0;
    private Texture waveTexture1;
    private Texture waveTexture2;
    private Texture waveTexture3;
    private Texture boosterTexture;
    private Texture backgroundTexture;

    // items-----------------------------------------------------------------------

    private Sprite player; // Spirte used for the game logic involving the player is not drawn
    private Sprite player_walk0;
    private Sprite player_walk1;
    private Sprite player_walk2;  // three player walk for the animation fo the player movement
    Sprite debugBeat;
    private Array<Sprite> hearts;
    private Array<Sprite> waves;
    private Array<Platform> platforms;
    private Array<Sprite> boosters;
    private Array<Powerup> powerups;
    private Array<Sprite> backgrounds;
    private final BitmapFont font = new BitmapFont();

    // variables used in the game logic

    private int lives = 5;
    private int jumps = 2;
    private int jumpTime;
    private int fallSpeedChangeTime;
    private int shield;
    private int counter = 0; // counter used for the walk animation of the player
    private float volume = 1;
    private float move;
    private float fallSpeedMod;
    private float speedModHor = 1;
    private boolean isPaused;

    // last variables used to prevent items from spawning to often
    private long lastWaveTime;
    private long lastBoosterTime;
    private long lastPlatformTime;
    private long lastPowerupTime;
    private boolean canSpawn;
    private float debug_fallspeed;
    private int debug_remove;
    private int levelId;

    private JumpAndRun(final Start game) {  // dont use this constructor use the one with levelId
        this.game = game;
        // initialise textures
        playerTexture = new Texture("characterSprite\\playerSprite.png");
        playerTexture0 = new Texture("characterSprite\\player0.png");
        playerTexture1 = new Texture("characterSprite\\player0.png");
        playerTexture2 = new Texture("characterSprite\\player1.png");
        waveTexture0 = new Texture("jumpAndRunSprites\\wave0.png");
        waveTexture1 = new Texture("jumpAndRunSprites\\wave1.png");
        waveTexture2 = new Texture("jumpAndRunSprites\\wave2.png");
        waveTexture3 = new Texture("jumpAndRunSprites\\wave3.png");
        heartTexture = new Texture("jumpAndRunSprites\\heartsprite.png");
        boosterTexture = new Texture("jumpAndRunSprites\\booster2.png");

        debugBeatTexture = new Texture("jumpAndRunSprites\\debugBeat2.png");

        player = new Sprite(playerTexture, 128 ,128  ); // used for the game logic is not visable
        player_walk0 = new Sprite(playerTexture0, 128 ,128  ); // standing frame
        player_walk1 = new Sprite(playerTexture1, 128 ,128 ); // one step frame
        player_walk2 = new Sprite(playerTexture2, 128 ,128 ); // other step

        player.setX(MAX_WIDTH / 2- 32);
        player.setY(MAX_HEIGTH / 2);

        isPaused = false;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        shield = 0;
        fallSpeedChangeTime = 0;
        // initializing Arrays

        hearts = new Array<>();
        waves = new Array<>();
        boosters = new Array<>();
        platforms = new Array<>();
        powerups = new Array<>();
        // array that holds the hearts and sets them
        for (int i = 0;i < 10; i++) {
            Sprite heart = new Sprite(heartTexture,64,64);
            hearts.add(heart);
            heart.setY(MAX_HEIGTH - heart.getHeight() * 2);
        }
        // initialised the first two backgrounds
    };

    public JumpAndRun(final Start game, int levelId) { // levelId sets the background, thus it is used to distinguish the two levels (0 = Way to uni 1 = way back)
        this(game);
        if (levelId == 1)backgroundTexture = new Texture("jumpAndRunSprites\\Background_Sunrise.png");//if levelId == 0 set the background to the one for the first level
        else backgroundTexture = new Texture("jumpAndRunSprites\\Background_Sunset.png"); // other case when it isn't the first level levelId == 1 not used because it could cause crashes defaults to first level
        if (levelId == 0) song = Gdx.audio.newMusic(Gdx.files.internal("Music\\Homeway_120bpm.mp3"));
        else song = Gdx.audio.newMusic(Gdx.files.internal("Music\\Homeway_120bpm.mp3"));
        conductor = new Conductor(120, 0);
        backgrounds = new Array<>();
        Sprite background = new Sprite(backgroundTexture,0,0,MAX_WIDTH,MAX_HEIGTH);// had a problem where the second spawned bugged when not created similar to the first (srX doesnt seem to do anything)
        backgrounds.add(background);
        spawnBackground(); //
        debugBeat = new Sprite(debugBeatTexture,10,0,5,1080);
        if(levelId == 1) song.setOnCompletionListener((a)-> game.setScreen(new TransitionScreen(game,"Homeway")));
        else song.setOnCompletionListener((a)-> game.setScreen(new TransitionScreen(game,"OcarinaLevel")));
        conductor.start();
        this.levelId = levelId;
    }

    @Override
    public void show() {
        setIsPaused(false);
        song.setVolume(Start.volume);
        song.play();
        game.batch.setProjectionMatrix(camera.combined);
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
            camera.update();
            update(); // update the conductor
            draw(backgrounds,waves, powerups,platforms,boosters,platforms);

            setplayerCords(player,player_walk0,player_walk1,player_walk2);

            for (int i = 0; i < lives; i++) { // draw as many hearts as there are lives
                hearts.get(i).setX( 20 +(i* hearts.get(i).getWidth()));
                hearts.get(i).draw(game.batch);
            }

            if (counter/ 10 == 0  ) player_walk0.draw(game.batch);
            else if (counter/ 10 == 1)player_walk1.draw(game.batch);
            else if (counter/ 10 == 2)player_walk0.draw(game.batch);
            else if (counter/ 10 == 3)player_walk2.draw(game.batch);

            counter += 3;
            if (counter > 39) counter = 0;


            if (DEBUGGING) {
                font.draw(game.batch," removed count "+ debug_remove + " backgroundarray length = "+ backgrounds.size + " fallspeed = " + debug_fallspeed + " spedMod = " + speedModHor + "Speed time = " + "Lives : " + lives + " Nr_Boosters : " + boosters.size + "  Jumptime = " + jumpTime + " Nr of jumps = " + jumps + " playery = " + player.getY() , MAX_WIDTH / 2, 900);
                game.batch.draw(debugBeatTexture,MAX_WIDTH/2-2,0);

                player.draw(game.batch);
            }
        }
        game.batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            setIsPaused(true);
            song.pause();
            game.setScreen(new PauseScreen(game, this));
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            jumpTime = 0;
            jumps = 0;
            fallSpeedMod = 2;
            fallSpeedChangeTime = 20;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && jumps > 0 ) { // just pressed so that the player has to press space again to double jump
            move = player.getY() + 800 * Gdx.graphics.getDeltaTime();
            if (move + player.getHeight() > MAX_HEIGTH) move = player.getY();
            player.setY(move);
            jumpTime = 20;
            jumps -= 1;

        } else if (jumpTime <= 0) { // player character falls when this is active
            move = player.getY() - fallSpeedMod * 600 * Gdx.graphics.getDeltaTime();
            debug_fallspeed = - fallSpeedMod * 600 * Gdx.graphics.getDeltaTime();
            float checkPlatform = checkPlatforms();

            if (checkPlatform!= -100 ) {
                if (jumps < 2)jumps = 2;
                move = checkPlatform;
                fallSpeedMod = 1;
            } else if (move < 10) { // < 10 because 10 as the lower boundary looks nicer with the background then 0
                move = 10;
                fallSpeedMod = 1;
            }
            player.setY(move);
            if (player.getY() == 10 && jumps < 2)jumps = 2; // when the player has hit the ground he can jump again

        } else { // when the player characters moves up
            jumpTime -= 1;
            move = player.getY() + 800 * Gdx.graphics.getDeltaTime();
            if (move + player.getHeight() > MAX_HEIGTH) move = MAX_HEIGTH-player.getHeight();
            player.setY(move);
        }

        // Wave Objects

        for (Iterator<Sprite> iter = waves.iterator(); iter.hasNext(); ) {
            Sprite wave = iter.next();
            wave.setX(wave.getX() - itemSpeed * Gdx.graphics.getDeltaTime());
            if(wave.getX() + wave.getWidth()< 0) iter.remove();

            if(overlap(wave)) {
                iter.remove();
                if (shield > 0) shield -=1;
                else lives -= 1;
            }
        }

        // Boosters
        for (Iterator<Sprite> iter = boosters.iterator(); iter.hasNext(); ) {
            Sprite booster = iter.next();
            booster.setX(booster.getX() - itemSpeed * Gdx.graphics.getDeltaTime());
            if(booster.getX()+ booster.getWidth() < 0) iter.remove();

            if(overlap(booster)) {
                fallSpeedMod = (float) -2; // negative because the fallspeed is multiplied with it
                fallSpeedChangeTime = 20;
                iter.remove();
            }
        }

        // platforms
        for (Iterator<Platform> iter = platforms.iterator(); iter.hasNext(); ) {
            Sprite platform = iter.next();
            platform.setX(platform.getX() - itemSpeed * Gdx.graphics.getDeltaTime());
            if(platform.getX()+ platform.getWidth()< 0) iter.remove();
            }

        // powerups
        for (Iterator<Powerup> iter = powerups.iterator(); iter.hasNext(); ) {
            Powerup powerup = iter.next();
            powerup.setX(powerup.getX() - itemSpeed * Gdx.graphics.getDeltaTime());
            if(powerup.getX() + powerup.getWidth()< 0) iter.remove();

            if(overlap(powerup)) {
                iter.remove();
                if(powerup.getPower() == Powerup.Power.moreJumps)jumps = 3;
                else if(powerup.getPower() == Powerup.Power.shield)shield = 1;
                else if(powerup.getPower() == Powerup.Power.live) {
                    if (lives < 5) lives++;
                };
            }
        }

        // backgrounds
        for (Iterator<Sprite> iter = backgrounds.iterator(); iter.hasNext(); ) {
            Sprite background= iter.next();
            background.setX(background.getX() - itemSpeed * Gdx.graphics.getDeltaTime());
            if(background.getX()  + background.getWidth() < 0) {
                spawnBackground();
                iter.remove();
                debug_remove ++;
            }
        }

        if (fallSpeedChangeTime > 0 ) fallSpeedChangeTime -= 1;
        else if (fallSpeedChangeTime == 0) fallSpeedMod = 1;

        if(TimeUtils.nanoTime() - lastWaveTime > 1500000000 && canSpawn) {
            spawnWavebot();
            spawnWavetop();
        };
        if(TimeUtils.nanoTime() - lastBoosterTime > 10000000000L  && canSpawn) spawnBooster();
        if(TimeUtils.nanoTime() - lastPlatformTime > 1000000000L && (Math.random() > 0.5)  && canSpawn) spawnPlatform();
        if(TimeUtils.nanoTime() - lastPowerupTime > 10000000000L && (Math.random() > 0.75)  && canSpawn) spawnPowerup();
        if (lives <= 0)  {
            if (levelId == 1) game.setScreen(new GameOver(game, "Homeway"));
            else {
                game.setScreen(new GameOver(game, "WayThere"));
                song.stop();
            }
        };
    }

    public void update() {
        if (song.getPosition() >= conductor.lastBeat + conductor.crochet - 0.3f && song.getPosition()<= conductor.lastBeat + conductor.crochet  + 0.3f) {
            canSpawn = true;
            conductor.lastBeat += conductor.crochet;
        } else {
            canSpawn = false;
        }
    }

    public static boolean overlap(Sprite sp1, Sprite sp2) {
        if (sp2.getX() + sp2.getWidth() < sp1.getX()) return false;
        if (sp2.getY() + sp2.getHeight() < sp1.getY()) return false;
        if (sp2.getY() > sp1.getY() + sp1.getHeight()) return false;
        if (sp2.getX() > sp1.getX() + sp1.getWidth()) return false;
        return true;
    }

    public boolean overlap (Sprite sp) {
        return overlap(sp,player);
    }

    private boolean overlap (Array< ? extends Sprite> arr, Sprite sp) {
        if (arr == platforms) {
            for (Platform item : (Array<Platform>)arr) {
                if (item.checkIfspritesOverlap(sp)) return  true;

            }
        }else {
            for (Sprite item : arr) {
                if (overlap(item, sp)) return true;
            }
        }
        return false;
    }

    private float checkPlatforms() {
        for (Platform platform : platforms) {
            Data data = platform.overlap(player);
            if (data.isOverlap() && jumpTime == 0) return data.getY();
        }
        return -100; // return as a false
    }

    private Sprite randomWave() {
        Sprite wave;
        double random = Math.random() * 100;
        if (random < 25) wave = new Sprite(waveTexture0,waveWidth,waveHeigth);
        else if (random < 50) wave = new Sprite(waveTexture1,waveWidth,waveHeigth);
        else if (random < 75) wave = new Sprite(waveTexture2,waveWidth,waveHeigth);
        else wave = new Sprite(waveTexture3,waveWidth,waveHeigth);
        return wave;
    }



    private void spawnWavebot() {
        double random = Math.random();
        Sprite wave = randomWave();
        int y;
        if(random < 0.4) y = 0;
        else if (random < 0.8) y = (int) (200 +  waveSPawnVariantion*Math.random());
        else y = (int) (MAX_HEIGTH * random);
        boolean noOverlap = spawnSpriteSetup(waves,wave,MAX_WIDTH,y,true);
        if (!noOverlap) return; // cancel when overlap otherwise stack overflow error sometimes
        lastWaveTime = TimeUtils.nanoTime();
    }

    private void spawnWavetop() {
        double random = Math.random();
        Sprite wave = randomWave();
        int y;
        if (random < 0.5) y = (int) (450 +  100*Math.random());
        else if  (random < 0.7) y = (int) (800 +  waveSPawnVariantion*Math.random());
        else if  (random < 0.9) y = (int) (800 +  waveSPawnVariantion*Math.random());
        else y = (int) (MAX_HEIGTH * random);

        boolean noOverlap = spawnSpriteSetup(waves,wave,MAX_WIDTH,y,true);
        if (!noOverlap) return; // cancel when overlap otherwise stack overflow error sometimes
        lastWaveTime = TimeUtils.nanoTime();
    }

    private void spawnBooster() {
        double random = Math.random();
        Sprite booster = new Sprite(boosterTexture,boosterWidth,boosterHeigth );
        int y = 0;
        if (random < 0.33) booster.setY(0);
        else if (random < 0.66) y = (int) (450 +  100*Math.random()) ;
        else y = (int) (700 +  100*Math.random());
        boolean noOverlap = spawnSpriteSetup(boosters,booster,MAX_WIDTH,y,true);
        if (!noOverlap) return; // cancel when overlap otherwise stack overflow error sometimes
        lastBoosterTime = TimeUtils.nanoTime();
    }


    private void spawnPlatform() {
        double random = Math.random();
        Platform platform = Platform.createRandomPlatform();
        System.out.print(platform.getWidth());
        double randomY = Math.random();
        int y = (int) (30 + 250* randomY);

        boolean noOverlap = spawnSpriteSetup(platforms,platform,MAX_WIDTH,y,true);
        if (!noOverlap) return; // cancel when overlap otherwise stack overflow error sometimes
        lastPlatformTime = TimeUtils.nanoTime();
    }



    private void spawnBackground() {
        Sprite background = new Sprite(backgroundTexture,MAX_WIDTH,MAX_HEIGTH);
        spawnSpriteSetup(backgrounds,background,MAX_WIDTH-1,0,false); // Maxwidth -1 one because it seems to reduce the amount of times where the blue background "flickers" through the background image
    }
    private void spawnPowerup() {
        Powerup powerup;

        double random = Math.random();
        int y = (int) (200 +  150*Math.random());
        if (random > 0.5) y = (int) (450 +  150*Math.random());
        double effect = Math.random();
        if (effect < 0.33) powerup = Powerup.createPowerup(Powerup.Power.moreJumps);
        else if (effect < 0.66)powerup = Powerup.createPowerup(Powerup.Power.live);
        else powerup = Powerup.createPowerup(Powerup.Power.shield);
        boolean noOverlap = spawnSpriteSetup(powerups,powerup,MAX_WIDTH,y,true);
        if (!noOverlap) return; // cancel when overlap otherwise stack overflow error sometimes
        lastPowerupTime = TimeUtils.nanoTime();
    }

    private <T extends Sprite> boolean spawnSpriteSetup(Array<T> arr, T sp, int x, int y, boolean check) { // used to avoid code compilation try later to remove unsafe operation
        sp.setX(x);
        sp.setY(y);
        if (check) {
            if (overlap(boosters, sp)) return false;// return false to signal that there is an overlap
            else if (overlap(platforms, sp)) return false; // shouldn't spawn in the platform sprite so the custom overlap is not used
            else if (overlap(powerups, sp)) return false;
            else if (overlap(waves, sp)) return false;
        }
        arr.add(sp);
        return true;
    }

    @SafeVarargs
    private final void draw(Array<? extends Sprite>... arr) { // methode to avoid code repetition to draw all my items
        for (Array<? extends Sprite> itemarray : arr) {
            for(Sprite item : itemarray) {
                item.draw(game.batch);
            }
        }
    }

    private void setplayerCords(Sprite player,Sprite ... walks) { // function that sets all the player Sprites to the one used in the gmae logic (first one)
        float x = player.getX();
        float y = player.getY();
        for(Sprite playerWalk : walks) {
            playerWalk.setX(x);
            playerWalk.setY(y);
        }
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
        waveTexture0.dispose();
        heartTexture.dispose();
        boosterTexture.dispose();
        backgroundTexture.dispose();
        song.dispose();
        playerTexture0.dispose();
        playerTexture1.dispose();
        playerTexture2.dispose();
        debugBeatTexture.dispose();
        heartTexture.dispose();
        waveTexture0.dispose();
        waveTexture1.dispose();
        waveTexture2.dispose();
        waveTexture3.dispose();
        boosterTexture.dispose();
        backgroundTexture.dispose();
    }
}
