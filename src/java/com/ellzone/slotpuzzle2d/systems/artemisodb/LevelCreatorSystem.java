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
import com.ellzone.slotpuzzle2d.level.light.LightGrid;
import com.ellzone.slotpuzzle2d.level.reel.ReelGrid;
import com.ellzone.slotpuzzle2d.spin.SpinWheelSlotPuzzleTileMap;
import com.ellzone.slotpuzzle2d.sprites.lights.HoldLightButton;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedPredictedReel;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReelECS;
import com.ellzone.slotpuzzle2d.sprites.slothandle.SlotHandleSprite;

import org.w3c.dom.css.Rect;

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
    private boolean isReelSpinDirectionClockwise = false;
    private boolean isSetup;

    public LevelCreatorSystem(
            LevelCreatorInjectionInterface levelCreatorInjectionInterface,
            World box2dWorld,
            RayHandler rayHandler
            ) {
        this.levelCreatorInjectionInterface = levelCreatorInjectionInterface;
        this.box2dWorld = box2dWorld;
        this.rayHandler = rayHandler;
    }

    public LevelCreatorSystem(
            LevelCreatorInjectionInterface levelCreatorInjectionInterface,
            World box2dWorld,
            RayHandler rayHandler,
            boolean isReelSpinDirectionClockwise) {
        this.levelCreatorInjectionInterface = levelCreatorInjectionInterface;
        this.box2dWorld = box2dWorld;
        this.rayHandler = rayHandler;
        this.isReelSpinDirectionClockwise = isReelSpinDirectionClockwise;
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
        setUpLightButtons();
        setUpLightGrids();
        setUpLightButtonsWithinLightGrids();
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

    private void setUpLightGrids() {
        for (LightGrid lightGrid :
            new Array.ArrayIterator<>(levelObjectCreatorEntityHolder.getLightGrids()))
                processLightGrid(lightGrid);
    }

    private void processAnimatedReel(AnimatedReel animatedReel) {
        Array<Integer> entityIds = new Array<>();
        AnimatedReelECS animatedReelECS =
                createAnimatedReeECSFromAnimatedReel(animatedReel);
        animatedReelsECS.add(animatedReelECS);
        E e = E.E()
                .positionX(animatedReelECS.getReel().getX())
                .positionY(animatedReelECS.getReel().getY())
                .spinScrollSY(animatedReelECS.getReel().getSy())
                .animatedReelComponent();
        entities.insert(e.id(), animatedReelECS);

        e = E.E()
            .positionX(animatedReelECS.getReel().getX())
            .positionY(animatedReelECS.getReel().getY())
            .textureRegionRender();
        entities.insert(e.id(), animatedReelECS.getReel().getRegion());
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
                null,
                isReelSpinDirectionClockwise);
    }

    private void processAnimatedPredictedReel(AnimatedPredictedReel animatedPredictedReel) {
        Array<Integer> entityIds = new Array<>();
        E e = E.E()
                .positionX(animatedPredictedReel.getReel().getX())
                .positionY(animatedPredictedReel.getReel().getY())
                .tag("AnimatedPredictedReel")
                .animatedPredictedReelComponent();
        entities.insert(e.id(), animatedPredictedReel);

        e = E.E()
                .positionX(animatedPredictedReel.getReel().getX())
                .positionY(animatedPredictedReel.getReel().getY())
                .textureRegionRender();
        entities.insert(e.id(), animatedPredictedReel.getReel().getRegion());
        entityIds.add(e.id());
        animatedPredictedReel.getReel().setEntityIds(entityIds);
    }

    private void processSlotHandle(SlotHandleSprite handle) {
        Array<Integer> entityIds = new Array<>();
        E e = E.E()
           .slotHandle();
        entities.insert(e.id(), handle);

        e = E.E()
            .positionX(handle.getSlotHandleBaseSprite().getX())
            .positionY(handle.getSlotHandleBaseSprite().getY())
            .textureRegionRender();
        entityIds.add(e.id());
        entities.insert(e.id(), handle.getSlotHandleBaseSprite());

        e = E.E()
            .positionX(handle.getSlotHandleSprite().getX())
            .positionY(handle.getSlotHandleSprite().getY())
            .rotation(0)
            .textureRegionRender();
        entityIds.add(e.id());
        handle.setEntityIds(entityIds);
        entities.insert(e.id(), handle.getSlotHandleSprite());
    }

    private void processSpinWheel(SpinWheelSlotPuzzleTileMap spinWheel) {
        spinWheel.setUpSpinWheel();
        E e = E.E()
               .spinWheel();
        entities.insert(e.id(), spinWheel);

        e = E.E()
            .positionX(spinWheel.getWheelImage().getImageX())
            .positionY(spinWheel.getWheelImage().getImageY())
            .boxedBody(spinWheel.getWheelBody())
            .imageRender();
        entities.insert(e.id(), spinWheel.getWheelImage());

        e = E.E()
            .positionX(spinWheel.getNeedleImage().getImageX())
            .positionY(spinWheel.getNeedleImage().getImageY())
            .boxedBody(spinWheel.getNeedleBody())
            .imageRender();
        entities.insert(e.id(), spinWheel.getNeedleImage());

        e = E.E()
                .positionX(spinWheel.getSpinButton().getImageX())
                .positionY(spinWheel.getSpinButton().getImageY())
                .imageRender();
        entities.insert(e.id(), spinWheel.getSpinButton());
    }

    private void processReelGrid(ReelGrid reelGrid) {
        E e = E.E()
                .positionX(reelGrid.getX())
                .positionY(reelGrid.getY());
        entities.insert(e.id(), reelGrid);
    }

    private void processLightGrid(LightGrid lightGrid) {
        E e = E.E()
                .positionX(lightGrid.getX())
                .positionY(lightGrid.getY());
        entities.insert(e.id(), lightGrid);
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

    private void setUpLightButtons() {
        for (HoldLightButton holdLightButton :
                new Array.ArrayIterator<>(levelObjectCreatorEntityHolder.getHoldLightButtons()))
            processLightButton(holdLightButton);
    }

    private void processLightButton(HoldLightButton holdLightButton) {
        Array<Integer> entityIds = new Array<>();
        E e = E.E()
                .positionX(holdLightButton.getLight().getX())
                .positionY(holdLightButton.getLight().getY())
                .holdLightButtonComponent();
        entities.insert(e.id(), holdLightButton);

        e = E.E()
                .positionX(holdLightButton.getSprite().getX())
                .positionY(holdLightButton.getSprite().getY())
                .textureRegionRender();
        entityIds.add(e.id());
        entities.insert(e.id(), holdLightButton.getSprite());
        holdLightButton.setEntityIds(entityIds);
    }

    private void setUpLightButtonsWithinLightGrids() {
        for (HoldLightButton holdLightButton :
                new Array.ArrayIterator<>(levelObjectCreatorEntityHolder.getHoldLightButtons()))
            checkLightButtonWithinLightGrids(holdLightButton);
    }

    private void checkLightButtonWithinLightGrids(HoldLightButton holdLightButton) {
        for (LightGrid lightGrid :
            new Array.ArrayIterator<>(levelObjectCreatorEntityHolder.getLightGrids()))
            checkLightButtonWithinLightGrid(holdLightButton, lightGrid);
    }

    private void checkLightButtonWithinLightGrid(HoldLightButton holdLightButton, LightGrid lightGrid) {
        Rectangle holdLightButtonRectangle = new Rectangle(
                holdLightButton.getSprite().getX(),
                holdLightButton.getSprite().getY(),
                holdLightButton.getSprite().getWidth(),
                holdLightButton.getSprite().getHeight());
        Rectangle lightGridRectangle = new Rectangle(
                lightGrid.getX(),
                lightGrid.getY(),
                lightGrid.getWidth(),
                lightGrid.getHeight());
        if (holdLightButtonRectangle.overlaps(lightGridRectangle))
            lightGrid.addHoldLightButton(holdLightButton);
        lightGrid.getHoldLightButtonsWithinLightGrid().sort(
                new Comparator<HoldLightButton>() {
                    @Override
                    public int compare(HoldLightButton b1, HoldLightButton b2) {
                        return (int) (b1.getSprite().getX() - b2.getSprite().getX());
                    }
                }
        );
    }
}
