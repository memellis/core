package com.ellzone.slotpuzzle2d.level.bombreel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.gdx.MyGDXApplication;
import com.ellzone.slotpuzzle2d.level.MatchSlots;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.AnimatedReelsMatrixCreator;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.SlotPuzzleMatrices;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.puzzlegrid.Tuple;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleType;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

import org.junit.Test;

import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_HEIGHT;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_WIDTH;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestBombReel {

    private AnimatedReelsMatrixCreator animatedReelsMatrixCreator;

    @Test
    public void testWithOneBombReel() throws Exception {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = getAnimatedReels(SlotPuzzleMatrices.createMatrixFWithOneBomb());

        Array<ReelTileGridValue> matchedSlots = getMatchedSlots(animatedReels);
        assertThat(matchedSlots.size, is(equalTo(0)));
    }

    @Test
    public void testWithTwoBombReels() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = getAnimatedReels(SlotPuzzleMatrices.createMatrixFWithTwoBombs());

        Array<ReelTileGridValue> matchedSlots = getMatchedSlots(animatedReels);
        Array<TupleValueIndex> createdMatrixEntries = getMatrixEntriesNotDeleted(
                SlotPuzzleMatrices.createMatrixFWithTwoBombs());

        assertThat(matchedSlots.size, is(equalTo(createdMatrixEntries.size)));
        int index = 0;
        for (ReelTileGridValue matchedSlot : matchedSlots) {
            assertThat(matchedSlot.r, is(equalTo(createdMatrixEntries.get(index).r)));
            assertThat(matchedSlot.c, is(equalTo(createdMatrixEntries.get(index).c)));
            assertThat(matchedSlot.reelTile.getEndReel(), is(equalTo(createdMatrixEntries.get(index).value)));
            index++;
        }
    }

    private Array<ReelTileGridValue> getMatchedSlots(Array<AnimatedReel> animatedReels) {
        MatchSlots matchSlots = new MatchSlots(
                PuzzleGridTypeReelTile.getReelTilesFromAnimatedReels(animatedReels),
                GAME_LEVEL_WIDTH,
                GAME_LEVEL_HEIGHT)
                .invoke();
        PuzzleGridTypeReelTile puzzleGridTypeReelTile = matchSlots.getPuzzleGridTypeReelTile();
        ReelTileGridValue[][] puzzleGrid = matchSlots.getPuzzleGrid();
        return matchSlots.getMatchedSlots();
    }

    private Array<AnimatedReel> getAnimatedReels(int[][] matrixFWithOneBomb) {
        Array<AnimatedReel> animatedReels = new Array<>();
        animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                matrixFWithOneBomb, false);
        PuzzleGridTypeReelTile.printSlotMatrix(animatedReels);
        return animatedReels;
    }
    
    private Array<TupleValueIndex> getMatrixEntriesNotDeleted(int[][] matrix) {
        Array<TupleValueIndex> nonDeletedEntries = new Array<>();
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                if (matrix[r][c] >= 0)
                    nonDeletedEntries.add(
                        new TupleValueIndex(r, c, r * matrix.length + c, matrix[r][c])
                    );
            }
        }
        return nonDeletedEntries;
    }
}
