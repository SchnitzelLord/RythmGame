package com.mygdx.game.ocarinagame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;


class ScoreProgressBar extends ProgressBar implements Disposable {
    private final Skin skin;
    private final Texture progressBarTexture;
    private final Texture progressBarEmptyTexture;

    ScoreProgressBar(float min, float max, float stepSize, float animateDuration) {
        super(min, max, stepSize, false, new ProgressBarStyle());

        skin = new Skin();

        // Setup textures for progress bar
        progressBarTexture = new Texture(Gdx.files.internal("ocarina-game/progress-bar.png"));
        progressBarEmptyTexture = new Texture(Gdx.files.internal("ocarina-game/progress-bar-empty.png"));

        // Setup progress bar
        skin.add("progressBar", progressBarTexture);
        skin.add("progressBarEmpty", progressBarEmptyTexture);

        // Set texture of progressbar for when it's filled and when it's empty
        getStyle().background = skin.getDrawable("progressBarEmpty");
        getStyle().knobBefore = skin.getDrawable("progressBar");

        setValue(0);
        setSize(progressBarTexture.getWidth(), progressBarTexture.getHeight());

        // Smooth animation by setting time needed to change from one step to another
        setAnimateDuration(animateDuration);
    }

    @Override
    public void dispose() {
        skin.dispose();
        progressBarTexture.dispose();
        progressBarEmptyTexture.dispose();
    }
}
