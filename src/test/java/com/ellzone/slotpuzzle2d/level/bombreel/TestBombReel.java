package com.ellzone.slotpuzzle2d.level.bombreel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.gdx.MyGDXApplication;
import com.ellzone.slotpuzzle2d.level.MatchSlots;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.AnimatedReelsMatrixCreator;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.SlotPuzzleMatrices;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;

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
        Array<AnimatedReel> animatedReels = new Array<>();
        animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                SlotPuzzleMatrices.createMatrixFWithOneBomb(), false);
        PuzzleGridTypeReelTile.printSlotMatrix(animatedReels);

        MatchSlots matchSlots = new MatchSlots(
                PuzzleGridTypeReelTile.getReelTilesFromAnimatedReels(animatedReels),
                GAME_LEVEL_WIDTH,
                GAME_LEVEL_HEIGHT)
                .invoke();
        PuzzleGridTypeReelTile puzzleGridTypeReelTile = matchSlots.getPuzzleGridTypeReelTile();
        ReelTileGridValue[][] puzzleGrid = matchSlots.getPuzzleGrid();
        Array<ReelTileGridValue> matchedSlots = matchSlots.getMatchedSlots();
        assertThat(matchedSlots.size, is(equalTo(0)));
    }

    @Test
    public void testWithTwoBombReels() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                SlotPuzzleMatrices.createMatrixFWithTwoBombs(), false);
        PuzzleGridTypeReelTile.printSlotMatrix(animatedReels);

        MatchSlots matchSlots = new MatchSlots(
                PuzzleGridTypeReelTile.getReelTilesFromAnimatedReels(animatedReels),
                GAME_LEVEL_WIDTH,
                GAME_LEVEL_HEIGHT)
                .invoke();
        PuzzleGridTypeReelTile puzzleGridTypeReelTile = matchSlots.getPuzzleGridTypeReelTile();
        ReelTileGridValue[][] puzzleGrid = matchSlots.getPuzzleGrid();
        Array<ReelTileGridValue> matchedSlots = matchSlots.getMatchedSlots();
        assertThat(matchedSlots.size, is(equalTo(2)));
    }
}
