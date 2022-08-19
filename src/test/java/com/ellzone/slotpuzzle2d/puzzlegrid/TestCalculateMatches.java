package com.ellzone.slotpuzzle2d.puzzlegrid;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.level.reel.ReelGrid;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.ellzone.slotpuzzle2d.utils.InputMatrix;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class TestCalculateMatches {
    private static final int TEST_GRID_WIDTH = 5;
    private static final int TEST_GRID_HEIGHT = 5;
    private ReelGrid reelGrid;
    private CalculateMatches calculateMatches;


    @Before
    public void setUp() {
        reelGrid = createReel();
        calculateMatches = new CalculateMatches();
    }


    @After
    public void tearDown() {
        reelGrid = null;
        calculateMatches = null;
    }

    @Test
    public void testOnePairReelsMatchGrid() {
        Array<Array<Vector2>> matchPattern =
                calculateMatchPattern(onePairReelsMatchGrid(TEST_GRID_WIDTH, TEST_GRID_HEIGHT));
        assertThat(matchPattern.size, is(equalTo(0)));
    }

    @Test
    public void testOneRowMatchGrid() {
        Array<Array<Vector2>> matchPattern =
                calculateMatchPattern(oneRowMatchGrid(TEST_GRID_WIDTH, TEST_GRID_HEIGHT));
        assertThat(matchPattern.size, is(equalTo(1)));
    }

    @Test
    public void testOneRowAcrossDifferentRows() {
        Array<Array<Vector2>> matchPattern =
                calculateMatchPattern(
                        oneRowAcrossDifferentRowsMatchGrid(TEST_GRID_WIDTH, TEST_GRID_HEIGHT));
        assertThat(matchPattern.size, is(equalTo(1)));
    }

    @Test
    public void testTwoRowAcrossDifferentRows() {
        Array<Array<Vector2>> matchPattern =
                calculateMatchPattern(
                        twoRowsAcrossDifferentRowsMatchGrid(TEST_GRID_WIDTH, TEST_GRID_HEIGHT));
        assertThat(matchPattern.size, is(equalTo(2)));
    }

    @Test
    public void diagonalsAcrossDifferentRowsMatchGrid() {
        Array<Array<Vector2>> matchPattern =
                calculateMatchPattern(
                        diagonalsAcrossDifferentRowsMatchGrid(TEST_GRID_WIDTH, TEST_GRID_HEIGHT));
        assertThat(matchPattern.size, is(equalTo(3)));
    }

    @Test
    public void interCrossRowsMatchGrid() {
        Array<Array<Vector2>> matchPattern =
                calculateMatchPattern(interCrossRowsMatchGrid(TEST_GRID_WIDTH, TEST_GRID_HEIGHT));
        assertThat(matchPattern.size, is(equalTo(5)));
        printMatchPattern(matchPattern);
    }

    @Test
    public void interCrossRows2MatchGrid() {
        Array<Array<Vector2>> matchPattern =
                calculateMatchPattern(interCrossRows2MatchGrid(TEST_GRID_WIDTH, TEST_GRID_HEIGHT));
        assertThat(matchPattern.size, is(equalTo(4)));
        printMatchPattern(matchPattern);
    }

    private Array<Array<Vector2>> calculateMatchPattern(int[][] grid) {
        int[][] testReelGrid = grid;
        return calculateMatches.process(testReelGrid, reelGrid);
    }

    private void printMatchPattern(Array<Array<Vector2>> matchPattern) {
        for (Array<Vector2> matchedRow : new Array.ArrayIterator<>(matchPattern)) {
            for (Vector2 vector2 : new Array.ArrayIterator<>(matchedRow))
                System.out.println(vector2);
            System.out.println();
        }
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

    private static int[][] diagonalsAcrossDifferentRowsMatchGrid(int testGridWidth, int testGridHeight) {
        String matrixToInput = getMatrixSizeAsString(testGridWidth, testGridHeight)
                + " 0  1  2  1  0\n"
                + " 1  2  3  2  1\n"
                + " 2  3  4  3  2\n"
                + " 3  4  5  4  3\n"
                + " 4  5  6  4  4\n";
        return
                new InputMatrix(matrixToInput).readMatrix();
    }

    private static int[][] interCrossRowsMatchGrid(int testGridWidth, int testGridHeight) {
        String matrixToInput = getMatrixSizeAsString(testGridWidth, testGridHeight)
                + " 0  0  0  1  1\n"
                + " 1  1  1  0  0\n"
                + " 2  2  2  3  3\n"
                + " 3  3  3  2  2\n"
                + " 4  4  4  4  4\n";
        return
                new InputMatrix(matrixToInput).readMatrix();
    }

    private static int[][] interCrossRows2MatchGrid(int testGridWidth, int testGridHeight) {
        String matrixToInput = getMatrixSizeAsString(testGridWidth, testGridHeight)
                + " 1  1  2  2  1\n"
                + " 2  2  3  3  2\n"
                + " 3  3  4  4  3\n"
                + " 4  4  5  5  4\n"
                + " 5  5  6  6  5\n";
        return
                new InputMatrix(matrixToInput).readMatrix();
    }

    private static String getMatrixSizeAsString(int testGridWidth, int testGridHeight) {
        return String.valueOf(testGridWidth) + " x " +
               String.valueOf(testGridHeight) + "\n";
    }

    private ReelGrid createReel() {
        return new ReelGrid(120, 400,  200, 200);
    }
}
