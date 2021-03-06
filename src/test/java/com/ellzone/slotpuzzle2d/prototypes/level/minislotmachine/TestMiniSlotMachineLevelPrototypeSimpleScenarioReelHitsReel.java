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

package com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimpleScenario;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.captureInt;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.createNicePartialMock;
import static org.powermock.api.easymock.PowerMock.expectPrivate;
import static org.powermock.api.easymock.PowerMock.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeSimpleScenario.class,
                  PuzzleGridTypeReelTile.class,
                  PhysicsManagerCustomBodies.class} )

public class TestMiniSlotMachineLevelPrototypeSimpleScenarioReelHitsReel {
    private static final String REELBOXES_FIELD_NAME = "reelBoxes";
    private static final String LEVEL_CREATOR_FIELD_NAME = "levelCreator";

    private MiniSlotMachineLevelPrototypeSimpleScenario partialMockMiniSlotMachineLevelPrototypeSimpleScenario;
    private ReelTile reelTileAMock, reelTileBMock;
    private Array reelBoxesMock;
    private Body reelBoxMock;
    private Capture<Float> captureReelSetY;
    private LevelCreatorSimpleScenario levelCreatorMock;
    private Capture<ReelTile> reelTileCaptureArg1, reelTileCaptureArg2;
    private Capture<Integer> rACapture, cACapture, rBCapture, cBCapture;

    @Before
    public void setUp() {
        setUpPowerMocks();
        setUpEasyMocks();
        setUpCaptures();
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario = createNicePartialMock(MiniSlotMachineLevelPrototypeSimpleScenario.class,
                "swapReelsAboveMe",
                              "reelsLeftToFall",
                              "processTileHittingTile");
        mockStatic(PuzzleGridTypeReelTile.class);
        mockStatic(PhysicsManagerCustomBodies.class);
    }

    private void setUpEasyMocks() {
        reelTileAMock = createMock(ReelTile.class);
        reelTileBMock = createMock(ReelTile.class);
        reelBoxesMock = createMock(Array.class);
        reelBoxMock = createMock(Body.class);
        levelCreatorMock = createMock(LevelCreatorSimpleScenario.class);
    }

    private void setUpCaptures(){
        captureReelSetY = EasyMock.newCapture();
        reelTileCaptureArg1 = EasyMock.newCapture();
        reelTileCaptureArg2 = EasyMock.newCapture();
        rACapture = EasyMock.newCapture();
        cACapture = EasyMock.newCapture();
        rBCapture = EasyMock.newCapture();
        cBCapture = EasyMock.newCapture();
    }

    @After
    public void tearDown() {
        tearDownPowerMocks();
        tearDownEasyMocks();
        tearDownCaptures();
    }

    private void tearDownPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario = null;
    }

    private void tearDownEasyMocks() {
        reelTileAMock = null;
        reelTileBMock = null;
        reelBoxesMock = null;
        reelBoxMock = null;
        levelCreatorMock = null;
    }

    private void tearDownCaptures() {
        captureReelSetY = null;
        reelTileCaptureArg1 = null;
        reelTileCaptureArg2 = null;
        rACapture = null;
        cACapture = null;
        rBCapture = null;
        cBCapture = null;
    }

    @Test
    public void testDealWithReelHitsReel_whenReelAAboveReelBByOneReel() throws Exception {
        setFields();
        setExpectations();
        replayAll();
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario.dealWithReelTileHittingReelTile(reelTileAMock, reelTileBMock);
        assertions(3,2, 2,2 );
        verifyAll();
    }

    @Test
    public void testDealWithReelHitsReel_whenReelAAboveReelBGreaterThanOneReel() throws Exception {
        setFields();
        setExpectationsWhenReelAAboveReelBGreaterThanOne();
        replayAll();
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario.dealWithReelTileHittingReelTile(reelTileAMock, reelTileBMock);
        assertions(3, 2,1, 2);
        verifyAll();
    }

    private void setExpectationsWhenReelAAboveReelBGreaterThanOne() throws Exception {
        setExpectsRowColumn(3, 2, 1, 2);
        setExpectsFlashing();
    }

    private void setFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, REELBOXES_FIELD_NAME, reelBoxesMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, LEVEL_CREATOR_FIELD_NAME, levelCreatorMock);
    }

    private void setExpectations() throws Exception {
        setExpectsRowColumn(3, 2, 2,2 );
        setExpectsFlashing();
    }

    private void setExpectsRowColumn(int rA, int cA, int rB, int cB) {
        expect(PuzzleGridTypeReelTile.getRowFromLevel(10.0f, 9)).andReturn(rA);
        expect(reelTileAMock.getDestinationY()).andReturn(10.0f);
        expect(PuzzleGridTypeReelTile.getColumnFromLevel(10.0f)).andReturn(cA);
        expect(reelTileAMock.getDestinationX()).andReturn(10.0f);
        expect(PuzzleGridTypeReelTile.getRowFromLevel(10.0f, 9)).andReturn(rB);
        expect(reelTileBMock.getDestinationY()).andReturn(10.0f);
        expect(PuzzleGridTypeReelTile.getColumnFromLevel(10.0f)).andReturn(cB);
        expect(reelTileBMock.getDestinationX()).andReturn(10.0f);
    }

    private void setExpectsFlashing() throws Exception {
        expect(levelCreatorMock.getPlayState()).andReturn(PlayStates.INTRO_FLASHING).times(2);
        expectPrivate(partialMockMiniSlotMachineLevelPrototypeSimpleScenario,
                "processTileHittingTile",
                            capture(reelTileCaptureArg1),
                            capture(reelTileCaptureArg2),
                            captureInt(rACapture),
                            captureInt(cACapture),
                            captureInt(rBCapture),
                            captureInt(cBCapture));
    }

    private void replayAll() {
        PowerMock.replay(PuzzleGridTypeReelTile.class,
                partialMockMiniSlotMachineLevelPrototypeSimpleScenario,
                         reelBoxesMock,
                         reelTileAMock,
                         reelTileBMock,
                         PhysicsManagerCustomBodies.class,
                         levelCreatorMock);
    }

    private void verifyAll() {
        PowerMock.verify(PuzzleGridTypeReelTile.class,
                partialMockMiniSlotMachineLevelPrototypeSimpleScenario,
                         reelBoxesMock,
                         reelTileAMock,
                         reelTileBMock,
                         PhysicsManagerCustomBodies.class,
                         levelCreatorMock);
    }

    private void assertions(int rA, int cA, int rB, int cB) {
        assertThat(reelTileCaptureArg1.getValue(), is(equalTo(reelTileAMock)));
        assertThat(reelTileCaptureArg2.getValue(), is(equalTo(reelTileBMock)));
        assertThat(rACapture.getValue(), is(equalTo(rA)));
        assertThat(cACapture.getValue(), is(equalTo(cA)));
        assertThat(rBCapture.getValue(), is(equalTo(rB)));
        assertThat(cBCapture.getValue(), is(equalTo(cB)));
    }
}
