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

package com.ellzone.slotpuzzle2d.systems.artemisodb;

import com.artemis.Entity;
import com.artemis.annotations.All;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.component.artemis.SpinWheel;
import com.ellzone.slotpuzzle2d.spin.SpinWheelSlotPuzzleTileMap;

@All({SpinWheel.class})
public class SpinWheelSystem extends EntityProcessingSystem  {
    private LevelCreatorSystem levelCreatorSystem;
    private OrthographicCamera camera;
    private Vector3 unProjectTouch;
    private boolean touched = false;

    public SpinWheelSystem() {
        setup();
    }

    private void setup() {
        setupCamera();
    }

    private void setupCamera() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(
                false,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());
    }

    public void end() {
        touched = false;
    }

    public void touched(Vector3 unProjectTouch) {
        this.unProjectTouch = unProjectTouch;
        camera.unproject(unProjectTouch);
        touched = true;
    }

    @Override
    protected void process(Entity e) {
        SpinWheelSlotPuzzleTileMap spinWheel =
                (SpinWheelSlotPuzzleTileMap) levelCreatorSystem.getEntities().get(e.getId());
        updateSpinWheel(spinWheel);
        if (touched)
            processTouched(spinWheel);
    }

    private void processTouched(SpinWheelSlotPuzzleTileMap spinWheel) {
        if (spinWheel.isInsideSpinButton(
                new Vector2(
                        unProjectTouch.x / SlotPuzzleConstants.PIXELS_PER_METER,
                        unProjectTouch.y / SlotPuzzleConstants.PIXELS_PER_METER)))
            spinWheel.spin(MathUtils.random(5F, 30F));
        touched = false;
    }

    private void updateSpinWheel(SpinWheelSlotPuzzleTileMap spinWheel) {
        if(isSpinPressed())
            spinWheel.spin(MathUtils.random(5F, 30F));
        if (!spinWheel.spinningStopped()) {
            updateCoordinates(spinWheel.getWheelBody(), spinWheel.getWheelImage(), 0);
            updateCoordinates(spinWheel.getNeedleBody(), spinWheel.getNeedleImage(), -25F);
        }
    }

    private void updateCoordinates(Body body, Image image, float incY) {
        image.setPosition(
                (body.getPosition().x +
                        ((float) 0 / com.ellzone.slotpuzzle2d.spin.SpinWheel.PPM)),
                (body.getPosition().y +
                        (incY / com.ellzone.slotpuzzle2d.spin.SpinWheel.PPM)),
                Align.center);
        image.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
    }

    private boolean isSpinPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.S);
    }
}
