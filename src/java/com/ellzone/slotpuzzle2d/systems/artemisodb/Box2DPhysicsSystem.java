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

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.ellzone.slotpuzzle2d.component.artemis.Boxed;

public class Box2DPhysicsSystem extends FluidSystem {
    private World box2dWorld;

    public Box2DPhysicsSystem(World box2dWorld) {
        super(Aspect.all(Boxed.class));
        this.box2dWorld = box2dWorld;
    }

    @Override
    protected void initialize() {
        super.initialize();
    }


    @Override
    protected void process(E e) {
        Body body = e.boxedBody();
    }

    @Override
    protected void begin() {
        super.begin();
        box2dWorld.step(1 / 60f, 8, 2);
    }

    @Override
    protected void end() {
        super.end();
    }
}
