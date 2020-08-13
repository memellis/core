package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.gdx.MyGDXApplication;
import com.ellzone.slotpuzzle2d.level.fixtures.ReelPrepare;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.AnimatedReelsMatrixCreator;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.SlotPuzzleMatrices;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class TestMatchSlots {

    private AnimatedReelsMatrixCreator animatedReelsMatrixCreator;
    private Array<AnimatedReel> animatedReels;
    private Array<ReelTile> reelTiles;

    @Test
    public void testMatchSlotsWithZeroMatches() {
        Gdx.app = new MyGDXApplication();
        int[][] matrix = getMatrix(SlotPuzzleMatrices.createMatrixWithNoBoxes());
        PuzzleGridTypeReelTile.printMatchGrid(reelTiles, matrix[0].length, matrix.length);
        MatchSlots matchSlots = new MatchSlots(
                reelTiles,
                matrix[0].length,
                matrix.length
        ).invoke();
        Array<ReelTileGridValue> matchedSlots = matchSlots.getMatchedSlots();
        assertThat(matchedSlots.size, is(equalTo(0)));
    }

    @Test
    public void testMatchSlotsWithTwoMatches() {
        int[][] matrix = prepareMatchSlotsTestWithTwoMatches();
        MatchSlots matchSlots = new MatchSlots(
                reelTiles,
                matrix[0].length,
                matrix.length
        ).invoke();
        Array<ReelTileGridValue> matchedSlots = matchSlots.getMatchedSlots();
        assertThat(matchedSlots.get(0).reelTile.getIndex(), is(equalTo(84)));
        assertThat(matchedSlots.get(1).reelTile.getIndex(), is(equalTo(96)));
    }

    @Test
    public void testMatchSlotsWithTwoReelsWithOneReelSpinning() {
        Gdx.app = new MyGDXApplication();
        ReelPrepare reelPrepare = new ReelPrepare();
        int[][] matrix = getMatrix(SlotPuzzleMatrices.createMatrixWithTwoBoxes());
        reelPrepare.prepareTestWithReelAction(
                reelPrepare.reelSetNotSpinningAction, reelTiles, 84);
        PuzzleGridTypeReelTile.printMatchGrid(reelTiles, matrix[0].length, matrix.length);
        MatchSlots matchSlots = new MatchSlots(
                reelTiles,
                matrix[0].length,
                matrix.length
        ).invoke();
        Array<ReelTileGridValue> matchedSlots = matchSlots.getMatchedSlots();
        assertThat(matchedSlots.size, is(equalTo(0)));
    }

    @Test
    public void testMacthSLotWithThreeReelsWithOneReelSpinning() {
        Gdx.app = new MyGDXApplication();
        ReelPrepare reelPrepare = new ReelPrepare();
        int[][] matrix = getMatrix(SlotPuzzleMatrices.createMatrixWithThreeBoxes());
        reelPrepare.prepareTestWithReelAction(
                reelPrepare.reelSetNotSpinningAction, reelTiles, 84, 96);
        PuzzleGridTypeReelTile.printMatchGrid(reelTiles, matrix[0].length, matrix.length);
        MatchSlots matchSlots = new MatchSlots(
                reelTiles,
                matrix[0].length,
                matrix.length
        ).invoke();
        Array<ReelTileGridValue> matchedSlots = matchSlots.getMatchedSlots();
        assertThat(matchedSlots.get(0).reelTile.getIndex(), is(equalTo(84)));
        assertThat(matchedSlots.get(1).reelTile.getIndex(), is(equalTo(96)));
    }

    private int[][] prepareMatchSlotsTestWithTwoMatches() {
        Gdx.app = new MyGDXApplication();
        ReelPrepare reelPrepare = new ReelPrepare();
        int[][] matrix = getMatrix(SlotPuzzleMatrices.createMatrixWithTwoBoxes());
        reelPrepare.prepareTestWithReelAction(
                reelPrepare.reelSetNotSpinningAction, reelTiles, 84, 96);
        PuzzleGridTypeReelTile.printMatchGrid(reelTiles, matrix[0].length, matrix.length);
        return matrix;
    }

    private int[][] getMatrix(int[][] matrixWithTwoBoxes) {
        animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        int[][] matrix = matrixWithTwoBoxes;
        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                matrix
        );
        reelTiles = animatedReelsMatrixCreator.getReelTilesFromAnimatedReels(animatedReels);
        return matrix;
    }
}
