package com.ellzone.slotpuzzle2d.testpuzzlegrid;

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

import static com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile.getSurroundingReelTiles;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_HEIGHT;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_WIDTH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TestGetSurroundingReelTiles {

    @Test
    public void testGetSurroundingReelTilesWithNullParameter() {
        Array<ReelTileGridValue> surroundingReelTiles =
                PuzzleGridTypeReelTile.getSurroundingReelTiles(null);
        assertThat(surroundingReelTiles, is(equalTo(null)));
    }

    @Test
    public void testGetSurroundingReelTilesWithEmptyParameter() {
        Array<ReelTileGridValue> surroundingReelTiles =
                PuzzleGridTypeReelTile.getSurroundingReelTiles(new Array<ReelTileGridValue>());
        assertThat(surroundingReelTiles.size, is(equalTo(0)));
    }

    @Test
    public void testGetSurroundingReelWithTwoReelTilesAndNoSurroundingReelTiles() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = getAnimatedReels(SlotPuzzleMatrices.createMatrixFWithTwoBombs());

        Array<ReelTileGridValue> matchedSlots = getMatchedSlots(animatedReels);
        Array<ReelTileGridValue> surroundingReelTiles =
                PuzzleGridTypeReelTile.getSurroundingReelTiles(matchedSlots);

        assertThat(surroundingReelTiles.size, is(equalTo(0)));
    }

    private Array<AnimatedReel> getAnimatedReels(int[][] matrixFWithOneBomb) {
        Array<AnimatedReel> animatedReels = new Array<>();
        AnimatedReelsMatrixCreator animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                matrixFWithOneBomb, false);
        PuzzleGridTypeReelTile.printSlotMatrix(animatedReels);
        return animatedReels;
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
}
