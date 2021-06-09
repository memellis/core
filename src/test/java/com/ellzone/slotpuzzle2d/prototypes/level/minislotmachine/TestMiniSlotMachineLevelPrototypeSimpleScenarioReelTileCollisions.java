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

import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimpleScenario;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.captureBoolean;
import static org.easymock.EasyMock.captureInt;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.createNicePartialMock;
import static org.powermock.api.easymock.PowerMock.expectPrivate;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeSimpleScenario.class, PuzzleGridTypeReelTile.class} )

public class TestMiniSlotMachineLevelPrototypeSimpleScenarioReelTileCollisions {
    private static final String LEVEL_CREATOR_FIELD_NAME = "levelCreator";

    private MiniSlotMachineLevelPrototypeSimpleScenario partialMockMiniSlotMachineLevelPrototypeSimpleScenario;
    private ReelTile reelTileMock;
    private LevelCreatorSimpleScenario levelCreatorSimpleScenarioMock;
    private Capture<Boolean> captureLevelCreatorSetHitSinkBottomArgument;
    private Capture<ReelTile> captureSwapReelsAboveArgument;
    private Capture<Integer> captureReelsLeftToFallArgumentRow, captureReelsLeftToFallArgumentCol;

    @Before
    public void setUp() {
        setUpPowerMocks();
        setUpEasyMocks();
        setUpCapture();
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario = createNicePartialMock(MiniSlotMachineLevelPrototypeSimpleScenario.class,
                "swapReelsAboveMe",
                              "reelsLeftToFall");
        mockStatic(PuzzleGridTypeReelTile.class);
    }

    private void setUpEasyMocks() {
        reelTileMock = createMock(ReelTile.class);
        levelCreatorSimpleScenarioMock = createMock(LevelCreatorSimpleScenario.class);
    }

    private void setUpCapture() {
        captureLevelCreatorSetHitSinkBottomArgument = EasyMock.newCapture();
        captureSwapReelsAboveArgument = EasyMock.newCapture();
        captureReelsLeftToFallArgumentRow = EasyMock.newCapture();
        captureReelsLeftToFallArgumentCol = EasyMock.newCapture();
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
        reelTileMock = null;
        levelCreatorSimpleScenarioMock = null;
    }

    private void tearDownCaptures() {
        captureLevelCreatorSetHitSinkBottomArgument = null;
        captureSwapReelsAboveArgument = null;
        captureReelsLeftToFallArgumentRow = null;
        captureReelsLeftToFallArgumentCol = null;
    }

    @Test
    public void testDealWithHitSinkBottomIntroSpinning() throws Exception {
        setFields();
        setExpects(PlayStates.INTRO_SPINNING);
        replayAll();
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario.dealWithHitSinkBottom(reelTileMock);
        assertThat(captureLevelCreatorSetHitSinkBottomArgument.getValue(), is(true));
        verifyAll();
    }

    @Test
    public void testDealWithHitSinkBottomReelsFlashing() throws Exception {
        setFields();
        setExpects(PlayStates.INTRO_FLASHING);
        replayAll();
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario.dealWithHitSinkBottom(reelTileMock);
        assertThat(captureSwapReelsAboveArgument.getValue(), is(reelTileMock));
        assertThat(captureReelsLeftToFallArgumentRow.getValue(), is(2));
        assertThat(captureReelsLeftToFallArgumentCol.getValue(), is(2));
        verifyAll();
    }

    private void setFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, LEVEL_CREATOR_FIELD_NAME, levelCreatorSimpleScenarioMock);
    }

    private void setExpects(PlayStates playState) throws Exception {
        expect(levelCreatorSimpleScenarioMock.getPlayState()).andReturn(playState);
        if (playState == PlayStates.INTRO_SPINNING)
            levelCreatorSimpleScenarioMock.setHitSinkBottom(captureBoolean(captureLevelCreatorSetHitSinkBottomArgument));

        expect(levelCreatorSimpleScenarioMock.getPlayState()).andReturn(playState);
        expect(levelCreatorSimpleScenarioMock.getPlayState()).andReturn(playState);
        expectIsFlashing(playState);
    }

    private void expectIsFlashing(PlayStates playState) throws Exception {
        if ((playState == PlayStates.INTRO_FLASHING) |
            (playState == PlayStates.REELS_FLASHING))
            expectFlashing();
    }

    private void expectFlashing() throws Exception {
        expectrc();
        expectSwapReelsAboveMe();
    }

    private void expectSwapReelsAboveMe() throws Exception {
        expect(levelCreatorSimpleScenarioMock.findReel(10, 120)).andReturn(0);
        expect(reelTileMock.getDestinationX()).andReturn(10.0f);
        expectPrivate(partialMockMiniSlotMachineLevelPrototypeSimpleScenario,
                     "swapReelsAboveMe",
                      capture(captureSwapReelsAboveArgument));
        expectPrivate(partialMockMiniSlotMachineLevelPrototypeSimpleScenario,
                     "reelsLeftToFall",
                      captureInt(captureReelsLeftToFallArgumentRow),
                      captureInt(captureReelsLeftToFallArgumentCol));
    }

    private void expectrc() {
        expect(PuzzleGridTypeReelTile.getRowFromLevel(10.0f, 9)).andReturn(2);
        expect(reelTileMock.getDestinationY()).andReturn(10.0f);
        expect(PuzzleGridTypeReelTile.getColumnFromLevel(10.0f)).andReturn(2);
        expect(reelTileMock.getDestinationX()).andReturn(10.0f);
    }

    private void replayAll() {
        replay(PuzzleGridTypeReelTile.class,
                partialMockMiniSlotMachineLevelPrototypeSimpleScenario,
               reelTileMock,
                levelCreatorSimpleScenarioMock);
    }

    private void verifyAll() {
        verify(PuzzleGridTypeReelTile.class,
                partialMockMiniSlotMachineLevelPrototypeSimpleScenario,
               reelTileMock,
                levelCreatorSimpleScenarioMock);
    }
}
