package com.ellzone.slotpuzzle2d.prototypes.box2d.collisions;

import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.utils.InputMatrix;

public class SlotPuzzleMatrices {
    public static int[][] createMatrixWithOneBox() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithTwoBoxes() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithThreeBoxes() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithFillColumnNineBoxes() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithOneBoxOnOneBox() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithTwoBoxesOnOneBox() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithThreeBoxesOnOneBox() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithTwoByOnOneBox() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithTwoByOnTwoBoxes() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithTwoByOnThreeBoxes() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithTwoByOnTFourBoxes() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithTwoFillColumnNineBoxes() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0  0 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithOneBoxGapTwoOnOneBox() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithOneBoxGapThreeOnOneBox() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1  0 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createDynamicMatrix(int[] matrixIdentifier,
                                              int width,
                                              int height) {
        int[][] dynamicMatrix = new int[height][width];

        for (int r = 0; r < dynamicMatrix.length; r++)
            for (int c = 0; c < dynamicMatrix[0].length; c++) {
                dynamicMatrix[r][c] =  extractBitAt(matrixIdentifier[c], r) == 0 ? -1 : 0;
            }

        return dynamicMatrix;
    }

    public static int extractBitAt(int number, int position) {
        return (number >> position) & 1;
    }

    public static Array<int[][]> getSlotMatrices() {
        Array<int[][]> slotMatrices = new Array<>();
        slotMatrices.add(createMatrixWithOneBoxGapThreeOnOneBox());
        slotMatrices.add(createMatrixWithOneBoxGapTwoOnOneBox());
        slotMatrices.add(createMatrixWithFillColumnNineBoxes());
        slotMatrices.add(createMatrixWithThreeBoxesOnOneBox());
        slotMatrices.add(createMatrixWithThreeBoxesOnOneBox());
        slotMatrices.add(createMatrixWithThreeBoxesOnOneBox());
        slotMatrices.add(createMatrixWithOneBoxOnOneBox());
        slotMatrices.add(createMatrixWithTwoBoxesOnOneBox());
        slotMatrices.add(createMatrixWithThreeBoxesOnOneBox());
        slotMatrices.add(createMatrixWithOneBoxOnOneBox());
        slotMatrices.add(createMatrixWithTwoBoxesOnOneBox());
        slotMatrices.add(createMatrixWithThreeBoxesOnOneBox());
        slotMatrices.add(createMatrixWithTwoByOnOneBox());
        slotMatrices.add(createMatrixWithTwoByOnTwoBoxes());
        slotMatrices.add(createMatrixWithTwoByOnThreeBoxes());
        slotMatrices.add(createMatrixWithTwoByOnTFourBoxes());
        slotMatrices.add(createMatrixWithTwoFillColumnNineBoxes());
        slotMatrices.add(createMatrixWithOneBox());
        slotMatrices.add(createMatrixWithTwoBoxes());
        slotMatrices.add(createMatrixWithThreeBoxes());
        slotMatrices.add(createMatrixWithFillColumnNineBoxes());
        return slotMatrices;
    }

}
