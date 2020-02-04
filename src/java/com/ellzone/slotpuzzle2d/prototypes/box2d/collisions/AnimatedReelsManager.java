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
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.messaging.MessageType;
import com.ellzone.slotpuzzle2d.physics.ReelSink;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_HEIGHT;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_WIDTH;

public class AnimatedReelsManager implements Telegraph {
    private Array<AnimatedReel> animatedReels;
    private Array<ReelTile> reelTiles;
    private int numberOfReelsToFall = 0;

    AnimatedReelsManager(Array<AnimatedReel> animatedReels) {
        this.animatedReels = animatedReels;
        reelTiles = getReelTilesFromAnimatedReels(animatedReels);
    }

    public void setNumberOfReelsToFall(int numberOfReelsToFall) {
        this.numberOfReelsToFall = numberOfReelsToFall;
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
            reelsLeftToFall();
            return true;
        }
        return false;
    }

    private void swapReelsAboveMe(ReelTile reelTileA,
                                  ReelTile reelTileB) {
        TupleValueIndex[] reelsAboveMe = PuzzleGridType.getReelsAboveMe(
                PuzzleGridTypeReelTile.populateMatchGridStatic(
                        reelTiles,
                        GAME_LEVEL_WIDTH,
                        GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getRowFromLevel(reelTileA.getDestinationY(), GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getColumnFromLevel(reelTileA.getDestinationX()));

        swapReels(reelTileA, reelTileB);

        ReelTile currentReel = reelTileA;

        for (int reelsAboveMeIndex = 0; reelsAboveMeIndex < reelsAboveMe.length; reelsAboveMeIndex++)
            currentReel = swapReels(reelsAboveMe[reelsAboveMeIndex], currentReel);

        PuzzleGridTypeReelTile.printGrid(
                PuzzleGridTypeReelTile.populateMatchGridStatic(
                reelTiles,
                GAME_LEVEL_WIDTH,
                GAME_LEVEL_HEIGHT));
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

    private void reelsLeftToFall() {
        numberOfReelsToFall--;
        System.out.println("reelsLeftToFall=" + numberOfReelsToFall);
    }
}
