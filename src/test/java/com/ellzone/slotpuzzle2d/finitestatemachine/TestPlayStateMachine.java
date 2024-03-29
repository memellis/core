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

package com.ellzone.slotpuzzle2d.finitestatemachine;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static junit.framework.TestCase.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;

public class TestPlayStateMachine {
    private Input mockInput;
    private Application mockApplication;

    @BeforeEach
    public void setUp() {
        setUpMocks();
        mockGdx();
    }

    private void setUpMocks() {
        mockInput = createMock(Input.class);
        mockApplication = createMock(Application.class);
    }

    private void mockGdx() {
        Gdx.input = mockInput;
        Gdx.app = mockApplication;
    }

    @AfterEach
    public  void tearDown() {
        tearDownMocks();
        tearDownGdx();
    }

    private void tearDownMocks() {
        Gdx.input = null;
        Gdx.app = null;
    }

    private void tearDownGdx() {
        mockInput = null;
        mockApplication = null;
    }

    // Meeds a a relook
    @Test
    public void testIntroDropPlay() {
        PlayStateMachine playStateMachine = new PlayStateMachine(PlaySimulator.class.getSimpleName());
        assertEquals(playStateMachine.getStateMachine().getCurrentState(), PlayState.INTRO_FALLING_SEQUENCE);

        playUpdateUntilStopped(playStateMachine);
        assertEquals(playStateMachine.getStateMachine().getCurrentState(), PlayState.INTRO_SPINNING_SEQUENCE);

        playUpdateUntilStopped(playStateMachine);
        assertEquals(playStateMachine.getStateMachine().getCurrentState(), PlayState.INTRO_FLASHING_SEQUENCE);

        playUpdateUntilStopped(playStateMachine);
        assertEquals(playStateMachine.getStateMachine().getCurrentState(), PlayState.INTRO_ENDING_SEQUENCE);

        playUpdateUntilStopped(playStateMachine);
        assertEquals(playStateMachine.getStateMachine().getCurrentState(), PlayState.PLAY);
    }

    // Meeds a a relook
    @Test
    public void testIntroDropDropPlay() {
        PlayStateMachine playStateMachine = new PlayStateMachine(PlaySimulator.class.getSimpleName());
        assertEquals(playStateMachine.getStateMachine().getCurrentState(), PlayState.INTRO_FALLING_SEQUENCE);

        playUpdateUntilStopped(playStateMachine);
        assertEquals(playStateMachine.getStateMachine().getCurrentState(), PlayState.INTRO_SPINNING_SEQUENCE);

        playUpdateUntilStopped(playStateMachine);
        assertEquals(playStateMachine.getStateMachine().getCurrentState(), PlayState.INTRO_FLASHING_SEQUENCE);

        playUpdateUntilStopped(playStateMachine);
        assertEquals(playStateMachine.getStateMachine().getCurrentState(), PlayState.INTRO_ENDING_SEQUENCE);

        playUpdateUntilStopped(playStateMachine);
        assertEquals(playStateMachine.getStateMachine().getCurrentState(), PlayState.PLAY);
    }


    private void playUpdateUntilStopped(PlayStateMachine playStateMachine) {
        playStateMachine.getConcretePlay().start();
        while (!playStateMachine.getConcretePlay().isStopped()) {
            try {
                playStateMachine.update();
                Thread.sleep(10);
            } catch (InterruptedException ie) {
                System.out.println("oops I've been interrupted:" + ie.getMessage() + "Stacktrace:" + ie.getStackTrace());
            }
        }
        playStateMachine.update();
    }
}
