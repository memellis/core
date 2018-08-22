package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

public class HiddenShape extends HiddenPattern{
    public HiddenShape(TiledMap level) {
        super(level);
    }

    @Override
    public boolean isHiddenPatternRevealed(TupleValueIndex[][] grid, Array<ReelTile> reelTiles, int levelWidth, int levelHeight) {
        for (MapObject mapObject : level.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            int c = PuzzleGridTypeReelTile.getColumnFromLevel(mapRectangle.getX());
            int r = PuzzleGridTypeReelTile.getRowFromLevel(mapRectangle.getY(), levelHeight);
            if ((r >= 0) & (r <= levelHeight) & (c >= 0) & (c <= levelWidth)) {
                if (grid[r][c] != null) {
                    if (!reelTiles.get(grid[r][c].getIndex()).isReelTileDeleted())
                        return false;
                } else
                    throw new HiddenPattern.HiddenPatternPuzzleGridException(String.format("Grid cell r=%d, c=%d is null", r, c));
            } else
                throw new HiddenPattern.HiddenPatternPuzzleGridException(String.format("Grid cell r=%d, c=%d has exceeded the grid limits: width=%d, height=%d", r, c, levelWidth, levelHeight));
        }
        return true;
    }
}
