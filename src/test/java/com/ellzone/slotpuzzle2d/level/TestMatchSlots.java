package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.gdx.MyGDXApplication;
import com.ellzone.slotpuzzle2d.level.fixtures.ReelPrepare;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.AnimatedReelsMatrixCreator;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.SlotPuzzleMatrices;
import com.ellzone.slotpuzzle2d.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;


public class TestMatchSlots {

    private Array<ReelTile> reelTiles;

    @Test
    public void testMatchSlotsWithZeroMatches() {
        Gdx.app = new MyGDXApplication();
        int[][] matrix = getMatrix(SlotPuzzleMatrices.createMatrixWithNoBoxes());
        MatchSlots matchSlots = new MatchSlots(
                reelTiles,
                new GridSize(matrix[0].length, matrix.length)
        ).invoke();
        Array<ReelTileGridValue> matchedSlots = matchSlots.getMatchedSlots();
        MatcherAssert.assertThat(matchedSlots.size, is(equalTo(0)));
    }

    @Test
    public void testMatchSlotsWithTwoMatches() {
        int[][] matrix = prepareMatchSlotsTestWithTwoMatches();
        MatchSlots matchSlots = new MatchSlots(
                reelTiles,
                new GridSize(matrix[0].length, matrix.length)
        ).invoke();
        Array<ReelTileGridValue> matchedSlots = matchSlots.getMatchedSlots();
        MatcherAssert.assertThat(matchedSlots.get(0).reelTile.getIndex(), is(equalTo(84)));
        MatcherAssert.assertThat(matchedSlots.get(1).reelTile.getIndex(), is(equalTo(96)));
    }

    @Test
    public void testMatchSlotsWithTwoReelsWithOneReelSpinning() {
        Gdx.app = new MyGDXApplication();
        ReelPrepare reelPrepare = new ReelPrepare();
        int[][] matrix = getMatrix(SlotPuzzleMatrices.createMatrixWithTwoBoxes());
        reelPrepare.prepareTestWithReelAction(
                reelPrepare.reelSetNotSpinningAction, reelTiles, 84);
        MatchSlots matchSlots = new MatchSlots(
                reelTiles,
                new GridSize(matrix[0].length, matrix.length)
        ).invoke();
        Array<ReelTileGridValue> matchedSlots = matchSlots.getMatchedSlots();
        MatcherAssert.assertThat(matchedSlots.size, is(equalTo(0)));
    }

    @Test
    public void testMatchSlotWithThreeReelsWithOneReelSpinning() {
        Gdx.app = new MyGDXApplication();
        ReelPrepare reelPrepare = new ReelPrepare();
        int[][] matrix = getMatrix(SlotPuzzleMatrices.createMatrixWithThreeBoxes());
        reelPrepare.prepareTestWithReelAction(
                reelPrepare.reelSetNotSpinningAction, reelTiles, 84, 96);
        MatchSlots matchSlots = new MatchSlots(
                reelTiles,
                new GridSize(matrix[0].length, matrix.length)
        ).invoke();
        Array<ReelTileGridValue> matchedSlots = matchSlots.getMatchedSlots();
        MatcherAssert.assertThat(matchedSlots.get(0).reelTile.getIndex(), is(equalTo(84)));
        MatcherAssert.assertThat(matchedSlots.get(1).reelTile.getIndex(), is(equalTo(96)));
    }

    private int[][] prepareMatchSlotsTestWithTwoMatches() {
        Gdx.app = new MyGDXApplication();
        ReelPrepare reelPrepare = new ReelPrepare();
        int[][] matrix = getMatrix(SlotPuzzleMatrices.createMatrixWithTwoBoxes());
        reelPrepare.prepareTestWithReelAction(
                reelPrepare.reelSetNotSpinningAction, reelTiles, 84, 96);
        return matrix;
    }

    private int[][] getMatrix(int[][] matrixWithTwoBoxes) {
        AnimatedReelsMatrixCreator animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        Array<AnimatedReel> animatedReels =
                animatedReelsMatrixCreator.
                        createAnimatedReelsFromSlotPuzzleMatrix(matrixWithTwoBoxes);
        reelTiles = animatedReelsMatrixCreator.getReelTilesFromAnimatedReels(animatedReels);
        return matrixWithTwoBoxes;
    }
}
