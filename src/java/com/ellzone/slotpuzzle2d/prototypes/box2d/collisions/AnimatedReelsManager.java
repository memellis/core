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

import java.text.MessageFormat;

import static com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.Box2DBoxesFallingFromSlotPuzzleMatrices.SCREEN_OFFSET;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_HEIGHT;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_WIDTH;

public class AnimatedReelsManager implements Telegraph {
    private Array<AnimatedReel> animatedReels;
    private Array<Body> reelBodies;
    private Array<ReelTile> reelTiles;
    private int numberOfReelsToFall = 0;
    private int reelsStoppedFalling = 0;

    AnimatedReelsManager(Array<AnimatedReel> animatedReels) {
        if (animatedReels == null)
            throw new IllegalArgumentException("animatedReels is null");
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

        TupleValueIndex[] reelsAboveMe = PuzzleGridType.getReelsAboveMe(
                PuzzleGridTypeReelTile.populateMatchGridStatic(
                        reelTiles,
                        GAME_LEVEL_WIDTH,
                        GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getRowFromLevel(reelTile.getDestinationY(), GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getColumnFromLevel(reelTile.getDestinationX()));

        markAllReelsAvoveInContactAsFallen(reelTile, reelsAboveMe);
        PuzzleGridTypeReelTile.printGrid(
               PuzzleGridTypeReelTile.populateMatchGridStatic(
                       reelTiles,
                       GAME_LEVEL_WIDTH,
                       GAME_LEVEL_HEIGHT)
        );
        System.out.println();
     }

    private void markAllReelsAvoveInContactAsFallen(ReelTile reelTile, TupleValueIndex[] reelsAboveMe) {
        ReelTile currentReelTile = reelTile;
        int currentRow = PuzzleGridTypeReelTile.getRowFromLevel(currentReelTile.getY(), GAME_LEVEL_HEIGHT);
        int destinationRow = PuzzleGridTypeReelTile.getRowFromLevel(currentReelTile.getDestinationY(), GAME_LEVEL_HEIGHT);
        if (destinationRow < currentRow) {
            processReelFallenBelowDestinationRow(reelTile, reelsAboveMe);
            return;
        }
        for (int i = 0; i < reelsAboveMe.length; i++) {
            if (PuzzleGridTypeReelTile.getRowFromLevel(
                    currentReelTile.getDestinationY(), GAME_LEVEL_HEIGHT) - 1 == reelsAboveMe[i].getR()) {
                System.out.println("currentRow="+
                        PuzzleGridTypeReelTile.getRowFromLevel(currentReelTile.getY(), GAME_LEVEL_HEIGHT));
                System.out.println("destinationRow="+
                        PuzzleGridTypeReelTile.getRowFromLevel(currentReelTile.getDestinationY(), GAME_LEVEL_HEIGHT));
                recordDecrementReelsLeftToFall(animatedReels.get(reelsAboveMe[i].index).getReel());
            }
            currentReelTile = animatedReels.get(reelsAboveMe[i].index).getReel();
        }
    }

    private void processReelFallenBelowDestinationRow(ReelTile reelTile, TupleValueIndex[] reelsAboveMe) {
        ReelTile bottomDeletedReel = reelTiles.get(findReel((int) reelTile.getDestinationX(), 40));
        swapReelsForFallenReel(reelTile, bottomDeletedReel);
        for (int i=0; i<reelsAboveMe.length; i++) {
            bottomDeletedReel = reelTiles.get(findReel((int) reelTile.getDestinationX(), 40 + 40 * (i+1)));
            swapReelsForFallenReel(reelTiles.get(reelsAboveMe[i].index), bottomDeletedReel);
        }
        PuzzleGridTypeReelTile.printGrid(
                PuzzleGridTypeReelTile.populateMatchGridStatic(
                        reelTiles,
                        GAME_LEVEL_WIDTH,
                        GAME_LEVEL_HEIGHT)
        );
        System.out.println();
    }

    private void swapReelsAboveMe(ReelTile reelTileA,
                                  ReelTile reelTileB) {

        printReelsAB(reelTileA, reelTileB);
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

        recordDecrementReelsLeftToFall(reelTileA);

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

    private void printReelsAB(ReelTile reelTileA, ReelTile reelTileB) {
        System.out.println(
                MessageFormat.format(
                        "rA.dest({0},{1})", reelTileA.getDestinationX(), reelTileA.getDestinationY()
                )
        );
        System.out.println(
                MessageFormat.format(
                        "rA.XY({0},{1})", reelTileA.getX(), reelTileA.getY()
                )
        );
        System.out.println(
                MessageFormat.format(
                        "rA.destrc({0},{1})",
                        PuzzleGridTypeReelTile.getRowFromLevel(reelTileA.getDestinationY(), GAME_LEVEL_HEIGHT),
                        PuzzleGridTypeReelTile.getColumnFromLevel(reelTileA.getDestinationX())
                )
        );
        System.out.println(
                MessageFormat.format(
                        "rB.destrc({0},{1})",
                        PuzzleGridTypeReelTile.getRowFromLevel(reelTileB.getDestinationY(), GAME_LEVEL_HEIGHT),
                        PuzzleGridTypeReelTile.getColumnFromLevel(reelTileB.getDestinationX())
                )
        );
        System.out.println(
                MessageFormat.format(
                        "rA.xyrc({0},{1})",
                        PuzzleGridTypeReelTile.getRowFromLevel(reelTileA.getY(), GAME_LEVEL_HEIGHT),
                        PuzzleGridTypeReelTile.getColumnFromLevel(reelTileA.getX())
                )
        );
        System.out.println(
                MessageFormat.format(
                        "rB.xyrc({0},{1})",
                        PuzzleGridTypeReelTile.getRowFromLevel(reelTileB.getY(), GAME_LEVEL_HEIGHT),
                        PuzzleGridTypeReelTile.getColumnFromLevel(reelTileB.getX())
                )
        );
        System.out.println(
                MessageFormat.format(
                        "rB.dest({0},{1})", reelTileB.getDestinationX(), reelTileB.getDestinationY()
                )
        );
        System.out.println(
                MessageFormat.format(
                        "rB.XY({0},{1})", reelTileB.getX(), reelTileB.getY()
                )
        );
    }

    private ReelTile swapReels(TupleValueIndex tupleValueIndex, ReelTile currentReel) {
        float savedDestinationY;
        int reelHasFallenFrom;

        if (currentReel==null)
            return null;
        ReelTile deletedReel;
        savedDestinationY = reelTiles.get(tupleValueIndex.getIndex()).getDestinationY();
        reelHasFallenFrom = findReel((int) currentReel.getDestinationX(), (int) currentReel.getDestinationY() + 40);
        if (reelHasFallenFrom<0)
            return null;
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
        if (reelHasFallenFrom<0)
            return;
        System.out.println("reelHasFallenFrom"+reelHasFallenFrom);
        ReelTile deletedReel = reelTiles.get(reelHasFallenFrom);

        reelTileA.setDestinationY(reelTileB.getDestinationY());
        reelTileA.setY(reelTileB.getDestinationY());
        reelTileA.unDeleteReelTile();

        deletedReel.setDestinationY(savedDestinationY);
        deletedReel.deleteReelTile();
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
}
