package com.mygdx.game.ocarinagame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.Conductor;
import com.mygdx.game.MainMenuScreen;
import com.mygdx.game.Start;

import java.util.Comparator;

public final class OcarinaGameAppearing extends AbstractOcarinaGame {
    private static final boolean IS_ARROW_POSITION_OFFSET_ACTIVE = false;
    private static final float ARROW_UPTIME = 0.5f;
    private static final float SPAWN_TIME_OFFSET = 0.35f;

    private final Array<Arrow> upArrows;
    private final Array<Arrow> leftArrows;
    private final Array<Arrow> downArrows;
    private final Array<Arrow> rightArrows;

    // Background for better visibility of arrows
    private final Texture blackBoxTexture;
    private final Image blackBox;

    // Constructor

    public OcarinaGameAppearing(Start game) {
        // Setup basic game elements like
        // camera, viewpoint, textures, allArrows
        super(game);

        // Change game state values
        totalBeatCount = 10;
        beatStart = 13.117f;

        // Setup UI
        hud = new HUD(game.batch, this);
        ScoreProgressBar progressBar = hud.getProgressBar();
        // Position at top right corner
        progressBar.setPosition(WORLD_WIDTH - progressBar.getWidth() - 3, WORLD_HEIGHT - progressBar.getHeight() - 3);

        // Setup audio
        song = Gdx.audio.newMusic(Gdx.files.internal("Music\\Wake_Up_110bpm.mp3"));
        song.setLooping(false);
        conductor = new Conductor(110, 0);
        conductor.start();
        // First arrow spawns earlier than beat
        // Calculation for when next beat based on last beat
        // Offset therefore included in every beat
        // Offset necessary because of reaction time of player
        conductor.lastBeat = SPAWN_TIME_OFFSET + beatStart;

        // Initialize arrays
        upArrows = new Array<>();
        leftArrows = new Array<>();
        downArrows = new Array<>();
        rightArrows = new Array<>();

        // Setup background
        backgroundTexture = new Texture("ocarina-game\\wake-up-background.png");
        background = new Image(backgroundTexture);

        // Setup blackbox
        blackBoxTexture = new Texture("ocarina-game\\black-box.png");
        blackBox = new Image(blackBoxTexture);
            // Center blackBox
        blackBox.setPosition(0.5f * (WORLD_WIDTH - blackBox.getWidth()), 0.5f * (WORLD_HEIGHT - blackBox.getHeight()));
    }

    // Overrides

    @Override
    public void render(float delta) {
        // Clear screen, update HUD & camera
        super.render(delta);

        if (isRunning) {
            controls();

            draw();

            if (canArrowSpawn()) spawnArrow();

            // Remove arrow after certain amount of time
            removeAfterUptime();

            // If game is won or song has ended, clear memory usage for textures and switch to next screen
            if (isGameWon() || song.getPosition() > 58) {
                dispose();
                switchToScreen(new MainMenuScreen(game));
            }
        }
    }

    @Override
    protected void setupArrowSpawnPosition(Arrow arrow) {
        Arrow.Direction direction = arrow.getDirection();
        Sprite sprite = arrow.getSprite();

        int offsetFactor = IS_ARROW_POSITION_OFFSET_ACTIVE ? 1 : 0;

        // Coordinates anchor at bottem left
        float centerArrowX = 0.5f * (WORLD_WIDTH - arrowTexture.getWidth());
        float centerArrowY = 0.5f * (WORLD_HEIGHT- arrowTexture.getHeight());

        // Setup spawnpoint depending on arrow direction and duplicates (if offset is active)
        switch (direction) {
            case UP:
                float multUpOffset = arrowTexture.getHeight() * upArrows.size;
                sprite.setPosition(centerArrowX, centerArrowY + arrowTexture.getHeight() + ARROW_SPAWN_POSITION_OFFSET + multUpOffset * offsetFactor);
                upArrows.add(arrow);
                break;
            case DOWN:
                float multDownOffset = arrowTexture.getHeight() * downArrows.size;
                sprite.setPosition(centerArrowX, centerArrowY - arrowTexture.getHeight() - ARROW_SPAWN_POSITION_OFFSET - multDownOffset * offsetFactor);
                downArrows.add(arrow);
                break;
            case LEFT:
                float multLeftOffset = arrowTexture.getWidth() * leftArrows.size;
                sprite.setPosition(centerArrowX - arrowTexture.getWidth() - ARROW_SPAWN_POSITION_OFFSET - multLeftOffset * offsetFactor, centerArrowY);
                leftArrows.add(arrow);
                break;
            case RIGHT:
                float multRightOffset = arrowTexture.getWidth() * rightArrows.size;
                sprite.setPosition(centerArrowX + arrowTexture.getWidth() + ARROW_SPAWN_POSITION_OFFSET + multRightOffset * offsetFactor, centerArrowY);
                rightArrows.add(arrow);
                break;
        }
    }

    @Override
    protected void setBPM(int bpm) {
        conductor = new Conductor(bpm, 0);
        conductor.lastBeat = SPAWN_TIME_OFFSET + song.getPosition();
        conductor.start();
    }

    @Override
    protected void controls() {
        // Check for every arrow if correct key has been pressed
        for (Arrow a : allArrows) {
            Arrow.Direction direction = a.getDirection();

            if (isInputEqualsDirection(direction)) {
                score++;
                removeArrowOfDirection(a.getDirection());
            } else if (isMissPressPenaltyTriggered()) {
                reduceScore();
            }
        }

        pauseGameOnEscape();
    }

    @Override
    protected boolean canArrowSpawn()  {
        // Calculate time when next beat will happen
        conductor.nextBeatTime = conductor.lastBeat + conductor.crochet;

        // Spawn arrow when nextBeatTime is reached
        if (song.getPosition() >= conductor.nextBeatTime) {
            conductor.lastBeat = song.getPosition();
            return true;
        }
        return false;
    }

    // Private function & utility methods

    private void draw() {
        // Setup spriteBatch and draw sprites/textures
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        background.draw(game.batch, 1.0f);
        if (song.getPosition() >= beatStart) blackBox.draw(game.batch, 0.6f);
        game.batch.end();

        hud.draw();

        drawArrows();
    }

    private void removeAfterUptime() {
        for (Arrow currArrow : allArrows) {
            if (song.getPosition() - currArrow.getSpawnTime() > ARROW_UPTIME) {
                removeArrowOfDirection(currArrow.getDirection());
                if (isPenaltyOn) reduceScore();
            }
        }
    }

    private void removeArrowOfDirection(Arrow.Direction direction) {
        // Remove the oldest arrow with specific direction and adjust position by filling the gap after removal
        // Sort array to easily remove arrow with smallest spawntime (earliest spawn)
        Arrow toRemove = null;
        switch (direction) {
            case UP:
                upArrows.sort(Comparator.comparingDouble(Arrow::getSpawnTime));
                if (IS_ARROW_POSITION_OFFSET_ACTIVE) {
                    for (int i = 1; i < upArrows.size; i++) {
                        transferPositionFromTo(upArrows.get(i-1), upArrows.get(i));
                    }
                }
                toRemove = upArrows.removeIndex(0);
                break;
            case LEFT:
                leftArrows.sort(Comparator.comparingDouble(Arrow::getSpawnTime));
                if (IS_ARROW_POSITION_OFFSET_ACTIVE) {
                    for (int i = 1; i < leftArrows.size; i++) {
                        transferPositionFromTo(leftArrows.get(i - 1), leftArrows.get(i));
                    }
                }
                toRemove = leftArrows.removeIndex(0);
                break;
            case DOWN:
                downArrows.sort(Comparator.comparingDouble(Arrow::getSpawnTime));
                if (IS_ARROW_POSITION_OFFSET_ACTIVE) {
                    for (int i = 1; i < downArrows.size; i++) {
                        transferPositionFromTo(downArrows.get(i - 1), downArrows.get(i));
                    }
                }
                toRemove = downArrows.removeIndex(0);
                break;
            case RIGHT:
                rightArrows.sort(Comparator.comparingDouble(Arrow::getSpawnTime));
                if (IS_ARROW_POSITION_OFFSET_ACTIVE) {
                    for (int i = 1; i < rightArrows.size; i++) {
                        transferPositionFromTo(rightArrows.get(i - 1), rightArrows.get(i));
                    }
                }
                toRemove = rightArrows.removeIndex(0);
                break;
        }
        allArrows.removeValue(toRemove, true);
    }

    private void transferPositionFromTo(Arrow arrow1, Arrow arrow2) {
        Sprite arr1 = arrow1.getSprite();
        arrow2.getSprite().setPosition(arr1.getX(), arr1.getY());
    }
}
