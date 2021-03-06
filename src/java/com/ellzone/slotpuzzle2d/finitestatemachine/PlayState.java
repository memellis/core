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

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

/*
Search for PlanetUML with your favourite Internet earch engine.

@startuml

state Intro {
[*] --> IntroFallingSequence
IntroFallingSequence -> IntroSpinningSequence : NumberOfReelsFalling == 0
IntroSpinningSequence --> IntroFlashingSequence : NumberOfReelsSpinning == 0
IntroFlashingSequence --> IntroEndingSequence: NumberOfFlashingReels > 0
IntroEndingSequence --> Drop : NumberOfMatchedReels == 0
IntroFlashingSequence --> Drop : NumberOfReelsFlashing == 0
IntroFlashingSequence --> Play : NumberOfReelsDeleted == 0
}

state Drop {
Drop --> Spin : NumberOfReelsSFalling == 0
Spin -> Flash : NumberOfReelsSpinning == 0
Flash --> Drop : (NumberOfReelsFlashing == 0) & (NumberOfReelsDeleted == 0) & (NumberOfMatchedReeks > 0)
Flash --> Play : (NumberOfReelsFlashing == 0) & (NumberOfReelsDeleted == 0) & (NumberOfMatchedReels == 0)
}

State Play {
}

@enduml
 */

public enum PlayState implements State<PlayStateMachine> {

    INITIALISING() {
        @Override
        public void enter(PlayStateMachine entity) {
        }

        @Override
        public void update(PlayStateMachine entity) {
        }

        @Override
        public void exit(PlayStateMachine entity) {
        }

        @Override
        public boolean onMessage(PlayStateMachine entity, Telegram telegram) {
            return false;
        }
    },

    INTRO_FALLING_SEQUENCE() {
        @Override
        public void enter(PlayStateMachine playStateMachine) {
        }

        @Override
        public void update(PlayStateMachine playStateMachine) {
            if (!playStateMachine.getConcretePlay().areReelsFalling()) {
                playStateMachine.getStateMachine().changeState(INTRO_SPINNING_SEQUENCE);
            }
        }

        @Override
        public void exit(PlayStateMachine playStateMachine) {
        }

        @Override
        public boolean onMessage(PlayStateMachine playStateMachine, Telegram telegram) {
            return false;
        }
    },

    INTRO_SPINNING_SEQUENCE() {
        @Override
        public void enter(PlayStateMachine playStateMachine) {
        }

        @Override
        public void update(PlayStateMachine playStateMachine) {
            if (!playStateMachine.getConcretePlay().areReelsSpinning()) {
                playStateMachine.getConcretePlay().setReelsAreFlashing(true);
                playStateMachine.getStateMachine().changeState(INTRO_FLASHING_SEQUENCE);
            }
        }

        @Override
        public void exit(PlayStateMachine playStateMachine) {
        }

        @Override
        public boolean onMessage(PlayStateMachine playStateMachine, Telegram telegram) {
            return false;
        }
    },

    INTRO_FLASHING_SEQUENCE() {
        @Override
        public void enter(PlayStateMachine playStateMachine) {
        }

        @Override
        public void update(PlayStateMachine playStateMachine) {
             if (playStateMachine.getConcretePlay().areReelsStartedFlashing() ||
                playStateMachine.getConcretePlay().isFinishedMatchingSlots())
                if (!playStateMachine.getConcretePlay().areReelsFlashing())
                    playStateMachine.getStateMachine().changeState(INTRO_ENDING_SEQUENCE);
        }

        @Override
        public void exit(PlayStateMachine playStateMachine) {
        }

        @Override
        public boolean onMessage(PlayStateMachine playStateMachine, Telegram telegram) {
            return false;
        }
    },

    INTRO_ENDING_SEQUENCE() {
        @Override
        public void enter(PlayStateMachine playStateMachine) {
        }

        @Override
        public void update(PlayStateMachine playStateMachine) {
            if (!playStateMachine.getConcretePlay().areReelsDeleted()) {
                playStateMachine.getStateMachine().changeState(PLAY);
            }
        }

        @Override
        public void exit(PlayStateMachine playStateMachine) {

        }

        @Override
        public boolean onMessage(PlayStateMachine playStateMachine, Telegram telegram) {
            return false;
        }
    },

    DROP() {
        @Override
        public void enter(PlayStateMachine playStateMachine) {
        }

        @Override
        public void update(PlayStateMachine playStateMachine) {
            if (!playStateMachine.getConcretePlay().areReelsFalling()) {
                playStateMachine.getStateMachine().changeState(SPIN);
            }
        }

        @Override
        public void exit(PlayStateMachine playStateMachine) {
        }

        @Override
        public boolean onMessage(PlayStateMachine playStateMachine, Telegram telegram) {
            return false;
        }

    },

    SPIN() {
        @Override
        public void enter(PlayStateMachine playStateMachine) {
        }

        @Override
        public void update(PlayStateMachine playStateMachine) {
            if (!playStateMachine.getConcretePlay().areReelsSpinning()) {
                playStateMachine.getStateMachine().changeState(FLASH);
            }
        }

        @Override
        public void exit(PlayStateMachine playStateMachine) {
        }

        @Override
        public boolean onMessage(PlayStateMachine playStateMachine, Telegram telegram) {
            return false;
        }
    },

    FLASH() {
        @Override
        public void enter(PlayStateMachine playStateMachine) {
        }

        @Override
        public void update(PlayStateMachine playStateMachine) {
            if (!playStateMachine.getConcretePlay().areReelsFlashing() & playStateMachine.getConcretePlay().getNumberOfReelsMatched() == 0) {
                playStateMachine.getStateMachine().changeState(PLAY);
            } else {
                if (!playStateMachine.getConcretePlay().areReelsFlashing() & playStateMachine.getConcretePlay().getNumberOfReelsMatched() > 0) {
                    playStateMachine.getStateMachine().changeState(DROP);
                }
            }
        }

        @Override
        public void exit(PlayStateMachine playStateMachine) {
        }

        @Override
        public boolean onMessage(PlayStateMachine playStateMachine, Telegram telegram) {
            return false;
        }
    },

    PLAY() {
        @Override
        public void enter(PlayStateMachine playStateMachine) {
        }

        @Override
        public void update(PlayStateMachine playStateMachine) {
        }

        @Override
        public void exit(PlayStateMachine playStateMachine) {
        }

        @Override
        public boolean onMessage(PlayStateMachine playStateMachine, Telegram telegram) {
            return false;
        }
    }
}
