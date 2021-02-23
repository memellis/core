/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.prototypes.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;

public class AnimateBombViaSpriteSheet extends SPPrototype {
    // Constant rows and columns of the sprite sheet
    private static final int FRAME_COLS = 7, FRAME_ROWS = 2;

    // Objects used
    Array<Animation<TextureRegion>> bombAnimations;
    Texture bombSheet;
    TextureAtlas atlas;
    SpriteBatch spriteBatch;

    // A variable for tracking elapsed time for the animation
    float stateTime;

    @Override
    public void create() {

        // Load the sprite sheet as a Texture
        bombSheet = new Texture(Gdx.files.internal("bomb/bomb40x40.png"));

        atlas = new TextureAtlas(Gdx.files.internal("bomb/bomb_animation.pack.atlas"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(bombSheet,
                bombSheet.getWidth() / FRAME_COLS,
                bombSheet.getHeight() / FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }

        bombAnimations = new Array<Animation<TextureRegion>>();

        // Initialize the Animation with the frame interval and array of frames
        bombAnimations.add(new Animation<TextureRegion>(0.1f, walkFrames));
        bombAnimations.add(
                new Animation<TextureRegion>(
                        0.1f,
                        atlas.findRegions("bomb"),
                        Animation.PlayMode.LOOP));

        spriteBatch = new SpriteBatch();
        stateTime = 0f;
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stateTime += Gdx.graphics.getDeltaTime();

        int count = 0;
        spriteBatch.begin();
        for (Animation bombAnimation : bombAnimations)
            drawAnimationCurrentFrame(
                    spriteBatch,
                    (TextureRegion) bombAnimation.getKeyFrame(stateTime, true),
                    50 * count++,
                    (int) Gdx.graphics.getHeight() / 2);
        spriteBatch.end();
    }

    private void drawAnimationCurrentFrame(
            SpriteBatch spriteBatch, TextureRegion currentFrame, int x, int y) {
        spriteBatch.draw(currentFrame, x, y);
    }

    @Override
    public void dispose() { // SpriteBatches and Textures must always be disposed
        spriteBatch.dispose();
        bombSheet.dispose();
    }
}
