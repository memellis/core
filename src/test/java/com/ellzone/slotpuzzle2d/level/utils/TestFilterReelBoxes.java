package com.ellzone.slotpuzzle2d.level.utils;

import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.level.creator.utils.FilterReelBoxes;
import org.junit.jupiter.api.Test;

public class TestFilterReelBoxes {

    @Test
    public void testFilterReelBoxes_emptyBoxes() {
        Array<Integer> deletedReelBoxes = new Array<>();
        float difficultLevel = 0;
        Array<Integer> filteredReelBoxes = FilterReelBoxes.
                filterReelBoxesByDifficultyLevel(deletedReelBoxes, difficultLevel);
        assert(filteredReelBoxes.size == 0);
    }

    @Test
    public void testFilterReelBoxes_oneBox() {
        Array<Integer> deletedReelBoxes = new Array<>();
        deletedReelBoxes.add(1);
        float difficultLevel = 0;
        Array<Integer> filteredReelBoxes = FilterReelBoxes.
                filterReelBoxesByDifficultyLevel(deletedReelBoxes, difficultLevel);
        assert(filteredReelBoxes.size == 1);
        assert(filteredReelBoxes.get(0) == 1);
    }

    @Test
    public void testFilterBoxes_by10Percent() {
        int numberOfDeletedBoxes = 100;
        Array<Integer> deletedReelBoxes = new Array<>();
        for (int i = 0; i < numberOfDeletedBoxes; i++)
            deletedReelBoxes.add(i);
        float difficultLevel = 0.1f;
        for (int testLoop = 0; testLoop < numberOfDeletedBoxes * 10; testLoop++) {
            Array<Integer> filteredReelBoxes = FilterReelBoxes.
                    filterReelBoxesByDifficultyLevel(deletedReelBoxes, difficultLevel);
            assert (filteredReelBoxes.size == numberOfDeletedBoxes / 10);
            assert (filteredReelBoxes.get(0) >= 0);
            assert (filteredReelBoxes.get(0) < numberOfDeletedBoxes);
        }
    }

}
