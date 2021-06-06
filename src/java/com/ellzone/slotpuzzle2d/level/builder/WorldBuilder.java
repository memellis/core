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

package com.ellzone.slotpuzzle2d.level.builder;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;

import box2dLight.RayHandler;

public class WorldBuilder {
    private com.badlogic.gdx.physics.box2d.World box2dWorld;
    private RayHandler rayHandler;

    public void build() {
        box2dWorld = new com.badlogic.gdx.physics.box2d.World(
                new Vector2(0, SlotPuzzleConstants.EARTH_GRAVITY),
                true);
        rayHandler = new RayHandler(box2dWorld);
    }

    public World getBox2dWorld() {
        return box2dWorld;
    }

    public RayHandler getRayHandler() {
        return rayHandler;
    }
}
