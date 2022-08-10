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

package com.ellzone.slotpuzzle2d.puzzlegrid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.gdx.MyGDXApplication;
import com.ellzone.slotpuzzle2d.level.MatchSlots;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.AnimatedReelsMatrixCreator;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.SlotPuzzleMatrices;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TestGetSurroundingReelTiles {

    @Test
    public void testGetSurroundingReelTilesWithNullParameter() {
        Array<ReelTileGridValue> surroundingReelTiles =
                PuzzleGridTypeReelTile.getSurroundingReelTiles(null, null);
        assertThat(surroundingReelTiles, is(equalTo(null)));
    }

    @Test
    public void testGetSurroundingReelTilesWithEmptyParameter() {
        Array<ReelTileGridValue> surroundingReelTiles =
                PuzzleGridTypeReelTile.getSurroundingReelTiles(new Array<ReelTileGridValue>(), null);
        assertThat(surroundingReelTiles.size, is(equalTo(0)));
    }

    @Test
    public void testGetSurroundingReelWithTwoReelTilesAndNoSurroundingReelTiles() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = getAnimatedReels(SlotPuzzleMatrices.createMatrixWithTwoBombs());

        MatchSlots matchSlots = getMatchedSlots(animatedReels);
        Array<ReelTileGridValue> matchedSlots = matchSlots.getMatchedSlots();
        Array<ReelTileGridValue> surroundingReelTiles =
                PuzzleGridTypeReelTile.getSurroundingReelTiles(matchedSlots, null);

        assertThat(surroundingReelTiles, is(equalTo(null)));
    }

    @Test
    public void testGetSurroundingReelWithTwoReelTilesAndSurroundingReelTiles() {
        Gdx.app = new MyGDXApplication();
        PuzzleGridTypeReelTile puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        AnimatedReelsMatrixCreator animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        int[][] grid = SlotPuzzleMatrices.createMatrixWithTwoBombsSurroundedByReelTilesTopLeft();
        Array<AnimatedReel> animatedReels = getAnimatedReels(grid);
        Array<ReelTile> reelTiles = animatedReelsMatrixCreator.
                getReelTilesFromAnimatedReels(animatedReels);

        MatchSlots matchSlots = getMatchedSlots(animatedReels);
        Array<ReelTileGridValue> matchedSlots = matchSlots.getMatchedSlots();

        ReelTileGridValue[][] matchGrid =
                puzzleGridTypeReelTile.populateMatchGrid(
                        reelTiles,
                        new GridSize(grid[0].length, grid.length));
        ReelTileGridValue[][] linkGrid = puzzleGridTypeReelTile.createGridLinksWithoutMatch(matchGrid);


        Array<ReelTileGridValue> surroundingReelTiles =
                PuzzleGridTypeReelTile.getSurroundingReelTiles(matchedSlots, linkGrid);

        Array<ReelTileGridValue> expectedSurroundingTiles = getExpectedSurroundingTiles(matchGrid);
        int index=0;
        for (ReelTileGridValue surroundingReeTile : surroundingReelTiles) {
            assertSurroundingReelTile(surroundingReeTile, expectedSurroundingTiles.get(index++));
        }
    }

    private Array<AnimatedReel> getAnimatedReels(int[][] matrixFWithOneBomb) {
        Array<AnimatedReel> animatedReels;
        AnimatedReelsMatrixCreator animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                matrixFWithOneBomb, false);
        PuzzleGridTypeReelTile.printSlotMatrix(animatedReels);
        return animatedReels;
    }

    private MatchSlots getMatchedSlots(Array<AnimatedReel> animatedReels) {
        return new MatchSlots(
                PuzzleGridTypeReelTile.getReelTilesFromAnimatedReels(animatedReels),
                new GridSize(
                        SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                        SlotPuzzleConstants.GAME_LEVEL_HEIGHT))
                .invoke();
    }

    private Array<ReelTileGridValue> getExpectedSurroundingTiles(ReelTileGridValue[][] grid) {
        Array<ReelTileGridValue> expectedSurroundingTiles = new Array<>();
        expectedSurroundingTiles.add(grid[0][0]);
        expectedSurroundingTiles.add(grid[0][1]);
        expectedSurroundingTiles.add(grid[0][2]);
        expectedSurroundingTiles.add(grid[0][3]);
        expectedSurroundingTiles.add(grid[1][0]);
        expectedSurroundingTiles.add(grid[1][3]);
        expectedSurroundingTiles.add(grid[2][0]);
        expectedSurroundingTiles.add(grid[2][1]);
        expectedSurroundingTiles.add(grid[2][2]);
        expectedSurroundingTiles.add(grid[2][3]);
        return expectedSurroundingTiles;
    }

    private void assertSurroundingReelTile(ReelTileGridValue reelTile1, ReelTileGridValue reelTile2) {
        assertThat(reelTile1.r, is(equalTo(reelTile2.r)));
        assertThat(reelTile1.c, is(equalTo(reelTile2.c)));
        assertThat(reelTile1.value, is(equalTo(reelTile2.value)));
    }
}
