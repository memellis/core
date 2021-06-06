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

package com.ellzone.slotpuzzle2d.prototypes.artemis;

import com.artemis.FluidEntityPlugin;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.prototypes.artemis.system.SlotPuzzleSystem;

import net.mostlyoriginal.plugin.ProfilerPlugin;

public class RenderReelsUsingArtemis extends SPPrototype {
    
    public static final String WORLD_NOT_INITIALISED = "World not initialised";
    public static final float MIN_DELTA = 1 / 15f;

    private World world;

    public void create() {
        world = createWorld();
    }

    private World createWorld() {
        return new World(new WorldConfigurationBuilder()
                .alwaysDelayComponentRemoval(true)
                .dependsOn(
                        ProfilerPlugin.class,
                        FluidEntityPlugin.class)
                .with(
                        new SlotPuzzleSystem()
                )
                .build());
    }

    @Override
    public void render () {
        if (world == null)
            throw new RuntimeException(WORLD_NOT_INITIALISED);

        preventSpikesInDeltaCausingInsaneWorldUpdates(Gdx.graphics.getDeltaTime());
        world.process();
    }

    private void preventSpikesInDeltaCausingInsaneWorldUpdates(float deltaTime) {
        world.setDelta(MathUtils.clamp(deltaTime, 0, MIN_DELTA));
    }
}
