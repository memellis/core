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

package com.ellzone.slotpuzzle2d.prototypes.box2d.collisions;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestSlotPuzzleMatricesExtractBit {

    @Test
    public void testZero() {
        assertThat(SlotPuzzleMatrices.extractBitAt(0,0), is(0));
    }

    @Test
    public void testOne() {
        assertThat(SlotPuzzleMatrices.extractBitAt(1,0), is(1));
    }

    @Test
    public void testTwo() {
        assertThat(SlotPuzzleMatrices.extractBitAt(2,0), is(0));
        assertThat(SlotPuzzleMatrices.extractBitAt(2, 1), is(1));
    }

    @Test
    public void testThree() {
        assertThat(SlotPuzzleMatrices.extractBitAt(3,0), is(1));
        assertThat(SlotPuzzleMatrices.extractBitAt(3, 1), is(1));
    }

    @Test
    public void testFour() {
        assertThat(SlotPuzzleMatrices.extractBitAt(4,0), is(0));
        assertThat(SlotPuzzleMatrices.extractBitAt(4, 1), is(0));
        assertThat(SlotPuzzleMatrices.extractBitAt(4,2), is(1));
    }

    @Test
    public void testAssertNumberUpTo9bits() {
        for (int number=0; number<Math.pow(2, 9); number++)
            assertExtractNumber(number);
    }

    private void assertExtractNumber(int number) {
        int n = number;
        int i = 1;
        while (n > 0) {
            assertThat(
                SlotPuzzleMatrices.extractBitAt(number, i-1),
                is(n % 2 == 0 ? 0 : 1));
            n = n / 2;
            i++;
        }
    }
}
