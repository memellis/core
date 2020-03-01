package com.ellzone.slotpuzzle2d.prototypes.box2d.collisions;

import com.ellzone.slotpuzzle2d.SlotPuzzle;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Test;

import java.text.MessageFormat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class TestCreateDynamicMatricesByIdentifier {
    public static final int MATRIX_WIDTH = 12;
    public static final int MATRIX_HEIGHT = 9;

    @Test
    public void testCreateDynamicMatrixWithEmptyCode() {
        int[] matrixIdentifier = new int[MATRIX_WIDTH];
        int [][] slotPuzzleMatrix =
                SlotPuzzleMatrices.createDynamicMatrix(
                        matrixIdentifier,
                        MATRIX_WIDTH,
                        MATRIX_HEIGHT);
        assertThat(slotPuzzleMatrix.length, is(equalTo(MATRIX_HEIGHT)));
        assertThat(slotPuzzleMatrix[0].length, is(equalTo(MATRIX_WIDTH)));
        assertMatrixIsEmpty(matrixIdentifier, slotPuzzleMatrix);
    }

    @Test
    public void testCreateDynamicMatrixWithZeroCode() {
        int[] matrixIdentifier = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int [][] slotPuzzleMatrix =
                SlotPuzzleMatrices.createDynamicMatrix(
                        matrixIdentifier,
                        MATRIX_WIDTH,
                        MATRIX_HEIGHT);
        assertThat(slotPuzzleMatrix.length, is(equalTo(MATRIX_HEIGHT)));
        assertThat(slotPuzzleMatrix[0].length, is(equalTo(MATRIX_WIDTH)));
        assertMatrixIsEmpty(matrixIdentifier, slotPuzzleMatrix);
    }

    @Test
    public void testCreateDynamicMatrixForAllCodes() {
        int[] matrixIdentifier = new int[MATRIX_WIDTH];
        for (int matrixValue = 0; matrixValue < Math.pow(2, MATRIX_HEIGHT); matrixValue++) {
            setArrayValues(matrixIdentifier, matrixValue);
            int [][] slotPuzzleMatrix =
                    SlotPuzzleMatrices.createDynamicMatrix(
                            matrixIdentifier,
                            MATRIX_WIDTH,
                            MATRIX_HEIGHT);
            assertThat(slotPuzzleMatrix.length, is(equalTo(MATRIX_HEIGHT)));
            assertThat(slotPuzzleMatrix[0].length, is(equalTo(MATRIX_WIDTH)));
            assertMatrixMatchesMatrixIdentifer(matrixIdentifier, slotPuzzleMatrix);
        }
    }

    @Test
    public void testCreateDynamicMatrixWithDifferentColumnValues() {
        int[] matrixIdentifier = new int[MATRIX_WIDTH];
        for (int matrixValue = 0; matrixValue < Math.pow(2, MATRIX_HEIGHT); matrixValue++) {
            setColumnValues(matrixIdentifier, matrixValue);
            int [][] slotPuzzleMatrix =
                    SlotPuzzleMatrices.createDynamicMatrix(
                            matrixIdentifier,
                            MATRIX_WIDTH,
                            MATRIX_HEIGHT);
            assertThat(slotPuzzleMatrix.length, is(equalTo(MATRIX_HEIGHT)));
            assertThat(slotPuzzleMatrix[0].length, is(equalTo(MATRIX_WIDTH)));
            assertMatrixMatchesMatrixIdentifer(matrixIdentifier, slotPuzzleMatrix);
        }
    }

    private void setColumnValues(int[] matrixIdentifier, int matrixValue) {
        for (int i=0; i<matrixIdentifier.length; i++)
            matrixIdentifier[i]=matrixValue + i;
    }

    private void setArrayValues(int[] array, int matrixValue) {
        for (int index=0; index<array.length; index++) {
            array[index] = matrixValue;
        }
    }

    private void assertMatrixIsEmpty(int[] matrixIdentifier, int[][] slotPuzzleMatrix) {
        for (int r=0; r<slotPuzzleMatrix.length; r++)
            for (int c=0; c<slotPuzzleMatrix.length; c++)
                assertThat(slotPuzzleMatrix[r][c],is(equalTo(-1)));
    }

    private void assertMatrixMatchesMatrixIdentifer(int[] matrixIdentifier, int[][] slotPuzzleMatrix) {
        for (int r=0; r<slotPuzzleMatrix.length; r++)
            for (int c=0; c<slotPuzzleMatrix.length; c++)
                assertThat(
                        slotPuzzleMatrix[r][c],
                        is(SlotPuzzleMatrices.extractBitAt(matrixIdentifier[c], r) == 1 ? 0 : -1));
    }

    private void printMatrix(int[][] matrix) {
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++)
                System.out.print(String.format("%3d", matrix[r][c]));
            System.out.println();
        }
    }
}
