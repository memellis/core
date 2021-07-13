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
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;

public class AnimatedBomb extends SPPrototype {
    private static final float WIDTH = 800;
    private static final float HEIGHT = 600;
    private Stage stage;
    private TextureAtlas bombAtlas;
    private Animation<TextureRegion> bombAnimation;
    private SpriteBatch spriteBatch;
    private float stateTime;
    private TextureRegion currentFrame;

    @Override
    public void create() {
        bombAtlas = new TextureAtlas(Gdx.files.internal("bomb/bomb_animation.pack.atlas"));
        bombAnimation = new Animation<TextureRegion>(0.08f, bombAtlas.findRegions("bomb"));

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
        currentFrame = bombAnimation.getKeyFrame(stateTime, true);
    }

    private void clearScreen() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }
}
