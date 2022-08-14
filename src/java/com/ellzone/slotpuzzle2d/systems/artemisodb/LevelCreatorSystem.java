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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.component.artemis.Position;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorInjectionInterface;
import com.ellzone.slotpuzzle2d.level.creator.LevelObjectCreatorEntityHolder;
import com.ellzone.slotpuzzle2d.level.reel.ReelGrid;
import com.ellzone.slotpuzzle2d.spin.SpinWheelSlotPuzzleTileMap;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedPredictedReel;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReelECS;
import com.ellzone.slotpuzzle2d.sprites.slothandle.SlotHandleSprite;

import java.util.Comparator;

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
    private Array<AnimatedReelECS> animatedReelsECS = new Array<>();

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

    public LevelObjectCreatorEntityHolder getLevelObjectCreatorEntityHolder() {
        return levelObjectCreatorEntityHolder;
    }

    public Array<AnimatedReelECS> getAnimatedReelsECS() {
        return animatedReelsECS;
    }

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
        setUpAnimatedReels();
        setUpAnimatedPredictedReels();
        setUpSlotHandle();
        setUpSpinWheels();
        setUpReelGrids();
        setUpAnimatedReelsWithinReelGrids();
    }

    private void setUpAnimatedReels() {
        for (AnimatedReel animatedReel :
                new Array.ArrayIterator<>(
                        levelObjectCreatorEntityHolder.getAnimatedReels()))
            processAnimatedReel(animatedReel);
    }

    private void setUpAnimatedPredictedReels() {
        for (AnimatedPredictedReel animatedPredictedReel :
                new Array.ArrayIterator<>(
                        levelObjectCreatorEntityHolder.getAnimatedPredictedReels()))
            processAnimatedPredictedReel(animatedPredictedReel);
    }

    private void setUpSlotHandle() {
        for (SlotHandleSprite handle :
                new Array.ArrayIterator<>(
                        levelObjectCreatorEntityHolder.getHandles()))
            processSlotHandle(handle);
    }

    private void setUpSpinWheels() {
        for (SpinWheelSlotPuzzleTileMap spinWheel :
            new Array.ArrayIterator<>(
                    levelObjectCreatorEntityHolder.getSpinWheels()))
            processSpinWheel(spinWheel);
    }

    private void setUpReelGrids() {
        for (ReelGrid reelGrid :
            new Array.ArrayIterator<>(levelObjectCreatorEntityHolder.getReelGrids()))
                processReelGrid(reelGrid);
    }

    private void processAnimatedReel(AnimatedReel animatedReel) {
        Array<Integer> entityIds = new Array<>();
        AnimatedReelECS animatedReelECS =
                createAnimatedReeECSFromAnimatedReel(animatedReel);
        animatedReelsECS.add(animatedReelECS);
        E.E()
                .positionX(animatedReel.getReel().getX())
                .positionY(animatedReel.getReel().getY())
                .spinScrollSY(animatedReel.getReel().getSy())
                .animatedReelComponent();
        entities.add(animatedReelECS);

        E e =E.E()
            .positionX(animatedReel.getReel().getX())
            .positionY(animatedReel.getReel().getY())
            .textureRegionRender();
        entities.add(animatedReel.getReel().getRegion());
        entityIds.add(e.id());
        animatedReelECS.getReel().setEntityIds(entityIds);
    }

    private AnimatedReelECS createAnimatedReeECSFromAnimatedReel(AnimatedReel animatedReel) {
        return new AnimatedReelECS(
                animatedReel.getTexture(),
                animatedReel.getX(),
                animatedReel.getY(),
                animatedReel.getTileWidth(),
                animatedReel.getTileHeight(),
                animatedReel.getReelDisplayWidth(),
                animatedReel.getReelDisplayHeight(),
                animatedReel.getEndReel(),
                null);
    }

    private void processAnimatedPredictedReel(AnimatedPredictedReel animatedPredictedReel) {
        Array<Integer> entityIds = new Array<>();
        E.E()
                .positionX(animatedPredictedReel.getReel().getX())
                .positionY(animatedPredictedReel.getReel().getY())
                .tag("AnimatedPredictedReel")
                .animatedPredictedReelComponent();
        entities.add(animatedPredictedReel);

        E e = E.E()
                .positionX(animatedPredictedReel.getReel().getX())
                .positionY(animatedPredictedReel.getReel().getY())
                .textureRegionRender();
        entities.add(animatedPredictedReel.getReel().getRegion());
        entityIds.add(e.id());
        animatedPredictedReel.getReel().setEntityIds(entityIds);
    }

    private void processSlotHandle(SlotHandleSprite handle) {
        Array<Integer> entityIds = new Array<>();
        E.E()
           .slotHandle();
        entities.add(handle);

        E e = E.E()
            .positionX(handle.getSlotHandleBaseSprite().getX())
            .positionY(handle.getSlotHandleBaseSprite().getY())
            .textureRegionRender();
        entityIds.add(e.id());
        entities.add(handle.getSlotHandleBaseSprite());

        e = E.E()
            .positionX(handle.getSlotHandleSprite().getX())
            .positionY(handle.getSlotHandleSprite().getY())
            .rotation(0)
            .textureRegionRender();
        entityIds.add(e.id());
        handle.setEntityIds(entityIds);
        entities.add(handle.getSlotHandleSprite());
    }

    private void processSpinWheel(SpinWheelSlotPuzzleTileMap spinWheel) {
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

        E.E()
                .positionX(spinWheel.getSpinButton().getImageX())
                .positionY(spinWheel.getSpinButton().getImageY())
                .imageRender();
        entities.add(spinWheel.getSpinButton());
    }

    private void processReelGrid(ReelGrid reelGrid) {
        E.E()
                .positionX(reelGrid.getX())
                .positionY(reelGrid.getY());
    }

    private void setUpAnimatedReelsWithinReelGrids() {
          for (AnimatedReelECS animatedReel :
                  new Array.ArrayIterator<>(getAnimatedReelsECS()))
              checkAnimatedReelWithinReelGrids(animatedReel);
    }

    private void checkAnimatedReelWithinReelGrids(AnimatedReelECS animatedReel) {
        for (ReelGrid reelGrid :
                new Array.ArrayIterator<>(levelObjectCreatorEntityHolder.getReelGrids()))
                    checkAnimatedReelWithinReelGrid(animatedReel, reelGrid);
    }

    private void checkAnimatedReelWithinReelGrid(AnimatedReelECS animatedReel, ReelGrid reelGrid) {
        Rectangle animatedReelRectangle = new Rectangle(
                animatedReel.getX(),
                animatedReel.getY(),
                animatedReel.getTileWidth(),
                animatedReel.getTileHeight());
        Rectangle reelGridRectangle = new Rectangle(
                reelGrid.getX(),
                reelGrid.getY(),
                reelGrid.getWidth(),
                reelGrid.getHeight()
        );
        if (animatedReelRectangle.overlaps(reelGridRectangle))
            reelGrid.addAnimatedReel(animatedReel);
        reelGrid.getAnimatedReelsWithinReelGrid().sort(
                new Comparator<AnimatedReelECS>() {
                    @Override
                    public int compare(AnimatedReelECS reel1, AnimatedReelECS reel2) {
                        return (int) (reel1.getReel().getX() - reel2.getReel().getX());
                    }
                }
        );
    }
}
