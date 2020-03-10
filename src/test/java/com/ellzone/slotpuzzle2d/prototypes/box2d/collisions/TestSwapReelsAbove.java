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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.gdx.MyGDXApplication;
import com.ellzone.slotpuzzle2d.messaging.MessageType;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestSwapReelsAbove {
    @Test(expected = IllegalArgumentException.class)
    public void testSwapReelsAboveMeWithHandleMessageWithNullMessage() {
        Array<AnimatedReel> animatedReels = new Array<>();
        AnimatedReelsManager animatedReelsManager = new AnimatedReelsManager(animatedReels);
        animatedReelsManager.handleMessage(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSwapReelsAboveMeWithHandleMessageSwapReelsAboveMeNullExtraInfo() {
        Array<AnimatedReel> animatedReels = new Array<>();
        AnimatedReelsManager animatedReelsManager = new AnimatedReelsManager(animatedReels);
        Telegram message = new Telegram();
        message.message = MessageType.SwapReelsAboveMe.index;
        animatedReelsManager.handleMessage(message);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSwapReelsAboveMeWithHandleMessageSwapReelsAboveMeOneAnimtaedReelInExtraInfo() {
        Array<AnimatedReel> animatedReels = new Array<>();
        AnimatedReelsManager animatedReelsManager = new AnimatedReelsManager(animatedReels);
        Telegram message = new Telegram();
        message.message = MessageType.SwapReelsAboveMe.index;
        addAnimatedReel(animatedReels, 160, 40);
        message.extraInfo = animatedReels;
        animatedReelsManager.handleMessage(message);
    }

    @Test
    public void testSwapReelsAboveMeTwoAnimatedReels() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        AnimatedReel animatedReel = createAnimatedReel(160, 40);
        animatedReel.getReel().setDestinationX(160);
        animatedReel.getReel().setDestinationY(40);
        animatedReels.add(animatedReel);
        animatedReel = createAnimatedReel(160, 80);
        animatedReels.add(animatedReel);
        Telegram message = new Telegram();
        message.message = MessageType.SwapReelsAboveMe.index;
        message.extraInfo = animatedReels;
        AnimatedReelsManager animatedReelsManager = new AnimatedReelsManager(animatedReels);
        animatedReelsManager.handleMessage(message);
        Array<AnimatedReel> swappedReelsAboveMeAnimatedReels = animatedReelsManager.getAnimatedReels();
        assertThat(animatedReels.get(0).getReel().getX(),
                is(equalTo(swappedReelsAboveMeAnimatedReels.get(0).getReel().getX())));
        assertThat(animatedReels.get(0).getReel().getY(),
                is(equalTo(swappedReelsAboveMeAnimatedReels.get(0).getReel().getY())));
        assertThat(animatedReels.get(0).getReel().getDestinationX(),
                is(equalTo(swappedReelsAboveMeAnimatedReels.get(0).getReel().getDestinationX())));
        assertThat(animatedReels.get(0).getReel().getDestinationY(),
                is(equalTo(swappedReelsAboveMeAnimatedReels.get(0).getReel().getDestinationY())));
    }

    private void addAnimatedReel(Array<AnimatedReel> animatedReels, int x, int y) {
        AnimatedReel animatedReel = createAnimatedReel(x, y);
        animatedReel.getReel().setDestinationX(x);
        animatedReel.getReel().setDestinationY(y);
        animatedReels.add(animatedReel);
    }

    private AnimatedReel createAnimatedReel(int x, int y) {
        return new AnimatedReel(
                null,
                x,
                y,
                40,
                40,
                40,
                40,
                0,
                null
        );
    }
}
