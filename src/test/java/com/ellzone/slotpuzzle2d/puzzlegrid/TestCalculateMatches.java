package com.ellzone.slotpuzzle2d.puzzlegrid;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.level.reel.ReelGrid;
import com.ellzone.slotpuzzle2d.utils.InputMatrix;
import com.ellzone.slotpuzzle2d.utils.InputVector2AsArray;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class TestCalculateMatches {
    private static final int TEST_GRID_WIDTH = 5;
    private static final int TEST_GRID_HEIGHT = 5;
    private ReelGrid reelGrid;
    private CalculateMatches calculateMatches;


    @BeforeEach
    public void setUp() {
        reelGrid = createReel();
        calculateMatches = new CalculateMatches();
    }

    @AfterEach
    public void tearDown() {
        reelGrid = null;
        calculateMatches = null;
    }

    @Test
    public void testOnePairReelsMatchGrid() {
        Array<Array<Vector2>> matchPattern =
                calculateMatchPattern(onePairReelsMatchGrid());
        assertThat(matchPattern.size, is(equalTo(0)));
    }

    @Test
    public void testOneRowMatchGrid() {
        Array<Array<Vector2>> matchPattern =
                calculateMatchPattern(oneRowMatchGrid());
        assertThat(matchPattern.size, is(equalTo(1)));
    }

    @Test
    public void testOneRowAcrossDifferentRows() {
        Array<Array<Vector2>> matchPattern =
                calculateMatchPattern(
                        oneRowAcrossDifferentRowsMatchGrid());
        assertThat(matchPattern.size, is(equalTo(1)));
    }

    @Test
    public void testTwoRowAcrossDifferentRows() {
        Array<Array<Vector2>> matchPattern =
                calculateMatchPattern(
                        twoRowsAcrossDifferentRowsMatchGrid());
        assertThat(matchPattern.size, is(equalTo(2)));
    }

    @Test
    public void testDiagonalsAcrossDifferentRowsMatchGrid() {
        Array<Array<Vector2>> matchPattern =
                calculateMatchPattern(
                        diagonalsAcrossDifferentRowsMatchGrid());
        assertThat(matchPattern.size, is(equalTo(3)));
    }

    @Test
    public void testInterCrossRowsMatchGrid() {
        Array<Array<Vector2>> matchPattern =
                calculateMatchPattern(interCrossRowsMatchGrid());
        Array<Array<Vector2>> expectedMatchPattern = getExpectedInterCrossRows();
        assertMatchedPattern(matchPattern, expectedMatchPattern);
    }

    @Test
    public void testInterCrossRows2MatchGrid() {
        Array<Array<Vector2>> matchPattern =
                calculateMatchPattern(interCrossRows2MatchGrid());
        Array<Array<Vector2>> expectedMatchPattern = getExpectedInterCrossRows2();
        assertMatchedPattern(matchPattern, expectedMatchPattern);
    }

    private Array<Array<Vector2>> calculateMatchPattern(int[][] grid) {
        return calculateMatches.process(grid, reelGrid);
    }

    private static int[][] onePairReelsMatchGrid() {
        String matrixToInput = getMatrixSizeAsString()
                + " 1  1  2  3  4\n"
                + " 0  4  5  1  5\n"
                + " 2  3  0  6  0\n"
                + " 6  1  5  3  1\n"
                + " 4  5  0  1  2\n";
        return
                new InputMatrix(matrixToInput).readMatrix();
    }

    private static int[][] oneRowMatchGrid() {
        String matrixToInput = getMatrixSizeAsString()
                + " 1  1  1  1  1\n"
                + " 0  4  5  2  5\n"
                + " 2  3  0  6  0\n"
                + " 6  1  5  3  1\n"
                + " 4  5  0  1  2\n";
        return
                new InputMatrix(matrixToInput).readMatrix();
    }

    private static int[][] oneRowAcrossDifferentRowsMatchGrid() {
        String matrixToInput = getMatrixSizeAsString()
                + " 1  2  3  4  0\n"
                + " 0  1  5  2  5\n"
                + " 2  3  1  1  0\n"
                + " 6  1  5  3  1\n"
                + " 4  5  0  4  2\n";
        return
                new InputMatrix(matrixToInput).readMatrix();
    }

    private static int[][] twoRowsAcrossDifferentRowsMatchGrid() {
        String matrixToInput = getMatrixSizeAsString()
                + " 1  2  3  4  0\n"
                + " 0  1  5  2  5\n"
                + " 2  0  1  1  0\n"
                + " 6  1  0  0  1\n"
                + " 4  5  0  4  0\n";
        return
                new InputMatrix(matrixToInput).readMatrix();
    }

    private static int[][] diagonalsAcrossDifferentRowsMatchGrid() {
        String matrixToInput = getMatrixSizeAsString()
                + " 0  1  2  1  0\n"
                + " 1  2  3  2  1\n"
                + " 2  3  4  3  2\n"
                + " 3  4  5  4  3\n"
                + " 4  5  6  4  4\n";
        return
                new InputMatrix(matrixToInput).readMatrix();
    }

    private static int[][] interCrossRowsMatchGrid() {
        String matrixToInput = getMatrixSizeAsString()
                + " 0  0  0  1  1\n"
                + " 1  1  1  0  0\n"
                + " 2  2  2  3  3\n"
                + " 3  3  3  2  2\n"
                + " 4  4  4  4  4\n";
        return
                new InputMatrix(matrixToInput).readMatrix();
    }

    private static int[][] interCrossRows2MatchGrid() {
        String matrixToInput = getMatrixSizeAsString()
                + " 1  1  2  2  1\n"
                + " 2  2  3  3  2\n"
                + " 3  3  4  4  3\n"
                + " 4  4  5  5  4\n"
                + " 5  5  6  6  5\n";
        return
                new InputMatrix(matrixToInput).readMatrix();
    }

    private static String getMatrixSizeAsString() {
        return String.valueOf(TestCalculateMatches.TEST_GRID_WIDTH) + " x " +
                TestCalculateMatches.TEST_GRID_HEIGHT + "\n";
    }

    private ReelGrid createReel() {
        return new ReelGrid(120, 400,  200, 200);
    }

    private Array<Array<Vector2>> getExpectedInterCrossRows() {
        Array<Array<Vector2>> expectedPattern = new Array<>();
        expectedPattern.add(
                new InputVector2AsArray("140 580 180 580 220 580 260 540 300 540")
                        .readVector2s());
        expectedPattern.add(
                new InputVector2AsArray("140 540 180 540 220 540 260 580 300 580")
                        .readVector2s());
        expectedPattern.add(
                new InputVector2AsArray("140 500 180 500 220 500 260 460 300 460")
                        .readVector2s());
        expectedPattern.add(
                new InputVector2AsArray("140 460 180 460 220 460 260 500 300 500")
                        .readVector2s());
        expectedPattern.add(
                new InputVector2AsArray("140 420 180 420 220 420 260 420 300 420")
                        .readVector2s());
        return expectedPattern;
    }

    private Array<Array<Vector2>> getExpectedInterCrossRows2() {
        Array<Array<Vector2>> expectedPattern = new Array<>();
        expectedPattern.add(
                new InputVector2AsArray("140 540 180 540 220 580 260 580 300 540")
                        .readVector2s());
        expectedPattern.add(
                new InputVector2AsArray("140 500 180 500 220 540 260 540 300 500")
                        .readVector2s());
        expectedPattern.add(
                new InputVector2AsArray("140 460 180 460 220 500 260 500 300 460")
                        .readVector2s());
        expectedPattern.add(
                new InputVector2AsArray("140 420 180 420 220 460 260 460 300 420")
                        .readVector2s());
        return expectedPattern;
    }

    private void assertMatchedPattern(
            Array<Array<Vector2>> matchPattern,
            Array<Array<Vector2>> expectedMatchPattern) {
        assertThat(matchPattern.equals(expectedMatchPattern), is(true));
    }
}
