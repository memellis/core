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

package com.ellzone.slotpuzzle2d.utils;

public class WorldScreenConvert {
    public static final int MAP_WIDTH = 4000;
    public static final int MAP_HEIGHT = 16000;
    public static final int WORLD_WIDTH = 10000;
    public static final int WORLD_HEIGHT = 40000;

    public static float convertTileMapXToWorldPostionX(float x) {
        return x / MAP_WIDTH * WORLD_WIDTH;
    }

    public static float convertTileMapYToWorldPostionY(float y) {
        return y / MAP_HEIGHT * WORLD_HEIGHT;
    }
}
