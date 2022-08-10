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

package com.ellzone.slotpuzzle2d.puzzlegrid;

import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.utils.Random;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestRemoveDuplicateMatches {

    public static final int MAX_ROWS = 9;
    public static final int MAX_COLUMNS = 11;
    public static final int MAX_VALUES = 8;
    public static final int MAX_REELS = 100;

    @Test
    public void testRemoveDuplicateMatches() {
        Array<ReelTileGridValue> reels = setUpReels();
        Array<ReelTileGridValue> duplicateMatches = setUpCreateDuplicateMatches(reels);
        Array<ReelTileGridValue> matchedSlots = setUpMatchSlots(duplicateMatches);
        Array<ReelTileGridValue> processedMatchedSlots = PuzzleGridTypeReelTile.removeDuplicateMatches(duplicateMatches, matchedSlots);
        assertRemoveDuplicateMatches(reels, matchedSlots, processedMatchedSlots, duplicateMatches);
    }

    private Array<ReelTileGridValue> setUpCreateDuplicateMatches(Array<ReelTileGridValue> reels) {
        Array<ReelTileGridValue> duplicateMatches = new Array<>();
        for (int index = 0; index < reels.size; index++)
            duplicateMatches = addDuplicateEntries(duplicateMatches, reels.get(index));
        return duplicateMatches;
    }

    private Array<ReelTileGridValue> addDuplicateEntries(Array<ReelTileGridValue> duplicateMatches, ReelTileGridValue reel) {
        duplicateMatches.add(new ReelTileGridValue(reel.r, reel.c, reel.index * 2, reel.value));
        duplicateMatches.add(new ReelTileGridValue(reel.r, reel.c, reel.index * 2 + 1, reel.value));
        return duplicateMatches;
    }

    private Array<ReelTileGridValue> setUpReels() {
        Array<ReelTileGridValue> reels = new Array<>();
        int duplicateMatchSize = Random.getInstance().nextInt(MAX_REELS);
        for (int index = 0; index < duplicateMatchSize; index++) {
            addEntry(reels, index);
        }
        return reels;
    }

    private void addEntry(Array<ReelTileGridValue> reels, int loop) {
        int row = Random.getInstance().nextInt(MAX_ROWS);
        int column = Random.getInstance().nextInt(MAX_COLUMNS);
        int value = Random.getInstance().nextInt(MAX_VALUES);

        if (!isEntryExists(reels, row, column))
            reels.add(new ReelTileGridValue(row, column, loop, value));
    }

    private boolean isEntryExists(Array<ReelTileGridValue> reels, int row, int column) {
        boolean isEntryExists = false;
        for (ReelTileGridValue reel : reels) {
            if ((reel.r == row) & ( reel.c == column)) {
                isEntryExists = true;
                break;
            }
        }
        return isEntryExists;
    }

    private Array<ReelTileGridValue> setUpMatchSlots(Array<ReelTileGridValue> duplicateMatches) {
        Array<ReelTileGridValue> matchSlots = new Array<>();
        for (int index = 0; index < duplicateMatches.size  ; index++)
            matchSlots.add(new ReelTileGridValue(duplicateMatches.get(index).r,
                                                 duplicateMatches.get(index).c,
                                                 duplicateMatches.get(index).index,
                                                 duplicateMatches.get(index).value));
        return matchSlots;
    }

    private void assertRemoveDuplicateMatches(Array<ReelTileGridValue> reels, Array<ReelTileGridValue> matchedSlots, Array<ReelTileGridValue> processMatchedSlots, Array<ReelTileGridValue> duplicateMatches) {
        assertThat(matchedSlots.size, is(equalTo(processMatchedSlots.size)));
        assertThat(duplicateMatches.size / 2, is(equalTo(processMatchedSlots.size)));
        assertReelEntriesEqual(reels, processMatchedSlots);
    }

    private void assertReelEntriesEqual(Array<ReelTileGridValue> first, Array<ReelTileGridValue> second) {
        for (int index = 0; index < first.size; index++) {
            assertThat(first.get(index).r, is(equalTo(second.get(index).r)));
            assertThat(first.get(index).c, is(equalTo(second.get(index).c)));
        }
    }
}
