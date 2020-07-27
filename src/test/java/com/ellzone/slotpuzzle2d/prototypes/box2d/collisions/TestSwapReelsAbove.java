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
        Array<AnimatedReel> animatedReels = prepareTestSwapReelsFallenWithThreeReels();

        AnimatedReelsManager animatedReelsManager = runTestSwapReelsFallen(
                animatedReels,
                96,
                72);

        assertTestSwapReelsFallenWithThreeReels(animatedReelsManager);
    }

    private Array<AnimatedReel> prepareTestSwapReelsFallenWithThreeReels() {
        Array<AnimatedReel> animatedReels =
                prepareMatrxBasedTest(SlotPuzzleMatrices.createMatrixWithThreeBoxes(), SCREEN_OFFSET);

        animatedReels.get(84).getReel().setDestinationY(80);
        animatedReels.get(84).getReel().setY(80 + SCREEN_OFFSET);
        animatedReels.get(84).getReel().deleteReelTile();
        animatedReels.get(96).getReel().setY(40);
        animatedReels.get(72).getReel().setY(80);
        animatedReels.get(60).getReel().setY(120);
        return animatedReels;
    }

    private Array<AnimatedReel> prepareMatrxBasedTest(int[][] matrixToCreateFrom, int screenOffset) {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        AnimatedReelsMatrixCreator animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        animatedReelsMatrixCreator.setSpriteWidth(40);
        animatedReelsMatrixCreator.setSpriteHeight(40);
        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                matrixToCreateFrom, screenOffset);
        return animatedReels;
    }

    private AnimatedReelsManager runTestSwapReelsFallen(
            Array<AnimatedReel> animatedReels,
            int reelBelow,
            int reelAbove) {
        return sendSwapReelsAboveMessage(animatedReels, reelBelow, reelAbove);
    }

    private void assertTestSwapReelsFallenWithThreeReels(AnimatedReelsManager animatedReelsManager) {
        Array<AnimatedReel> swappedReelsAboveAnimatedReels = animatedReelsManager.getAnimatedReels();

        assertThat(swappedReelsAboveAnimatedReels.get(72).getReel().getDestinationY(), is(equalTo(80.0f)));
        assertThat(swappedReelsAboveAnimatedReels.get(84).getReel().isReelTileDeleted(), is(true));
        assertThat(swappedReelsAboveAnimatedReels.get(96).getReel().getDestinationY(), is(equalTo(40.0f)));
    }

    @Test
    public void testSwapReelsFallenWithFourReels() {
        Array<AnimatedReel> animatedReels = prepareTestSwapReelsFallenWithFourReels();

        AnimatedReelsManager animatedReelsManager = runTestSwapReelsFallen(
                animatedReels,
                96,
                72);

        assertTestSwapReelsFallenWithFourReels(animatedReelsManager);
    }

    private Array<AnimatedReel> prepareTestSwapReelsFallenWithFourReels() {
        Array<AnimatedReel> animatedReels =
                prepareMatrxBasedTest(SlotPuzzleMatrices.createMatrixWithFourBoxes(), SCREEN_OFFSET);

        prepareAnimatedReelsForFallingWithFourReels(animatedReels);
        return animatedReels;
    }

    private void prepareAnimatedReelsForFallingWithFourReels(Array<AnimatedReel> animatedReels) {
        animatedReels.get(84).getReel().setDestinationY(80);
        animatedReels.get(84).getReel().setY(80 + SCREEN_OFFSET);
        animatedReels.get(84).getReel().deleteReelTile();

        animatedReels.get(96).getReel().setY(40);
        animatedReels.get(72).getReel().setY(80);
        animatedReels.get(60).getReel().setY(120);
        animatedReels.get(48).getReel().setY(160);
        animatedReels.get(48).getReel().setEndReel(0);
        animatedReels.get(48).getReel().unDeleteReelTile();
    }

    private Array<AnimatedReel> assertTestSwapReelsFallenWithFourReels(AnimatedReelsManager animatedReelsManager) {
        Array<AnimatedReel> swappedReelsAboveAnimatedReels = animatedReelsManager.getAnimatedReels();

        assertThat(swappedReelsAboveAnimatedReels.get(48).getReel().getDestinationY(), is(equalTo(160.0f)));
        assertThat(swappedReelsAboveAnimatedReels.get(60).getReel().getDestinationY(), is(equalTo(120.0f)));
        assertThat(swappedReelsAboveAnimatedReels.get(72).getReel().getDestinationY(), is(equalTo(80.0f)));
        assertThat(swappedReelsAboveAnimatedReels.get(84).getReel().isReelTileDeleted(), is(true));
        assertThat(swappedReelsAboveAnimatedReels.get(96).getReel().getDestinationY(), is(equalTo(40.0f)));
        return swappedReelsAboveAnimatedReels;
    }

    @Test
    public void testSwapReelsFallenWithFourReelsAndWithReelsStoppedFalling() {
        Array<AnimatedReel> animatedReels = prepareTestSwapReelsFallenWithFourReels();
        animatedReels.get(96).getReel().setIsFallen(true);

        AnimatedReelsManager animatedReelsManager = runTestSwapReelsWithFourReelsAndWithReelsStoppedFalling(animatedReels);

        assertTestSwappedReelsFallenWithFourReelsAndWithStoppedFalling(animatedReelsManager);
    }

    private AnimatedReelsManager runTestSwapReelsWithFourReelsAndWithReelsStoppedFalling(Array<AnimatedReel> animatedReels) {
        AnimatedReelsManager animatedReelsManager = new AnimatedReelsManager(animatedReels);
        animatedReelsManager.setNumberOfReelsToFall(3);
        sendSwapReelsAboveMessageWithAnimatedReelsManager(animatedReelsManager, animatedReels, 96, 72);
        return animatedReelsManager;
    }

    private void assertTestSwappedReelsFallenWithFourReelsAndWithStoppedFalling(AnimatedReelsManager animatedReelsManager) {
        Array<AnimatedReel> swappedReelsAboveAnimatedReels =
                assertTestSwapReelsFallenWithFourReels(animatedReelsManager);
        assertThat(swappedReelsAboveAnimatedReels.get(72).getReel().isFallen(), is(true));
        assertThat(swappedReelsAboveAnimatedReels.get(60).getReel().isFallen(), is(true));
        assertThat(animatedReelsManager.getNumberOfReelsToFall(), is(equalTo(0)));
    }

    @Test
    public void testSwapReelsFallenWithFillColumnNineBoxes() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        AnimatedReelsMatrixCreator animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                SlotPuzzleMatrices.createMatrixWithFourBoxes());

        AnimatedReelsManager animatedReelsManager = getAnimatedReelsManager(animatedReels, 96, 84);
        Array<AnimatedReel> swappedReelsAboveAnimatedReels = animatedReelsManager.getAnimatedReels();
        for (int currentReel = 0; currentReel < animatedReels.size; currentReel++)
            assertAnimatedReelNotSwapped(animatedReels, swappedReelsAboveAnimatedReels, currentReel);
    }

    private AnimatedReelsManager getAnimatedReelsManager(Array<AnimatedReel> animatedReels, int i, int i2) {
        return sendSwapReelsAboveMessage(animatedReels, i, i2);
    }

    @Test
    public void testSwapReelsFallenWithTwoByTwoReelsDeleted() {
        Array<AnimatedReel> animatedReels =
                prepareMatrxBasedTest(SlotPuzzleMatrices.createMatrixWithFillColumnNineBoxes(), 0);

        prepareTestWithDeletedBoxes84and72And60Hitting96(animatedReels);
        AnimatedReelsManager animatedReelsManager = getAnimatedReelsManager(animatedReels, 96, 60);
        prepareTestWith36And24Hitting60(animatedReels);
        sendSwapReelsAboveMessage(animatedReelsManager, animatedReels, 60, 24);
        animatedReelsManager.printSlotMatrix();
        assertTwoByTwoReelsDeleted(animatedReelsManager.getAnimatedReels());
    }

    @Test
    public void testSwapReelsFallenAvoidingDuplicateReels() {
        Array<AnimatedReel> animatedReels =
                prepareMatrxBasedTest(SlotPuzzleMatrices.createMatrixWithFillColumnNineBoxes(), 0);

        prepareTestWithDeleteReel(24, animatedReels);
        animatedReels.get(12).getReel().setY(280);
        animatedReels.get(0).getReel().setY(320);
        AnimatedReelsManager animatedReelsManager = getAnimatedReelsManager(animatedReels, 36, 12);

        Array<AnimatedReel> swappedReels = animatedReelsManager.getAnimatedReels();
        assertReelsColumnAvoidingDuplicateReels(swappedReels);
    }

    @Test
    public void testSwapReelsFallenAvoidingDuplicateReelsTopReelsDeleted() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        AnimatedReelsMatrixCreator animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                SlotPuzzleMatrices.createMatrixWithFillColumnNineBoxes());
        prepareTestWithDeleteReel(animatedReels, 0, 24, 36);
        animatedReels.get(12).getReel().setY(240);
        AnimatedReelsManager animatedReelsManager = getAnimatedReelsManager(animatedReels, 48, 12);
        Array<AnimatedReel> swappedReels = animatedReelsManager.getAnimatedReels();
        assertReelsColumnAvoidingDuplicatesTopReelDeleted(swappedReels);
    }

    @Test
    public void testSwapReelFallenAvoidDuplicateReelsFourReelsDeleted() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        AnimatedReelsMatrixCreator animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();

        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                SlotPuzzleMatrices.createMatrixWithFillColumnNineBoxes());
        prepareTestWithDeleteReel(animatedReels, 84, 72, 48, 36);
        animatedReels.get(60).getReel().setY(80);
        animatedReels.get(24).getReel().setY(120);
        animatedReels.get(12).getReel().setY(160);
        animatedReels.get(0).getReel().setY(200);
        AnimatedReelsManager animatedReelsManager = getAnimatedReelsManager(animatedReels, 96, 60);
        sendSwapReelsAboveMessage(animatedReels, 60, 24);
        Array<AnimatedReel> swappedReels = animatedReelsManager.getAnimatedReels();

        assertReelYDestionYIsDelete(swappedReels,  0, 200.0f, 200.0f, false);
        assertReelYDestionYIsDelete(swappedReels, 12, 160.0f, 160.0f, false);
        assertReelYDestionYIsDelete(swappedReels, 24, 120.0f, 120.0f, false);
        assertReelYDestionYIsDelete(swappedReels, 36, 760.0f, 360.0f, true);
        assertReelYDestionYIsDelete(swappedReels, 48, 720.0f, 320.0f, true);
        assertReelYDestionYIsDelete(swappedReels, 60,  80.0f,  80.0f, false);
        assertReelYDestionYIsDelete(swappedReels, 72, 680.0f, 280.0f, true);
        assertReelYDestionYIsDelete(swappedReels, 84, 640.0f, 240.0f, true);
        assertReelYDestionYIsDelete(swappedReels, 96,  40.0f,  40.0f, false);
    }

    @Test
    public void testDropReelOntoOneReel() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        AnimatedReelsMatrixCreator animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                SlotPuzzleMatrices.createMatrixWithOneBox());
        AnimatedReelsManager animatedReelsManager = new AnimatedReelsManager(animatedReels);
        animatedReels.get(0).getReel().setY(80);
        animatedReels.get(0).getReel().unDeleteReelTile();
        animatedReels.get(96).getReel().setIsFallen(true);
        animatedReels.get(96).getReel().setIsStoppedFalling(true);
        animatedReelsManager.setNumberOfReelsToFall(1);
        sendSwapReelsAboveMessageWithAnimatedReelsManager(animatedReelsManager, animatedReels,96, 0);
        Array<AnimatedReel> swappedReels = animatedReelsManager.getAnimatedReels();
        assertThat(swappedReels.get( 0).getReel().getY(), is(equalTo(80.0f)));
        assertThat(swappedReels.get( 0).getReel().getDestinationY(), is(equalTo(80.0f)));
        assertThat(swappedReels.get(96).getReel().getY(), is(equalTo(40.0f)));
        assertThat(swappedReels.get(96).getReel().getDestinationY(), is(equalTo(40.0f)));
        assertThat(animatedReelsManager.getNumberOfReelsToFall(), is(equalTo(0)));
    }

    private void assertReelsColumnAvoidingDuplicatesTopReelDeleted(Array<AnimatedReel> swappedReels) {
        assertReelYDestionYIsDelete(swappedReels, 0, 760.0f, 360.0f, true);
        assertReelYDestionYIsDelete(swappedReels, 12, 240.0f, 240.0f, false);
        assertReelYDestionYIsDelete(swappedReels, 24, 720.0f, 320.0f, true);
        assertReelYDestionYIsDelete(swappedReels, 36, 680.0f, 280.0f, true);
        assertReelYDestionYIsDelete(swappedReels, 48, 200.0f, 200.0f, false);
        assertReelYDestionYIsDelete(swappedReels, 60, 160.0f, 160.0f, false);
        assertReelYDestionYIsDelete(swappedReels, 72, 120.0f, 120.0f, false);
        assertReelYDestionYIsDelete(swappedReels, 84,  80.0f,  80.0f, false);
        assertReelYDestionYIsDelete(swappedReels, 96,  40.0f,  40.0f, false);
    }

    private void prepareTestWithDeleteReel(Array<AnimatedReel> animatedReels, int... reelsToDelete) {
        for (int reelToDelete=0; reelToDelete<reelsToDelete.length; reelToDelete++)
            animatedReels.get(reelsToDelete[reelToDelete]).getReel().deleteReelTile();
    }

    private void assertReelsColumnAvoidingDuplicateReels(Array<AnimatedReel> swappedReels) {
        assertReelYDestionYIsDelete(swappedReels, 0, 320.0f, 320.0f, false);
        assertReelYDestionYIsDelete(swappedReels, 12, 280.0f, 280.0f, false);
        assertReelYDestionYIsDelete(swappedReels, 24, 760.0f, 360.0f, true);
        assertReelYDestionYIsDelete(swappedReels, 36, 240.0f, 240.0f, false);
        assertReelYDestionYIsDelete(swappedReels, 48, 200.0f, 200.0f, false);
        assertReelYDestionYIsDelete(swappedReels, 60, 160.0f, 160.0f, false);
        assertReelYDestionYIsDelete(swappedReels, 72, 120.0f, 120.0f, false);
        assertReelYDestionYIsDelete(swappedReels, 84,  80.0f,  80.0f, false);
        assertReelYDestionYIsDelete(swappedReels, 96,  40.0f,  40.0f, false);
    }

    private void assertReelYDestionYIsDelete(
            Array<AnimatedReel> swappedReels,
            int reelIndex,
            float y,
            float destinatonY,
            boolean isDeleted) {
        assertThat(swappedReels.get(reelIndex).getReel().getY(), is(equalTo(y)));
        assertThat(swappedReels.get(reelIndex).getReel().getDestinationY(), is(equalTo(destinatonY)));
        assertThat(swappedReels.get(reelIndex).getReel().isReelTileDeleted(), is(isDeleted));
    }

    private void prepareTestWithDeleteReel(int reelToDelete, Array<AnimatedReel> animatedReels) {
        animatedReels.get(reelToDelete).getReel().deleteReelTile();
    }

    private void prepareTestWithDeletedBoxes84and72And60Hitting96(
            Array<AnimatedReel> animatedReels) {
        animatedReels.get(84).getReel().deleteReelTile();
        animatedReels.get(72).getReel().deleteReelTile();
        animatedReels.get(48).getReel().deleteReelTile();
        animatedReels.get(36).getReel().deleteReelTile();
        animatedReels.get(60).getReel().setY(80);
    }

    private void assertTwoByTwoReelsDeleted(Array<AnimatedReel> animatedReels) {
        assertReelYDestionYIsDelete(animatedReels,  0, 200.0f, 200.0f, false);
        assertReelYDestionYIsDelete(animatedReels, 12, 160.0f, 160.0f, false);
        assertReelYDestionYIsDelete(animatedReels, 24, 120.0f, 120.0f, false);
        assertReelYDestionYIsDelete(animatedReels, 36, 760.0f, 360.0f, true);
        assertReelYDestionYIsDelete(animatedReels, 48, 720.0f, 320.0f, true);
        assertReelYDestionYIsDelete(animatedReels, 60,  80.0f,  80.0f, false);
        assertReelYDestionYIsDelete(animatedReels, 72, 680.0f, 280.0f, true);
        assertReelYDestionYIsDelete(animatedReels, 84, 640.0f, 240.0f, true);
        assertReelYDestionYIsDelete(animatedReels, 96,  40.0f,  40.0f, false);
    }

    private void prepareTestWith36And24Hitting60(
            Array<AnimatedReel> animatedReels) {
        animatedReels.get(24).getReel().setY(120);
        animatedReels.get(12).getReel().setY(160);
        animatedReels.get(0).getReel().setY(200);
    }

    private void sendSwapReelsAboveMessageWithAnimatedReelsManager(
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

    private AnimatedReelsManager sendSwapReelsAboveMessage(
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
