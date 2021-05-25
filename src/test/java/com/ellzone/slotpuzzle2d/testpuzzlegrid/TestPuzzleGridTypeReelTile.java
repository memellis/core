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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.utils.InputMatrix;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PuzzleGridTypeReelTile.class, ReelTile.class, Gdx.class})

public class TestPuzzleGridTypeReelTile {

    private PuzzleGridTypeReelTile puzzleGridTypeReelTile;
    private ReelTileGridValue[][] puzzleGrid;
    private ReelTileGridValue[][] resultsGrid;
    private TupleValueIndex[][] lonelyTestGrid;
    private Array<ReelTile> reelTiles;
    private Application applicationMock;
    private Capture<String> logCaptureArgument1, logCaptureArgument2;
    private Capture<String> debugCaptureArgument1, debugCaptureArgument2;

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
        assertPuzzleGridRow1(puzzleGrid[0]);

        assertPuzzleGridRow2(puzzleGrid[1]);

        assertThat(puzzleGrid[2][0].getIndex(), is(6));
        assertThat(puzzleGrid[2][0].getValue(), is(0));
        assertThat(puzzleGrid[2][1].getIndex(), is(7));
        assertThat(puzzleGrid[2][1].getValue(), is(0));
        assertThat(puzzleGrid[2][2].getIndex(), is(8));
        assertThat(puzzleGrid[2][2].getValue(), is(0));
    }

    private void assertPuzzleGridRow1(ReelTileGridValue[] reelTileGridValues) {
        assertThat(reelTileGridValues[0].getIndex(), is(0));
        assertThat(reelTileGridValues[0].getValue(), is(0));
        assertThat(reelTileGridValues[1].getIndex(), is(1));
        assertThat(reelTileGridValues[1].getValue(), is(1));
        assertThat(reelTileGridValues[2].getIndex(), is(2));
        assertThat(reelTileGridValues[2].getValue(), is(2));
    }

    private void assertPuzzleGridRow2(ReelTileGridValue[] reelTileGridValues) {
        assertThat(reelTileGridValues[0].getIndex(), is(3));
        assertThat(reelTileGridValues[0].getValue(), is(1));
        assertThat(reelTileGridValues[1].getIndex(), is(4));
        assertThat(reelTileGridValues[1].getValue(), is(0));
        assertThat(reelTileGridValues[2].getIndex(), is(5));
        assertThat(reelTileGridValues[2].getValue(), is(1));
    }

    @Ignore
    @Test
    public void testAdjustForAnyLonelyReels() {
        setUpMocks();
        mockGdx();
        int[][] lonelytTilesMatrix = createLonelyTilesMatrix();
        createLonelyTilesGrid(lonelytTilesMatrix);
        int[][] expectedLonelyTiles = createExpectedLonelyTilesMatrix();
        int[][] expectedLonelyReels =  setLonleyReelsExpectations(expectedLonelyTiles);
        relayAll(expectedLonelyTiles);
        Array<ReelTile> levelAdjustedForLonelyReels = puzzleGridTypeReelTile.adjustForAnyLonelyReels(reelTiles, SlotPuzzleConstants.GAME_LEVEL_WIDTH, SlotPuzzleConstants.GAME_LEVEL_HEIGHT);
        for (int[] expectedLonelyReel : expectedLonelyReels)
            assertThat(Whitebox.getInternalState(levelAdjustedForLonelyReels.get(expectedLonelyReel[0]),
                      "endReel"),
                    is(equalTo(Whitebox.getInternalState(levelAdjustedForLonelyReels.get(expectedLonelyReel[1]),
                              "endReel"))));
        verifyAll(expectedLonelyTiles);
    }

    private void setUpMocks() {
        applicationMock = createMock(Application.class);
        mockGdx();
        setUpCaptureArguments();
    }

    private void mockGdx() {
        Gdx.app = applicationMock;
    }

    private void setUpCaptureArguments() {
        logCaptureArgument1 = EasyMock.newCapture();
        logCaptureArgument2 = EasyMock.newCapture();
        debugCaptureArgument1 = EasyMock.newCapture();
        debugCaptureArgument2 = EasyMock.newCapture();
    }

    private int[][] setLonleyReelsExpectations(int[][] expectedLonelyTileMatrix) {
        setReelTilesExpectations();
        return setExpectedLonleyTiles(expectedLonelyTileMatrix);
    }

     private void setReelTilesExpectations() {
        for(int i=0; i < reelTiles.size ; i++) {
            ReelTile reelTile = reelTiles.get(i);
            expect(reelTile.getDestinationX()).andReturn((Float) Whitebox.getInternalState(reelTile, "x")).times(2);
            expect(reelTile.getDestinationY()).andReturn((Float) Whitebox.getInternalState(reelTile, "y")).times(2);
            expect(reelTile.isReelTileDeleted()).andReturn((boolean) Whitebox.getInternalState(reelTile, "tileDeleted"));
            expect(reelTile.getEndReel()).andReturn((int) Whitebox.getInternalState(reelTile, "endReel")).times(2);
            expect(reelTile.getX()).andReturn((Float) Whitebox.getInternalState(reelTile, "x"));
            expect(reelTile.getY()).andReturn((Float) Whitebox.getInternalState(reelTile, "y"));
            applicationMock.debug(capture(debugCaptureArgument1), capture(debugCaptureArgument2));

            if ((float) Whitebox.getInternalState(reelTile, "x") == 560.0f) {
                applicationMock.debug(capture(debugCaptureArgument1), capture(debugCaptureArgument2));
                applicationMock.debug(capture(debugCaptureArgument1), capture(debugCaptureArgument2));
            }
        }
    }

    private int[][] setExpectedLonleyTiles(int[][] expectedLonelyTileMatrix) {
        int[][] expectedlonelyReels = new int[expectedLonelyTileMatrix.length][2];
        for (int row = 0; row < expectedLonelyTileMatrix.length; row++) {
            ReelTile reelTile = reelTiles.get(lonelyTestGrid[lonelyTestGrid.length - 1 - expectedLonelyTileMatrix[row][0]][expectedLonelyTileMatrix[row][1]].index);
            if (expectedLonelyTileMatrix[row][0] == 0) {
                adjustExpectedLonelyTile(expectedLonelyTileMatrix[row], expectedlonelyReels, row, reelTile, -1, 0);
            } else if (expectedLonelyTileMatrix[row][1] == 0) {
                adjustExpectedLonelyTile(expectedLonelyTileMatrix[row], expectedlonelyReels, row, reelTile, 0, 1);
            } else if (expectedLonelyTileMatrix[row][0] == lonelyTestGrid.length - 1) {
                adjustExpectedLonelyTile(expectedLonelyTileMatrix[row], expectedlonelyReels, row, reelTile, 1, 0);
             } else if (expectedLonelyTileMatrix[row][1] == lonelyTestGrid[0].length - 1) {
                 adjustExpectedLonelyTile(expectedLonelyTileMatrix[row], expectedlonelyReels, row, reelTile, 0, 1);
             } else {
                adjustExpectedLonelyTile(expectedLonelyTileMatrix[row], expectedlonelyReels, row - 1, reelTile, 0, 1);
            }
        }
        return expectedlonelyReels;
    }

    private void adjustExpectedLonelyTile(int[] expectedLonelyTileMatrix, int[][] expectedlonelyReels, int row, ReelTile reelTile, int adjustRow, int adjustColumn) {
        ReelTile adjustedReel = reelTiles.get(getexpectedLonelyReelIndex(expectedLonelyTileMatrix, adjustRow, adjustColumn));
        expect(adjustedReel.getEndReel()).andReturn((Integer) Whitebox.getInternalState(adjustedReel, "endReel"));
        reelTile.setEndReel((int) Whitebox.getInternalState(adjustedReel, "endReel"));
        expectedlonelyReels[row][0] = getexpectedLonelyReelIndex(expectedLonelyTileMatrix, 0, 0);
        expectedlonelyReels[row][1] = getexpectedLonelyReelIndex(expectedLonelyTileMatrix, adjustRow, adjustColumn);
    }

    private int getexpectedLonelyReelIndex(int[] expectedLonelyTileMatrix, int adjustedRow, int adjustedColumn ) {
        int row = lonelyTestGrid.length - 1 - expectedLonelyTileMatrix[0] +adjustedRow;
        int column = expectedLonelyTileMatrix[1] + adjustedColumn;
        return lonelyTestGrid[row][column].index;
    }

    private int[][] createLonelyTilesMatrix() {
        String matrixToInput = "11 x 9\n"
                             + " 0  1  2  3  4  3  6  7  8  9  10\n"
                             + " 1  1  2  3  3  3  6  7  8  9   9\n"
                             + " 0  1  2  3  3  3  6  7  8  9  10\n"
                             + " 0  1  2  3  4  3  6  7  8  9  10\n"
                             + " 0  1  2  3  3  3  6  7  8  9  10\n"
                             + " 0  1  2  3  3  3  6  7  8  9  10\n"
                             + " 0  1  2  3  3  5  6  7  8  9  10\n"
                             + " 7  7 -1  3  3  3  6  7  8  9   9\n"
                             + " 0  7  2  3  4  3  6  7  8  9  10\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    private void createLonelyTilesGrid(int[][] matrix) {
        lonelyTestGrid = new TupleValueIndex[matrix.length][matrix[0].length];
        reelTiles = new Array<>();
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                lonelyTestGrid[r][c] = new TupleValueIndex(r, c, r * matrix[0].length + c, matrix[r][c]);
                ReelTile reelTileMock = PowerMock.createMock(ReelTile.class);
                Whitebox.setInternalState(reelTileMock,"x", PlayScreen.PUZZLE_GRID_START_X + (c * 40));
                Whitebox.setInternalState(reelTileMock,"y",(r  * 40) + PlayScreen.PUZZLE_GRID_START_Y);
                Whitebox.setInternalState(reelTileMock, "tileDeleted", matrix[r][c] < 0);
                Whitebox.setInternalState(reelTileMock, "index", r * lonelyTestGrid[0].length + c);
                reelTiles.add(reelTileMock);
            }
        }
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

    private int[][] createExpectedLonelyTilesMatrix() {
        String matrixToInput = "2 x 7\n"
                             + "0  0\n"
                             + "0  4\n"
                             + "0  10\n"
                             + "4  4\n"
                             + "8  0\n"
                             + "8  4\n"
                             + "8 10\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    private void relayAll(int[][] expectedLonelyTileMatrix) {
        replay(ReelTile.class,
               applicationMock);
        for (int i = 0 ; i < reelTiles.size; i++) {
            replay(reelTiles.get(i));
        }
    }

    private void verifyAll(int[][] expectedLonelyTileMatrix) {
        verify(ReelTile.class,
               applicationMock);
    }
}
