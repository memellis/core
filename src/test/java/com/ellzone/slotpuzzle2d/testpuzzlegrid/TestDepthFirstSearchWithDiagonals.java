package com.ellzone.slotpuzzle2d.testpuzzlegrid;

import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.utils.InputMatrix;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestDepthFirstSearchWithDiagonals {

    private PuzzleGridTypeReelTile puzzleGridTypeReelTile;
    private ReelTileGridValue[][] puzzleGrid1,
                                  puzzleGrid2,
                                  puzzleGrid3,
                                  resultsGrid;
    private Array<ReelTileGridValue> expectedDepthSearchResults1,
                                     expectedDepthSearchResults2;
    private Array<ReelTileGridValue> depthSearchResults;
    private int[][] expectedDepthSearchResults3;

    @Before
    public void setUp() {
        initialiseFields();
    }

    private void initialiseFields() {
        puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        puzzleGrid1 = new ReelTileGridValue[3][3];
        puzzleGrid2 = new ReelTileGridValue[4][4];
        expectedDepthSearchResults1 = new Array<ReelTileGridValue>();
        expectedDepthSearchResults2 = new Array<ReelTileGridValue>();
    }

    private void setUpGrid1() {
        setUpGridRows();
        setUpExpectedDepthSearchResults1();
        setUpExpectedDepthSearchResults2();
    }

    private void setUpGridRows() {
        setUpGrid1Row1();
        setUpGrid1Row2();
        setUpgrid1Row3();
    }

    private void setUpExpectedDepthSearchResults1() {
        expectedDepthSearchResults1.add(puzzleGrid1[0][0]);
        expectedDepthSearchResults1.add(puzzleGrid1[1][1]);
        expectedDepthSearchResults1.add(puzzleGrid1[2][0]);
        expectedDepthSearchResults1.add(puzzleGrid1[2][1]);
        expectedDepthSearchResults1.add(puzzleGrid1[2][2]);
    }

    private void setUpExpectedDepthSearchResults2() {
        expectedDepthSearchResults2.add(puzzleGrid1[1][0]);
        expectedDepthSearchResults2.add(puzzleGrid1[0][1]);
        expectedDepthSearchResults2.add(puzzleGrid1[1][2]);
    }

    private void setUpGrid1Row1() {
        puzzleGrid1[0][0] = new ReelTileGridValue(0, 0, 0, 0);
        puzzleGrid1[0][1] = new ReelTileGridValue(0, 1, 1, 1);
        puzzleGrid1[0][2] = new ReelTileGridValue(0, 2, 2, 2);
    }

    private void setUpGrid1Row2() {
        puzzleGrid1[1][0] = new ReelTileGridValue(1, 0, 3, 1);
        puzzleGrid1[1][1] = new ReelTileGridValue(1, 1, 4, 0);
        puzzleGrid1[1][2] = new ReelTileGridValue(1, 2, 5, 1);
    }


    private void setUpgrid1Row3() {
        puzzleGrid1[2][0] = new ReelTileGridValue(2, 0, 6, 0);
        puzzleGrid1[2][1] = new ReelTileGridValue(2, 1, 7, 0);
        puzzleGrid1[2][2] = new ReelTileGridValue(2, 2, 8, 0);
    }

    private void setUpGrid2() {
        setUpGrid2Row1();
        setUpGrid2Row2();
        setUpgrid2Row3();
        setUpgrid2Row4();
    }

    private void setUpGrid2Row1() {
        puzzleGrid2[0][0] = new ReelTileGridValue(0 , 0,  0, 0);
        puzzleGrid2[0][1] = new ReelTileGridValue(0 , 1,  1, 2);
        puzzleGrid2[0][2] = new ReelTileGridValue(0 , 2,  2, 1);
        puzzleGrid2[0][3] = new ReelTileGridValue(0 , 3,  3, 2);
    }

    private void setUpGrid2Row2() {
        puzzleGrid2[1][0] = new ReelTileGridValue(1 , 0,  4, 0);
        puzzleGrid2[1][1] = new ReelTileGridValue(1 , 1,  5, 1);
        puzzleGrid2[1][2] = new ReelTileGridValue(1 , 2,  6, 2);
        puzzleGrid2[1][3] = new ReelTileGridValue(1 , 3,  7, 1);
    }

    private void setUpgrid2Row3() {
        puzzleGrid2[2][0] = new ReelTileGridValue(2 , 0,  8, 1);
        puzzleGrid2[2][1] = new ReelTileGridValue(2 , 1,  9, 0);
        puzzleGrid2[2][2] = new ReelTileGridValue(2 , 2, 10, 2);
        puzzleGrid2[2][3] = new ReelTileGridValue(2 , 3, 11, 2);
    }

    private void setUpgrid2Row4() {
        puzzleGrid2[3][0] = new ReelTileGridValue(3 , 0, 12, 2);
        puzzleGrid2[3][1] = new ReelTileGridValue(3 , 1, 13, 2);
        puzzleGrid2[3][2] = new ReelTileGridValue(3 , 2, 14, 0);
        puzzleGrid2[3][3] = new ReelTileGridValue(3 , 3, 15, 0);
    }

    @After
    public void tearDown() {
        puzzleGridTypeReelTile = null;
        puzzleGrid1 = null;
        puzzleGrid2 = null;
        puzzleGrid3 = null;
        expectedDepthSearchResults1 = null;
        expectedDepthSearchResults2 = null;
        resultsGrid = null;
    }

    @Test
    public void testDepthFirstSearchWithDiagonals() {
        setUpGrid1();
        resultsGrid = puzzleGridTypeReelTile.createGridLinks(puzzleGrid1);
        depthSearchResults = puzzleGridTypeReelTile.depthFirstSearchIncludeDiagonals(resultsGrid[0][0]);
        assertThat(depthSearchResults, is(expectedDepthSearchResults1));
        depthSearchResults = puzzleGridTypeReelTile.depthFirstSearchIncludeDiagonals(resultsGrid[1][0]);
        assertThat(depthSearchResults, is(expectedDepthSearchResults2));
    }

    @Test
    public void testIsRow() {
        setUpGrid1();
        resultsGrid = puzzleGridTypeReelTile.createGridLinks(puzzleGrid1);
        assertRow(resultsGrid[0][0], true);
        assertRow(resultsGrid[1][0], true);
        assertRow(resultsGrid[2][0], true);
    }

    private void assertRow(ReelTileGridValue startCell, boolean predicate) {
        puzzleGridTypeReelTile.clearDiscovered(resultsGrid);
        depthSearchResults = puzzleGridTypeReelTile.depthFirstSearchIncludeDiagonals(startCell);
        assertThat(puzzleGridTypeReelTile.isRow(depthSearchResults, resultsGrid), is(predicate));
    }

    @Test
    public void testIsRow4x4Grid() {
        setUpGrid2();
        resultsGrid = puzzleGridTypeReelTile.createGridLinks(puzzleGrid2);
        assertRow(resultsGrid[0][0], true);
        assertRow(resultsGrid[1][0], true);
        assertRow(resultsGrid[2][0], true);
        assertRow(resultsGrid[3][0], true);
    }

    @Test
    public void testMultipleRowsFromStartCell() {
        setUpGrid3();
        resultsGrid = puzzleGridTypeReelTile.createGridLinks(puzzleGrid3);
        depthSearchResults = puzzleGridTypeReelTile.depthFirstSearchIncludeDiagonals(resultsGrid[0][0]);
        setExpectedDepthSearchResults3();
        assertDepthResults(depthSearchResults, expectedDepthSearchResults3);
    }

    private void setExpectedDepthSearchResults3() {
        expectedDepthSearchResults3 = new int[][]{{0, 0, 0},
                                               {1, 0, 0},
                                               {2, 1, 0},
                                               {3, 2, 0},
                                               {3, 3, 0},
                                               {0, 1, 0},
                                               {0, 2, 0},
                                               {0, 3, 0}};
    }

    private void assertDepthResults(Array<ReelTileGridValue> depthSearchResults, int[][] expectedDepthSearchResults3) {
        int i = 0;
        for (ReelTileGridValue gridValue : depthSearchResults) {
            assertThat(gridValue.r, is(expectedDepthSearchResults3[i][0]));
            assertThat(gridValue.c, is(expectedDepthSearchResults3[i][1]));
            assertThat(gridValue.getValue(), is(expectedDepthSearchResults3[i][2]));
            i++;
        }
    }

    private void setUpGrid3() {
        String matrixToInput = "4 x 4\n"
                + "0 0 0 0\n"
                + "0 1 2 1\n"
                + "1 0 2 2\n"
                + "2 2 0 0\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        int[][] matrix = inputMatrix.readMatrix();
        puzzleGrid3 = puzzleGridTypeReelTile.createGridFromMatrix(matrix);
    }
}
