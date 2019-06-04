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

import com.badlogic.gdx.ai.fsm.StateMachine;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.expect;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {PlayStateMachine.class} )
public class TestPlayState {
    private PlayStateMachine playStateMachineMock;
    private StateMachine stateMachineMock;
    private PlaySimulator playSimulatorMock;

    @Before
    public void setUp() {
        playStateMachineMock = PowerMock.createMock(PlayStateMachine.class);
        stateMachineMock = PowerMock.createMock(StateMachine.class);
        playSimulatorMock = PowerMock.createMock(PlaySimulator.class);
    }

    @After
    public void tearDown() {
        playStateMachineMock = null;
        stateMachineMock = null;
        playSimulatorMock = null;
    }

    @Test
    public void testPlayStateIntroFallingSequence()  {
        expect(playStateMachineMock.getConcretePlay()).andReturn(playSimulatorMock);
        expect(playSimulatorMock.areReelsFalling()).andReturn(false);
        expect(playStateMachineMock.getStateMachine()).andReturn(stateMachineMock);
        stateMachineMock.changeState(PlayState.INTRO_SPINNING_SEQUENCE);
        PowerMock.expectLastCall();
        replayAll();
        PlayState.INTRO_FALLING_SEQUENCE.update(playStateMachineMock);
        verifyAll();
    }

    @Test
    public void testPlayStateIntroSpinningSequence() {
        expect(playStateMachineMock.getConcretePlay()).andReturn(playSimulatorMock).atLeastOnce();
        expect(playSimulatorMock.areReelsSpinning()).andReturn(false);
        expect(playStateMachineMock.getStateMachine()).andReturn(stateMachineMock);
        stateMachineMock.changeState(PlayState.INTRO_FLASHING_SEQUENCE);
        PowerMock.expectLastCall();
        playSimulatorMock.setReelsAreFlashing(true);
        replayAll();
        PlayState.INTRO_SPINNING_SEQUENCE.update(playStateMachineMock);
        verifyAll();
    }

    @Test
    public void testPlayStateIntroFlashingSequence() {
        expect(playStateMachineMock.getConcretePlay()).andReturn(playSimulatorMock).times(2);
        expect(playSimulatorMock.areReelsStartedFlashing()).andReturn(true);
        expect(playSimulatorMock.areReelsFlashing()).andReturn(false);
        expect(playStateMachineMock.getStateMachine()).andReturn(stateMachineMock);
        stateMachineMock.changeState(PlayState.INTRO_ENDING_SEQUENCE);
        PowerMock.expectLastCall();
        replayAll();
        PlayState.INTRO_FLASHING_SEQUENCE.update(playStateMachineMock);
        verifyAll();
    }

    @Test
    public void testPlayStateIntroEndingSequence() {
        expect(playStateMachineMock.getConcretePlay()).andReturn(playSimulatorMock);
        expect(playSimulatorMock.areReelsDeleted()).andReturn(false);
        expect(playStateMachineMock.getStateMachine()).andReturn(stateMachineMock);
        stateMachineMock.changeState(PlayState.PLAY);
        PowerMock.expectLastCall();
        replayAll();
        PlayState.INTRO_ENDING_SEQUENCE.update(playStateMachineMock);
        verifyAll();
    }

    @Test
    public void testPlayStateDropSequence() {
        expect(playStateMachineMock.getConcretePlay()).andReturn(playSimulatorMock);
        expect(playSimulatorMock.areReelsFalling()).andReturn(false);
        expect(playStateMachineMock.getStateMachine()).andReturn(stateMachineMock);
        stateMachineMock.changeState(PlayState.SPIN);
        PowerMock.expectLastCall();
        replayAll();
        PlayState.DROP.update(playStateMachineMock);
        verifyAll();
    }

    @Test
    public void testPlayStateSpinSequence() {
        expect(playStateMachineMock.getConcretePlay()).andReturn(playSimulatorMock);
        expect(playSimulatorMock.areReelsSpinning()).andReturn(false);
        expect(playStateMachineMock.getStateMachine()).andReturn(stateMachineMock);
        stateMachineMock.changeState(PlayState.FLASH);
        PowerMock.expectLastCall();
        replayAll();
        PlayState.SPIN.update(playStateMachineMock);
        verifyAll();
    }

    @Test
    public void testPlayStateFlashtoPlaySequence() {
        expect(playStateMachineMock.getConcretePlay()).andReturn(playSimulatorMock);
        expect(playSimulatorMock.areReelsFlashing()).andReturn(false);
        expect(playStateMachineMock.getConcretePlay()).andReturn(playSimulatorMock);
        expect(playSimulatorMock.getNumberOfReelsMatched()).andReturn(0);
        expect(playStateMachineMock.getStateMachine()).andReturn(stateMachineMock);
        stateMachineMock.changeState(PlayState.PLAY);
        PowerMock.expectLastCall();
        replayAll();
        PlayState.FLASH.update(playStateMachineMock);
        verifyAll();
    }

    @Test
    public void testPlayStateFlashtoDropSequence() {
        expect(playStateMachineMock.getConcretePlay()).andReturn(playSimulatorMock);
        expect(playSimulatorMock.areReelsFlashing()).andReturn(false);
        expect(playStateMachineMock.getConcretePlay()).andReturn(playSimulatorMock);
        expect(playSimulatorMock.getNumberOfReelsMatched()).andReturn(1);
        expect(playStateMachineMock.getConcretePlay()).andReturn(playSimulatorMock);
        expect(playSimulatorMock.areReelsFlashing()).andReturn(false);
        expect(playStateMachineMock.getStateMachine()).andReturn(stateMachineMock);
        expect(playStateMachineMock.getConcretePlay()).andReturn(playSimulatorMock);
        expect(playSimulatorMock.getNumberOfReelsMatched()).andReturn(1);
        stateMachineMock.changeState(PlayState.DROP);
        PowerMock.expectLastCall();
        replayAll();
        PlayState.FLASH.update(playStateMachineMock);
        verifyAll();
    }

    private void replayAll() {
        PowerMock.replay(stateMachineMock, playSimulatorMock, playStateMachineMock, PlayStateMachine.class);
    }

    private void verifyAll() {
        PowerMock.verify(stateMachineMock, playSimulatorMock, playSimulatorMock, PlayStateMachine.class);
    }
}