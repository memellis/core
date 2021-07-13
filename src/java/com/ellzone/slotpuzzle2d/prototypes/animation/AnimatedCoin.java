package com.ellzone.slotpuzzle2d.prototypes.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;

import static com.badlogic.gdx.graphics.g3d.particles.ParticleChannels.TextureRegion;

public class AnimatedCoin extends SPPrototype {

    public static final String COIN_PACK_ATLAS = "coin/coin40x40.pack.atlas";
    public static final String COIN_REGION = "coin";
    private TextureAtlas coinAtlas;
    private Animation coinAnimation;
    private SpriteBatch spriteBatch;
    private float stateTime;
    private TextureRegion currentFrame;

    @Override
    public void create() {
        coinAtlas = new TextureAtlas(Gdx.files.internal(COIN_PACK_ATLAS));
        coinAnimation = new Animation<TextureRegion>(
                0.08333f, coinAtlas.findRegions(COIN_REGION));
        spriteBatch = new SpriteBatch();
        stateTime = 0f;
    }

    public void render() {
        clearScreen();
        update();
        spriteBatch.begin();
        spriteBatch.draw(currentFrame, 50, 50);
        spriteBatch.end();
    }

    private void update() {
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = (TextureRegion) coinAnimation.getKeyFrame(stateTime, true);
    }

    private void clearScreen() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }

}
