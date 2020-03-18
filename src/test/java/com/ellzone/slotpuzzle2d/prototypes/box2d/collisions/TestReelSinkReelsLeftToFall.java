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

package com.ellzone.slotpuzzle2d.prototypes.box2d.collisions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.gdx.MyGDXApplication;
import com.ellzone.slotpuzzle2d.messaging.MessageType;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

import org.junit.Test;

import static com.ellzone.slotpuzzle2d.prototypes.assets.CreateLevelReels.REEL_HEIGHT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestReelSinkReelsLeftToFall {
    @Test(expected = IllegalArgumentException.class)
    public void testReelSinkToFallNullAnimatedReels() {
        Array<AnimatedReel> animatedReels = new Array<>();
        Telegram message = new Telegram();
        message.message = MessageType.ReelSinkReelsLeftToFall.index;
        AnimatedReelsManager animatedReelsManager = new AnimatedReelsManager(animatedReels);
        animatedReelsManager.handleMessage(message);
    }

    @Test
    public void testReelSinkToFallOneReel() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        animatedReels.add(createAnimatedReel(160, 40, 0, 0));
        AnimatedReelsManager animatedReelsManager = sendReelSinkReelsLeftToFallMessage(
                animatedReels,
                0
        );
        assertThat(animatedReels.get(0).getReel().getY(),is(equalTo(40.0f)));
    }

    @Test
    public void testReelSinktoFallTwoReels() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        animatedReels = createAnimatedReelsFromSlotPuzzleMatrix(
                SlotPuzzleMatrices.createMatrixWithTwoBoxes());
        AnimatedReelsManager animatedReelsManager = sendReelSinkReelsLeftToFallMessage(
                animatedReels,
                96
        );
        Array<AnimatedReel> swappedReelsAboveAnimatedReels = animatedReelsManager.getAnimatedReels();
        assertDestinationY(
                swappedReelsAboveAnimatedReels,
                84,
                80,
                2,
                12,
                -REEL_HEIGHT
        );
    }

    @Test
    public void testReelSinkToFallAllSlotMatrices() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        Array<int[][]> slotMatrices = SlotPuzzleMatrices.getSlotMatrices();
        for (int[][] slotMatrix : slotMatrices)
            testReelSinkToFallSlotMatrix(slotMatrix, animatedReels);
    }

    private void testReelSinkToFallSlotMatrix(
            int[][] slotMatrix,
            Array<AnimatedReel> animatedReels) {
        animatedReels = createAnimatedReelsFromSlotPuzzleMatrix(slotMatrix);
        for (int column = 0; column < slotMatrix[0].length; column++) {
            AnimatedReelsManager animatedReelsManager = sendReelSinkReelsLeftToFallMessage(
                    animatedReels,
                    (slotMatrix.length - 1) * slotMatrix[0].length + column);
            Array<AnimatedReel> swappedReelsAboveAnimatedReels = animatedReelsManager.getAnimatedReels();
            assertSlotMatrix(slotMatrix, swappedReelsAboveAnimatedReels);
        }
    }

    private void assertSlotMatrix(int[][] slotMatrix, Array<AnimatedReel> swappedReelsAboveAnimatedReels) {
        for (int r = 0; r < slotMatrix.length; r++) {
            for (int c = 0; c < slotMatrix[0].length; c++) {
                int index = r * slotMatrix[0].length + c;
                float x = PlayScreen.PUZZLE_GRID_START_X + (c * 40);
                float y = ((slotMatrix.length - 1 - r) * 40) + 40;
                assertThat(swappedReelsAboveAnimatedReels.get(index).getReel().getX(), is(equalTo(x)));
                assertThat(swappedReelsAboveAnimatedReels.get(index).getReel().getY(), is(equalTo(y)));
                assertThat(swappedReelsAboveAnimatedReels.get(index).getReel().getDestinationX(), is(equalTo(x)));
                assertThat(swappedReelsAboveAnimatedReels.get(index).getReel().getDestinationY(), is(equalTo(y)));
            }
        }
    }

    AnimatedReelsManager sendReelSinkReelsLeftToFallMessage(
            Array<AnimatedReel> animatedReels,
            int animatedReel) {
        Telegram message = new Telegram();
        message.message = MessageType.ReelSinkReelsLeftToFall.index;
        message.extraInfo = animatedReels.get(animatedReel);
        AnimatedReelsManager animatedReelsManager = new AnimatedReelsManager(animatedReels);
        animatedReelsManager.handleMessage(message);
        return animatedReelsManager;
    }

    private Array<AnimatedReel> createAnimatedReelsFromSlotPuzzleMatrix(int[][] slotPuzzleMatrix) {
        Array<AnimatedReel> animatedReels = new Array<AnimatedReel>();
        int numberOfAnimatedReelsCreated = 0;
        for (int r = 0; r < slotPuzzleMatrix.length; r++) {
            for (int c = 0; c < slotPuzzleMatrix[0].length; c++) {
                animatedReels.add(
                        createAnimatedReel(
                                (int) PlayScreen.PUZZLE_GRID_START_X + (c * 40),
                                ((slotPuzzleMatrix.length - 1 - r) * 40) + 40,
                                slotPuzzleMatrix[r][c],
                                numberOfAnimatedReelsCreated));
                if (slotPuzzleMatrix[r][c] < 0)
                    animatedReels.get(numberOfAnimatedReelsCreated).getReel().deleteReelTile();
                numberOfAnimatedReelsCreated++;
            }
        }
        return animatedReels;
    }

    private AnimatedReel createAnimatedReel(int x, int y, int endReel, int index) {
        AnimatedReel animatedReel = getAnimatedReel(x, y, endReel);
        setUpReelTileInAnimatedReel(index, animatedReel);
        return animatedReel;
    }

    private void setUpReelTileInAnimatedReel(int index, AnimatedReel animatedReel) {
        ReelTile reelTile = animatedReel.getReel();
        reelTile.setDestinationX(reelTile.getX());
        reelTile.setDestinationY(reelTile.getY());
        reelTile.setY(reelTile.getY());
        reelTile.setIsFallen(false);
        reelTile.setIsStoppedFalling(false);
        reelTile.setIndex(index);
    }

    private AnimatedReel getAnimatedReel(int x, int y, int endReel) {
        AnimatedReel animatedReel = new AnimatedReel(
                null,
                x,
                y,
                40,
                40,
                40,
                40,
                0,
                null);
        animatedReel.setSx(0);
        animatedReel.setEndReel(endReel);
        animatedReel.setupSpinning();
        animatedReel.getReel().startSpinning();
        return animatedReel;
    }

    private void assertDestinationY(
            Array<AnimatedReel> animatedReels,
            int startIndex,
            float startY,
            int numberOfReels,
            int step,
            int reelHeight) {
        int count = 0;
        for (int index = startIndex; index < startIndex + numberOfReels * step; index+=step) {
            assertThat(
                    animatedReels.get(index).getReel().getDestinationY(),
                    is(equalTo(startY + count * reelHeight)));
            count++;
        }
    }
}
