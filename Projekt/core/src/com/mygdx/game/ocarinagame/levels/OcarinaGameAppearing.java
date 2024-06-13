package com.mygdx.game.ocarinagame.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Conductor;
import com.mygdx.game.MainMenuScreen;
import com.mygdx.game.Start;
import com.mygdx.game.ocarinagame.Arrow;
import com.mygdx.game.ocarinagame.BeatMusic;
import com.mygdx.game.ocarinagame.ui.HUD;
import com.mygdx.game.ocarinagame.ui.ScoreProgressBar;

import java.util.Comparator;

public final class OcarinaGameAppearing extends AbstractOcarinaGame {
    // Constant game states
    private static final boolean IS_ARROW_POSITION_OFFSET_ACTIVE = false;
    private static final float ARROW_UPTIME = 0.75f;
    private static final float SPAWN_TIME_OFFSET = 0.35f;

    // Storage for every arrow direction
    private final Array<Arrow> upArrows;
    private final Array<Arrow> leftArrows;
    private final Array<Arrow> downArrows;
    private final Array<Arrow> rightArrows;

    // Background for better visibility of arrows
    private final Texture whiteBoxTexture;
    private final Image whiteBox;

    // Constructor

    public OcarinaGameAppearing(Start game) {
        // Setup common game elements like
        // camera, viewpoint, textures, allArrows
        super(game);

        // Setup audio
        Music song = Gdx.audio.newMusic(Gdx.files.internal("Music\\wake_up_110bpm.mp3"));
        music = new BeatMusic(song, 110, 13.177f, 56.338f, 58.984f);
        song.dispose();
        conductor = new Conductor(music.getBPM(), 0);
        conductor.start();
        // First arrow spawns earlier than beat
        // Calculation for when next beat based on last beat
        // Offset therefore included in every beat
        // Offset necessary because of reaction time of player
        conductor.lastBeat = SPAWN_TIME_OFFSET + music.getBeatStart();

        // Setup UI
        hud = new HUD(game.batch, this);
        ScoreProgressBar progressBar = hud.getProgressBar();
        // Position at top right corner
        progressBar.setPosition(WORLD_WIDTH - progressBar.getWidth() - 3, WORLD_HEIGHT - progressBar.getHeight() - 3);
        progressBar.setRange(0, music.getTotalBeatCount() * WIN_RATE);

        // Initialize arrays
        upArrows = new Array<>();
        leftArrows = new Array<>();
        downArrows = new Array<>();
        rightArrows = new Array<>();

        // Setup background
        backgroundTexture = new Texture("ocarina-game\\wake-up-background.png");
        background = new Image(backgroundTexture);

        // Setup blackbox
        whiteBoxTexture = new Texture("ocarina-game\\white-box.png");
        whiteBox = new Image(whiteBoxTexture);
            // Center blackBox
        whiteBox.setPosition(0.5f * (WORLD_WIDTH - whiteBox.getWidth()), 0.5f * (WORLD_HEIGHT - whiteBox.getHeight()));
    }

    // Overrides


    @Override
    public void show() {
        super.show();
        music.play();
    }

    @Override
    public void render(float delta) {
        // Clear screen, update HUD & camera
        super.render(delta);

        if (isRunning) {
            controlsAction();

            draw();

            if (canArrowSpawn() && music.getPosition() < music.getBeatEnd()) spawnArrow();

            // Remove arrow after certain amount of time
            removeAfterUptime();

            gameOverAction();
        }
    }

    @Override
    protected void setupArrowSpawnPosition(Arrow arrow) {
        Arrow.Direction direction = arrow.getDirection();
        Sprite sprite = arrow.getSprite();

        int offsetFactor = IS_ARROW_POSITION_OFFSET_ACTIVE ? 1 : 0;

        // Coordinates anchor at bottom left
        float centerArrowX = 0.5f * (WORLD_WIDTH - arrowTexture.getWidth());
        float centerArrowY = 0.5f * (WORLD_HEIGHT- arrowTexture.getHeight());

        // Setup spawnpoint depending on arrow direction (and duplicates if offset is active)
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
        conductor.lastBeat = SPAWN_TIME_OFFSET + music.getPosition();
        conductor.start();
        music.setBPM(bpm);
    }

    @Override
    protected void controlsAction() {
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
        if (music.getPosition() >= conductor.nextBeatTime) {
            conductor.lastBeat = music.getPosition();
            return true;
        }
        return false;
    }

    @Override
    protected void gameOverAction() {
        // If beat part of song has finished playing + 1s delay and
        // if game is won and song has ended, clear memory usage for textures and switch to next screen
        // (Use that song is not immediately stopping after beat part has ended)
        if (music.getPosition() >= music.getBeatEnd() + 1) {
            if (hasWinRateBeenReached()) {
                dispose();
                switchToScreen(new MainMenuScreen(game));
            }
        }
    }

    // Private function & utility methods

    private void draw() {
        // Setup spriteBatch and draw sprites/textures
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        background.draw(game.batch, 1.0f);
        if (music.getPosition() >= music.getBeatStart()) whiteBox.draw(game.batch, 0.5f);
        game.batch.end();

        hud.draw();

        drawArrows();
    }

    private void removeAfterUptime() {
        for (Arrow currArrow : allArrows) {
            if (music.getPosition() - currArrow.getSpawnTime() > ARROW_UPTIME) {
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
