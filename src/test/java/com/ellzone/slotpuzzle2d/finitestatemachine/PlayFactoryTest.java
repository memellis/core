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

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {PlayStateMachine.class} )

public class PlayFactoryTest {
    @Test
    public void testPlayFactory() throws Exception {
        PlayFactory playFactoryMock = createMock(PlayFactory.class);
        RealPlay realPlayMock = createMock(RealPlay.class);
        PlaySimulator playSimulatorMock = createMock(PlaySimulator.class);

        PlayStateMachine play = new PlayStateMachine();

        expect(playFactoryMock.getPlay(RealPlay.class.getSimpleName(), play)).andReturn(realPlayMock);
        expect(playFactoryMock.getPlay(PlaySimulator.class.getSimpleName(), play)).andReturn(playSimulatorMock);
        expect(playFactoryMock.getPlay("", play)).andReturn(null);
        expect(playFactoryMock.getPlay(null, play)).andReturn(null);

        replay(PlayStateMachine.class, playFactoryMock, PlayFactory.class);
        assertThat(playFactoryMock.getPlay(RealPlay.class.getSimpleName(), play), CoreMatchers.<PlayInterface>equalTo(realPlayMock));
        assertThat(playFactoryMock.getPlay(PlaySimulator.class.getSimpleName(), play), CoreMatchers.<PlayInterface>equalTo(playSimulatorMock));
        assertNull(playFactoryMock.getPlay("", play));
        assertNull(playFactoryMock.getPlay(null, play));
        verify(PlayStateMachine.class, playFactoryMock, PlayFactory.class);
    }
}
