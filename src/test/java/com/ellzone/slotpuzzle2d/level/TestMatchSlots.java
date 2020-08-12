package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.gdx.MyGDXApplication;
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
        animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        int [][] matrix = SlotPuzzleMatrices.createMatrixWithNoBoxes();
        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                matrix
        );
        reelTiles = animatedReelsMatrixCreator.getReelTilesFromAnimatedReels(animatedReels);
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
        Gdx.app = new MyGDXApplication();
        animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        int [][] matrix = SlotPuzzleMatrices.createMatrixWithTwoBoxes();
        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                matrix
        );
        reelTiles = animatedReelsMatrixCreator.getReelTilesFromAnimatedReels(animatedReels);
        prepareTestWithReelAction(reelSetNotSpinningAction, reelTiles, 84, 96);
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

    private void prepareTestWithReelAction(
            ReelAction action,
            Array<ReelTile> reelTiles,
            int... reelsToAction) {
        for (int reelToAction = 0; reelToAction < reelsToAction.length; reelToAction++)
            action.action(reelTiles, reelsToAction[reelToAction]);
    }

    public interface ReelAction {
        public void action(Array<ReelTile> reel, int reelToAction);
    }

    private ReelAction reelSetNotSpinningAction = new ReelAction() {
        @Override
        public void action(Array<ReelTile> reels, int reelToAction) {
            reels.get(reelToAction).setSpinning(false);
        }
    };


}
