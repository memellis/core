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

package com.ellzone.slotpuzzle2d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class TransformComponent implements Component {
    public final Vector3 position = new Vector3();
    public final Vector2 origin = new Vector2();
    public final Vector2 scale = new Vector2();
    public float rotation = 0.0f;
    public boolean isHidden = false;

    public TransformComponent() {}

    public TransformComponent(Vector3 position,
                              Vector2 origin,
                              Vector2 scale,
                              float rotation,
                              boolean isHidden) {
        this.position.set(position);
        this.origin.set(origin);
        this.scale.set(scale);
        this.rotation = rotation;
        this.isHidden = isHidden;
    }

    public TransformComponent(float positionX,
                              float positionY,
                              float positionZ,
                              float originX,
                              float originY,
                              float scaleX,
                              float scaleY,
                              float rotation,
                              int isHidden) {
        this(new Vector3(positionX, positionY, positionZ),
             new Vector2(originX, originY),
             new Vector2(scaleX, scaleY),
             rotation,
             isHidden == 0  ? false : true);
    }
}
