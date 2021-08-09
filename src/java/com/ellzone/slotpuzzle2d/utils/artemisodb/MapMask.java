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

package com.ellzone.slotpuzzle2d.utils.artemisodb;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.utils.tilemap.TileMapAttributes;

public class MapMask {
    public final boolean[][] v;
    public final int height;
    public final int width;
    private final int tileWidth;
    private final int tileHeight;
    private final Array<MapLayer> layers;
    private final String propertyKey;

    public MapMask(int height, int width, int tileWidth, int tileHeight) {
        this(height, width, tileWidth, tileHeight,null,null);
    }

    public MapMask(TileMapAttributes tileMapAttributes, String propertyKey) {
        this(
                tileMapAttributes.getHeight(),
                tileMapAttributes.getWidth(),
                tileMapAttributes.getTileWidth(),
                tileMapAttributes.getTileHeight(),
                tileMapAttributes.getLayers(),
                propertyKey);
    }

    public MapMask(int height,
                   int width,
                   int tileWidth,
                   int tileHeight,
                   Array<MapLayer> layers,
                   String propertyKey) {
        this.height = height;
        this.width = width;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        v = new boolean[height][width];
        this.layers = layers;
        this.propertyKey = propertyKey;
        refresh();
    }

    public void refresh() {
        if (layers != null)
            generate(this.layers, this.propertyKey);
    }

    public void set(final int x, final int y, boolean value) {
        if (x >= width || x < 0 || y < 0 || y >= height) return;
        v[y][x] = value;
    }

    /**
     * @param x grid coordinates
     * @param y grid coordinates.
     * @return TRUE when property found at TILE coordinates, FALSE if otherwise or out of bounds.
     */
    public boolean atGrid(final int x, final int y, boolean outOfBoundsResult) {
        if (x >= width || x < 0 || y < 0 || y >= height) return outOfBoundsResult;
        return v[y][x];
    }

    /**
     * @param x
     * @param y
     * @return TRUE when property found at PIXEL coordinates.
     */
    public boolean atScreen(final int x, final int y, boolean outOfBoundsResult) {
        return atGrid((int) (x / tileWidth), (int) (y / tileHeight), outOfBoundsResult);
    }

    public boolean atScreen(final float x, final float y, boolean outOfBoundsResult) {
        return atGrid((int) ((int) x / tileWidth), (int) ((int) y / tileHeight), outOfBoundsResult);
    }

    public void generate(Array<MapLayer> layers, String propertyKey) {
        clear();
        for (MapLayer layer : layers) {
            if (layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer mapTileLayer = (TiledMapTileLayer) layer;
                for (int ty = 0; ty < height; ty++) {
                    for (int tx = 0; tx < width; tx++) {
                        final TiledMapTileLayer.Cell cell = mapTileLayer.getCell(tx, ty);
                        if (cell != null &&
                                cell.getTile() != null &&
                                cell.getTile().getProperties().containsKey(propertyKey)) {
                            v[ty][tx] = true;
                        }
                    }
                }
            }
        }
    }

    public void clear() {
        for (int ty = 0; ty < height; ty++) {
            for (int tx = 0; tx < width; tx++) {
                v[ty][tx] = false;
            }
        }
    }
}