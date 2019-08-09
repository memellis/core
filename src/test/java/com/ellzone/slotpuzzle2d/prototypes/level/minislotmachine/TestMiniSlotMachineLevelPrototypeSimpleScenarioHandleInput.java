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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimpleScenario;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeSimpleScenario.class} )

public class TestMiniSlotMachineLevelPrototypeSimpleScenarioHandleInput {
    private static final String VIEWPORT_FIELD_NAME = "viewport";
    private static final String LEVEL_CREATOR_FIELD_NAME = "levelCreator";

    private MiniSlotMachineLevelPrototypeSimpleScenario partialMockMiniSlotMachineLevelPrototypeSimpleScenario;
    private Input mockInput;
    private Application mockApplication;
    private FitViewport mockViewPort;
    private LevelCreatorSimpleScenario levelCreatorSimpleScenarioMock;
    private Vector3 vector3Mock;
    private Capture<String> logCaptureArgument1, logCaptureArgument2;

    private void setUp() {
        setUpMocks();
        mockGdx();
        setUpCaptureArguments();
    }

    private void setUpMocks() {
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario = PowerMock.createNicePartialMock(MiniSlotMachineLevelPrototypeSimpleScenario.class, "processIsTileClicked");
        mockInput = createMock(Input.class);
        mockApplication = createMock(Application.class);
        mockViewPort = createMock(FitViewport.class);
        levelCreatorSimpleScenarioMock = createMock(LevelCreatorSimpleScenario.class);
        vector3Mock = createMock(Vector3.class);
    }

    private void setUpCaptureArguments() {
        logCaptureArgument1 = EasyMock.newCapture();
        logCaptureArgument2 = EasyMock.newCapture();
    }

    private void mockGdx() {
        Gdx.input = mockInput;
        Gdx.app = mockApplication;
    }

    private void tearDown() {
        tearDownMocks();
        tearDownGdx();
        tearDownCaptureArguments();
    }

    private void tearDownMocks() {
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario = null;
        mockInput = null;
        mockApplication = null;
        mockViewPort = null;
        levelCreatorSimpleScenarioMock = null;
        vector3Mock = null;
    }

    private void tearDownGdx() {
        Gdx.input = null;
        Gdx.app = null;
    }

    private void tearDownCaptureArguments() {
        logCaptureArgument1 = null;
        logCaptureArgument2 = null;
    }

    @Test
    public void testHandleInput() throws Exception {
        for (PlayStates playState : PlayStates.values())
            testHandleInputPlayStates(playState);
    }

    private void testHandleInputPlayStates(PlayStates playState) throws Exception {
        setUp();
        expectations(playState);
        replayAll();
        inokeHandleInput();
        assertThat(logCaptureArgument2.getValue(), CoreMatchers.equalTo(playState.toString()));
        verifyAll();
        tearDown();
    }

    private void verifyAll() {
        verify(mockInput,
               mockApplication,
                levelCreatorSimpleScenarioMock,
                partialMockMiniSlotMachineLevelPrototypeSimpleScenario);
    }

    private void inokeHandleInput() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, VIEWPORT_FIELD_NAME, mockViewPort);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, LEVEL_CREATOR_FIELD_NAME, levelCreatorSimpleScenarioMock);
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario.handleInput();
    }

    private void replayAll() {
        replay(mockInput,
               mockApplication,
                levelCreatorSimpleScenarioMock,
                partialMockMiniSlotMachineLevelPrototypeSimpleScenario);
    }

    private void expectations(PlayStates playState) throws Exception {
        expect(mockInput.justTouched()).andReturn(true);
        expect(mockInput.getX()).andReturn(10);
        expect(mockInput.getY()).andReturn(10);
        whenNew(Vector3.class).withArguments(10.0f, 10.0f, 0.0f).thenReturn(vector3Mock);
        expect(levelCreatorSimpleScenarioMock.getPlayState()).andReturn(playState);
        mockApplication.debug(capture(logCaptureArgument1), capture(logCaptureArgument2));
        if (playState == PlayStates.PLAYING)
            PowerMock.expectPrivate(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, "processIsTileClicked").atLeastOnce();
    }
}