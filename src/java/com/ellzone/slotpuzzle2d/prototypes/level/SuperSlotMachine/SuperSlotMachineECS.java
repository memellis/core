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
import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.link.EntityLinkManager;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.ellzone.slotpuzzle2d.level.builder.WorldBuilder;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorInjector;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorInjectorExtended;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.systems.artemisodb.AnimatedReelSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.Box2DPhysicsSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.ClearScreenSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.DebugPointRenderSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.InputSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.LevelCreatorSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.RenderImagesSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.RenderTextureRegionRotationSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.RenderTextureRegionSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.RenderVectorsSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.SlotHandleSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.SpinWheelSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.TiledMapSystem;
import com.ellzone.slotpuzzle2d.systems.artemisodb.TweenEngineSystem;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.convert.TileMapToWorldConvert;
import com.ellzone.slotpuzzle2d.utils.tilemap.TileMapAttributes;

import net.mostlyoriginal.plugin.ProfilerPlugin;
import net.mostlyoriginal.plugin.OperationsPlugin;

public class SuperSlotMachineECS extends SPPrototype {

    public static final String WORLD_NOT_INITIALISED = "World not initialised";
    public static final float MIN_DELTA = 1 / 15f;
    public static final String LEVELS_SUPER_SLOT_MACHINE_LEVEL_TMX = "levels/super slot machine level.tmx";

    private World world;
    private LevelCreatorInjectorExtended levelCreatorInjectorExtended;
    private LevelCreatorSystem levelCreatorSystem;
    private TileMapAttributes tileMapAttributes;
    private final TweenManager tweenManager= new TweenManager();
    private boolean isDebugPointRenderSystemEnabled = false;

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
                        EntityLinkManager.class,
                        ProfilerPlugin.class,
                        FluidEntityPlugin.class,
                        OperationsPlugin.class)
                .with(
                        new SuperMapper(),
                        new TagManager(),
                        new GroupManager()
                )
                .with(
                        new TiledMapSystem(tileMapAttributes),
                        new LevelCreatorSystem(
                                levelCreatorInjectorExtended,
                                worldBuilder.getBox2dWorld(),
                                worldBuilder.getRayHandler()),
                        new Box2DPhysicsSystem(worldBuilder.getBox2dWorld()),
                        new TweenEngineSystem(tweenManager),
                        new AnimatedReelSystem(),
                        new SpinWheelSystem(),
                        new SlotHandleSystem(),
                        new InputSystem(),
                        new ClearScreenSystem(),
                        new RenderTextureRegionSystem(),
                        new RenderTextureRegionRotationSystem(),
                        new RenderImagesSystem(),
                        new RenderVectorsSystem(),
                        new DebugPointRenderSystem(isDebugPointRenderSystemEnabled)
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
