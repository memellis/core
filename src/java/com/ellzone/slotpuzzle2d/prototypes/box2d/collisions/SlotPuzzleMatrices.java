package com.ellzone.slotpuzzle2d.prototypes.box2d.collisions;

import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.utils.InputMatrix;

public class SlotPuzzleMatrices {

    public static int[][] createMatrixMimicingDynamicMatrixFailed1() {
        String matrixToInput = "12 x 9\n"
                + "-1  0 -1  0 -1 -1 -1 -1 -1  0  -1   0\n"
                + "-1 -1  0  0 -1 -1  0 -1 -1 -1   0   0\n"
                + "-1 -1 -1 -1  0 -1  0 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1  0  0   0  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixMimicingDynamicMatrixFailed2() {
        String matrixToInput = "12 x 9\n"
                + " 0  0 -1  0 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1  0  0 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0  0  0 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixMimicingDynamicMatrixFailed3() {
        String matrixToInput = "12 x 9\n"
                + "-1  0 -1  0 -1  0 -1  0 -1  0  -1   0\n"
                + "-1  0  0  0 -1 -1  0  0 -1 -1   0   0\n"
                + "-1  0 -1 -1  0  0  0  0 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1  0  0   0   0\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixMimicingDynamicMatrix() {
        String matrixToInput = "12 x 9\n"
                + "-1  0 -1  0 -1  0 -1  0 -1  0  -1   0\n"
                + "-1 -1  0  0 -1 -1  0  0 -1 -1   0   0\n"
                + "-1 -1 -1 -1  0  0  0  0 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1  0  0   0   0\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithNoBoxes() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

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
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
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
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
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
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithFourBoxes() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithFillColumnNineBoxes() {
        String matrixToInput = "12 x 9\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
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
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
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
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithThreeBoxesOnOneBox() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
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
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
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
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
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
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
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
                + " 0 -0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithTwoFillColumnNineBoxes() {
        String matrixToInput = "12 x 9\n"
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0  0 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
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
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithOneBoxGapThreeOnOneBox() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + " 0 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    public static int[][] createMatrixWithAFullMatrix() {
        String matrixToInput = "12 x 9\n"
                + " 0  0  0  0  0  0  0  0  0  0   0   0\n"
                + " 0  0  0  0  0  0  0  0  0  0   0   0\n"
                + " 0  0  0  0  0  0  0  0  0  0   0   0\n"
                + " 0  0  0  0  0  0  0  0  0  0   0   0\n"
                + " 0  0  0  0  0  0  0  0  0  0   0   0\n"
                + " 0  0  0  0  0  0  0  0  0  0   0   0\n"
                + " 0  0  0  0  0  0  0  0  0  0   0   0\n"
                + " 0  0  0  0  0  0  0  0  0  0   0   0\n"
                + " 0  0  0  0  0  0  0  0  0  0   0   0\n";
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
        slotMatrices.add(createMatrixMimicingDynamicMatrixFailed1());
        slotMatrices.add(createMatrixMimicingDynamicMatrixFailed2());
        slotMatrices.add(createMatrixMimicingDynamicMatrixFailed3());
        slotMatrices.add(createMatrixMimicingDynamicMatrix());
        slotMatrices.add(createMatrixWithNoBoxes());
        slotMatrices.add(createMatrixWithOneBox());
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

    public static Array<int[][]> getSlotMatricesTouchingBottom() {
        Array<int[][]> slotMatrices = new Array<>();
        slotMatrices.add(createMatrixWithOneBox());
        slotMatrices.add(createMatrixWithTwoBoxes());
        slotMatrices.add(createMatrixWithThreeBoxes());
        slotMatrices.add(createMatrixWithFourBoxes());
        slotMatrices.add(createMatrixWithTwoByOnOneBox());
        slotMatrices.add(createMatrixWithTwoByOnTwoBoxes());
        slotMatrices.add(createMatrixWithTwoByOnThreeBoxes());
        slotMatrices.add(createMatrixWithTwoByOnTFourBoxes());
        slotMatrices.add(createMatrixWithTwoFillColumnNineBoxes());
        return slotMatrices;
    }

    public static Array<AnimatedReel> createAnimatedReelsFromSlotPuzzleMatrix(int[][] slotPuzzleMatrix) {
        Array<AnimatedReel> animatedReels = new Array<AnimatedReel>();
        int numberOfAnimatedReelsCreated = 0;
        for (int r = 0; r < slotPuzzleMatrix.length; r++) {
            for (int c = 0; c < slotPuzzleMatrix[0].length; c++) {
                animatedReels.add(
                        createAnimatedReel(
                                (int) PlayScreen.PUZZLE_GRID_START_X + (c * 40),
                                ((slotPuzzleMatrix.length - 1 - r) * 40) + 40,
                                slotPuzzleMatrix[r][c],
                                numberOfAnimatedReelsCreated));
                if (slotPuzzleMatrix[r][c] < 0)
                    animatedReels.get(numberOfAnimatedReelsCreated).getReel().deleteReelTile();
                numberOfAnimatedReelsCreated++;
            }
        }
        return animatedReels;
    }

    private static AnimatedReel createAnimatedReel(int x, int y, int endReel, int index) {
        AnimatedReel animatedReel = getAnimatedReel(x, y, endReel);
        setUpReelTileInAnimatedReel(index, animatedReel);
        return animatedReel;
    }

    private static void setUpReelTileInAnimatedReel(int index, AnimatedReel animatedReel) {
        ReelTile reelTile = animatedReel.getReel();
        reelTile.setDestinationX(reelTile.getX());
        reelTile.setDestinationY(reelTile.getY());
        reelTile.setY(reelTile.getY());
        reelTile.setIsFallen(false);
        reelTile.setIsStoppedFalling(false);
        reelTile.setIndex(index);
    }

    private static AnimatedReel getAnimatedReel(int x, int y, int endReel) {
        AnimatedReel animatedReel = new AnimatedReel(
                null,
                x,
                y,
                40,
                40,
                40,
                40,
                0,
                null);
        animatedReel.setSx(0);
        animatedReel.setEndReel(endReel);
        animatedReel.setupSpinning();
        animatedReel.getReel().startSpinning();
        return animatedReel;
    }
}
