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

package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.gdx.MyGDXApplication;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.AnimatedReelsMatrixCreator;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.SlotPuzzleMatrices;
import com.ellzone.slotpuzzle2d.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestFlashSlots {

    public static final int MAP_WIDTH = 12;
    public static final int MAP_HEIGHT = 9;
    public static final int DELTA_TIME = 100;

    @Test
    public void testFlashSlotsWithEmptyReelsToFlash() {
        Array<ReelTile> reelTiles = new Array<>();
        FlashSlots flashSlots = new FlashSlots(
                null,
                new GridSize(MAP_WIDTH,  MAP_HEIGHT),
                reelTiles);
        assertThat(flashSlots.getNumberOfReelsFlashing(),is(equalTo(0)));
    }

    @Test
    public void testFlashSlotsWithEmptyReelsFlashSlots() {
        Array<ReelTile> reelTiles = new Array<>();
        FlashSlots flashSlots = new FlashSlots(
                null,
                new GridSize(MAP_WIDTH, MAP_HEIGHT),
                reelTiles);
        flashSlots.flashSlots(reelTiles);
        assertFlashSlotsHasFinished(flashSlots);
    }


    @Test
    public void testFlashSlotsWithOneReel() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels;
        AnimatedReelsMatrixCreator animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                  SlotPuzzleMatrices.createMatrixWithOneBox());
        Array<ReelTile> reelTiles = getReelTilesFromAnimatedReels(animatedReels);
        FlashSlots flashSlots = new FlashSlots(
                 null,
                  new GridSize(MAP_WIDTH, MAP_HEIGHT),
                  reelTiles);
                  flashSlots.flashSlots(reelTiles);
         assertFlashSlotsHasFinished(flashSlots);
    }

    @Test
    public void testFlashSlotsWithTwoReelsThatMatch() {
        assertFlashSlotsHasFinishedFlashingReels(testFlashSlotsForSlotMatrix(
                SlotPuzzleMatrices.createMatrixWithTwoBoxes()));
    }

    @Test
    public void testFlashSlotsWithSlotMatrices() {
        Array<int [][]> slotMatrices = SlotPuzzleMatrices.getSlotMatrices();
        for (int slotMatrixIndex = 0; slotMatrixIndex < slotMatrices.size; slotMatrixIndex++)
            assertFlashSlotsHasFinishedFlashingReels(
                    testFlashSlotsForSlotMatrix(slotMatrices.get(slotMatrixIndex)));
    }

    private FlashSlots testFlashSlotsForSlotMatrix(int[][] slotMatrix) {
        Gdx.app = new MyGDXApplication();
        TweenManager tweenManager = initialTweenEngine();
        AnimatedReelsMatrixCreator animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        Array<AnimatedReel> animatedReels =
             animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(slotMatrix);

        Array<ReelTile> reelTiles = getReelTilesFromAnimatedReels(animatedReels);

        FlashSlots flashSlots = new FlashSlots(
                tweenManager,
                new GridSize(MAP_WIDTH, MAP_HEIGHT),
                reelTiles);

        flashSlots.flashSlots(reelTiles);
        waitUntilAllReelsHaveflashed(tweenManager, flashSlots);
        return flashSlots;
    }

    private TweenManager initialTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        return new TweenManager();
    }

    private void waitUntilAllReelsHaveflashed(TweenManager tweenManager, FlashSlots flashSlots) {
        while (flashSlots.getNumberOfReelsFlashing() > 0) {
            try {
                tweenManager.update(DELTA_TIME);
                Thread.sleep(DELTA_TIME);
            } catch (InterruptedException ie) {}
        }
    }

    private Array<ReelTile> getReelTilesFromAnimatedReels(Array<AnimatedReel> animatedReels) {
        Array<ReelTile> reelTiles = new Array<>();
        for (AnimatedReel animatedReel : animatedReels)
            reelTiles.add(animatedReel.getReel());
        return reelTiles;
    }

    private void assertFlashSlotsHasFinished(FlashSlots flashSlots) {
        assertThat(flashSlots.getNumberOfReelsFlashing(), is(equalTo(0)));
        assertThat(flashSlots.areReelsFlashing(), is(equalTo(false)));
        assertThat(flashSlots.areReelsStartedFlashing(), is(equalTo(false)));
        assertThat(flashSlots.areReelsDeleted(), is(equalTo(true)));
    }

    private void assertFlashSlotsHasFinishedFlashingReels(FlashSlots flashSlots) {
        assertThat(flashSlots.getNumberOfReelsFlashing(), is(equalTo(0)));
        assertThat(flashSlots.areReelsFlashing(), is(equalTo(false)));
    }
}
