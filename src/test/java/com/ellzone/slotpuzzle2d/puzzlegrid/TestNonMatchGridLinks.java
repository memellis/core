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
import com.ellzone.slotpuzzle2d.gdx.MyGDXApplication;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.AnimatedReelsMatrixCreator;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.SlotPuzzleMatrices;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TestNonMatchGridLinks {

    @Test
    public void testNonMatchGridLinks() {
        Gdx.app = new MyGDXApplication();
        PuzzleGridTypeReelTile puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        int[][] grid = SlotPuzzleMatrices.createMatrixWithTwoBombsSurroundedByReelTilesTopLeft();
        Array<AnimatedReel> animatedReels = getAnimatedReels(grid);
        AnimatedReelsMatrixCreator animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        Array<ReelTile> reelTiles = animatedReelsMatrixCreator.
                getReelTilesFromAnimatedReels(animatedReels);
        ReelTileGridValue[][] matchGrid =
                puzzleGridTypeReelTile.populateMatchGrid(
                        reelTiles,
                        new GridSize(grid[0].length, grid.length));
        ReelTileGridValue[][] linkGrid = puzzleGridTypeReelTile.createGridLinksWithoutMatch(matchGrid);

        for (int r = 0; r < grid.length; r++)
            for (int c = 0; c < grid[0].length; c++)
                assertACell(
                        linkGrid[r][c],
                        getValidPoints(new Point(r, c, grid[0].length), puzzleGridTypeReelTile, linkGrid),
                        linkGrid);
    }

    private Array<AnimatedReel> getAnimatedReels(int[][] matrixFWithOneBomb) {
        Array<AnimatedReel> animatedReels;
        AnimatedReelsMatrixCreator animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                matrixFWithOneBomb, false);
        return animatedReels;
    }

    private void assertACell(
            ReelTileGridValue cell,
            Array<Point> compassPoints,
            ReelTileGridValue[][] grid
            ) {
        int pointIndex = 0;
        for (int neighbourCount = 0; neighbourCount<cell.reelTileNeighbours.length; neighbourCount++)
            if (cell.gridValueNeighbours[neighbourCount] != null) {
                pointIndex = advanceToNonDeletedReel(cell, grid, compassPoints, pointIndex);
                if (isNonDeletedReel(cell, grid,compassPoints, pointIndex))
                    assertThat(
                            cell.gridValueNeighbours[neighbourCount],
                            is(equalTo(grid[compassPoints.get(pointIndex).getR()]
                                           [compassPoints.get(pointIndex).getC()])));
                pointIndex++;
            }
    }

    private int advanceToNonDeletedReel(
            ReelTileGridValue cell,
            ReelTileGridValue[][] grid,
            Array<Point> compassPoints,
            int pointIndex) {
        while ((pointIndex < cell.gridValueNeighbours.length) &&
                (grid[compassPoints.get(pointIndex).getR()]
                     [compassPoints.get(pointIndex).getC()].value < 0))
            pointIndex++;
        return pointIndex;
    }

    private boolean isNonDeletedReel(
            ReelTileGridValue cell,
            ReelTileGridValue[][] grid,
            Array<Point> compassPoints,
            int pointIndex) {
        return ((pointIndex < cell.gridValueNeighbours.length) &&
                (grid[compassPoints.get(pointIndex).getR()]
                [compassPoints.get(pointIndex).getC()].value >= 0));
    }

    private Array<Point> getValidPoints(
            Point point,
            PuzzleGridTypeReelTile puzzleGridTypeReelTile,
            ReelTileGridValue[][] grid) {
        Array<Point> points = new Array<>();
        if (puzzleGridTypeReelTile.isWithinGrid(grid, point.getR() - 1, point.getC()))
            points.add(new Point(point.getR() - 1, point.getC(), grid[0].length));
        if (puzzleGridTypeReelTile.isWithinGrid(grid, point.getR() - 1, point.getC() + 1))
            points.add(new Point(point.getR() - 1, point.getC() + 1, grid[0].length));
        if (puzzleGridTypeReelTile.isWithinGrid(grid, point.getR(), point.getC() + 1))
            points.add(new Point(point.getR(), point.getC() + 1, grid[0].length));
        if (puzzleGridTypeReelTile.isWithinGrid(grid, point.getR() + 1, point.getC() + 1))
            points.add(new Point(point.getR() + 1, point.getC() + 1, grid[0].length));
        if (puzzleGridTypeReelTile.isWithinGrid(grid, point.getR() + 1, point.getC()))
            points.add(new Point(point.getR() + 1, point.getC(), grid[0].length));
        if (puzzleGridTypeReelTile.isWithinGrid(grid, point.getR() + 1, point.getC() - 1))
            points.add(new Point(point.getR() + 1, point.getC() - 1, grid[0].length));
        if (puzzleGridTypeReelTile.isWithinGrid(grid, point.getR(), point.getC() - 1))
            points.add(new Point(point.getR(), point.getC() - 1, grid[0].length));
        if (puzzleGridTypeReelTile.isWithinGrid(grid, point.getR() - 1, point.getC() - 1))
            points.add(new Point(point.getR() - 1, point.getC() - 1, grid[0].length));
        return points;
    }

}
