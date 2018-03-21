/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ellzone.slotpuzzle2d.finitestatemachine;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

/*
Search for PlanetUML

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
Drop --> DropReels : NumberOfReelsToDelete > 0
DropReels --> Fallen : NumberOfReelsDropped == 0
Fallen --> Spin : NumberOfReelsSpinning > 0
Spin -> Match : NumberOfReelsFalling == 0
Match -> Flash : NumberOfMatchedReels > 0
Match -> Play : NumberOfMatchedReels == 0
Flash --> Drop : NumberOfReelsDeleted > 0
}

State Play {
}

@enduml
 */

public enum PlayState implements State<Play> {

    INTRO_FALLING_SEQUENCE() {
        @Override
        public void enter(Play play) {
        }

        @Override
        public void update(Play play) {
            if (!play.getConcretePlay().areReelsFalling()) {
                play.getStateMachine().changeState(INTRO_SPINNING_SEQUENCE);
            }
        }

        @Override
        public void exit(Play play) {

        }

        @Override
        public boolean onMessage(Play play, Telegram telegram) {
            return false;
        }
    },

    INTRO_SPINNING_SEQUENCE() {
        @Override
        public void enter(Play play) {
        }

        @Override
        public void update(Play play) {
            if (!play.getConcretePlay().areReelsSpinning()) {
                play.getStateMachine().changeState(INTRO_FLASHING_SEQUENCE);
            }
        }

        @Override
        public void exit(Play play) {

        }

        @Override
        public boolean onMessage(Play play, Telegram telegram) {
            return false;
        }
    },

    INTRO_FLASHING_SEQUENCE() {
        @Override
        public void enter(Play play) {
        }

        @Override
        public void update(Play play) {
            if (!play.getConcretePlay().areReelsFlashing()) {
                play.getStateMachine().changeState(INTRO_ENDING_SEQUENCE);
            }
        }

        @Override
        public void exit(Play play) {

        }

        @Override
        public boolean onMessage(Play play, Telegram telegram) {
            return false;
        }
    },

    INTRO_ENDING_SEQUENCE() {
        @Override
        public void enter(Play play) {
        }

        @Override
        public void update(Play play) {
            if (!play.getConcretePlay().areReelsDeleted()) {
                play.getStateMachine().changeState(DROP_REELS);
            }
        }

        @Override
        public void exit(Play play) {

        }

        @Override
        public boolean onMessage(Play play, Telegram telegram) {
            return false;
        }
    },

    DROP_REELS() {
        @Override
        public void enter(Play play) {

        }

        @Override
        public void update(Play play) {

        }

        @Override
        public void exit(Play play) {

        }

        @Override
        public boolean onMessage(Play play, Telegram telegram) {
            return false;
        }

    },

    FALL() {
        @Override
        public void enter(Play play) {

        }

        @Override
        public void update(Play play) {

        }

        @Override
        public void exit(Play play) {

        }

        @Override
        public boolean onMessage(Play play, Telegram telegram) {
            return false;
        }
    },

    SPIN() {
        @Override
        public void enter(Play play) {

        }

        @Override
        public void update(Play play) {

        }

        @Override
        public void exit(Play play) {

        }

        @Override
        public boolean onMessage(Play play, Telegram telegram) {
            return false;
        }
    },

    MATCH() {
        @Override
        public void enter(Play play) {

        }

        @Override
        public void update(Play play) {

        }

        @Override
        public void exit(Play play) {

        }

        @Override
        public boolean onMessage(Play play, Telegram telegram) {
            return false;
        }
    },

    FLASH() {
        @Override
        public void enter(Play play) {

        }

        @Override
        public void update(Play play) {

        }

        @Override
        public void exit(Play play) {

        }

        @Override
        public boolean onMessage(Play play, Telegram telegram) {
            return false;
        }
    }
}
