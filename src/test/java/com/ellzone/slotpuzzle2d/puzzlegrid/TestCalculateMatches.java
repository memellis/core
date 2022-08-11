package com.ellzone.slotpuzzle2d.puzzlegrid;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.utils.InputMatrix;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class TestCalculateMatches {
    private static final int TEST_GRID_WIDTH = 5;
    private static final int TEST_GRID_HEIGHT = 5;

    @Test
    public void testOnePairReelsMatchGrid() {
        int[][] testReelGrid = onePairReelsMatchGrid(TEST_GRID_WIDTH, TEST_GRID_HEIGHT);
        CalculateMatches calculateMatches = new CalculateMatches();
        Array<Array<Vector2>> matchPattern = calculateMatches.process(testReelGrid);
        assertThat(matchPattern.size, is(equalTo(0)));
    }

    @Test
    public void testOneRowMatchGrid() {
        int[][] testReelGrid = oneRowMatchGrid(TEST_GRID_WIDTH, TEST_GRID_HEIGHT);
        CalculateMatches calculateMatches = new CalculateMatches();
        Array<Array<Vector2>> matchPattern = calculateMatches.process(testReelGrid);
        assertThat(matchPattern.size, is(equalTo(1)));
    }

    @Test
    public void testOneRowAcrossDifferentRows() {
        int[][] testReelGrid = oneRowAcrossDifferentRowsMatchGrid(TEST_GRID_WIDTH, TEST_GRID_HEIGHT);
        CalculateMatches calculateMatches = new CalculateMatches();
        Array<Array<Vector2>> matchPattern = calculateMatches.process(testReelGrid);
        assertThat(matchPattern.size, is(equalTo(1)));
    }

    @Test
    public void testTwoRowAcrossDifferentRows() {
        int[][] testReelGrid = twoRowsAcrossDifferentRowsMatchGrid(TEST_GRID_WIDTH, TEST_GRID_HEIGHT);
        CalculateMatches calculateMatches = new CalculateMatches();
        Array<Array<Vector2>> matchPattern = calculateMatches.process(testReelGrid);
        assertThat(matchPattern.size, is(equalTo(2)));
    }

    private static int[][] onePairReelsMatchGrid(int testGridWidth, int testGridHeight) {
        String matrixToInput = getMatrixSizeAsString(testGridWidth, testGridHeight)
                + " 1  1  2  3  4\n"
                + " 0  4  5  1  5\n"
                + " 2  3  0  6  0\n"
                + " 6  1  5  3  1\n"
                + " 4  5  0  1  2\n";
        return
                new InputMatrix(matrixToInput).readMatrix();
    }

    private static int[][] oneRowMatchGrid(int testGridWidth, int testGridHeight) {
        String matrixToInput = getMatrixSizeAsString(testGridWidth, testGridHeight)
                + " 1  1  1  1  1\n"
                + " 0  4  5  2  5\n"
                + " 2  3  0  6  0\n"
                + " 6  1  5  3  1\n"
                + " 4  5  0  1  2\n";
        return
                new InputMatrix(matrixToInput).readMatrix();
    }

    private static int[][] oneRowAcrossDifferentRowsMatchGrid(int testGridWidth, int testGridHeight) {
        String matrixToInput = getMatrixSizeAsString(testGridWidth, testGridHeight)
                + " 1  2  3  4  0\n"
                + " 0  1  5  2  5\n"
                + " 2  3  1  1  0\n"
                + " 6  1  5  3  1\n"
                + " 4  5  0  4  2\n";
        return
                new InputMatrix(matrixToInput).readMatrix();
    }

    private static int[][] twoRowsAcrossDifferentRowsMatchGrid(int testGridWidth, int testGridHeight) {
        String matrixToInput = getMatrixSizeAsString(testGridWidth, testGridHeight)
                + " 1  2  3  4  0\n"
                + " 0  1  5  2  5\n"
                + " 2  0  1  1  0\n"
                + " 6  1  0  0  1\n"
                + " 4  5  0  4  0\n";
        return
                new InputMatrix(matrixToInput).readMatrix();
    }

        private static String getMatrixSizeAsString(int testGridWidth, int testGridHeight) {
        return String.valueOf(testGridWidth) + " x " +
               String.valueOf(testGridHeight) + "\n";
    }
}
