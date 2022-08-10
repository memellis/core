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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.gdx.MyGDXApplication;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.AnimatedReelsMatrixCreator;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.SlotPuzzleMatrices;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;

import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class TestPuzzleGridWithSpinningReels {

    @Test
    public void testGridWithSpinningReel() {
        Gdx.app = new MyGDXApplication();
        AnimatedReelsMatrixCreator animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        int [][] matrix = SlotPuzzleMatrices.createMatrixWithTwoBoxes();
        Array<AnimatedReel> animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                matrix
        );
        Array<ReelTile> reelTiles = animatedReelsMatrixCreator.getReelTilesFromAnimatedReels(animatedReels);
        prepareTestWithReelAction(reelSetSpinningAction, reelTiles, 84, 96);
        ReelTileGridValue[][] grid = PuzzleGridTypeReelTile.populateMatchGridStatic(
                reelTiles,
            new GridSize(matrix[0].length, matrix.length)
        );
        assertThat(grid[7][0].value, is(equalTo(-2)));
        assertThat(grid[8][0].value, is(equalTo(-2)));
        PuzzleGridTypeReelTile.printMatchGrid(
                reelTiles,
                new GridSize(matrix[0].length, matrix.length));
    }

    private void prepareTestWithReelAction(
            ReelAction action,
            Array<ReelTile> reelTiles,
            int... reelsToAction) {
        for (int reelToAction = 0; reelToAction < reelsToAction.length; reelToAction++)
            action.action(reelTiles, reelToAction);
    }

    public interface ReelAction {
        void action(Array<ReelTile> reel, int reelToAction);
    }

    private final ReelAction reelSetSpinningAction = new ReelAction() {
        @Override
        public void action(Array<ReelTile> reels, int reelToAction) {
            reels.get(reelToAction).setSpinning(true);
        }
    };
}
