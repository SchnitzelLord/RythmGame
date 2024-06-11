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

public class OcarinaGameFalling extends AbstractOcarinaGame {

    // Game state constants
    private static final float SPEED = 75;

    // Arrow hit and spawn position
    private static final float HIT_POSITION = 2;
    private final float ARROW_SPAWN_POSITION_Y = WORLD_HEIGHT * 2;

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

        // Setup audio
        song = Gdx.audio.newMusic(Gdx.files.internal("Music\\Wake_Up_110bpm.mp3"));
        conductor = new Conductor(110, 0);
        conductor.start();
        // Set first beat time stamp
        conductor.lastBeat = conductor.crochet;

        // Setup hitZone
        hitZoneTexture = new Texture("ocarina-game\\hit-zone.png");
        hitZone = new Sprite(hitZoneTexture, hitZoneTexture.getWidth(), hitZoneTexture.getHeight());
        hitZone.setPosition(0.5f * (WORLD_WIDTH - hitZone.getWidth()), HIT_POSITION);

        // Setup background
        backgroundTexture = new Texture("ocarina-game\\zelda-background.png");
        background = new Image(backgroundTexture);
    }

    // Overrides

    // Already updates HUD and camera, checks for controls, draws arrows and background, spawn arrows
    @Override
    public void render(float delta) {
        // Clear screen, update HUD & camera
        super.render(delta);

        if (isRunning) {

            // Check input and act upon them
            controls();

            draw();

            if (canArrowSpawn()) spawnArrow();
            moveArrowsDown(delta);
            deleteArrowsReachingHitPosition();

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
        int offset = 4;

        switch (direction) {
            case LEFT:
                sprite.setPosition((WORLD_WIDTH - offset) * 0.5f - sprite.getWidth() * 2 - offset, ARROW_SPAWN_POSITION_Y);
                break;
            case UP:
                sprite.setPosition((WORLD_WIDTH - offset) * 0.5f - sprite.getWidth(), ARROW_SPAWN_POSITION_Y);
                break;
            case DOWN:
                sprite.setPosition((WORLD_WIDTH + offset) * 0.5f, ARROW_SPAWN_POSITION_Y);
                break;
            case RIGHT:
                sprite.setPosition((WORLD_WIDTH + offset) * 0.5f + sprite.getWidth() + offset, ARROW_SPAWN_POSITION_Y);
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
        float distance = ARROW_SPAWN_POSITION_Y - HIT_POSITION;
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
        return HIT_POSITION < arrow.getSprite().getY() && arrow.getSprite().getY() < HIT_POSITION + hitZone.getHeight() - arrowTexture.getHeight();
    }

    private void moveArrowsDown(float delta) {
        for (Arrow a : allArrows) {
            a.getSprite().translateY(-SPEED * delta);
        }
    }

    private void deleteArrowsReachingHitPosition() {
        for (Iterator<Arrow> it = allArrows.iterator(); it.hasNext(); ) {
            Sprite s = it.next().getSprite();
            if (s.getY() < HIT_POSITION) {
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
