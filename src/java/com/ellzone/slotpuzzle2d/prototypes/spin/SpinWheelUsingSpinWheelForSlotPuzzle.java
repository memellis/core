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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.spin.SpinWheelForSlotPuzzle;

public class SpinWheelUsingSpinWheelForSlotPuzzle extends SPPrototype {
    private static final float WHEEL_DIAMETER = 750F;
    private static final int NUMBER_OF_PEGS = 12;

    private static final String TAG = SpinWheelUsingSpinWheelForSlotPuzzle.class.getSimpleName();
    private World world;
    private SpinWheelForSlotPuzzle spinWheel;
    private Stage stage;
    private boolean box2dDebugRender = false;
    private Box2DDebugRenderer renderer;

    @Override
    public void create() {
        stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Gdx.input.setInputProcessor(stage);

        world = new World(new Vector2(0, -10), true);

        setUpSpinWheel();
    }

    private void setUpSpinWheel() {
        spinWheel = new SpinWheelForSlotPuzzle(
                WHEEL_DIAMETER,
                Gdx.graphics.getWidth() / 2,
                Gdx.graphics.getHeight() / 2,
                NUMBER_OF_PEGS,
                world);
        spinWheel.setUpSpinWheel(stage);
    }

    public void render() {
        final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update(delta);

        if (!spinWheel.spinningStopped()) {
            spinWheel.updateCoordinates(spinWheel.getWheelBody(), spinWheel.getWheelImage(), 0, 0);

            spinWheel.updateCoordinates(spinWheel.getNeedleBody(), spinWheel.getNeedleImage(), 0, -25F);
        } else {
            System.out.println("lucky element is: " + spinWheel.getLuckyWinElement());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ENTER))
            spinWheel.spin(0.2F);

        stage.act(delta);
        stage.draw();
    }

    private void update(float delta) {
        world.step(1 / 60f, 8, 2);
        if (box2dDebugRender)
            renderer.render(world, stage.getCamera().combined);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}