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

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import junitparams.Parameters;
import static org.hamcrest.CoreMatchers.*;

public class    TestPuzzleGridType {
    private static final int GRID_SIZE_X = 4;
    private static final int GRID_SIZE_Y = 4;

    @Test
    public void testMatchRowSlots() {
        PuzzleGridType puzzleGridType = new PuzzleGridType();
        TupleValueIndex[][] testPuzzleGrid = new TupleValueIndex[TestPuzzleGridType.GRID_SIZE_X][TestPuzzleGridType.GRID_SIZE_Y];
        TupleValueIndex[][] expectedPuzzleGrid = new TupleValueIndex[TestPuzzleGridType.GRID_SIZE_X][TestPuzzleGridType.GRID_SIZE_Y];

        testPuzzleGrid[0][0] = new TupleValueIndex(0, 0, 0, -1);
        testPuzzleGrid[0][1] = new TupleValueIndex(0, 1, 1, -1);
        testPuzzleGrid[0][2] = new TupleValueIndex(0, 2, 2, 0);
        testPuzzleGrid[0][3] = new TupleValueIndex(0, 3, 3, 0);

        testPuzzleGrid[1][0] = new TupleValueIndex(1, 0, 4, 1);
        testPuzzleGrid[1][1] = new TupleValueIndex(1, 1, 5, 1);
        testPuzzleGrid[1][2] = new TupleValueIndex(1, 2, 6, 1);
        testPuzzleGrid[1][3] = new TupleValueIndex(1, 3, 7, 1);

        testPuzzleGrid[2][0] = new TupleValueIndex(2, 0, 8, -1);
        testPuzzleGrid[2][1] = new TupleValueIndex(2, 1, 9, -1);
        testPuzzleGrid[2][2] = new TupleValueIndex(2, 2, 10, -1);
        testPuzzleGrid[2][3] = new TupleValueIndex(2, 3, 11, -1);

        testPuzzleGrid[3][0] = new TupleValueIndex(3, 0, 12, 3);
        testPuzzleGrid[3][1] = new TupleValueIndex(3, 1, 13, 3);
        testPuzzleGrid[3][2] = new TupleValueIndex(3, 2, 14, 3);
        testPuzzleGrid[3][3] = new TupleValueIndex(3, 3, 15, -1);

        expectedPuzzleGrid[0][0] = new TupleValueIndex(0, 0, 0, -1);
        expectedPuzzleGrid[0][1] = new TupleValueIndex(0, 1, 1, -1);
        expectedPuzzleGrid[0][2] = new TupleValueIndex(0, 2, 2, 2);
        expectedPuzzleGrid[0][3] = new TupleValueIndex(0, 3, 3, 2);

        expectedPuzzleGrid[1][0] = new TupleValueIndex(1, 0, 4, 4);
        expectedPuzzleGrid[1][1] = new TupleValueIndex(1, 1, 5, 4);
        expectedPuzzleGrid[1][2] = new TupleValueIndex(1, 2, 6, 4);
        expectedPuzzleGrid[1][3] = new TupleValueIndex(1, 3, 7, 4);

        expectedPuzzleGrid[2][0] = new TupleValueIndex(2, 0, 8, -1);
        expectedPuzzleGrid[2][1] = new TupleValueIndex(2, 1, 9, -1);
        expectedPuzzleGrid[2][2] = new TupleValueIndex(2, 2, 10, -1);
        expectedPuzzleGrid[2][3] = new TupleValueIndex(2, 3, 11, -1);

        expectedPuzzleGrid[3][0] = new TupleValueIndex(3, 0, 12, 3);
        expectedPuzzleGrid[3][1] = new TupleValueIndex(3, 1, 13, 3);
        expectedPuzzleGrid[3][2] = new TupleValueIndex(3, 2, 14, 3);
        expectedPuzzleGrid[3][3] = new TupleValueIndex(3, 3, 15, -1);

        TupleValueIndex[][] resultGridPuzzleGrid = puzzleGridType.matchRowSlots(testPuzzleGrid);
        assertTrueGridsAreEqual(expectedPuzzleGrid, resultGridPuzzleGrid);
    }

    @Test
    public void testMatchColumnSlots() {
        PuzzleGridType puzzleGridType = new PuzzleGridType();
        TupleValueIndex[][] testPuzzleGrid = new TupleValueIndex[TestPuzzleGridType.GRID_SIZE_X][TestPuzzleGridType.GRID_SIZE_Y];
        TupleValueIndex[][] expectedPuzzleGrid = new TupleValueIndex[TestPuzzleGridType.GRID_SIZE_X][TestPuzzleGridType.GRID_SIZE_Y];

        testPuzzleGrid[0][0] = new TupleValueIndex(0, 0, 0, 3);
        testPuzzleGrid[0][1] = new TupleValueIndex(0, 1, 1, 0);
        testPuzzleGrid[0][2] = new TupleValueIndex(0, 2, 2, 0);
        testPuzzleGrid[0][3] = new TupleValueIndex(0, 3, 3, 0);

        testPuzzleGrid[1][0] = new TupleValueIndex(1, 0, 4, 3);
        testPuzzleGrid[1][1] = new TupleValueIndex(1, 1, 5, 1);
        testPuzzleGrid[1][2] = new TupleValueIndex(1, 2, 6, 4);
        testPuzzleGrid[1][3] = new TupleValueIndex(1, 3, 7, 4);

        testPuzzleGrid[2][0] = new TupleValueIndex(2, 0, 8, 3);
        testPuzzleGrid[2][1] = new TupleValueIndex(2, 1, 9, 3);
        testPuzzleGrid[2][2] = new TupleValueIndex(2, 2, 10, 4);
        testPuzzleGrid[2][3] = new TupleValueIndex(2, 3, 11, 5);

        testPuzzleGrid[3][0] = new TupleValueIndex(3, 0, 12, 3);
        testPuzzleGrid[3][1] = new TupleValueIndex(3, 1, 13, 3);
        testPuzzleGrid[3][2] = new TupleValueIndex(3, 2, 14, 4);
        testPuzzleGrid[3][3] = new TupleValueIndex(3, 3, 15, 5);

        expectedPuzzleGrid[0][0] = new TupleValueIndex(0, 0, 0, 4);
        expectedPuzzleGrid[0][1] = new TupleValueIndex(0, 1, 1, 1);
        expectedPuzzleGrid[0][2] = new TupleValueIndex(0, 2, 2, 1);
        expectedPuzzleGrid[0][3] = new TupleValueIndex(0, 3, 3, 1);

        expectedPuzzleGrid[1][0] = new TupleValueIndex(1, 0, 4, 4);
        expectedPuzzleGrid[1][1] = new TupleValueIndex(1, 1, 5, 1);
        expectedPuzzleGrid[1][2] = new TupleValueIndex(1, 2, 6, 3);
        expectedPuzzleGrid[1][3] = new TupleValueIndex(1, 3, 7, 1);

        expectedPuzzleGrid[2][0] = new TupleValueIndex(2, 0, 8, 4);
        expectedPuzzleGrid[2][1] = new TupleValueIndex(2, 1, 9, 2);
        expectedPuzzleGrid[2][2] = new TupleValueIndex(2, 2, 10, 3);
        expectedPuzzleGrid[2][3] = new TupleValueIndex(2, 3, 11, 2);

        expectedPuzzleGrid[3][0] = new TupleValueIndex(3, 0, 12, 4);
        expectedPuzzleGrid[3][1] = new TupleValueIndex(3, 1, 13, 2);
        expectedPuzzleGrid[3][2] = new TupleValueIndex(3, 2, 14, 3);
        expectedPuzzleGrid[3][3] = new TupleValueIndex(3, 3, 15, 2);

        TupleValueIndex[][] resultGridPuzzleGrid = puzzleGridType.matchColumnSlots(testPuzzleGrid);
        assertTrueGridsAreEqual(expectedPuzzleGrid, resultGridPuzzleGrid);
    }

    @Test
    public void testFindAnyLonelyTiles() {
        PuzzleGridType puzzleGridType = new PuzzleGridType();
        TupleValueIndex[][] testPuzzleGrid = new TupleValueIndex[TestPuzzleGridType.GRID_SIZE_X][TestPuzzleGridType.GRID_SIZE_Y];

        testPuzzleGrid[0][0] = new TupleValueIndex(0, 0, 0, 3);
        testPuzzleGrid[0][1] = new TupleValueIndex(0, 1, 1, -1);
        testPuzzleGrid[0][2] = new TupleValueIndex(0, 2, 2, 0);
        testPuzzleGrid[0][3] = new TupleValueIndex(0, 3, 3, 0);

        testPuzzleGrid[1][0] = new TupleValueIndex(1, 0, 4, -1);
        testPuzzleGrid[1][1] = new TupleValueIndex(1, 1, 5, -1);
        testPuzzleGrid[1][2] = new TupleValueIndex(1, 2, 6, -1);
        testPuzzleGrid[1][3] = new TupleValueIndex(1, 3, 7, -1);

        testPuzzleGrid[2][0] = new TupleValueIndex(2, 0, 8, 3);
        testPuzzleGrid[2][1] = new TupleValueIndex(2, 1, 9, -1);
        testPuzzleGrid[2][2] = new TupleValueIndex(2, 2, 10, 4);
        testPuzzleGrid[2][3] = new TupleValueIndex(2, 3, 11, -1);

        testPuzzleGrid[3][0] = new TupleValueIndex(3, 0, 12, 3);
        testPuzzleGrid[3][1] = new TupleValueIndex(3, 1, 13, -1);
        testPuzzleGrid[3][2] = new TupleValueIndex(3, 2, 14, -1);
        testPuzzleGrid[3][3] = new TupleValueIndex(3, 3, 15, -1);
        Assertions.assertTrue(puzzleGridType.anyLonelyTiles(testPuzzleGrid));

        testPuzzleGrid[0][0] = new TupleValueIndex(0, 0, 0, -1);
        Assertions.assertTrue(puzzleGridType.anyLonelyTiles(testPuzzleGrid));

        testPuzzleGrid[2][2] = new TupleValueIndex(2, 2, 10, -1);
        Assertions.assertFalse(puzzleGridType.anyLonelyTiles(testPuzzleGrid));
    }

    @Test
    public void testFindAnyLonelyTilesInitialLevel() {
        PuzzleGridType puzzleGridType = new PuzzleGridType();
        TupleValueIndex[][] testPuzzleGrid = new TupleValueIndex[TestPuzzleGridType.GRID_SIZE_X][TestPuzzleGridType.GRID_SIZE_Y];
        testPuzzleGrid[0][0] = new TupleValueIndex(3, 0, 0, 3);
        testPuzzleGrid[0][1] = new TupleValueIndex(3, 1, 1, 0);
        testPuzzleGrid[0][2] = new TupleValueIndex(3, 2, 2, 0);
        testPuzzleGrid[0][3] = new TupleValueIndex(3, 3, 3, 0);

        testPuzzleGrid[1][0] = new TupleValueIndex(2, 0, 4, 0);
        testPuzzleGrid[1][1] = new TupleValueIndex(2, 1, 5, 0);
        testPuzzleGrid[1][2] = new TupleValueIndex(2, 2, 6, 2);
        testPuzzleGrid[1][3] = new TupleValueIndex(2, 3, 7, 2);

        testPuzzleGrid[2][0] = new TupleValueIndex(1, 0, 8, 3);
        testPuzzleGrid[2][1] = new TupleValueIndex(1, 1, 9, 3);
        testPuzzleGrid[2][2] = new TupleValueIndex(1, 2, 10, 4);
        testPuzzleGrid[2][3] = new TupleValueIndex(1, 3, 11, 3);

        testPuzzleGrid[3][0] = new TupleValueIndex(0, 0, 12, 3);
        testPuzzleGrid[3][1] = new TupleValueIndex(0, 1, 13, 3);
        testPuzzleGrid[3][2] = new TupleValueIndex(0, 2, 14, 3);
        testPuzzleGrid[3][3] = new TupleValueIndex(0, 3, 15, 3);

        Assertions.assertTrue(puzzleGridType.anyLonelyTiles(testPuzzleGrid));

        testPuzzleGrid[0][0] = new TupleValueIndex(0, 0, 0, 0);
        Assertions.assertTrue(puzzleGridType.anyLonelyTiles(testPuzzleGrid));

        testPuzzleGrid[2][2] = new TupleValueIndex(2, 2, 10, 3);
        Assertions.assertFalse(puzzleGridType.anyLonelyTiles(testPuzzleGrid));
    }


    private void assertTrueGridsAreEqual(TupleValueIndex[][] first, TupleValueIndex[][] second) {
        for (int x = 0; x < first.length; x++) {
            for (int y = 0; y < first[x].length; y++) {
                Assertions.assertEquals(first[x][y].r, second[x][y].r);
                Assertions.assertEquals(first[x][y].c, second[x][y].c);
                Assertions.assertEquals(first[x][y].index, second[x][y].index);
                Assertions.assertEquals(first[x][y].value, second[x][y].value);
                Assertions.assertEquals(first[x][y].getR(), second[x][y].getR());
                Assertions.assertEquals(first[x][y].getC(), second[x][y].getC());
                Assertions.assertEquals(first[x][y].getIndex(), second[x][y].getIndex());
                Assertions.assertEquals(first[x][y].getValue(), second[x][y].getValue());
            }
        }
    }

    @Test
    @Disabled
    @Parameters(method = "puzzleGridData")
    public void testRowsAboveMe(TupleValueIndex[][] testPuzzleGrid, int r, int c, TupleValueIndex[] expectedReelsAbove) {
        TupleValueIndex[] reelsAboveMe = PuzzleGridType.getReelsAboveMe(testPuzzleGrid, r, c);
        MatcherAssert.assertThat(reelsAboveMe.length, is(expectedReelsAbove.length));
        for (int index=0; index<reelsAboveMe.length; index++) {
            MatcherAssert.assertThat(reelsAboveMe[index].r, is(expectedReelsAbove[index].r));
            MatcherAssert.assertThat(reelsAboveMe[index].c, is(expectedReelsAbove[index].c));
            MatcherAssert.assertThat(reelsAboveMe[index].index, is(expectedReelsAbove[index].index));
            MatcherAssert.assertThat(reelsAboveMe[index].value, is(expectedReelsAbove[index].value));
        }
    }

    private Object puzzleGridData() {
        TupleValueIndex[][] testPuzzleGrid = new TupleValueIndex[TestPuzzleGridType.GRID_SIZE_X][TestPuzzleGridType.GRID_SIZE_Y];
        testPuzzleGrid[0][0] = new TupleValueIndex(0, 0, 0, 3);
        testPuzzleGrid[0][1] = new TupleValueIndex(0, 1, 1, 3);
        testPuzzleGrid[0][2] = new TupleValueIndex(0, 2, 2, 3);
        testPuzzleGrid[0][3] = new TupleValueIndex(0, 3, 3, -1);

        testPuzzleGrid[1][0] = new TupleValueIndex(1, 0, 4, -1);
        testPuzzleGrid[1][1] = new TupleValueIndex(1, 1, 5, -1);
        testPuzzleGrid[1][2] = new TupleValueIndex(1, 2, 6, -1);
        testPuzzleGrid[1][3] = new TupleValueIndex(1, 3, 7, -1);

        testPuzzleGrid[2][0] = new TupleValueIndex(2, 0, 8, 1);
        testPuzzleGrid[2][1] = new TupleValueIndex(2, 1, 9, 1);
        testPuzzleGrid[2][2] = new TupleValueIndex(2, 2, 10, 1);
        testPuzzleGrid[2][3] = new TupleValueIndex(2, 3, 11, 1);

        testPuzzleGrid[3][0] = new TupleValueIndex(3, 0, 12, -1);
        testPuzzleGrid[3][1] = new TupleValueIndex(3, 1, 13, -1);
        testPuzzleGrid[3][2] = new TupleValueIndex(3, 2, 14, 0);
        testPuzzleGrid[3][3] = new TupleValueIndex(3, 3, 15, 0);

        int[] r = new int[] { 3, 3, 3, 3 };
        int[] c = new int[] { 0, 1, 2, 3 };

        TupleValueIndex[][] expectedTiles = new TupleValueIndex[][] {
                {new TupleValueIndex(2, 0, 8, 1),
                 new TupleValueIndex(0, 0, 0, 3)},
                {new TupleValueIndex(2, 1, 9, 1),
                 new TupleValueIndex(0, 1, 1, 3)},
                {new TupleValueIndex(2, 2, 10, 1),
                 new TupleValueIndex(0, 2, 2, 3)},
                {new TupleValueIndex(2, 3, 11, 1)}
        };

        return new Object[] {
            new Object[] {
                    testPuzzleGrid,
                    r[0],
                    c[0],
                    expectedTiles[0]
            },
            new Object[] {
                    testPuzzleGrid,
                    r[1],
                    c[1],
                    expectedTiles[1]
            }
        };
     }
}
