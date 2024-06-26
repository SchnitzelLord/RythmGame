package com.mygdx.game.ocarinagame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;


public final class ProgressBar extends com.badlogic.gdx.scenes.scene2d.ui.ProgressBar implements Disposable {
    private final Texture progressBarTexture;
    private final Texture progressBarEmptyTexture;
    private final Texture progressBarBorderTexture;
    private final Image progressBarBorder;

    private static final int TEXTURE_OFFSET = 16;

    // Constructor

    ProgressBar(float min, float max, float stepSize, float animateDuration) {
        super(min, max, stepSize, false, new ProgressBarStyle());

        // Setup textures for progress bar
        progressBarTexture = new Texture(Gdx.files.internal("ocarina-game/progress-bar-fhd.png"));
        progressBarEmptyTexture = new Texture(Gdx.files.internal("ocarina-game/progress-bar-empty-fhd.png"));
        progressBarBorderTexture = new Texture(Gdx.files.internal("ocarina-game/progress-bar-border-fhd.png"));

        // Prevent left side from stretching to retain roundness of border
        NinePatch progressBarNP = new NinePatch(progressBarTexture, TEXTURE_OFFSET, 0, 0, 0);

        // Set texture of progressbar for when it's filled and when it's empty
        getStyle().background = new TextureRegionDrawable(new TextureRegion(progressBarEmptyTexture));
        getStyle().knobBefore = new NinePatchDrawable(progressBarNP);

        // Set progress bar properties
        setValue(0);
        setSize(progressBarTexture.getWidth(), progressBarTexture.getHeight());

        // Smooth animation by setting time needed to change from one step to another
        setAnimateDuration(animateDuration);

        // Setup border of progress bar to be on top of actual progress bar
        progressBarBorder = new Image(progressBarBorderTexture);
        progressBarBorder.setPosition(getX(), getY());
        progressBarBorder.setZIndex(1);
    }

    // Getter

    public int getTextureOffset() {
        return TEXTURE_OFFSET;
    }

    // Overrides

    @Override
    public void dispose() {
        progressBarTexture.dispose();
        progressBarEmptyTexture.dispose();
        progressBarBorderTexture.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        progressBarBorder.draw(batch, parentAlpha);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        progressBarBorder.setPosition(x, y);
    }
}
