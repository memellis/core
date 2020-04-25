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
import com.ellzone.slotpuzzle2d.physics.contact.AnimatedReelsManager;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static com.ellzone.slotpuzzle2d.prototypes.assets.CreateLevelReels.REEL_HEIGHT;
import static com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.Box2DBoxesFallingFromSlotPuzzleMatrices.SCREEN_OFFSET;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestSwapReelsAbove {
    @Test(expected = IllegalArgumentException.class)
    public void testSwapReelsAboveMeWithHandleMessageWithNullMessage() {
        Array<AnimatedReel> animatedReels = new Array<>();
        AnimatedReelsManager animatedReelsManager = new AnimatedReelsManager(animatedReels);
        animatedReelsManager.handleMessage(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSwapReelsAboveMeWithHandleMessageSwapReelsAboveMeNullExtraInfo() {
        Array<AnimatedReel> animatedReels = new Array<>();
        AnimatedReelsManager animatedReelsManager = new AnimatedReelsManager(animatedReels);
        Telegram message = new Telegram();
        message.message = MessageType.SwapReelsAboveMe.index;
        animatedReelsManager.handleMessage(message);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSwapReelsAboveMeWithHandleMessageSwapReelsAboveMeOneAnimtaedReelInExtraInfo() {
        Array<AnimatedReel> animatedReels = new Array<>();
        AnimatedReelsManager animatedReelsManager = new AnimatedReelsManager(animatedReels);
        Telegram message = new Telegram();
        message.message = MessageType.SwapReelsAboveMe.index;
        addAnimatedReel(animatedReels, 160, 40);
        message.extraInfo = animatedReels;
        animatedReelsManager.handleMessage(message);
    }

    @Test
    public void testSwapReelsAboveMeTwoAnimatedReels() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        addAnimatedReel(animatedReels, 160, 40);
        addAnimatedReel(animatedReels, 160, 80);
        AnimatedReelsManager animatedReelsManager = new AnimatedReelsManager(animatedReels);
        handleSwapReelsAboveMessage(animatedReels, animatedReelsManager, 0, 1);
        Array<AnimatedReel> swappedReelsAboveMeAnimatedReels = animatedReelsManager.getAnimatedReels();
        for (int currentReel = 0; currentReel < animatedReels.size; currentReel++)
            assertAnimatedReelNotSwapped(animatedReels, swappedReelsAboveMeAnimatedReels, currentReel);
    }

    @Test
    public void testSwapReelsAboveMeTThreeAnimatedReels() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        addAnimatedReel(animatedReels, 160, 40);
        addAnimatedReel(animatedReels, 160, 80);
        addAnimatedReel(animatedReels, 160, 120);
        AnimatedReelsManager animatedReelsManager = new AnimatedReelsManager(animatedReels);
        handleSwapReelsAboveMessage(animatedReels, animatedReelsManager, 0, 1);
        Array<AnimatedReel> swappedReelsAboveMeAnimatedReels = animatedReelsManager.getAnimatedReels();
        for (int currentReel = 0; currentReel < animatedReels.size; currentReel++)
            assertAnimatedReelNotSwapped(animatedReels, swappedReelsAboveMeAnimatedReels, currentReel);
    }

    @Test
    public void testSwapReelsAboveMeWithOneReelFallingBelowDestination() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        addAnimatedReelXYandDestXY(animatedReels, 160, 40, 160, 40);
        addAnimatedReelXYandDestXY(animatedReels, 160, 80, 160, 120);
        AnimatedReelsManager animatedReelsManager = new AnimatedReelsManager(animatedReels);
        handleSwapReelsAboveMessage(animatedReels, animatedReelsManager, 0, 1);
        Array<AnimatedReel> swappedReelsAboveMeAnimatedReels = animatedReelsManager.getAnimatedReels();
        for (int currentReel = 0; currentReel < animatedReels.size; currentReel++)
            assertAnimatedReelNotSwapped(animatedReels, swappedReelsAboveMeAnimatedReels, currentReel);
    }

    @Test
    public void testSwapReelsAboveMeWithTwoReelsFallingBelowDestination() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        addAnimatedReelXYandDestXY(animatedReels, 160, 40, 160, 40);
        addAnimatedReelXYandDestXY(animatedReels, 160, 80, 160, 120);
        addAnimatedReelXYandDestXY(animatedReels, 160, 120, 160, 160);
        AnimatedReelsManager animatedReelsManager = new AnimatedReelsManager(animatedReels);
        handleSwapReelsAboveMessage(animatedReels, animatedReelsManager, 0, 1);
        Array<AnimatedReel> swappedReelsAboveMeAnimatedReels = animatedReelsManager.getAnimatedReels();
        for (int currentReel = 0; currentReel < animatedReels.size; currentReel++)
            assertAnimatedReelNotSwapped(animatedReels, swappedReelsAboveMeAnimatedReels, currentReel);
    }

    @Test
    public void testSwapReelsFallenWithThreeReels() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        animatedReels = SlotPuzzleMatrices.createAnimatedReelsFromSlotPuzzleMatrix(
            SlotPuzzleMatrices.createMatrixWithThreeBoxes());
        animatedReelsSetDestinationY(
                animatedReels,
                72,
                160.0f,
                2,
                12,
                -REEL_HEIGHT);
        animatedReels.get(60).getReel().setDestinationY(80);
        animatedReels.get(60).getReel().setY(80 + SCREEN_OFFSET);
        animatedReels.get(60).getReel().deleteReelTile();

        AnimatedReelsManager animatedReelsManager =
                sendSwapReelsAboveMessage(animatedReels, 96, 84);

        Array<AnimatedReel> swappedReelsAboveAnimatedReels = animatedReelsManager.getAnimatedReels();

        assertDestinationY(
                swappedReelsAboveAnimatedReels,
                60,
                160.0f,
                4,
                12,
                -REEL_HEIGHT);
        assertThat(swappedReelsAboveAnimatedReels.get(60).getReel().isReelTileDeleted(), is(true));
    }

    @Test
    public void testSwapReelsFallenWithFourReels() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        animatedReels = SlotPuzzleMatrices.createAnimatedReelsFromSlotPuzzleMatrix(
                SlotPuzzleMatrices.createMatrixWithFourBoxes());
        animatedReelsSetDestinationY(
                animatedReels,
                60,
                200.0f,
                3,
                12,
                -REEL_HEIGHT);

        animatedReels.get(48).getReel().setDestinationY(80);
        animatedReels.get(48).getReel().setY(80 + SCREEN_OFFSET);
        animatedReels.get(48).getReel().deleteReelTile();

        AnimatedReelsManager animatedReelsManager =
                sendSwapReelsAboveMessage(animatedReels, 96, 84);

        Array<AnimatedReel> swappedReelsAboveAnimatedReels = animatedReelsManager.getAnimatedReels();

        assertDestinationY(
                swappedReelsAboveAnimatedReels,
                48,
                200.0f,
                5,
                12,
                -REEL_HEIGHT);
        assertThat(swappedReelsAboveAnimatedReels.get(48).getReel().isReelTileDeleted(), is(true));
    }

    @Test
    public void testSwapReelsFallenWithFourReelsAndWithReelsStoppingFalling() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        animatedReels = SlotPuzzleMatrices.createAnimatedReelsFromSlotPuzzleMatrix(
                SlotPuzzleMatrices.createMatrixWithFourBoxes());
        animatedReelsSetDestinationY(
                animatedReels,
                60,
                200.0f,
                3,
                12,
                -REEL_HEIGHT);
        animatedReels.get(48).getReel().setDestinationY(80);
        animatedReels.get(48).getReel().setY(80 + SCREEN_OFFSET);
        animatedReels.get(48).getReel().deleteReelTile();

        AnimatedReelsManager animatedReelsManager =
                sendSwapReelsAboveMessage(animatedReels, 96, 84);

        Array<AnimatedReel> swappedReelsAboveAnimatedReels = animatedReelsManager.getAnimatedReels();
        assertDestinationY(
                swappedReelsAboveAnimatedReels,
                48,
                200.0f,
                5,
                12,
                -REEL_HEIGHT);
        assertThat(swappedReelsAboveAnimatedReels.get(96).getReel().isStoppedFalling(), is(true));
        assertThat(swappedReelsAboveAnimatedReels.get(84).getReel().isStoppedFalling(), is(true));
        assertThat(swappedReelsAboveAnimatedReels.get(72).getReel().isStoppedFalling(), is(true));
        assertThat(swappedReelsAboveAnimatedReels.get(60).getReel().isStoppedFalling(), is(true));
        assertThat(swappedReelsAboveAnimatedReels.get(48).getReel().getDestinationY(), is(equalTo(200f)));
    }

    @Test
    public void testSwapReelsFallenWithFillColumnNineBoxes() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        animatedReels = SlotPuzzleMatrices.createAnimatedReelsFromSlotPuzzleMatrix(
                SlotPuzzleMatrices.createMatrixWithFourBoxes());

        AnimatedReelsManager animatedReelsManager =
                sendSwapReelsAboveMessage(animatedReels, 96, 84);
        Array<AnimatedReel> swappedReelsAboveAnimatedReels = animatedReelsManager.getAnimatedReels();
        for (int currentReel = 0; currentReel < animatedReels.size; currentReel++)
            assertAnimatedReelNotSwapped(animatedReels, swappedReelsAboveAnimatedReels, currentReel);
    }

    @Test
    public void testSwapReelsFallenWithTwoByTwoReelsDeleted() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        animatedReels = SlotPuzzleMatrices.createAnimatedReelsFromSlotPuzzleMatrix(
                SlotPuzzleMatrices.createMatrixWithFillColumnNineBoxes());

        prepareTestWithDeletedBoxes84and72And60Hitting96(animatedReels);
        AnimatedReelsManager animatedReelsManager =
                sendSwapReelsAboveMessage(animatedReels, 96, 60);
        assertBoxesAfter60Hitting96(animatedReels);
        prepareTestWith36And24Hitting60(animatedReels);
        sendSwapReelsAboveMessage(animatedReelsManager, animatedReels, 60, 24);
        assertBoxes24Hitting60(animatedReels);
    }

    private void prepareTestWithDeletedBoxes84and72And60Hitting96(
            Array<AnimatedReel> animatedReels) {
        animatedReels.get(84).getReel().deleteReelTile();
        animatedReels.get(72).getReel().deleteReelTile();
        animatedReels.get(48).getReel().deleteReelTile();
        animatedReels.get(36).getReel().deleteReelTile();
        animatedReels.get(60).getReel().setY(80);
    }

    private void assertBoxesAfter60Hitting96(
            Array<AnimatedReel> animatedReels) {
        assertThat(animatedReels.get(96).getReel().getY(), is(equalTo(40.0f)));
        assertThat(animatedReels.get(96).getReel().getDestinationY(), is(equalTo(40.0f)));

        assertThat(animatedReels.get(60).getReel().getY(), is(equalTo(80.0f)));
        assertThat(animatedReels.get(60).getReel().getDestinationY(), is(equalTo(80.0f)));

        assertThat(animatedReels.get(48).getReel().getY(), is(equalTo(200.0f)));
        assertThat(animatedReels.get(48).getReel().getDestinationY(), is(equalTo(200.0f)));
        assertThat(animatedReels.get(48).getReel().isReelTileDeleted(), is(true));

        assertThat(animatedReels.get(36).getReel().getY(), is(equalTo(240.0f)));
        assertThat(animatedReels.get(36).getReel().getDestinationY(), is(equalTo(240.0f)));
        assertThat(animatedReels.get(36).getReel().isReelTileDeleted(), is(true));

        assertThat(animatedReels.get(24).getReel().getY(), is(equalTo(280.f)));
        assertThat(animatedReels.get(24).getReel().getY(), is(equalTo(280.f)));

        assertThat(animatedReels.get(12).getReel().getY(), is(equalTo(320.f)));
        assertThat(animatedReels.get(12).getReel().getY(), is(equalTo(320.f)));

        assertThat(animatedReels.get(0).getReel().getY(), is(equalTo(360.f)));
        assertThat(animatedReels.get(0).getReel().getY(), is(equalTo(360.f)));

    }

    private void prepareTestWith36And24Hitting60(
            Array<AnimatedReel> animatedReels) {
        animatedReels.get(24).getReel().setY(120);
        animatedReels.get(12).getReel().setY(160);
        animatedReels.get(0).getReel().setY(200);
    }

    private void sendSwapReelsAboveMessage(
            AnimatedReelsManager animatedReelsManager,
            Array<AnimatedReel> animatedReels,
            int reelBelow,
            int reelAbove) {
        Telegram message = new Telegram();
        message.message = MessageType.SwapReelsAboveMe.index;
        Array<AnimatedReel> reelsAB = new Array<>();
        reelsAB.add(animatedReels.get(reelBelow));
        reelsAB.add(animatedReels.get(reelAbove));
        message.extraInfo = reelsAB;
        animatedReelsManager.handleMessage(message);
    }

    private void assertBoxes24Hitting60(
            Array<AnimatedReel> animatedReels) {
        assertThat(animatedReels.get(96).getReel().getDestinationY(), is(equalTo(40.0f)));
        assertThat(animatedReels.get(60).getReel().getDestinationY(), is(equalTo(80.0f)));
        assertThat(animatedReels.get(24).getReel().getDestinationY(), is(equalTo(120.0f)));
        assertThat(animatedReels.get(12).getReel().getDestinationY(), is(equalTo(160.0f)));
        assertThat(animatedReels.get( 0).getReel().getDestinationY(), is(equalTo(200.0f)));
    }

    private AnimatedReelsManager sendSwapReelsAboveMessage(
            Array<AnimatedReel> animatedReels,
            int reelBelow,
            int reelAbove) {
        Telegram message = new Telegram();
        message.message = MessageType.SwapReelsAboveMe.index;
        Array<AnimatedReel> reelsAB = new Array<>();
        reelsAB.add(animatedReels.get(reelBelow));
        reelsAB.add(animatedReels.get(reelAbove));
        message.extraInfo = reelsAB;
        AnimatedReelsManager animatedReelsManager = new AnimatedReelsManager(animatedReels);
        animatedReelsManager.handleMessage(message);
        return animatedReelsManager;
    }

    private void animatedReelsSetDestinationY(
            Array<AnimatedReel> animatedReels,
            int startIndex,
            float startY,
            int numberOfReels,
            int step,
            int reelHeight) {
        int count = 0;
        for (int index = startIndex; index < startIndex + numberOfReels*step ; index+=step) {
            animatedReels.get(index).getReel().setDestinationY(startY + count * reelHeight);
            count++;
        }
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


    private void assertAnimatedReelNotSwapped(
            Array<AnimatedReel> animatedReels,
            Array<AnimatedReel> swappedReelsAboveMeAnimatedReels,
            int currentReel) {
        assertThat(animatedReels.get(currentReel).getReel().getX(),
                is(equalTo(swappedReelsAboveMeAnimatedReels.get(currentReel).getReel().getX())));
        assertThat(animatedReels.get(currentReel).getReel().getY(),
                is(equalTo(swappedReelsAboveMeAnimatedReels.get(currentReel).getReel().getY())));
        assertThat(animatedReels.get(currentReel).getReel().getDestinationX(),
                is(equalTo(swappedReelsAboveMeAnimatedReels.get(currentReel).getReel().getDestinationX())));
        assertThat(animatedReels.get(currentReel).getReel().getDestinationY(),
                is(equalTo(swappedReelsAboveMeAnimatedReels.get(currentReel).getReel().getDestinationY())));
    }

    private void handleSwapReelsAboveMessage(
            Array<AnimatedReel> animatedReels,
            AnimatedReelsManager animatedReelsManager,
            int reelA,
            int reelB) {
        Telegram message = new Telegram();
        message.message = MessageType.SwapReelsAboveMe.index;
        Array<AnimatedReel> reelsAB = new Array<>();
        reelsAB.add(animatedReels.get(reelA));
        reelsAB.add(animatedReels.get(reelB));
        message.extraInfo = reelsAB;
        animatedReelsManager.handleMessage(message);
    }

    private void addAnimatedReel(Array<AnimatedReel> animatedReels, int x, int y) {
        AnimatedReel animatedReel = createAnimatedReel(x, y);
        animatedReel.getReel().setDestinationX(x);
        animatedReel.getReel().setDestinationY(y);
        animatedReels.add(animatedReel);
    }

    private void addAnimatedReelXYandDestXY(
            Array<AnimatedReel> animatedReels,
            int x,
            int y,
            int destX,
            int destY) {
        AnimatedReel animatedReel = createAnimatedReel(x, y);
        animatedReel.getReel().setDestinationX(destX);
        animatedReel.getReel().setDestinationY(destY);
        animatedReels.add(animatedReel);
    }

    private AnimatedReel createAnimatedReel(int x, int y) {
        return new AnimatedReel(
                null,
                x,
                y,
                40,
                40,
                40,
                40,
                0,
                null
        );
    }
}
