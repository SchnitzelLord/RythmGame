package com.mygdx.game.ocarinagame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Conductor;
import com.mygdx.game.MainMenuScreen;
import com.mygdx.game.Start;

import java.util.Comparator;

public class OcarinaGameAppearing extends AbstractOcarinaGame {
    private static final float SPAWN_POSITION_OFFSET = 10;
    private static final boolean IS_ARROW_POSITION_OFFSET_ACTIVE = false;
    private static final float ARROW_UPTIME = 0.5f;
    private static final float SPAWN_TIME_OFFSET = 0.35f;

    private final Array<Arrow> upArrows;
    private final Array<Arrow> leftArrows;
    private final Array<Arrow> downArrows;
    private final Array<Arrow> rightArrows;

    public OcarinaGameAppearing(Start game) {
        // Setup basic game elements like
        // camera, viewpoint, textures, lastArrowDirection, allArrows
        super(game);

        // Setup UI
        hud = new HUD(game.batch, this);

        // Setup audio
        song = Gdx.audio.newMusic(Gdx.files.internal("Music\\testBeat.mp3"));
        conductor = new Conductor(120, 0);
        conductor.start();
        // First arrow spawns earlier than beat
        // Calculation for when next beat based on last beat
        // Offset therefore included in every beat
        // Offset necessary because of reaction time of player
        conductor.lastBeat = SPAWN_TIME_OFFSET;

        // Initialize arrays
        upArrows = new Array<>();
        leftArrows = new Array<>();
        downArrows = new Array<>();
        rightArrows = new Array<>();
    }

    // Overrides

    @Override
    public void render(float delta) {
        if (isRunning) {
            ScreenUtils.clear(Color.PURPLE);
            camera.update();
            controls();
            draw();
            // Spawn arrow at certain interval
            if (arrowCanSpawn()) spawnArrow();

            // Remove arrow after certain amount of time
            removeAfterUptime();

            if (checkWinCondition()) {
                dispose();
                switchToScreen(new MainMenuScreen(game));
            };

            hud.update();
        }
    }

    @Override
    protected void setupArrowSpawnPosition(Arrow arrow) {
        Arrow.Direction direction = arrow.getDirection();
        Sprite sprite = arrow.getSprite();
        int offsetFactor = IS_ARROW_POSITION_OFFSET_ACTIVE ? 1 : 0;

        // Setup spawnpoint depending on arrow direction and duplicates
        switch (direction) {
            case UP:
                float multUpOffset = arrowTexture.getHeight() * upArrows.size;
                sprite.setPosition(playerX, playerY + player.getHeight() + SPAWN_POSITION_OFFSET + multUpOffset* offsetFactor);
                upArrows.add(arrow);
                break;
            case DOWN:
                float multDownOffset = arrowTexture.getHeight() * downArrows.size;
                sprite.setPosition(playerX, playerY - player.getHeight() - SPAWN_POSITION_OFFSET - multDownOffset * offsetFactor);
                downArrows.add(arrow);
                break;
            case LEFT:
                float multLeftOffset = arrowTexture.getWidth() * leftArrows.size;
                sprite.setPosition(playerX - player.getWidth() - SPAWN_POSITION_OFFSET - multLeftOffset * offsetFactor, playerY);
                leftArrows.add(arrow);
                break;
            case RIGHT:
                float multRightOffset = arrowTexture.getWidth() * rightArrows.size;
                sprite.setPosition(playerX + player.getWidth() + SPAWN_POSITION_OFFSET + multRightOffset * offsetFactor, playerY);
                rightArrows.add(arrow);
                break;
        }
    }

    @Override
    protected void setBPM(int bpm) {
        conductor = new Conductor(bpm, 0);
        conductor.lastBeat = SPAWN_TIME_OFFSET + song.getPosition();
        conductor.start();
        arrowCanSpawn();
    }

    private void controls() {
        // Check for every arrow if correct key has been pressed
        for (Arrow a : allArrows) {
            Arrow.Direction direction = a.getDirection();

            if (isInputEqualsDirection(direction)) {
                score++;
                removeArrowOfDirection(a.getDirection());
            } else if (isMissPenaltyTriggered()) {
                reduceScore();
            }
        }

        pauseGameOnEscape();
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
