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
import com.ellzone.slotpuzzle2d.utils.InputMatrix;

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
    }

    @After
    public void tearDown() {
        puzzleGridTypeReelTile = null;
    }

    @Test
    public void testCreateGridLinks() {
        setUpGrid1();
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

    private void setUpGrid1() {
        puzzleGrid = new ReelTileGridValue[3][3];
        setUpGrid1Row1();
        setUpGrid1Row2();
        setUpgrid1Row3();
    }

    private void setUpGrid1Row1() {
        puzzleGrid[0][0] = new ReelTileGridValue(0, 0, 0, 0);
        puzzleGrid[0][1] = new ReelTileGridValue(0, 1, 1, 1);
        puzzleGrid[0][2] = new ReelTileGridValue(0, 2, 2, 2);
    }

    private void setUpGrid1Row2() {
        puzzleGrid[1][0] = new ReelTileGridValue(1, 0, 3, 1);
        puzzleGrid[1][1] = new ReelTileGridValue(1, 1, 4, 0);
        puzzleGrid[1][2] = new ReelTileGridValue(1, 2, 5, 1);
    }

    private void setUpgrid1Row3() {
        puzzleGrid[2][0] = new ReelTileGridValue(2, 0, 6, 0);
        puzzleGrid[2][1] = new ReelTileGridValue(2, 1, 7, 0);
        puzzleGrid[2][2] = new ReelTileGridValue(2, 2, 8, 0);
    }

    @Test
    public void testCreateGridFromMatrix() {
        String matrixToInput = "3 x 3\n"
                + "0 1 2\n"
                + "1 0 1\n"
                + "0 0 0\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        int[][] matrix = inputMatrix.readMatrix();
        PuzzleGridTypeReelTile puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        ReelTileGridValue[][] puzzleGrid = puzzleGridTypeReelTile.createGridFromMatrix(matrix);
        assertThat(puzzleGrid[0][0].getIndex(), is(0));
        assertThat(puzzleGrid[0][0].getValue(), is(0));
        assertThat(puzzleGrid[0][1].getIndex(), is(1));
        assertThat(puzzleGrid[0][1].getValue(), is(1));
        assertThat(puzzleGrid[0][2].getIndex(), is(2));
        assertThat(puzzleGrid[0][2].getValue(), is(2));

        assertThat(puzzleGrid[1][0].getIndex(), is(3));
        assertThat(puzzleGrid[1][0].getValue(), is(1));
        assertThat(puzzleGrid[1][1].getIndex(), is(4));
        assertThat(puzzleGrid[1][1].getValue(), is(0));
        assertThat(puzzleGrid[1][2].getIndex(), is(5));
        assertThat(puzzleGrid[1][2].getValue(), is(1));

        assertThat(puzzleGrid[2][0].getIndex(), is(6));
        assertThat(puzzleGrid[2][0].getValue(), is(0));
        assertThat(puzzleGrid[2][1].getIndex(), is(7));
        assertThat(puzzleGrid[2][1].getValue(), is(0));
        assertThat(puzzleGrid[2][2].getIndex(), is(8));
        assertThat(puzzleGrid[2][2].getValue(), is(0));
    }
}
