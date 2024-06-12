package com.mygdx.game.ocarinagame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Conductor;
import com.mygdx.game.MainMenuScreen;
import com.mygdx.game.Start;

import java.util.Iterator;

public final class OcarinaGameFalling extends AbstractOcarinaGame {

    // Game state constants
    private static final float SPEED = 75;

    // Arrow hit and spawn position
    private static final float HIT_POSITION = 1;

    // Hitzone
    private final Texture hitZoneTexture;
    private final Sprite hitZone;

    // Constructor

    public OcarinaGameFalling(Start game) {
        // Setup basic game elements like
        // camera, viewpoint, textures, allArrows
        super(game);

        // Setup UI
        hud = new HUD(game.batch, this);
        ScoreProgressBar progressBar = hud.getProgressBar();
            // Position at top right corner
        progressBar.setPosition(WORLD_WIDTH - progressBar.getWidth() - 3, WORLD_HEIGHT - progressBar.getHeight() - 3);

        // Setup audio
        song = Gdx.audio.newMusic(Gdx.files.internal("Music\\Wake_Up_110bpm.mp3"));
        conductor = new Conductor(110, 0);
        conductor.start();
        // Set first beat time stamp
        conductor.lastBeat = 13.117f - conductor.crochet;

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
            controls();

            draw();

            if (canArrowSpawn() && song.isPlaying()) spawnArrow();
            moveArrowsDown(delta);
            deleteArrowsOutOfWorld();

            // If game is won, clear memory usage for textures and switch to next screen
            if (isGameWon()) {
                dispose();
                switchToScreen(new MainMenuScreen(game));
            }
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
        conductor.lastBeat = song.getPosition() + conductor.crochet;
        conductor.start();
    }

    @Override
    protected void controls() {
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
            } else if (isMissPressPenaltyTriggered()) {
                reduceScore();
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
        if (song.getPosition() >= conductor.nextBeatTime) {
            conductor.lastBeat = song.getPosition();
            return true;
        }
        return false;
    }

    // Private function & utility methods

    private boolean isArrowInHitZone(Arrow arrow) {
        return hitZone.getY() <= arrow.getSprite().getY() && arrow.getSprite().getY() <= hitZone.getY() + hitZone.getHeight() - arrowTexture.getHeight();
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
