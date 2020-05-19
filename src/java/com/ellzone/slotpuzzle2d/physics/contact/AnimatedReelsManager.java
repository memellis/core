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
import com.ellzone.slotpuzzle2d.puzzlegrid.Point;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

import java.util.HashSet;

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
    private HashSet<Point> reelPoints = new HashSet<>();

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
            AnimatedReel animatedReelB = reelsAB.get(1);

            swapReelsAboveReel(
                    animatedReelA.getReel(),
                    animatedReelB.getReel(),
                    swapReelActionStoppedFalling);

            printSlotMatrix();
            Array<ReelTile> duplicateReels = checkForDuplicateReels();
            if (duplicateReels.size>0) {
                System.out.println("Duplicate reels in AnimatedReelsManager");
               for (ReelTile reelTile : duplicateReels)
                printColumn((int)reelTile.getDestinationX());
            }
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
            if (isTouching(currentReelBelow, reel)) {
                swapReelDestination(currentReelBelow, reel);
                if (swapReelAction != null)
                    swapReelAction.doAction(currentReelBelow);
                currentReelBelow = reelTiles.get(reel.index);
            }
        }
        swapReelAction.doAction(currentReelBelow);
        printSlotMatrix();
    }

    private void swapReelsAboveReel(
            ReelTile reelBelow,
            ReelTile reelAbove,
            SwapReelAction swapReelAction) {
        moveDeletedReelsToTheTopOfTheColumn(reelBelow, reelAbove);
        setFallenReelsToCurrentPostions(reelBelow, reelAbove, swapReelAction);
        printSlotMatrix();
    }

    private void moveDeletedReelsToTheTopOfTheColumn(ReelTile reelBelow, ReelTile reelAbove) {
        Array<Integer> reelsBetween = getReelsBetween(reelBelow, reelAbove);
        Array<Integer> reelsDeleted = getTheReelsDeletedInColumn(reelBelow.getDestinationX());
        int numberOfReelsDeletedInColumn = reelsDeleted.size;
        for (Integer reelBetween : reelsBetween) {
            ReelTile reelTileBetween = reelTiles.get(reelBetween);
            for (int index = 0; index < numberOfReelsDeletedInColumn; index++ ) {
                ReelTile reelTileDeleted = reelTiles.get(reelsDeleted.get(index));
                if (reelTileBetween.getDestinationX() == reelTileDeleted.getDestinationX() &
                    reelTileBetween.getDestinationY() == reelTileDeleted.getDestinationY()) {
                    reelTileBetween.setDestinationY(360 - (index * 40));
                    reelTileBetween.setY(360 + SCREEN_OFFSET - (index * 40));
                }
            }
        }
    }

    private void setFallenReelsToCurrentPostions(ReelTile reelBelow,
                                                 ReelTile reelAbove,
                                                 SwapReelAction swapReelAction) {
        Array<Integer> reelsAboveInContact = getReelsInContactAbove(reelBelow.getIndex());
        for (Integer reelAboveInContact : reelsAboveInContact)
            setFallReelToCurrentPosition(reelAboveInContact, swapReelAction);
        setFallReelToCurrentPosition(reelBelow.getIndex(), swapReelAction);
    }

    private void setFallReelToCurrentPosition(Integer reelAboveInContact, SwapReelAction swapReelAction) {
        if (reelAboveInContact>=0) {
            ReelTile reelTile = reelTiles.get(reelAboveInContact);
            if (!reelTile.isReelTileDeleted()) {
                reelTile.setDestinationY(reelTile.getSnapY());
                swapReelAction.doAction(reelTile);
            }
        }
    }

    private Array<Integer> getTheReelsDeletedInColumn(float column) {
        Array<Integer> reelsDeleted = new Array<>();
        int reelDeleted;
        for (ReelTile reelTile : reelTiles) {
            if (reelTile.getDestinationX() == column)
                if (reelTile.isReelTileDeleted())
                    reelsDeleted.add(reelTile.getIndex());
        }
        return reelsDeleted;
    }

    private Array<Integer> getReelsBetween(ReelTile reelBelow, ReelTile reelAbove) {
        Array<Integer> reelsBetween = new Array<>();
        ReelTile nextReelAbove = reelBelow;
        int foundReel;
        boolean exitgetReelsBetween = false;
        do {
            foundReel = findReel(
                    (int) nextReelAbove.getDestinationX(),
                    (int) (nextReelAbove.getDestinationY() + 40.0f));
            if (foundReel >= 0) {
                nextReelAbove = animatedReels.get(foundReel).getReel();
                if (nextReelAbove.getDestinationY() < reelAbove.getDestinationY())
                    reelsBetween.add(foundReel);
                else
                    exitgetReelsBetween = true;
            } else
                exitgetReelsBetween = true;
        } while (!exitgetReelsBetween);
        return reelsBetween;
    }

    private boolean isTouching(ReelTile currentReelBelow, TupleValueIndex reel) {
        return
            currentReelBelow.getSnapY() + 40 ==
            animatedReels.get(reel.index).getReel().getSnapY();
    }

    public Array<Integer> getReelsInContactAbove(int reel) {
        Array<Integer> reelsAbove = new Array<>();
        if (reel < 0)
            return reelsAbove;
        ReelTile reelTile = reelTiles.get(reel);
        boolean foundReelAvove;
        do {
            int reelAbove = findReelUsingSnapYIgnoringDeletedReels(
                    (int) reelTile.getDestinationX(),
                    (int) reelTile.getSnapY() + 40);
            foundReelAvove =
                    reelAbove >= 0 &&
                    !reelTiles.get(reelAbove).isReelTileDeleted() ? true : false;
            if (foundReelAvove) {
                reelsAbove.add(reelAbove);
                reelTile = reelTiles.get(reelAbove);
            }

        } while (foundReelAvove);
        return reelsAbove;
    }

    private void swapReelDestination(ReelTile currentReelBelow, TupleValueIndex reel) {
        int deletedReelIndex = findReel(
                (int) currentReelBelow.getDestinationX(),
                (int) currentReelBelow.getDestinationY() + 40);
        if (deletedReelIndex >= 0) {
            ReelTile reelTile = reelTiles.get(reel.index);
            ReelTile deletedReelTile = reelTiles.get(deletedReelIndex);
            deletedReelTile.setY(reelTile.getDestinationY() + SCREEN_OFFSET);
            deletedReelTile.setDestinationY(deletedReelTile.getDestinationY() + SCREEN_OFFSET);
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

    private void reelSinkReelsLeftToFall(AnimatedReel animatedReel) {
        ReelTile reelTile = animatedReel.getReel();
        if (isReelAtDestination(reelTile, BOTTOM_ROW)) {
            recordDecrementReelsLeftToFall(reelTile);
            markAllReelsAvoveInContactAsFallen(reelTile);
        }
        if (isReelFallenFromAbove(reelTile, BOTTOM_ROW))
            processReelFallenBelowDestinationRow(reelTile, getReelsAboveMe(reelTile));
    }

    private boolean isReelAtDestination(ReelTile currentReel, float destinationY) {
        return currentReel.getDestinationY() == destinationY;
    }

    private boolean isReelFallenFromAbove(ReelTile currentReel, float destinationY) {
        return currentReel.getDestinationY() > destinationY;
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

    private int findReelUsingSnapY(int x, int y) {
        int findReelIndex = 0;
        while (findReelIndex < reelTiles.size) {
            if (isReelFoundUsingSnapY(x, y, findReelIndex)) {
                return findReelIndex;
            }
            findReelIndex++;
        }
        return -1;
    }

    private int findReelUsingSnapYIgnoringDeletedReels(int destinationX, int destinationY) {
        int findReelIndex = 0;
        while (findReelIndex < reelTiles.size) {
            if (isReelFoundUsingSnapYIgnoringDeletedReels(destinationX, destinationY, findReelIndex))
                return findReelIndex;
            findReelIndex++;
        }
        return -1;
    }

    private boolean isReelFoundUsingSnapY(
            int destinationX,
            int destinationY,
            int findReelIndex) {
        return (reelTiles.get(findReelIndex).getDestinationX() == destinationX) &
                ((reelTiles.get(findReelIndex).getSnapY() == destinationY) |
                 (reelTiles.get(findReelIndex).getSnapY()+SCREEN_OFFSET == destinationY));
    }

    private boolean isReelFoundUsingSnapYIgnoringDeletedReels(
            int destinationX,
            int destinationY,
            int findReelIndex) {
        if (reelTiles.get(findReelIndex).isReelTileDeleted())
            return false;
        return (reelTiles.get(findReelIndex).getDestinationX() == destinationX) &
                ((reelTiles.get(findReelIndex).getSnapY() == destinationY) |
                 (reelTiles.get(findReelIndex).getSnapY()+SCREEN_OFFSET == destinationY));
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

    public void printSlotMatrix() {
        PuzzleGridTypeReelTile.printGrid(
                PuzzleGridTypeReelTile.populateMatchGridStatic(
                        reelTiles,
                        GAME_LEVEL_WIDTH,
                        GAME_LEVEL_HEIGHT)
        );
        System.out.println();
    }

    public Array<ReelTile> checkForDuplicateReels() {
        Array<ReelTile> duplicateReels = new Array<>();
        reelPoints.clear();
        for (ReelTile reelTile : reelTiles)
            if (isDuplicateReel(reelTile))
                duplicateReels.add(reelTile);
        return duplicateReels;
    }

    private boolean isDuplicateReel(ReelTile reelTile) {
        int r = PuzzleGridTypeReelTile.getRowFromLevel(reelTile.getDestinationY(), GAME_LEVEL_HEIGHT);
        int c = PuzzleGridTypeReelTile.getColumnFromLevel(reelTile.getDestinationX());
        Point point = new Point(r, c, GAME_LEVEL_WIDTH);
        boolean isNotADuplicate = reelPoints.add(point);
        if (!isNotADuplicate)
            reelPoints.add(point);
        return !isNotADuplicate;
    }

    private void printColumn(int column) {
        for (ReelTile reelTile : reelTiles) {
            if (reelTile.getDestinationX() == column)
                System.out.print(" "+reelTile.getIndex());
        }
        System.out.println();
    }
}
