package com.ellzone.slotpuzzle2d.testpuzzlegrid;

import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestDepthFirstSearchWithDiagonals {

    private PuzzleGridTypeReelTile puzzleGridTypeReelTile;
    private ReelTileGridValue[][] puzzleGrid;
    private ReelTileGridValue[][] resultsGrid;
    private Array<ReelTileGridValue> expectedDepthSearchResults1,
                                     expectedDepthSearchResults2;

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
        expectedDepthSearchResults1 = new Array<ReelTileGridValue>();
        expectedDepthSearchResults2 = new Array<ReelTileGridValue>();
        setUpExpectedDepthSearchResults1();
        setUpExpectedDepthSearchResults2();
    }

    private void setUpExpectedDepthSearchResults1() {
        expectedDepthSearchResults1.add(puzzleGrid[0][0]);
        expectedDepthSearchResults1.add(puzzleGrid[1][1]);
        expectedDepthSearchResults1.add(puzzleGrid[2][0]);
        expectedDepthSearchResults1.add(puzzleGrid[2][1]);
        expectedDepthSearchResults1.add(puzzleGrid[2][2]);
    }

    private void setUpExpectedDepthSearchResults2() {
        expectedDepthSearchResults2.add(puzzleGrid[1][0]);
        expectedDepthSearchResults2.add(puzzleGrid[0][1]);
        expectedDepthSearchResults2.add(puzzleGrid[1][2]);
    }

    private void setUpGrid1Row1() {
        puzzleGrid[2][0] = new ReelTileGridValue(2, 0, 6, 0);
        puzzleGrid[2][1] = new ReelTileGridValue(2, 1, 7, 0);
        puzzleGrid[2][2] = new ReelTileGridValue(2, 2, 8, 0);
    }

    private void setUpGrid1Row2() {
        puzzleGrid[1][0] = new ReelTileGridValue(1, 0, 3, 1);
        puzzleGrid[1][1] = new ReelTileGridValue(1, 1, 4, 0);
        puzzleGrid[1][2] = new ReelTileGridValue(1, 2, 5, 1);
    }

    private void setUpgrid1Row3() {
        puzzleGrid[0][0] = new ReelTileGridValue(0, 0, 0, 0);
        puzzleGrid[0][1] = new ReelTileGridValue(0, 1, 1, 1);
        puzzleGrid[0][2] = new ReelTileGridValue(0, 2, 2, 2);
    }


    @After
    public void tearDown() {
        puzzleGridTypeReelTile = null;
        puzzleGrid = null;
    }

    @Test
    public void testDepthFirstSearchWithDiagonals() {
        resultsGrid = puzzleGridTypeReelTile.createGridLinks(puzzleGrid);
        Array<ReelTileGridValue> depthSearchResults = puzzleGridTypeReelTile.depthFirstSearchIncludeDiagonals(resultsGrid[0][0]);
        assertThat(depthSearchResults, is(expectedDepthSearchResults1));
        depthSearchResults = puzzleGridTypeReelTile.depthFirstSearchIncludeDiagonals(resultsGrid[1][0]);
        assertThat(depthSearchResults, is(expectedDepthSearchResults2));
    }
}
