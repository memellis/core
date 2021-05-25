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

package com.ellzone.slotpuzzle2d.physics.contact;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.messaging.MessageType;
import com.ellzone.slotpuzzle2d.physics.ReelSink;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;

import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.GAME_LEVEL_HEIGHT;

public class FallenReel {
    private AnimatedReel animatedReelA;
    private AnimatedReel animatedReelB;
    private AnimatedReel animatedReel;
    private ReelSink reelSink;
    private MessageManager messageManager;

    public FallenReel(AnimatedReel animatedReelA, AnimatedReel animatedReelB) {
        this.animatedReelA = animatedReelA;
        this.animatedReelB = animatedReelB;
        messageManager = setUpMessageManager();
    }

    public FallenReel(AnimatedReel animatedReel, ReelSink reelSink) {
        this.animatedReel = animatedReel;
        this.reelSink = reelSink;
        messageManager = setUpMessageManager();
    }

    private MessageManager setUpMessageManager() {
        return MessageManager.getInstance();
    }

    public void processRows() {
        int destinationRowA, destinationRowB;
        int snapYA, snapYB;

        destinationRowA = PuzzleGridTypeReelTile.getRowFromLevel(
                animatedReelA.getReel().getDestinationY(), GAME_LEVEL_HEIGHT);
        destinationRowB = PuzzleGridTypeReelTile.getRowFromLevel(
                animatedReelB.getReel().getDestinationY(), GAME_LEVEL_HEIGHT);

        snapYA = (int) animatedReelA.getReel().getSnapY();
        snapYB = (int) animatedReelB.getReel().getSnapY();
        if (snapYA>snapYB)
            processReelHittingReel(destinationRowA, destinationRowB, animatedReelA);
        else
            processReelHittingReel(destinationRowA, destinationRowB, animatedReelB);
    }

    public void processFallenReelHittingReelSink() {
        messageManager.dispatchMessage(MessageType.ReelSinkReelsLeftToFall.index, animatedReel);
    }

    private void processReelHittingReel(int rowA, int rowB, AnimatedReel animatedReel) {
        messageManager.dispatchMessage(MessageType.ReelsLeftToFall.index, animatedReel);
        if (isFallenGapGreaterThanOneReel(rowA, rowB))
            processReelsFallenMoreThanOneTile(rowA, rowB);
    }

    private boolean isFallenGapGreaterThanOneReel(int rowA, int rowB) {
        return Math.abs(rowA - rowB) > 1;
    }

     private void processReelsFallenMoreThanOneTile(int rowA, int rowB) {
         if (rowA > rowB)
             swapReelsAboveMeAB();
         else
             swapReelsAboveMeBA();
     }

    private void swapReelsAboveMeAB() {
        Array<AnimatedReel> reelsAB = new Array<>();
        reelsAB.add(animatedReelA);
        reelsAB.add(animatedReelB);
        messageManager.dispatchMessage(MessageType.SwapReelsAboveMe.index, reelsAB);
    }

    private void swapReelsAboveMeBA() {
        Array<AnimatedReel> reelsAB = new Array<>();
        reelsAB.add(animatedReelB);
        reelsAB.add(animatedReelA);
        messageManager.dispatchMessage(MessageType.SwapReelsAboveMe.index, reelsAB);
    }
}
