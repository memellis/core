/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.prototypes.level.bombreel;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class FloorBody {

    public FloorBody(World world, float[] points) {
        BodyDef floorDef = new BodyDef();
        floorDef.type = BodyDef.BodyType.StaticBody;
        floorDef.position.set(0, 0f);

        Body floorBody = world.createBody(floorDef);

        ChainShape floorShape = new ChainShape();
        floorShape.createChain(points);

        FixtureDef floorFixtureDef = new FixtureDef();
        floorFixtureDef.shape = floorShape;
        floorFixtureDef.density = 4f;
        floorFixtureDef.friction = 0.3f;
        floorFixtureDef.restitution= 0.3f;

        floorBody.createFixture(floorFixtureDef);

        floorShape.dispose();
    }
}