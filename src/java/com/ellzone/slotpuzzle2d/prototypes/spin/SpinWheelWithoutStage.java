/*
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ellzone.slotpuzzle2d.prototypes.spin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.spin.SpinWheel;
import com.ellzone.slotpuzzle2d.spin.SpinWheelForSlotPuzzle;

public class SpinWheelWithoutStage extends SPPrototype {
    public static final float WHEEL_DIAMETER = 500F;
    public static final int NUMBER_OF_PEGS = 12;
    private static final float GRAVITY = -9.80f;

    private static final String TAG = SpinWheelWithoutStage.class.getSimpleName();
    protected World world;
    protected SpinWheelForSlotPuzzle spinWheel;
    private SpriteBatch batch;
    private boolean box2dDebugRender = true;
    private Box2DDebugRenderer renderer;
    private OrthographicCamera camera;
    private DelayedRemovalArray<EventListener> listeners;

    public class SpinWheelInputProcessor implements InputProcessor {

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
            if (spinWheel.isInsideSpinButton(new Vector2(unProjectTouch.x, unProjectTouch.y)))
                spinWheel.spin(MathUtils.random(5F, 30F));
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
        batch = new SpriteBatch();
        world = new World(new Vector2(0, GRAVITY), true);
        renderer = new Box2DDebugRenderer();
        setUpCamera();
        Gdx.input.setInputProcessor(new SpinWheelInputProcessor());
        setUpSpinWheel();
    }

    public boolean isOver(Image image, float x, float y) {
        return image.getX() <= x && x <= image.getX() + image.getWidth() &&
               image.getY() <= y && y <= image.getY() + image.getHeight();
    }

    private void setUpCamera() {
        camera = new OrthographicCamera(
                Gdx.graphics.getWidth() / SpinWheel.PPM,
                Gdx.graphics.getHeight() / SpinWheel.PPM);
        camera.setToOrtho(false,
                Gdx.graphics.getWidth() / SpinWheel.PPM,
                Gdx.graphics.getHeight() / SpinWheel.PPM);
    }

    protected void setUpSpinWheel() {
        spinWheel = new SpinWheelForSlotPuzzle(
                WHEEL_DIAMETER,
                Gdx.graphics.getWidth() / 2,
                Gdx.graphics.getHeight() / 2,
                NUMBER_OF_PEGS,
                world);
        spinWheel.setUpSpinWheel();
    }

    public void render() {
        final float delta = Math.min(1 / 30f, Gdx.graphics.getDeltaTime());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update(delta);

        if (!spinWheel.spinningStopped()) {
            spinWheel.updateCoordinates(
                    spinWheel.getWheelBody(), spinWheel.getWheelImage(), 0, 0);
            spinWheel.updateCoordinates(
                    spinWheel.getNeedleBody(), spinWheel.getNeedleImage(), 0, -25F);
        } else
            System.out.println("lucky element is: " + spinWheel.getLuckyWinElement());

        if (Gdx.input.isKeyPressed(Input.Keys.ENTER))
            spinWheel.spin(0.2F);

        if (Gdx.input.isKeyPressed(Input.Keys.S))
            spinWheel.spin(MathUtils.random(5F, 30F));

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        spinWheel.getWheelImage().draw(batch, 1.0f);
        spinWheel.getNeedleImage().draw(batch, 1.0f);
        batch.end();
        if (box2dDebugRender)
            box2dDebugRender();
    }

    private void box2dDebugRender() {
        renderer.render(world, camera.combined);
    }

    private void update(float delta) {
        world.step(1 / 60f, 8, 2);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
        renderer.dispose();
    }
}
