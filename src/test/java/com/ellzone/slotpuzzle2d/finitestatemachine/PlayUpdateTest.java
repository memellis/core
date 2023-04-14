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
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {PlayStateMachine.class} )

public class PlayUpdateTest {
    @Test
    public void testUpdate() {
        PlayFactory playFactoryMock = createMock(PlayFactory.class);
        PlaySimulator playSimulatorMock = createMock(PlaySimulator.class);
        StateMachine stateMachineMock = createMock(StateMachine.class);

        PlayStateMachine playStateMachine = new PlayStateMachine();

        expect(playFactoryMock.getPlay(PlaySimulator.class.getSimpleName(), playStateMachine)).andReturn(playSimulatorMock);
        playStateMachine.update();
        stateMachineMock.update();
        playStateMachine.getConcretePlay().updateState(0.0f);
        expectLastCall().atLeastOnce();
        replay(PlayStateMachine.class, playFactoryMock, PlayFactory.class);

        assertThat(playFactoryMock.getPlay(PlaySimulator.class.getSimpleName(), playStateMachine), CoreMatchers.<PlayInterface>equalTo(playSimulatorMock));

        verify(playFactoryMock);
    }
}
