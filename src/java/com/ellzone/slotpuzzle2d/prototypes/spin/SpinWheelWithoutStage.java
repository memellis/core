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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.spin.SpinWheel;
import com.ellzone.slotpuzzle2d.spin.SpinWheelForSlotPuzzle;

public class SpinWheelWithoutStage extends SPPrototype {
    private static final float WHEEL_DIAMETER = 500F;
    private static final int NUMBER_OF_PEGS = 12;
    private static final float GRAVITY = -9.80f;

    private static final String TAG = SpinWheelWithoutStage.class.getSimpleName();
    private World world;
    private SpinWheelForSlotPuzzle spinWheel;
    private SpriteBatch batch;
    private Image wheelImage;
    private Image needleImage;
    private boolean box2dDebugRender = true;
    private Box2DDebugRenderer renderer;
    private OrthographicCamera camera;

    @Override
    public void create() {
        batch = new SpriteBatch();
        world = new World(new Vector2(0, GRAVITY), true);
        renderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(
                Gdx.graphics.getWidth() / SpinWheel.PPM,
                Gdx.graphics.getHeight() / SpinWheel.PPM);
        camera.setToOrtho(false,
                Gdx.graphics.getWidth() / SpinWheel.PPM,
                Gdx.graphics.getHeight() / SpinWheel.PPM);
        setUpSpinWheel();
    }

    private void setUpSpinWheel() {
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
