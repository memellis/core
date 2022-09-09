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

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.ellzone.slotpuzzle2d.component.artemis.Position;

import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;

public class InputSystem extends BaseSystem {
    private AnimatedReelSystem animatedReelSystem;
    private SpinWheelSystem spinWheelSystem;
    private SlotHandleSystem slotHandleSystem;
    private HoldLightButtonSystem holdLightButtonSystem;

    @Override
    protected void processSystem() {
        if (Gdx.input.justTouched())
            processTouched();
    }

    private void processTouched() {
        Vector3 unProjectTouch =
                new Vector3(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 0);
        slotHandleSystem.touched(unProjectTouch);
        animatedReelSystem.touched(unProjectTouch);
        spinWheelSystem.touched(unProjectTouch);
        holdLightButtonSystem.touched(unProjectTouch);
    }
}
