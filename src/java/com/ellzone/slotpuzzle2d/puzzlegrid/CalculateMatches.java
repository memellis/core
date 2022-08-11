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

public class CalculateMatches {
    public Array<Array<Vector2>> process(int[][] reelGrid) {
        return matchReels(reelGrid);
    }

    private Array<Array<Vector2>> matchReels(int[][] reelGrid) {
        PuzzleGridTypeReelTile puzzleGrid = new PuzzleGridTypeReelTile();
        ReelTileGridValue[][] matchGrid = puzzleGrid.populateMatchGrid(reelGrid);
        PuzzleGridTypeReelTile puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        matchGrid = puzzleGridTypeReelTile.createGridLinks(matchGrid);
        PuzzleGridTypeReelTile.printGrid(matchGrid);
        return matchRowsToDraw(matchGrid, puzzleGridTypeReelTile);
     }

    private Array<Array<Vector2>> matchRowsToDraw(
            ReelTileGridValue[][] matchGrid,
            PuzzleGridTypeReelTile puzzleGridTypeReelTile) {
        Array<ReelTileGridValue> depthSearchResults;
        Array<Array<Vector2>> rowMatchesToDraw = new Array<>();
        for (ReelTileGridValue[] reelTileGridValues : matchGrid) {
            depthSearchResults = puzzleGridTypeReelTile.depthFirstSearchIncludeDiagonals(reelTileGridValues[0]);
            if (puzzleGridTypeReelTile.isRow(depthSearchResults, matchGrid)) {
                rowMatchesToDraw.add(getRowMatchPattern(depthSearchResults));
            }
        }
        return rowMatchesToDraw;
    }

    private Array<Vector2> getRowMatchPattern(Array<ReelTileGridValue> depthSearchResults) {
        Array<Vector2> rowMatchPattern = new Array<>();
        for (ReelTileGridValue cell : new Array.ArrayIterator<>(depthSearchResults)) {
            rowMatchPattern.add(new Vector2(cell.c * 40, cell.r * 40));
        }
        return rowMatchPattern;
    }
}
