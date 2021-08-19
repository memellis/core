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
import com.artemis.annotations.All;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.component.artemis.Position;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorInjectionInterface;
import com.ellzone.slotpuzzle2d.level.creator.LevelObjectCreatorEntityHolder;
import com.ellzone.slotpuzzle2d.spin.SpinWheelSlotPuzzleTileMap;
import com.ellzone.slotpuzzle2d.sprites.slothandle.SlotHandleSprite;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import box2dLight.RayHandler;

@All({Position.class})
public class LevelCreatorSystem extends BaseSystem {

    public static final String COMPONENTS = "Components";
    private final World box2dWorld;
    private final RayHandler rayHandler;
    private TiledMapSystem tiledMapSystem;
    private LevelObjectCreatorEntityHolder levelObjectCreatorEntityHolder;
    private final LevelCreatorInjectionInterface levelCreatorInjectionInterface;
    private Array<Object> entities = new Array<>();

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

    public Array<Object> getEntities() { return entities; }

    private void setup() {
        createLevelEntities();
        setEntityComponents();
    }

    private void createLevelEntities() {
        for (MapLayer layer : tiledMapSystem.getTiledMap().getLayers()) {
            if (layer.isVisible()) {
                if (layer.getName().contains(COMPONENTS)) {
                    levelObjectCreatorEntityHolder.createLevel(
                            layer.getObjects().getByType(MapObject.class));
                }
            }
        }
    }

    private void setEntityComponents() {
        for (SlotHandleSprite handle :
                new Array.ArrayIterator<>(
                        levelObjectCreatorEntityHolder.getHandles()))
            processSlotHandle(handle);

        for (SpinWheelSlotPuzzleTileMap spinWheel :
            new Array.ArrayIterator<>(
                    levelObjectCreatorEntityHolder.getSpinWheels()))
            processSpinWheel(spinWheel);
    }

    private void processSlotHandle(SlotHandleSprite handle) {
        Array<Integer> entityIds = new Array<>();
        E e = E.E()
                .positionX(handle.getSlotHandleBaseSprite().getX())
                .positionY(handle.getSlotHandleBaseSprite().getY())
                .textureRegionRender();
        entityIds.add(e.id());
        entities.add(handle.getSlotHandleBaseSprite());

        e = E.E()
                .positionX(handle.getSlotHandleSprite().getX())
                .positionY(handle.getSlotHandleSprite().getY())
                .textureRegionRender();
        entityIds.add(e.id());
        handle.setEntityIds(entityIds);
        entities.add(handle.getSlotHandleSprite());
    }

    private void processSpinWheel(SpinWheelSlotPuzzleTileMap spinWheel) {
        Array<Integer> entityIds = new Array<>();
        spinWheel.setUpSpinWheel();

        E.E()
         .spinWheel();
        entities.add(spinWheel);

        E.E()
         .positionX(spinWheel.getWheelImage().getImageX())
         .positionY(spinWheel.getWheelImage().getImageY())
         .boxedBody(spinWheel.getWheelBody())
         .imageRender();
        entities.add(spinWheel.getWheelImage());

        E.E()
                .positionX(spinWheel.getNeedleImage().getImageX())
                .positionY(spinWheel.getNeedleImage().getImageY())
                .boxedBody(spinWheel.getNeedleBody())
                .imageRender();
        entities.add(spinWheel.getNeedleImage());
    }
}
