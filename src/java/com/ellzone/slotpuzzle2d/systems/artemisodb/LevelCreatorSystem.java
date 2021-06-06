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
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.All;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.component.artemis.Position;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorInjectionInterface;
import com.ellzone.slotpuzzle2d.level.creator.LevelObjectCreatorEntityHolder;
import com.ellzone.slotpuzzle2d.sprites.SpriteRenderInterface;
import com.ellzone.slotpuzzle2d.sprites.slothandle.SlotHandleSprite;
import com.ellzone.slotpuzzle2d.sprites.slothandle.SlotHandleTileMap;

import box2dLight.RayHandler;

@All({Position.class})
public class LevelCreatorSystem extends BaseSystem {

    private final World box2dWorld;
    private final RayHandler rayHandler;
    private TiledMapSystem tiledMapSystem;
    private LevelObjectCreatorEntityHolder levelObjectCreatorEntityHolder;
    private final LevelCreatorInjectionInterface levelCreatorInjectionInterface;
    public Array<TextureRegion> spritesToRender = new Array<>();

    private boolean isSetup;

    public LevelCreatorSystem(
            LevelCreatorInjectionInterface levelCreatorInjectionInterface,
            World box2dWorld,
            RayHandler rayHandler) {
        this.levelCreatorInjectionInterface = levelCreatorInjectionInterface;
        this.box2dWorld = box2dWorld;
        this.rayHandler = rayHandler;
    }

    @Override
    public void initialize() {
        levelObjectCreatorEntityHolder = new LevelObjectCreatorEntityHolder(
              levelCreatorInjectionInterface,
              box2dWorld,
              rayHandler
        );
    }

    @Override
    protected void processSystem() {
        if (!isSetup) {
            isSetup = true;
            setup();
        }
     }

    private void setup() {
        createLevelEntities();
        setEntityComponents();
    }

    private void createLevelEntities() {
        for (MapLayer layer : tiledMapSystem.map.getLayers()) {
            if (layer.isVisible()) {
                if (layer.getName().contains("Components")) {
                    levelObjectCreatorEntityHolder.createLevel(
                            layer.getObjects().getByType(MapObject.class));
                }
            }
        }
    }

    private void setEntityComponents() {
        for (SlotHandleSprite handle :
                new Array.ArrayIterator<>(
                        levelObjectCreatorEntityHolder.getHandles())) {

            Array<Integer> entityIds = new Array<>();
            E e = E.E()
                    .positionX(handle.getSlotHandleBaseSprite().getX())
                    .positionY(handle.getSlotHandleBaseSprite().getY());
            entityIds.add(e.id());
            spritesToRender.add(handle.getSlotHandleBaseSprite());

            e = E.E()
                    .positionX(handle.getSlotHandleSprite().getX())
                    .positionY(handle.getSlotHandleSprite().getY());
            entityIds.add(e.id());
            handle.setEntityIds(entityIds);
            spritesToRender.add(handle.getSlotHandleSprite());

        }
    }
}
