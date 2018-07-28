package com.ellzone.slotpuzzle2d.testpuzzlegrid;

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

import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestPuzzleGridTypeReelTile {

    private PuzzleGridTypeReelTile puzzleGridTypeReelTile;
    private ReelTileGridValue[][] puzzleGrid;
    private ReelTileGridValue[][] resultsGrid;

    @Before
    public void setUp() {
        puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        setUpGrid1();
    }

    private void setUpGrid1() {
        puzzleGrid = new ReelTileGridValue[3][3];
        setUpGrid1Row1();
        setUpGrid1Row2();
        setUpgrid1Row3();
    }

    private void setUpgrid1Row3() {
        puzzleGrid[2][0] = new ReelTileGridValue(2, 0, 6, 0);
        puzzleGrid[2][1] = new ReelTileGridValue(2, 1, 7, 0);
        puzzleGrid[2][2] = new ReelTileGridValue(2, 2, 8, 0);
    }

    private void setUpGrid1Row2() {
        puzzleGrid[1][0] = new ReelTileGridValue(1, 0, 3, 1);
        puzzleGrid[1][1] = new ReelTileGridValue(1, 1, 4, 0);
        puzzleGrid[1][2] = new ReelTileGridValue(1, 2, 5, 1);
    }

    private void setUpGrid1Row1() {
        puzzleGrid[0][0] = new ReelTileGridValue(0, 0, 0, 0);
        puzzleGrid[0][1] = new ReelTileGridValue(0, 1, 1, 1);
        puzzleGrid[0][2] = new ReelTileGridValue(0, 2, 2, 2);
    }

    @After
    public void tearDown() {
        puzzleGridTypeReelTile = null;
    }

    @Test
    public void testCreateGridLinks() {
        resultsGrid = puzzleGridTypeReelTile.createGridLinks(puzzleGrid);
        assertThat(resultsGrid[0][0].getCompassPoint(ReelTileGridValue.Compass.SOUTHEAST), is(resultsGrid[1][1]));
        assertThat(resultsGrid[1][1].getCompassPoint(ReelTileGridValue.Compass.NORTHWEST), is(resultsGrid[0][0]));
        assertThat(resultsGrid[0][1].getCompassPoint(ReelTileGridValue.Compass.SOUTHWEST), is(resultsGrid[1][0]));
        assertThat(resultsGrid[1][0].getCompassPoint(ReelTileGridValue.Compass.NORTHEAST), is(resultsGrid[0][1]));
        assertThat(resultsGrid[0][1].getCompassPoint(ReelTileGridValue.Compass.SOUTHEAST), is(resultsGrid[1][2]));
        assertThat(resultsGrid[1][2].getCompassPoint(ReelTileGridValue.Compass.NORTHWEST), is(resultsGrid[0][1]));
        assertThat(resultsGrid[1][1].getCompassPoint(ReelTileGridValue.Compass.SOUTHEAST), is(resultsGrid[2][2]));
        assertThat(resultsGrid[2][2].getCompassPoint(ReelTileGridValue.Compass.NORTHWEST), is(resultsGrid[1][1]));
        assertThat(resultsGrid[1][1].getCompassPoint(ReelTileGridValue.Compass.SOUTH),     is(resultsGrid[2][1]));
        assertThat(resultsGrid[2][1].getCompassPoint(ReelTileGridValue.Compass.NORTH),     is(resultsGrid[1][1]));
        assertThat(resultsGrid[2][0].getCompassPoint(ReelTileGridValue.Compass.NORTHEAST), is(resultsGrid[1][1]));
        assertThat(resultsGrid[1][1].getCompassPoint(ReelTileGridValue.Compass.SOUTHWEST), is(resultsGrid[2][0]));
    }
}
