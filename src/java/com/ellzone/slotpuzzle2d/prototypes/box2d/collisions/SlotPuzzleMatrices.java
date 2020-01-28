package com.ellzone.slotpuzzle2d.prototypes.box2d.collisions;

import com.ellzone.slotpuzzle2d.utils.InputMatrix;

public class SlotPuzzleMatrices {
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
}
