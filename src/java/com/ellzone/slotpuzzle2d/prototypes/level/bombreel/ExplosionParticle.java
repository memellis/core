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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class ExplosionParticle {
    public int blastPower = 100;
    public static final int NUMRAYS = 60;
    public Body body;

    public ExplosionParticle(World world, Vector2 vector, Vector2 rayDir) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.fixedRotation = true; // rotation not necessary
        bd.bullet = true; // prevent tunneling at high speed
        bd.linearDamping = 10; // drag due to moving through air
        bd.gravityScale = 0; // ignore gravity
        bd.position.x = vector.x;
        bd.position.y = vector.y;// start at blast center
        rayDir.scl(blastPower);
        bd.linearVelocity.x = rayDir.x;
        bd.linearVelocity.y = rayDir.y;
        body = world.createBody(bd);
        //create a reference to this class in the body(this allows us to loop through the world bodies and check if the body is an Explosion particle)
        body.setUserData(this);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.05f); // very small

        FixtureDef fd = new FixtureDef();
        fd.shape = circleShape;
        fd.density = 120 / (float) NUMRAYS; // very high - shared across all particles
        fd.friction = 0; // friction not necessary
        fd.restitution = 0.99f; // high restitution to reflect off obstacles
        fd.filter.groupIndex = -1; // particles should not collide with each other
        body.createFixture(fd);
    }
}