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

package com.ellzone.slotpuzzle2d.prototypes.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.math.Vector3;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.utils.assets.AssetsAnnotation;

public class UnProjectCamera extends SPPrototype {

    public static final String COIN_REGION = "coin";
    private SpriteBatch spriteBatch;
    protected OrthographicCamera camera;
    private Animation coinAnimation;
    private float stateTime;
    private TextureRegion currentFrame;


    public class UnProjectInputProcessor implements InputProcessor {

        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Vector3 unProjectTouch = new Vector3(screenX, screenY, 0);
            camera.unproject(unProjectTouch);
            System.out.println("Touched: screenXY(" + screenX+","+screenY+")");
            System.out.println("Touched: unProjectXY(" + unProjectTouch.x+","+unProjectTouch.y+")");
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            return false;
        }
    }

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        setUpCamera();
        Gdx.input.setInputProcessor(new UnProjectInputProcessor());
        setupThingToBeDisplayed();

    }

    private void setUpCamera() {
        camera = new OrthographicCamera(
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());
        camera.setToOrtho(
                false,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());
    }

    private void setupThingToBeDisplayed() {
        TextureAtlas coinAtlas =
                new TextureAtlas(Gdx.files.internal(AssetsAnnotation.COIN_ANIMATION));
        coinAnimation = new Animation<TextureRegion>(
                0.08333f, coinAtlas.findRegions(COIN_REGION));

    }

    public void render() {
        clearScreen();
        update();
        renderCurrentFrame();
    }

    private void clearScreen() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void update() {
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = (TextureRegion) coinAnimation.getKeyFrame(stateTime, true);
    }

    private void renderCurrentFrame() {
        spriteBatch.begin();
        spriteBatch.draw(currentFrame, 50, 50);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }
}
