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

    public static Array<int[][]> getSlotMatrices() {
        Array<int[][]> slotMatrices = new Array<>();
        slotMatrices.add(createMatrixWithFillColumnNineBoxes());
        slotMatrices.add(createMatrixWithOneBoxGapTwoOnOneBox());
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
        slotMatrices.add(createMatrixWithOneBox());
        slotMatrices.add(createMatrixWithTwoBoxes());
        slotMatrices.add(createMatrixWithThreeBoxes());
        slotMatrices.add(createMatrixWithFillColumnNineBoxes());
        return slotMatrices;
    }
}
