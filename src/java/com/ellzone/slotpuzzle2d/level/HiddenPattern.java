package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

public abstract class HiddenPattern {
    protected TiledMap level;
    public HiddenPattern(TiledMap level) {
        this.level = level;
    }

    public abstract boolean isHiddenPatternRevealed(TupleValueIndex[][] grid, Array<ReelTile> reelTiles, int levelWidth, int levelHeight);

    public static class HiddenPatternPuzzleGridException extends GdxRuntimeException {
        public HiddenPatternPuzzleGridException(String message) {
            super(message);
        }
    }

}
