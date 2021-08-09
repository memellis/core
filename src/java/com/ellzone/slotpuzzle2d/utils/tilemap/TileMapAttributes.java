/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ellzone.slotpuzzle2d.utils.tilemap;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;

public class TileMapAttributes {

    private TiledMap map;
    private Array<MapLayer> layers;
    private Integer width;
    private Integer height;
    private Integer tileWidth;
    private Integer tileHeight;

    public TileMapAttributes(String mapFilename) {
        initialise(mapFilename);
    }
    
    private void initialise(String mapFilename) {
        map = new TmxMapLoader().load(mapFilename);
        layers = map.getLayers().getByType(MapLayer.class);
        width = map.getProperties().get("width", Integer.class);
        height = map.getProperties().get("height", Integer.class);
        tileWidth = map.getProperties().get("tilewidth", Integer.class);
        tileHeight = map.getProperties().get("tileheight", Integer.class);
    }

    public TiledMap getTiledMap() {
        return map;
    }

    public Array<MapLayer> getLayers() {
        return layers;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getTileWidth() {
        return tileWidth;
    }

    public Integer getTileHeight() {
        return tileHeight;
    }
}
