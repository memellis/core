/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.prototypes.level.SuperSlotMachine;

import com.artemis.FluidEntityPlugin;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.ellzone.slotpuzzle2d.level.builder.WorldBuilder;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorInjector;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorInjectorExtended;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.systems.artemisodb.DebugPointRenderSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.LevelCreatorSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.RenderImagesSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.RenderTextureRegionSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.SpinWheelSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.TiledMapSystem;
import com.ellzone.slotpuzzle2d.utils.convert.TileMapToWorldConvert;
import com.ellzone.slotpuzzle2d.utils.tilemap.TileMapAttributes;

import net.mostlyoriginal.plugin.ProfilerPlugin;

public class SuperSlotMachine extends SPPrototype {

    public static final String WORLD_NOT_INITIALISED = "World not initialised";
    public static final float MIN_DELTA = 1 / 15f;
    public static final String LEVELS_SUPER_SLOT_MACHINE_LEVEL_TMX = "levels/super slot machine level.tmx";

    private World world;
    private LevelCreatorInjectorExtended levelCreatorInjectorExtended;
    private LevelCreatorSystem levelCreatorSystem;
    private TileMapAttributes tileMapAttributes;

    public void create() {
        tileMapAttributes =
                new TileMapAttributes(LEVELS_SUPER_SLOT_MACHINE_LEVEL_TMX);
        TileMapToWorldConvert tileMapToWorldConvert =
                new TileMapToWorldConvert(
                        tileMapAttributes.getWidth() * tileMapAttributes.getTileWidth(),
                        tileMapAttributes.getHeight() * tileMapAttributes.getTileWidth(),
                        Gdx.graphics.getWidth(),
                        Gdx.graphics.getHeight());
        levelCreatorInjectorExtended = new LevelCreatorInjectorExtended(tileMapToWorldConvert);

        world = createWorld();
    }

    private World createWorld() {
        WorldBuilder worldBuilder = new WorldBuilder();
        worldBuilder.build();
        return new World(new WorldConfigurationBuilder()
                .alwaysDelayComponentRemoval(true)
                .dependsOn(
                        ProfilerPlugin.class,
                        FluidEntityPlugin.class)
                .with(
                        new TiledMapSystem(tileMapAttributes),
                        new LevelCreatorSystem(
                                levelCreatorInjectorExtended,
                                worldBuilder.getBox2dWorld(),
                                worldBuilder.getRayHandler()),
                        new SpinWheelSystem(),
                        new RenderTextureRegionSystem(),
                        new RenderImagesSystem(),
                        new DebugPointRenderSystem()
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
