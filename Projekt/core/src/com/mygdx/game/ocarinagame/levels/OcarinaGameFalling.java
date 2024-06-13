package com.mygdx.game.ocarinagame.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Conductor;
import com.mygdx.game.GameOver;
import com.mygdx.game.Start;
import com.mygdx.game.TransitionScreen;
import com.mygdx.game.ocarinagame.Arrow;
import com.mygdx.game.ocarinagame.BeatMusic;
import com.mygdx.game.ocarinagame.ui.HUD;
import com.mygdx.game.ocarinagame.ui.ProgressBar;

import java.util.Iterator;

public final class OcarinaGameFalling extends AbstractOcarinaGame {

    // Game state constants
    private static final float SPEED = 75;
    private static final float DELAY = 3;

    // Arrow hit and spawn position
    private static final float HIT_POSITION = 1;

    // Hitzone
    private final Texture hitZoneTexture;
    private final Sprite hitZone;

    // Constructor

    public OcarinaGameFalling(Start game) {
        // Setup common game elements like
        // camera, viewpoint, textures, allArrows
        super(game);

        // Setup audio
        Music song = Gdx.audio.newMusic(Gdx.files.internal("Music\\zelda_fight_150bpm.mp3"));
        music = new BeatMusic(song, 150, 0, 37.144f, 37.878f);
        music.playMusicAfterSec(DELAY);
        song.dispose();
        conductor = new Conductor(music.getBPM(), 0);
        conductor.start();
        // Set first beat time stamp with delay since beat begins immediately
        conductor.lastBeat = music.getBeatStart() - conductor.crochet + DELAY;

        // Timer to switch to another screen depending on result after GAME_OVER_DELAY
        delayedSongOverSwitchScreen(music.getSongLength(), new TransitionScreen(game,"MazeLevel"), new GameOver(game, "WakeUp"));

        // Setup UI
        // Progressbar max is set to WIN_RATE * totalBeatCount, e.g. is progress bar full then the game is won
        hud = new HUD(game.batch, this);
        // Progressbar max is set to WIN_RATE * totalBeatCount, e.g. is progress bar full then the game is won
        // hud.getScoreProgressBar().setRange(0, music.getTotalBeatCount() * WIN_RATE);
        // Progressbar fills according to progress in song
        hud.getProgressBar().setRange(0, music.getSongLength());

        // Setup hitZone
        hitZoneTexture = new Texture("ocarina-game\\hit-zone.png");
        hitZone = new Sprite(hitZoneTexture, hitZoneTexture.getWidth(), hitZoneTexture.getHeight());
        hitZone.setPosition(0.5f * (WORLD_WIDTH - hitZone.getWidth()), HIT_POSITION );

        // Setup background
        backgroundTexture = new Texture("ocarina-game\\zelda-background.png");
        background = new Image(backgroundTexture);
    }

    // Overrides

    @Override
    public void render(float delta) {
        // Clear screen, update HUD & camera
        super.render(delta);

        if (isRunning) {
            // Check input and act upon them
            controlsAction();

            draw();

            // Spawn, move and delete arrows
            if (canArrowSpawn()) spawnArrow();
            moveArrowsDown(delta);
            deleteArrowsOutOfWorld();

            // If all live has been lost, restart game
            if (lives == 0) game.setScreen(new GameOver(game, "OcarinaLevel"));
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        hitZoneTexture.dispose();
    }

    @Override
    protected void setupArrowSpawnPosition(Arrow arrow) {
        Arrow.Direction direction = arrow.getDirection();
        Sprite sprite = arrow.getSprite();

        // Position anchors at bottom left of sprite
        switch (direction) {
            case LEFT:
                sprite.setPosition((WORLD_WIDTH - ARROW_SPAWN_POSITION_OFFSET) * 0.5f - sprite.getWidth() * 2 - ARROW_SPAWN_POSITION_OFFSET, WORLD_HEIGHT);
                break;
            case UP:
                sprite.setPosition((WORLD_WIDTH - ARROW_SPAWN_POSITION_OFFSET) * 0.5f - sprite.getWidth(), WORLD_HEIGHT);
                break;
            case DOWN:
                sprite.setPosition((WORLD_WIDTH + ARROW_SPAWN_POSITION_OFFSET) * 0.5f, WORLD_HEIGHT);
                break;
            case RIGHT:
                sprite.setPosition((WORLD_WIDTH + ARROW_SPAWN_POSITION_OFFSET) * 0.5f + sprite.getWidth() + ARROW_SPAWN_POSITION_OFFSET, WORLD_HEIGHT);
                break;
        }
    }

    @Override
    protected void setBPM(int bpm) {
        conductor = new Conductor(bpm, 0);
        conductor.lastBeat = music.getPosition() + conductor.crochet;
        conductor.start();
        music.setBPM(bpm);
    }

    @Override
    protected void controlsAction() {
        pauseGameOnEscape();

        // Look for any arrows that have reached hitZone and remember them
        Array<Arrow> arrowsInHitZone = new Array<>();
        for (Arrow a : allArrows) {
            if (isArrowInHitZone(a)) arrowsInHitZone.add(a);
        }

        // Check for every arrow that is in hitZone if corresponding key has been pressed
        for (Arrow arrow : arrowsInHitZone) {
            if (isArrowInHitZone(arrow) && isInputEqualsDirection(arrow.getDirection())) {
                score++;
                allArrows.removeValue(arrow, true);
            } else if (isAnyDirectionKeyPressed()) {
                if (isPenaltyOn) reduceScore();
                if (areLivesActive) reduceLives();
            }
        }
    }

    @Override
    protected boolean canArrowSpawn() {
        // Calculate distance from arrow to center of hitZone and time needed to reach it
        // Coordinates anchor at bottom left
        float hitZoneCenterPos = hitZone.getY() + (hitZone.getHeight() - arrowTexture.getHeight()) * 0.5f;
        float distance = WORLD_HEIGHT - hitZoneCenterPos;
        float travelTime = (distance / SPEED) * Gdx.graphics.getDeltaTime();

        // Calculate time when next beat will happen
        // Should happen _travelTime_ seconds earlier, so that arrow will reach HIT_POSITION when beat happens
        conductor.nextBeatTime = conductor.lastBeat + conductor.crochet - travelTime;

        // Spawn arrow when nextBeatTime is reached
        if (music.getPosition() >= conductor.nextBeatTime && music.getPosition() <= music.getBeatEnd()) {
            conductor.lastBeat = music.getPosition();
            return true;
        }
        return false;
    }

    // Private function & utility methods

    private boolean isArrowInHitZone(Arrow arrow) {
        int offset = 2;
        return hitZone.getY() - offset <= arrow.getSprite().getY() &&
                arrow.getSprite().getY() <= hitZone.getY() + offset + hitZone.getHeight() - arrowTexture.getHeight();
    }

    private void moveArrowsDown(float delta) {
        for (Arrow a : allArrows) {
            a.getSprite().translateY(-SPEED * delta);
        }
    }

    private void deleteArrowsOutOfWorld() {
        for (Iterator<Arrow> it = allArrows.iterator(); it.hasNext(); ) {
            Sprite s = it.next().getSprite();
            if (s.getY() < -s.getHeight()) {
                it.remove();
                if (isPenaltyOn) reduceScore();
            }
        }
    }

    private void draw() {
        // Start spriteBatch and draw hitZone
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        background.draw(game.batch, 1.0f);
        hitZone.draw(game.batch);
        game.batch.end();

        hud.draw();

        drawArrows();
    }
}
