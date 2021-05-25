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

package com.ellzone.slotpuzzle2d;

public final class SlotPuzzleConstants {
    public final static int VIRTUAL_WIDTH = 800;
    public final static int VIRTUAL_HEIGHT = 480;
    public final static int PIXELS_PER_METER = 100;
    public final static float EARTH_GRAVITY = -9.8f;
    public final static int TILE_WIDTH = 40;
    public final static int TILE_HEIGHT = 40;
    public static final int GAME_LEVEL_WIDTH = 12;
    public static final int GAME_LEVEL_HEIGHT = 9;

    public final static String SLOT_PUZZLE = "Slot Puzzle";
    public final static String DEBUG = "DEBUG";
    public final static String INFO = "INFO";
    public final static String ERROR = "ERROR";
    public final static String LIBGDX_LOGLEVEL_PROPERTY = "libgdx.logLevel";
    public final static String LIBGDX_LOGLEVEL = "LIBGDX_LOGLEVEL";

    public static final String BOMBS_LEVEL_TYPE = "BombsLevel";
    public static final String HIDDEN_PATTERN_LEVEL_TYPE = "HiddenPattern";
    public static final String HIDDEN_PATTERN_LAYER_NAME = "Hidden Pattern Object";
    public static final String PLAYING_CARD_LEVEL_TYPE = "PlayingCard";
    public static final String BONUS_LEVEL_TYPE = "BonusLevelType";
    public static final String FALLING_REELS_LEVEL_TYPE = "FallingReels";

    private SlotPuzzleConstants() {
    }
}
