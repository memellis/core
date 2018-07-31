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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestInputMatrix {
    @Test(expected = InputMismatchException.class)
    public void testInputMatrixThrowsInputMismatchException() {
        String matrixToInput = "3x3\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        int[][] matrix = inputMatrix.readMatrix();
    }

    @Test
    public void testInputMatrix() {
        String matrixToInput1 = "3 x 3\n"
                              + "0 1 2\n"
                              + "1 0 1\n"
                              + "0 0 0\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput1);
        int[][] matrix = inputMatrix.readMatrix();
        assertMatrixSize(matrix);
        assertMatrixRow0(matrix);
        assertMatrixRow1(matrix);
        assertMatrixRow2(matrix);
    }

    private void assertMatrixRow0(int[][] matrix) {
        assertThat(matrix[0][0], is(0));
        assertThat(matrix[0][1], is(1));
        assertThat(matrix[0][2], is(2));
    }

    private void assertMatrixRow1(int[][] matrix) {
        assertThat(matrix[1][0], is(1));
        assertThat(matrix[1][1], is(0));
        assertThat(matrix[1][2], is(1));
    }

    private void assertMatrixRow2(int[][] matrix) {
        assertThat(matrix[2][0], is(0));
        assertThat(matrix[2][1], is(0));
        assertThat(matrix[2][2], is(0));
    }

    private void assertMatrixSize(int[][] matrix) {
        assertThat(matrix.length, is(3));
        assertThat(matrix[0].length, is(3));
    }
}
