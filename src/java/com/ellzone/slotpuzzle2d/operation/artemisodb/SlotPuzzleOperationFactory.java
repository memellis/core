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

package com.ellzone.slotpuzzle2d.operation.artemisodb;

import com.badlogic.gdx.math.Interpolation;
import net.mostlyoriginal.api.operation.common.Operation;

public class SlotPuzzleOperationFactory {
    private SlotPuzzleOperationFactory() {}

    public static TweenSpinScrollOperation spinScrollBetween(
            float sx1, float sy1, float sx2, float sy2, float duration) {
        return spinScrollBetween(sx1, sy1, sx2, sy2, duration, Interpolation.linear);
    }

    public static TweenSpinScrollOperation spinScrollBetween(
            float sx1, float sy1, float sx2, float sy2, float duration, Interpolation interpolation) {
        final TweenSpinScrollOperation operation = Operation.prepare(TweenSpinScrollOperation.class);
        operation.setup(interpolation, duration);
        operation.getFrom().set(sx1, sy1);
        operation.getTo().set(sx2, sy2);
        return operation;
    }

    public static TweenRotationOperation rotateBetween(float a1, float a2, float duration) {
        return rotateBetween(a1, a2, duration, Interpolation.linear);
    }

    public static TweenRotationOperation rotateBetween(
            float a1, float a2, float duration, Interpolation interpolation) {
        final TweenRotationOperation operation = Operation.prepare(TweenRotationOperation.class);
        operation.setup(interpolation, duration);
        operation.getFrom().set(a1);
        operation.getTo().set(a2);
        return operation;
    }
}
