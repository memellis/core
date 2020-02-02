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

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.messaging.MessageType;
import com.ellzone.slotpuzzle2d.physics.ReelSink;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;

import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_HEIGHT;

public class FallenReel {
    private AnimatedReel animatedReelA;
    private AnimatedReel animatedReelB;
    private AnimatedReel animatedReel;
    private ReelSink reelSink;

    private MessageManager messageManager;
    private AnimatedReelsManager animatedReelsManager;


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
        int rowA, rowB;

        rowA = PuzzleGridTypeReelTile.getRowFromLevel(
                animatedReelA.getReel().getDestinationY(), GAME_LEVEL_HEIGHT);
        rowB = PuzzleGridTypeReelTile.getRowFromLevel(
                animatedReelB.getReel().getDestinationY(), GAME_LEVEL_HEIGHT);

        processReelHittingReel(rowA, rowB);
    }

    public void processFallenReelHittingReelSink() {
        messageManager.dispatchMessage(MessageType.ReelsLeftToFall.index);
    }

    private void processReelHittingReel(int rowA, int rowB) {
        if (isFallenGapGreaterThanOneReel(rowA, rowB))
            processReelsFallenMoreThanOneTile(rowA, rowB);
    }

    private boolean isFallenGapGreaterThanOneReel(int rowA, int rowB) {
        return Math.abs(rowA - rowB) > 1;
    }

     private void processReelsFallenMoreThanOneTile(int rowA, int rowB) {
         if (rowA > rowB)
             swapReelsAboveMeBA();
         else
             swapReelsAboveMeAB();
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