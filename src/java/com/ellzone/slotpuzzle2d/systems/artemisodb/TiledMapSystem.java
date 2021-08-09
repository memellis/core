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

package com.ellzone.slotpuzzle2d.systems.artemisodb;

import com.artemis.BaseSystem;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.scene.Tile;
import com.ellzone.slotpuzzle2d.utils.artemisodb.MapMask;
import com.ellzone.slotpuzzle2d.utils.convert.TileMapToWorldConvert;
import com.ellzone.slotpuzzle2d.utils.tilemap.TileMapAttributes;

public class TiledMapSystem extends BaseSystem {

    private String mapFilename;
    private TileMapAttributes tileMapAttributes;
    private boolean isSetup;

    public TiledMapSystem(String mapFilename) {
        this.mapFilename = mapFilename;
    }

    public TiledMapSystem(TileMapAttributes tileMapAttributes) {
        this.tileMapAttributes = tileMapAttributes;
    }

    @Override
    protected void initialize() {
        if (mapFilename != null)
            tileMapAttributes = new TileMapAttributes(mapFilename);
    }

    public MapMask getMask(String property) {
        return new MapMask(tileMapAttributes, property);
    }

    public TiledMap getTiledMap() { return tileMapAttributes.getTiledMap(); }
    public int getMapWidth() { return tileMapAttributes.getWidth(); }
    public int getMapHeight() { return tileMapAttributes.getHeight(); }
    public int getTileWidth() { return tileMapAttributes.getTileWidth(); }
    public int getTileHeight() { return tileMapAttributes.getTileHeight(); }

    protected void setup() {
        for (MapLayer layer : new Array.ArrayIterator<>(tileMapAttributes.getLayers())) {
            if (layer instanceof TiledMapTileLayer) {
                final TiledMapTileLayer mapTileLayer = (TiledMapTileLayer) layer;
                for (int ty = 0; ty < tileMapAttributes.getHeight(); ty++) {
                    for (int tx = 0; tx < tileMapAttributes.getWidth(); tx++) {

                        final TiledMapTileLayer.Cell cell = mapTileLayer.getCell(tx, ty);
                        if (cell != null) {
                            final MapProperties properties = cell.getTile().getProperties();
                            if (properties.containsKey("entity")) {
                                mapTileLayer.setCell(tx, ty, null);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void processSystem() {
        if (!isSetup) {
            isSetup = true;
            setup();
        }
    }
}
