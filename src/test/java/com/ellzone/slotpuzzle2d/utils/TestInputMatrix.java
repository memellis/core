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

package com.ellzone.slotpuzzle2d.utils;

import org.junit.Test;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestInputMatrix {

    @Test(expected = NoSuchElementException.class)
    public void testInputMatrixWithSizeOnlySpecified() {
        String matrixToInput = "3 x 3\n"
                             + "0 1 2\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        int[][] matrix = inputMatrix.readMatrix();
        assertMatrixSize(matrix, 3, 3);
    }

    @Test(expected = InputMismatchException.class)
    public void testInputMatrixThrowsInputMismatchException() {
        String matrixToInput = "3x3\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        int[][] matrix = inputMatrix.readMatrix();
    }

    @Test(expected = NoSuchElementException.class)
    public void testInputMatrixWhenNotAllMatrixElementsAreSupplied() {
        String matrixToInput = "3 x 3\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        int[][] matrix = inputMatrix.readMatrix();
        assertMatrixSize(matrix,3,3);
        assert3x3MatrixRow0(matrix);
    }

    @Test
    public void testInput3x3Matrix() {
        String matrixToInput = "3 x 3\n"
                             + "0 1 2\n"
                             + "1 0 1\n"
                             + "0 0 0\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        int[][] matrix = inputMatrix.readMatrix();
        assertMatrixSize(matrix, 3, 3);
        assert3x3MatrixRow0(matrix);
        assert3x3MatrixRow1(matrix);
        assert3x3MatrixRow2(matrix);
    }

    private void assertMatrixSize(int[][] matrix, int matrixWidth, int matrixHeight) {
        assertThat(matrix.length, is(matrixHeight));
        assertThat(matrix[0].length, is(matrixWidth));
    }

    private void assert3x3MatrixRow0(int[][] matrix) {
        assertThat(matrix[0][0], is(0));
        assertThat(matrix[0][1], is(1));
        assertThat(matrix[0][2], is(2));
    }

    private void assert3x3MatrixRow1(int[][] matrix) {
        assertThat(matrix[1][0], is(1));
        assertThat(matrix[1][1], is(0));
        assertThat(matrix[1][2], is(1));
    }

    private void assert3x3MatrixRow2(int[][] matrix) {
        assertThat(matrix[2][0], is(0));
        assertThat(matrix[2][1], is(0));
        assertThat(matrix[2][2], is(0));
    }

    @Test
    public void testInput4x4Matrix() {
        String matrixToInput = "4 x 4\n"
                + "0 2 1 2\n"
                + "0 1 2 1\n"
                + "1 0 2 2\n"
                + "2 2 0 0\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        int[][] matrix = inputMatrix.readMatrix();
        assertMatrixSize(matrix, 4, 4);
        assert4x4MatrixRow0(matrix);
        assert4x4MatrixRow1(matrix);
        assert4x4MatrixRow2(matrix);
        assert4x4MatrixRow3(matrix);
    }

    private void assert4x4MatrixRow0(int[][] matrix) {
        assertThat(matrix[0][0], is(0));
        assertThat(matrix[0][1], is(2));
        assertThat(matrix[0][2], is(1));
        assertThat(matrix[0][3], is(2));
    }

    private void assert4x4MatrixRow1(int[][] matrix) {
        assertThat(matrix[1][0], is(0));
        assertThat(matrix[1][1], is(1));
        assertThat(matrix[1][2], is(2));
        assertThat(matrix[1][3], is(1));
    }

    private void assert4x4MatrixRow2(int[][] matrix) {
        assertThat(matrix[2][0], is(1));
        assertThat(matrix[2][1], is(0));
        assertThat(matrix[2][2], is(2));
        assertThat(matrix[2][3], is(2));
    }

    private void assert4x4MatrixRow3(int[][] matrix) {
        assertThat(matrix[3][0], is(2));
        assertThat(matrix[3][1], is(2));
        assertThat(matrix[3][2], is(0));
        assertThat(matrix[3][3], is(0));
    }
}
