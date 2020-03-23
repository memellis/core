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

package com.ellzone.slotpuzzle2d.prototypes.box2d.collisions;

import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.physics.contact.AnimatedReelsManager;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;

import org.junit.Test;

public class TestAnimatedReelsManager {
    @Test(expected = IllegalArgumentException.class)
    public void testAnimatedReelsManagerCreatedWithNullAnimatedReelsParameter() {
        com.ellzone.slotpuzzle2d.physics.contact.AnimatedReelsManager animatedReelsManager = new com.ellzone.slotpuzzle2d.physics.contact.AnimatedReelsManager(null);
    }

    @Test
    public void testAnimatedReelsManagerCreatedWithEmptyAnimatedReels() {
        Array<AnimatedReel> animatedReels = new Array<>();
        com.ellzone.slotpuzzle2d.physics.contact.AnimatedReelsManager animatedReelsManager = new AnimatedReelsManager(animatedReels);
    }
}
