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

package com.ellzone.slotpuzzle2d.prototypes.box2d.collisions;

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

import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_HEIGHT;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_WIDTH;

public class AnimatedReelsManager implements Telegraph {
    private Array<AnimatedReel> animatedReels;
    private Array<Body> reelBodies;
    private Array<ReelTile> reelTiles;
    private int numberOfReelsToFall = 0;
    private int reelsStoppedFalling = 0;

    AnimatedReelsManager(Array<AnimatedReel> animatedReels) {
        this.animatedReels = animatedReels;
        reelTiles = getReelTilesFromAnimatedReels(animatedReels);
    }

    AnimatedReelsManager(Array<AnimatedReel> animatedReels,
                         Array<Body> reelBodies) {
        this(animatedReels);
        this.reelBodies = reelBodies;
    }

    public void setAnimatedReels(Array<AnimatedReel> animatedReels) {
        this.animatedReels = animatedReels;
        reelTiles = getReelTilesFromAnimatedReels(animatedReels);
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
        if (message.message == MessageType.SwapReelsAboveMe.index) {
            Array<AnimatedReel> reelsAB = (Array<AnimatedReel>) message.extraInfo;

            AnimatedReel animatedReelA = reelsAB.get(0);
            AnimatedReel animatedReelB = reelsAB.get(1);

            swapReelsAboveMe(
                    animatedReelA.getReel(),
                    animatedReelB.getReel());

            return true;
        }
        if (message.message == MessageType.ReelsLeftToFall.index) {
            AnimatedReel animatedReel = (AnimatedReel) message.extraInfo;
            reelsLeftToFall(animatedReel);
            return true;
        }
        if (message.message == MessageType.ReelSinkReelsLeftToFall.index) {
            AnimatedReel animatedReel = (AnimatedReel) message.extraInfo;
            reelSinkReelsLeftToFall(animatedReel);
        }
        return false;
    }

    private void reelsLeftToFall(AnimatedReel animatedReel) {
        ReelTile reelTile = animatedReel.getReel();
        recordDecrementReelsLeftToFall(reelTile);
        recordIfReelHasStoppedFalling(reelTile);
    }

    private void recordIfReelHasStoppedFalling(ReelTile reelTile) {
        Body reelBody = reelBodies.get(reelTile.getIndex());
        if(!reelBody.isAwake()) {
            System.out.println("reelTile x="+reelTile.getY()+"has stopped falling");
            reelsStoppedFalling--;
        }
    }

    private void recordDecrementReelsLeftToFall(ReelTile reelTile) {
        if (!reelTile.isFallen()) {
            reelTile.setIsFallen(true);
            decrementReelsLeftToFall();
        }
    }

    private void reelSinkReelsLeftToFall(AnimatedReel animatedReel) {
        ReelTile reelTile = animatedReel.getReel();
        recordDecrementReelsLeftToFall(reelTile);
        recordIfReelHasStoppedFalling(reelTile);

        PuzzleGridTypeReelTile.printGrid(
                PuzzleGridTypeReelTile.populateMatchGridStatic(
                        reelTiles,
                        GAME_LEVEL_WIDTH,
                        GAME_LEVEL_HEIGHT));

        TupleValueIndex[] reelsAboveMe = PuzzleGridType.getReelsAboveMe(
                PuzzleGridTypeReelTile.populateMatchGridStatic(
                        reelTiles,
                        GAME_LEVEL_WIDTH,
                        GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getRowFromLevel(reelTile.getDestinationY(), GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getColumnFromLevel(reelTile.getDestinationX()));
        markAllReelsAvoveInContactAsFallen(reelTile, reelsAboveMe);
     }

    private void markAllReelsAvoveInContactAsFallen(ReelTile reelTile, TupleValueIndex[] reelsAboveMe) {
        ReelTile currentReelTile = reelTile;
        for (int i = 0; i < reelsAboveMe.length; i++) {
            if (PuzzleGridTypeReelTile.getRowFromLevel(
                    currentReelTile.getDestinationY(), GAME_LEVEL_HEIGHT) - 1 == reelsAboveMe[i].getR()) {
                currentReelTile = animatedReels.get(reelsAboveMe[i].index).getReel();
                recordDecrementReelsLeftToFall(currentReelTile);
                recordIfReelHasStoppedFalling(currentReelTile);
            }
        }
    }

    private void swapReelsAboveMe(ReelTile reelTileA,
                                  ReelTile reelTileB) {
        PuzzleGridTypeReelTile.printGrid(
                PuzzleGridTypeReelTile.populateMatchGridStatic(
                        reelTiles,
                        GAME_LEVEL_WIDTH,
                        GAME_LEVEL_HEIGHT));

        swapReels(reelTileA, reelTileB);

        TupleValueIndex[] reelsAboveMe = PuzzleGridType.getReelsAboveMe(
                PuzzleGridTypeReelTile.populateMatchGridStatic(
                        reelTiles,
                        GAME_LEVEL_WIDTH,
                        GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getRowFromLevel(reelTileA.getDestinationY(), GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getColumnFromLevel(reelTileA.getDestinationX()));

        ReelTile currentReel = reelTileA;

        for (int reelsAboveMeIndex = 0; reelsAboveMeIndex < reelsAboveMe.length; reelsAboveMeIndex++)
            currentReel = swapReels(reelsAboveMe[reelsAboveMeIndex], currentReel);


        reelsAboveMe = PuzzleGridType.getReelsAboveMe(
                PuzzleGridTypeReelTile.populateMatchGridStatic(
                        reelTiles,
                        GAME_LEVEL_WIDTH,
                        GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getRowFromLevel(reelTileA.getDestinationY(), GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getColumnFromLevel(reelTileA.getDestinationX()));

        for (int reelsAboveMeIndex = 0; reelsAboveMeIndex < reelsAboveMe.length; reelsAboveMeIndex++)
            recordDecrementReelsLeftToFall(
                    animatedReels.get(reelsAboveMe[reelsAboveMeIndex].getIndex()).getReel());
    }

    private ReelTile swapReels(TupleValueIndex tupleValueIndex, ReelTile currentReel) {
        float savedDestinationY;
        int reelHasFallenFrom;
        ReelTile deletedReel;
        savedDestinationY = reelTiles.get(tupleValueIndex.getIndex()).getDestinationY();
        reelHasFallenFrom = findReel((int) currentReel.getDestinationX(), (int) currentReel.getDestinationY() + 40);
        deletedReel = reelTiles.get(reelHasFallenFrom);

        reelTiles.get(tupleValueIndex.getIndex()).setDestinationY(currentReel.getDestinationY() + 40);
        reelTiles.get(tupleValueIndex.getIndex()).setY(currentReel.getDestinationY() + 40);

        deletedReel.setDestinationY(savedDestinationY);
        deletedReel.setY(savedDestinationY);

        currentReel = reelTiles.get(tupleValueIndex.getIndex());
        return currentReel;
    }

    private void swapReels(ReelTile reelTileA, ReelTile reelTileB) {
        float savedDestinationY = reelTileA.getDestinationY();
        int reelHasFallenFrom = findReel((int)reelTileB.getDestinationX(), (int) reelTileB.getDestinationY() + 40);
        ReelTile deletedReel = reelTiles.get(reelHasFallenFrom);

        reelTileA.setDestinationY(reelTileB.getDestinationY() + 40);
        reelTileA.setY(reelTileB.getDestinationY() + 40);
        reelTileA.unDeleteReelTile();

        deletedReel.setDestinationY(savedDestinationY);
        deletedReel.deleteReelTile();
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
            if ((reelTiles.get(findReelIndex).getDestinationX() == destinationX) &
                    (reelTiles.get(findReelIndex).getDestinationY() == destinationY)) {
                return findReelIndex;
            }
            findReelIndex++;
        }
        return -1;
    }

    private void decrementReelsLeftToFall() {
        numberOfReelsToFall--;
        System.out.println("reelsLeftToFall=" + numberOfReelsToFall);
    }

    private void decreaseReelsLeftToFallBy(int decreaseValue) {
        numberOfReelsToFall -= decreaseValue;
        System.out.println("reelsLeftToFall=" + numberOfReelsToFall);
    }

    public void checkForReelsStoppedFalling() {
        for (Body reelBoxBody : reelBodies) {
            if (reelBoxBody != null)
                if (PhysicsManagerCustomBodies.isStopped(reelBoxBody)) {
                    AnimatedReel animatedReel = (AnimatedReel) reelBoxBody.getUserData();
                    if (!animatedReel.getReel().isStoppedFalling()) {
                        animatedReel.getReel().setIsStoppedFalling(true);
                        reelsStoppedFalling--;
                        System.out.println("reelsStoppedFalling="+reelsStoppedFalling);
                    }
                }
        }
    }
}
