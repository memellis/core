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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.spin.SpinWheel;
import com.ellzone.slotpuzzle2d.spin.SpinWheelForSlotPuzzle;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class SpinWheelUsingSpinWheelForSlotPuzzle extends SPPrototype {
    private static final float WHEEL_DIAMETER = 750F;
    private static final int NUMBER_OF_PEGS = 12;

    private static final String TAG = SpinWheelUsingSpinWheelForSlotPuzzle.class.getSimpleName();
    private World world;
    private SpinWheelForSlotPuzzle spinWheel;
    private Stage stage;
    private Image wheelImage;
    private Image needleImage;
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

        final TextureAtlas atlas = new TextureAtlas("spin/spin_wheel_ui.atlas");

        setUpSpinWheelBody(atlas);

        setUpSpinWheelSpinButton(atlas);

        setUpSpinWheelNeedleBody(atlas);

        setElementData();
    }

    private void setUpSpinWheelNeedleBody(TextureAtlas atlas) {
        spinWheel.getNeedleBody().setUserData(needleImage = new Image(new Sprite(atlas.findRegion("needle"))));
        updateCoordinates(spinWheel.getNeedleBody(), needleImage, 0, -25F);
        needleImage.setOrigin(spinWheel.getNeedleCenterX(needleImage.getWidth()), spinWheel.getNeedleCenterY(needleImage.getHeight()));
        stage.addActor(needleImage);
    }

    private void setUpSpinWheelSpinButton(TextureAtlas atlas) {
        final Image btnSpin = new Image(atlas.findRegion("spin_button"));
        btnSpin.setOrigin(Align.center);
        btnSpin.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Align.center);
        stage.addActor(btnSpin);

        btnSpin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btnSpin.addAction(sequence(scaleTo(1.25F, 1.25F, 0.10F), scaleTo(1F, 1F, 0.10F)));
                spinWheel.spin(MathUtils.random(5F, 30F));

                Gdx.app.debug(TAG, "Spinning.");
            }
        });
    }

    private void setUpSpinWheelBody(TextureAtlas atlas) {
        spinWheel.getWheelBody().setUserData(wheelImage = new Image(atlas.findRegion("spin_wheel")));
        updateCoordinates(spinWheel.getWheelBody(), wheelImage, 0, 0);
        wheelImage.setOrigin(Align.center);
        stage.addActor(wheelImage);
    }

    private void updateCoordinates(Body body, Image image, float incX, float incY) {
        image.setPosition((body.getPosition().x * SpinWheel.PPM) + incX, (body.getPosition().y * SpinWheel.PPM) + incY, Align.center);
        image.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
    }

    private void setElementData() {
        spinWheel.addElementData(Color.valueOf("e966ac"), getData(1, 2));
        spinWheel.addElementData(Color.valueOf("b868ad"), getData(2, 3));
        spinWheel.addElementData(Color.valueOf("8869ad"), getData(3, 4));
        spinWheel.addElementData(Color.valueOf("3276b5"), getData(4, 5));
        spinWheel.addElementData(Color.valueOf("33a7d8"), getData(5, 6));
        spinWheel.addElementData(Color.valueOf("33b8a5"), getData(6, 7));
        spinWheel.addElementData(Color.valueOf("a3fd39"), getData(7, 8));
        spinWheel.addElementData(Color.valueOf("fff533"), getData(8, 9));
        spinWheel.addElementData(Color.valueOf("fece3e"), getData(9, 10));
        spinWheel.addElementData(Color.valueOf("f9a54b"), getData(10, 11));
        spinWheel.addElementData(Color.valueOf("f04950"), getData(12, 1));
    }

    private IntArray getData(int peg_1, int peg_2) {
        IntArray array = new IntArray(2);
        array.addAll(peg_1, peg_2);
        return array;
    }

    public void render() {
        final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update(delta);

        if (!spinWheel.spinningStopped()) {
            updateCoordinates(spinWheel.getWheelBody(), wheelImage, 0, 0);

            updateCoordinates(spinWheel.getNeedleBody(), needleImage, 0, -25F);
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