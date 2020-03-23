/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.physics.contact;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.messaging.MessageType;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

import static com.ellzone.slotpuzzle2d.prototypes.assets.CreateLevelReels.REEL_WIDTH;
import static com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.Box2DBoxesFallingFromSlotPuzzleMatrices.SCREEN_OFFSET;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_HEIGHT;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_WIDTH;

public class AnimatedReelsManager implements Telegraph {
    private static final float BOTTOM_ROW = 40.0f;
    private Array<AnimatedReel> animatedReels;
    private Array<Body> reelBodies;
    private Array<ReelTile> reelTiles;
    private int numberOfReelsToFall = 0;
    private int reelsStoppedFalling = 0;

    public AnimatedReelsManager(Array<AnimatedReel> animatedReels) {
        if (animatedReels == null)
            throw new IllegalArgumentException("animatedReels is null");
        this.animatedReels = animatedReels;
        reelTiles = getReelTilesFromAnimatedReels(animatedReels);
    }

    public AnimatedReelsManager(Array<AnimatedReel> animatedReels,
                         Array<Body> reelBodies) {
        this(animatedReels);
        this.reelBodies = reelBodies;
    }

    public void setAnimatedReels(Array<AnimatedReel> animatedReels) {
        this.animatedReels = animatedReels;
        reelTiles = getReelTilesFromAnimatedReels(animatedReels);
    }

    public Array<AnimatedReel> getAnimatedReels() {
        return animatedReels;
    }

    public Array<ReelTile> getReelTiles() {
        return reelTiles;
    }

    public void setReelBodies(Array<Body> reelBodies) {
        this.reelBodies = reelBodies;
    }

    public void setNumberOfReelsToFall(int numberOfReelsToFall) {
        this.numberOfReelsToFall = numberOfReelsToFall;
        this.reelsStoppedFalling = numberOfReelsToFall;
    }

    public int getNumberOfReelsToFall() {
        return numberOfReelsToFall;
    }

    public int getReelsStoppedFalling() {
        return reelsStoppedFalling;
    }

    @Override
    public boolean handleMessage(Telegram message) {
        if (message == null) {
            throw new IllegalArgumentException("message is null");
        }

        if (message.message == MessageType.SwapReelsAboveMe.index) {
            Array<AnimatedReel> reelsAB = (Array<AnimatedReel>) message.extraInfo;
            if (reelsAB == null)
                throw new IllegalArgumentException("message.extrainfo is null");
            if (reelsAB.size != 2)
                throw new IllegalArgumentException("message.extrainfo does have a two AnimatedReels");

            AnimatedReel animatedReelA = reelsAB.get(0);
            swapReelsAboveReel(
                    animatedReelA.getReel(),
                    swapReelActionStoppedFalling);

            return true;
        }
        if (message.message == MessageType.ReelsLeftToFall.index) {
            AnimatedReel animatedReel = (AnimatedReel) message.extraInfo;
            if (animatedReel == null)
                throw new IllegalArgumentException("message.extrainfo is null");
            reelsLeftToFall(animatedReel);
            return true;
        }
        if (message.message == MessageType.ReelSinkReelsLeftToFall.index) {
            AnimatedReel animatedReel = (AnimatedReel) message.extraInfo;
            if (animatedReel == null)
                throw new IllegalArgumentException("message.extrainfo is null");
            reelSinkReelsLeftToFall(animatedReel);
        }
        return false;
    }

    private SwapReelAction swapReelActionStoppedFalling = new SwapReelAction() {
        @Override
        public void doAction(ReelTile reelTile) {
            reelTile.setIsStoppedFalling(true);
        }
    };

    private void swapReelsAboveReel(ReelTile reelBelow, SwapReelAction swapReelAction) {
        TupleValueIndex[] reelsAboveMe = getReelsAboveMe(reelBelow);
        ReelTile currentReelBelow = reelBelow;
        for (TupleValueIndex reel : reelsAboveMe) {
            if (!isReelAtDestination(currentReelBelow, reel)) {
                swapReelDestination(currentReelBelow, reel);
                if (swapReelAction != null)
                    swapReelAction.doAction(currentReelBelow);
                currentReelBelow = reelTiles.get(reel.index);
            }
        }
        swapReelAction.doAction(currentReelBelow);
    }

    private void swapReelDestination(ReelTile currentReelBelow, TupleValueIndex reel) {
        int deletedReelIndex = findReel(
                (int) currentReelBelow.getDestinationX(),
                (int) currentReelBelow.getDestinationY() + 40);
        if (deletedReelIndex >= 0) {
            ReelTile reelTile = reelTiles.get(reel.index);
            ReelTile deletedReelTile = reelTiles.get(deletedReelIndex);
            deletedReelTile.setY(reelTile.getDestinationY() + SCREEN_OFFSET);
            deletedReelTile.setDestinationY(deletedReelTile.getDestinationY() + 40);
            reelTile.setDestinationY(currentReelBelow.getDestinationY() + 40);
        }
    }

    private TupleValueIndex[] getReelsAboveMe(ReelTile reelTile) {
        return PuzzleGridType.getReelsAboveMe(
            PuzzleGridTypeReelTile.populateMatchGridStatic(
                    reelTiles,
                    GAME_LEVEL_WIDTH,
                    GAME_LEVEL_HEIGHT),
            PuzzleGridTypeReelTile.getRowFromLevel(reelTile.getDestinationY(), GAME_LEVEL_HEIGHT),
            PuzzleGridTypeReelTile.getColumnFromLevel(reelTile.getDestinationX()));
    }

    private boolean isReelAtDestination(ReelTile currentReelBelow, TupleValueIndex reel) {
        return currentReelBelow.getDestinationY() + REEL_WIDTH == reelTiles.get(reel.index).getDestinationY();
    }

    private boolean isReelAtDestination(ReelTile currentReel, float destinationY) {
        return currentReel.getDestinationY() == destinationY;
    }

    private void reelSinkReelsLeftToFall(AnimatedReel animatedReel) {
        ReelTile reelTile = animatedReel.getReel();
        if (isReelAtDestination(reelTile, BOTTOM_ROW)) {
            recordDecrementReelsLeftToFall(reelTile);
            markAllReelsAvoveInContactAsFallen(reelTile);
            printSlotMatrix();
        }
    }

    private void markAllReelsAvoveInContactAsFallen(ReelTile reelTile) {
        TupleValueIndex[] reelsAboveMe = getReelsAboveMe(reelTile);
        if (isReelFallenBelowDestinationRow(reelTile))
            processReelFallenBelowDestinationRow(reelTile, reelsAboveMe);
        else
            processReelsLeftToFall(reelTile, reelsAboveMe);
    }

    private void processReelsLeftToFall(ReelTile currentReelTile, TupleValueIndex[] reelsAboveMe) {
        for (int i = 0; i < reelsAboveMe.length; i++) {
            if (PuzzleGridTypeReelTile.getRowFromLevel(
                    currentReelTile.getDestinationY(), GAME_LEVEL_HEIGHT) - 1 == reelsAboveMe[i].getR()) {
                recordDecrementReelsLeftToFall(animatedReels.get(reelsAboveMe[i].index).getReel());
            }
            currentReelTile = animatedReels.get(reelsAboveMe[i].index).getReel();
        }
    }

    private boolean isReelFallenBelowDestinationRow(ReelTile currentReelTile) {
        int currentRow = PuzzleGridTypeReelTile.getRowFromLevel(currentReelTile.getY(), GAME_LEVEL_HEIGHT);
        int destinationRow = PuzzleGridTypeReelTile.getRowFromLevel(currentReelTile.getDestinationY(), GAME_LEVEL_HEIGHT);
        return destinationRow < currentRow;
    }

    private void reelsLeftToFall(AnimatedReel animatedReel) {
        ReelTile reelTile = animatedReel.getReel();
        recordDecrementReelsLeftToFall(reelTile);
    }

    private void recordDecrementReelsLeftToFall(ReelTile reelTile) {
        if (!reelTile.isFallen()) {
            reelTile.setIsFallen(true);
            decrementReelsLeftToFall();
        }
    }

    private void processReelFallenBelowDestinationRow(ReelTile reelTile, TupleValueIndex[] reelsAboveMe) {
        ReelTile bottomDeletedReel = reelTiles.get(findReel((int) reelTile.getDestinationX(), 40));
        swapReelsForFallenReel(reelTile, bottomDeletedReel);
        for (int i=0; i<reelsAboveMe.length; i++) {
            bottomDeletedReel = reelTiles.get(findReel((int) reelTile.getDestinationX(), 40 + 40 * (i+1)));
            swapReelsForFallenReel(reelTiles.get(reelsAboveMe[i].index), bottomDeletedReel);
        }
        printSlotMatrix();
    }

    private void swapReelsForFallenReel(ReelTile reelTileA, ReelTile reelTileB) {
        float savedDestinationY = reelTileA.getDestinationY();
        reelTileA.setDestinationY(reelTileB.getDestinationY());
        reelTileA.setY(reelTileB.getDestinationY());
        reelTileA.unDeleteReelTile();
        reelTileB.setDestinationY(savedDestinationY);
    }

    private Array<ReelTile> getReelTilesFromAnimatedReels(Array<AnimatedReel> animatedReels) {
        Array<ReelTile> reelTiles = new Array<>();
        for (AnimatedReel animatedReel : animatedReels)
            reelTiles.add(animatedReel.getReel());
        return reelTiles;
    }

    private int findReel(int destinationX, int destinationY) {
        int findReelIndex = 0;
        while (findReelIndex < reelTiles.size) {
            if (isReelFound(destinationX, destinationY, findReelIndex)) {
                return findReelIndex;
            }
            findReelIndex++;
        }
        return -1;
    }

    private boolean isReelFound(int destinationX, int destinationY, int findReelIndex) {
        return (reelTiles.get(findReelIndex).getDestinationX() == destinationX) &
                ((reelTiles.get(findReelIndex).getDestinationY() == destinationY) |
                 (reelTiles.get(findReelIndex).getDestinationY()+SCREEN_OFFSET == destinationY));
    }

    private void decrementReelsLeftToFall() {
        numberOfReelsToFall--;
    }

    public void checkForReelsStoppedFalling() {
        for (Body reelBoxBody : reelBodies) {
            if (reelBoxBody != null)
                if (PhysicsManagerCustomBodies.isStopped(reelBoxBody)) {
                    AnimatedReel animatedReel = (AnimatedReel) reelBoxBody.getUserData();
                    if (!animatedReel.getReel().isReelTileDeleted())
                        if (!animatedReel.getReel().isStoppedFalling()) {
                            animatedReel.getReel().setIsStoppedFalling(true);
                            reelsStoppedFalling--;
                        }
                }
        }
    }

    private void printSlotMatrix() {
        PuzzleGridTypeReelTile.printGrid(
                PuzzleGridTypeReelTile.populateMatchGridStatic(
                        reelTiles,
                        GAME_LEVEL_WIDTH,
                        GAME_LEVEL_HEIGHT)
        );
        System.out.println();
    }
}
