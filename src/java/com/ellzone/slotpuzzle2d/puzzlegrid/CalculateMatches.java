package com.ellzone.slotpuzzle2d.puzzlegrid;

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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.level.reel.ReelGrid;

public class CalculateMatches {
    public Array<Array<Vector2>> process(int[][] reelGridMatrix, ReelGrid reelGrid) {
        return matchReels(reelGridMatrix, reelGrid);
    }

    private Array<Array<Vector2>> matchReels(int[][] reelGridMatrix, ReelGrid reelGrid) {
        PuzzleGridTypeReelTile puzzleGrid = new PuzzleGridTypeReelTile();
        ReelTileGridValue[][] matchGrid = puzzleGrid.populateMatchGrid(reelGridMatrix);
        PuzzleGridTypeReelTile puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        PuzzleGridTypeReelTile.printGrid(matchGrid);
        matchGrid = puzzleGridTypeReelTile.createGridLinks(matchGrid);
        System.out.println();
        return matchRowsToDraw(matchGrid, puzzleGridTypeReelTile, reelGrid);
     }

    private Array<Array<Vector2>> matchRowsToDraw(
            ReelTileGridValue[][] matchGrid,
            PuzzleGridTypeReelTile puzzleGridTypeReelTile,
            ReelGrid reelGrid) {
        Array<ReelTileGridValue> depthSearchResults;
        Array<Array<Vector2>> rowMatchesToDraw = new Array<>();
        for (ReelTileGridValue[] reelTileGridValues : matchGrid) {
            depthSearchResults = puzzleGridTypeReelTile.depthFirstSearchIncludeDiagonals(reelTileGridValues[0]);
            if (puzzleGridTypeReelTile.isRow(depthSearchResults, matchGrid)) {
                rowMatchesToDraw.add(getRowMatchPattern(depthSearchResults, reelGrid));
            }
        }
        return rowMatchesToDraw;
    }

    private Array<Vector2> getRowMatchPattern(
            Array<ReelTileGridValue> depthSearchResults, ReelGrid reelGrid) {
        int x_offset = (int) reelGrid.getX();
        int y_offset = (int) (reelGrid.getY() + reelGrid.getHeight());
        Array<Vector2> rowMatchPattern = new Array<>();
        for (ReelTileGridValue cell : new Array.ArrayIterator<>(depthSearchResults)) {
            rowMatchPattern.add(new Vector2(
                    x_offset + 20 +  cell.c * 40, y_offset - 20 - cell.r * 40));
        }
        return rowMatchPattern;
    }
}
