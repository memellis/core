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

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {PlayStateMachine.class} )
public class PlayStateMachineTest {

    @Test
    public void testPlayDefaultConstructor() throws Exception {
        PlayStateMachine playStateMachine = new PlayStateMachine();
        PlayInterface concretePlay = Whitebox.getInternalState(playStateMachine, "concretePlay");
        assertThat(concretePlay.getClass().getSimpleName(), equalTo(RealPlay.class.getSimpleName()));
    }

    @Test
    public void testPlayWithOneConstructor() throws Exception {
        PlayStateMachine playStateMachine = new PlayStateMachine(PlaySimulator.class.getSimpleName());
        PlayInterface concretePlay = Whitebox.getInternalState(playStateMachine, "concretePlay");
        assertThat(concretePlay.getClass().getSimpleName(), equalTo(PlaySimulator.class.getSimpleName()));
    }
}
