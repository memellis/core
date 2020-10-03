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

package com.ellzone.slotpuzzle2d.effects;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import aurelienribon.tweenengine.TweenAccessor;

public class CameraAccessor implements TweenAccessor<Camera> {
        public static final int POS_XY = 1;
        public static final int CPOS_XY = 2;
        public static final int SCALE_XY = 3;
        public static final int ROTATION = 4;
        public static final int OPACITY = 5;
        public static final int TINT = 6;
        public static final int SCROLL_XY = 7;

        @Override
        public int getValues(Camera target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POS_XY:
                returnValues[0] = target.position.x;
                returnValues[1] = target.position.y;
                return 2;

            case CPOS_XY:
                returnValues[0] = target.position.x + target.viewportWidth / 2;
                returnValues[1] = target.position.y + target.viewportHeight / 2;
                return 2;

            case SCALE_XY: return -1;
            case ROTATION: return -1;
            case OPACITY: return -1;
            case TINT: return -1;
            default: assert false; return -1;
        }
    }

        @Override
        public void setValues(Camera target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POS_XY: target.position.set(newValues[0], newValues[1], 0);
                break;
            case CPOS_XY:
                target.position.set(
                        newValues[0] - target.viewportWidth / 2,
                        newValues[1] - target.viewportHeight / 2,
                        0);
                break;
            case SCALE_XY: break;
            case ROTATION: break;
            case OPACITY: break;
            case TINT: break;
            case SCROLL_XY: break;

            default: assert false;
        }
    }
}
