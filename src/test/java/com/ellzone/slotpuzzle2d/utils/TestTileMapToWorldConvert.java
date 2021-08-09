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

package com.ellzone.slotpuzzle2d.utils;

import com.ellzone.slotpuzzle2d.physics.Point;
import com.ellzone.slotpuzzle2d.utils.convert.ConvertInterface;
import com.ellzone.slotpuzzle2d.utils.convert.TileMapToWorldConvert;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TestTileMapToWorldConvert {
    @Test
    public void testWithSimpleTileMap() {
        com.ellzone.slotpuzzle2d.utils.convert.TileMapToWorldConvert tileMapToWorldConvert =
                new TileMapToWorldConvert(
                        4000,
                        16000,
                        10000,
                        40000 );
        Point pointToConvert = new Point(1660.0f, 1230.0f);
        Point convertedPoint = tileMapToWorldConvert.convertToWorldPosition(pointToConvert);
        assertPoint(pointToConvert, convertedPoint, tileMapToWorldConvert);
    }

    private void assertPoint(
            Point pointToConvert,
            Point convertedPoint,
            TileMapToWorldConvert tileMapToWorldConvert) {
        assertThat(convertedPoint.getX(),
                is(equalTo(pointToConvert.getX() / tileMapToWorldConvert.getMapWidth() *
                        tileMapToWorldConvert.getWorldWidth())));
        assertThat(convertedPoint.getY(),
                is(equalTo(pointToConvert.getY() / tileMapToWorldConvert.getMapHeight() *
                        tileMapToWorldConvert.getWorldHeight())));
    }

    @Test
    public void testWithConvertObjectParameter() {
        com.ellzone.slotpuzzle2d.utils.convert.TileMapToWorldConvert tileMapToWorldConvert =
                new TileMapToWorldConvert(
                        20*40,
                        24*40,
                        1280,
                        720 );

        Point pointToConvert = new Point(355.92f, 878.92f);
        Point convertedPoint = convertPoint(tileMapToWorldConvert, pointToConvert);
        assertPoint(pointToConvert, convertedPoint, tileMapToWorldConvert);
    }

    private Point convertPoint(TileMapToWorldConvert tileMapToWorldConvert,
                               Point pointToConvert) {
        return
           tileMapToWorldConvert.convertToWorldPosition(pointToConvert);
    }
}
