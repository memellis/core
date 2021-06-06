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
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.utils.artemisodb.MapMask;

public class TiledMapSystem extends BaseSystem {

    private final String mapFilename;

    public TiledMap map;

    private int width;
    private int height;
    private int tileWidth;
    private int tileHeight;

    private boolean isSetup;

    public Array<MapLayer> layers;

    public TiledMapSystem(String mapFilename) {
        this.mapFilename = mapFilename;
    }

    @Override
    protected void initialize() {
        map = new TmxMapLoader().load(mapFilename);
        layers = map.getLayers().getByType(MapLayer.class);
        width = map.getProperties().get("width", Integer.class);
        height = map.getProperties().get("height", Integer.class);
        tileWidth = map.getProperties().get("tilewidth", Integer.class);
        tileHeight = map.getProperties().get("tileheight", Integer.class);
    }

    public MapMask getMask(String property) {
        return new MapMask(height, width, tileWidth, tileHeight, layers, property);
    }

    protected void setup() {
        for (MapLayer layer : new Array.ArrayIterator<>(layers)) {
            if (layer instanceof TiledMapTileLayer) {
                final TiledMapTileLayer mapTileLayer = (TiledMapTileLayer) layer;
                for (int ty = 0; ty < height; ty++) {
                    for (int tx = 0; tx < width; tx++) {

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
